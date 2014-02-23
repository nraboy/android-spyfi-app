package com.nraboy.spyfi;

/*
 * Spyfi
 * Created by Nic Raboy
 * www.nraboy.com
 */

import com.nraboy.spyfi.adapters.*;
import com.nraboy.spyfi.objects.*;
import com.nraboy.spyfi.datasources.*;
import android.app.*;
import android.os.*;
import android.view.*;
import android.content.*;
import java.util.*;
import android.widget.*;
import android.net.*;
import java.io.*;
import android.app.AlertDialog.*;
import android.widget.AdapterView.*;
import android.graphics.*;
import android.view.View.*;
import android.preference.*;
import android.preference.Preference.*;
import com.google.analytics.tracking.android.EasyTracker;

public class StreamActivity extends Activity {

    private Context context;
    private SharedPreferences settings;
    private Datasource datasource;
    private int cameraId;
    private Camera camera;
    private String cameraUri;
    private PowerManager powerManager;
    private PowerManager.WakeLock screenLock;
    private ImageView ivStream;
    private TextView btnPTZLeft;
    private TextView btnPTZRight;
    private TextView btnPTZUp;
    private TextView btnPTZDown;
    private Typeface fontFamily;
    private boolean isStreaming;
    private Bitmap snapshot;
    private Handler threadHandler;
    private RemoteImage remoteImage;
    private InputStream inputStream;
    private MotionController motionController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        this.context = this;
        this.settings = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.datasource = new Datasource(this.context);
        this.fontFamily = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome.ttf");
        this.ivStream = (ImageView) findViewById(R.id.stream);
        this.btnPTZLeft = (TextView)findViewById(R.id.ptz_left);
        this.btnPTZRight = (TextView)findViewById(R.id.ptz_right);
        this.btnPTZUp = (TextView)findViewById(R.id.ptz_up);
        this.btnPTZDown = (TextView)findViewById(R.id.ptz_down);

        btnPTZLeft.setTypeface(fontFamily);
        btnPTZRight.setTypeface(fontFamily);
        btnPTZUp.setTypeface(fontFamily);
        btnPTZDown.setTypeface(fontFamily);

        this.cameraId = getIntent().getExtras().getInt("camera_id");
        this.powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.screenLock = this.powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Stay Awake While Streaming");
        this.screenLock.acquire();
        this.camera = this.datasource.select(this.cameraId);
        this.cameraUri = camera.getHost() + ":" + camera.getPort();
        this.isStreaming = true;
        this.remoteImage = new RemoteImage();
        this.threadHandler = new Handler();

        this.btnPTZLeft.setOnTouchListener(buttonClick);
        this.btnPTZRight.setOnTouchListener(buttonClick);
        this.btnPTZUp.setOnTouchListener(buttonClick);
        this.btnPTZDown.setOnTouchListener(buttonClick);

        if(!isWifiEnabled()) {
            this.displayError(getResources().getString(R.string.dialog_title_wifi_error), getResources().getString(R.string.dialog_message_wifi_error));
        }

        (new Thread(streamControl)).start();
        this.motionController = new MotionController(camera.getHost() + ":" + camera.getPort(), camera.getUsername(), camera.getPassword()); 
        (new Thread(motionController)).start();
    }

    /*
     * If a PTZ button received a motion event, figure out which one received it and perform 
     * the appropriate action.  To help figure out which button was pressed, turn it green
     */
    final OnTouchListener buttonClick = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                ((TextView) v).setTextColor(0xFF00FF00);
                switch(v.getId()) {
                    case R.id.ptz_left:
                        motionController.moveLeft(true);
                        break;
                    case R.id.ptz_right:
                        motionController.moveRight(true);
                        break;
                    case R.id.ptz_up:
                        motionController.moveUp(true);
                        break;
                    case R.id.ptz_down:
                        motionController.moveDown(true);
                        break;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                ((TextView) v).setTextColor(0xFFFFFFFF);
                switch(v.getId()) {
                    case R.id.ptz_left:
                        motionController.moveLeft(false);
                        break;
                    case R.id.ptz_right:
                        motionController.moveRight(false);
                        break;
                    case R.id.ptz_up:
                        motionController.moveUp(false);
                        break;
                    case R.id.ptz_down:
                        motionController.moveDown(false);
                        break;
                }
            }
            return true;
        }
    };

    /*
     * The worker thread that will continuously get snapshots from the camera API.  When first 
     * launching the thread, make sure the camera can be reached via a PortScanner.  If the 
     * PortScanner can reach the camera, then streaming will work
     */
    final Runnable streamControl = new Runnable() {
        public void run() {
            PortScanner portScanner = new PortScanner(camera.getHost(), Integer.parseInt(camera.getPort()));
            if(!portScanner.isReachableWithRetry(500, 3)) {
                isStreaming = false;
                threadHandler.post(errorUI);
            }
            while(isStreaming) {
                inputStream = remoteImage.download(cameraUri + "/snapshot.cgi?user=" + camera.getUsername() + "&pwd=" + camera.getPassword() + "&resolution=32");
                if(inputStream != null) {
                    snapshot = BitmapFactory.decodeStream(inputStream);
                }
                threadHandler.post(updateUI);
            }
        }
    };

    /*
     * If the camera is still streaming and a snapshot exists, update the ImageView 
     * on the UI thread
     */
    final Runnable updateUI = new Runnable() {
        public void run() {
            if(snapshot != null && isStreaming) {
                ivStream.setImageBitmap(snapshot);
            }
        }
    };

    /*
     * If the PortScanner fails to reach the camera, show an error on the UI
     * thread
     */
    final Runnable errorUI = new Runnable() {
        public void run() {
            displayError(getResources().getString(R.string.dialog_title_communication_error), "There was a problem reaching `" + camera.getTitle() + "` at " + camera.getHost() + ":" + camera.getPort());
        }
    };

    /*
     * Display a custom error message and finish or destroy the activity killing all running 
     * threads
     *
     * @param    String title
     * @param    String message
     */
    public void displayError(String title, String message) {
        AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this.context);
        errorBuilder.setMessage(message)
            .setTitle(title)
            .setCancelable(false)
            .setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    finish();
                }
            });
        AlertDialog alert = errorBuilder.show();
    }

    /*
     * Clean up all running threads and release the screen lock because 
     * the activity is being destroyed
     *
     * @param
     * @return
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isStreaming = false;
        this.motionController.setIsActive(false);
        this.screenLock.release();
    }

    /*
     * Check to see if the device is connected to the internet
     *
     * @param
     * @return   boolean
     */
    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /*
     * Check to see if the Wifi is enabled
     *
     * @param
     * @return   boolean
     */
    public boolean isWifiEnabled() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.settings.getBoolean("pref_key_analytics", true)) {
            EasyTracker.getInstance(this).activityStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(this.settings.getBoolean("pref_key_analytics", true)) {
            EasyTracker.getInstance(this).activityStop(this);
        }
    }

}
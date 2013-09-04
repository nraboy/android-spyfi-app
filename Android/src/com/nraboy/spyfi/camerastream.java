package com.nraboy.spyfi;

import android.app.*;
import android.os.*;
import java.io.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.widget.LinearLayout.LayoutParams;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import com.google.analytics.tracking.android.EasyTracker;

public class camerastream extends Activity {

    private ImageView iStream;
    private LinearLayout streamLayout;
    private TextView errorView;
    private String cameraLink = "";
    private RemoteImage rImage;
    private InputStream is;
    private Bitmap bmp = null;
    private Thread sThread = null;
    private boolean isStreaming = false;
    private DataSource ds;
    private int cameraId;
    private Camera c;
    private PowerManager.WakeLock screenLock;
    private Handler h;
    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerastream);
        cameraId = getIntent().getExtras().getInt("cameraId");
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        screenLock = pm.newWakeLock(pm.SCREEN_DIM_WAKE_LOCK, "Stay Awake While Streaming");
        screenLock.acquire();
        ds = new DataSource(this);
        c = ds.select(cameraId);
        Display display = getWindowManager().getDefaultDisplay();
        if(display.getWidth() < 600) {
            cameraLink = c.getHost() + ":" + c.getPort() + "/snapshot.cgi?user=" + c.getUsername() + "&pwd=" + c.getPassword() + "&resolution=8";
        } else {
            cameraLink = c.getHost() + ":" + c.getPort() + "/snapshot.cgi?user=" + c.getUsername() + "&pwd=" + c.getPassword() + "&resolution=32";
        }
        iStream = (ImageView)findViewById(R.id.imagestream);
        streamLayout = (LinearLayout)findViewById(R.id.streamlayout);
        errorView = new TextView(this);
        rImage = new RemoteImage();
        h = new Handler();
        sThread = new Thread(streamControl);        
        sThread.start();        
    }

    @Override
    public void onStart() {
        super.onStart();
        if(settings.getBoolean("pref_key_analytics", true)) {
            EasyTracker.getInstance().activityStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(settings.getBoolean("pref_key_analytics", true)) {
            EasyTracker.getInstance().activityStop(this);
        }
    }
    
    final Runnable streamControl = new Runnable() {
        public void run() {
            isStreaming = true;
            while(isStreaming) {
                is = rImage.download(cameraLink);
                if(is != null) {
                    bmp = BitmapFactory.decodeStream(is);
                } else {
                    System.out.println("Could Not Launch Camera");
                    isStreaming = false;
                }
                h.post(updateUI);
            }
        }
    };
    
    final Runnable updateUI = new Runnable() {
        public void run() {
            if(isStreaming == false) {
                errorView.setText(getResources().getString(R.string.dialog_message_stream_error));
                errorView.setGravity(Gravity.CENTER);
                errorView.setTextSize(2, 20);
                errorView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                streamLayout.removeView(iStream);
                streamLayout.addView(errorView);
                return;
            }
            if(bmp != null) {
                iStream.setImageBitmap(bmp);
            }
        }
    };
    
    public void onDestroy() {
        super.onDestroy();
        if(sThread != null) {
            isStreaming = false;
            sThread.interrupt();
            sThread = null;
            rImage = null;
        }
        screenLock.release();
    }

}

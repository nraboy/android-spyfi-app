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
import android.app.AlertDialog.*;
import android.widget.AdapterView.*;
import android.preference.*;
import android.preference.Preference.*;
import com.google.android.gms.ads.*;
import com.google.analytics.tracking.android.EasyTracker;

public class MainActivity extends Activity {

    private Context context;
    private SharedPreferences settings;
    private ListView lvCamera;
    private ArrayList<Camera> cameraList;
    private CameraListAdapter cameraListAdapter;
    private Datasource datasource;
    private AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        this.settings = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.lvCamera = (ListView) findViewById(R.id.cameras);
        this.adView = (AdView)findViewById(R.id.ad_view);
        this.cameraList = new ArrayList<Camera>();
        this.datasource = new Datasource(this.context);
        this.cameraListAdapter = new CameraListAdapter(this.context, this.cameraList);
        this.lvCamera.setAdapter(cameraListAdapter);
        this.cameraList.addAll(this.datasource.selectAll());
        this.lvCamera.setOnItemLongClickListener(cameraLongClick);
        this.lvCamera.setOnItemClickListener(cameraClick);
        AdRequest adRequest = new AdRequest.Builder().build();
        this.adView.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.cameraList.clear();
        this.cameraList.addAll(this.datasource.selectAll());
        this.cameraListAdapter.notifyDataSetChanged();
    }

    /*
     * When a camera is selected in the list, open the stream by starting the stream 
     * activity and passing the camera to stream
     */
    private OnItemClickListener cameraClick = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            Camera camera = (Camera) lvCamera.getItemAtPosition(position);
            Intent cameraStream = new Intent(context, StreamActivity.class);
            cameraStream.putExtra("camera_id", camera.getId());
            startActivity(cameraStream);
        }
    };

    private OnItemLongClickListener cameraLongClick = new OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
            final Camera camera = (Camera) lvCamera.getItemAtPosition(position);
            final String button_update = getResources().getString(R.string.button_update);
            final String button_delete = getResources().getString(R.string.button_delete);
            final CharSequence[] items = {button_update, button_delete};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getResources().getString(R.string.dialog_title_action));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if(item == 0) {
                        Intent updateCamera = new Intent(context, CameraInfoActivity.class);
                        updateCamera.putExtra("camera_id", camera.getId());
                        startActivity(updateCamera);
                    }
                    if(item == 1) {
                        datasource.delete(camera.getId());
                        cameraList.clear();
                        cameraList.addAll(datasource.selectAll());
                        cameraListAdapter.notifyDataSetChanged();
                    }
                }
            });
            AlertDialog alert = builder.show();
            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.add:
                i = new Intent(this.context, CameraInfoActivity.class);
                i.putExtra("camera_id", 0);
                startActivity(i);
                return true;
            case R.id.settings:
                i = new Intent(this.context, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
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
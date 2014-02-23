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
import android.preference.*;
import android.preference.Preference.*;
import com.google.analytics.tracking.android.EasyTracker;

public class CameraInfoActivity extends Activity {

    private Context context;
    private SharedPreferences settings;
    private Datasource datasource;
    private int cameraId;
    private EditText etTitle;
    private EditText etHost;
    private EditText etPort;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerainfo);
        this.context = this;
        this.settings = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.etTitle = (EditText) findViewById(R.id.title);
        this.etHost = (EditText) findViewById(R.id.host);
        this.etPort = (EditText) findViewById(R.id.port);
        this.etUsername = (EditText) findViewById(R.id.username);
        this.etPassword = (EditText) findViewById(R.id.password);
        this.datasource = new Datasource(this.context);
        this.cameraId = getIntent().getExtras().getInt("camera_id");
        if(this.cameraId != 0) {
            this.load();
        }
    }

    /*
     * Fill the form based on the camera id that was provided
     *
     * @param
     * @return
     */
    public void load() {
        Camera c = this.datasource.select(this.cameraId);
        this.etTitle.setText(c.getTitle());
        this.etHost.setText(c.getHost());
        this.etPort.setText(c.getPort());
        this.etUsername.setText(c.getUsername());
        this.etPassword.setText(c.getPassword());
    }

    /*
     * Prepare the contents of the form for saving to the database
     *
     * @param
     * @return   Camera
     */
    public Camera process() {
        Camera c = new Camera();
        c.setTitle(this.etTitle.getText().toString());
        c.setHost(this.etHost.getText().toString());
        c.setPort(this.etPort.getText().toString());
        c.setUsername(this.etUsername.getText().toString());
        c.setPassword(this.etPassword.getText().toString());
        if(!c.getHost().startsWith("http://") && !c.getHost().equals("")) {
            c.setHost("http://" + c.getHost());
        }
        return c;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Camera c = this.process();
                if(!c.getTitle().equals("") && !c.getHost().equals("") && !c.getPort().equals("") && !c.getUsername().equals("") && !c.getPassword().equals("")) {
                    if(this.cameraId == 0) {
                        this.datasource.insert(c);
                    } else {
                        c.setId(this.cameraId);
                        this.datasource.update(c);
                    }
                    finish();
                } else {
                    Toast.makeText(this.context, getResources().getString(R.string.dialog_message_incomplete), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.cancel:
                finish();
                return true;
            case R.id.settings:
                Intent i = new Intent(this.context, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_camerainfo, menu);
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
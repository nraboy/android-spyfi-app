package com.nraboy.spyfi;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.*;
import android.view.View;
import android.widget.*;
import android.content.Context;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import com.google.analytics.tracking.android.EasyTracker;

public class updatecamera extends SherlockActivity {

    private DataSource ds;
    private int cameraId;
    private EditText cameraLabel;
    private EditText cameraHost;
    private EditText cameraPort;
    private EditText cameraUsername;
    private EditText cameraPassword;
    private Context context;
    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerainfo);
        context = this;
        ds = new DataSource(this);
        cameraId = getIntent().getExtras().getInt("cameraId");
        cameraLabel = (EditText) findViewById(R.id.camera_info_label);
        cameraHost = (EditText) findViewById(R.id.camera_info_host);
        cameraPort = (EditText) findViewById(R.id.camera_info_port);
        cameraUsername = (EditText) findViewById(R.id.camera_info_username);
        cameraPassword = (EditText) findViewById(R.id.camera_info_password);
        Camera c = ds.select(cameraId);
        cameraLabel.setText(c.getLabel());
        cameraHost.setText(c.getHost());
        cameraPort.setText(c.getPort());
        cameraUsername.setText(c.getUsername());
        cameraPassword.setText(c.getPassword());   
        settings = PreferenceManager.getDefaultSharedPreferences(this);     
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_info_save:
                cameraLabel = (EditText) findViewById(R.id.camera_info_label);
                cameraHost = (EditText) findViewById(R.id.camera_info_host);
                cameraPort = (EditText) findViewById(R.id.camera_info_port);
                cameraUsername = (EditText) findViewById(R.id.camera_info_username);
                cameraPassword = (EditText) findViewById(R.id.camera_info_password);
                String cameraLabelStr = cameraLabel.getText().toString();
                String cameraHostStr = cameraHost.getText().toString();
                if(!cameraHostStr.startsWith("http://") && !cameraHostStr.equals("")) {
                    cameraHostStr = "http://" + cameraHostStr;
                }
                String cameraPortStr = cameraPort.getText().toString();
                String cameraUsernameStr = cameraUsername.getText().toString();
                String cameraPasswordStr = cameraPassword.getText().toString();
                if(!cameraLabelStr.equals("") && !cameraHostStr.equals("") && !cameraPortStr.equals("") && !cameraUsernameStr.equals("")) {
                    Camera c = new Camera();
                    c.setId(cameraId);
                    c.setLabel(cameraLabelStr);
                    c.setHost(cameraHostStr);
                    c.setPort(cameraPortStr);
                    c.setUsername(cameraUsernameStr);
                    c.setPassword(cameraPasswordStr);
                    ds.update(c);
                    finish();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.dialog_message_incomplete), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.camera_info_cancel:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.layout.camerainfomenu, menu);
        return true;
    }
}

package com.nraboy.spyfi;

/*
 * Spyfi
 * Created by Nic Raboy
 * www.nraboy.com
 */

import android.app.*;
import android.os.*;
import android.content.*;
import java.util.*;
import android.net.*;
import android.preference.*;
import android.preference.Preference.*;
import com.google.analytics.tracking.android.EasyTracker;

public class SplashActivity extends Activity {

    private Context context;
    private Runnable splashRunner;
    private SharedPreferences settings;
    private Intent mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.context = this;
        this.settings = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.mainActivity = new Intent(this.context, MainActivity.class);
        this.mainActivity.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.splashRunner = new Runnable() {
            public void run() {
                long startTime = System.currentTimeMillis();
                long elapsedTime;
                while(true) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    if(elapsedTime > 300) {
                        startActivity(mainActivity);
                        break;
                    }
                }
            }
        };
        new Thread(this.splashRunner).start();
    }

    @Override
    public void onBackPressed() { }

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
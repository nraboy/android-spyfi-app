package com.nraboy.spyfi;

/*
 * Spyfi
 * Created by Nic Raboy
 * www.nraboy.com
 */

import com.nraboy.spyfi.objects.*;
import com.nraboy.spyfi.datasources.*;
import android.app.*;
import android.os.*;
import android.content.*;
import java.util.*;
import android.net.*;
import android.util.*;
import java.io.*;
import android.database.sqlite.*;
import android.database.*;
import android.preference.*;
import android.preference.Preference.*;
import com.google.analytics.tracking.android.EasyTracker;

public class SplashActivity extends Activity {

    private Context context;
    private Runnable splashRunner;
    private SharedPreferences settings;
    private Intent mainActivity;
    private Datasource datasource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.context = this;
        this.settings = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.datasource = new Datasource(this.context);
        this.mainActivity = new Intent(this.context, MainActivity.class);
        this.mainActivity.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.splashRunner = new Runnable() {
            public void run() {
                long startTime = System.currentTimeMillis();
                long elapsedTime;
                boolean isLoaded = false;
                while(true) {
                    if(!isLoaded) {
                        //Upgrade db from 1.x.x to 2.0.0
                        upgradeLegacyDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db");
                        isLoaded = true;
                    }
                    elapsedTime = System.currentTimeMillis() - startTime;
                    if(elapsedTime > 1000) {
                        startActivity(mainActivity);
                        break;
                    }
                }
            }
        };
        new Thread(this.splashRunner).start();
    }

    /*
     * Disable the back button so no drama happens to our load thread
     *
     * @param
     * @return
     */
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

    /*
     * =====================================================
     * =====================================================
     * ============ DB UPGRADE 1.X.X TO 2.0.0 ==============
     * =====================================================
     * =====================================================
     */
    public void upgradeLegacyDatabase(String legacyDatabase) {
        if(hasLegacyDatabase(legacyDatabase)) {
            Log.d(getResources().getString(R.string.app_name), "Upgrading Legacy Database...");
            Camera camera;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(legacyDatabase, null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("select * from tblCamera order by lower(camera_desc) asc", null);
            while(c.moveToNext()) {
                camera = new Camera();
                camera.setTitle(c.getString(c.getColumnIndex("camera_desc")));
                camera.setHost(c.getString(c.getColumnIndex("host")));
                camera.setPort(c.getString(c.getColumnIndex("port")));
                camera.setUsername(c.getString(c.getColumnIndex("username")));
                camera.setPassword(c.getString(c.getColumnIndex("password")));
                this.datasource.insert(camera);
            }
            c.close();
            db.close();
            Log.d(getResources().getString(R.string.app_name), "Removing Obsolete Legacy Database...");
            removeLegacyDatabase(legacyDatabase);
        }
    }
    
    public boolean hasLegacyDatabase(String legacyDatabase) {
        File f = new File(legacyDatabase);
        return f.exists();
    }

    public boolean removeLegacyDatabase(String legacyDatabase) {
        File f = new File(legacyDatabase);
        return f.delete();
    }
    /*
     * =====================================================
     * =====================================================
     * =========== END DB UPGRADE 1.X.X TO 2.0.0 ===========
     * =====================================================
     * =====================================================
     */

}
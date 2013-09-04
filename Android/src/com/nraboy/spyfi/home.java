package com.nraboy.spyfi;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.*;
import java.util.ArrayList;
import android.content.*;
import android.widget.AdapterView.*;
import android.widget.*;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.view.View;
import java.io.*;
import android.database.sqlite.*;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.net.Uri;
import com.google.analytics.tracking.android.EasyTracker;

public class home extends SherlockActivity {

    private ListView lvCamera;
    private ArrayList<Camera> cameraList;
    private CameraListAdapter adapter;
    private DataSource ds;
    private Context context;
    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.context = this;
        getSupportActionBar().setHomeButtonEnabled(true);
        this.cameraList = new ArrayList<Camera>(); 
        
        //For debugging (db upgrade 1.x.x to 2.0.0
        //copyDatabase("/data/data/com.nraboy.spyfi/databases/", "Spyfi.db");
        if(oldDBExists()) {
            System.out.println("FOUND OBSOLETE DATABASE");
            renameOldDB();
        }
        
        this.ds = new DataSource(this);      
        
        //Upgrade db from 1.x.x to 2.0.0
        upgradeDB();
        
        this.lvCamera = (ListView) findViewById(R.id.camera_list);
        this.adapter = new CameraListAdapter(this, this.cameraList);
        this.lvCamera.setAdapter(adapter);
        this.lvCamera.setOnItemLongClickListener(cameraLongClick);
        this.lvCamera.setOnItemClickListener(cameraClick);
        for(int i = 0; i < ds.selectAll().size(); i++) {
            cameraList.add(ds.selectAll().get(i));
        }
        this.settings = PreferenceManager.getDefaultSharedPreferences(this.context);
        int usageCount = this.settings.getInt("rstamp", 0);
        if(usageCount == 10) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setMessage(getResources().getString(R.string.dialog_message_please_rate))
                .setTitle(getResources().getString(R.string.dialog_title_please_rate))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.button_rate_now), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_store)));
                        startActivity(browserIntent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.button_dont_remind), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
            AlertDialog alert = builder.show();
        }
        this.settings.edit().putInt("rstamp", usageCount + 1).commit();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if(this.settings.getBoolean("pref_key_analytics", true)) {
            EasyTracker.getInstance().activityStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(this.settings.getBoolean("pref_key_analytics", true)) {
            EasyTracker.getInstance().activityStop(this);
        }
    }
    
    
    /*
     * =====================================================
     * =====================================================
     * ============ DB UPGRADE 1.X.X TO 2.0.0 ==============
     * =====================================================
     * =====================================================
     */
    public void upgradeDB() {
        File f = new File("/data/data/com.nraboy.spyfi/databases/Spyfi_old.db");
        if(f.exists()) {
            System.out.println("UPGRADING DATABASE");
            Camera cam;
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi_old.db", null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("select * from tblCamera order by lower(camera_desc) asc", null);
            while(c.moveToNext()) {
                cam = new Camera();
                cam.setLabel(c.getString(c.getColumnIndex("camera_desc")));
                cam.setHost(c.getString(c.getColumnIndex("host")));
                cam.setPort(c.getString(c.getColumnIndex("port")));
                cam.setUsername(c.getString(c.getColumnIndex("username")));
                cam.setPassword(c.getString(c.getColumnIndex("password")));
                cam.setTypeId(c.getInt(c.getColumnIndex("camera_type_id")));
                ds.insert(cam);
            }
            c.close();
            db.close();
            System.out.println("REMOVING OBSOLETE DATABASE");
            f.delete();
        }
    }
    
    public boolean oldDBExists() {
        File f = new File("/data/data/com.nraboy.spyfi/databases/Spyfi.db");
        return f.exists();
    }
    
    public void renameOldDB() {
        System.out.println("RENAMING OBSOLETE DATABASE");
        File f = new File("/data/data/com.nraboy.spyfi/databases/Spyfi.db");
        File f2 = new File("/data/data/com.nraboy.spyfi/databases/Spyfi_old.db");
        f.renameTo(f2);
    }
    
    private void copyDatabase(String path, String db) {
        try {
            InputStream inputStream = getAssets().open(db);
            File f = new File(path);
            if ( !f.exists() )
                f.mkdir();
            OutputStream outputStream = new FileOutputStream(path + db);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    /*
     * =====================================================
     * =====================================================
     * =========== END DB UPGRADE 1.X.X TO 2.0.0 ===========
     * =====================================================
     * =====================================================
     */
    
    
    
    
    
    
    @Override
    public void onResume() {
        super.onResume();
        cameraList.clear();
        for(int i = 0; i < ds.selectAll().size(); i++) {
            cameraList.add(ds.selectAll().get(i));
        }
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent showSettings = new Intent(this, SettingsActivity.class);
                startActivity(showSettings);
                return true;
            case R.id.add_camera:
                Intent addCamera = new Intent(this, addcamera.class);
                startActivity(addCamera);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.layout.homemenu, menu);
        return true;
    }
    
    private OnItemClickListener cameraClick = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            Camera fullObject = (Camera) lvCamera.getItemAtPosition(position);
            Intent cameraStream = new Intent(context, camerastream.class);
            cameraStream.putExtra("cameraId", fullObject.getId());
            startActivity(cameraStream);
        }
    };
    
    private OnItemLongClickListener cameraLongClick = new OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
            final Camera fullObject = (Camera) lvCamera.getItemAtPosition(position);
            final String button_update = getResources().getString(R.string.button_update);
            final String button_delete = getResources().getString(R.string.button_delete);
            final CharSequence[] items = {button_update, button_delete};
            AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
            builder.setTitle(getResources().getString(R.string.dialog_title_action));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if(item == 0) {
                        Intent updateCamera = new Intent(context, updatecamera.class);
                        updateCamera.putExtra("cameraId", fullObject.getId());
                        startActivity(updateCamera);
                    }
                    if(item == 1) {
                        ds.delete(fullObject.getId());
                        cameraList.clear();
                        for(int i = 0; i < ds.selectAll().size(); i++) {
                            cameraList.add(ds.selectAll().get(i));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            AlertDialog alert = builder.show();
            return true;
        }
    };
}

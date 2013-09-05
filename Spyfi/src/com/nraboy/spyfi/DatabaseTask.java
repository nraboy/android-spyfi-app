package com.nraboy.spyfi;

/*
 * Product Name: Spyfi
 * Created By: Nic Raboy
 * Version: 1.3.0
 */

import android.os.AsyncTask;
import java.io.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.app.Activity;

public class DatabaseTask extends AsyncTask<String, Integer, String> {

    private mainactivity myActivity;
    public boolean isComplete = false;
    private File f;
    private boolean dbEmpty = false;

    public DatabaseTask(mainactivity context) {
        myActivity = context;
    }    
    
    public void attach(mainactivity context) {
        myActivity = context;
    }
    
    public void detach() {
        myActivity = null;
    }
    
    /*
     * Check to see if the database exists and if it doesn't then copy the
     * database to the data directory on the device.
     */
    @Override
    protected String doInBackground(String... params) {
        if(!checkDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db")) {
            copyDatabase("/data/data/com.nraboy.spyfi/databases/", "Spyfi.db");
        }
        return "";
    }
    
    /*
     * Check to see if the database exists.  If we don't first check, the database will be
     * created every time which takes time and resources.
     */
    private boolean checkDatabase(String db) {
        File f = new File(db);
        return f.exists();
    }
    
    /*
     * The database cannot be used until it is first copied out of the
     * apk file and into the android devices data directory.  This will
     * copy it to the appropriate location.
     */
    private void copyDatabase(String path, String db) {
        try {
            InputStream inputStream = myActivity.getAssets().open(db);
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
     * After the database has finished copying run a query on it to see if any cameras exist.  If no cameras exist
     * then open the activity responsible for adding new cameras.
     */
    @Override
    protected void onPostExecute(String result) {
        myActivity.itemList.clear();
        myActivity.db = SQLiteDatabase.openDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db", null, SQLiteDatabase.OPEN_READONLY);
        if(myActivity.db == null) {
            myActivity.itemList.add("Database Load Failure");
        } else {
            myActivity.c = myActivity.db.rawQuery("select camera_desc from tblCamera order by lower(camera_desc) asc", null);
            if(myActivity.c.getCount() > 0) {
                while(myActivity.c.moveToNext()) {
                    myActivity.itemList.add(myActivity.c.getString(myActivity.c.getColumnIndex("camera_desc")));
                }
            } else {
                myActivity.itemList.add("No Cameras Have Been Added Yet");
                dbEmpty = true;
            }
            myActivity.c.close();
        }
        myActivity.db.close();
        supportVersions();
        myActivity.adapter.notifyDataSetChanged();
        if(dbEmpty)
            myActivity.startActivity(myActivity.addcamera);
        detach();
        isComplete = true;
    }
    
    /*
     * The database may change through various versions.  We need to make sure each of
     * these changes get updated in peoples databases.
     */
    private void supportVersions() {
        updateCameraTypes("Yanmix");
    }
    
    /*
     * Every time a camera brand becomes supported it needs to be added to the database.  If
     * this is a fresh download it will already be in the database
     */
    private void updateCameraTypes(String ct_desc) {
        myActivity.db = SQLiteDatabase.openDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db", null, SQLiteDatabase.OPEN_READWRITE);
        myActivity.c = myActivity.db.rawQuery("select camera_type from tblCameraType where camera_type = '" + ct_desc + "'", null);
        if(myActivity.c.getCount() <= 0) {
            String query = "insert into tblCameraType (camera_type) values ('" + ct_desc + "')";
            myActivity.db.execSQL(query);
        }
        myActivity.c.close();
        myActivity.db.close();
    }

    @Override
    protected void onPreExecute() { }

    @Override
    protected void onProgressUpdate(Integer... p) { }
}

package com.nraboy.spyfi;

/*
 * Product Name: Spyfi
 * Created By: Nic Raboy
 * Version: 1.3.0
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import java.io.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import java.util.*;
import android.view.*;
import android.widget.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.content.res.*;
import android.content.Intent;
import android.widget.LinearLayout.LayoutParams;
import android.os.Environment;

public class stream extends Activity
{

    DatabaseTask dTask = null;
    ImageView iStream;
    LinearLayout streamLayout;
    TextView errorView;
    SQLiteDatabase db;
    Cursor c;
    String ip = "";
    String port = "";
    String user = "";
    String pass = "";
    String cameraLink = "";
    RemoteImage rImage;
    InputStream is;
    Bitmap tempBmp = null;
    Bitmap bmp = null;
    Thread sThread = null;
    boolean isStreaming = false;
    Bundle postData;
    Intent about;
    boolean isSaving = false;
    boolean isSaved = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);
        
        about = new Intent(this, about.class); 
        
        postData = getIntent().getExtras();
        String camera_desc = postData.getString("cameraname");
        
        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db", null, SQLiteDatabase.OPEN_READONLY);
            c = db.rawQuery("select * from tblCamera where camera_desc = '" + camera_desc + "'", null);
            if(c.moveToFirst()) {
                ip = c.getString(c.getColumnIndex("host"));
                port = c.getString(c.getColumnIndex("port"));
                user = c.getString(c.getColumnIndex("username"));
                pass = c.getString(c.getColumnIndex("password"));
                Display display = getWindowManager().getDefaultDisplay();
                if(display.getWidth() < 600) {
                    // Low resolution image stream using the cgi link
                    cameraLink = ip + ":" + port + "/snapshot.cgi?user=" + user + "&pwd=" + pass + "&resolution=8";
                } else {
                    // High resolution image stream using the cgi link
                    cameraLink = ip + ":" + port + "/snapshot.cgi?user=" + user + "&pwd=" + pass + "&resolution=32";
                }
            }
            c.close();
            db.close();
        } catch (SQLiteException e) {
            System.out.println("SQLException: " + e);
        }
        
        iStream = (ImageView)findViewById(R.id.imagestream);
        streamLayout = (LinearLayout)findViewById(R.id.streamlayout);
        errorView = new TextView(this);
        
        rImage = new RemoteImage();
        
        final Handler h = new Handler();
        
        final Runnable updateUI = new Runnable() {
            public void run() {
                if(isStreaming == false) {
                    errorView.setText("Camera failed to load.  Please make sure all connection information is correct and the camera is set up correctly.");
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
                if(isSaving) {
                    if(isSaved) {
                        Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Image Failed To Save", Toast.LENGTH_SHORT).show();
                    }
                    isSaving = false;
                    isSaved = false;
                }
            }
        };
        
        sThread = new Thread(new Runnable() {
            public void run() {
                isStreaming = true;
                while(isStreaming) {
                    is = rImage.download(cameraLink);
                    if(isSaving) {
                        Calendar rightNow = Calendar.getInstance();
                        if(is != null)
                            isSaved = rImage.save(is, Environment.getExternalStorageDirectory().getPath() + "/spyfi/save_" + rightNow.getTimeInMillis() + ".jpg");
                        else
                            isSaved = false;
                    }
                    if(is != null) {
                        tempBmp = BitmapFactory.decodeStream(is);
                        if(tempBmp != null) {
                            bmp = tempBmp;
                            tempBmp = null;
                        }
                    } else {
                        System.out.println("Could Not Launch Camera");
                        isStreaming = false;
                    }
                    h.post(updateUI);
                }
            };
        });
        
        sThread.start();       
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.streammenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                saveImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /*
     * Kill the thread when the user closes the application or hits the back button
     * to leave the stream.  Also destroy when screen is rotated.  We do this so the thread
     * doesn't continue running when the application is not running.  We also do this so a new
     * thread isn't piled onto this thread when the screen rotates.  Instead we just create a new
     * thread when rotated without keeping the old.
     */
    public void onPause() {
        super.onPause();
        if(sThread != null) {
            isStreaming = false;
            sThread.interrupt();
            sThread = null;
            rImage = null;
        }
    }
    
    /*
     * Start the streaming again when returning to the stream activity
     */
    public void onResume() {
        super.onResume();
        isStreaming = true;
    }
    
    /*
     * Kill the thread when the user closes the application or hits the back button
     * to leave the stream.  Also destroy when screen is rotated.  We do this so the thread
     * doesn't continue running when the application is not running.  We also do this so a new
     * thread isn't piled onto this thread when the screen rotates.  Instead we just create a new
     * thread when rotated without keeping the old.
     */
    public void onDestroy() {
        super.onDestroy();
        if(sThread != null) {
            isStreaming = false;
            sThread.interrupt();
            sThread = null;
            rImage = null;
        }
    }
    
    public void saveImage() {
        isSaving = true;
    }

}

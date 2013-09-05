package com.nraboy.spyfi;

/*
 * Product Name: Spyfi
 * Created By: Nic Raboy
 * Version: 1.3.0
 */

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ImageView;
import java.io.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import java.util.*;
import android.view.*;
import android.widget.*;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.content.res.*;
import android.content.Intent;
import android.text.*;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView.OnEditorActionListener;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Environment;

public class mainactivity extends ListActivity
{

    ArrayList<String> itemList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView cameraList;
    SQLiteDatabase db;
    Cursor c;
    Intent stream;
    Intent addcamera;
    Intent updatecamera;
    Intent about;
    DatabaseTask dTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        stream = new Intent(this, stream.class); 
        addcamera = new Intent(this, addcamera.class); 
        updatecamera = new Intent(this, updatecamera.class); 
        about = new Intent(this, about.class); 
        
        adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, itemList);
        this.setListAdapter(adapter);
        cameraList = getListView(); 
        cameraList.setOnItemClickListener(listClick);
        cameraList.setOnItemLongClickListener(listHold);
        
        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/spyfi");
        if (!f.exists())
            f.mkdir();
                 
        dTask = (DatabaseTask)getLastNonConfigurationInstance();
        
        if(dTask == null) {
            dTask = new DatabaseTask(this);
            dTask.execute("");
        } else {
            dTask.attach(this);
        }
        
    }   
    
    public void onResume() {
        super.onResume();
        File f = new File("/data/data/com.nraboy.spyfi/databases/Spyfi.db");
        if(f.exists()) {
            itemList.clear();
            db = SQLiteDatabase.openDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db", null, SQLiteDatabase.OPEN_READWRITE);
            if(db == null) {
                itemList.add("Database Load Failure");
            } else {
                c = db.rawQuery("select camera_desc from tblCamera order by lower(camera_desc) asc", null);
                if(c.getCount() > 0) {
                    while(c.moveToNext()) {
                        itemList.add(c.getString(c.getColumnIndex("camera_desc")));
                    }
                } else {
                    itemList.add("No Cameras Have Been Added Yet");
                }
                c.close();
            }
            db.close();
            adapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.mainmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAbout:
                startActivity(about);
                return true;
            case R.id.menuAddCamera:
                startActivity(addcamera);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private OnItemClickListener listClick = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String camera_desc = ((TextView) view).getText().toString();
            if(camera_desc.equals("No Cameras Have Been Added Yet") == false) {
                stream.putExtra("cameraname", ((TextView) view).getText());
                startActivity(stream);
            }
        }
    }; 
    
    private OnItemLongClickListener listHold = new OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final String camera_desc = ((TextView) view).getText().toString();
            final CharSequence[] items = {"Update Info", "Delete Camera"};
            AlertDialog.Builder builder = new AlertDialog.Builder(mainactivity.this);        
            builder.setTitle("Perform An Action");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if(item == 0) {
                        updatecamera.putExtra("cameraname", camera_desc);
                        startActivity(updatecamera);
                    } else if(item == 1) {
                        db = SQLiteDatabase.openDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db", null, SQLiteDatabase.OPEN_READWRITE);
                        if(db == null) {
                            itemList.add("Database Load Failure");
                        } else {
                            db.execSQL("delete from tblCamera where camera_desc = '" + camera_desc + "'");
                            db.close();
                        }
                        onResume();
                    }
                    Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alert = builder.show();
            return true;
        }
    };
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(c != null) c.close();
        if(db != null) db.close();
    }
    
    @Override
    public Object onRetainNonConfigurationInstance ()
    {
        dTask.detach();
        if(dTask.isComplete == true) {
            dTask = null;
        }
        return dTask;
    }
}

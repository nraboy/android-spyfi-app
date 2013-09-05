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
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.content.res.*;
import android.content.Intent;
import android.text.*;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView.OnEditorActionListener;
import android.os.AsyncTask;
import android.widget.AdapterView.OnItemSelectedListener;

public class updatecamera extends Activity
{

    SQLiteDatabase db;
    Cursor c;
    String camera_desc;
    String host;
    String port;
    String user;
    String pass;
    EditText editDesc;
    EditText editHost;
    EditText editPort;
    EditText editUser;
    EditText editPass;
    String updateCamera;
    Bundle postData;
    String query;
    Spinner spinner;
    String camera_brand;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatecamera); 
        
        postData = getIntent().getExtras();
        updateCamera = postData.getString("cameraname");
            
        editDesc = (EditText) findViewById(R.id.editDesc);  
        editHost = (EditText) findViewById(R.id.editHost);
        editPort = (EditText) findViewById(R.id.editPort);
        editUser = (EditText) findViewById(R.id.editUser);
        editPass = (EditText) findViewById(R.id.editPass);  
        
        // Retreive all the possible camera types from the database and add them to the ArrayList
        db = SQLiteDatabase.openDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor c_cat = db.rawQuery("select _id, camera_type from tblCameraType", null);
        startManagingCursor(c_cat);
        spinner = (Spinner) findViewById(R.id.camerabrand);
        String[] from = new String[]{"camera_type"};
        // create an array of the display item we want to bind our data to
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, c_cat, from, to);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);
        db.close();
        
        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db", null, SQLiteDatabase.OPEN_READONLY);
            c = db.rawQuery("select * from tblCamera where camera_desc = '" + updateCamera + "'", null);
            if(c.moveToFirst()) {
                editDesc.setText(updateCamera);
                editHost.setText(c.getString(c.getColumnIndex("host")));
                editPort.setText(c.getString(c.getColumnIndex("port")));
                editUser.setText(c.getString(c.getColumnIndex("username")));
                editPass.setText(c.getString(c.getColumnIndex("password")));
                spinner.setSelection(c.getInt(c.getColumnIndex("camera_type_id"))-1);
            }
            c.close();
            db.close();
        } catch (SQLiteException e) {
            System.out.println("SQLException: " + e);
        } 
        
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Cursor c1 = (Cursor)parent.getItemAtPosition(pos);
                camera_brand = c1.getString(c1.getColumnIndexOrThrow("_id"));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });   
    }   
    
    /*
     * Update the selected record in the database with all the fields present on the screen.
     */
    public void updateDevice(View view) {
        camera_desc = editDesc.getText().toString();
        host = editHost.getText().toString();
        port = editPort.getText().toString();
        user = editUser.getText().toString();
        pass = editPass.getText().toString();
        if(camera_desc.equals("") || host.equals("") || host.equals("http://") || port.equals("") || user.equals("") || pass.equals("")) {
            Toast.makeText(this, "Please Make Sure All Fields Are Complete", Toast.LENGTH_SHORT).show();
        } else {
            try {
                query = "update tblcamera set camera_type_id = '" + camera_brand + "', host = '" + host + "', port = '" + port + "', username = '" + user + "', password = '" + pass + "', camera_desc = '" + camera_desc + "' where camera_desc = '" + updateCamera + "'";
                db = SQLiteDatabase.openDatabase("/data/data/com.nraboy.spyfi/databases/Spyfi.db", null, SQLiteDatabase.OPEN_READWRITE);
                db.execSQL(query);
                db.close();
            } catch (SQLiteException e) {
                System.out.println("SQLException: " + e);
            }
            this.finish();
        }
    }
}

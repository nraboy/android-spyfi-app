package com.nraboy.spyfi;

import com.nraboy.spyfi.objects.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.util.*;

public class Datasource extends SQLiteOpenHelper {

    public static final String databaseName = "spyfi";
    public static final int databaseVersion = 1;
    
    public Datasource(Context context) {
        super(context, databaseName, null, databaseVersion);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCameraTable = "create table tblCamera (_id integer primary key autoincrement, host text, port text, username text, password text, camera_desc text)";
        db.execSQL(createCameraTable);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    
    public int insert(Camera c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int cameraId = 0;
        values.put("host", c.getHost());
        values.put("port", c.getPort());
        values.put("username", c.getUsername());
        values.put("password", c.getPassword());
        values.put("camera_desc", c.getTitle());
        cameraId = (int)db.insert("tblCamera", null, values);
        db.close();
        return cameraId;
    }
    
    public void update(Camera c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("host", c.getHost());
        values.put("port", c.getPort());
        values.put("username", c.getUsername());
        values.put("password", c.getPassword());
        values.put("camera_desc", c.getTitle());
        int rowsChanged = db.update("tblCamera", values, "_id=?", new String[] { Integer.toString(c.getId()) });
        db.close();
    }
    
    public Camera select(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("tblCamera", new String[] { "_id", "host", "port", "username", "password", "camera_desc" }, "_id=?", new String[] { String.valueOf(id) }, null, null, null, null);
        Camera c = null;
        if (cursor != null) {
            cursor.moveToFirst();
            c = new Camera(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            cursor.close();
        }
        db.close();
        return c;
    }
    
    public ArrayList<Camera> selectAll() {
        ArrayList<Camera> cameraList = new ArrayList<Camera>();
        String selectQuery = "select * from tblCamera";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Camera c = new Camera();
                c.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
                c.setHost(cursor.getString(cursor.getColumnIndex("host")));
                c.setPort(cursor.getString(cursor.getColumnIndex("port")));
                c.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                c.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                c.setTitle(cursor.getString(cursor.getColumnIndex("camera_desc")));
                cameraList.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cameraList;
    }
    
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tblCamera", "_id=?", new String[] { Integer.toString(id) });
        db.close();
    }

}
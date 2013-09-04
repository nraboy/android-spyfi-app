package com.nraboy.spyfi;

import java.io.*;
import java.net.*;

public class RemoteImage {

    boolean isActive;
    private InputStream is;
    
    public RemoteImage() {
        isActive = false;
    }

    public boolean save(String s) {
        try {
            OutputStream os = new FileOutputStream(s);
            byte buffer[] = new byte[1024];
            int length;
            while((length = this.is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.close();
            this.is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public InputStream download(String url) {
        URL myFileURL = null;
        this.isActive = true;
        try {
            myFileURL = new URL(url);
            this.is = myFileURL.openStream();
        } catch (Exception e) { }
        this.isActive = false;
        return this.is;
    }
}

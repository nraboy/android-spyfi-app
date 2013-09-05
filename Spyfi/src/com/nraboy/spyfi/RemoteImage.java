package com.nraboy.spyfi;

/*
 * Product Name: Spyfi
 * Created By: Nic Raboy
 * Version: 1.3.0
 */

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class RemoteImage {

    boolean isActive;
    private InputStream is;
    
    public RemoteImage() { 
        isActive = false;
    }
    
    /*
     * The save function has not been used yet in Spyfi.  The function allows the same stream used
     * in the ImageView to be saved as an image file.  Ideally this would be used to record the 
     * images being streamed to the sdcard.
     */
    public boolean save(InputStream imageStream, String s) {
        try {
            OutputStream os = new FileOutputStream(s);
            byte buffer[] = new byte[1024];
            int length;
            while((length = imageStream.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.close();
            imageStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /*
     * The save function has not been used yet in Spyfi.  The function allows the same stream used
     * in the ImageView to be saved as an image file.  Ideally this would be used to record the 
     * images being streamed to the sdcard.
     */
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
    
    /*
     * Download an image, but keep it as a stream rather than storing it as a file.  Spyfi
     * will connect to the cgi script responsible for returning images and keep these images as a
     * stream.  Use the save function to save this stream as a file.
     */
    public InputStream download(String url) {
        URL myFileURL = null;
        try {
            myFileURL = new URL(url);
            this.is = myFileURL.openStream();
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL Exception");
            return this.is;
        } catch (UnknownHostException e) {
            System.out.println("Host Is Bad");
            return this.is;
        } catch (IOException e) {
            System.out.println("IO Exception");
            return this.is;
        }
        return this.is;
    }
}

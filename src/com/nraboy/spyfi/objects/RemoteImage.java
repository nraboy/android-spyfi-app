package com.nraboy.spyfi;

/*
 * Spyfi
 * Created by Nic Raboy
 * www.nraboy.com
 */

import java.io.*;
import java.net.*;

public class RemoteImage {
    
    public RemoteImage() { }
    
    /*
     * Donwload the target URL to an InputStream.  In this case, 
     * the InputStream is the snapshot provided from the camera API
     *
     * @param    String url
     * @return   InputStream
     */
    public InputStream download(String url) {
        URL snapshotUri = null;
        InputStream inputStream = null;
        try {
            snapshotUri = new URL(url);
            inputStream = snapshotUri.openStream();
        } catch (Exception e) { }
        return inputStream;
    }

}
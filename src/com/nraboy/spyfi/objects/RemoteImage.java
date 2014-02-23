package com.nraboy.spyfi;

import java.io.*;
import java.net.*;
import android.util.*;

public class RemoteImage {
    
    public RemoteImage() { }
    
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
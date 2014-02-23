package com.nraboy.spyfi;

/*
 * Spyfi
 * Created by Nic Raboy
 * www.nraboy.com
 */

import java.net.*;

public class PortScanner {

    private String host;
    private int port;
    
    public PortScanner(String h, int p) {    
        this.host = h.replaceAll("http://", "").replaceAll("www.", "");
        this.port = p;
    }
    
    public void setPort(int p) {
        this.port = p;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public boolean isReachable(int timeOut) {    
        boolean isReachable = false;
        try {
            InetAddress hAddr = InetAddress.getByName(this.host);
            SocketAddress sAddr = new InetSocketAddress(hAddr, this.port);
            Socket socket = new Socket();
            socket.connect(sAddr, timeOut);
            isReachable = true;
            socket.close();
        } catch (Exception e) { 
        	System.out.println(e.getMessage());
        }
        return isReachable;
    }

    public boolean isReachableWithRetry(int timeOut, int count) {
        boolean isReachable = false;
        for(int i = 0; i < count; i++) {
            try {
                InetAddress hAddr = InetAddress.getByName(this.host);
                SocketAddress sAddr = new InetSocketAddress(hAddr, this.port);
                Socket socket = new Socket();
                socket.connect(sAddr, timeOut);
                isReachable = true;
                socket.close();
            } catch (Exception e) { 
                System.out.println(e.getMessage());
            }
            if(isReachable) {
                break;
            }
        }
        return isReachable;
    }
    
}

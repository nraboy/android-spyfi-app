package com.nraboy.spyfi;

public class Camera {

    private int cameraId = 0;
    private String cameraHost = "";
    private String cameraPort = "";
    private String cameraUsername = "";
    private String cameraPassword = "";
    private String cameraLabel = "";
    private int cameraTypeId = 1;
    
    public Camera(int i, String h, String p, String u, String pwd, String l, int tid) {
        this.cameraId = i;
        this.cameraHost = h;
        this.cameraPort = p;
        this.cameraUsername = u;
        this.cameraPassword = pwd;
        this.cameraLabel = l;
        this.cameraTypeId = tid;
    }
    
    public Camera() { }
    
    public void setId(int i) {
        this.cameraId = i;
    }
    
    public int getId() {
        return this.cameraId;
    }

    public void setHost(String h) {
        this.cameraHost = h;
    }

    public String getHost() {
        return this.cameraHost;
    }

    public void setPort(String p) {
        this.cameraPort = p;
    }

    public String getPort() {
        return this.cameraPort;
    }

    public void setUsername(String u) {
        this.cameraUsername = u;
    }
    
    public String getUsername() {
        return this.cameraUsername;
    }
    
    public void setPassword(String p) {
        this.cameraPassword = p;
    }
    
    public String getPassword() {
        return this.cameraPassword;
    }
    
    public void setLabel(String l) {
        this.cameraLabel = l;
    }
    
    public String getLabel() {
        return this.cameraLabel;
    }
    
    public void setTypeId(int tid) {
        this.cameraTypeId = tid;
    }
    
    public int getTypeId() {
        return this.cameraTypeId;
    }
}

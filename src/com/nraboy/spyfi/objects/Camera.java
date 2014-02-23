package com.nraboy.spyfi;

public class Camera {

    private int id = 0;
    private String host = "";
    private String port = "";
    private String username = "";
    private String password = "";
    private String title = "";
    
    public Camera(int i, String h, String p, String u, String pwd, String l) {
        this.id = i;
        this.host = h;
        this.port = p;
        this.username = u;
        this.password = pwd;
        this.title = l;
    }
    
    public Camera() { }
    
    public void setId(int i) {
        this.id = i;
    }
    
    public int getId() {
        return this.id;
    }

    public void setHost(String h) {
        this.host = h;
    }

    public String getHost() {
        return this.host;
    }

    public void setPort(String p) {
        this.port = p;
    }

    public String getPort() {
        return this.port;
    }

    public void setUsername(String u) {
        this.username = u;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setPassword(String p) {
        this.password = p;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setTitle(String l) {
        this.title = l;
    }
    
    public String getTitle() {
        return this.title;
    }
    
}
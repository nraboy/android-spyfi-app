package com.nraboy.spyfi;

import org.apache.http.message.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.utils.*;

public class MotionController implements Runnable {

    private boolean isActive;
    private String uri;
    private String username;
    private String password;
    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;
    private boolean isMovingLeft;
    private boolean isMovingRight;
    private boolean isMovingUp;
    private boolean isMovingDown;

    public MotionController(String uri, String username, String password) {
        this.uri = uri;
        this.username = username;
        this.password = password;
        this.isActive = true;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void moveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void moveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public void moveUp(boolean moveUp) {
        this.moveUp = moveUp;
    }

    public void moveDown(boolean moveDown) {
        this.moveDown = moveDown;
    }

    @Override
    public void run() {
        while(this.isActive) {
            if(this.moveUp && !this.isMovingUp) {
                this.send("0");
                this.isMovingUp = true;
            } else if(!this.moveUp && this.isMovingUp) {
                this.send("1");
                this.isMovingUp = false;
            }
            if(this.moveDown && !this.isMovingDown) {
                this.send("2");
                this.isMovingDown = true;
            } else if(!this.moveDown && this.isMovingDown) {
                this.send("3");
                this.isMovingDown = false;
            }
            if(this.moveLeft && !this.isMovingLeft) {
                this.send("6");
                this.isMovingLeft = true;
            } else if(!this.moveLeft && this.isMovingLeft) {
                this.send("7");
                this.isMovingLeft = false;
            }
            if(this.moveRight && !this.isMovingRight) {
                this.send("4");
                this.isMovingRight = true;
            } else if(!this.moveRight && this.isMovingRight) {
                this.send("5");
                this.isMovingRight = false;
            }
        }
    }

    private String send(String cmd) {
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(this.uri + "/decoder_control.cgi?user=" + this.username + "&pwd=" + this.password + "&command=" + cmd);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = httpclient.execute(httpget, responseHandler);
            result = response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean hasMotion() {
        return isMovingLeft || isMovingRight || isMovingUp || isMovingDown ? true : false;
    }

}
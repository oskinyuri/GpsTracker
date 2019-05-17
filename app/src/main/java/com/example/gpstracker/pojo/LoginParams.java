package com.example.gpstracker.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Awesome Pojo Generator
 */
public class LoginParams {
    @SerializedName("usename")
    @Expose
    private String username;
    @SerializedName("passwd")
    @Expose
    private String passwd;

    public LoginParams(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
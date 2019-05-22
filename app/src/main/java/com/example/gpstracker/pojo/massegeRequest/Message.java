package com.example.gpstracker.pojo.massegeRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Awesome Pojo Generator
 */
public class Message {
    @SerializedName("table_alias")
    @Expose
    private String table_alias;

    public Message() {
        this.table_alias = "t";
    }

    public void setTable_alias(String table_alias) {
        this.table_alias = table_alias;
    }

    public String getTable_alias() {
        return table_alias;
    }
}
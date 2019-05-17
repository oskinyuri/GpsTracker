package com.example.gpstracker.pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class Result{
  @SerializedName("usename")
  @Expose
  private String usename;
  public void setUsename(String usename){
   this.usename=usename;
  }
  public String getUsename(){
   return usename;
  }
}
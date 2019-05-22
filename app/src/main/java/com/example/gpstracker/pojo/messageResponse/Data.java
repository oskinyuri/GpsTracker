package com.example.gpstracker.pojo.messageResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class Data{
  @SerializedName("alarm")
  @Expose
  private String alarm;
  @SerializedName("lon")
  @Expose
  private Double lon;
  @SerializedName("ts_key")
  @Expose
  private String ts_key;
  @SerializedName("track")
  @Expose
  private String track;
  @SerializedName("message")
  @Expose
  private String message;
  @SerializedName("lat")
  @Expose
  private Double lat;
  public void setAlarm(String alarm){
   this.alarm=alarm;
  }
  public String getAlarm(){
   return alarm;
  }
  public void setLon(Double lon){
   this.lon=lon;
  }
  public Double getLon(){
   return lon;
  }
  public void setTs_key(String ts_key){
   this.ts_key=ts_key;
  }
  public String getTs_key(){
   return ts_key;
  }
  public void setTrack(String track){
   this.track=track;
  }
  public String getTrack(){
   return track;
  }
  public void setMessage(String message){
   this.message=message;
  }
  public String getMessage(){
   return message;
  }
  public void setLat(Double lat){
   this.lat=lat;
  }
  public Double getLat(){
   return lat;
  }
}
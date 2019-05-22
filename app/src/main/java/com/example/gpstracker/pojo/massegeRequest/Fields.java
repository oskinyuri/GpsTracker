package com.example.gpstracker.pojo.massegeRequest;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class Fields{
  @SerializedName("message")
  @Expose
  private Message message;

    public Fields() {
        message = new Message();
    }

    public void setMessage(Message message){
   this.message=message;
  }
  public Message getMessage(){
   return message;
  }
}
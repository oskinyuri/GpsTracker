package com.example.gpstracker.pojo.messageResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class Records{
  @SerializedName("count")
  @Expose
  private Integer count;
  public void setCount(Integer count){
   this.count=count;
  }
  public Integer getCount(){
   return count;
  }
}
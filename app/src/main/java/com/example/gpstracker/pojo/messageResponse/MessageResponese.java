package com.example.gpstracker.pojo.messageResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class MessageResponese{
  @SerializedName("result")
  @Expose
  private Result result;
  @SerializedName("usename")
  @Expose
  private String usename;
  @SerializedName("jsonrpc")
  @Expose
  private Double jsonrpc;
  @SerializedName("error")
  @Expose
  private Object error;
  public void setResult(Result result){
   this.result=result;
  }
  public Result getResult(){
   return result;
  }
  public void setUsename(String usename){
   this.usename=usename;
  }
  public String getUsename(){
   return usename;
  }
  public void setJsonrpc(Double jsonrpc){
   this.jsonrpc=jsonrpc;
  }
  public Double getJsonrpc(){
   return jsonrpc;
  }
  public void setError(Object error){
   this.error=error;
  }
  public Object getError(){
   return error;
  }
}
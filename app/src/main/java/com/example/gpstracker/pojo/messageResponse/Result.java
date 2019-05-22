package com.example.gpstracker.pojo.messageResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Awesome Pojo Generator
 * */
public class Result{
  @SerializedName("data")
  @Expose
  private List<Data> data;
  @SerializedName("offset")
  @Expose
  private Integer offset;
  @SerializedName("records")
  @Expose
  private List<Records> records;
  @SerializedName("sql")
  @Expose
  private String sql;
  public void setData(List<Data> data){
   this.data=data;
  }
  public List<Data> getData(){
   return data;
  }
  public void setOffset(Integer offset){
   this.offset=offset;
  }
  public Integer getOffset(){
   return offset;
  }
  public void setRecords(List<Records> records){
   this.records=records;
  }
  public List<Records> getRecords(){
   return records;
  }
  public void setSql(String sql){
   this.sql=sql;
  }
  public String getSql(){
   return sql;
  }
}
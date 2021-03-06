package com.example.gpstracker.datasource;

import com.example.gpstracker.pojo.authenticateResponse.AuthResp;
import com.example.gpstracker.pojo.messageResponse.MessageResponese;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GpsTrackerApi {

    @FormUrlEncoded
    @POST("request.php")
    Call<AuthResp> login(@Field("method") String authenticate, @Field("params") String params);


    @FormUrlEncoded
    @POST("request.php")
    Call<AuthResp> updateGps(@Field("method") String updateEntity, @Header("Cookie") String cookie, @Field("params") String params);

    @FormUrlEncoded
    @POST("request.php")
    Call<MessageResponese> updateMessage(@Field("method") String getTableDataPredicate, @Header("Cookie") String cookie, @Field("params") String params);

}

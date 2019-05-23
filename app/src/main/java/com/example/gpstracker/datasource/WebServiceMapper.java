package com.example.gpstracker.datasource;

import android.content.Context;
import android.util.Log;

import com.example.gpstracker.pojo.authenticateResponse.AuthResp;
import com.example.gpstracker.pojo.messageResponse.Data;
import com.example.gpstracker.pojo.messageResponse.MessageResponese;
import com.example.gpstracker.util.callbacks.AuthenticateCallback;
import com.example.gpstracker.util.callbacks.UpdateLocationCallback;
import com.example.gpstracker.util.callbacks.UpdateMessageCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceMapper {

    public final static String METHOD_UPDATE = "updateEntity";
    public final static String METHOD_AUTHENTICATE = "authenticate";
    public final static String METHOD_GET_TABLE_DATA_PREDICATE = "getTableDataPredicate";

    private SharedPrefManager mPrefManager;
    private GpsTrackerApi mGpsTrackerApi;
    private Context mContext;

    public WebServiceMapper(Context context) {
        mContext = context;
        mPrefManager = new SharedPrefManager(mContext);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://abris.site/asut/Server/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        mGpsTrackerApi = retrofit.create(GpsTrackerApi.class);
    }

    public void updateMessage(String params, UpdateMessageCallback callback) {
        mGpsTrackerApi.updateMessage(METHOD_GET_TABLE_DATA_PREDICATE, mPrefManager.getToken(), "[" + params + "]").enqueue(new Callback<MessageResponese>() {
            @Override
            public void onResponse(Call<MessageResponese> call, Response<MessageResponese> response) {
                List<Data> dataList = null;
                if (response.body() != null) {
                    dataList = response.body().getResult().getData();
                }
                assert dataList != null;
                for (Data data : dataList) {
                    callback.onResponse(data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<MessageResponese> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    public void updateGps(String params, UpdateLocationCallback callback) {
        mGpsTrackerApi.updateGps(METHOD_UPDATE, mPrefManager.getToken(), "[" + params + "]").enqueue(new Callback<AuthResp>() {
            @Override
            public void onResponse(Call<AuthResp> call, Response<AuthResp> response) {
                callback.onResponse();
            }

            @Override
            public void onFailure(Call<AuthResp> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void authenticate(String params, AuthenticateCallback callback) {
        mGpsTrackerApi.login(METHOD_AUTHENTICATE, "[" + params + "]").enqueue(new Callback<AuthResp>() {
            @Override
            public void onResponse(Call<AuthResp> call, Response<AuthResp> response) {
                if (response.body() == null) {
                    callback.onFailure(new Exception("Empty body!"));
                    return;
                }
                if (response.body().getError() == null) {
                    Headers headerList = response.headers();
                    List<String> values = headerList.values("Set-Cookie");
                    mPrefManager.saveToken(values.get(0));
                    callback.onResponse();
                } else {
                    callback.onFailure(new Exception(response.body().getError().toString()));
                }

            }

            @Override
            public void onFailure(Call<AuthResp> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}

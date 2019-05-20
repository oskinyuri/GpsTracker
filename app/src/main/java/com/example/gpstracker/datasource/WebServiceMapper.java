package com.example.gpstracker.datasource;

import android.content.Context;

import com.example.gpstracker.pojo.AuthResp;
import com.example.gpstracker.ui.AuthenticateCallback;
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

    private SharedPrefManager mPrefManager;
    private GpsTrackerApi mGpsTrackerApi;
    private Context mContext;

    public WebServiceMapper(Context context) {
        mContext = context;
        mPrefManager = new SharedPrefManager(mContext);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
// add your other interceptors …

// add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://abris.site/asut/Server/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        mGpsTrackerApi = retrofit.create(GpsTrackerApi.class);
    }


    public void updateGps(String params) {
        mGpsTrackerApi.updateGps(METHOD_UPDATE, mPrefManager.getToken(), "[" + params + "]").enqueue(new Callback<AuthResp>() {
            @Override
            public void onResponse(Call<AuthResp> call, Response<AuthResp> response) {

            }

            @Override
            public void onFailure(Call<AuthResp> call, Throwable t) {

            }
        });
    }

    public void authenticate(String params, AuthenticateCallback callback) {
        mGpsTrackerApi.login(METHOD_AUTHENTICATE, "[" + params + "]").enqueue(new Callback<AuthResp>() {
            @Override
            public void onResponse(Call<AuthResp> call, Response<AuthResp> response) {
                if (response.body() == null){
                    //TODO узнать почему с сервера может придти пустое и создать Exception
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

package com.colaapk.assistant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LLY on 2017/8/11.
 */

public class GetAsynHttpUtil {
    private static GetAsynHttpUtil mGetAsynHttpUtil;
    private OkHttpClient mOkHttpClient;

    public static GetAsynHttpUtil newGetAsynHttpUtil() {
        if (mGetAsynHttpUtil == null) {
            mGetAsynHttpUtil = new GetAsynHttpUtil();
        }
        return mGetAsynHttpUtil;
    }

    private GetAsynHttpUtil() {
        mOkHttpClient = new OkHttpClient();
    }

    public void get(String url, final GetHttpListener listener) {
        final Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                listener.onSuccess(json);
            }
        });

    }
}

interface GetHttpListener {
    void onError();

    void onSuccess(String json);
}
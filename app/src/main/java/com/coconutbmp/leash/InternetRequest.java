package com.coconutbmp.leash;

import android.app.Activity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InternetRequest {
    Request request;
    OkHttpClient client = new OkHttpClient();

    public void doRequest(String url, Activity activity, JSONObject parameters, com.coconutbmp.leash.RequestHandler requestHandler){
        HttpUrl.Builder test = HttpUrl.parse(url).newBuilder();
        List<String> keys = new ArrayList<>();
        Iterator<String> it = parameters.keys();
        it.forEachRemaining(e->keys.add(e));
        for (String key: keys){
            try {
                test.addQueryParameter(key, parameters.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        request = new Request.Builder().url(test.build()).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestHandler.processResponse(responseData);
                    }
                });
            }
        });
    }
}

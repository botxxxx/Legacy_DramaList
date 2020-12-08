package com.example.mydramalist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.mydramalist.MainActivity.dramaData;
import static com.example.mydramalist.MainActivity.updateUI;

public class JSONAsyncTask extends AsyncTask<String, Void, JSONObject[]> {
    @SuppressLint("StaticFieldLeak")
    Context context;

    JSONAsyncTask(Context context) {
        this.context = context;
    }

    protected JSONObject[] doInBackground(String... urls) {
        JSONObject[] json = new JSONObject[1];
        json[0] = new JSONParser().getJSONFromUrl(urls[0]);

        return json;
    }

    protected void onPostExecute(JSONObject[] json) {
        try {
            if (json[0] != null) {
                dramaData.clear();
                JSONArray items = json[0].getJSONArray("data");
                for (int i = 0; i < items.length(); i++) {
                    dramaData.add(new DramaData(items.getJSONObject(i)));
                    Log.e(MainActivity.TAG, "JSONArray:" + items.getJSONObject(i).getString("name"));
                }
                updateUI(context);
            }
        } catch (Exception ignored) {
        }
    }
}

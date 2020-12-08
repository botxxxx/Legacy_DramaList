package com.example.mydramalist;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

public class DramaData {
    String drama_id;
    String name;
    String total_views;
    String created_at;
    String thumb;
    String rating;

    DramaData(Cursor cursor) {
        this.drama_id = cursor.getString(0);
        this.name = cursor.getString(1);
        this.total_views = cursor.getString(2);
        this.created_at = cursor.getString(3);
        this.thumb = cursor.getString(4);
        this.rating = cursor.getString(5);
    }


    DramaData(JSONObject jsonObject) {
        try {
            this.drama_id = jsonObject.getString("drama_id");
            this.name = jsonObject.getString("name");
            this.total_views = jsonObject.getString("total_views");
            this.created_at = jsonObject.getString("created_at");
            this.thumb = jsonObject.getString("thumb");
            this.rating = jsonObject.getString("rating");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    *
    * DramaData(Cursor cursor) {
        this.drama_id = cursor.getInt(0);
        this.name = cursor.getString(1);
        this.total_views = cursor.getInt(2);
        this.created_at = Date.from(Instant.parse(cursor.getString(3)));
        this.thumb = cursor.getString(4);
        this.rating = cursor.getDouble(5);
    }

    DramaData(JSONObject jsonObject) {
        try {
            this.drama_id = jsonObject.getInt("drama_id");
            this.name = jsonObject.getString("name");
            this.total_views = jsonObject.getInt("total_views");
            this.created_at = Date.from(Instant.parse(jsonObject.getString("created_at")));
            this.thumb = jsonObject.getString("thumb");
            this.rating = jsonObject.getDouble("rating");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    * */
}

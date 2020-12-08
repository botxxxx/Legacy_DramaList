package com.example.mydramalist;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DramaData {
    String drama_id;
    String name;
    String total_views;
    String created_at;
    String thumb;
    String rating;
    byte[] image;

    DramaData(Cursor cursor) {
        try {
            this.drama_id = cursor.getString(0);
            this.name = cursor.getString(1);
            this.total_views = cursor.getString(2);
            this.created_at = cursor.getString(3);
            this.thumb = cursor.getString(4);
            this.rating = cursor.getString(5);
            //this.image = cursor.getBlob(6);
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "DramaData cursor:" + e.toString());

        }
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
            Log.e(MainActivity.TAG, "DramaData jsonObject:" + e.toString());
        }
    }

    public void setImage(ImageView image) {
        try {
            if (image != null) {
                Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                this.image = stream.toByteArray();
            }
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "DramaData setImage:" + e.toString());
        }
    }

//    public void setThumbImage(){
//        try {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//        this.image = bos.toByteArray();
//            bos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


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

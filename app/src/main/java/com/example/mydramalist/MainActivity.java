package com.example.mydramalist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.mydramalist.SQLOpenHelper.CREATED_AT;
import static com.example.mydramalist.SQLOpenHelper.ID;
import static com.example.mydramalist.SQLOpenHelper.NAME;
import static com.example.mydramalist.SQLOpenHelper.RATING;
import static com.example.mydramalist.SQLOpenHelper.TABLE;
import static com.example.mydramalist.SQLOpenHelper.THUMB;
import static com.example.mydramalist.SQLOpenHelper.TOTAL_VIEWS;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends Activity {
    public static final String TAG = "MDL";
    public static DateTimeFormatter formatter;
    public static ArrayList<DramaData> dramaData;
    public static ListView listView;
    private static final String URI = "https://static.linetv.tw/interview/dramas-sample.json";
    private static SQLOpenHelper db_helper;
    private static SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermission()) {
            showPermission();
        } else {
            init();
            checkDB();
        }
    }

    private void init() {
        dramaData = new ArrayList<>();
        listView = findViewById(R.id.drama_list);
        formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.TAIWAN)
                .withZone(ZoneId.systemDefault());
        if (checkInternet())
            new JSONAsyncTask(this).execute(URI);

//        dramaData.add(new DramaData(3, "如果有妹妹就好了", 2931180, "2017-10-21T12:34:41.000Z", "https://i.pinimg.com/originals/32/c1/7a/32c17af1c085be75657e965954f8d601.jpg", 4.0647));
//        updateUI();
    }

    public static void updateUI(Context context) {
        listView.setAdapter(new DramaListAdapter());
        if (dramaData != null)
            for (DramaData drama : dramaData)
                insertData(context, drama);
    }

    private void showPermission() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.INTERNET);
        requestPermissions(permissions.toArray(new String[0]), 0);
    }

    private boolean checkPermission() {
        int STORAGE = checkSelfPermission(Manifest.permission.INTERNET);
        return STORAGE != PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkInternet() {
        boolean Internet = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                assert networkInfo != null;
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    Internet = true;
                }
            }
        } catch (Exception ignored) {
        }
        if (!Internet) Toast.makeText(this, "No internet.", Toast.LENGTH_SHORT).show();
        findViewById(R.id.error_img).setVisibility(Internet ? View.GONE : View.VISIBLE);
        return Internet;
    }

    private void checkDB() {
        if (db_helper == null) {
            db_helper = new SQLOpenHelper(getApplicationContext());
        }

        if (db == null) {
            db = db_helper.getReadableDatabase();
        }

        Cursor cursor = db.query(
                TABLE,
                new String[]{ID, NAME, TOTAL_VIEWS, CREATED_AT, THUMB, RATING},
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        try{
            for(cursor.moveToFirst(); !cursor.isAfterLast();cursor.moveToNext()) {
                dramaData.add(new DramaData(cursor));
                if (checkInternet()) {
//                dramaData.get(i).setBitmap(this, true);
//                    Log.e(TAG, "download:" + dramaData.get(i).thumb);
                }
            }
        }catch (Exception e){

        }

        cursor.close();
        updateUI(this);
    }

    private static void insertData(Context context, DramaData dramaData) {
        if (db_helper == null) {
            db_helper = new SQLOpenHelper(context);
        }

        if (db == null) {
            db = db_helper.getReadableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put(ID, dramaData.drama_id);
        values.put(NAME, dramaData.name);
        values.put(TOTAL_VIEWS, dramaData.total_views);
        values.put(CREATED_AT, dramaData.created_at);
        values.put(THUMB, dramaData.thumb);
        values.put(RATING, dramaData.rating);

        db.insert(TABLE, null, values);
    }
}

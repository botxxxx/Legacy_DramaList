package com.example.mydramalist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.mydramalist.SQLOpenHelper.CREATED_AT;
import static com.example.mydramalist.SQLOpenHelper.ID;
import static com.example.mydramalist.SQLOpenHelper.IMAGE;
import static com.example.mydramalist.SQLOpenHelper.NAME;
import static com.example.mydramalist.SQLOpenHelper.RATING;
import static com.example.mydramalist.SQLOpenHelper.TABLE;
import static com.example.mydramalist.SQLOpenHelper.THUMB;
import static com.example.mydramalist.SQLOpenHelper.TOTAL_VIEWS;
import static com.example.mydramalist.SQLOpenHelper2.DATA2;
import static com.example.mydramalist.SQLOpenHelper2.TABLE2;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends Activity {
    public static final String TAG = "MDL";
    public static DateTimeFormatter formatter;
    public static ArrayList<DramaData> dramaData;
    public static ListView listView;
    private static final String URI = "https://static.linetv.tw/interview/dramas-sample.json";
    private static SQLOpenHelper db_helper;
    private static SQLOpenHelper2 db_helper2;
    private static SQLiteDatabase db, db2;
    private EditText editText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermission()) {
            showPermission();
        } else {
            init();
            checkDB(null);
            if (checkInternet())
                new JSONAsyncTask(this).execute(URI);
            else
                findViewById(R.id.search_oops).setVisibility(View.VISIBLE);
            checkDB2();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        dramaData = new ArrayList<>();
        listView = findViewById(R.id.drama_list);
        formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.TAIWAN)
                .withZone(ZoneId.systemDefault());
        findViewById(R.id.error_img).setOnClickListener((View v) -> {
            if (checkInternet()) {
                new JSONAsyncTask(this).execute(URI);
                editText.setText("");
                dramaData.clear();
                checkDB(null);
                findViewById(R.id.search_btn).setVisibility(View.GONE);
            } else
                findViewById(R.id.search_oops).setVisibility(View.VISIBLE);
        });
        editText = ((EditText) findViewById(R.id.search_edit));
        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().length() > 0) {
                    dramaData.clear();
                    checkDB(editText.getText().toString());
                    findViewById(R.id.search_btn).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.search_btn).setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.search_btn).setVisibility(View.GONE);
        findViewById(R.id.search_btn).setOnClickListener((View v) -> {
            editText.setText("");
            dramaData.clear();
            checkDB(null);
            findViewById(R.id.search_btn).setVisibility(View.GONE);
        });
    }

    public static void updateUI(Context context, boolean insert) {
        listView.setAdapter(new DramaListAdapter());
        if (insert) {
            db_helper.onUpgrade(db, db.getVersion(), db.getVersion() + 1);
            for (int i = 0; i < dramaData.size(); i++) {
                insertData(context, dramaData.get(i));
            }
        }
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

    private void checkDB2() {
        if (db_helper2 == null) {
            db_helper2 = new SQLOpenHelper2(getApplicationContext());
        }

        if (db2 == null) {
            db2 = db_helper2.getReadableDatabase();
        }
        try {
            Cursor cursor = db2.query(TABLE2,
                    new String[]{DATA2},
                    null,
                    null,
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();
            for (int i = 0;i<cursor.getCount();i++) {
                Log.e(MainActivity.TAG, "cursor:" + cursor.getString(i));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "cursor:" + e.toString());
        }
        updateUI(this, false);
    }

    private void checkDB(String select) {
        insertTempData(this, select);
        if (db_helper == null) {
            db_helper = new SQLOpenHelper(getApplicationContext());
        }
        if (db == null) {
            db = db_helper.getReadableDatabase();
        }
        findViewById(R.id.search_oops).setVisibility(View.GONE);
        if (select != null) {
            select = NAME + " like " + "'%" + select + "%' OR " +
                    NAME + " like '" + select + "%' OR " +
                    NAME + " like '%" + select + "' OR " +
                    NAME + " like '" + select + "'";
        }
        Cursor cursor = db.query(TABLE,
                new String[]{ID, NAME, TOTAL_VIEWS, CREATED_AT, THUMB, RATING},
                select,
                null,
                null,
                null,
                null
        );
        try {
            if (cursor.getCount() == 0)
                findViewById(R.id.search_oops).setVisibility(View.VISIBLE);
            cursor.moveToFirst();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                dramaData.add(new DramaData(cursor));
                Log.e(MainActivity.TAG, "cursor:" + cursor.getString(2));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "cursor:" + e.toString());
        }
        updateUI(this, false);
    }

    public static void insertData(Context context, DramaData dramaData) {
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
        if (dramaData.image != null)
            values.put(IMAGE, dramaData.image);

        db.insert(TABLE, null, values);
    }

    public static void insertTempData(Context context, String edit_text) {
        if (db_helper2 == null) {
            db_helper2 = new SQLOpenHelper2(context);
        }

        if (db2 == null) {
            db2 = db_helper2.getReadableDatabase();
        }
        db_helper2.onUpgrade(db2 ,db2.getVersion(), db2.getVersion() + 1);
        ContentValues values = new ContentValues();
        if (edit_text != null)
            values.put(DATA2, edit_text);
        else
            values.put(DATA2, "");
        Log.e(MainActivity.TAG, "insertTempData:" + edit_text);
        db2.insert(TABLE2, null, values);
    }
}

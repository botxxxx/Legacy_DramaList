package com.example.mydramalist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLOpenHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DB_VERSION = 1;

    // データーベース名
    public static final String DB = "my_drama.db";
    public static final String TABLE = "my_drama_list";
    public static final String ID = "drama_id";
    public static final String NAME = "name";
    public static final String TOTAL_VIEWS = "total_views";
    public static final String CREATED_AT = "created_at";
    public static final String THUMB = "thumb";
    public static final String RATING = "rating";
    public static final String IMAGE = "image";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY," +
                    NAME + " TEXT," +
                    TOTAL_VIEWS + " INTEGER," +
                    CREATED_AT + " DATE," +
                    THUMB + " TEXT," +
                    RATING + " REAL," +
                    IMAGE + " BLOB)";

    private static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE;


    SQLOpenHelper(Context context) {
        super(context, DB, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
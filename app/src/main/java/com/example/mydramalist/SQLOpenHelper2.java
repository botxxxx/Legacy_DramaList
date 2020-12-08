package com.example.mydramalist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLOpenHelper2 extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DB_VERSION = 1;

    // データーベース名
    public static final String DB2 = "temp.db";
    public static final String TABLE2 = "temps";
    public static final String DATA2 = "data";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE2 + " (" +
                    DATA2 + " TEXT)";

    private static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE2;


    SQLOpenHelper2(Context context) {
        super(context, DB2, null, DB_VERSION);
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
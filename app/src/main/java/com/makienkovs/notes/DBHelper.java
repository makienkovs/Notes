package com.makienkovs.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_SQLITE = "data.sqlite";
    public static final int DB_VERSION = 1;
    public static final String MYTABLE = "MYTABLE";
    public static final String CONTENT = "CONTENT";
    public static final String TIME = "TIME";
    public static final String DONE = "DONE";
    public static final String CANCEL = "CANCEL";

    public DBHelper(Context context) {
        super(context, DB_SQLITE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MYTABLE (ID INTEGER PRIMARY KEY AUTOINCREMENT, CONTENT TEXT, TIME TEXT, DONE INTEGER, CANCEL INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

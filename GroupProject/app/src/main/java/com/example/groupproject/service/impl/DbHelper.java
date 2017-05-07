package com.example.groupproject.service.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hasit on 5/4/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, "zones.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.ZoneTable.Columns.ID + "(" + " _id integer primary key autoincrement, " +
                DbSchema.ZoneTable.Columns.ZONE_NAME + ", " +
                DbSchema.ZoneTable.Columns.HOURS + ", " +
                DbSchema.ZoneTable.Columns.ZONE_TYPE + ", " +
                DbSchema.ZoneTable.Columns.START_LAT + ", " +
                DbSchema.ZoneTable.Columns.START_LONG + ", " +
                DbSchema.ZoneTable.Columns.END_LAT + ", " +
                DbSchema.ZoneTable.Columns.END_LONG +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

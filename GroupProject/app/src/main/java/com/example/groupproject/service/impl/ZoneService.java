package com.example.groupproject.service.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.example.groupproject.Model.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasit on 5/4/2017.
 */

public class ZoneService {

    private SQLiteDatabase db;

    public ZoneService(Context c){
        db = new DbHelper(c).getWritableDatabase();
    }

    private static ContentValues getContentValues(Zone zone){
        ContentValues contentValues = new ContentValues();

        contentValues.put (DbSchema.ZoneTable.Columns.ID, zone.getId());
        contentValues.put (DbSchema.ZoneTable.Columns.ZONE_NAME, zone.getName());
        contentValues.put (DbSchema.ZoneTable.Columns.HOURS, zone.getHours());
        contentValues.put (DbSchema.ZoneTable.Columns.ZONE_TYPE, zone.getZoneType());
        contentValues.put (DbSchema.ZoneTable.Columns.START_LAT, zone.getStart_lat());
        contentValues.put (DbSchema.ZoneTable.Columns.START_LONG, zone.getStart_long());
        contentValues.put (DbSchema.ZoneTable.Columns.END_LAT, zone.getEnd_lat());
        contentValues.put (DbSchema.ZoneTable.Columns.END_LONG, zone.getEnd_long());

        return contentValues;

    }

    public void addZone(Zone zone){
        ContentValues c = getContentValues(zone);
        db.insert(DbSchema.ZoneTable.NAME,null,c);
    }

    public void removeZone(Zone zone){
        db.delete(DbSchema.ZoneTable.NAME, "ID=?", new String[]{Integer.toString(zone.getId())});
    }

    public List<Zone> getZones(){
        Cursor c = db.query(DbSchema.ZoneTable.NAME, null,null,null,null, null,null);
        List<Zone> zones = new ArrayList<>();
        StoryCursorWrapper wrapper = new StoryCursorWrapper(c);

        try{
            wrapper.moveToFirst();
            while(!c.isAfterLast()){
                zones.add(wrapper.getZone());
                wrapper.moveToNext();
            }
        }finally {
            wrapper.close();
        }
        return zones;
    }

    private class StoryCursorWrapper extends CursorWrapper {

        StoryCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Zone getZone() {
            int id = getInt(getColumnIndex(DbSchema.ZoneTable.Columns.ID));
            String zone_name = getString(getColumnIndex(DbSchema.ZoneTable.Columns.ZONE_NAME));
            int hours = getInt(getColumnIndex(DbSchema.ZoneTable.Columns.HOURS));
            int zone_type = getInt(getColumnIndex(DbSchema.ZoneTable.Columns.ZONE_TYPE));
            double start_lat = getDouble(getColumnIndex(DbSchema.ZoneTable.Columns.START_LAT));
            double start_long = getDouble(getColumnIndex(DbSchema.ZoneTable.Columns.START_LONG));
            double end_lat = getDouble(getColumnIndex(DbSchema.ZoneTable.Columns.END_LAT));
            double end_long = getDouble(getColumnIndex(DbSchema.ZoneTable.Columns.END_LONG));

            Zone zone = new Zone(zone_name,hours,zone_type,start_lat,start_long,end_lat,end_long,id);
            return zone;
        }
    }


}

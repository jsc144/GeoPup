package com.example.groupproject;

import android.content.Context;

import com.example.groupproject.service.impl.ZoneService;

/**
 * Created by hasit on 5/4/2017.
 */

public class ServiceManager {
    private static ZoneService zoneService;

    public static ZoneService getZoneService(Context c){
        if(zoneService == null){
            zoneService = new ZoneService(c);
        }
        return zoneService;
    }
}

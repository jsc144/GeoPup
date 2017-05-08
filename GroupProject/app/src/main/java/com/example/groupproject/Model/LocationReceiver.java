package com.example.groupproject.Model;

/**
 * Created by Joe on 5/1/2017.
 */

/***
 Copyright (c) 2010 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.commonsware.cwac.locpoll.LocationPollerResult;
import com.example.groupproject.service.impl.ZoneService;

import java.util.List;

public class LocationReceiver extends BroadcastReceiver {
    private Context _context;
    private long points = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("recieved","received!");
        _context = context;
        Bundle b = intent.getExtras();
        LocationPollerResult locationResult = new LocationPollerResult(b);

        Location loc=locationResult.getLocation();
        String msg;
        if (loc==null) {
            loc=locationResult.getLastKnownLocation();

            if (loc==null) {
                msg=locationResult.getError();
            }
            else {
                msg="TIMEOUT, lastKnown="+loc.toString();
            }
        }
        else {
            List<Zone> zones = getZones();
            countPoints(zones, loc.getLongitude(), loc.getLatitude());
            msg=loc.toString();
        }

        if (msg==null) {
            msg="Invalid broadcast received!";
        }
        Log.d("MSG", msg +" "+ points);
    }
    private void countPoints(List<Zone> zones, double longitude, double latitude){
        for (Zone zone:zones) {
            if(inZone(zone,longitude,latitude))
            {
                if(zone.getZoneType() != 0)
                    addPoints(-1);
                else
                    addPoints(1);
            }
            Log.d("Points", Long.toString(points));
        }



    }
    private void addPoints(long pointValue){
        //TODO:Make persistent
        android.content.SharedPreferences pm = _context.getSharedPreferences("GeoCat",0);
        points = pm.getLong("points",0);
        points = points + pointValue;
        android.content.SharedPreferences.Editor editor = pm.edit();
        editor.putLong("points",points);
        editor.commit();
    }
    private List<Zone> getZones(){
        ZoneService zs = new ZoneService(_context);
        return zs.getZones();
    /**    ArrayList<Zone> zones = new ArrayList<>();
        Zone z1 = new Zone("test",1,0,0,0,100,100,1);
        zones.add(z1);
        return  zones;*/
    }
    private boolean inZone(Zone zone, double longitude, double latitude){
        if(latitude >= zone.getStart_lat() && latitude < zone.getEnd_lat()){
            if(longitude >= zone.getStart_long() && longitude < zone.getEnd_long()){
                return true;
            }
        }
        return false;
    }

}
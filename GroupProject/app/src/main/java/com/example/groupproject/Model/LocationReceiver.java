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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.commonsware.cwac.locpoll.LocationPollerResult;
import com.example.groupproject.MainActivity;
import com.example.groupproject.R;
import com.example.groupproject.service.impl.ZoneService;

import java.util.List;
import java.util.Random;

import static com.example.groupproject.Model.Constants.HEALTH_INCREMENT;
import static com.example.groupproject.Model.Constants.HUNGER_INCREMENT;
import static com.example.groupproject.Model.Constants.JOY_INCREMENT;
import static com.example.groupproject.Model.Constants.MAX_HEALTH;
import static com.example.groupproject.Model.Constants.MAX_HUNGER;
import static com.example.groupproject.Model.Constants.MAX_JOY;
import static com.example.groupproject.Model.Constants.POINT_INCREMENT;

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
        //sendNotification("GeoDog","counting...");
        android.content.SharedPreferences pm = _context.getSharedPreferences("GeoCat",0);
        String name = pm.getString("name","Your Geo-Dog");
        long hunger = pm.getLong("hunger",MAX_HUNGER);
        if(hunger <= 0){
            if(hunger == 0){
                sendNotification("GeoDog",
                       name + " is starving and its health is deteriorating. Buy some food!");
                addHunger(-1);
            }
            long health = pm.getLong("health",MAX_HEALTH);
            if(health == 0){
                 String message = name +
                         " was taken away by a concerned vet. It's okay though, pick yourself up and try again!";
                sendNotification("GeoDog",message);
                resetStats();
            }else{
                addHealth(HEALTH_INCREMENT);
            }
        }else{
            addHunger(HUNGER_INCREMENT);
        }
        for (Zone zone:zones) {
            if(inZone(zone,longitude,latitude))
            {
                if(zone.getZoneType() != 0) {
                    addJoy(JOY_INCREMENT);
                    long joy = pm.getLong("joy",MAX_JOY);
                    if(joy <= 0){
                        if(joy == 0) {
                            sendNotification("GeoDog",
                                    "Oh no! "+name
                                            + "is completely pooped! buy a treat to cheer it up!");
                            addJoy(-1);
                        }//10% chance to reset statistics
                        Random r = new Random();
                        if(r.nextInt(100-0) >= 90){
                            sendNotification("GeoDog", "Dude, "+name + " just ran away!");
                            resetStats();
                        }
                    }
                }
                else {
                    addPoints(POINT_INCREMENT);
                }
            }
            Log.d("Points", Long.toString(points));
        }
    }
    private void resetStats(){
        android.content.SharedPreferences pm = _context.getSharedPreferences("GeoCat",0);
        android.content.SharedPreferences.Editor editor = pm.edit();
        editor.putLong("health", MAX_HEALTH);
        editor.putLong("hunger", MAX_HUNGER);
        editor.putLong("joy", MAX_JOY);
        editor.putBoolean("zerohealth",true);
        editor.commit();
        //TODO:reset name
    }
    private void addPoints(long pointValue){
        //TODO:Make persistent
        android.content.SharedPreferences pm = _context.getSharedPreferences("GeoCat",0);
        points = pm.getLong("points",100);
        points = points + pointValue;
        android.content.SharedPreferences.Editor editor = pm.edit();
        editor.putLong("points",points);
        editor.commit();
    }
    private void addHunger(long hungerValue){
        SharedPreferences pm = _context.getSharedPreferences("GeoCat",0);
        long hunger = pm.getLong("hunger",MAX_HUNGER);
        hunger = hunger + hungerValue;
        SharedPreferences.Editor editor = pm.edit();
        editor.putLong("hunger",hunger);
        editor.commit();
    }
    private void addJoy(long joyValue){
        SharedPreferences pm = _context.getSharedPreferences("GeoCat",0);
        long joy = pm.getLong("joy",MAX_JOY);
        joy += joyValue;
        SharedPreferences.Editor editor = pm.edit();
        editor.putLong("joy",joy);
        editor.commit();
    }
    private void addHealth(long healthValue){
        SharedPreferences pm = _context.getSharedPreferences("GeoCat",0);
        long health= pm.getLong("health",MAX_HEALTH);
        health += healthValue;
        SharedPreferences.Editor editor = pm.edit();
        editor.putLong("health",health);
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
    private void sendNotification(String title, String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(_context)
                .setSmallIcon(R.mipmap.ic_launcher_2).setContentTitle(title).setContentText(message);

        Intent resultIntent = new Intent(_context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(_context,0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mgr =
                (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.notify(0,mBuilder.build());
    }
}
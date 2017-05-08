package com.example.groupproject.Model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;

import static android.content.Context.ALARM_SERVICE;
import static com.example.groupproject.Model.Constants.PERIOD;

/**
 * Created by Joe on 5/8/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    private PendingIntent pi=null;
    private AlarmManager mgr=null;
    private Context _context;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Boot","boot recieved");
        _context = context;
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences("GeoCat",0);
            if(prefs.getBoolean("started",false)){
                startService();
            }
        }

    }
    private void startService(){
        mgr=(AlarmManager)_context.getSystemService(ALARM_SERVICE);

        Intent i=new Intent(_context, LocationPoller.class);
        Intent i2 = new Intent(_context, LocationReceiver.class);

        Bundle bundle = new Bundle();
        LocationPollerParameter parameter = new LocationPollerParameter(bundle);
        parameter.setIntentToBroadcastOnCompletion(i2);
        // try GPS and fall back to NETWORK_PROVIDER
        parameter.setProviders(new String[] {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER});
        parameter.setTimeout(60000);
        i.putExtras(bundle);


        pi= PendingIntent.getBroadcast(_context, 0, i, 0);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), PERIOD,  pi);

        SharedPreferences prefs = _context.getSharedPreferences("GeoCat",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("started",true);
        editor.commit();
    }
    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = _context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}

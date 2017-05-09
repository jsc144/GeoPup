package com.example.groupproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;
import com.example.groupproject.Model.LocationReceiver;
import com.example.groupproject.Model.Zone;
import com.example.groupproject.service.impl.ZoneService;
import com.google.android.gms.maps.model.LatLng;

import static com.example.groupproject.Model.Constants.MY_PERMISSIONS_REQUEST;
import static com.example.groupproject.Model.Constants.PERIOD;

public class AddZoneActivity extends AppCompatActivity {

    private Button gozone_button, nogozone_button, chooseZone_button, cancel_button, submit_button;
    private LinearLayout ll;
    private TextView hours_textview;
    private EditText name_edittext, hours_edittext;
    private boolean gotMapResult = false;
    private LatLng firstPoint = null, lastPoint = null;
    private ZoneService serv;

    private static int zone_type = 0; //0 for go, 1 for no go
    static final String MAP_INTENT_COLOR = "MAP_INTENT_COLOR";
    static final int MAP_INTENT_REQUEST_CODE = 3;
    private PendingIntent pi=null;
    private AlarmManager mgr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_zone);

        serv = ServiceManager.getZoneService(getApplicationContext());

        ll = (LinearLayout)findViewById(R.id.ll);
        gozone_button = (Button) findViewById(R.id.gozone_button);
        nogozone_button = (Button) findViewById(R.id.nogozone_button);
        chooseZone_button = (Button) findViewById(R.id.chooseZone_Button);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        submit_button = (Button) findViewById(R.id.submit_button);

        name_edittext = (EditText) findViewById(R.id.name_edittext);
        hours_edittext = (EditText) findViewById(R.id.hours_edittext);

        hours_textview = (TextView) findViewById(R.id.hours_textview);


        gozone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionBar ab = getSupportActionBar();
                ll.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.goZone_color));
                gozone_button.setBackgroundResource(R.drawable.zone_shape_selected);
                nogozone_button.setBackgroundResource(R.drawable.zone_shape_unselected);
                ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.goZone_color)));

                zone_type = 0;

            }
        });

        nogozone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionBar ab = getSupportActionBar();
                ll.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.nogoZone_color));
                nogozone_button.setBackgroundResource(R.drawable.zone_shape_selected);
                gozone_button.setBackgroundResource(R.drawable.zone_shape_unselected);
                ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.nogoZone_color)));

                zone_type = 1;
            }
        });

        chooseZone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
                if(zone_type == 0){
                    mapIntent.putExtra(MAP_INTENT_COLOR, 0);
                }else{
                    mapIntent.putExtra(MAP_INTENT_COLOR, 1);
                }

                startActivityForResult(mapIntent,MAP_INTENT_REQUEST_CODE);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                /*askLocationPermission();
                SharedPreferences prefs = getSharedPreferences("GeoCat",0);
                long points = prefs.getLong("points",0);
                Log.d("init points", Long.toString(points));
           */
                finish();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gotMapResult){
                    String name = name_edittext.getText().toString();

                    if(name.equals("")){
                        Toast.makeText(getApplicationContext(),"Enter a name for the Zone", Toast.LENGTH_SHORT).show();
                    }else{
                        Zone z = new Zone(name,zone_type,firstPoint.latitude, firstPoint.longitude,
                                    lastPoint.latitude, lastPoint.longitude);
                        serv.addZone(z);
                        //now, check if the alarm manager was scheduled
                        SharedPreferences prefs = getSharedPreferences("GeoCat",0);
                        if(!prefs.getBoolean("started",false)){
                            askLocationPermission();
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Choose a zone on the map", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == MAP_INTENT_REQUEST_CODE){
            Bundle b = data.getExtras();
            firstPoint = (LatLng)b.get(MapsActivity.FIRST_POINT);
            lastPoint = (LatLng)b.get(MapsActivity.LAST_POINT);
            gotMapResult = true;
        }else{
            if(firstPoint == null || lastPoint == null){
                gotMapResult = false;
            }
        }
    }
    private void startService(){
        mgr=(AlarmManager)getSystemService(ALARM_SERVICE);

        Intent i=new Intent(this, LocationPoller.class);
        Intent i2 = new Intent(this, LocationReceiver.class);

        Bundle bundle = new Bundle();
        LocationPollerParameter parameter = new LocationPollerParameter(bundle);
        parameter.setIntentToBroadcastOnCompletion(i2);
        // try GPS and fall back to NETWORK_PROVIDER
        parameter.setProviders(new String[] {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER});
        parameter.setTimeout(60000);
        i.putExtras(bundle);


        pi= PendingIntent.getBroadcast(this, 0, i, 0);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), PERIOD,  pi);

        SharedPreferences prefs = getSharedPreferences("GeoCat",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("started",true);
        editor.commit();
        Toast.makeText(this, getFilesDir().getPath(),
                Toast.LENGTH_LONG).show();
    }
    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    private void askLocationPermission(){
        if(!checkLocationPermission()){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            if(checkLocationPermission()){
                startService();
            }
        }
        else {
            startService();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startService();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void stopService(View v) {
        mgr.cancel(pi);
        finish();
    }
}

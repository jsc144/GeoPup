package com.example.groupproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_ZONE = 0;
    private static final int PERIOD=1800000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.main_activity_color)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add_zone:
                Intent x = new Intent(getApplicationContext(), AddZoneActivity.class);
                startActivityForResult(x, REQUEST_CODE_ADD_ZONE);
                return true;
            case R.id.menu_item_zone_list:
                Intent y = new Intent(getApplicationContext(), ZoneListActivity.class);
                startActivity(y);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(),"New zone was added", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Zone was not added", Toast.LENGTH_SHORT).show();
        }
    }
    private void startService(){
        AlarmManager mgr=(AlarmManager)getSystemService(ALARM_SERVICE);

        Intent i=new Intent(this, LocationPoller.class);

        Bundle bundle = new Bundle();
        LocationPollerParameter parameter = new LocationPollerParameter(bundle);
        parameter.setIntentToBroadcastOnCompletion(new Intent(this, MainActivity.class));
        // try GPS and fall back to NETWORK_PROVIDER
        parameter.setProviders(new String[] {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER});
        parameter.setTimeout(60000);
        i.putExtras(bundle);


        PendingIntent pi= PendingIntent.getBroadcast(this, 0, i, 0);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                PERIOD,
                pi);

        Toast
                .makeText(this,
                        "Location polling every 30 minutes begun",
                        Toast.LENGTH_LONG)
                .show();
    }
}

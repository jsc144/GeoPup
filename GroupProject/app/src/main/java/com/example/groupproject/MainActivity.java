package com.example.groupproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.groupproject.Model.Zone;
import com.example.groupproject.service.impl.ZoneService;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_ZONE = 0;
    private PendingIntent pi=null;
    private AlarmManager mgr=null;
    private ZoneService serv;
    private static int points = 100;
    private Button food, milk, treat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        serv = ServiceManager.getZoneService(getApplicationContext());

        List<Zone> zones = serv.getZones();

        if(zones.size()==0){
            setContentView(R.layout.activity_empty_main);
        }else{
            setContentView(R.layout.activity_main);

            food = (Button) findViewById(R.id.food_btn);
            milk = (Button) findViewById(R.id.milk_btn);
            treat = (Button) findViewById(R.id.treat_btn);

            food.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            milk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            treat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

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

        List<Zone> zones = serv.getZones();

        if(zones.size()==0){
            setContentView(R.layout.activity_empty_main);
        }else{
            setContentView(R.layout.activity_main);
        }
    }
}

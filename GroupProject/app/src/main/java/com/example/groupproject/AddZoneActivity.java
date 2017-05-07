package com.example.groupproject;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.Model.Zone;
import com.example.groupproject.service.impl.ZoneService;
import com.google.android.gms.maps.model.LatLng;

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

                hours_textview.setVisibility(View.VISIBLE);
                hours_edittext.setVisibility(View.VISIBLE);

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

                hours_textview.setVisibility(View.INVISIBLE);
                hours_edittext.setVisibility(View.INVISIBLE);

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
                finish();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gotMapResult){
                    String name = name_edittext.getText().toString();
                    String hours = hours_edittext.getText().toString();

                    if(name.equals("")){
                        Toast.makeText(getApplicationContext(),"Enter a name for the Zone", Toast.LENGTH_SHORT).show();
                    }else{
                        int hours_int;
                        Zone z;
                        if(hours.equals("")){
                            z = new Zone(name,0,zone_type,firstPoint.latitude, firstPoint.longitude,
                                    lastPoint.latitude, lastPoint.longitude,0);
                        }else{
                            hours_int = Integer.parseInt(hours);
                            z = new Zone(name,hours_int,zone_type,firstPoint.latitude, firstPoint.longitude,
                                    lastPoint.latitude, lastPoint.longitude,0);
                        }
                        serv.addZone(z);
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
}

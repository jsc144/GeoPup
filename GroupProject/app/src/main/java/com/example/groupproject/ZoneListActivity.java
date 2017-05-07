package com.example.groupproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.groupproject.Model.Zone;
import com.example.groupproject.service.impl.ZoneService;

import java.util.List;

public class ZoneListActivity extends AppCompatActivity {

    private ZoneService serv;
    private LinearLayout layout_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_list);

        serv = ServiceManager.getZoneService(getApplicationContext());
        layout_list = (LinearLayout)findViewById(R.id.layout_list);

        List<Zone> zones = serv.getZones();

        for(final Zone i : zones){
            TextView t = new TextView(getApplicationContext());
            t.setText(i.getName());
            layout_list.addView(t);
        }

    }
}

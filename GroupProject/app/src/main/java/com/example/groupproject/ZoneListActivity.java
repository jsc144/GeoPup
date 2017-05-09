package com.example.groupproject;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.groupproject.Model.Zone;
import com.example.groupproject.service.impl.ZoneService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

public class ZoneListActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnPolygonClickListener  {

    private ZoneService serv;

    private GoogleMap google_map;
    private GoogleApiClient mGoogleApiClient;
    private CameraPosition cam_pos;
    private boolean gotPermission;
    private final LatLng def_loc = new LatLng(-33.8523341, 151.2106085);

    private static final int FINE_LOCATION_PERMISSIONS_REQUEST = 1;
    private static final int ZOOM_LEVEL = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    static final String MAP_INTENT_COLOR = "MAP_INTENT_COLOR";
    private FrameLayout frameLayout;

    private Location mLastKnownLocation;

    static boolean Is_MAP_Moveable = false; // to detect map is movable
    static LatLng firstPoint,secondPoint, thirdPoint, lastPoint;
    static double f1, f2, l1, l2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_list);
        // Retrieve location and camera position from saved instance state.

        serv = ServiceManager.getZoneService(getApplicationContext());

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout2);
        ActionBar ab = getSupportActionBar();
        int color_code = getIntent().getIntExtra(MAP_INTENT_COLOR, 1);
        frameLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.main_activity_color));
        ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.main_activity_color)));


        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cam_pos = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        //play services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }


    /**
     * Fuction that is executed when the map is ready
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        google_map = googleMap;

        updateLocationUI();
        getDeviceLocation();
        google_map.setOnPolygonClickListener(this);


        Draw_Map();
    }

    public void Draw_Map() {

        google_map.clear();

        List<Zone> zones = serv.getZones();

        for(Zone i : zones){
            Log.d("rofl", i.getID());
            PolygonOptions rectOptions = new PolygonOptions();

            firstPoint = new LatLng(i.getStart_lat(),i.getStart_long());
            secondPoint = new LatLng(i.getStart_lat(),i.getEnd_long());
            thirdPoint = new LatLng(i.getEnd_lat(),i.getStart_long());
            lastPoint = new LatLng(i.getEnd_lat(),i.getEnd_long());

            rectOptions.add(firstPoint);
            rectOptions.add(secondPoint);
            rectOptions.add(lastPoint);
            rectOptions.add(thirdPoint);

            if(i.getZoneType()==0){
                rectOptions.strokeColor(Color.GREEN);
                rectOptions.fillColor(R.color.goZone_color);
            }else{
                rectOptions.strokeColor(Color.RED);
                rectOptions.fillColor(R.color.nogoZone_color);
            }
            rectOptions.strokeWidth(7);

            Polygon polygon = google_map.addPolygon(rectOptions);
            String id = i.getID();
            polygon.setTag(id);
            polygon.setClickable(true);
        }

    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gotPermission = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSIONS_REQUEST);
        }

        if (gotPermission) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        if (cam_pos != null) {
            google_map.moveCamera(CameraUpdateFactory.newCameraPosition(cam_pos));
        } else if (mLastKnownLocation != null) {
            google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), ZOOM_LEVEL));
        } else {
            Log.d("Errors", "Current location is null. Using defaults.");
            google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(def_loc, ZOOM_LEVEL));
            google_map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        gotPermission = false;
        switch (requestCode) {
            case FINE_LOCATION_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotPermission = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (google_map == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gotPermission = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSIONS_REQUEST);
        }

        if (gotPermission) {
            google_map.setMyLocationEnabled(true);
            google_map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            google_map.setMyLocationEnabled(false);
            google_map.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    //on Google play services success
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    }

    //suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Errors", "Suspended");
    }

    //connection failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Errors", "Connection failed");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (google_map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, google_map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPolygonClick(final Polygon polygon) {
        final List<LatLng> points = polygon.getPoints();
        final String l = (String)polygon.getTag();
        Log.d("lmao",l.toString());
        AlertDialog alertDialog = new AlertDialog.Builder(ZoneListActivity.this).create();
        alertDialog.setTitle("Delete Zone");
        alertDialog.setMessage("Press OK to delete zone");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        serv.removeZone(l);
                        dialog.dismiss();
                        polygon.remove();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }
}

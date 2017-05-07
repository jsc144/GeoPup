package com.example.groupproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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
    private Polygon polygon;
    static final String FIRST_POINT = "FIRST_POINT";
    static final String LAST_POINT = "LAST_POINT";

    public static void setMovable(boolean x){
        Is_MAP_Moveable = x;
    }

    public static boolean getMovable(){
        return Is_MAP_Moveable;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Retrieve location and camera position from saved instance state.

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        ActionBar ab = getSupportActionBar();
        int color_code = getIntent().getIntExtra(MAP_INTENT_COLOR, 1);

        if(color_code == 0) {
            frameLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.goZone_color));
            ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.goZone_color)));
        }else{
                frameLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.nogoZone_color));
                ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.nogoZone_color)));
        }

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

        FrameLayout fram_map = (FrameLayout) findViewById(R.id.fram_map);
        final Button btn_draw_State = (Button) findViewById(R.id.btn_draw_State);
        final Button btn_retry = (Button) findViewById(R.id.btn_Retry);
        final Button btn_cancel = (Button) findViewById(R.id.btn_Cancel);
        final Button btn_Done = (Button) findViewById(R.id.btn_Done);

        btn_retry.setVisibility(View.GONE);

        btn_draw_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setMovable(true);
                btn_draw_State.setVisibility(View.GONE);
                btn_retry.setVisibility(View.VISIBLE);
            }
        });

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google_map.clear();
                setMovable(false);
                btn_retry.setVisibility(View.GONE);
                btn_draw_State.setVisibility(View.VISIBLE);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btn_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(FIRST_POINT,firstPoint);
                result.putExtra(LAST_POINT,lastPoint);
                setResult(RESULT_OK,result);
                finish();
            }
        });
        fram_map.setOnTouchListener(new View.OnTouchListener() {     @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            int x_co = Math.round(x);
            int y_co = Math.round(y);

            Projection projection = google_map.getProjection();
            Point x_y_points = new Point(x_co, y_co);

            LatLng latLng = google_map.getProjection().fromScreenLocation(x_y_points);
            double latitude = latLng.latitude;

            double longitude = latLng.longitude;

            int eventaction = event.getAction();
            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
                    // finger touches the screen
                    firstPoint = new LatLng(latitude, longitude);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // finger moves on the screen
                    //last_point = new LatLng(latitude, longitude);
                    lastPoint = new LatLng(latitude, longitude);
                    Draw_Map();
                    break;
                case MotionEvent.ACTION_UP:
                    // finger leaves the screen
                    lastPoint = new LatLng(latitude, longitude);
                    Draw_Map();
                    break;
            }

            return getMovable();
        }
        });

    }

    public void Draw_Map() {

        google_map.clear();

        PolygonOptions rectOptions = new PolygonOptions();

        secondPoint = new LatLng(firstPoint.latitude,lastPoint.longitude);
        thirdPoint = new LatLng(lastPoint.latitude,firstPoint.longitude);

        rectOptions.add(firstPoint);
        rectOptions.add(secondPoint);
        rectOptions.add(lastPoint);
        rectOptions.add(thirdPoint);

        rectOptions.strokeColor(Color.BLUE);
        rectOptions.strokeWidth(7);
        rectOptions.fillColor(Color.CYAN);

        polygon = google_map.addPolygon(rectOptions);
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
                .findFragmentById(R.id.map);
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
}
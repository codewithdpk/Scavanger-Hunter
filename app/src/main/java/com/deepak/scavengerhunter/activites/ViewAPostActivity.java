package com.deepak.scavengerhunter.activites;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.deepak.scavengerhunter.APIs.SharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deepak.scavengerhunter.R;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class ViewAPostActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView txt_hunt_name_top;
    TextView txt_post_name;
    TextView txt_creator_name;
    TextView txt_hunt_name_bottom;
    RelativeLayout btn_learn_where_are_you;
    GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    View mapView;
    private LocationManager AppUtils;



    // Variables
    String player_name;
    String hunt_id;
    String hunt_name;
    String post_id;
    String post_name;
    String player_id ;
    String creator_id;
    String post_lat;
    String post_long;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private double currentLat;
    private double currentLong;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_a_post);
        init();
        getValuesFromIntent();
        setValues();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        AppUtils = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view_a_post);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(ViewAPostActivity.this);

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!AppUtils.isLocationEnabled()) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ViewAPostActivity.this);
                    dialog.setMessage("Location not enabled!");
                    dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub

                        }
                    });
                    dialog.show();
                }
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(ViewAPostActivity.this, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        btn_learn_where_are_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAPostActivity.this,LearnMoreAboutPostActivity.class);
                intent.putExtra("hunt_id",hunt_id);
                intent.putExtra("post_id",post_id);
                startActivity(intent);
            }
        });

    }

    private void init(){
         txt_hunt_name_top = findViewById(R.id.txt_hunt_name);
         txt_post_name = findViewById(R.id.txt_posts_name);
         txt_creator_name = findViewById(R.id.txt_owner_name);
         txt_hunt_name_bottom = findViewById(R.id._txt_hunt_name);
         btn_learn_where_are_you = findViewById(R.id.btn_learn_where_are_you);
         progressBar();

    }

    private void getValuesFromIntent(){
        Intent intent = getIntent();
        player_name = intent.getStringExtra("player_name");
        hunt_id = intent.getStringExtra("hunt_id");
        hunt_name = intent.getStringExtra("hunt_name");
        post_id = intent.getStringExtra("post_id");
        post_name = intent.getStringExtra("post_name");
        player_id = intent.getStringExtra("player_id");
        creator_id = intent.getStringExtra("creator_id");
        post_lat = intent.getStringExtra("post_lat");
        post_long = intent.getStringExtra("post_long");
    }

    private void setValues(){
         txt_hunt_name_top.setText(hunt_name);
         txt_post_name.setText(post_name);
         txt_creator_name.setText(creator_id);
         txt_hunt_name_bottom.setText(hunt_name);
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();
        showMarkerAtMap(Double.parseDouble(post_lat), Double.parseDouble(post_long));

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        currentLat =  locationResult.getLastLocation().getLatitude();
                        currentLong = locationResult.getLastLocation().getLongitude();
                    }
                },
                Looper.myLooper());

    }

    private void progressBar() {
        progressDialog = new ProgressDialog(ViewAPostActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void showMarkerAtMap(double lat, double longt) {
//        double latD = Double.parseDouble(lat);
//        double longtD = Double.parseDouble(longt);

//        MarkerOptions marker = new MarkerOptions().position(new LatLng(latD, longtD)).title("point");
//        mMap.addMarker(marker);

        LatLng sydney = new LatLng(lat, longt);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        mMap.addMarker(new MarkerOptions().title("Sydney").snippet("The most populous city in Australia.").position(sydney));

    }
}
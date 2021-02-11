package com.deepak.scavengerhunter.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.APIs.SharedPref;
import com.deepak.scavengerhunter.Adaptors.MyHuntsAdaptor;
import com.deepak.scavengerhunter.Adaptors.PostsBlueAdapter;
import com.deepak.scavengerhunter.Modals.PostsModal;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.classes.Utils;
import com.deepak.scavengerhunter.services.ChatHeadService;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class StartHuntActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    RecyclerView rv_posts;
    String HuntId;
    String HuntName;
    String hunt_owner;
    ArrayList<PostsModal> postsList;
    ProgressDialog progressDialog;

    TextView tv_hunt_name_top;
    TextView tv_hunt_name_bottom;
    TextView tv_owner_name;
    TextView tv_information_of_hunt;
    TextView tv_post_count;

    GoogleMap mMap;
    View mapView;
    double startingLat;
    double startingLong;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationManager AppUtils;
    private GoogleApiClient mGoogleApiClient;
    Button btn_start_hunt;
    Double currentLat;
    Double currentLong;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    int huntSequance = 0;
    View rootView;
    private boolean isActivityReopened = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_hunt);
        init();
        Intent intent = getIntent();
        HuntId = intent.getStringExtra("hunt_id");
        Log.d("HUNT_ID:", HuntId);
        huntSequance = 1;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        AppUtils = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(StartHuntActivity.this);

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!AppUtils.isLocationEnabled()) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(StartHuntActivity.this);
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
            Toast.makeText(StartHuntActivity.this, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

            getHuntInformation(HuntId);


        btn_start_hunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkHuntStatus(SharedPref.getUserId(StartHuntActivity.this), HuntId);


            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentLat!=null && currentLong!= null && postsList != null) {
            // instantiate the location manager, note you will need to request permissions in your manifest
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // get the last know location from your location manager.
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
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Log.d("LOCATION:",location.toString());
            float distance = checkDistance(location.getLatitude(),location.getLongitude(),Double.parseDouble(postsList.get(0).getLat()),Double.parseDouble(postsList.get(0).getLong()));
            Log.d("ONRESUME",distance+"");
            if(distance <=20.00){
                Utils.createToast(StartHuntActivity.this,rootView,"Cool, You have reached at posts destination. ");

                try {
                    Log.d("player_name",SharedPref.getUserInfo(StartHuntActivity.this).getString("name"));
                    Log.d("hunt_id",HuntId);
                    Log.d("hunt_name",HuntName);
                    Log.d("post_id",postsList.get(0).getPost_id());
                    Log.d("post_name",postsList.get(0).getPost_name());
                    Log.d("player_id",SharedPref.getUserId(StartHuntActivity.this));
                    Log.d("creator_id",hunt_owner);
                    Log.d("post_lat",postsList.get(0).getLat());
                    Log.d("post_long",postsList.get(0).getLong());

                    Intent intent = new Intent(StartHuntActivity.this,ViewAPostActivity.class);
                    intent.putExtra("player_name",SharedPref.getUserInfo(StartHuntActivity.this).getString("name"));
                    intent.putExtra("hunt_id",HuntId);
                    intent.putExtra("hunt_name",HuntName);
                    intent.putExtra("post_id",postsList.get(0).getPost_id());
                    intent.putExtra("post_name",postsList.get(0).getPost_name());
                    intent.putExtra("player_id",SharedPref.getUserId(StartHuntActivity.this));
                    intent.putExtra("creator_id",hunt_owner);
                    intent.putExtra("post_lat",postsList.get(0).getLat());
                    intent.putExtra("post_long",postsList.get(0).getLong());
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }else{
                Utils.createToast(StartHuntActivity.this,rootView,"You have not reached to destination yet, This can not be considered as a complete post.");
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + currentLat + "," + currentLong + "&daddr=" + postsList.get(0).getLat() + "," + postsList.get(0).getLong() + "&mode=d";
                //Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                //startActivity(Intent.createChooser(intent, "Select an application"));


                //Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                //mapIntent.setPackage("com.google.android.apps.maps");
                try {

                    startService(new Intent(StartHuntActivity.this, ChatHeadService.class));
                    //startActivity(mapIntent);
                } catch (ActivityNotFoundException ex) {
                    try {

                        startService(new Intent(StartHuntActivity.this, ChatHeadService.class));
                        //startActivity(mapIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(StartHuntActivity.this, "Application not installed", Toast.LENGTH_LONG).show();
                    }
                }
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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




    private void init() {
        rootView = getWindow().getDecorView().getRootView();
        postsList = new ArrayList<>();
        rv_posts = findViewById(R.id.posts_of_hunts);
        tv_hunt_name_top = findViewById(R.id.tv_hunt_name_top);
        tv_hunt_name_bottom = findViewById(R.id.tv_hunt_name_bottom);
        tv_owner_name = findViewById(R.id.tv_owner_name);
        tv_information_of_hunt = findViewById(R.id.tv_information_of_hunt);
        tv_post_count = findViewById(R.id.tv_post_count);
        btn_start_hunt = findViewById(R.id.btn_start_hunt);
        progressBar();
    }

    private void getHuntInformation(String id) {
        Log.d("HUNT-CALLED",id);
        progressDialog.show();
        JSONObject params = new JSONObject();
        try {
            params.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.GET_HUNT_BY_ID, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());
                try {
                    if (response.getString("status").equals("OK")) {
                        JSONObject ownerDetails = response.getJSONObject("owner");
                        JSONObject huntDetails = response.getJSONObject("hunt");
                        JSONArray posts = response.getJSONArray("posts");
                        HuntName = huntDetails.getString("name");
                        hunt_owner = ownerDetails.getString("id");
                        tv_hunt_name_top.setText(huntDetails.getString("name"));
                        tv_hunt_name_bottom.setText(huntDetails.getString("name"));
                        tv_owner_name.setText(ownerDetails.getString("name"));
                        //tv_information_of_hunt.setText(huntDetails.getString("information"));
                        startingLat = huntDetails.getDouble("startingLat");
                        startingLong = huntDetails.getDouble("startingLong");
                        showMarkerAtMap(startingLat, startingLong);
                        tv_post_count.setText(posts.length() + " POSTS");
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.getJSONObject(i);
                            postsList.add(new PostsModal(post.getString("post_id"),
                                    post.getString("post_name"),
                                    post.getString("address"),
                                    post.getString("long"),
                                    post.getString("lat"),
                                    post.getString("hunt_id"),
                                    post.getString("hunt_name"),
                                    post.getString("createdBy"),
                                    post.getString("information"),
                                    post.getString("defaultQuestion"),
                                    post.getString("questionId"),
                                    post.getString("created"),
                                    post.getString("updated"),
                                    post.getString("status")
                            ));
                        }


                        PostsBlueAdapter adapter = new PostsBlueAdapter(StartHuntActivity.this, postsList);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(StartHuntActivity.this, LinearLayoutManager.VERTICAL, false);
                        rv_posts.setLayoutManager(layoutManager);
                        rv_posts.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.toString());
                Toast.makeText(StartHuntActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonOblect);


    }

    private void progressBar() {
        progressDialog = new ProgressDialog(StartHuntActivity.this);
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
        showMarkerAtMap(startingLat, startingLong);

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

    // Check distance between points

    private float checkDistance(double lat1,double lon1,double lat2,double lon2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters;
    }

    // Check is user already started this hunt or not

    private void checkHuntStatus(String user_id,String hunt_id){
        progressDialog.show();
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", user_id);
            params.put("hunt_id",hunt_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.CHECK_HUNT_RECORD, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());

                try {
                    if(response.getString("status").equals("created")){
                        startHuntIntent();
                        progressDialog.dismiss();
                    }else{
                        startHuntIntent();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.getMessage());
                Toast.makeText(StartHuntActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonOblect);
    }

    private void startHuntIntent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(StartHuntActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }else {


            String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + currentLat + "," + currentLong + "&daddr=" + postsList.get(0).getLat() + "," + postsList.get(0).getLong() + "&mode=d";
            //Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            //startActivity(Intent.createChooser(intent, "Select an application"));


            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");
            try {

                startService(new Intent(StartHuntActivity.this, ChatHeadService.class));
                startActivity(mapIntent);
            } catch (ActivityNotFoundException ex) {
                try {

                    startService(new Intent(StartHuntActivity.this, ChatHeadService.class));
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(StartHuntActivity.this, "Application not installed", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    }
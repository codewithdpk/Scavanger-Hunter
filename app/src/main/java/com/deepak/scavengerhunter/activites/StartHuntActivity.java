package com.deepak.scavengerhunter.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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
import com.deepak.scavengerhunter.Adaptors.MyHuntsAdaptor;
import com.deepak.scavengerhunter.Adaptors.PostsBlueAdapter;
import com.deepak.scavengerhunter.Modals.PostsModal;
import com.deepak.scavengerhunter.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StartHuntActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    RecyclerView rv_posts;
    String HuntId;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_hunt);
        init();
        Intent intent = getIntent();
        HuntId = intent.getStringExtra("hunt_id");
        Log.d("HUNT_ID:",HuntId);

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


    private void init(){
        postsList = new ArrayList<>();
        rv_posts = findViewById(R.id.posts_of_hunts);
        tv_hunt_name_top = findViewById(R.id.tv_hunt_name_top);
        tv_hunt_name_bottom = findViewById(R.id.tv_hunt_name_bottom);
        tv_owner_name = findViewById(R.id.tv_owner_name);
        tv_information_of_hunt = findViewById(R.id.tv_information_of_hunt);
        tv_post_count = findViewById(R.id.tv_post_count);
        progressBar();
    }

    private void getHuntInformation(String id) {
        progressDialog.show();
        JSONObject params = new JSONObject();
        try {
            params.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.GET_HUNT_BY_ID, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());
                try {
                    if(response.getString("status").equals("OK")){
                        JSONObject ownerDetails = response.getJSONObject("owner");
                        JSONObject huntDetails = response.getJSONObject("hunt");
                        JSONArray posts = response.getJSONArray("posts");
                        tv_hunt_name_top.setText(huntDetails.getString("name"));
                        tv_hunt_name_bottom.setText(huntDetails.getString("name"));
                        tv_owner_name.setText(ownerDetails.getString("name"));
                        //tv_information_of_hunt.setText(huntDetails.getString("information"));
                        startingLat = huntDetails.getDouble("startingLat");
                        startingLong = huntDetails.getDouble("startingLong");
                        showMarkerAtMap(startingLat,startingLong);
                        tv_post_count.setText(posts.length()+" POSTS");
                        for(int i=0;i<posts.length();i++){
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



                        PostsBlueAdapter adapter = new PostsBlueAdapter(StartHuntActivity.this,postsList);
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

    private void progressBar(){
        progressDialog=new ProgressDialog(StartHuntActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void showMarkerAtMap(double lat, double longt){
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
        showMarkerAtMap(startingLat,startingLong);

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
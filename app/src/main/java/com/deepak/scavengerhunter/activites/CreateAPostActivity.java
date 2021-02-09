package com.deepak.scavengerhunter.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.APIs.IntentCodes;
import com.deepak.scavengerhunter.APIs.SharedPref;
import com.deepak.scavengerhunter.BuildConfig;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.classes.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CreateAPostActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AdapterView.OnItemSelectedListener {
    private GoogleMap mMap;
    View mapView;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    Context mContext;
    TextView mLocationMarkerText;
    private LatLng mCenterLatLong;
    private List<Address> list;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    TextView fetchedaddress;
    String knownName;
    FloatingActionButton getMyCurrentLocation;
    EditText edt_post_name;
    EditText edt_post_information;
    EditText edt_quiz;
    Button btn_create_post;
    Button btn_cancel_post;
    ImageView image_selector;

    Bitmap post_image = null;

    public int IMAGE_CAPTURE = 1;
    public int IMAGE_CHOOSE = 2;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private CreateAPostActivity.AddressResultReceiver mResultReceiver;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    protected String mAreaOutput;

    String AddressString;
    ImageView backbtn;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private LocationManager AppUtils;
    private ProgressDialog progressDialog;
    LatLng currentLocation;
    View rootView;
    public String hunt_id;
    public String hunt_name;
    Spinner ansType;
    String[] bankNames = {"Media", "Text", "Both"};
    String selectedAnswerType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_a_post);
        mContext = this;
        rootView = getWindow().getDecorView().getRootView();
        hunt_id = getIntent().getStringExtra("hunt_id");
        hunt_name = getIntent().getStringExtra("hunt_name");
        FirebaseApp.initializeApp(CreateAPostActivity.this);

        init();


        ansType.setOnItemSelectedListener(this);

//Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        ansType.setAdapter(aa);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_create_a_post);
        mapView = mapFragment.getView();

        getMyCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(CreateAPostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateAPostActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    changeMap(mLastLocation);
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_create_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String post_name = edt_post_name.getText().toString();
                String information = edt_post_information.getText().toString();
                String quiz = edt_quiz.getText().toString();

                double lat = currentLocation.latitude;
                double Long = currentLocation.longitude;
                if (post_name.equals("") || information.equals("") || quiz.equals("")) {
                    Utils.createToast(CreateAPostActivity.this, rootView, "Please fill all details");
                } else {
                    if (post_image != null) {

                        if (selectedAnswerType != null) {
                            //Utils.createToast(CreateAPostActivity.this,rootView,AddressString);
                            progressDialog.show();
                            Bitmap bt = getScaledBitmap(post_image);
                            uploadFile(bt, post_name, AddressString, Long, lat, hunt_id, hunt_name, information, quiz, selectedAnswerType);

                        } else {
                            Utils.createToast(CreateAPostActivity.this, rootView, "Please select answer type.");

                        }

                    } else {
                        Utils.createToast(CreateAPostActivity.this, rootView, "Please add a image.");

                    }


                }

            }
        });

        image_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        AppUtils = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // click event for show-dismiss bottom sheet
        // callback for do something
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        mapFragment.getMapAsync(CreateAPostActivity.this);
        mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!AppUtils.isLocationEnabled()) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
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
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

    }


    private void init() {
        mLocationMarkerText = (TextView) findViewById(R.id.locationMarkertext);
        getMyCurrentLocation = findViewById(R.id.currentlocfab);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        fetchedaddress = findViewById(R.id.locateaddress);
        edt_post_name = findViewById(R.id.edt_post_name);
        edt_post_information = findViewById(R.id.edt_add_post_information);
        edt_quiz = findViewById(R.id.edt_quiz);
        btn_cancel_post = findViewById(R.id.btn_cancel_post);
        btn_create_post = findViewById(R.id.btn_add_post);
        backbtn = findViewById(R.id.ic_back_create_a_post);
        image_selector = findViewById(R.id.image_selector);
        ansType = (Spinner) findViewById(R.id.simpleSpinner);
        progressBar();
    }

    private void progressBar() {
        progressDialog = new ProgressDialog(CreateAPostActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                mMap.clear();

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    //startIntentService(mLocation);
                    currentLocation = mCenterLatLong;
                    getAddress(mCenterLatLong.latitude, mCenterLatLong.longitude);
                    //mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(Bundle bundle) {
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
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
             changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


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

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(15f).tilt(30).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            getAddress(location.getLatitude(), location.getLongitude());
            //startIntentService(location);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }


    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(android.os.Handler handler) {
            super(handler);
            Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//
//            // Display the address string or an error message sent from the intent service.
//            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
//
//            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
//
//            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
//            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);
//
//            displayAddressOutput();
//
//            // Show a toast message if an address was found.
//            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
//                //  showToast(getString(R.string.address_found));
//
//
//            }
//
//
//        }

    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");

                fetchedaddress.setText(knownName);
            edt_post_name.setText(knownName);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
//    protected void startIntentService(Location mLocation) {
//        // Create an intent for passing to the intent service responsible for fetching the address.
//        Intent intent = new Intent(this, FetchAddressIntentService.class);
//
//        // Pass the result receiver as an extra to the service.
//        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);
//
//        // Pass the location data as an extra to the service.
//        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);
//
//        // Start the service. If the service isn't already running, it is instantiated and started
//        // (creating a process for it if needed); if it is running then it remains running. The
//        // service kills itself automatically once all intents are processed.
//        startService(intent);
//    }
    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("This is your result code: ",resultCode+"");
        Toast.makeText(mContext, "This is your result code: " + resultCode, Toast.LENGTH_SHORT).show();
        if (requestCode == 1) {

            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            try {
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                        bitmapOptions);
                //post_image = getStringImage(bitmap);
                post_image = bitmap;
                image_selector.setImageBitmap(bitmap);
                String path = android.os.Environment
                        .getExternalStorageDirectory()
                        + File.separator
                        + "Phoenix" + File.separator + "default";
                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == 2) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            Log.w("path", picturePath + "");
            image_selector.setImageBitmap(thumbnail);
            //post_image = getStringImage(thumbnail);
            post_image = thumbnail;
        }
        // Check that the result was from the autocomplete widget.
//        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
//            if (resultCode == RESULT_OK) {
//                // Get the user's selected place from the Intent.
//                Place place = (Place) PlaceAutocomplete.getPlace(mContext, data);
//
//                // TODO call location based filter
//
//
//                LatLng latLong;
//
//
//                latLong = place.getLatLng();
//
//                //mLocationText.setText(place.getName() + "");
//
//                CameraPosition cameraPosition = new CameraPosition.Builder()
//                        .target(latLong).zoom(19f).tilt(70).build();
//
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                mMap.setMyLocationEnabled(true);
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//
//            }
//
//
//        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//            Status status = PlaceAutocomplete.getStatus(mContext, data);
//        } else if (resultCode == RESULT_CANCELED) {
//            // Indicates that the activity closed before a selection was made. For example if
//            // the user pressed the back button.
//        }

    }

    private void getAddress(double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(CreateAPostActivity.this, Locale.getDefault());
        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
            if (list != null && list.size() > 0) {
                address = list.get(0).getAddressLine(0);
                knownName = list.get(0).getFeatureName();
                AddressString = address;
                fetchedaddress.setText(address);
                edt_post_name.setText(knownName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Address> getAddressForApi(double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(CreateAPostActivity.this, Locale.getDefault());
        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
            return list;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createPost(final Context context, JSONObject params, final View view) {


        // progressDialog.show();
        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.CREATE_POST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());
                //Utils.createToast(context,rootView,response.toString());
                progressDialog.dismiss();
                try {
                    if (response.getString("status").equals("OK")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", true);
                        setResult(IntentCodes.CREATE_A_POST, returnIntent);
                        finish();
                    } else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", false);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();

                }


                //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.d("VOLLEY", error.toString());
                Utils.createToast(context, rootView, error.toString());
                progressDialog.dismiss();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonOblect);


    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAPostActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    Uri uri = FileProvider.getUriForFile(CreateAPostActivity.this, BuildConfig.APPLICATION_ID + ".provider", f);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    private void uploadFile(Bitmap bitmap, final String post_name, String addressString, final double aLong, final double lat, String hunt_id, String hunt_name, final String information, final String quiz, final String selectedAnswerType) {
        final JSONObject[] RES = {new JSONObject()};
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://scavenger-hunter-1608147586701.appspot.com");
        StorageReference mountainImagesRef = storageRef.child(generateImageName() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Utils.createToast(getApplicationContext(), rootView, exception.getMessage());
                Log.d("FIREBASE", exception.getMessage());
                progressDialog.dismiss();


                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String url = uri.toString();
                        Log.e("TAG:", "the url is: " + url);

                        JSONObject params = new JSONObject();

                        try {
                            params.put("post_name", post_name);
                            params.put("address", AddressString);
                            params.put("long", aLong);
                            params.put("lat", lat);
                            params.put("hunt_id", CreateAPostActivity.this.hunt_id);
                            params.put("hunt_name", CreateAPostActivity.this.hunt_name);
                            params.put("createdBy", SharedPref.getUserId(CreateAPostActivity.this));
                            params.put("information", information);
                            params.put("defaultQuestion", quiz);
                            params.put("post_image", url);
                            params.put("answerType", selectedAnswerType);
                            createPost(CreateAPostActivity.this, params, rootView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        String ref = yourStorageReference.getName();
//                        Log.e("TAG:", "the ref is: " + ref);
                    }
                });

                //sendMsg("" + downloadUrl, 2);


            }
        });


    }

    private String generateImageName() {
        String uniqueID = UUID.randomUUID().toString();

        return uniqueID;
    }


    public Bitmap getScaledBitmap(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//Compression quality, here 100 means no compression, the storage of compressed data to baos
        int options = 90;
        while (baos.toByteArray().length / 1024 > 400) {  //Loop if compressed picture is greater than 400kb, than to compression
            baos.reset();//Reset baos is empty baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//The compression options%, storing the compressed data to the baos
            options -= 10;//Every time reduced by 10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//The storage of compressed data in the baos to ByteArrayInputStream
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//The ByteArrayInputStream data generation
        return bitmap;
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), bankNames[position], Toast.LENGTH_LONG).show();
        selectedAnswerType = bankNames[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub
        selectedAnswerType = null;
    }


}
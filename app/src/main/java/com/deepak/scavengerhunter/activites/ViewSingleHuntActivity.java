package com.deepak.scavengerhunter.activites;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.APIs.IntentCodes;
import com.deepak.scavengerhunter.Adaptors.MyHuntsAdaptor;
import com.deepak.scavengerhunter.Adaptors.PostsAdapter;
import com.deepak.scavengerhunter.BuildConfig;
import com.deepak.scavengerhunter.Modals.PostsModal;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.classes.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewSingleHuntActivity extends AppCompatActivity {

    String hunt_id;
    ProgressDialog progressDialog;
    View rootView;
    TextView tv_name_of_hunt;
    TextView tv_hunt_owner;
    TextView tv_post_number;
    TextView tv_starting_point_name;
    TextView tv_starting_point_address;
    TextView tv_ending_point_name;
    TextView tv_ending_point_address;
    TextView tv_add_new_hunt;
    Button btn_complete_the_hunt;
    RecyclerView rv_hunts_posts;
    ArrayList<PostsModal> postsList;
    String hunt_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_hunt);
        init();
        Intent intent = getIntent();

        hunt_id = intent.getStringExtra("hunt_id");
        if (hunt_id != null || !hunt_id.equals("")) {
            // Get hunt data
            getHuntData(hunt_id);
        } else {
            finish();
        }

        btn_complete_the_hunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postsList.size() == 0){
                    Utils.createToast(ViewSingleHuntActivity.this,rootView,"You at least have one post in the hunt.");
                }else{
                    completeHunt();
                }

            }
        });

        tv_add_new_hunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSingleHuntActivity.this, CreateAPostActivity.class);
                intent.putExtra("hunt_id", hunt_id);
                intent.putExtra("hunt_name", hunt_name);
                startActivityForResult(intent, IntentCodes.CREATE_A_POST);
                //startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("RESULT:",resultCode+"");
        getHuntData(hunt_id);
 //       if (requestCode == IntentCodes.CREATE_A_POST) {
//            Log.d("RESULT:",resultCode+"");
//            if (resultCode == Activity.RESULT_OK) {
//
//
//                if(data.getBooleanExtra("result",true)){
//
//                }else{
//
//                }
//
//
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//                //Write your code if there's no result
//                Log.d("INTENT_REQUEST","Running-calceled");
//            }
 //       }
    }

    private void init() {
        progressBar();
        rootView = getWindow().getDecorView().getRootView();
        tv_name_of_hunt = findViewById(R.id.tv_name_of_hunt);
        tv_hunt_owner = findViewById(R.id.tv_hunt_owner);
        tv_post_number = findViewById(R.id.tv_post_number);
        tv_starting_point_name = findViewById(R.id.tv_starting_point_name);
        tv_starting_point_address = findViewById(R.id.tv_starting_point_address);
        tv_ending_point_name = findViewById(R.id.tv_ending_point_name);
        tv_ending_point_address = findViewById(R.id.tv_ending_point_address);
        tv_add_new_hunt = findViewById(R.id.tv_add_new_hunt);
        rv_hunts_posts = findViewById(R.id.rv_hunts_posts);
        btn_complete_the_hunt = findViewById(R.id.btn_complete_the_hunt);
        postsList = new ArrayList<>();
    }

    private void getHuntData(String hunt_id) {
        postsList.clear();
        progressDialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("id", hunt_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.GET_HUNT_BY_ID, params, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());

                try {
                    if (response.getString("status").equals("OK")) {
                        JSONObject data = response.getJSONObject("hunt");
                        hunt_name = data.getString("name");
                        tv_name_of_hunt.setText(data.getString("name"));
                        tv_hunt_owner.setText(data.getString("createdBy"));

                        tv_starting_point_name.setText(data.getString("startingArea"));
                        tv_starting_point_address.setText(data.getString("completeStartingAddress"));
                        if (data.getString("endingArea").equals("none")) {
                            tv_ending_point_name.setText("Pending");
                            tv_ending_point_address.setText("Pending");
                        } else {
                            tv_ending_point_name.setText(data.getString("endingArea"));
                            tv_ending_point_address.setText(data.getString("endingStartingAddress"));
                        }
                        JSONArray posts = response.getJSONArray("posts");
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
                                    post.getString("status")));
                        }

                        tv_post_number.setText(postsList.size()+" posts");

                        PostsAdapter adapter = new PostsAdapter(ViewSingleHuntActivity.this,postsList);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewSingleHuntActivity.this, LinearLayoutManager.VERTICAL, false);
                        rv_hunts_posts.setLayoutManager(layoutManager);
                        rv_hunts_posts.setAdapter(adapter);
                        progressDialog.dismiss();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                progressDialog.dismiss();


                //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.toString());
                Utils.createToast(ViewSingleHuntActivity.this, rootView, error.toString());
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
        progressDialog = new ProgressDialog(ViewSingleHuntActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }
    private void completeHunt() {
        final CharSequence[] options = { "Use latest post as end point", "Add New post for end point","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewSingleHuntActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Use latest post as end point"))
                {
                    useLatestPostAsEndingPoint();
                }
                else if (options[item].equals("Add New post for end point"))
                {

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void useLatestPostAsEndingPoint(){
        progressDialog.show();
        JSONObject params = new JSONObject();
        try {
            params.put("hunt_id", hunt_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.USE_LATEST_POST_ENDING_POINT, params, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());
                progressDialog.dismiss();
                try {
                    if(response.getString("status").equals("OK")){
                        Utils.createToast(ViewSingleHuntActivity.this,rootView,response.getString("message"));
                        getHuntData(hunt_id);
                    }else{
                        Utils.createToast(ViewSingleHuntActivity.this,rootView,response.getString("message"));

                    }
                } catch (JSONException e) {
                    //progressDialog.dismiss();
                    e.printStackTrace();
                }





                //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.toString());
                Utils.createToast(ViewSingleHuntActivity.this, rootView, error.toString());
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



}
package com.deepak.scavengerhunter.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StartHuntActivity extends AppCompatActivity {

    RecyclerView rv_posts;
    String HuntId;
    ArrayList<PostsModal> postsList;
    ProgressDialog progressDialog;

    TextView tv_hunt_name_top;
    TextView tv_hunt_name_bottom;
    TextView tv_owner_name;
    TextView tv_information_of_hunt;
    TextView tv_post_count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_hunt);
        init();
        Intent intent = getIntent();
        HuntId = intent.getStringExtra("hunt_id");
        Log.d("HUNT_ID:",HuntId);
        getHuntInformation(HuntId);
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
}
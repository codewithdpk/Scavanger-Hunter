package com.deepak.scavengerhunter.activites;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.APIs.SharedPref;
import com.deepak.scavengerhunter.Adaptors.MyHuntsAdaptor;
import com.deepak.scavengerhunter.Modals.HuntModal;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.classes.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyHuntsActivity extends AppCompatActivity {

    RecyclerView rv_myhunts;
    public ArrayList<HuntModal> my_hunts;
    View rootView;

    ProgressDialog progressDialog;

    RelativeLayout no_hunt_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_hunts);
        rootView = getWindow().getDecorView().getRootView();
        init();

        JSONObject params = new JSONObject();
        try {
            params.put("id", SharedPref.getUserId(MyHuntsActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getAllHunts(MyHuntsActivity.this,params,rootView);
    }

    private void init(){
        rv_myhunts = findViewById(R.id.rv_my_hunts);
        my_hunts = new ArrayList<>();
        no_hunt_layout = findViewById(R.id.layout_no_results_found);
        progressBar();
    }



    private void getAllHunts(final Context context, JSONObject params, final View view) {
       progressDialog.show();


        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.GET_USERS_HUNT, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:",response.toString());

                try {
                    if(response.getString("status").equals("OK")){
                        JSONArray hunts = response.getJSONArray("hunts");
                        if(hunts.length()!=0){
                            no_hunt_layout.setVisibility(View.GONE);
                            rv_myhunts.setVisibility(View.VISIBLE);
                            for( int i=0; i< hunts.length(); i++){
                                JSONObject hunt = hunts.getJSONObject(i);
                                JSONObject obj = hunt.getJSONObject("hunt");
                                JSONArray posts = hunt.getJSONArray("posts");
                                my_hunts.add(new HuntModal(obj.getString("hunt_id"),obj.getString("createdBy"),obj.getString("name"),obj.getString("startingArea"),obj.getString("completeStartingAddress"),obj.getString("startingLong"),obj.getString("startingLat"),obj.getString("endingArea"),obj.getString("endingStartingAddress"),obj.getString("endingLong"),obj.getString("endingLat"),obj.getString("created"),obj.getString("updated"),obj.getString("status"),posts));
                            }
                            MyHuntsAdaptor adapter = new MyHuntsAdaptor(MyHuntsActivity.this,my_hunts);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(MyHuntsActivity.this, LinearLayoutManager.VERTICAL, false);
                            rv_myhunts.setLayoutManager(layoutManager);
                            rv_myhunts.setAdapter(adapter);
                            progressDialog.dismiss();
                        }else{
                            no_hunt_layout.setVisibility(View.VISIBLE);
                            rv_myhunts.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }

                    }else{

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
                Log.d("VOLLEY",error.toString());
                Utils.createToast(context,rootView,error.toString());
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
        progressDialog=new ProgressDialog(MyHuntsActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }
}
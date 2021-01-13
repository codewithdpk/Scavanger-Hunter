package com.deepak.scavengerhunter.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.Adaptors.MyHuntsAdaptor;
import com.deepak.scavengerhunter.Modals.HuntModal;
import com.deepak.scavengerhunter.Modals.People;
import com.deepak.scavengerhunter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchHuntActivity extends AppCompatActivity {

    ArrayList<HuntModal> results;
    RecyclerView rv_hunts;
    EditText searchBar;
    ImageView ic_back;
    ImageView ic_close;
    ProgressDialog progressDialog;
    RelativeLayout layout_no_results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hunt);
        init();
        searchBar.addTextChangedListener(new TextWatcher() {

            final android.os.Handler handler = new android.os.Handler();
            Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                //show some progress, because you can access UI here
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        //do some work with s.toString()
                        if(s.length()>2){
                            progressDialog.show();
                            hitAPi(s.toString());
                        }
                    }
                };
                handler.postDelayed(runnable, 1500);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void init(){
        results = new ArrayList<>();
        rv_hunts = findViewById(R.id.rv_hunts);
        searchBar = findViewById(R.id.edt_hunts_search_bar);
        ic_back = findViewById(R.id.ic_back);
        ic_close = findViewById(R.id.ic_close);
        layout_no_results = findViewById(R.id.layout_no_results_found);
        progressBar();


    }

    private void hitAPi(String key){
        results.clear();
        JSONObject params = new JSONObject();
        Log.d("AutoCompleteTextView:", key);
        try {
            params.put("key",key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.GET_FILTERS_HUNTS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());
                try {

                        if(response.getString("status").equals("OK")){
                            JSONArray hunts = response.getJSONArray("hunts");
                            if(hunts.length() == 0){
                                rv_hunts.setVisibility(View.GONE);
                                layout_no_results.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }else{
                                rv_hunts.setVisibility(View.VISIBLE);
                                layout_no_results.setVisibility(View.GONE);
                                for( int i=0; i< hunts.length(); i++){
                                    JSONObject hunt = hunts.getJSONObject(i);
                                    JSONObject obj = hunt.getJSONObject("hunt");
                                    JSONArray posts = hunt.getJSONArray("posts");
                                    results.add(new HuntModal(obj.getString("hunt_id"),obj.getString("createdBy"),obj.getString("name"),obj.getString("startingArea"),obj.getString("completeStartingAddress"),obj.getString("startingLong"),obj.getString("startingLat"),obj.getString("endingArea"),obj.getString("endingStartingAddress"),obj.getString("endingLong"),obj.getString("endingLat"),obj.getString("created"),obj.getString("updated"),obj.getString("status"),posts));
                                }
                                MyHuntsAdaptor adapter = new MyHuntsAdaptor(SearchHuntActivity.this,results);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(SearchHuntActivity.this, LinearLayoutManager.VERTICAL, false);
                                rv_hunts.setLayoutManager(layoutManager);
                                rv_hunts.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }

                        }else{

                        }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.toString());
                Toast.makeText(SearchHuntActivity.this, error.toString(), Toast.LENGTH_LONG).show();


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
        progressDialog=new ProgressDialog(SearchHuntActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }
}
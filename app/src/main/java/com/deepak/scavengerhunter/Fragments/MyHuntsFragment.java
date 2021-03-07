package com.deepak.scavengerhunter.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.APIs.SharedPref;
import com.deepak.scavengerhunter.Adaptors.HuntsAdapter;
import com.deepak.scavengerhunter.Adaptors.MyHuntsAdaptor;
import com.deepak.scavengerhunter.Modals.HuntModal;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.activites.MyHuntsActivity;
import com.deepak.scavengerhunter.classes.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyHuntsFragment extends Fragment {

    RecyclerView rv_myhunts;
    public ArrayList<HuntModal> my_hunts;
    View rootView;
    ProgressDialog progressDialog;

    RelativeLayout no_hunt_layout;
    ShimmerFrameLayout shimmerLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_hunts, container, false);
        init(rootView);
        this.rootView = rootView;

        JSONObject params = new JSONObject();
        try {
            params.put("id", SharedPref.getUserId(getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getAllHunts(getContext(), params, rootView);
        return rootView;
    }


    private void init(View view) {

        rv_myhunts = view.findViewById(R.id.rv_my_hunts);
        my_hunts = new ArrayList<>();
        no_hunt_layout = view.findViewById(R.id.layout_no_results_found);
        shimmerLayout = view.findViewById(R.id.shimmer_view_container);

        progressBar();
    }


    private void getAllHunts(final Context context, JSONObject params, final View view) {
        //progressDialog.show();
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();


        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.GET, EndPoints.GET_LEADERSHIP_HUNTS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());

                try {
                    if (response.getString("status").equals("OK")) {
                        JSONArray hunts = response.getJSONArray("hunts");
                        if (hunts.length() != 0) {
                            no_hunt_layout.setVisibility(View.GONE);
                            rv_myhunts.setVisibility(View.VISIBLE);
                            for (int i = 0; i < hunts.length(); i++) {
                                JSONObject hunt = hunts.getJSONObject(i);
                                JSONObject obj = hunt.getJSONObject("hunt");
                                JSONArray posts = hunt.getJSONArray("posts");
                                JSONObject owner = hunt.getJSONObject("owner");
                                my_hunts.add(new HuntModal(obj.getString("hunt_id"), owner.getString("name"), obj.getString("name"), obj.getString("startingArea"), obj.getString("completeStartingAddress"), obj.getString("startingLong"), obj.getString("startingLat"), obj.getString("endingArea"), obj.getString("endingStartingAddress"), obj.getString("endingLong"), obj.getString("endingLat"), obj.getString("created"), obj.getString("updated"), obj.getString("status"), posts));
                            }
                            HuntsAdapter adapter = new HuntsAdapter(getContext(), my_hunts);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            rv_myhunts.setLayoutManager(layoutManager);
                            rv_myhunts.setAdapter(adapter);
                            shimmerLayout.stopShimmerAnimation();
                            shimmerLayout.setVisibility(View.GONE);
                            //progressDialog.dismiss();
                        } else {
                            no_hunt_layout.setVisibility(View.VISIBLE);
                            rv_myhunts.setVisibility(View.GONE);
                            shimmerLayout.stopShimmerAnimation();
                            shimmerLayout.setVisibility(View.GONE);
                            //progressDialog.dismiss();

                        }

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //progressDialog.dismiss();
                    shimmerLayout.stopShimmerAnimation();
                    shimmerLayout.setVisibility(View.GONE);
                }


                //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.toString());
                Utils.createToast(context, rootView, error.toString());
                //progressDialog.dismiss();
                shimmerLayout.stopShimmerAnimation();
                shimmerLayout.setVisibility(View.GONE);


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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }
}

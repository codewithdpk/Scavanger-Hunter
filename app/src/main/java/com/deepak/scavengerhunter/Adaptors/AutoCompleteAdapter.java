package com.deepak.scavengerhunter.Adaptors;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> data;
    //private final String server = "http://myserver/script.php?query=";
    public Context context;
    public int layoutResourceId;

    public AutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource,ArrayList<String> data) {
        super(context, resource);
        this.layoutResourceId = resource;
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();
                if (constraint != null) {


                    JSONObject params = new JSONObject();
                    Log.d("AutoCompleteTextView:", constraint.toString());
                    try {
                        params.put("key", constraint.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.GET_FILTERS_HUNTS, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("RESPONSE:", response.toString());

                            JSONArray terms = null;
                            try {
                                terms = new JSONArray(response.getString("hunts"));
                                ArrayList<String> suggestions = new ArrayList<>();
                                for (int ind = 0; ind < terms.length(); ind++) {
                                    String term = terms.getJSONObject(ind).getString("name");
                                    suggestions.add(term);
                                }
                                results.values = suggestions;
                                results.count = suggestions.size();
                                data = suggestions;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("VOLLEY", error.toString());
                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();


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
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }
        };
    }
}

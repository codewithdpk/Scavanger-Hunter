package com.deepak.scavengerhunter.APIs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StyleFetcher {

    public void retrieveResults(String searchKey, final Context context) {
        JSONObject params = new JSONObject();
        try {
            params.put("key",searchKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.CREATE_HUNT, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:",response.toString());
                //Utils.createToast(context,rootView,response.toString());



                //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY",error.toString());
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
}

package com.deepak.scavengerhunter.Adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.Modals.People;
import com.deepak.scavengerhunter.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class PeopleAdapter extends ArrayAdapter<People> {

    private ArrayList<People> dataList;
    private Context mContext;
    private int itemLayout;

//    private LocalDatabaseRepo localDatabaseRepo = new LocalDatabaseRepo();

    private PeopleAdapter.ListFilter listFilter = new PeopleAdapter.ListFilter();

    public PeopleAdapter(Context context, int resource, ArrayList<People> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public People getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        TextView strName = (TextView) view.findViewById(R.id.lbl_name);
        strName.setText(getItem(position).getName());

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = new ArrayList<String>();
                    results.count = 0;
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                //Call to database to get matching records using room
                JSONObject params = new JSONObject();
                Log.d("AutoCompleteTextView:", searchStrLowerCase);
                try {
                    params.put("key",searchStrLowerCase);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final ArrayList<People> matchValues  = new ArrayList<>();
                final FilterResults filterResults = new FilterResults();
                JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.GET_FILTERS_HUNTS, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE:", response.toString());

                        JSONArray terms = null;
                        try {
                            terms = new JSONArray(response.getString("hunts"));

                            for (int ind = 0; ind < terms.length(); ind++) {
                                String term = terms.getJSONObject(ind).getString("name");

                                matchValues.add(new People(term,term,0));

                            }

//                            for (People people : matchValues) {
//                                if (people.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
//                                    suggestions.add(people);
//                                }
//                            }

                            results.values = matchValues;
                            results.count = matchValues.size();
                            //Log.d("SIZE",suggestions.size()+"");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY", error.toString());
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();


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
            if (results.values != null) {
                dataList = (ArrayList<People>)results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}


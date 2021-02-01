package com.deepak.scavengerhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.APIs.SharedPref;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.activites.HomeActivity;
import com.deepak.scavengerhunter.activites.HomeBaseActivity;
import com.deepak.scavengerhunter.classes.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity {

    Button btn_signup;

    EditText edt_fullname;
    EditText edt_email;
    EditText edt_password;
    EditText edt_confirm_password;

    TextView txt_signin;

    String fullname;
    String email;
    String password;
    String confirm_password;

    Utils utils;
    EndPoints endPoints;
    LinearLayout rootLayout;
    ProgressDialog mProgressDialog;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialization of variables
        init();
        rootView = getWindow().getDecorView().getRootView();


        // Go to Sign in screen back
        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this,AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                fullname = edt_fullname.getText().toString();
                email = edt_email.getText().toString();
                password = edt_password.getText().toString();
                confirm_password = edt_confirm_password.getText().toString();

                if(fullname.equals("") || email.equals("") ||password.equals("") || confirm_password.equals("")){
                    utils.createToast(RegistrationActivity.this,v,"All fields are required to be filled.");
                    mProgressDialog.dismiss();
                }else{
                    if(utils.isValidEmailId(email)){

                        if(password.equals(confirm_password)){

//                            HashMap<String,String> params = new HashMap<>();
//                            params.put("name",fullname);
//                            params.put("email",email);
//                            params.put("password",password);
//                            params.put("googleid","none");
//                            params.put("facebookid","none");
//                            params.put("image_url","none");
//                            params.put("mode","email");
                            callApi(fullname,email,password,"none","none","none","email");

                        }else{
                            utils.createToast(RegistrationActivity.this, v, "Passwords are not matching");
                            mProgressDialog.dismiss();
                        }
                    }else{
                        mProgressDialog.dismiss();

                        utils.createToast(RegistrationActivity.this,v, "Please enter a valid email id.");



                    }

                }
            }
        });
    }

    private void init(){
        utils = new Utils();

        //makeRequests = new MakeRequests();
        endPoints = new EndPoints();
        rootLayout = (LinearLayout) findViewById(R.id.rootLayout_RegistrationActivity);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        edt_fullname = (EditText) findViewById(R.id.edt_fullname);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_confirm_password = (EditText) findViewById(R.id.edt_confirm_password);
        txt_signin = (TextView) findViewById(R.id.txt_signin);
        progressBar();
    }

    private void progressBar (){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

    }

//    private void callApi(String name, String email, String password, String googleid, String facebookid,String image_url,String mode){
//
//    }


    void callApi(String fullname, String email, String password, String googleid, String facebookid, String image_url, String mode){
        JSONObject params = new JSONObject();

        try {
            params.put("name", fullname);
            params.put("email",email);
            params.put("password",password);
            params.put("googleid",googleid);
            params.put("facebookid",facebookid);
            params.put("mode",mode);
            params.put("image_url",image_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.RESGISTRATION, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());
                mProgressDialog.dismiss();
                Utils.createToast(RegistrationActivity.this,rootView,"Login Success.");
                //Utils.createToast(context,rootView,response.getJSONObject("userInfo").toString());

                try {
                    SharedPref.saveUserInfo(RegistrationActivity.this,response.getJSONObject("userDetails").toString());
                    startActivity(new Intent(RegistrationActivity.this, HomeBaseActivity.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgressDialog.dismiss();
                }

                //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.toString());
                Toast.makeText(RegistrationActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                mProgressDialog.dismiss();
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
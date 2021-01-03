package com.deepak.scavengerhunter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.deepak.scavengerhunter.R;

import com.deepak.scavengerhunter.activities.APIs.EndPoints;

import com.deepak.scavengerhunter.activities.classes.Modals.RegisterUser;
import com.deepak.scavengerhunter.activities.classes.Modals.User;
import com.deepak.scavengerhunter.activities.classes.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;



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
    RelativeLayout rootLayout;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialization of variables
        init();

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

                            HashMap<String,String> params = new HashMap<>();
                            params.put("name",fullname);
                            params.put("email",email);
                            params.put("password",password);
                            params.put("googleid","none");
                            params.put("facebookid","none");
                            params.put("image_url","none");
                            params.put("mode","email");
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
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout_RegistrationActivity);
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

    private void callApi(String name, String email, String password, String googleid, String facebookid,String image_url,String mode){


        mProgressDialog.dismiss();
    }
}
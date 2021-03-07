package com.deepak.scavengerhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.APIs.SharedPref;
import com.deepak.scavengerhunter.R;

import com.deepak.scavengerhunter.activites.HomeActivity;
import com.deepak.scavengerhunter.activites.HomeBaseActivity;
import com.deepak.scavengerhunter.classes.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {


    Button btn_continue_with_google;
    //Button btn_continue_with_facebook;
    Button btn_signin;

    TextView txt_signup;
    TextView txt_forgot_password;

    EditText edt_email;
    EditText edt_password;

    public static int RC_SIGN_IN = 1111;
    ProgressDialog pDialog;

    public View rootView;

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account); Do something..
        //Toast.makeText(this, account.toString(), Toast.LENGTH_LONG).show();
    }

    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        rootView = getWindow().getDecorView().getRootView();
        // Initialization of variables
        init();

        // Go to signup screen
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        // Signup with email and password

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Grab values in variables

                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();
                if(email.equals("") || password.equals("")){
                    Utils.createToast(AuthActivity.this,rootView,"Please fill all values");
                }else{

                    pDialog.show();
                    JSONObject params = new JSONObject();
                    try {
                        params.put("email",email);
                        params.put("password",password);
                        params.put("mode","email");
                        hitApi(AuthActivity.this, EndPoints.LOGIN,email,password,rootView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }







            }
            }
        });

        // Sign up with google
        btn_continue_with_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(AuthActivity.this, gso);

    }

    private void init(){
        btn_continue_with_google = (Button) findViewById(R.id.btn_continue_with_google);
        //btn_continue_with_facebook = (Button) findViewById(R.id.btn_continue_with_facebook);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        txt_signup = (TextView) findViewById(R.id.txt_signup);
        txt_forgot_password = (TextView) findViewById(R.id.txt_forgot_password);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        progressBar();


    }

    private void progressBar(){
        pDialog = new ProgressDialog(AuthActivity.this); //Your Activity.this
        pDialog.setMessage("Loading...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("GOOGLE-SIGNIN","Running");
            singinWithGoogle(account);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void singinWithGoogle(GoogleSignInAccount account) {

        JSONObject params = new JSONObject();

        try {
            params.put("name", account.getDisplayName());
            params.put("email",account.getEmail());
            params.put("password","none");
            params.put("googleid",account.getId());
            params.put("facebookid","none");
            params.put("mode","google");
            params.put("image_url",account.getPhotoUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, EndPoints.LOGIN_GOOGLE, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE:", response.toString());
                pDialog.dismiss();
                Utils.createToast(AuthActivity.this,rootView,"Login Success.");
                //Utils.createToast(context,rootView,response.getJSONObject("userInfo").toString());

                try {
                    SharedPref.saveUserInfo(AuthActivity.this,response.getJSONObject("userDetails").toString());
                    startActivity(new Intent(AuthActivity.this, HomeBaseActivity.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                }

                //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.getMessage());
                Toast.makeText(AuthActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

                pDialog.dismiss();
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

    private void hitApi(final Context context, String url, final String email, final String password, final View view) {
        final boolean[] loggedin = {false};
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("mode", "email");


            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("RESPONSE:",response.toString());
                        if(response.getString("status").equals("OK")){
                            pDialog.dismiss();
                            Utils.createToast(context,rootView,"Login Success.");
                            //Utils.createToast(context,rootView,response.getJSONObject("userInfo").toString());

                            SharedPref.saveUserInfo(context,response.getJSONObject("userInfo").toString());

                            startActivity(new Intent(AuthActivity.this, HomeBaseActivity.class));
                            finish();


                        }else if(response.getString("status").equals("user_not_found")){
                            pDialog.dismiss();
                            Utils.createToast(context,rootView,response.getString("message"));

                        }else{
                            pDialog.dismiss();
                            Utils.createToast(context,rootView,"Something went wrong.");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("VOLLEY",e.toString());

                        pDialog.dismiss();
                    }

                    //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VOLLEY",error.toString());
                    Utils.createToast(context,rootView,error.toString());


                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    return headers;
                }
            };
            AppController.getInstance().addToRequestQueue(jsonOblect);

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            Log.d("VOLLEY",jsonException.toString());

            pDialog.dismiss();
        }






    }
}
package com.deepak.scavengerhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.deepak.scavengerhunter.APIs.SharedPref;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.activites.HomeActivity;
import com.deepak.scavengerhunter.activites.HomeBaseActivity;


public class SplashActivity extends AppCompatActivity {
    private static boolean splashLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Log.d("SPLASH:",SharedPref.getUserInfo(SplashActivity.this).toString());
        // Delay
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(SharedPref.checkLoginStatus(SplashActivity.this)){
                    startActivity(new Intent(SplashActivity.this, HomeBaseActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                    finish();
                }

            }
        }, secondsDelayed * 1000);
    }
}
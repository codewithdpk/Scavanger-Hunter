package com.deepak.scavengerhunter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.activities.APIs.SharedPref;
import com.deepak.scavengerhunter.activities.activites.HomeActivity;

public class SplashActivity extends AppCompatActivity {
    private static boolean splashLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Delay
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(SharedPref.checkLoginStatus(SplashActivity.this)){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                    finish();
                }

            }
        }, secondsDelayed * 1000);
    }
}
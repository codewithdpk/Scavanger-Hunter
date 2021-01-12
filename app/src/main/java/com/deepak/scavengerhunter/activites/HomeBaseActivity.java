package com.deepak.scavengerhunter.activites;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.deepak.scavengerhunter.APIs.SharedPref;
import com.deepak.scavengerhunter.Fragments.DashboardFragment;
import com.deepak.scavengerhunter.Fragments.MyHuntsFragment;
import com.deepak.scavengerhunter.Fragments.ProfileFragment;
import com.deepak.scavengerhunter.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeBaseActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homebase_layout);
        init();



        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_dashboard:
                                openFragment(new DashboardFragment());
                                return true;
                            case R.id.navigation_sponsorships:
                                openFragment(new MyHuntsFragment());
                                return true;
                            case R.id.navigation_profile:
                                openFragment(new ProfileFragment());
                                return true;
                        }
                        return false;
                    }

                };

        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        openFragment(new DashboardFragment() );


    }



    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    private void init(){
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }
}

package com.deepak.scavengerhunter.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.deepak.scavengerhunter.APIs.SharedPref;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.SplashActivity;
import com.deepak.scavengerhunter.activites.MyHuntsActivity;
import com.deepak.scavengerhunter.classes.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    RelativeLayout myHunts;
    RelativeLayout layout_logout;
    TextView name;
    TextView email;
    ImageView profile_image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        init(rootView);

        JSONObject info = SharedPref.getUserInfo(getContext());
        //Log.d("USER_INFO",info.toString());
        try {
            name.setText(info.getString("name"));
            email.setText(info.getString("email"));
            if(!info.getString("image_url").equals("none")){
                Glide.with(getContext())
                        .asBitmap()
                        .load(info.getString("image_url"))
                        .into(profile_image);
            }else{
                profile_image.setImageResource(R.drawable.user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        layout_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedPref.logout(getContext())){
                    startActivity(new Intent(getContext(), SplashActivity.class));
                    getActivity().finish();
                }else{
                    Utils.createToast(getContext(),rootView,"Logout failed.");
                }
            }
        });


        myHunts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyHuntsActivity.class));
            }
        });

        return rootView;
    }

    private void init(View rootView){
        myHunts = rootView.findViewById(R.id.layout_My_hunts);
        name = rootView.findViewById(R.id.tv_name);
        email = rootView.findViewById(R.id.tv_email_user);
        layout_logout = rootView.findViewById(R.id.layout_logout);
        profile_image = rootView.findViewById(R.id.profile_image);
    }
}

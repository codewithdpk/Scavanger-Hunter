package com.deepak.scavengerhunter.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.activites.MyHuntsActivity;

public class ProfileFragment extends Fragment {

    RelativeLayout myHunts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        init(rootView);

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
    }
}

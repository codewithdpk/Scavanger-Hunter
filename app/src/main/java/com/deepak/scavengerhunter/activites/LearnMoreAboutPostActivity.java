package com.deepak.scavengerhunter.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.deepak.scavengerhunter.R;

public class LearnMoreAboutPostActivity extends AppCompatActivity {

    TextView txt_post_name;
    ImageView img_post_image;
    TextView txt_post_details;
    Button btn_take_quiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_more_about_post);
        init();
    }

    private void init(){
        txt_post_details = findViewById(R.id.txt_post_details);
        img_post_image = findViewById(R.id.img_post_image);
        txt_post_name = findViewById(R.id.txt_posts_name);
        btn_take_quiz = findViewById(R.id.btn_take_quiz);
    }
}
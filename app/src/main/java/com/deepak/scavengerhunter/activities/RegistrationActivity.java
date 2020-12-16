package com.deepak.scavengerhunter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.deepak.scavengerhunter.R;

public class RegistrationActivity extends AppCompatActivity {

    Button btn_signup;

    EditText edt_fullname;
    EditText edt_email;
    EditText edt_password;
    EditText edt_confirm_password;

    TextView txt_signin;


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
    }

    private void init(){
        btn_signup = (Button) findViewById(R.id.btn_signup);
        edt_fullname = (EditText) findViewById(R.id.edt_fullname);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_confirm_password = (EditText) findViewById(R.id.edt_confirm_password);
        txt_signin = (TextView) findViewById(R.id.txt_signin);
    }
}
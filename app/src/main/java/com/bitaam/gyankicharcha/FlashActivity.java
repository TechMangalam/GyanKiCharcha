package com.bitaam.gyankicharcha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class FlashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(mAuth.getCurrentUser()!=null){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }

            }
        },1000);

    }
}
package com.bitaam.gyankicharcha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView signInToUpTv;

    Button loginBtn;

    FirebaseAuth mAuth;

    ProgressBar progressBar;
    EditText userName,userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();
        signInToUpTv = findViewById(R.id.signInToUp);
        loginBtn = findViewById(R.id.LoginUser);

        progressBar = findViewById(R.id.progBar);
        userName = findViewById(R.id.email);
        userName.requestFocus();
        userPassword = findViewById(R.id.userP);


        onClickActivities();

    }

    private void onClickActivities() {

        signInToUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterNewUserActivity.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

    }
    private void userLogin(){

        String useremail = userName.getText().toString().trim();
        String userpassword = userPassword.getText().toString().trim();

        if(useremail.isEmpty()){

            userName.setError("phone no. is Empty");
            userName.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
            userName.setError("Please enter valid phone no");
            userName.requestFocus();
            return;
        }

        if(userpassword.isEmpty()){

            userPassword.setError("Password is Empty");
            userPassword.requestFocus();
            return;
        }

        if(userpassword.length()<6){
            userPassword.setError("Minimum length is 6");
            userPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(useremail,userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    userName.requestFocus();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

    }


}
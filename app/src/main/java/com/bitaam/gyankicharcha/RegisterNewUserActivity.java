package com.bitaam.gyankicharcha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.gyankicharcha.modals.UserModal;
import com.bitaam.gyankicharcha.utillity.Encryptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterNewUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView signUpToInTv;

    EditText nameEdt,emailEdt,phoneEdt,passEdt,passReEdt;

    Spinner countryMenu,stateMenu,interest1Menu,interest2Menu;

    Button registerBtn;

    ProgressBar progressBar;

    FirebaseAuth mAuth;
    FirebaseUser user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();

        signUpToInTv = findViewById(R.id.signUpToIn);
        nameEdt = findViewById(R.id.profileName);
        emailEdt = findViewById(R.id.email);
        phoneEdt = findViewById(R.id.phoneEdt);
        passEdt = findViewById(R.id.passwordEdt);
        passReEdt = findViewById(R.id.cPasswordEdt);
        interest1Menu = findViewById(R.id.interestsMenu);
        interest2Menu = findViewById(R.id.interests2Menu);
        countryMenu = findViewById(R.id.countryMenu);
        stateMenu = findViewById(R.id.stateMenu);
        registerBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progBarRegister);


        onClickActivities();

    }

    private void onClickActivities() {

        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.CountryList,android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryMenu.setAdapter(countryAdapter);
        countryMenu.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.StateList,android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateMenu.setAdapter(stateAdapter);
        stateMenu.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> inteAdapter1 = ArrayAdapter.createFromResource(getApplicationContext(),R.array.InterestList,android.R.layout.simple_spinner_item);
        inteAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interest1Menu.setAdapter(inteAdapter1);
        interest1Menu.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> inteAdapter2 = ArrayAdapter.createFromResource(getApplicationContext(),R.array.InterestList,android.R.layout.simple_spinner_item);
        inteAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interest2Menu.setAdapter(inteAdapter2);
        interest2Menu.setOnItemSelectedListener(this);

        signUpToInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private  void registerUser()
    {
        final String useremail = emailEdt.getText().toString().trim();
        String userpassword = passEdt.getText().toString().trim();
        String userpasswordC = passReEdt.getText().toString().trim();
        String phoneNo = phoneEdt.getText().toString().trim();
        String name = nameEdt.getText().toString().trim();

        if(name.isEmpty()){

            nameEdt.setError("Name is Empty");
            nameEdt.requestFocus();
            return;
        }

        if(useremail.isEmpty()){

            emailEdt.setError("Email is Empty");
            emailEdt.requestFocus();
            return;
        }

        if(!Patterns.PHONE.matcher(phoneNo).matches()){
            phoneEdt.setError("Please enter valid phone no.");
            phoneEdt.requestFocus();
            return;
        }

        if(userpassword.isEmpty()){

            passEdt.setError("Password is Empty");
            passEdt.requestFocus();
            return;
        }
        if(userpasswordC.isEmpty()){

            passReEdt.setError("Password is Empty");
            passReEdt.requestFocus();
            return;
        }

        if (!userpassword.contentEquals(userpasswordC)){
            passEdt.setText(null);
            passReEdt.setText(null);
            passEdt.setError("Re-enter ! Password and confirmation password not matched.");
            return;
        }

        if(userpassword.length()<6){
            passEdt.setError("Minimum length is 6");
            passEdt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(useremail,userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    setUserProfile(name,phoneNo,useremail,String.valueOf(countryMenu.getSelectedItem()),String.valueOf(stateMenu.getSelectedItem()),String.valueOf(interest1Menu.getSelectedItem()),String.valueOf(interest2Menu.getSelectedItem()));

                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    emailEdt.requestFocus();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setUserProfile(String name,String phone,String email,String country,String state,String inte1,String inte2){

        DatabaseReference databaseReference ;
        user =  FirebaseAuth.getInstance().getCurrentUser();
        Encryptions encryptions = new Encryptions();

        UserModal userModal = new UserModal();
        userModal.setName(encryptions.getEncreption(name,user.getUid(),user.getUid()));
        userModal.setEmail(encryptions.getEncreption(email,user.getUid(),user.getUid()));
        userModal.setPhoneNo(encryptions.getEncreption(phone,user.getUid(),user.getUid()));
        userModal.setCountry(encryptions.getEncreption(country,user.getUid(),user.getUid()));
        userModal.setState(encryptions.getEncreption(state,user.getUid(),user.getUid()));
        userModal.setInterest1(encryptions.getEncreption(inte1,user.getUid(),user.getUid()));
        userModal.setInterest2(encryptions.getEncreption(inte2,user.getUid(),user.getUid()));



        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        /*if (role.equals("User")){

        }else if (role.equals("Patanjali Chikitsalaya or store")){
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Patanjali Store");

        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Doctor");

        }*/



        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userModal.getName()).build();

        user.updateProfile(profileUpdates);


        databaseReference.setValue(userModal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
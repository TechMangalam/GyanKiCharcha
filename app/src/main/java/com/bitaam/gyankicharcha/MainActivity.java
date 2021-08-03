package com.bitaam.gyankicharcha;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.fragment.NavHostFragment;

import com.bitaam.gyankicharcha.Services.ChatBackgroundUpdateService;
import com.bitaam.gyankicharcha.Services.Restarter;
import com.bitaam.gyankicharcha.ui.MyCourse.MyCourseFragment;
import com.bitaam.gyankicharcha.ui.chat.ChatFragment;
import com.bitaam.gyankicharcha.ui.gyan.GyanFragment;
import com.bitaam.gyankicharcha.ui.post.PostFragment;
import com.bitaam.gyankicharcha.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    Fragment active;
    boolean homeFlag = true;
    AlertDialog.Builder builder;
    FirebaseAuth mAuth;
//    private ChatBackgroundUpdateService updateService;
//    Intent mServiceIntent;
//String encode = Base64.encodeToString(edit_query.getText().toString().getBytes(), Base64.DEFAULT);

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        builder = new AlertDialog.Builder(this);
        mAuth = FirebaseAuth.getInstance();

        navView = findViewById(R.id.nav_view);
        active  = new ChatFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment,active,"TAG").commit();

        navView.setSelectedItemId(R.id.navigation_chat);

        onClickActivities();

        requestForSpecificPermission();


//        File tarjeta = Environment.getExternalStorageDirectory();
//        Toast.makeText(this, tarjeta.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//        File logFile = new File(tarjeta.getAbsolutePath()+"/DCIM/", "log.txt");
//        if (!logFile.exists())
//        {
//            try
//            {
//                logFile.createNewFile();
//            }
//            catch (IOException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        try {
//            FileOutputStream fileout=openFileOutput("mytext.txt", MODE_PRIVATE);
//            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
//            outputWriter.write("textmsg.getText().toString()");
//            outputWriter.close();
//
//            //display file saved message
//            Toast.makeText(getBaseContext(), "File saved successfully!",
//                    Toast.LENGTH_SHORT).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        //saveToInternalStorage(((BitmapDrawable)getDrawable(R.drawable.profile_img)).getBitmap());
//        updateService = new ChatBackgroundUpdateService();
//        mServiceIntent = new Intent(this, updateService.getClass());
//        if (!isMyServiceRunning(updateService.getClass())) {
//            startService(mServiceIntent);
//        }




    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.FOREGROUND_SERVICE}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Grant PERMISSIONS from app info!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void onClickActivities(){

        String msg = "I am not encoded";

        String encode = Base64.encodeToString(msg.getBytes(), Base64.DEFAULT);
        Log.d("encoded",encode);
        String decode = Base64.decode(encode,Base64.DEFAULT).toString();
        Log.d("decoded",decode);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new ChatFragment();


                switch(item.getItemId()){

                    case R.id.navigation_chat:
                        fragment = new ChatFragment();
                        homeFlag = true;
                        //toolbar.setTitle("My Patanjali");
                        break;
                    case R.id.navigation_post:
                        fragment = new PostFragment();
                        homeFlag = false;
                        //toolbar.setTitle("Upchar");
                        //if (interstitialAd.isLoaded())
                        //interstitialAd.show();
                        break;
                    case R.id.navigation_gyan:
                        fragment = new GyanFragment();
                        homeFlag = false;
                        //toolbar.setTitle("Ask Doctor");
                        //if (interstitialAd.isLoaded())
                        //interstitialAd.show();
                        break;
                    case R.id.navigation_mycourse:
                        fragment = new MyCourseFragment();
                        homeFlag = false;
                        //toolbar.setTitle("Yoga");
                        //if (interstitialAd.isLoaded())
                        //interstitialAd.show();
                        break;
                    case R.id.navigation_profile:
                        fragment = new ProfileFragment();
                        homeFlag = false;
                        //toolbar.setTitle("Bhajan");
                        //if (interstitialAd.isLoaded())
                        //interstitialAd.show();
                        break;

                }

                Bundle bundle = new Bundle();
                bundle.putString("MyNo","8853486911");
                //assert fragment != null;
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,fragment,"TAG").remove(active).commit();
                active = fragment;//.hide(active)
                return true;
            }
        });





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.log_out) {
            AlertExit();
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }else if(id == R.id.share){

            Intent intent = new Intent(android.content.Intent.ACTION_SEND);

            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share app");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://drive.google.com/file/d/1utb25LaWEhP-vt0H2Gxrgh6nCEescJL3/view?usp=sharing \nOR\n  https://bitaam.online/EngTextScanner.html");

            startActivity(Intent.createChooser(intent, "Select app to share"));
        }

        return true;
    }

    public void AlertExit(){

        builder.setMessage("Do you really want to Logout?").setTitle("Confirmation")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(mAuth.getCurrentUser()!=null){
                            mAuth.signOut();
                            finish();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }

                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);



    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }
}
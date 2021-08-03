package com.bitaam.gyankicharcha.ui.profile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.audiofx.DynamicsProcessing;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bitaam.gyankicharcha.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    ImageView profileImgView;
    ProgressBar profProgressBar;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImgView = view.findViewById(R.id.profile_image);
        profProgressBar = view.findViewById(R.id.prof_progress_bar);
        profProgressBar.setVisibility(View.VISIBLE);

//        try {
//            downloadFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return view;
    }


    private static final long DOWNLOAD_LIMIT = 1024 * 1024; // you can change this

    public void downloadFile() throws IOException {

        String path = Environment.getExternalStorageDirectory().toString();
        File dir = new File(requireContext().getExternalFilesDir(null), "/Gyani/media/images/");

        File file = new File(dir, "filename" + ".jpg");
        File mydir = getContext().getDir("Newfolder", Context.MODE_WORLD_WRITEABLE); //Creating an internal dir;


        Toast.makeText(getContext(), mydir.getPath(), Toast.LENGTH_LONG).show();
        String imagePath =  file.getAbsolutePath();


        File localFile = File.createTempFile("sshot", "png");
        String url = "https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/Screenshot%20(1).png?alt=media&token=4892d416-081c-4d51-b289-f67604c5ef4a";

        //Glide.with(requireContext()).load(url).circleCrop().into(profileImgView);

        Glide.with(requireContext())
                .load(url).circleCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        profileImgView.setImageResource(R.drawable.profile_img);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        profProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(profileImgView);

        StorageReference ref = FirebaseStorage.getInstance().getReference("Screenshot (1).png");
//        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//
//            }
//        });
        ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                profileImgView.setImageURI(Uri.fromFile(file));
            }
        });
//        profileImgView.setImageURI(Uri.fromFile(localFile));
        //.addOnSuccessListener(new OnSuccessListener<byte[]>()
//        {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
//                        + "/DCIM/Camera/ss.png";
//                Toast.makeText(getContext(), path, Toast.LENGTH_LONG).show();
//
//                try {
//                    writeToFile(bytes, path);
//                } catch (IOException e) {
//
//                    e.printStackTrace();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//
//                Log.e(TAG, "fail to download: " + exception);
//            }
//        });
    }

    public void writeToFile(byte[] data, String fileName) throws IOException {
        FileOutputStream out = new FileOutputStream(fileName);

        out.write(data);
        out.close();
    }

}
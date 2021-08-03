package com.bitaam.gyankicharcha;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bitaam.gyankicharcha.modals.ChatViewTypeModel;
import com.bitaam.gyankicharcha.modals.PostModel;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;



import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CreatePostActivity extends AppCompatActivity {

    TextInputLayout postTextInputLayout;
    TextInputEditText postTextEditText;
    RelativeLayout postAttachmentRlt;
    FloatingActionButton postAttachmentFloatingActionBtn;
    ImageButton imgAttachmentBtn,cameraAttachmentBtn,videoAttachmentBtn,documentAttachmentBtn,customEmojiAttachmentBtn,visibilityAttachmentBtn;
    ShapeableImageView postImageView;
    VideoView postVideoView;
    PlayerView exoPlayerView;
    SimpleExoPlayer player;
    ImageButton uploadPostBtn,removeAttachedImageBtn;
    int postType;

    ArrayList<Uri> imgUriList;
    Uri imgUri;
    String offlinePath;
    ArrayList<String> dataUrls;
    ArrayList<String> offlineDataUrls;
    File fileVideo;


    ProgressDialog progressDialog;
    Uri fileUri;
    AlertDialog.Builder builder;

    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postTextInputLayout = findViewById(R.id.post_text_input_layout);
        postTextEditText = findViewById(R.id.post_text_text_input_field);
        postAttachmentFloatingActionBtn = findViewById(R.id.postAttachmentActionBtn);
        postAttachmentRlt = findViewById(R.id.attachmentContentRelLayout);
        imgAttachmentBtn = findViewById(R.id.imageAttachImageBtn);
        removeAttachedImageBtn = findViewById(R.id.removeAttachedImageBtn);
        videoAttachmentBtn = findViewById(R.id.videoAttachImageBtn);
        documentAttachmentBtn = findViewById(R.id.documentAttachImageBtn);
        cameraAttachmentBtn = findViewById(R.id.cameraAttachImageBtn);
        customEmojiAttachmentBtn = findViewById(R.id.emojiAttachImageBtn);
        visibilityAttachmentBtn = findViewById(R.id.visibilityAttachImageBtn);
        postImageView = findViewById(R.id.create_post_shapable_iv);
        postVideoView = findViewById(R.id.create_post_video_view);
        uploadPostBtn = findViewById(R.id.upload_post_btn);
        exoPlayerView  = findViewById(R.id.exoplayer_post);
        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        exoPlayerView.setPlayer(player);

        imgUriList = new ArrayList<>();
        dataUrls = new ArrayList<>();
        offlineDataUrls = new ArrayList<>();
        postType = PostModel.TEXT_TYPE;

        builder = new AlertDialog.Builder(this);

//        postTextEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //postTextInputLayout.setEndIconDrawable(R.drawable.btn_clear);
//                postTextInputLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        onClickActivities();

        //postImageView.setVisibility(View.VISIBLE);
        //Glide.with(this).asGif().load(R.raw.giphy).into(postImageView);

    }

    private void onClickActivities() {

        postAttachmentFloatingActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postAttachmentRlt.getVisibility() == View.VISIBLE){
                    postAttachmentRlt.setVisibility(View.GONE);
                }else{
                    postAttachmentRlt.setVisibility(View.VISIBLE);
                }
            }
        });

        imgAttachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUriList.size()>0){
                    postImageView.setImageURI(null);
                    imgUriList.clear();
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Select Image"), PostModel.IMAGE_TYPE);
            }
        });

        removeAttachedImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postImageView.setImageURI(null);
                offlinePath = null;
                imgUriList.clear();
                removeAttachedImageBtn.setVisibility(View.GONE);
                postImageView.setVisibility(View.GONE);
            }
        });

        cameraAttachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                offlinePath=null;
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, 111);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "com.bitaam.gyankicharcha.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 111);
                    }
                }

            }
        });

        videoAttachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Select video"),PostModel.IMAGE_VIDEO_TYPE);
            }
        });

        documentAttachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,"Select file"),ChatViewTypeModel.IMAGE_TYPE);
            }
        });

        visibilityAttachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //downloadFile(getApplicationContext(),"https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/raining5-7-21.mp4?alt=media&token=a0e496a1-d160-4c08-aab9-f92b35ac8dff");

                //downloadFile();
            }
        });

        uploadPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert();
                progressDialog.setMessage("Please Wait");
                if (postType == PostModel.IMAGE_TYPE && imgUriList.size()>0) {
                    try {
                        getImageFromStorage(imgUriList.get(0));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else if (postType == PostModel.IMAGE_VIDEO_TYPE){
                    uploadFileToDatabase(System.currentTimeMillis()+"_post.mp4",fileVideo,postType);
                }else {
                    createPostTextType();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            switch (requestCode){
                case 111:
                    if (offlinePath!=null){
                        //Bundle extras = data.getExtras();
                        //Bitmap imageBitmap = (Bitmap) extras.get("data");
                        postImageView.setVisibility(View.VISIBLE);
                        removeAttachedImageBtn.setVisibility(View.VISIBLE);
                        //postImageView.setImageBitmap(imageBitmap);
                        imgUriList.add(0,Uri.parse(offlinePath));
                        postImageView.setImageURI(Uri.parse(offlinePath));
                    }
                    break;
                case PostModel.IMAGE_TYPE:
                    assert data != null;
                    if (data.getClipData() != null) {

                        int countClipData = data.getClipData().getItemCount();
                        int currentImageSlect = 0;

                        while (currentImageSlect < countClipData) {

                            imgUri = data.getClipData().getItemAt(currentImageSlect).getUri();
                            imgUriList.add(0,imgUri);
                            //getImageFromStorage(imgUri);
                            currentImageSlect = currentImageSlect + 1;
                        }


                    }else if (data.getData()!=null){
                        imgUriList.add(0,data.getData());
                        //postImageView.setVisibility(View.VISIBLE);
                        //postImageView.setImageURI(data.getData());
                        //getImageFromStorage(data.getData());
                    }else {
                        imgUriList.clear();
                        Toast.makeText(this, "Please Select Images", Toast.LENGTH_SHORT).show();
                    }
                    if (imgUriList.size()>0 && (getFileSize(imgUriList.get(0))/(1024*1024))<=10){
                        exoPlayerView.setVisibility(View.GONE);
                        if (player.isPlaying()){
                            player.stop();
                            player.release();
                        }
                        postType = PostModel.IMAGE_TYPE;
                        removeAttachedImageBtn.setVisibility(View.VISIBLE);
                        postImageView.setVisibility(View.VISIBLE);
                        postImageView.setImageURI(imgUriList.get(0));

                    }else if(imgUriList.size()>0){
                        Toast.makeText(this, "Choose images having size less than 10 MB", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PostModel.IMAGE_VIDEO_TYPE:
                    assert data != null;
                    if (data.getData() != null){
                        Uri mVideoURI = data.getData();
                        //postVideoView.setVideoURI(mVideoURI);
//                        postVideoView.setVisibility(View.VISIBLE);
//                        MediaController mediaController = new MediaController(this);
//                        mediaController.setAnchorView(postVideoView);
//                        postVideoView.setMediaController(mediaController);
//                        exoPlayerView.setVisibility(View.VISIBLE);
//                        // Build the media item.
//                        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/raining5-7-21.mp4?alt=media&token=a0e496a1-d160-4c08-aab9-f92b35ac8dff"));
//// Set the media item to be played.
//                        player.setMediaItem(mediaItem);
//// Prepare the player.
//                        player.prepare();
//// Start the playback.
//                        player.play();

                        if ((getFileSize(mVideoURI)/(1024*1024))<=200) {
                            postImageView.setVisibility(View.GONE);
                            postImageView.setImageURI(null);
                            File file = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath());
                            dataUrls.clear();
                            postType = PostModel.IMAGE_VIDEO_TYPE;
                            alert();
                            progressDialog.setTitle("Compressing");
                            new CompressVideo().execute("false", mVideoURI.toString(), file.getPath());

                        }else{
                            Toast.makeText(this, "Choose file having size less than 100 MB", Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
            }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = System.currentTimeMillis()+"_Gyani";
        String imageFileName = "Image_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        offlinePath = image.getAbsolutePath();
        return image;
    }

    private double getFileSize(Uri fileUri) {
        Cursor returnCursor = getContentResolver().
                query(fileUri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        return returnCursor.getLong(sizeIndex);
    }

    private void getImageFromStorage(Uri uri) throws FileNotFoundException {

        Bitmap bmp = null;

        BitmapFactory.Options bitMapOption=new BitmapFactory.Options();
        bitMapOption.inJustDecodeBounds=true;
        BitmapFactory.decodeStream(
                this.getContentResolver().openInputStream(uri), null, bitMapOption);
        double imageWidth=bitMapOption.outWidth;
        double imageHeight=bitMapOption.outHeight;

        progressDialog.setMessage(imageHeight+"  "+imageWidth);


        try{
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            //bmp = rotateImage(bmp,90);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (imageHeight*2<imageWidth){
                bmp = rotateImage(bmp,-90);
            }else if(imageHeight<imageWidth) {
                bmp = rotateImage(bmp,90);
            }

            bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
            byte[] fileInBytes = baos.toByteArray();
            //byte[] fileRotedBytes = rotateImageIfRequired(getApplicationContext(),uri,fileInBytes);
            createFile(fileInBytes,uri);
            //uploadImageToFirebase(fileInBytes,offPath);



        }catch (Exception ignored){}


    }

    private void createFile(byte[] fileData,Uri uri) throws IOException {

        String offFilePath = null;
        String fileName = System.currentTimeMillis()+"_post.jpg";

        try {
            //Create directory..
            File rootPath = new File(getApplicationContext().getExternalFilesDir("")+"/Gyani/Gyani Images", "Post Images");

            if(!rootPath.exists()) {
                rootPath.mkdirs();
            }

            final File localFile = new File(rootPath,fileName);


            FileOutputStream out = new FileOutputStream(localFile);
            out.write(fileData);
            out.close();

            offFilePath = localFile.getAbsolutePath();

            uploadImageToFirebase(localFile,fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void uploadImageToFirebase(File file,String fileName) {

        //new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(new Date())+""+ FirebaseAuth.getInstance().getCurrentUser().getUid()+System.currentTimeMillis()+".jpg";
//        ImgId = imgId;
        final StorageReference fileRef = FirebaseStorage.getInstance().getReference("Post Images").child(currUser.getUid()).child(fileName);
        fileRef.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        dataUrls.add(0,uri.toString());
                        createPostFileType(file.getAbsolutePath(),dataUrls.get(0),PostModel.IMAGE_TYPE);
                        //sendTextMessage(getImageChat(uri.toString()));
//                        chatImgUrl = uri.toString();
//                        Picasso.get().load(uri)
//                                .into(queImg);
//                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                queImg.setVisibility(View.GONE);
//                queImg.setImageURI(null);
//                queImgUrls = null;
//                progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Failed Uploading Image",Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                //update post to database

                //progressDialog.dismiss();
            }
        });
    }

    private void uploadFileToDatabase(String fileName,File file,int type){

        alert();
        if (player.isPlaying()){
            player.stop();
        }
        player.release();
        progressDialog.setTitle("Posting");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);

        //File file = new File(offlinePath);
        String dataParent = null;
        if (type == PostModel.IMAGE_VIDEO_TYPE){
            dataParent = "Post Videos";
        }else{
            dataParent = "Post Documents";
        }

        final StorageReference fileRef = FirebaseStorage.getInstance().getReference(dataParent).child(currUser.getUid()).child(fileName);
        fileRef.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        dataUrls.add(0,uri.toString());
                        createPostFileType(file.getAbsolutePath(),dataUrls.get(0),PostModel.IMAGE_VIDEO_TYPE);
                        //sendTextMessage(getImageChat(uri.toString()));
//                        chatImgUrl = uri.toString();
//                        Picasso.get().load(uri)
//                                .into(queImg);
//                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setProgress((int)progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                queImg.setVisibility(View.GONE);
//                queImg.setImageURI(null);
//                queImgUrls = null;
//                progressBar.setVisibility(View.GONE);
                progressDialog.setMessage(e.getLocalizedMessage());
                Log.e("Failure",e.getLocalizedMessage());
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                //update post to database

                //progressDialog.dismiss();
            }
        });

    }

    private void createPostFileType(String offlinePath, String url, int type) {

        String dateTime = new SimpleDateFormat("MMM dd,yy h:mm aa", Locale.getDefault()).format(new Date());;
        PostModel model = new PostModel();
        model.setProfileName(currUser.getDisplayName());
        model.setProfileImageUrl("https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/Screenshot%20(1).png?alt=media&token=4892d416-081c-4d51-b289-f67604c5ef4a");
        model.setPostDate(dateTime);
        model.setPostVisibility(PostModel.VISIBILITY_PUBLIC);
        model.setPostType(type);
        model.setDataUrls(url);
        model.setOfflineDataUrls(offlinePath);

        if (!String.valueOf(postTextEditText.getText()).isEmpty()){
            model.setPostText(String.valueOf(postTextEditText.getText()));
        }
        model.setPostCategory(PostModel.CATEGORY_GENERAL);
        model.setPostAuthId(currUser.getUid());

        sendPostToDatabase(model);

    }

    private void createPostTextType(){

        String dateTime = new SimpleDateFormat("MMM dd,yy h:mm aa", Locale.getDefault()).format(new Date());;
        PostModel model = new PostModel();
        model.setProfileName(currUser.getDisplayName());
        model.setProfileImageUrl("https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/Screenshot%20(1).png?alt=media&token=4892d416-081c-4d51-b289-f67604c5ef4a");
        model.setPostDate(dateTime);
        model.setPostVisibility(PostModel.VISIBILITY_PUBLIC);
        model.setPostType(PostModel.TEXT_TYPE);

        if (!String.valueOf(postTextEditText.getText()).isEmpty()){
            model.setPostText(String.valueOf(postTextEditText.getText()));
        }else{
            postTextEditText.setError("We can not send empty post");
        }
        model.setPostCategory(PostModel.CATEGORY_GENERAL);
        model.setPostAuthId(currUser.getUid());

        sendPostToDatabase(model);

    }

    private void sendPostToDatabase(PostModel model) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        String key = databaseReference.push().getKey();

        assert key != null;
        databaseReference.child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnCompleteListener(task -> {
            progressDialog.setMessage("Posted");
            progressDialog.dismiss();
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        player.stop();
        player.release();
//        if (postTextEditText.isCursorVisible()){
//            //postTextEditText.setFocusable(false);
//            postTextEditText.setCursorVisible(false);
//            postTextInputLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
//            postTextInputLayout.setEndIconDrawable(R.drawable.ic_baseline_send_24);
//        }else{super.onBackPressed();}

    }

    private void downloadFile() {
        alert();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();//getReferenceFromUrl("<your_bucket>");
        StorageReference  islandRef = storageRef.child("raining5-7-21.mp4");

        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
        File primaryExternalStorage = externalStorageVolumes[0];



        File rootPath = new File(getApplicationContext().getExternalFilesDir("")+"/Gyani", "Gyani Videos");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,"test111.mp4");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CreatePostActivity.this, "Downloded", Toast.LENGTH_SHORT).show();
                Log.e("firebase ",";local tem file created  created " +localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull FileDownloadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setProgress((int)progress);
            }
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<FileDownloadTask.TaskSnapshot> task) {
                progressDialog.dismiss();
            }
        });
    }

    public void alert() {

        progressDialog = new ProgressDialog(CreatePostActivity.this);
        progressDialog.setTitle("Posting");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait ..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.setMax(100);
        progressDialog.show();
    }

    private static File saveVideoFile(Context context, String filePath) throws IOException {
        File videoFile = new File(filePath);
        String videoFileName = "" + System.currentTimeMillis() +".mp4";
        File rootPath;
//        if (Build.VERSION.SDK_INT < 30) {
//            rootPath = new File(Environment.getExternalStorageDirectory() +"/Gyani", "Gyani Videos");
//        }else {
            rootPath = new File(context.getExternalFilesDir(null) +"/Gyani", "Gyani Videos");
//        }
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        File desFile = new File(rootPath, videoFileName);
        if (desFile.exists()) {
            desFile.delete();
        }

        try {
            desFile.createNewFile();
        } catch (IOException var61) {
            var61.printStackTrace();
        }

        return desFile;

    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }



    @SuppressLint("StaticFieldLeak")
    private class CompressVideo extends AsyncTask<String,String,String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {

            String videoPath = null;

            try {
                Uri uri = Uri.parse(strings[1]);

                videoPath = SiliCompressor.with(CreatePostActivity.this)
                        .compressVideo(uri,strings[2]);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return videoPath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            fileVideo = new File(s);
            Uri uri = Uri.fromFile(fileVideo);
            MediaItem mediaItem = MediaItem.fromUri(uri);
            player.setMediaItem(mediaItem);
            player.prepare();
            exoPlayerView.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
            player.play();
            offlineDataUrls.add(0,uri.toString());
//            postVideoView.stopPlayback();
//            postVideoView.setVisibility(View.VISIBLE);
//            postVideoView.setVideoURI(uri);
//            postVideoView.start();

            float size = fileVideo.length()/1024f;

        }
    }



    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    private void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }


}//end of

    /*public void downloadFile(Context context, String url){
        alert();
        String mimeType = getMimeType(url);
        Glide.with(context).asFile().load(url).listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                saveFile(context,resource, mimeType);
                return false;
            }
        }).submit();
    }*/

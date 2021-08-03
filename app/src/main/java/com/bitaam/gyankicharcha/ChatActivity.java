package com.bitaam.gyankicharcha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.SparseBooleanArray;
import androidx.appcompat.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.gyankicharcha.adapters.ChatItemsAdapter;
import com.bitaam.gyankicharcha.modals.ChatViewTypeModel;
import com.bitaam.gyankicharcha.utillity.Encryptions;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements ChatItemsAdapter.ClickAdapterListener {

    EditText chatMsgEdt ;
    ImageButton chatSendImgViewBtn,chatAttachmentImgBtn,moreMenuChatBtn;
    RecyclerView chatRecyclerView;
    CircleImageView profileImgView;
    TextView profileNameTxtView,userStatusTextView;
    String friendName,friendNo,myNo;
    Encryptions encryptions;
    String chatId;
    ChatItemsAdapter adapter;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    RelativeLayout userInfoLayout,attachmentRelLayout;
    Toolbar toolbar;
    ArrayList<Uri> imgUriList;
    Uri imgUri;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        friendName = intent.getStringExtra("FriendName");
        friendNo = intent.getStringExtra("FriendNo");
        myNo = intent.getStringExtra("MyNo");

        encryptions = new Encryptions();

        getChatId(myNo,friendNo);

        imgUriList = new ArrayList<>();

        userInfoLayout = findViewById(R.id.userInfoLayout);
        attachmentRelLayout = findViewById(R.id.attachmentContentRelLayout);
        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.profile_icon);
        toolbar.setTitle(friendName);

        //toolbar.setNavigationIcon(R.drawable.profile_img);

//        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_USE_LOGO);
//        getSupportActionBar().setCustomView(R.layout.actionbar_chat);

        chatMsgEdt = findViewById(R.id.chat_msg_editTEXT);
        chatSendImgViewBtn = findViewById(R.id.chat_send_imgView);
        chatAttachmentImgBtn = findViewById(R.id.chat_attachment_imgView);
        moreMenuChatBtn = findViewById(R.id.moreMenuChat);
        profileNameTxtView = findViewById(R.id.profileName);
        userStatusTextView = findViewById(R.id.statusTv);
        profileImgView = findViewById(R.id.profile_image);

        chatRecyclerView = findViewById(R.id.queChatRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ChatItemsAdapter(new ArrayList<ChatViewTypeModel>(),getApplicationContext(),this,new SparseBooleanArray());
        chatRecyclerView.setAdapter(adapter);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        actionModeCallback = new ActionModeCallback();



        profileNameTxtView.setText(friendName);

        onClickActivities();


    }

    private void onClickActivities() {

        chatSendImgViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addNotification();
                if (chatMsgEdt.getText().toString().isEmpty()){
                    chatMsgEdt.setError("Write message to send !");
                    chatMsgEdt.requestFocus();
                }else{
                    sendTextMessage(getTextChat());
                }
            }
        });

        chatAttachmentImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attachmentRelLayout.getVisibility() == View.VISIBLE){
                    attachmentRelLayout.setVisibility(View.GONE);
                }else{
                    attachmentRelLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        CardView imgAttacmentCard = findViewById(R.id.image_attachment_card);
        CardView fileAttacmentCard = findViewById(R.id.file_attachment_card);
        CardView musicAttacmentCard = findViewById(R.id.music_attachment_card);
        CardView customEmojiAttacmentCard = findViewById(R.id.custom_emoji_attachment_card);
        CardView videoAttacmentCard = findViewById(R.id.video_attachment_card);
        CardView cameraAttacmentCard = findViewById(R.id.camera_attachment_card);

        imgAttacmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),ChatViewTypeModel.IMAGE_TYPE);
                attachmentRelLayout.setVisibility(View.GONE);
            }
        });

        fileAttacmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,"Select file"),ChatViewTypeModel.IMAGE_TYPE);
                attachmentRelLayout.setVisibility(View.GONE);
            }
        });

        musicAttacmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,"Select Audio"),ChatViewTypeModel.IMAGE_TYPE);
                attachmentRelLayout.setVisibility(View.GONE);
            }
        });

        cameraAttacmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Capture Image"),ChatViewTypeModel.IMAGE_TYPE);
                attachmentRelLayout.setVisibility(View.GONE);
            }
        });

        videoAttacmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Select video"),ChatViewTypeModel.IMAGE_TYPE);
                attachmentRelLayout.setVisibility(View.GONE);
            }
        });

        customEmojiAttacmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                startActivityForResult(Intent.createChooser(intent,"Select Emoji"),ChatViewTypeModel.IMAGE_TYPE);
                attachmentRelLayout.setVisibility(View.GONE);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {

            private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.item_selecter_color));


            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                //RecyclerData deletedCourse = recyclerDataArrayList.get(viewHolder.getAdapterPosition());

                // below line is to get the position
                // of the item at that position.
                int position = viewHolder.getAdapterPosition();
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
                //recyclerDataArrayList.remove(viewHolder.getAdapterPosition());

                // below line is to notify our item is removed from adapter.
                //recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());viewHolder.

                // below line is to display our snackbar with action.
                Snackbar.make(chatRecyclerView, "Replied", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // adding on click listener to our action of snack bar.
                        // below line is to add our item to array list with a position.
                        //recyclerDataArrayList.add(position, deletedCourse);

                        // below line is to notify item is
                        // added to our adapter class.
                        //recyclerViewAdapter.notifyItemInserted(position);
                    }
                }).show();

            }


            @Override
            public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);

            }
        }).attachToRecyclerView(chatRecyclerView);

    }

    private void sendTextMessage(ChatViewTypeModel textChat) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
        String msgId = reference.push().getKey();
        reference.keepSynced(true);
        reference.child(msgId).setValue(textChat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                chatMsgEdt.setText("");
                chatMsgEdt.requestFocus();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case ChatViewTypeModel.IMAGE_TYPE:
                if (resultCode == RESULT_OK) {


                    if (data.getClipData() != null) {

                        int countClipData = data.getClipData().getItemCount();
                        int currentImageSlect = 0;

                        while (currentImageSlect < countClipData) {

                            imgUri = data.getClipData().getItemAt(currentImageSlect).getUri();
                            imgUriList.add(imgUri);
                            getImageFromStorage(imgUri);
                            currentImageSlect = currentImageSlect + 1;
                        }


                    }else if (data.getData()!=null){
                        getImageFromStorage(data.getData());
                    }else {
                        Toast.makeText(this, "Please Select Images", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case ChatViewTypeModel.DOCUMENT_IMAGE_TYPE:
                if (resultCode == RESULT_OK){
                    File direct = new File(Environment.getExternalStorageDirectory()+"/GyanKiCharcha/Documents");

                    if(!direct.exists()) {
                        if(direct.mkdir()); //directory is created;
                    }else{

                    }
                }
        }

//        if(requestCode == 1000){
//            if(resultCode == Activity.RESULT_OK){
//                //progressBar.setVisibility(View.VISIBLE);
//                Uri imgUri = data.getData();
//                Bitmap bmp = null;
//                try{
//                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
//                }catch (Exception e){}
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
//                byte[] fileInBytes = baos.toByteArray();
////                queImg.setVisibility(View.VISIBLE);
////
////                queImg.setImageURI(imgUri);
//
//                uploadImageToFirebase(fileInBytes);
//            }
//        }

    }

    private void getFileFromStorage(Uri uri) throws IOException {

        File localFile = File.createTempFile("images", "jpg");


    }

    private void getImageFromStorage(Uri uri){

        Bitmap bmp = null;
        try{
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
            byte[] fileInBytes = baos.toByteArray();
            uploadImageToFirebase(fileInBytes);
        }catch (Exception e){}


    }

    private void uploadImageToFirebase(byte[] fileInBytes) {

        String imgId = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(new Date())+""+FirebaseAuth.getInstance().getCurrentUser().getUid()+System.currentTimeMillis()+".jpg";
//        ImgId = imgId;
        final StorageReference fileRef = FirebaseStorage.getInstance().getReference("Images").child(imgId);
        fileRef.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        sendTextMessage(getImageChat(uri.toString()));
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
                Toast.makeText(getApplicationContext(),"Failed Uploading Image",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getChatId(String mobU1, String mobU2){

        ArrayList<String> id1 = new ArrayList<String>();
        id1.add(mobU2+"@"+mobU1);
        id1.add(mobU1+"@"+mobU2);
                //{encryptions.getEncreption(mobU1, mobU2, mobU1) + "@" + encryptions.getEncreption(mobU2, mobU2, mobU1)};
        //mobU1+"@"+mobU2;
                //encryptions.getEncreption(mobU2,mobU1,mobU2)+"@"+encryptions.getEncreption(mobU1,mobU1,mobU2);


        DatabaseReference chatIdReference = FirebaseDatabase.getInstance().getReference("chats").child(mobU2+"@"+mobU1);
        chatIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    chatId = id1.get(0);
                    databaseActivities(chatId);
                    //Toast.makeText(getApplicationContext(), id[0], Toast.LENGTH_SHORT).show();
                }else {
                    chatId = id1.get(1);
                    databaseActivities(chatId);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });



    }


    private String getChatTime(){
        String dateTime = new SimpleDateFormat("MMM dd,yy h:mm aa", Locale.getDefault()).format(new Date());;


        return dateTime;
    }

    private ChatViewTypeModel getTextChat(){
        ChatViewTypeModel chatViewTypeModel = new ChatViewTypeModel();
        chatViewTypeModel.dateTime = getChatTime();
        chatViewTypeModel.authId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatViewTypeModel.type = ChatViewTypeModel.TEXT_TYPE;
        chatViewTypeModel.text = chatMsgEdt.getText().toString().trim();

        return chatViewTypeModel;
    }

    private ChatViewTypeModel getImageChat(String url){
        ChatViewTypeModel chatViewTypeModel = new ChatViewTypeModel();
        chatViewTypeModel.dateTime = getChatTime();
        chatViewTypeModel.authId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatViewTypeModel.type = ChatViewTypeModel.IMAGE_TYPE;
        chatViewTypeModel.text = "Image";
        chatViewTypeModel.dataUrl = url;

        return chatViewTypeModel;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public Bitmap createCircleBitmap(Bitmap bitmapimg){
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, 50,
                50);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(25,
                25, 25, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }

    private void databaseActivities(String chatId){


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ChatViewTypeModel model = snapshot.getValue(ChatViewTypeModel.class);
                if (!model.authId.equals(mUser.getUid())){
                    addNotification(friendName,model.text);
                }
                ((ChatItemsAdapter)chatRecyclerView.getAdapter()).update(model);
                chatRecyclerView.scrollToPosition(((ChatItemsAdapter)chatRecyclerView.getAdapter()).getItemCount()-1);

            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void addNotification(String friendName,String msg) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this,"channel_id")
                        .setSmallIcon(R.drawable.app_icon1_foreground)
                        .setAutoCancel(false)
                        .setVibrate(new long[]{1000, 1000, 1000,1000, 1000})
                        .setOnlyAlertOnce(true)
                        .setContentTitle(friendName)
                        .setContentText(msg);

        Intent notificationIntent = new Intent(this, ChatActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    "channel_id", "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(
                    notificationChannel);
        }
        manager.notify(0, builder.build());
    }

    private void getUpdate(String toString) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("encoding");

        String msg = toString;
        msg = new Encryptions().getEncreption(msg,"8853486911","9935853010");
        ref.child("encoded").setValue(msg);
//        String encode = Base64.encodeToString(msg.getBytes(), Base64.DEFAULT);
//        Log.d("encoded",encode);
//        byte[] decode = Base64.decode(encode,Base64.DEFAULT);
//        //Log.d("decoded",decode);
//        Log.d("check","testing for database");

        ref.child("decoded").setValue(new Encryptions().getDecreption(msg,"8853486911","9935853010"));

    }

    @Override
    public void onBackPressed() {
        if (actionMode != null){
            actionMode.finish();
            actionMode = null;
            adapter.clearSelections();
            userInfoLayout.setVisibility(View.VISIBLE);
        }else {
        super.onBackPressed();}
    }

    @Override
    public void onRowClicked(int position) {
        if (actionMode!=null){
            toggleSelection(position);

        }
    }

    @Override
    public void onRowLongClicked(int position) {
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
            userInfoLayout.setVisibility(View.GONE);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {

            actionMode.finish();
            actionMode = null;
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }



    private class ActionModeCallback implements ActionMode.Callback{


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.chat_actionbar_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {


                case R.id.item_delete:
                    // delete all the selected rows
                    //deleteRows();
                    Toast.makeText(ChatActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;

                case R.id.item_copy:
                    //updateColoredRows();
                    mode.finish();
                    return true;

                case R.id.item_forward:
                    //selectAll();
                    return true;

                case R.id.item_cancel:
                    //populateDataAndSetAdapter();
                    adapter.clearSelections();
                    mode.finish();
                    actionMode = null;
                    return true;

                default:
                    return false;

            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
                //adapter.clearSelections();
                actionMode = null;
                adapter.clearSelections();
                userInfoLayout.setVisibility(View.VISIBLE);

        }
    }


}
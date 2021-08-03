package com.bitaam.gyankicharcha.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.ContentResolver;

import com.bitaam.gyankicharcha.ChatActivity;
import com.bitaam.gyankicharcha.MainActivity;
import com.bitaam.gyankicharcha.R;
import com.bitaam.gyankicharcha.Services.ChatDatabaseFething;
import com.bitaam.gyankicharcha.UITestingActivity;
import com.bitaam.gyankicharcha.adapters.ChatItemsAdapter;
import com.bitaam.gyankicharcha.adapters.ChatUserListAdapter;
import com.bitaam.gyankicharcha.modals.ChatFriendModel;
import com.bitaam.gyankicharcha.modals.ChatViewTypeModel;
import com.bitaam.gyankicharcha.modals.UserModal;
import com.bitaam.gyankicharcha.utillity.Encryptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class ChatFragment extends Fragment {

    Toolbar toolbar;
    FloatingActionButton addNewContactActionBtn,storyActionBtn;
    String id,name,phoneNo,email,myNo,chatId;
    Encryptions encryptions;
    FirebaseUser mUser;
    Intent intent;
    RecyclerView chatUserListRecycler;
    ChatUserListAdapter adapter;
    UserModal myData;
    ArrayList<ChatFriendModel> chatFriendModels;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        encryptions = new Encryptions();

        chatFriendModels = new ArrayList<ChatFriendModel>();

//        if (getArguments() != null) {
//            myNo = getArguments().getString("MyNo");
//        }else{
//            myNo = "8853486911";
//        }

        getUserProfileData();


        //chatFriendModels.add(new ChatFriendModel("Ayush","8303104356","hello"));
        //chatFriendModels.add(new ChatFriendModel("Abhy","7865432312","hi"));

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("encoding");

        //String msg = "I am not encoded";
        //msg = new Encryptions().getEncreption(msg,"8853486911","9935853010");
        //ref.child("encoded").setValue(msg);
//        String encode = Base64.encodeToString(msg.getBytes(), Base64.DEFAULT);
//        Log.d("encoded",encode);
//        byte[] decode = Base64.decode(encode,Base64.DEFAULT);
//        //Log.d("decoded",decode);
//        Log.d("check","testing for database");

        //ref.child("decoded").setValue(new Encryptions().getDecreption(msg,"8853486911","9935853010"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);


        toolbar = view.findViewById(R.id.toolbarChat);
        addNewContactActionBtn = view.findViewById(R.id.addNewChatFriendActionBtn);
        storyActionBtn = view.findViewById(R.id.storyActionBtn);
        chatUserListRecycler = view.findViewById(R.id.chat_user_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        chatUserListRecycler.setLayoutManager(linearLayoutManager);
        adapter = new ChatUserListAdapter(chatFriendModels,getContext(),myNo);
        chatUserListRecycler.setAdapter(adapter);
        chatUserListRecycler.setItemAnimator(new DefaultItemAnimator());

        toolbar.setTitle("GyanKiCharcha");

        getFriendListFromDatabase();

        onClickActivities();



        return view;
    }



    private void getUserProfileData(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                myData = snapshot.child(mUser.getUid()).getValue(UserModal.class);
                myNo = encryptions.getDecreption(myData.getPhoneNo(),mUser.getUid(),mUser.getUid());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void getFriendListFromDatabase() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("FriendList");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ChatFriendModel model = snapshot.getValue(ChatFriendModel.class);
                ((ChatUserListAdapter) Objects.requireNonNull(chatUserListRecycler.getAdapter())).update(model,myNo);
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

    private void registerFriendInDatabase(String name,String no){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("FriendList");
        String id = reference.push().getKey();
        assert id != null;
        reference.child(id).setValue(new ChatFriendModel(name,no,"Welcome to charcha")).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "Error,Try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void onClickActivities() {

        addNewContactActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 123);
            }
        });

        storyActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UITestingActivity.class));
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (123):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        String name = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = getAccuratePhoneNo(numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                                name = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                //Toast.makeText(getContext(), "Number=" + num+"    "+name, Toast.LENGTH_LONG).show();
                                registerFriendInDatabase(name,num);
                                intent = new Intent(getContext(),ChatActivity.class);
                                intent.putExtra("FriendName",name);
                                intent.putExtra("FriendNo",num);
                                intent.putExtra("MyNo",myNo);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
                                return;
                            }
                        }
                    }
                    break;
                }
        }
    }

    private static String getAccuratePhoneNo(String no){

        no = no.replaceAll(" ","");
        if (no.length()>10){
            no = no.replaceAll("\\+","");
        }
        if (no.length()==11){
            no = no.substring(1);
        }else if(no.length()==12){
            no = no.substring(2);
        }


        return no;
    }

    private String getChatId(String mobU1,String mobU2){

        ArrayList<String> id1 = new ArrayList<String>();
        id1.add(mobU2+"@"+mobU1);
        id1.add(mobU1+"@"+mobU2);
        //{encryptions.getEncreption(mobU1, mobU2, mobU1) + "@" + encryptions.getEncreption(mobU2, mobU2, mobU1)};
        final String[] id = new String[1];//mobU1+"@"+mobU2;
        //encryptions.getEncreption(mobU2,mobU1,mobU2)+"@"+encryptions.getEncreption(mobU1,mobU1,mobU2);
//        boolean[] flag = {false};

        DatabaseReference chatIdReference = FirebaseDatabase.getInstance().getReference("chats");
        chatIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChild(id1.get(0))){
                    chatId = id1.get(1);
                    id[0] = id1.get(1);
                }else {
                    chatId = id1.get(0);
                    id[0] = id1.get(0);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return id[0];

    }



//    private void contactPicked(Intent data) {
//
//        Uri uri = data.getData();
//        Log.i(TAG, "contactPicked() uri " + uri.toString());
//        Cursor cursor;
//        ContentResolver cr = getActivity().getContentResolver();
//
//        try {
//            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//            if (null != cur && cur.getCount() > 0) {
//                cur.moveToFirst();
//                for (String column : cur.getColumnNames()) {
//                    Log.i(TAG, "contactPicked() Contacts column " + column + " : " + cur.getString(cur.getColumnIndex(column)));
//                }
//            }
//
//            if (cur.getCount() > 0) {
//                //Query the content uri
//                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//
//                if (null != cursor && cursor.getCount() > 0) {
//                    cursor.moveToFirst();
//                    for (String column : cursor.getColumnNames()) {
//                        Log.i(TAG, "contactPicked() uri column " + column + " : " + cursor.getString(cursor.getColumnIndex(column)));
//                    }
//                }
//
//                cursor.moveToFirst();
//                id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                Log.i(TAG, "contactPicked() uri id " + id);
//                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//                Log.i(TAG, "contactPicked() uri contact id " + contact_id);
//                // column index of the contact name
//                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                // column index of the phone number
//                phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                //get Email id of selected contact....
//                Log.e("ContactsFragment", "::>> " + id + name + phoneNo);
//
//                Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{contact_id}, null);
//
//                if (null != cur1 && cur1.getCount() > 0) {
//                    cur1.moveToFirst();
//                    for (String column : cur1.getColumnNames()) {
//                        Log.i(TAG, "contactPicked() Email column " + column + " : " + cur1.getString(cur1.getColumnIndex(column)));
//                        email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                    }
//
//                    //HERE YOU GET name, phoneno & email of selected contact from contactlist.....
//                    Log.e("setcontactDetails","::>>" + name+"\nPhoneno:" + phoneNo+"\nEmail: " + email);
//                } else {
//                    Log.e("setcontactDetails","::>>" + name+"\nPhoneno:" + phoneNo+"\nEmail: " + email);
//                }
//            }
//        } catch (IndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }
//    }


}
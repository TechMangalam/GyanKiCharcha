package com.bitaam.gyankicharcha.Services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bitaam.gyankicharcha.adapters.ChatUserListAdapter;
import com.bitaam.gyankicharcha.modals.ChatFriendModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public  class ChatDatabaseFething {

    ArrayList<ChatFriendModel> chatFriendModels;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public ChatDatabaseFething() {
        getFriendListFromDatabase();
    }

    private void getFriendListFromDatabase() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("FriendList");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ChatFriendModel model = snapshot.getValue(ChatFriendModel.class);
                chatFriendModels.add(model);
                //((ChatUserListAdapter) Objects.requireNonNull(chatUserListRecycler.getAdapter())).update(model,myNo);
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


}

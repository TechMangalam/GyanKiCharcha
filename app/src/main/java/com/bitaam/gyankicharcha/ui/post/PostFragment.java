package com.bitaam.gyankicharcha.ui.post;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitaam.gyankicharcha.CreatePostActivity;
import com.bitaam.gyankicharcha.R;
import com.bitaam.gyankicharcha.adapters.PostItemsAdapter;
import com.bitaam.gyankicharcha.modals.PostModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class PostFragment extends Fragment {

    RecyclerView postRecycler;
    FloatingActionButton createPostActionBtn;
    SwipeRefreshLayout postRefreshLayout;
    PostItemsAdapter postItemsAdapter;
    PostModel model;
    ArrayList<PostModel> models;
    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();


    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new PostModel();
        ArrayList<String> dataUrls = new ArrayList<String>();
        dataUrls.add("https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/imgonline-com-ua-compressed-Nhz4vVL9RFzIx.jpg?alt=media&token=df124c4c-c5bb-4a99-9875-ce77c067d38f");
        model.setDataUrls(dataUrls.get(0));
        model.setPostAuthId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        model.setPostCategory(PostModel.CATEGORY_GENERAL);
        model.setPostDate("June 28 ,2021 05:30 AM");
        model.setPostDevice("Android");
        model.setPostText(getString(R.string.large_text));
        model.setPostType(1);
        model.setPostVisibility(PostModel.VISIBILITY_PUBLIC);
        model.setProfileImageUrl("https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/Screenshot%20(1).png?alt=media&token=4892d416-081c-4d51-b289-f67604c5ef4a");
        model.setProfileName("Vaibhav Pandey");

        //models.add(model);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        postRefreshLayout = view.findViewById(R.id.post_swipe_refresh);
        createPostActionBtn = view.findViewById(R.id.addNewPostActionBtn);
        postRecycler = view.findViewById(R.id.post_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        SnapHelper snapHelper = new PagerSnapHelper();

        postRecycler.setLayoutManager(linearLayoutManager);
        snapHelper.attachToRecyclerView(postRecycler);

        postItemsAdapter = new PostItemsAdapter(new ArrayList<PostModel>(),getContext());
        postRecycler.setAdapter(postItemsAdapter);

        onClickActivities();

        getPostFromDatabase();

        return view;
    }

    private void onClickActivities(){
        postRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postItemsAdapter.notifyDataSetChanged();
                postRefreshLayout.setRefreshing(false);
            }
        });

        createPostActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreatePostActivity.class));
            }
        });

    }


    private void getPostFromDatabase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                PostModel model = snapshot.getValue(PostModel.class);
                ((PostItemsAdapter) Objects.requireNonNull(postRecycler.getAdapter())).update(model);
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
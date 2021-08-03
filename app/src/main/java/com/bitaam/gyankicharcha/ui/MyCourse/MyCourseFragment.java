package com.bitaam.gyankicharcha.ui.MyCourse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bitaam.gyankicharcha.R;
import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MyCourseFragment extends Fragment {

    private Menu menu;


    public MyCourseFragment() {
        // Required empty public constructor
    }

    public static MyCourseFragment newInstance(String param1, String param2) {
        MyCourseFragment fragment = new MyCourseFragment();
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

        View view = inflater.inflate(R.layout.fragment_my_course, container, false);

        AppBarLayout mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar_course);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption(R.id.action_info);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.action_info);
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.menu_scrolling, menu);
        hideOption(R.id.action_info);
        return;
    }



    private void hideOption(int id) {
        //MenuItem item = menu.findItem(id);
        //item.setVisible(false);
    }

    private void showOption(int id) {
        //MenuItem item = menu.findItem(id);
        //item.setVisible(true);
    }
}



package com.example.seoulapp.ui.home;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.seoulapp.ListViewAdapter;
import com.example.seoulapp.R;
import com.example.seoulapp.SearchActivity;

public class HomeFragment extends Fragment {

    private View root;
    private ListViewAdapter adapter;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        root = inflater.inflate(R.layout.fragment_home, container, false);


        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // 커스텀디자인 활성화
        actionBar.setDisplayShowCustomEnabled(true);
        // 툴바에 타이틀 비활성화
        actionBar.setDisplayShowTitleEnabled(false);
        // 툴바에 필터 버튼 활성화
        // actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_filter);

        View viewToolbar = getActivity().getLayoutInflater().inflate(R.layout.home_tool_bar, null);
        // ImageButton searchButton = viewToolbar.findViewById(R.id.search_btn);

        /*
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.stay, R.anim.stay);
            }
        });

         */


        toolbar.setBackgroundColor(Color.rgb(38,175,253));

        actionBar.setCustomView(viewToolbar, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT));

        return root;
    }
}


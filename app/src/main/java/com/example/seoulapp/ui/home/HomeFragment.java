package com.example.seoulapp.ui.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.seoulapp.ListViewAdapter;
import com.example.seoulapp.ListViewItem;
import com.example.seoulapp.R;
import com.example.seoulapp.SearchActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        // 툴바에 뒤로가기 버튼 활성화
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_filter);

        View viewToolbar = getActivity().getLayoutInflater().inflate(R.layout.home_tool_bar, null);
        ImageButton searchButton = viewToolbar.findViewById(R.id.search_btn);

        setHasOptionsMenu(true);

        ListViewAdapter adapter;

        // Adapter 생성
        adapter = new ListViewAdapter();

        FragmentManager fragmentManager = getFragmentManager();
        // 리스트뷰 참조 및 Adapter 닫기



        final CustomListFragment searchList = new CustomListFragment();
        final ListFragment searchShopList = new ListFragment();






        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_action_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_btn:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.stay, R.anim.stay);

            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
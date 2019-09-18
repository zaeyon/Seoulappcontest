package com.example.seoulapp.ui.notifications;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.seoulapp.R;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    ImageView ivSettings;
    ImageView ivNews;

    private ListView m_oListView = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        View v = inflater.inflate(R.layout.fragment_notifications, container, false);


        ivSettings = (ImageView) v.findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(new goSettings());
        ivNews = (ImageView) v.findViewById(R.id.ivNews);
        ivNews.setOnClickListener(new goNews());

        String[] strNumber = {"1", "2", "3"};
        int nNumCnt = 0;
        ArrayList<ItemData> oData = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            ItemData oItem = new ItemData();
            oItem.strShopName = "데이터" + (i+1);
            nNumCnt++;
            oData.add(oItem);
            if(nNumCnt >= strNumber.length) nNumCnt = 0;
        }

        m_oListView = (ListView) v.findViewById(R.id.listView);
        ListAdapter oAdapter = new ListAdapter(oData);
        m_oListView.setAdapter(oAdapter);

        // return root;
        return v;
    }

    class goSettings implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // final Intent intent1 = new Intent(getActivity(), SettingsActivity.class);
            Intent intentSettings = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intentSettings);
        }
    }

    class goNews implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentNews = new Intent(getActivity(), NewsActivity.class);
            startActivity(intentNews);
        }
    }
}
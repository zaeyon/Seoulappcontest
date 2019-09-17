package com.example.seoulapp.ui.notifications;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.seoulapp.R;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    ImageView ivProfile;
    ImageView ivSettings;
    ImageView ivNews;
    ImageView ivShop;

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

        ivProfile = (ImageView)v.findViewById(R.id.ivProfile);
        ivProfile.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            ivProfile.setClipToOutline(true);
        }

        // ivProfile에 현재 사용자 프로필 이미지
        // tvNickname에 현재 사용자 닉네임
        TextView tvNickname = (TextView)v.findViewById(R.id.tvNickname);
        // tvEmail에 현재 사용자 e-mail
        TextView tvEmail = (TextView)v.findViewById(R.id.tvEmail);
        // DB에서 현재 사용자의 id값과 일치하는 nickname과 email 추출
        // String(strNickname, strEmail)에 저장
        tvNickname.setText("닉네임"); // strNickname
        tvEmail.setText("email@naver.com"); // strEmail

        ivNews = (ImageView) v.findViewById(R.id.ivNews);
        ivNews.setOnClickListener(new goNews());
        ivShop = (ImageView) v.findViewById(R.id.ivShop);
        ivShop.setOnClickListener(new goShop());
        ivSettings = (ImageView) v.findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(new goSettings());

        // 즐겨찾기 리스트
        String[] strBookmark =  {"들락날락", "다래락", "라일락", "라운지오", "워커하우스"};
        ArrayList<ItemData> oData = new ArrayList<>();
        for (int i = 0; i < strBookmark.length; i++) {
            ItemData oItem = new ItemData();
            oItem.strShopName = strBookmark[i];
            oData.add(oItem);
        }

        m_oListView = (ListView) v.findViewById(R.id.listView);
        ListAdapter oAdapter = new ListAdapter(oData);
        m_oListView.setAdapter(oAdapter);

        // listview 클릭 시 각 매장 페이지로 이동(매장 id를 ShopDetaildInfo에 전달)

        // return root;
        return v;
    }

    class goNews implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentNews = new Intent(getActivity(), NewsActivity.class);
            startActivity(intentNews);
        }
    }

    class goShop implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentShop = new Intent(getActivity(), MyShopActivity.class);
            startActivity(intentShop);
        }
    }

    class goSettings implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentSettings = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intentSettings);
        }
    }
}
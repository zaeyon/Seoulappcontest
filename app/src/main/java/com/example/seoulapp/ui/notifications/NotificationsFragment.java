package com.example.seoulapp.ui.notifications;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;
import com.example.seoulapp.SettingShop;

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
import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    TextView tvNickname;

    ImageView ivProfile;
    ImageView ivSettings;
    ImageView ivNews;
    ImageView ivShopAdd;
    ImageView ivShopSetting;

    LinearLayout NoShopPage, YesShopPage;

    static String strEmail;
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

        SharedPreferences auto = this.getActivity().getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = auto.getString("inputId", "null");


        NoShopPage = v.findViewById(R.id.noShopPage);
        YesShopPage = v.findViewById(R.id.yesShopPage);

        ivProfile = (ImageView)v.findViewById(R.id.ivProfile);
        ivProfile.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            ivProfile.setClipToOutline(true);
        }

        // ivProfile에 현재 사용자 프로필 이미지
        // tvNickname에 현재 사용자 닉네임
        tvNickname = (TextView)v.findViewById(R.id.tvNickname);
        // tvEmail에 현재 사용자 e-mail
        TextView tvEmail = (TextView)v.findViewById(R.id.tvEmail);
        tvEmail.setText(strEmail); // strEmail
        Log.d("NotificationsFragment", "현재 사용자 : " + strEmail);
        // DB에서 현재 사용자의 id값과 일치하는 nickname과 email 추출
        // String(strNickname, strEmail)에 저장



        ivNews = (ImageView) v.findViewById(R.id.ivNews);
        ivNews.setOnClickListener(new goNews());
        ivSettings = (ImageView) v.findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(new goSettings());
        ivShopAdd = (ImageView)v.findViewById(R.id.ivAddShop);
        ivShopAdd.setOnClickListener(new goAddShop());
        ivShopSetting = (ImageView)v.findViewById(R.id.ivShopSetting);
        ivShopSetting.setOnClickListener(new goSettingShop());

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

        // 와이파이 새로 접속할 때마다 변경
        new JSONTask().execute("http://172.30.1.10:3000/getUserInfo");


        // listview 클릭 시 각 매장 페이지로 이동(매장 id를 ShopDetaildInfo에 전달)

        // return root;
        return v;
    }

    public class JSONTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌


                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String[] userInfo;
            userInfo = result.split("\\|");

            tvNickname.setText(userInfo[0]);
            String userProfileURL = "https://s3.ap-northeast-2.amazonaws.com/com.example.seoulapp/userProfileImage/" + userInfo[1];
            Glide.with(getContext()).load(userProfileURL).into(ivProfile);

            if(userInfo[2].equals("0"))
            {
                YesShopPage.setVisibility(INVISIBLE);
                NoShopPage.setVisibility(VISIBLE);
            }
            else{
                YesShopPage.setVisibility(VISIBLE);
                NoShopPage.setVisibility(INVISIBLE);
            }
            }
    }

    class goAddShop implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentAddShop = new Intent(getActivity(), AddShop.class);
            startActivity(intentAddShop);

        }
    }

    class goSettingShop implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentSettingShop = new Intent(getActivity(), SettingShop.class);
            startActivity(intentSettingShop);
        }
    }

    class goNews implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentNews = new Intent(getActivity(), NewsActivity.class);
            startActivity(intentNews);
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
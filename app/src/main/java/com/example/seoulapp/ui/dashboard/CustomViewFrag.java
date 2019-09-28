package com.example.seoulapp.ui.dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;

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

public class CustomViewFrag extends ListFragment {

    ListView lv;
    TextView tv_comment;
    ImageView User_img;
    ImageView onclick_heart;
    ImageView profile_img;
    TextView Commentmore;

    ReviewAdapter adapter;

    String[] User_Image;
    String[] User_Id;
    String[] User_Content;
    String[] Review_StoreName;
    String[] Review_Number;
    String[] Like;
    String[] allView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //추상화된 view를 실제적으로
        View getItemId = inflater.inflate(R.layout.review_listview_itemview, container, false);

        tv_comment = getItemId.findViewById(R.id.comment);
        User_img = (ImageView) getItemId.findViewById(R.id.User_img);
        onclick_heart = getItemId.findViewById(R.id.love_filed);
        profile_img = getItemId.findViewById(R.id.User_profilePicture);
        Commentmore = getItemId.findViewById(R.id.watching_comment);

        final ReviewAdapter adapter = new ReviewAdapter();
        setListAdapter(adapter);

        new JSONTaskUserProfile().execute("http://192.168.43.102:3000/getUserProfile");

        /* Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("CustomViewFrag");
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();*/

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void startOutput(){
        new JSONTaskUserProfile().execute("http://192.168.43.102:3000/getUserProfile");
    }



    //리뷰 불러오기
    public class JSONTaskUserProfile extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    //URL url = new URL("http://192.168.25.16:3000/users%22);
                    URL url = new URL(urls[0]);
                    //연결을 함
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

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

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
                            reader.close();//버퍼를 닫아줌
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
        protected void onPostExecute(String result) { //result 값은 2임.
            super.onPostExecute(result); //undefined라고 떠

            Log.d("CustomView", "UserProfile 성공");
            ReviewAdapter adapter = new ReviewAdapter();
            setListAdapter(adapter);

            allView = result.split("\\/"); //result = allView
            Log.e("1111111", String.valueOf(allView));
            User_Image = allView[0].split("\\|"); //유효 |
            User_Id = allView[1].split("\\|");
            User_Content = allView[2].split("\\|");
            Review_StoreName= allView[3].split("\\|");
            Like = allView[4].split("\\|");
            Review_Number = allView[5].split("\\|");

            //하나,둘 이렇게 되어있을 거거든요.

            for (int i = 0; i <User_Id.length; i++) {

                int a= User_Id.length-1;
                //String _number = Review_Number[a-i];
                String UserImg = User_Image[a-i];
                String UserId = User_Id[a-i];
                String UserContent = User_Content[a-i];
                String StoreName = Review_StoreName[a-i];
                String _Like = Like[a-i];
                String Number = Review_Number[a-i];

                int like = Integer.parseInt(_Like);
                int Numbering = Integer.parseInt(Number);
                adapter.addItem(UserImg, UserId, UserContent, StoreName, like, Numbering);
            }
            //comment_Id랑 comment를 commentAct로 넘겨야합니당..
        }

    }


}

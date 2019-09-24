package com.example.seoulapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

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

public class SearchActivity extends AppCompatActivity {

    ListView searchListView;
    // SearchListViewAdapter adapter;
    EditText searchEditText;
    String[] searchShopInfo;
    ImageView searchBtn;
    LinearLayout noResultPage;
    LinearLayout ResultPage;

    private ArrayList<SearchListViewItem> items = null;
    private SearchListViewAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        noResultPage = findViewById(R.id.noResultPage);
        ResultPage = findViewById(R.id.ResultPage);

        noResultPage.setVisibility(INVISIBLE);

        // Adapter 생성
        //adapter = new SearchListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        searchListView = findViewById(R.id.searchListView);
        searchEditText = findViewById(R.id.search_shopName);
        searchBtn = findViewById(R.id.search_button1);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                items = new ArrayList<SearchListViewItem>();
                adapter = new SearchListViewAdapter();
                searchListView.setAdapter(adapter);

                // 와이파이 새로 접속할 때마다 변경
                if(searchEditText.getText().toString().equals(""))
                {
                    ResultPage.setVisibility(INVISIBLE);
                    noResultPage.setVisibility(VISIBLE);
                }
                else {

                    new JSONTaskReq3().execute("http://172.30.1.10:3000/aa");
                }
            }
        });
    }

    public class JSONTaskReq3 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("searchShopName", searchEditText.getText());

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
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
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);



            searchListView.setAdapter(adapter);


            if (result.equals("noResult")) {

                noResultPage.setVisibility(VISIBLE);
                ResultPage.setVisibility(INVISIBLE);

            } else if (result.equals("error")) {

                noResultPage.setVisibility(VISIBLE);
                ResultPage.setVisibility(INVISIBLE);
            } else {
                noResultPage.setVisibility(INVISIBLE);
                ResultPage.setVisibility(VISIBLE);

                searchShopInfo = result.split("\\|");

                for(int i = 0; i < searchShopInfo.length/2; i++) {
                    String ImageUrl = "https://s3.ap-northeast-2.amazonaws.com/com.example.seoulapp/" + searchShopInfo[2*i+1];
                    adapter.addItem(ImageUrl, searchShopInfo[2*i]);

                }

            }
        }

    }
}

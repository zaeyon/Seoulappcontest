package com.example.seoulapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://dong-dong-c7d7e.appspot.com");


    int index;
    ListView searchListView;
    // SearchListViewAdapter adapter;
    EditText searchEditText;
    String[] searchShopInfo;
    ImageView searchBtn;
    LinearLayout noResultPage;
    LinearLayout ResultPage;
    String[] shopProfileImage;
    // public String imageUri;
    String[] searchShopProfile;
    String[] searchShopDetail;
    String fileUrl;
    private ArrayList<SearchListViewItem> items = null;
    private SearchListViewAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        noResultPage = findViewById(R.id.noResultPage);
        ResultPage = findViewById(R.id.ResultPage);

        noResultPage.setVisibility(INVISIBLE);

        fileUrl = "ss";
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
                    new JSONTaskReq3().execute("http://192.168.43.102:3000/aa");

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

                searchShopInfo = result.split("&&");
                searchShopProfile = searchShopInfo[0].split("\\$");
                searchShopDetail = searchShopInfo[1].split("\\|");

                for(int i = 0; i < searchShopProfile.length; i++)
                {
                    Log.d("searchShopProfile : ", searchShopProfile[i]);
                }

                for(int i = 0; i < searchShopDetail.length; i++)
                {
                    Log.d("searchShopDetail : ", searchShopDetail[i]);
                }


                StorageReference[] pathReference = new StorageReference[searchShopProfile.length];

                 for(index = 0; index < searchShopProfile.length; index++) {

                     pathReference[index] = storageReference.child("images/ShopProfileImage/" + searchShopProfile[index]);
                     pathReference[index].getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             Toast.makeText(getApplicationContext(), "다운로드 성공 : "+ uri, Toast.LENGTH_SHORT).show();
                              Uri downloadUrl = uri;
                              fileUrl = downloadUrl.toString();

                              }
                     });
                     Log.d("fileUrl", fileUrl);
                    }
            }
        }

    }
}

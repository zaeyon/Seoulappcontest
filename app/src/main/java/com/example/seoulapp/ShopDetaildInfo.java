package com.example.seoulapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

public class ShopDetaildInfo extends Activity implements OnClickListener {

    //레이아웃을 선언하기 위한 레이아웃 변수 선언
    LinearLayout feedPage, QAPage;

    String[] shopProduction;
    String[] shopProductionURL;
    int shopNumber;

    private int img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detaild_info);

        // 와이파이 새로 접속할때마다 변경

        new JSONTask2().execute("http://172.30.1.10:3000/shopNumber");
        new JSONTaskProduction().execute("http://172.30.1.10:3000/getShopProduction");

        // 선언한 변수에 생성한 레이아웃 설정


        // 버튼 클릭 이벤트 처리
       Intent intent = getIntent();

       ImageView shop_profile = findViewById(R.id.shop_profile);
      // ImageView shop_rep1 = findViewById(R.id.product_image1);
      // ImageView shop_rep2 = findViewById(R.id.product_image2);
      // ImageView shop_rep3 = findViewById(R.id.product_image3);
       TextView shop_name = findViewById(R.id.shop_name);
       TextView shop_building = findViewById(R.id.shop_building);
       TextView shop_floor = findViewById(R.id.shop_floor);
       TextView shop_rocation = findViewById(R.id.shop_rocation);
       TextView shop_category = findViewById(R.id.shop_category);
       TextView shop_style = findViewById(R.id.shop_style);
       TextView shop_intro = findViewById(R.id.shop_intro);


       //img=Integer.parseInt(intent.getStringExtra("profileImage"));
       //shop_profile.setImageResource(img);

        Glide.with(getApplicationContext()).load(intent.getStringExtra("profileImage")).into(shop_profile);
        //Glide.with(getApplicationContext()).load(intent.getStringExtra("representation1")).into(shop_rep1);
        //Glide.with(getApplicationContext()).load(intent.getStringExtra("representation2")).into(shop_rep2);
        //Glide.with(getApplicationContext()).load(intent.getStringExtra("representation3")).into(shop_rep3);
        shop_name.setText(intent.getStringExtra("name"));
       shop_building.setText(intent.getStringExtra("building"));
       shop_floor.setText(intent.getStringExtra("floor"));
       shop_rocation.setText(intent.getStringExtra("rocation"));
       shop_category.setText(intent.getStringExtra("category"));
       shop_style.setText(intent.getStringExtra("style"));
       shop_intro.setText(intent.getStringExtra("intro"));

        shop_profile.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            shop_profile.setClipToOutline(true);
        }
    }

    @Override
    public void onClick(View arg0){
        // TODO Auto-generated method stub

        // 버튼이 클릭 됐을 때 이벤트 처리

    }

    public class JSONTaskProduction extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopName", getIntent().getStringExtra("name"));

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
            shopProduction =  result.split("/");

            shopProductionURL = new String[shopProduction.length];

            for(int i = 0; i < shopProduction.length; i++){
               shopProductionURL[i] = "https://s3.ap-northeast-2.amazonaws.com/com.example.seoulapp/" + shopProduction[i];
            }

            for(int i = 0; i < shopProductionURL.length; i++){
                Log.d("shopProductionURL : ", shopProductionURL[i]);
            }

            GridView gridViewImages = (GridView)findViewById(R.id.gridViewImages);
            ImageGridAdapter imageGridAdapter = new ImageGridAdapter(ShopDetaildInfo.this, shopProductionURL);
            gridViewImages.setAdapter(imageGridAdapter);

        }
    }

    public class JSONTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopNumber", shopNumber);

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

            shopNumber = Integer.parseInt(result);
        }
    }
}

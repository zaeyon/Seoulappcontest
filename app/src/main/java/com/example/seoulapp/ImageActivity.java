package com.example.seoulapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ImageActivity extends AppCompatActivity {
    String[] productionImage;
    String[] productionInfo = new String[4];
    String productionURL;
    TextView productionName;
    TextView productionSize;
    TextView productionPrice;
    TextView productionIntro;
    ImageView binLike;
    ImageView canLike;
    String preUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.production_detail);

        // 확대되는 이미지를 보여주기 위해 ImageView 뷰를 설정합니다.
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        setImage(imageView);


        SharedPreferences auto = this.getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        preUserEmail = auto.getString("inputId", "null");

        productionName = findViewById(R.id.production_name);
        productionSize = findViewById(R.id.production_size);
        productionPrice = findViewById(R.id.production_price);
        productionIntro = findViewById(R.id.production_intro);

        binLike = findViewById(R.id.binLike);
        canLike = findViewById(R.id.canLike);

        binLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ImageActivity.this, "해당 상품이 내상품에 추가되었습니다.", Toast.LENGTH_SHORT).show();

                binLike.setVisibility(INVISIBLE);
                canLike.setVisibility(VISIBLE);

                new JSONTaskFavoriteProduction().execute("http://dongdong.com.ngrok.io/insertFavoriteProduction");


            }
        });

        canLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ImageActivity.this , "해당 상품이 내상품에서 제외되었습니다.", Toast.LENGTH_SHORT).show();

                binLike.setVisibility(VISIBLE);
                canLike.setVisibility(INVISIBLE);

                new JSONTaskFavoriteProduction().execute("http://dongdong.com.ngrok.io/deleteFavoriteProduction");

            }
        });
    }

    private void setImage(ImageView imageView) {

        // 이 액티비티는 getIntent 메소드를 호출하여 접근할 수 있습니다.
        Intent receivedIntent = getIntent();

        // 확대되는 이미지의 리소스 ID를 인텐트로부터 읽어들이고,
        // 그것을 ImageView 뷰의 이미지 리소스로 설정
        productionURL = (String) receivedIntent.getExtras().get("production");
        Glide.with(getApplicationContext()).load(productionURL).into(imageView);

        // 와이파이 접속할때마다 변경

        new JSONTaskProductionInfo().execute("http://dongdong.com.ngrok.io/getProductionInfo");

    }

    public class JSONTaskProductionInfo extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("productionURL", productionURL);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            productionInfo = result.split("\\|");

            for (int i = 0; i < 4; i++) {
                Log.d("productionInfo", productionInfo[i]);
            }

            productionName.setText(productionInfo[0]);
            productionSize.setText(productionInfo[1]);
            productionPrice.setText(productionInfo[2]);
            productionIntro.setText(productionInfo[3]);

            new JSONTaskFavoriteCheck().execute("http://dongdong.com.ngrok.io/getFavoriteProCheck");
        }

    }

    public class JSONTaskFavoriteProduction extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("productionName", productionName.getText());
                jsonObject.accumulate("productionPrice", productionPrice.getText());
                jsonObject.accumulate("productionIntro", productionIntro.getText());
                jsonObject.accumulate("userEmail", preUserEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    public class JSONTaskFavoriteCheck extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("userEmail", preUserEmail);
                jsonObject.accumulate("productionIntro", productionIntro.getText());
                jsonObject.accumulate("productionPrice", productionPrice.getText());

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String[] favoriteProduction;

            favoriteProduction = result.split("\\|");

            for(int i = 0; i < favoriteProduction.length; i++)
            {
                Log.d("favoriteProduction[i]", favoriteProduction[i]);
                Log.d("productionName", productionName.getText().toString());
                if(favoriteProduction[i].equals(productionName.getText().toString()))
                {
                    canLike.setVisibility(View.VISIBLE);
                    binLike.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}

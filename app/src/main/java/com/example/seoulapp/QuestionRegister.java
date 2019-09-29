package com.example.seoulapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

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

public class QuestionRegister extends AppCompatActivity {

    ArrayList<String> QnATypeList;
    ArrayAdapter<String> typeArrayAdapter;
    EditText RegQnAShopName;
    Spinner RegQnAType;
    EditText RegQnAProductionName;
    EditText RegQnAContent;
    EditText RegQnATitle;
    Button RegQnABtn;
   QnAListViewAdapter adapter;
    String preUserEmail;
    String[] QnAInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_register);

        SharedPreferences auto = this.getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        preUserEmail = auto.getString("inputId", "null");

        RegQnAShopName = findViewById(R.id.qnaRegShopName);
        RegQnAType = findViewById(R.id.qnaRegType);
        RegQnAProductionName = findViewById(R.id.qnaRegProduction);
        RegQnAContent = findViewById(R.id.qnaRegContent);
        RegQnABtn = findViewById(R.id.qnaRegBtn);
        RegQnATitle = findViewById(R.id.qnaRegTitle);

        RegQnAShopName.setText(getIntent().getStringExtra("shopName"));
        RegQnAShopName.setClickable(false);
        RegQnAShopName.setFocusable(false);

        ArrayList<QnAListViewItem> QnAListViewItemList = new ArrayList<QnAListViewItem>();

        QnATypeList = new ArrayList<>();
        QnATypeList.add("상품");
        QnATypeList.add("매장");
        QnATypeList.add("기타");

        typeArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, QnATypeList);

        RegQnAType.setAdapter(typeArrayAdapter);
        RegQnAType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        RegQnABtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new JSONTaskInsertQnA().execute("http://192.168.43.72:3000/InsertQnAInfo");


            }
        });
    }

    public class JSONTaskInsertQnA extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("RegQnAShopName", RegQnAShopName.getText());
                jsonObject.accumulate("RegQnAType", RegQnAType.getSelectedItem());
                jsonObject.accumulate("RegQnAProductionName", RegQnAProductionName.getText().toString());
                jsonObject.accumulate("RegQnAContent", RegQnAContent.getText());
                jsonObject.accumulate("RegQnATitle", RegQnATitle.getText());
                jsonObject.accumulate("RegQnAUserEmail", preUserEmail);
                Log.d("RegQnAUserEmail : ", preUserEmail);


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
            new JSONTaskQnA().execute("http://192.168.43.72:3000/getQnAInfo");

        }
    }

    public class JSONTaskQnA extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopName", RegQnAShopName.getText());

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

            if (result.equals("")) {

            }
            else {
                String[] QnA;
                QnA = result.split("&&&");

                adapter = new QnAListViewAdapter();

                ShopDetaildInfo.QnAListView.setAdapter(adapter);

                for (int i = 0; i < QnA.length; i++) {
                    QnAInfo = QnA[i].split("\\|");
                    for(int j = 0; j < QnAInfo.length; j++)
                    {
                        Log.d("QnAInfo : ", QnAInfo[j]);
                    }
                    adapter.addItem(QnAInfo[5], QnAInfo[6], QnAInfo[7], QnAInfo[4], QnAInfo[3], QnAInfo[1], QnAInfo[9]);

                }
            }
            onBackPressed();
        }
    }
}

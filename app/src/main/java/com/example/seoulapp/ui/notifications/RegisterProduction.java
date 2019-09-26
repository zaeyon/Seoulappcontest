package com.example.seoulapp.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.seoulapp.ClearEditText;
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

public class RegisterProduction extends AppCompatActivity {

    ImageView proRegImage;
    ClearEditText proRegTitle;
    ClearEditText proRegShopName;
    Spinner proRegType;
    EditText proRegIntro;
    EditText proRegSize;
    EditText proRegPrice;
    Button proRegBtn;
    String proUrl;

    ArrayList<String> productionTypeList;
    ArrayAdapter<String> productionTypeAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_production);

        proRegPrice = findViewById(R.id.proRegPrice);
        proRegSize = findViewById(R.id.proRegSize);
        proRegImage = findViewById(R.id.proRegImage);
        proRegTitle = findViewById(R.id.proRegTitle);
        proRegShopName = findViewById(R.id.proRegShopName);
        proRegType = findViewById(R.id.proRegType);
        proRegIntro = findViewById(R.id.proRegIntro);
        proRegBtn = findViewById(R.id.proRegBtn);
        proUrl = "";

        productionTypeList = new ArrayList<>();
        productionTypeList.add("상의");
        productionTypeList.add("하의");
        productionTypeList.add("가방");
        productionTypeList.add("신발");
        productionTypeList.add("기타");

        productionTypeAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productionTypeList);

        proRegType.setAdapter(productionTypeAdapter);


        proRegBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new JSONTaskInsertProduction().execute("http://172.30.1.10:3000/InsertProductionInfo");
            }
        });
    }

    public class JSONTaskInsertProduction extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("RegProShopName", "ShopName");
                jsonObject.accumulate("RegProType", proRegType.getSelectedItem().toString());
                jsonObject.accumulate("RegProTitle", proRegTitle.getText());
                jsonObject.accumulate("RegProIntro", proRegIntro.getText());
                jsonObject.accumulate("RegProImageUrl", proUrl);
                jsonObject.accumulate("RegProSize", proRegSize.getText());
                jsonObject.accumulate("RegProPrice", proRegPrice.getText());

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

            if(result.equals("production register faild"))
            {
              Toast toast = Toast.makeText(getApplicationContext(), "상품 등록에 실패했습니다.", Toast.LENGTH_LONG);
              toast.setGravity(Gravity.CENTER, 0, 0);
              toast.show();
            }
            else if(result.equals("production register success"))
            {
                Toast toast = Toast.makeText(getApplicationContext(), "상품이 등록되었습니다.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }
}

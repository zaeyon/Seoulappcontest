package com.example.seoulapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import static android.view.View.VISIBLE;


public class Login extends Activity {

    static int shopNumber;
    public static String[] shopName;
    static String[] shopProfileImage;
    static String[] shopBuilding;
    static String[] shopFloor;
    static String[] shopRocation;
    static String[] shopStyle;
    static String[] shopCategory;
    static String[] shopRepresentation1;
    static String[] shopRepresentation2;
    static String[] shopRepresentation3;

    Button loginBtn;
    EditText userEmail;
    EditText userPassword;
    TextView emailWrong;
    TextView notExistEmail;
    TextView wrongPassword;

    boolean emailCheck = false;
    boolean passwordCheck = false;
    boolean PWrong = false;
    boolean EWrong = false;

    public int getShopNumber() {
        return shopNumber;
    }



    TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {

            String s = edit.toString();
            loginBtn = findViewById(R.id.loginButton);
            emailCheck = true;
            EWrong = false;

            if(!PWrong && !EWrong && s.length() > 0 && emailCheck && passwordCheck)
            {
                loginBtn.setEnabled(true);
            }
            else if(s.length() <= 0)
            {
                emailCheck = false;
                loginBtn.setEnabled(false);
            }
            else if(!emailCheck)
            {
                loginBtn.setEnabled(false);
            }
            else if(!passwordCheck)
            {
                loginBtn.setEnabled(false);
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            emailWrong.setVisibility(View.INVISIBLE);
            notExistEmail.setVisibility(View.INVISIBLE);
        }
    };
    TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {

            String s = edit.toString();
            loginBtn = findViewById(R.id.loginButton);
            passwordCheck = true;
            PWrong = false;

            if(!PWrong && !EWrong &&s.length() > 0 && emailCheck && passwordCheck)
            {
                loginBtn.setEnabled(true);
            }
            else if(s.length() <= 0)
            {
                passwordCheck = false;
                loginBtn.setEnabled(false);
            }
            else if(!emailCheck)
            {
                loginBtn.setEnabled(false);
            }
            else if(!passwordCheck)
            {
                loginBtn.setEnabled(false);
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            wrongPassword.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.emailLogin);
        userPassword = findViewById(R.id.passwordLogin);
        loginBtn = findViewById(R.id.loginButton);
        emailWrong = findViewById(R.id.wrongEmailText);
        notExistEmail = findViewById(R.id.notExistEmail);
        wrongPassword = findViewById(R.id.wrongPasswordText);

        loginBtn.setEnabled(false);
        emailWrong.setVisibility(View.INVISIBLE);
        notExistEmail.setVisibility(View.INVISIBLE);
        wrongPassword.setVisibility(View.INVISIBLE);



        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
<<<<<<< HEAD
                    // 동방 와이파이
                    new JSONTask().execute("http://192.168.43.72:3000/emailCheck");
                    // 할리스 와이파이
                    // new JSONTask().execute("http://192.168.1.225:3000/emailCheck");
                    // 부경대 PKNU-WLAN 와이파이
                    // new JSONTask().execute("http://14.44.114.26:3000/emailCheck");
                    // new JSONTask().execute("http://14.44.114.12:3000/emailCheck");
                    // new JSONTask().execute("http://14.44.119.220:3000/emailCheck");
                    // new JSONTask().execute("http://14.44.112.144:3000/emailCheck");
                   //  new JSONTask().execute("http://14.44.118.177:3000/emailCheck");
                  // 리마크빌
                    //   new JSONTask().execute("http://172.30.1.10:3000/emailCheck");
                  // 망고
                  //  new JSONTask().execute("http://192.168.0.26:3000/emailCheck");
                  //  new JSONTask().execute("http://04a17171.ngrok.io/emailCheck");
                 // new JSONTask().execute("http://192.168.43.29:3000/emailCheck");
=======
                    // 와이파이 새로 접속할 때마다 변경
                     new JSONTask().execute("http://192.168.43.102:3000/emailCheck");
>>>>>>> 4e71ec9e7d4782511cde317f497f6877d9345542
                }

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
<<<<<<< HEAD
                // 동방 와이파이
               new JSONTask().execute("http://192.168.43.72:3000/login");
                new JSONTask2().execute("http://192.168.43.72:3000/shopNumber");
                // 할리스 와이파이
                // new JSONTask().execute("http://192.168.1.225:3000/login");
                // new JSONTask2().execute("http://192.168.1.225:3000/shopNumber");

                // 부경대 PKNU-WLAN 와이파이
                // new JSONTask().execute("http://14.44.114.26:3000/login");
                // new JSONTask().execute("http://14.44.119.220:3000/login");
                // new JSONTask().execute("http://14.44.119.220:3000/shopNumber");
                // WLAN A15
                //new JSONTask().execute("http://14.44.114.12:3000/login");
                // WLAN A12
                // new JSONTask().execute("http://14.44.112.144:3000/login");
                // new JSONTask().execute("http://14.44.118.177:3000/login");
                // 리마크빌
                // new JSONTask().execute("http://172.30.1.10:3000/login");
                // new JSONTask2().execute("http://172.30.1.10:3000/shopNumber");
                // 망고
                //new JSONTask().execute("http://192.168.0.26:3000/login");
                //new JSONTask2().execute("http://192.168.0.26:3000/shopNumber");
                // new JSONTask().execute("http://04a17171.ngrok.io/login");
                // new JSONTask().execute("http://04a17171.ngrok.io/shopNumber");
                // new JSONTask().execute("http://192.168.43.29:3000/login");
               // new JSONTask().execute("http://192.168.43.29:3000/shopNumber");


=======
                // 와이파이 새로 접속할 때마다 변경
                 new JSONTask().execute("http://192.168.43.102:3000/login");
                 new JSONTask().execute("http://192.168.43.102:3000/shopNumber");
>>>>>>> 4e71ec9e7d4782511cde317f497f6877d9345542
            }
        });

        userEmail.addTextChangedListener(emailTextWatcher);
        userPassword.addTextChangedListener(passwordTextWatcher);

    }





    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("userEmail", userEmail.getText());
                jsonObject.accumulate("userPassword", userPassword.getText());

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

            if(result != null && result.equals("emailWrong"))
            {
                emailWrong.setVisibility(VISIBLE);
                loginBtn.setEnabled(false);
            }
            else if(result != null && result.equals("emailCorrect"))
            {
                emailWrong.setVisibility(View.INVISIBLE);
            }

            if(result != null && result.equals("passwordNotMatch")) {

                wrongPassword.setVisibility(View.VISIBLE);
                notExistEmail.setVisibility(View.INVISIBLE);
                loginBtn.setEnabled(false);
                PWrong = true;
            }

            else if(result != null && result.equals("emailNotExist"))
            {
                wrongPassword.setVisibility(View.INVISIBLE);
                notExistEmail.setVisibility(View.VISIBLE);
                loginBtn.setEnabled(false);
                EWrong = true;
            }
            else if(result != null && result.equals("loginSuccess"))
            {
                Intent navigationIntent = new Intent(Login.this ,BottomNavigation.class);
                startActivity(navigationIntent);
            }
        }
    }

    public class JSONTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("userEmail", userEmail.getText());
                jsonObject.accumulate("userPassword", userPassword.getText());

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
            shopName = new String[shopNumber];
            shopProfileImage = new String[shopNumber];
            shopBuilding = new String[shopNumber];
            shopFloor = new String[shopNumber];
            shopRocation = new String[shopNumber];
            shopStyle = new String[shopNumber];
            shopCategory = new String[shopNumber];
            shopRepresentation1 = new String[shopNumber];
            shopRepresentation2 = new String[shopNumber];
            shopRepresentation3 = new String[shopNumber];

<<<<<<< HEAD
=======
            // 와이파이 새로 접속할 때마다 변경
             new JSONTask().execute("http://192.168.43.102/getShopName");

>>>>>>> 4e71ec9e7d4782511cde317f497f6877d9345542
        }
    }

    public class JSONTask3 extends AsyncTask<String, String, String> {

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

            shopName = result.split("/");
            Log.d("shop",shopName[0]);
        }
    }

}

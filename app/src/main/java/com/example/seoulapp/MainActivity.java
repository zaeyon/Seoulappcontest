package com.example.seoulapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    EditText userEmail;
    EditText userPassword;
    TextView emailWrong;
    TextView notExistEmail;
    TextView wrongPassword;
    public static final String name = "auto";

    public String loginId, loginPwd;

    boolean emailCheck = false;
    boolean passwordCheck = false;
    boolean PWrong = false;
    boolean EWrong = false;

    EditText id, pwd;
    boolean certification = false;

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
        setContentView(R.layout.activity_main);


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

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        loginId = auto.getString("inputId", null);
        loginPwd = auto.getString("inputPwd", null);

        if(loginId != null && loginPwd != null){
            Intent intent = new Intent(MainActivity.this, BottomNavigation.class);
            Log.d("MainActivity", "현재 사용자:" + loginId);
            startActivity(intent);
            finish();
        }

        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    // 와이파이 새로 접속할 때마다 변경
                    new JSONTask().execute("http://172.30.1.14:3000/emailCheck");
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 와이파이 새로 접속할 때마다 변경
                new JSONTask().execute("http://172.30.1.14:3000/login");
            }
        });

        userEmail.addTextChangedListener(emailTextWatcher);
        userPassword.addTextChangedListener(passwordTextWatcher);



    }

    public void onClickSignup(View view)
    {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
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
                if(loginId == null && loginPwd == null)
                {
                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor autoLogin = auto.edit();
                    autoLogin.putString("inputId", userEmail.getText().toString());
                    autoLogin.putString("inputPwd", userPassword.getText().toString());

                    autoLogin.commit();
                }

                Intent navigationIntent = new Intent(MainActivity.this ,BottomNavigation.class);
                startActivity(navigationIntent);
            }
        }
    }
}

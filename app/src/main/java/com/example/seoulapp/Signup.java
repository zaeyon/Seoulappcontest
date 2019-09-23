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

public class Signup extends Activity {

    EditText nicknameData;
    EditText emailData;
    EditText passwordData;
    EditText passwordConfirmData;
    Button signupBtn;
    TextView nicknamePo;
    TextView nicknameIm;
    TextView emailPo;
    TextView emailIm;
    TextView emailWr;
    TextView passwordPo;
    TextView passwordIm;

    static String nicknameData1;
    static String emailData1;
    static String passwordData1;
    static String passwordConfirmData1;


    int inputCount = 0;
    boolean nicknameCheck = false;
    boolean emailCheck = false;
    boolean passwordCheck = false;
    boolean passwordConfirmCheck = false;
    boolean emailWrongCheck = false;
    boolean nicknameWrongCheck = false;


    boolean passwordWrongCheck = false;

    TextWatcher nicknameTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {

            String s = edit.toString();
            signupBtn = findViewById(R.id.signupFinish);
            nicknameCheck = true;

            if (!passwordWrongCheck && !nicknameWrongCheck && !emailWrongCheck && s.length() > 0 && nicknameCheck && emailCheck && passwordCheck && passwordConfirmCheck) {
                signupBtn.setEnabled(true);
            } else if (s.length() <= 0) {
                nicknameCheck = false;
                signupBtn.setEnabled(false);
            } else if (!nicknameCheck) {
                signupBtn.setEnabled(false);
            } else if (!emailCheck) {
                signupBtn.setEnabled(false);
            } else if (!passwordCheck) {
                signupBtn.setEnabled(false);
            } else if (!passwordConfirmCheck) {
                signupBtn.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            nicknameIm.setVisibility(View.INVISIBLE);
            nicknamePo.setVisibility(View.INVISIBLE);
        }
    };

    TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {

            String s = edit.toString();
            signupBtn = findViewById(R.id.signupFinish);
            emailCheck = true;
            if (!passwordWrongCheck && !nicknameWrongCheck && !emailWrongCheck && s.length() > 0 && nicknameCheck && emailCheck && passwordCheck && passwordConfirmCheck) {
                signupBtn.setEnabled(true);
            } else if (s.length() <= 0) {
                emailCheck = false;
                signupBtn.setEnabled(false);
            } else if (!nicknameCheck) {
                signupBtn.setEnabled(false);
            } else if (!emailCheck) {
                signupBtn.setEnabled(false);
            } else if (!passwordCheck) {
                signupBtn.setEnabled(false);
            } else if (!passwordConfirmCheck) {
                signupBtn.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            emailPo.setVisibility(View.INVISIBLE);
            emailIm.setVisibility(View.INVISIBLE);
        }
    };

    TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {

            String s = edit.toString();
            signupBtn = findViewById(R.id.signupFinish);
            passwordCheck = true;
            if (!passwordWrongCheck && !nicknameWrongCheck && !emailWrongCheck && s.length() > 0 && nicknameCheck && emailCheck && passwordCheck && passwordConfirmCheck) {
                signupBtn.setEnabled(true);
            } else if (s.length() <= 0) {
                passwordCheck = false;
                signupBtn.setEnabled(false);
            } else if (!nicknameCheck) {
                signupBtn.setEnabled(false);
            } else if (!emailCheck) {
                signupBtn.setEnabled(false);
            } else if (!passwordCheck) {
                signupBtn.setEnabled(false);
            } else if (!passwordConfirmCheck) {
                signupBtn.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };

    TextWatcher passwordConfirmTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {


            String s = edit.toString();
            signupBtn = findViewById(R.id.signupFinish);
            passwordConfirmCheck = true;
            if (!passwordWrongCheck && !nicknameWrongCheck && !emailWrongCheck && s.length() > 0 && nicknameCheck && emailCheck && passwordCheck && passwordConfirmCheck) {
                signupBtn.setEnabled(true);
            } else if (s.length() <= 0) {
                passwordConfirmCheck = false;
                signupBtn.setEnabled(false);
            } else if (!nicknameCheck) {
                signupBtn.setEnabled(false);
            } else if (!emailCheck) {
                signupBtn.setEnabled(false);
            } else if (!passwordCheck) {
                signupBtn.setEnabled(false);
            } else if (!passwordConfirmCheck) {
                signupBtn.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            passwordIm.setVisibility(View.INVISIBLE);
            if (passwordConfirmData.getText().toString().equals(passwordData.getText().toString())) {
                passwordPo.setVisibility(View.VISIBLE);
                passwordIm.setVisibility(View.INVISIBLE);
                passwordWrongCheck = false;
                if (!nicknameWrongCheck && !emailWrongCheck && nicknameCheck && emailCheck && passwordCheck && passwordConfirmCheck) {
                    signupBtn.setEnabled(true);
                }
            } else {
                passwordPo.setVisibility(View.INVISIBLE);
                signupBtn.setEnabled(false);
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nicknameData = findViewById(R.id.nicknameInput);
        emailData = findViewById(R.id.emailInput);
        passwordData = findViewById(R.id.passwordInput);
        passwordConfirmData = findViewById(R.id.passwordConfirmInput);
        signupBtn = findViewById(R.id.signupFinish);
        nicknamePo = findViewById(R.id.nicknameText);
        nicknameIm = findViewById(R.id.nicknameText2);
        emailPo = findViewById(R.id.emailText);
        emailIm = findViewById(R.id.emailText2);
        emailWr = findViewById(R.id.emailText3);
        passwordPo = findViewById(R.id.passwordConfirmText);
        passwordIm = findViewById(R.id.passwordConfirmText2);

        signupBtn.setEnabled(false);
        nicknamePo.setVisibility(View.INVISIBLE);
        nicknameIm.setVisibility(View.INVISIBLE);
        emailPo.setVisibility(View.INVISIBLE);
        emailIm.setVisibility(View.INVISIBLE);
        emailWr.setVisibility(View.INVISIBLE);
        passwordPo.setVisibility(View.INVISIBLE);
        passwordIm.setVisibility(View.INVISIBLE);

        nicknameData.addTextChangedListener(nicknameTextWatcher);
        emailData.addTextChangedListener(emailTextWatcher);
        passwordData.addTextChangedListener(passwordTextWatcher);
        passwordConfirmData.addTextChangedListener(passwordConfirmTextWatcher);


        nicknameData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    // 와이파이 새로 접속할 때마다 변경


                    new JSONTask().execute("http://172.30.1.10:3000/nickname");

                }
            }
        });

        emailData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    // 와이파이 새로 접속할 때마다 변경

                    new JSONTask().execute("http://172.30.1.10:3000/email");

                }
            }
        });

        passwordData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (!passwordConfirmData.getText().toString().equals("") && !passwordData.getText().toString().equals("") && (passwordConfirmData.getText().toString().equals(passwordData.getText().toString()))) {
                        passwordPo.setVisibility(View.VISIBLE);
                        passwordIm.setVisibility(View.INVISIBLE);
                        passwordWrongCheck = false;
                        if (!passwordWrongCheck && !nicknameWrongCheck && !emailWrongCheck && nicknameCheck && emailCheck && passwordCheck && passwordConfirmCheck) {
                            signupBtn.setEnabled(true);
                        }

                    } else {
                        passwordPo.setVisibility(View.INVISIBLE);
                        passwordIm.setVisibility(View.INVISIBLE);
                        passwordWrongCheck = true;
                        signupBtn.setEnabled(false);

                    }
                }
            }
        });

        passwordConfirmData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (!passwordConfirmData.getText().toString().equals("") && !passwordData.getText().toString().equals("") && passwordConfirmData.getText().toString().equals(passwordData.getText().toString())) {
                        passwordPo.setVisibility(View.VISIBLE);
                        passwordIm.setVisibility(View.INVISIBLE);
                        passwordWrongCheck = false;
                        if (!passwordWrongCheck && !nicknameWrongCheck && !emailWrongCheck && nicknameCheck && emailCheck && passwordCheck && passwordConfirmCheck) {
                            signupBtn.setEnabled(true);
                        }
                    } else {
                        passwordPo.setVisibility(View.INVISIBLE);
                        passwordIm.setVisibility(View.VISIBLE);
                        passwordWrongCheck = true;
                        signupBtn.setEnabled(false);

                    }
                } else {
                    if (passwordConfirmData.getText().toString().equals(passwordData.getText().toString())) {
                        passwordPo.setVisibility(View.VISIBLE);
                        passwordIm.setVisibility(View.INVISIBLE);
                        passwordWrongCheck = false;
                        if (!passwordWrongCheck && !nicknameWrongCheck && !emailWrongCheck && nicknameCheck && emailCheck && passwordCheck && passwordConfirmCheck) {
                            signupBtn.setEnabled(true);
                        }
                    }
                }
            }
        });


//버튼이 클릭되면 여기 리스너로 옴
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 와이파이 새로 접속할 때마다 변경
                new JSONTask().execute("http://172.30.1.10:3000/post");


                Intent navigationIntent = new Intent(Signup.this ,BottomNavigation.class);
                startActivity(navigationIntent);
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
//JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("nickname", nicknameData.getText());
                jsonObject.accumulate("email", emailData.getText());
                jsonObject.accumulate("password", passwordData.getText());
                jsonObject.accumulate("passwordConfirm", passwordConfirmData.getText());

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
/*
            if(result.equals("nicknameSuccess"))
            {
                Log.d("dd", "nicknameSuccess");
                nicknamePo.setVisibility(View.VISIBLE);
                nicknameIm.setVisibility(View.INVISIBLE);
                nicknameWrongCheck = false;
                if (!passwordWrongCheck && !nicknameWrongCheck && !emailWrongCheck && nicknameCheck && emailCheck && passwordCheck && passwordConfirmCheck ) {
                    signupBtn.setEnabled(true);
                }
            }
            else if(result.equals("nicknameFail"))
            {
                Log.d("dd", "nicknameFail");
                nicknameIm.setVisibility(View.VISIBLE);
                nicknamePo.setVisibility(View.INVISIBLE);
                nicknameWrongCheck = true;
                signupBtn.setEnabled(false);

            }
            else if(result.equals("nicknameNull"))
            {
                nicknameIm.setVisibility(View.INVISIBLE);
                nicknamePo.setVisibility(View.INVISIBLE);
            }

            if(result.equals("emailSuccess"))
            {
                emailPo.setVisibility(View.VISIBLE);
                emailIm.setVisibility(View.INVISIBLE);
                emailWr.setVisibility(View.INVISIBLE);
                emailWrongCheck = false;


            }
            else if(result.equals("emailFail"))
            {
                emailPo.setVisibility(View.INVISIBLE);
                emailIm.setVisibility(View.VISIBLE);
                emailWr.setVisibility(View.INVISIBLE);
                signupBtn.setEnabled(false);
                emailWrongCheck = true;
            }
            else if(result.equals("emailNull"))
            {
                emailPo.setVisibility(View.INVISIBLE);
                emailIm.setVisibility(View.INVISIBLE);
                emailWr.setVisibility(View.INVISIBLE);
            }
            else if(result.equals("emailWrong"))
            {
                emailPo.setVisibility(View.INVISIBLE);
                emailIm.setVisibility(View.INVISIBLE);
                emailWr.setVisibility(View.VISIBLE);
                signupBtn.setEnabled(false);
                emailWrongCheck = true;
            }
        }*/
        }
    }
}
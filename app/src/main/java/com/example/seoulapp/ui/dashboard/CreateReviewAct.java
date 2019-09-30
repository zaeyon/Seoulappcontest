package com.example.seoulapp.ui.dashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateReviewAct extends AppCompatActivity {

    Context context;
    InputMethodManager Imm;
    EditText reviewwriting;
    EditText storewriting;
    ImageView imgView;
    LinearLayout layout;
    TextView id;
    ListView listView;
    private Uri reviewFilePath;
    Button gotoReview;
    ImageView reviewImage;
    ImageView reviewImagePlus;

    int GET_GALLERY_IMAGE = 200;
    int REQUEST_ACT = 1;
    //File ReviewImgStore;

    private File tempFile;
    String fileName;
    String strEmail; //판별용 아이디
    TextView nickname;
    String reviewImageFile;
    String reviewImageUrl;

    String[] allView;
    String Dbnickname;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_view);

        View view = getLayoutInflater().inflate(R.layout.activity_edit_profile, null); //무슨 역할일까

        //공백 누르면 키보드 ㅂㅂ
        Imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        SharedPreferences auto = getApplicationContext().getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = auto.getString("inputId", "null");
        id = findViewById(R.id.User_id);
        reviewwriting = findViewById(R.id.ReviewWriting); //
        storewriting = findViewById(R.id.StoreWriting); //
        layout = findViewById(R.id.ReviewLayout);
        reviewImage = findViewById(R.id.proRegImage1); //사용자가 올릴 이미지
        reviewImagePlus = findViewById(R.id.selectPhoto1);
        nickname = (TextView) view.findViewById(R.id.cetNickname); //되었음!!!!
        gotoReview = findViewById((R.id.gotoReview));



        reviewImagePlus.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
            }
        });

        storewriting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storewriting.setHint("상가가 여러 개라면 ,로 구별해주세요!");
            }
        });

        gotoReview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String write_Story = reviewwriting.getText().toString();
                String write_Store = storewriting.getText().toString();
                if (write_Store.equals("") || write_Story.equals("")) {
                    Toast.makeText(getApplicationContext(), "입력이 덜 되었습니다!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"저장되었습니다!", Toast.LENGTH_LONG).show();
                    new JSONTaskReviewUpload().execute("http://dongdong.com.ngrok.io/setReviewFile");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
            if(requestCode == 0 && resultCode == RESULT_OK){
                reviewFilePath = data.getData();
                try {
                    //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), reviewFilePath);
                    reviewImage.setImageBitmap(bitmap);
                     reviewUploadFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Cursor cursor = null;

            try {
                String[] proj = {MediaStore.Images.Media.DATA};

                assert uri != null;
                cursor = getContentResolver().query(uri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();
            } finally {
                if(cursor != null) {
                    cursor.close();
                }
            }
        }
    }


    public void linearOnClick(View v) { //공백 누름 시 키보드 닫기는 효과 줌
        Imm.hideSoftInputFromWindow(reviewwriting.getWindowToken(), 0);
        Imm.hideSoftInputFromWindow(storewriting.getWindowToken(), 0);
    } //공백을 넣어줘야겠당...


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backbutton_, menu);
        return true;
    }


    public class JSONTaskReviewUpload extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Email", strEmail); //Id
                jsonObject.accumulate("UserImg", reviewImageUrl);
                jsonObject.accumulate("UserContent", reviewwriting.getText().toString()); //story
                jsonObject.accumulate("StoreName", storewriting.getText().toString()); //store
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
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

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
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
                            reader.close();
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
            super.onPostExecute(result); //reviewurl nickname

            allView = result.split("#&#");

            Dbnickname = allView[0];

            onBackPressed();

        }
    }

    public class JSONTaskStoreUrl extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("Email", strEmail);
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
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

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
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
                            reader.close();
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

            finish();
        }
    }


    private void reviewUploadFile() {
        //업로드할 파일이 있으면 수행
        if (reviewFilePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            reviewImageFile = formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            final StorageReference storageRef = storage.getReferenceFromUrl("gs://dong-dong-c7d7e.appspot.com").child("images/ReviewImage/" + reviewImageFile);
            //올라가거라...
            storageRef.putFile(reviewFilePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();

                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    reviewImageUrl = downloadUrl.toString();
                                    Log.d("proImageUrl : ", reviewImageUrl);
                                }
                            });
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed(){
        Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
       super.onBackPressed();
    }
}
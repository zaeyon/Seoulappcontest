package com.example.seoulapp.ui.notifications;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seoulapp.ClearEditText;
import com.example.seoulapp.R;
import com.example.seoulapp.ShopDetaildInfo;
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
import java.util.ArrayList;
import java.util.Date;

public class RegisterProduction extends AppCompatActivity {

    String proImageFile;
    String proImageUrl;
    ImageView proRegImage;
    ImageView proRegPlus;
    EditText proRegTitle;
    ClearEditText proRegShopName;
    Spinner proRegType;
    EditText proRegIntro;
    EditText proRegSize;
    EditText proRegPrice;
    Button proRegBtn;
    String proUrl;
    private Uri filePath;

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
        proRegPlus = findViewById(R.id.selectPhoto);

        proRegShopName.setText(getIntent().getStringExtra("shopName"));
        proRegShopName.setEnabled(false);

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
    new JSONTaskInsertProduction().execute("http://192.168.43.72:3000/InsertProductionInfo");

            }
        });

        proRegPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);

            }
        });
    }

    public class JSONTaskInsertProduction extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("RegProShopName", proRegShopName.getText());
                jsonObject.accumulate("RegProType", proRegType.getSelectedItem().toString());
                jsonObject.accumulate("RegProTitle", proRegTitle.getText());
                jsonObject.accumulate("RegProIntro", proRegIntro.getText());
                jsonObject.accumulate("RegProImageUrl", proImageUrl);
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


                new JSONTaskgetShopInfo().execute("http://192.168.43.72:3000/getNewMyShopInfo");
                /*
                Intent shopDetailIntent = new Intent(RegisterProduction.this, ShopDetaildInfo.class);
                shopDetailIntent.putExtra("name", myShopInfo[5]);
                shopDetailIntent.putExtra("building", myShopInfo[6]);
                shopDetailIntent.putExtra("floor", myShopInfo[7]);
                shopDetailIntent.putExtra("rocation", myShopInfo[8]);
                shopDetailIntent.putExtra("category", myShopInfo[10]);
                shopDetailIntent.putExtra("style", myShopInfo[9]);
                shopDetailIntent.putExtra("intro", myShopInfo[11]);
                startActivity(shopDetailIntent);
                 */



}
        }
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
                filePath = data.getData();
                try {
                    //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    proRegImage.setImageBitmap(bitmap);
                    profileUploadFile();
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

    private void profileUploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            proImageFile = formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            final StorageReference storageRef = storage.getReferenceFromUrl("gs://dong-dong-c7d7e.appspot.com").child("images/ShopProductionImage/" + proImageFile);
            //올라가거라...
            storageRef.putFile(filePath)
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
                                    proImageUrl = downloadUrl.toString();
                                    Log.d("proImageUrl : ", proImageUrl);
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

    public class JSONTaskgetShopInfo extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("RegProShopName", proRegShopName.getText());

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

            String[] newMyShopInfo;

            newMyShopInfo = result.split("\\|");

            Intent shopDetailIntent = new Intent(RegisterProduction.this, ShopDetaildInfo.class);

                shopDetailIntent.putExtra("name", proRegShopName.getText().toString());
                shopDetailIntent.putExtra("building", newMyShopInfo[0]);
                shopDetailIntent.putExtra("floor", newMyShopInfo[1]);
                shopDetailIntent.putExtra("rocation", newMyShopInfo[2]);
                shopDetailIntent.putExtra("category", newMyShopInfo[3]);
                shopDetailIntent.putExtra("style", newMyShopInfo[4]);
                shopDetailIntent.putExtra("intro", newMyShopInfo[5]);

                Log.d("name : " , proRegShopName.getText().toString());

                for(int i = 0 ; i < newMyShopInfo.length; i++) {
                    Log.d("newMyShopInfo[i]", newMyShopInfo[i]);
                }

                startActivity(shopDetailIntent);


        }
    }

}

package com.example.seoulapp.ui.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.seoulapp.ClearEditText;
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
import java.util.ArrayList;
import java.util.Date;

public class AddShop extends AppCompatActivity {

    String profileImageFile;
    String repImageFile1;
    String repImageFile2;
    String repImageFile3;
    private Uri filePath;
    ImageView newShopProfileImage;
    EditText newShopName;
    TextView tvExShopName;
    Spinner newShopSalesItem;
    Spinner newShopBuilding;
    EditText newShopStyle;
    EditText newShopFloor;
    EditText newShopLocation;
    ClearEditText newShopIntro;
    private ImageView newShopRep1;
    private ImageView newShopRep2;
    private ImageView newShopRep3;

    Button signUpNext;
    ImageView addShopProfilePlus;
    ImageView addShopRepPlus1;
    ImageView addShopRepPlus2;
    ImageView addShopRepPlus3;

    private final int GET_GALLERY_IMAGE = 200;

    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonS3 s3;
    TransferUtility transferUtility;

    File f, f1, f2, f3;
    String fileName, fileName1, fileName2, fileName3;
    String shopID;
    int FOR_RESULT_CODE;

    String profileUrl;
    String repfileUrl1;
    String repfileUrl2;
    String repfileUrl3;

    String userEmail;

    ArrayList<String> salesItemList;
    ArrayList<String> buildingList;
    ArrayAdapter<String> salesItemAdapter;
    ArrayAdapter<String> buildingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_m);

        signUpNext = findViewById(R.id.addShopBtn);
        newShopProfileImage = findViewById(R.id.newShopProfileImage);
        newShopName = findViewById(R.id.newShopName);
        newShopSalesItem = findViewById(R.id.newShopSalesItem);
        newShopStyle = findViewById(R.id.newShopStyle);
        newShopBuilding = findViewById(R.id.newShopBuilding);
        newShopFloor = findViewById(R.id.newShopFloor);
        newShopLocation = findViewById(R.id.newShopLocation);
        newShopIntro = findViewById(R.id.newShopIntro);

        addShopProfilePlus = findViewById(R.id.newShopProfildPlus);

        addShopProfilePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
            }
        });

        /*
        addShopRepPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 1);
            }
        });

        addShopRepPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 2);
            }
        });

        addShopRepPlus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 3);
            }
        });

         */
;
        SharedPreferences user = getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        userEmail = user.getString("inputId", "null");
        Log.d("AddShop", "사용자 : " + userEmail);

        new JSONTask0().execute("http://192.168.43.72:3000/newShopData0");

        salesItemList = new ArrayList<>();
        salesItemList.add("여성 의류");
        salesItemList.add("남성 의류");
        salesItemList.add("신발");
        salesItemList.add("가방");
        salesItemList.add("액세서리");
        salesItemList.add("기타");

        buildingList = new ArrayList<>();
        buildingList.add("동대문 종합시장");
        buildingList.add("청평화 시장");
        buildingList.add("벨포스트");
        buildingList.add("광희 시장");
        buildingList.add("두산 타워");
        buildingList.add("기타");

        buildingAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, buildingList);

        salesItemAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, salesItemList);

        newShopSalesItem.setAdapter(salesItemAdapter);
        newShopSalesItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        newShopBuilding.setAdapter(buildingAdapter);
        newShopBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        // 매장 관리 페이지로 이동하게 수정
        signUpNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new JSONTask1().execute("http://192.168.43.72:3000/newShopData1");

                Log.d("profileFile : " ,profileImageFile);
               // Log.d("repFile : ", repImageFile1);
               // Log.d("repFIle2 : ", repImageFile2);
               //  Log.d("repFile3 : ", repImageFile3);
                Log.d("fileUrl : ", profileUrl);

                //finish();
            }
        });

        DisplayMetrics displaymetrics;
        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    }


    // 갤러리에서 이미지 불러오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            newShopProfileImage = (ImageView)findViewById(R.id.newShopProfileImage);

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
                    newShopProfileImage.setImageBitmap(bitmap);
                    profileUploadFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /*
            else if(requestCode == 1)
            {
                filePath = data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    newShopRep1.setImageBitmap(bitmap);
                    repUploadFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode == 2)
            {
                filePath = data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    newShopRep2.setImageBitmap(bitmap);
                    repUploadFile2();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode == 3)
            {
                filePath = data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    newShopRep3.setImageBitmap(bitmap);
                    repUploadFile3();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

             */


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

    public class JSONTask0 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", userEmail);
                jsonObject.accumulate("imageUrl", filePath);

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

            shopID = result;
        }
    }

    /* userEmail, shopName, shopBuilding, shopFloor, shopLocation, shopStyle, shopCategory, shopIntro를 DB에 저장 */
    public class JSONTask1 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", userEmail);
                jsonObject.accumulate("name", newShopName.getText());
                jsonObject.accumulate("building", newShopBuilding.getSelectedItem());
                jsonObject.accumulate("floor", newShopFloor.getText());
                jsonObject.accumulate("location", newShopLocation.getText());
                jsonObject.accumulate("style", newShopStyle.getText());
                jsonObject.accumulate("category", newShopSalesItem.getSelectedItem());
                jsonObject.accumulate("introduction", newShopIntro.getText());
                jsonObject.accumulate("profileImg", profileImageFile);
                jsonObject.accumulate("profileImageUrl", profileUrl);

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

            Intent registerRep1 = new Intent(AddShop.this, ShopRepPro1.class);

            registerRep1.putExtra("shopName", newShopName.getText().toString());

            startActivity(registerRep1);
        }
    }


    //upload the file
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
            profileImageFile = formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            final StorageReference storageRef = storage.getReferenceFromUrl("gs://dong-dong-c7d7e.appspot.com").child("images/ShopProfileImage/" + profileImageFile);
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
                                    profileUrl = downloadUrl.toString();
                                    Log.d("profileUrl1 : ", profileUrl);

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
}

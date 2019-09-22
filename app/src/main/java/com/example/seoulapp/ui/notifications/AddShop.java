package com.example.seoulapp.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.example.seoulapp.ClearEditText;
import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;

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
import java.util.ArrayList;

public class AddShop extends AppCompatActivity {

    ImageView newShopProfileImage;
    ClearEditText newShopName;
    Spinner newShopSalesItem;
    Spinner newShopBuilding;
    ClearEditText newShopStyle;
    ClearEditText newShopFloor;
    ClearEditText newShopLocation;
    ClearEditText newShopIntro;
    private ImageView newShopRep1;
    private ImageView newShopRep2;
    private ImageView newShopRep3;
    Context context;


    private File tempFile;
    Button signUpNext;

    private static final int REQUEST_CODE = 0;

    private final int GET_GALLERY_IMAGE = 200;
    final int REQ_CODE_SELECT_IMAGE = 100;
    private final int GET_REP1 = 300;
    private final int GET_REP2 = 400;
    private final int GET_REP3 = 500;
    private static final int PICK_FROM_ALBUM = 1;
    private final int PICK_FROM_FILE = 0;

    File photo = new File(Environment.getExternalStorageDirectory(),
            ".camera.jpg");
    Uri imageUri = Uri.fromFile(photo);

    private static final int PICK_IMAGE = 1000;
    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE = 1112;
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름


    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonS3 s3;
    TransferUtility transferUtility;

    File f, f1, f2, f3;
    String fileName, fileName1, fileName2, fileName3;
    String shopID;
    String rep1;
    String rep2;
    String rep3;
    int FOR_RESULT_CODE;

    static String userEmail;

    ArrayList<String> salesItemList;
    ArrayList<String> buildingList;
    ArrayAdapter<String> salesItemAdapter;
    ArrayAdapter<String> buildingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        signUpNext = findViewById(R.id.signUpNextButton);
        newShopProfileImage = findViewById(R.id.newShopProfileImage);
        newShopName = findViewById(R.id.newShopName);
        newShopSalesItem = findViewById(R.id.newShopSalesItem);
        newShopStyle = findViewById(R.id.newShopStyle);
        newShopBuilding = findViewById(R.id.newShopBuilding);
        newShopFloor = findViewById(R.id.newShopFloor);
        newShopLocation = findViewById(R.id.newShopLocation);
        newShopIntro = findViewById(R.id.newShopIntro);
        newShopRep1 = findViewById(R.id.newShopRep1);
        newShopRep2 = findViewById(R.id.newShopRep2);
        newShopRep3 = findViewById(R.id.newShopRep3);

        newShopProfileImage.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            newShopProfileImage.setClipToOutline(true);
        }
        newShopProfileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FOR_RESULT_CODE = 0;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        SharedPreferences user = getApplicationContext().getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        userEmail = user.getString("inputId", "null");

        // amazons3에 이미지 저장하기
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:cc1b25cd-c9e7-430f-9666-474b1d523655", // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전
        );
        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        transferUtility = new TransferUtility(s3, getApplicationContext());

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

        newShopRep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FOR_RESULT_CODE = 1;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        newShopRep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FOR_RESULT_CODE = 2;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        newShopRep3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FOR_RESULT_CODE = 3;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        signUpNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new JSONTask1().execute("http://172.30.1.14:3000/newShopData1");

                if (f != null && f1 != null && f2 != null && f3 != null) {
                    TransferObserver obsever = transferUtility.upload(
                            "com.example.seoulapp/ShopProfileImage",
                            fileName,
                            f
                    );
                    TransferObserver obsever1 = transferUtility.upload(
                            "com.example.seoulapp/ShopRepresentaionImage",
                            fileName1,
                            f1
                    );
                    TransferObserver obsever2 = transferUtility.upload(
                            "com.example.seoulapp/ShopRepresentaionImage",
                            fileName2,
                            f2
                    );
                    TransferObserver obsever3 = transferUtility.upload(
                            "com.example.seoulapp/ShopRepresentaionImage",
                            fileName3,
                            f3
                    );
                    Log.d("AddShop", "저장 성공, 파일 이름 : " + fileName + ", " + fileName1 + ", " + fileName2 + ", " + fileName3);
                } else {
                    Log.d("AddShop", "저장 실패, 파일 이름 : " + fileName + ", " + fileName1 + ", " + fileName2 + ", " + fileName3);
                }

                finish();
            }
        });

        DisplayMetrics displaymetrics;
        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    }


    // 갤러리에서 이미지 불러오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            newShopProfileImage = (ImageView)findViewById(R.id.newShopProfileImage);
            newShopRep1 = findViewById(R.id.newShopRep1);
            newShopRep2 = findViewById(R.id.newShopRep2);
            newShopRep3 = findViewById(R.id.newShopRep3);

            Uri selectedImageUri = data.getData();

            switch (FOR_RESULT_CODE) {
                case 1:
                    newShopRep1.setImageURI(selectedImageUri);
                    Log.d("AddShop", "Rep1 이미지 띄우기");
                    break;

                case 2:
                    newShopRep2.setImageURI(selectedImageUri);
                    Log.d("AddShop", "Rep2 프로필 이미지 띄우기");
                    break;

                case 3:
                    newShopRep3.setImageURI(selectedImageUri);
                    Log.d("AddShop", "Rep3 프로필 이미지 띄우기");
                    break;

                default:
                    newShopProfileImage.setImageURI(selectedImageUri);
                    Log.d("AddShop", "매장 프로필 이미지 띄우기");
                    break;
            }

            Cursor cursor = null;

            try {
                String[] proj = {MediaStore.Images.Media.DATA};

                assert selectedImageUri != null;
                cursor = getContentResolver().query(selectedImageUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                switch (FOR_RESULT_CODE) {
                    case 1:
                        f1 = new File(cursor.getString(column_index));
                        break;
                    case 2:
                        f2 = new File(cursor.getString(column_index));
                        break;
                    case 3:
                        f3 = new File(cursor.getString(column_index));
                        break;
                    default:
                        f = new File(cursor.getString(column_index));
                }
            } finally {
                if(cursor != null) {
                    cursor.close();
                }
            }
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

            new JSONTask2().execute("http://172.30.1.14:3000/newShopData2");
        }
    }

    public class JSONTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", userEmail);

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
            Log.d("AddShop", "매장 ID : " + shopID);
            fileName = shopID + "_" + f.getName();
            fileName1 = shopID + "_rep1_" + f1.getName();
            fileName2 = shopID + "_rep2_" + f2.getName();
            fileName3 = shopID + "_rep3_" + f3.getName();

            new JSONTask3().execute("http://172.30.1.14:3000/newShopData3");
        }
    }

    public class JSONTask3 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopID", shopID);
                jsonObject.accumulate("profileImage", fileName);
                jsonObject.accumulate("rep1", fileName1);
                jsonObject.accumulate("rep2", fileName2);
                jsonObject.accumulate("rep3", fileName3);

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
        }
    }
}

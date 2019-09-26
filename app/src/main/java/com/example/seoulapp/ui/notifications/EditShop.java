package com.example.seoulapp.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

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

public class EditShop extends AppCompatActivity {

    static String strEmail;

    private final int GET_GALLERY_IMAGE = 200;
    private File tempFile;

    ImageView ivShopProfileImage;
    ImageView ivShopRep1;
    ImageView ivShopRep2;
    ImageView ivShopRep3;
    ClearEditText cetShopName; // 못 바꾸게!
    ClearEditText cetShopStyle;
    EditText cetShopFloor;
    EditText cetShopLocation;
    ClearEditText cetShopIntro;
    Spinner sSalesItem;
    Spinner sBuilding;

    File f, f1, f2, f3;
    String fileName, fileName1, fileName2, fileName3;
    String shopID;
    String[] existingData;
    int FOR_RESULT_CODE;

    ArrayList<String> salesItemList;
    ArrayList<String> buildingList;
    ArrayAdapter<String> salesItemAdapter;
    ArrayAdapter<String> buildingAdapter;

    Button bComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        ivShopProfileImage = findViewById(R.id.newShopProfileImage);
        ivShopRep1 = findViewById(R.id.newShopRep1);
        ivShopRep2 = findViewById(R.id.newShopRep2);
        ivShopRep3 = findViewById(R.id.newShopRep3);
        cetShopName = findViewById(R.id.newShopName);
        cetShopStyle = findViewById(R.id.newShopStyle);
        cetShopFloor = findViewById(R.id.newShopFloor);
        cetShopLocation = findViewById(R.id.newShopLocation);
        cetShopIntro = findViewById(R.id.newShopIntro);
        sSalesItem = findViewById(R.id.newShopSalesItem);
        sBuilding = findViewById(R.id.newShopBuilding);
        bComplete = findViewById(R.id.signUpNextButton); // 수정 완료 버튼

        SharedPreferences user = getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = user.getString("inputId", "null");
        Log.d("AddShop", "사용자 : " + strEmail);

        new GetExistingData().execute("http://172.30.1.28:3000/getExistingData");

        ivShopProfileImage.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            ivShopProfileImage.setClipToOutline(true);
        }

        ivShopProfileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FOR_RESULT_CODE = 0;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

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

        sSalesItem.setAdapter(salesItemAdapter);
        sSalesItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sBuilding.setAdapter(buildingAdapter);
        sBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ivShopRep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FOR_RESULT_CODE = 1;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        ivShopRep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FOR_RESULT_CODE = 2;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        ivShopRep3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FOR_RESULT_CODE = 3;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        bComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new JSONTask1().execute("http://172.30.1.28:3000/editShop");

                if (f != null && f1 != null && f2 != null && f3 != null) {
                    // f 저장
                    // f1 저장
                    // f2 저장
                    // f3 저장
                    Log.d("AddShop", "저장 성공, 파일 이름 : " + fileName + ", " + fileName1 + ", " + fileName2 + ", " + fileName3);
                } else {
                    Log.d("AddShop", "저장 실패, 파일 이름 : " + fileName + ", " + fileName1 + ", " + fileName2 + ", " + fileName3);
                }

                finish();
                // 매장 개별 페이지로 이동
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            ivShopProfileImage = (ImageView)findViewById(R.id.newShopProfileImage);
            ivShopRep1 = findViewById(R.id.newShopRep1);
            ivShopRep2 = findViewById(R.id.newShopRep2);
            ivShopRep3 = findViewById(R.id.newShopRep3);

            Uri selectedImageUri = data.getData();

            switch (FOR_RESULT_CODE) {
                case 1:
                    ivShopRep1.setImageURI(selectedImageUri);
                    Log.d("AddShop", "Rep1 이미지 띄우기");
                    break;

                case 2:
                    ivShopRep2.setImageURI(selectedImageUri);
                    Log.d("AddShop", "Rep2 프로필 이미지 띄우기");
                    break;

                case 3:
                    ivShopRep3.setImageURI(selectedImageUri);
                    Log.d("AddShop", "Rep3 프로필 이미지 띄우기");
                    break;

                default:
                    ivShopProfileImage.setImageURI(selectedImageUri);
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
                        fileName1 = shopID + "_rep1_" + f1.getName();
                        break;
                    case 2:
                        f2 = new File(cursor.getString(column_index));
                        fileName2 = shopID + "_rep2_" + f2.getName();
                        break;
                    case 3:
                        f3 = new File(cursor.getString(column_index));
                        fileName3 = shopID + "_rep3_" + f3.getName();
                        break;
                    default:
                        f = new File(cursor.getString(column_index));
                        fileName = shopID + "_" + f.getName();
                        Log.d("AddShop", "파일 객체 초기화");
                }
            } finally {
                if(cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    public class GetExistingData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);

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

            ivShopProfileImage = findViewById(R.id.newShopProfileImage);
            existingData = result.split("\\|");
            // 0shopName, 1shopProfileImage, 2shopBuilding, 3shopFloor, 4shopRocation, 5shopStyle, 6shopCategory, 7shopIntro, 8shopRepresentation1, 9shpRepresentation2, 10shopRepresentation3

            cetShopName.setText(existingData[0]);
            String shopProfileImage = existingData[1];
            if (existingData[2].equals("동대문 종합시장")) sBuilding.setSelection(0);
            else if (existingData[2].equals("청평화 시장")) sBuilding.setSelection(1);
            else if (existingData[2].equals("벨포스트")) sBuilding.setSelection(2);
            else if (existingData[2].equals("광희 시장")) sBuilding.setSelection(3);
            else if (existingData[2].equals("두산 타워")) sBuilding.setSelection(4);
            else sBuilding.setSelection(5);
            cetShopFloor.setText(existingData[3]);
            cetShopLocation.setText(existingData[4]);
            cetShopStyle.setText(existingData[5]);
            if (existingData[6].equals("여성 의류"))  sSalesItem.setSelection(0);
            else if (existingData[6].equals("남성 의류")) sSalesItem.setSelection(1);
            else if (existingData[6].equals("신발")) sSalesItem.setSelection(2);
            else if (existingData[6].equals("가방")) sSalesItem.setSelection(3);
            else if (existingData[6].equals("액세서리")) sSalesItem.setSelection(4);
            else sSalesItem.setSelection(5);
            cetShopIntro.setText(existingData[7]);
            String shopRepImage1 = existingData[8];
            String shopRepImage2 = existingData[9];
            String shopRepImage3 = existingData[10];
        }
    }

    public class JSONTask1 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);
                jsonObject.accumulate("name", cetShopName.getText());
                jsonObject.accumulate("building", sBuilding.getSelectedItem());
                jsonObject.accumulate("floor", cetShopFloor.getText());
                jsonObject.accumulate("location", cetShopLocation.getText());
                jsonObject.accumulate("style", cetShopStyle.getText());
                jsonObject.accumulate("category", sSalesItem.getSelectedItem());
                jsonObject.accumulate("introduction", cetShopIntro.getText());
                jsonObject.accumulate("profileImg", fileName);
                jsonObject.accumulate("repImg1", fileName1);
                jsonObject.accumulate("repImg2", fileName2);
                jsonObject.accumulate("repImg3", fileName3);

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

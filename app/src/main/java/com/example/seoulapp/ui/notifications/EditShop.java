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

import com.bumptech.glide.Glide;
import com.example.seoulapp.ClearEditText;
import com.example.seoulapp.MainActivity;
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

public class EditShop extends AppCompatActivity {

    String strEmail;

    ImageView ivShopProfileImage;
    TextView tvExShopName;
    TextView cetShopName;
    EditText cetShopStyle;
    EditText cetShopFloor;
    EditText cetShopLocation;
    ClearEditText cetShopIntro;
    Spinner sSalesItem;
    Spinner sBuilding;

    String fileName, fileName1, fileName2, fileName3;
    String fileURL, fileURL1, fileURL2, fileURL3;
    String profileImageFile;
    String profileUrl;
    String repImageFile1, repImageFile2, repImageFile3;
    String repfileUrl1, repfileUrl2, repfileUrl3;
    private Uri filePath;
    String[] existingData;

    ArrayList<String> salesItemList;
    ArrayList<String> buildingList;
    ArrayAdapter<String> salesItemAdapter;
    ArrayAdapter<String> buildingAdapter;

    Button bComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_m);

        ivShopProfileImage = findViewById(R.id.newShopProfileImage);
        tvExShopName = findViewById(R.id.exShopName);
        cetShopName = findViewById(R.id.newShopName);
        cetShopStyle = findViewById(R.id.newShopStyle);
        cetShopFloor = findViewById(R.id.newShopFloor);
        cetShopLocation = findViewById(R.id.newShopLocation);
        cetShopIntro = findViewById(R.id.newShopIntro);
        sSalesItem = findViewById(R.id.newShopSalesItem);
        sBuilding = findViewById(R.id.newShopBuilding);
        bComplete = findViewById(R.id.signUpNextButton); // 수정 완료 버튼

        cetShopName.setVisibility(View.INVISIBLE);

        SharedPreferences user = getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = user.getString("inputId", "null");
        Log.d("AddShop", "사용자 : " + strEmail);

        new GetExistingData().execute("http://dongdong.com.ngrok.io/getExistingData");


        ivShopProfileImage.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            ivShopProfileImage.setClipToOutline(true);
        }

        ivShopProfileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
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


        bComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new JSONTask1().execute("http://dongdong.com.ngrok.io/editShop");

                Intent intentSettingShop = new Intent(EditShop.this, ShopDetaildInfo.class);
                intentSettingShop.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentSettingShop.putExtra("hostEmail", strEmail);
                intentSettingShop.putExtra("profileImage",profileUrl);
                intentSettingShop.putExtra("name", tvExShopName.getText());
                intentSettingShop.putExtra("building", sBuilding.getSelectedItem().toString());
                intentSettingShop.putExtra("floor", cetShopFloor.getText());
                intentSettingShop.putExtra("rocation", cetShopLocation.getText());
                intentSettingShop.putExtra("category", sSalesItem.getSelectedItem().toString());
                intentSettingShop.putExtra("style", cetShopStyle.getText());
                intentSettingShop.putExtra("intro", cetShopIntro.getText());
                intentSettingShop.putExtra("representation1", repfileUrl1);
                intentSettingShop.putExtra("representation2", repfileUrl2);
                intentSettingShop.putExtra("representation3", repfileUrl3);

                startActivity(intentSettingShop);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            ivShopProfileImage = (ImageView)findViewById(R.id.newShopProfileImage);

            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if(requestCode == 0 && resultCode == RESULT_OK){
                filePath = data.getData();
                try {
                    //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    ivShopProfileImage.setImageBitmap(bitmap);
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

//                switch (FOR_RESULT_CODE) {
//                    case 1:
//                        f1 = new File(cursor.getString(column_index));
//                        fileName1 = shopID + "_rep1_" + f1.getName();
//                        break;
//                    case 2:
//                        f2 = new File(cursor.getString(column_index));
//                        fileName2 = shopID + "_rep2_" + f2.getName();
//                        break;
//                    case 3:
//                        f3 = new File(cursor.getString(column_index));
//                        fileName3 = shopID + "_rep3_" + f3.getName();
//                        break;
//                    default:
//                        f = new File(cursor.getString(column_index));
//                        fileName = shopID + "_" + f.getName();
//                        Log.d("AddShop", "파일 객체 초기화");
//                }
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

            tvExShopName.setText(existingData[0]);
            profileUrl = existingData[1];
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

            Glide.with(EditShop.this).load(profileUrl).into(ivShopProfileImage);
        }
    }

    public class JSONTask1 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);
                jsonObject.accumulate("name", tvExShopName.getText());
                jsonObject.accumulate("building", sBuilding.getSelectedItem());
                jsonObject.accumulate("floor", cetShopFloor.getText());
                jsonObject.accumulate("location", cetShopLocation.getText());
                jsonObject.accumulate("style", cetShopStyle.getText());
                jsonObject.accumulate("category", sSalesItem.getSelectedItem());
                jsonObject.accumulate("introduction", cetShopIntro.getText());
                jsonObject.accumulate("profileImg", fileName);
                jsonObject.accumulate("profileURL", profileUrl);

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

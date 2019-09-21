package com.example.seoulapp.ui.notifications;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.seoulapp.ClearEditText;
import com.example.seoulapp.R;

import java.io.File;
import java.util.ArrayList;

import static android.provider.MediaStore.Video.Thumbnails.DATA;

public class AddShop extends AppCompatActivity {

    ImageView newShopProfileImage;
    ClearEditText newShopName;
    Spinner newShopSalesItem;
    Spinner newShopBuilding;
    ClearEditText newShopStyle;
    ClearEditText newShopFloor;
    ClearEditText newShopLocation;
    ClearEditText newShopIntro;

    Button signUpNext;

    private final int GET_GALLERY_IMAGE = 200;
    private File tempFile;

    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonS3 s3;
    TransferUtility transferUtility;

    File f;

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



        newShopProfileImage.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            newShopProfileImage.setClipToOutline(true);
        }
        newShopProfileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        // amazons3에 이미지 저장하기
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
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

        signUpNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TransferObserver obsever = transferUtility.upload(
                        "com.example.seoulapp/userProfileImage",
                        f.getName(),
                        f
                );

                Intent signPageIntent = new Intent(AddShop.this, AddShop2.class);
                startActivity(signPageIntent);
            };
        });



    }

    // 갤러리에서 이미지 불러오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            newShopProfileImage.setImageURI(selectedImageUri);

            Cursor cursor = null;


            try {
                String[] proj = {MediaStore.Images.Media.DATA};

                assert selectedImageUri != null;
                cursor = getContentResolver().query(selectedImageUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            setImage();
        }
    }

    private void setImage() {
        newShopProfileImage = (ImageView) findViewById(R.id.newShopProfileImage);

        f = tempFile;
    }
}

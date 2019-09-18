
package com.example.seoulapp.ui.notifications;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.seoulapp.R;

import java.io.File;
import java.util.ArrayList;

public class MyShopActivity extends AppCompatActivity {

    private final int GET_GALLERY_IMAGE = 200;
    private File tempFile;
    ImageView ivShopProfile;

    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonS3 s3;
    TransferUtility transferUtility;

    File f;

    private Spinner sSalesItem;
    private Spinner sBuilding;
    ArrayList<String> arrayList1;
    ArrayList<String> arrayList2;
    ArrayAdapter<String> arrayAdapter1;
    ArrayAdapter<String> arrayAdapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shop);

        ivShopProfile = (ImageView)findViewById(R.id.ivShopProfile);
        ivShopProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);

//                ivShopProfile = (ImageView)findViewById(R.id.ivShopProfile);
//
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
//                ivShopProfile.setImageBitmap(originalBm);
//
//                TransferObserver obsever = transferUtility.upload(
//                        "com.example.seoulapp",
//                        f.getName(),
//                        f
//                );
            }
        });

        // amazons3에 이미지 저장하기
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:cc1b25cd-c9e7-430f-9666-474b1d523655", // Identity Pool ID
                Regions.AP_NORTHEAST_2
        );
        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        transferUtility = new TransferUtility(s3, getApplicationContext());

        // 원형 이미지
//        ivShopProfile = (ImageView)findViewById(R.id.ivShopProfile);
//        ivShopProfile.setBackground(new ShapeDrawable(new OvalShape()));
//        if (Build.VERSION.SDK_INT >= 21) {
//            ivShopProfile.setClipToOutline(true);
//        }

        arrayList1 = new ArrayList<>();
        arrayList1.add("여성 의류");
        arrayList1.add("남성 의류");
        arrayList1.add("신발");
        arrayList1.add("가방");
        arrayList1.add("액세서리");

        arrayList2 = new ArrayList<>();
        arrayList2.add("동대문종합시장");
        arrayList2.add("청평화시장");
        arrayList2.add("벨포스트");
        arrayList2.add("광희시장");
        arrayList2.add("두산타워");

        arrayAdapter1 = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList1);
        arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList2);

        sSalesItem = (Spinner)findViewById(R.id.sSalesItem);
        sSalesItem.setAdapter(arrayAdapter1);
        sSalesItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), arrayList1.get(i)+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sBuilding = (Spinner)findViewById(R.id.sBuilding);
        sBuilding.setAdapter(arrayAdapter2);
        sBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), arrayList2.get(i)+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 컨셉 누르면 홈->필터 레이아웃
    }

    // 갤러리에서 이미지 불러오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            ivShopProfile.setImageURI(selectedImageUri);

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
                if(cursor != null) {
                    cursor.close();
                }
            }

            setImage();
        }
    }

    private void setImage() {
        ivShopProfile = (ImageView)findViewById(R.id.ivShopProfile);

        f = tempFile;

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
//        ivShopProfile.setImageBitmap(originalBm);


        // amazons3에 이미지 저장, 수정완료버튼을 눌렀을때 실행되도록 변경하기!
        TransferObserver obsever = transferUtility.upload(
                "com.example.seoulapp",
                f.getName(),
                f
        );
    }
}



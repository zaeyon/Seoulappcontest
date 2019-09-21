package com.example.seoulapp.ui.notifications;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.seoulapp.ClearEditText;
import com.example.seoulapp.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.provider.MediaStore.Video.Thumbnails.DATA;
import static java.security.AccessController.getContext;

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
        newShopRep1 = findViewById(R.id.newShopRep1);
        newShopRep2 = findViewById(R.id.newShopRep2);
        newShopRep3 = findViewById(R.id.newShopRep3);


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

                Intent addShopFinishIntent = new Intent(AddShop.this, NotificationsFragment.class);
                startActivity(addShopFinishIntent);
            }

            ;
        });

        newShopRep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

            }
        });

        newShopRep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_REP2);
            }
        });

        newShopRep3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_REP3);
            }
        });

        DisplayMetrics displaymetrics;
        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

    }


    // 갤러리에서 이미지 불러오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                try {
                    Uri imageContent = data.getData();
                    String imagePath = getRealPathFromURI(imageContent);
                    Bitmap image = BitmapFactory.decodeFile(imagePath);

                    ExifInterface exif = new ExifInterface(imagePath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    image = rotate(image, exifDegree);

                    // 변환된 이미지 사용
                    newShopRep1.setImageBitmap(image);
                }
                catch(Exception e)
                {
                    Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
                }
            }
        }
    }

        /*
        Uri selectedImageUri = data.getData();

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String imagePath = imageUri.getPath();
                    Bitmap Rep1_image = BitmapFactory.decodeFile(imagePath);

                    // 이미지를 상황에 맞게 회전시킨다
                    ExifInterface exif = new ExifInterface(imagePath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    Rep1_image = rotate(Rep1_image, exifDegree);

                    // 변환된 이미지 사용
                    newShopRep1.setImageBitmap(Rep1_image);
                }
                catch(Exception e)
                {
                    Toast.makeText(this, "오류발생:"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }


                    //

         */
                    /*
                    //이미지 데이터를 비트맵으로 받아온다.
                  // Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView) findViewById(R.id.newShopRep1);
                    String imagePath = getPath1(selectedImageUri);
                    Bitmap image_bitmap = BitmapFactory.decodeFile(imagePath);
                    ExifInterface exif = new ExifInterface(imagePath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    image_bitmap = rotate(image_bitmap, exifDegree);

                    image.setImageBitmap(image_bitmap);
                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show()
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

                     */

    private String getRealPathFromURI(Uri contentUri)
    {
        if (contentUri.getPath().startsWith("/storage"))
          {
              return contentUri.getPath();
          }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally { cursor.close(); } return null;
    }


    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);
            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    public String getPath1(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    public static Bitmap loadBitmap(String path, int orientation, final int targetWidth, final int targetHeight) {
        Bitmap bitmap = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int sourceWidth, sourceHeight;
            if (orientation == 90 || orientation == 270) {
                sourceWidth = options.outHeight;
                sourceHeight = options.outWidth;
            } else {
                sourceWidth = options.outWidth;
                sourceHeight = options.outHeight;
            }
            if (sourceWidth > targetWidth || sourceHeight > targetHeight) {
                float widthRatio = (float)sourceWidth / (float)targetWidth;
                float heightRatio = (float)sourceHeight / (float)targetHeight;
                float maxRatio = Math.max(widthRatio, heightRatio);
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int)maxRatio;
                bitmap = BitmapFactory.decodeFile(path, options);
            } else {
                bitmap = BitmapFactory.decodeFile(path);
            }
            if (orientation > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            sourceWidth = bitmap.getWidth();
            sourceHeight = bitmap.getHeight();
            if (sourceWidth != targetWidth || sourceHeight != targetHeight) {
                float widthRatio = (float)sourceWidth / (float)targetWidth;
                float heightRatio = (float)sourceHeight / (float)targetHeight;
                float maxRatio = Math.max(widthRatio, heightRatio);
                sourceWidth = (int)((float)sourceWidth / maxRatio);
                sourceHeight = (int)((float)sourceHeight / maxRatio);
                bitmap = Bitmap.createScaledBitmap(bitmap, sourceWidth, sourceHeight, true);
            }
        } catch (Exception e) {
        }
        return bitmap;
    }


}

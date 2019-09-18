package com.example.seoulapp.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.seoulapp.R;

public class CreateReviewAct extends AppCompatActivity {
    InputMethodManager Imm;
    EditText et1;
    EditText et2;
    ImageView imgView;
    LinearLayout layout;
    Button to_review_bnt;
    Toolbar mToolbar;

    int GET_GALLERY_IMAGE =200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_view);

        Imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        et1=findViewById(R.id.ReviewWriting_btn);
        et2=findViewById(R.id.StoreWriting_btn);
        layout = findViewById(R.id.ReviewLayout);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);

    /*  ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); //
        actionBar.setHomeAsUpIndicator(R.drawable.backbutton);

        getSupportActionBar().setTitle("Writing");
        //mToolbar.setTitleTextColor(Color.rgb(30,135,188));*/

        imgView = findViewById(R.id.Insert_img);
        imgView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        to_review_bnt = findViewById(R.id.gotoReview);
        to_review_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //종료 시 데이터 전달 가능하도록 하는 함수를 집어넣자
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            imgView.setImageURI(selectedImageUri);
        }
    }
    public void linearOnClick(View v){
        Imm.hideSoftInputFromWindow(et1.getWindowToken(), 0);
        Imm.hideSoftInputFromWindow(et2.getWindowToken(), 0);
    } //공백을 넣어줘야겠당...

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.backbutton_, menu);
        return true;
    }

    @Override //버튼 눌렀을 시의 응답
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_button) { //fragment로 돌아가야함!
            finish(); //이미지랑 댓글 상가 저장하고 가야함.
            //process your onClick here
        }
        return super.onOptionsItemSelected(item);
    }

}

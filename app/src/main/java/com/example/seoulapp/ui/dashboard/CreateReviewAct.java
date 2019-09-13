package com.example.seoulapp.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;

public class CreateReviewAct extends AppCompatActivity {

    ImageView imgView;
    int GET_GALLERY_IMAGE =200;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu._action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_view);

        imgView = findViewById(R.id.Insert_img);
        imgView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);

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
}

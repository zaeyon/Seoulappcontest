package com.example.seoulapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class ImageGridAdapter extends BaseAdapter {

    Context context = null;

    // 이 배열의 원소들은 자식 뷰의 imageView 뷰들이 무엇을 보여주는지 결정
    String[] shopProduction = null;

    public ImageGridAdapter(Context context, String[] production) {
        this.context = context;
        this.shopProduction = production;
    }

    public int getCount() {
        return (null != shopProduction) ? shopProduction.length : 0;
    }

    public Object getItem(int position) {
        return (null != shopProduction) ? shopProduction[position] : 0;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("shopProduction : " ,shopProduction[0]);

        ImageView imageView;

        if(convertView == null) {
          imageView = new ImageView(context);
          imageView.setLayoutParams(new GridView.LayoutParams(474, 474));
          imageView.setScaleType(ImageView.ScaleType.FIT_XY);
          imageView.setPadding(1, 0, 0, 0);
        }
        else {
               imageView = (ImageView)convertView;
        }

        Glide.with(context).load(shopProduction[position]).into(imageView);

        // 사진 항목들의 클릭을 처리하는 ImageClickListener 객체를 정의
        // 그리고 그것을 ImageView의 크릭 리스너로 설정합니다.
            ImageClickListener imageViewClickListener = new ImageClickListener(context, shopProduction[position]);
            imageView.setOnClickListener(imageViewClickListener);
        return imageView;
        }
 }

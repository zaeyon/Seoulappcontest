package com.example.seoulapp.ui.dashboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seoulapp.ListViewItem;
import com.example.seoulapp.R;

import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter {
    static int count;
    ArrayList<ReviewItem> list = new ArrayList<>();

    //개수 세어주는 거
    @Override
    public int getCount() {
        return 4;
    }

    //데이터 보내는 거
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    //위치 알려주는 거
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override //index, 우리가 보낸 view, view 그룹
    public View getView(int position, View convertView, ViewGroup parent) {

        Context c= parent.getContext();
        if(convertView ==null){
            LayoutInflater li= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.review_listview_itemview, parent, false);
        }
        TextView tv= convertView.findViewById(R.id.User_id);
        final TextView tv_like = convertView.findViewById(R.id.like);
        ImageView iv = convertView.findViewById(R.id.User_img);
        ImageView iv_User_pro_picture = convertView.findViewById(R.id.User_profilePicture);
        final ImageView iv_heart_filled = convertView.findViewById(R.id.love_filed);
        iv_heart_filled.setVisibility(View.INVISIBLE);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_heart_filled.setVisibility(View.VISIBLE);
                iv_heart_filled.animate().setDuration(300).start();
                count++;
                tv_like.setText("좋아요  "+count+"회");
            }
        });

        iv_User_pro_picture.setBackground(new ShapeDrawable(new OvalShape()));
        iv_User_pro_picture.setClipToOutline(true);



        ReviewItem _list = list.get(position);

        tv.setText(_list.getUserId());
        iv.setImageDrawable(_list.getUserimg());
        iv_User_pro_picture.setImageDrawable(_list.getUserProfileimg());


        return convertView;
    }

    public void addItem(Drawable img1, Drawable img2, String id, String tv){ //인스타 이미지 ,id, 스토리 내용
        ReviewItem ri = new ReviewItem();

        ri.setUserProfileimg(img1);
        ri.setUserImage(img2);
        ri.setUserId(id);
        list.add(ri);

    }
}


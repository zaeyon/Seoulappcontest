package com.example.seoulapp.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.seoulapp.R;

import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter {

    static Activity activity;
    public Object setAdapter;
    String[] User_profile_img;
    String[] User_id;
    String[] User_Content;
    String[] Review_StoreName;
    int like;
    String[] Distinguish_content;
    String comment;
    String comment_Id;


    static int count;
    ArrayList<ReviewItem> list = new ArrayList<>();
    //개수 세어주는 거
    public ReviewAdapter(){

 }

    public void ReviewsetAct(Activity act){
        this.activity = act;
 }
    public Activity ReviewgetAct() {
        return activity;
    }

    @Override
    public int getCount() {
        return list.size();
    } //할 방법이 없을까...? db에서 count한다

    //데이터 보내는 거
    public Object getItem(int position) {
        return list.get(position);
    }

    //위치 알려주는 거
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {//index, 우리가 보낸 view, view 그룹

        final Context c= parent.getContext(); //Context는 app에 대한 구분을 짓는 정보들..

        if(convertView ==null){
            LayoutInflater li= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.review_listview_itemview, parent, false);
        }

        TextView tv_id= convertView.findViewById(R.id.User_id);
        ImageView iv = convertView.findViewById(R.id.User_img);
        ImageView iv_User_pro_picture = convertView.findViewById(R.id.User_profilePicture);
        final ImageView iv_heart_filled = convertView.findViewById(R.id.love_filed);
        TextView watch_comment = convertView.findViewById(R.id.watching_comment);
        TextView User_description = convertView.findViewById(R.id.User_Description);
        TextView User_store = convertView.findViewById(R.id.User_Store);
        final TextView Like = convertView.findViewById(R.id.like); //좋아요 0회
        final EditText Cmt=convertView.findViewById(R.id.comment);
        final TextView input_cmt = convertView.findViewById(R.id.Input_hint); input_cmt.setVisibility(View.INVISIBLE);
      //  TextView Cmt_Id = convertView.findViewById(R.id.comment_id); //id값이 없네..

        iv_User_pro_picture.setBackground(new ShapeDrawable(new OvalShape()));
        iv_User_pro_picture.setClipToOutline(true); //프사 동그랗게 만들기

        ReviewItem Ri = list.get(position); //위젯에 대한 참조 기능 획득

        //Glide.with(c).load(Ri.getUserimg()).into(iv);
        tv_id.setText(Ri.getUserId());
        User_description.setText(Ri.getUserDes());
        User_store.setText(Ri.getStoreName());
        //like = Integer.parseInt(Like.getText().toString()); //int로 바뀌어씁니당
       // Like.setText(Ri.getLike()); //db에 든 like임.이게 int로 바뀌어야;

      //  Cmt_Id.setText(Ri.getCmt_Id());

        Cmt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_cmt.setVisibility(View.VISIBLE);
                input_cmt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //db랑 연결을 해야지...
                        if(Cmt == null){
                            Context context = parent.getContext();
                            Toast.makeText(context, "댓글을 쓰지 않았어요!",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{
                            //db랑 연결하는 코드를 작성합시다^^...


                        }
                    }
                });
            }
        });

        /*watch_comment.setOnClickListener(new View.OnClickListener() { //하트 애니메이션
            @Override
            public void onClick(View v) { //db에 좋아요(like) 컬럼 추가
                Context context = parent.getContext();
                Intent intent = new Intent(context, CommentFrag.class);
                ((Activity)context).startActivity(intent);
                //Like.setText("좋아요 "+like+"회");
            }
        });*/


        iv_heart_filled.setVisibility(View.INVISIBLE); //하트 출몰
        iv.setOnClickListener(new View.OnClickListener() { //하트 애니메이션
            @Override
            public void onClick(View v) { //db에 좋아요(like) 컬럼 추가
                Animation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(1000);
                iv_heart_filled.setVisibility(View.INVISIBLE);
                iv_heart_filled.setAnimation(animation2);
                iv_heart_filled.animate().setDuration(100).start();//count를 linear layout마다 따로..
                like++;
                Like.setText("좋아요 "+like+"회");
            }
        });

        return convertView;
    }

    //item 추가
    public void addItem(String img2, String str3, String str4, String str5, int like){
        //프로필 이미지, 메인이미지 ,id, 스토리, 상점, 좋아요, 댓글 아이디, 댓글
        ReviewItem ri = new ReviewItem();

         //db에서 불러온 값들을 저장해준다.
        ri.setUserImage(img2);
        ri.setUserId(str3);
        ri.setUserDes(str4);
        ri.setStoreName(str5);
        ri.setLike(like);
        list.add(ri);

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ReviewAdapter listAdapter = new ReviewAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}


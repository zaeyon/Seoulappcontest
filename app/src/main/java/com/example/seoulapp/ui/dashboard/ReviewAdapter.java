package com.example.seoulapp.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.util.Log;
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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

public class ReviewAdapter extends BaseAdapter {

    static AppCompatActivity activity;
    public Object setAdapter;
    String[] User_profile_img;
    String[] User_id;
    String[] User_Content;
    String[] Review_StoreName;
    int like;
    String[] Distinguish_content;
    String comment;
    String comment_Id;
    File file;
    static int increace_heart;


    ArrayList<ReviewItem> list = new ArrayList<>(); //
    public ReviewAdapter() {

    }

    public void ReviewsetAct(AppCompatActivity act) {
        this.activity = act;
    }

    public AppCompatActivity ReviewgetAct() {
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
    public View getView(final int position, View convertView, final ViewGroup parent) {//index, 우리가 보낸 view, view 그룹

        final Context c = parent.getContext(); //Context는 app에 대한 구분을 짓는 정보들..


        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.review_listview_itemview, parent, false);
        }

        TextView ReviewId = convertView.findViewById(R.id.User_id);
        ImageView ReviewImg = convertView.findViewById(R.id.User_img);

        ImageView profilePicture = convertView.findViewById(R.id.User_profilePicture);
        final ImageView heart_filled = convertView.findViewById(R.id.love_filed);
        TextView watch_comment = convertView.findViewById(R.id.watching_comment);
        TextView User_description = convertView.findViewById(R.id.User_Description);
        TextView User_store = convertView.findViewById(R.id.User_Store);
        final TextView Like = convertView.findViewById(R.id.like); //출력을 위한 거고
        final EditText Cmt = convertView.findViewById(R.id.comment);

        final TextView input_cmt = convertView.findViewById(R.id.Input_hint);
        input_cmt.setVisibility(View.INVISIBLE);
        //  TextView Cmt_Id = convertView.findViewById(R.id.comment_id); //id값이 없네..

        profilePicture.setBackground(new ShapeDrawable(new OvalShape()));
        profilePicture.setClipToOutline(true); //프사 동그랗게 만들기

        ReviewItem Ri = list.get(position); //위젯에 대한 참조 기능 획득
        Log.d("11111", String.valueOf(Ri));

        //Glide.with(c).load(Ri.getUserProfileimg()).into(iv_User_pro_picture);
        Glide.with(c).load(Ri.getUserimg()).into(ReviewImg); //0번째 사진이 덮여씀
        ReviewId.setText(Ri.getUserId());
        User_description.setText(Ri.getUserDes());
        User_store.setText(Ri.getStoreName());
        //이미 list에는 int로 저장이 되어있다..

        //  Cmt_Id.setText(Ri.getCmt_Id());

        Cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_cmt.setVisibility(View.VISIBLE);
                input_cmt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //db랑 연결을 해야지...
                        if (Cmt == null) {
                            Context context = parent.getContext(); //baseadapter
                            Toast.makeText(context, "댓글을 쓰지 않았어요!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            //db랑 연결하는 코드를 작성합시다^^...
                            Log.e("111111111", "들어왔엉1");
                            new JSONTaskStoreComment().execute("http://192.168.43.102:3000/StoreComment");
                        }
                    }
                });
            }
        });

        watch_comment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){ //db에 좋아요(like) 컬럼 추가
            Context context = parent.getContext();
            Intent intent = new Intent(context, Comment.class);


            ((Activity) context).startActivity(intent);
            //Like.setText("좋아요 "+like+"회");
            }
        });

        heart_filled.setVisibility(View.INVISIBLE); //하트 출몰
        ReviewImg.setOnClickListener(new View.OnClickListener() { //하트 애니메이션
            @Override
            public void onClick(View v) { //db에 좋아요(like) 컬럼 추가
                ReviewItem list_number = list.get(position); //몇번째인지
                increace_heart = list_number.getLike();
                increace_heart++;
                Like.setText("좋아요 "+increace_heart+"회");
                list_number.setLike(increace_heart);

                Animation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(1000);
                heart_filled.setVisibility(View.INVISIBLE);
                heart_filled.setAnimation(animation2);
                heart_filled.animate().setDuration(100).start();//count를 linear layout마다 따로.
                //like의 개수가 나오겠죠?

            }
        });

        return convertView;
    }

    //item 추가
    public void addItem(String img2, String str3, String str4, String str5, int Numbering) {
        //프로필 이미지, 메인이미지 ,id, 스토리, 상점, 좋아요, 댓글 아이디, 댓글
        ReviewItem ri = new ReviewItem();

        //    ri.setUserProfileimg(img1); //db에서 불러온 값들을 저장해준다.
        ri.setUserImage(img2);
        ri.setUserId(str3);
        ri.setUserDes(str4);
        ri.setStoreName(str5);
        ri.setLike(Numbering);
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

    public class JSONTaskStoreComment extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다. //코멘트르 집어
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Comment", comment);
                jsonObject.accumulate("comment_Id", comment_Id);

                HttpURLConnection con = null;
                //HttpURLConnection class is an abstract class directly extending from URLConnection class.
                // It includes all the functionality of its parent class with additional HTTP specific features.
                // HttpsURLConnection is another class which is used for the more secured HTTPS protocol.
                BufferedReader reader = null;

                try {
                    //URL url = new URL("http://192.168.25.16:3000/users%22);
                    URL url = new URL(urls[0]);
                    //연결을 함
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

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

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
                            reader.close();//버퍼를 닫아줌
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
        protected void onPostExecute(String result) { //result 값은 2임.
            super.onPostExecute(result); //undefined라고 떠

        }
    }
}

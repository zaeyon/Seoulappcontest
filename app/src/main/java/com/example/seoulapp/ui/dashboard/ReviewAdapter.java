package com.example.seoulapp.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.seoulapp.MainActivity;
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

    static int increace_heart;
    EditText Cmt;

    int innumber;
    int getposition;
    int heartNum;
    ImageView ReviewImg; //리뷰 이미지
    ImageView empty_love;
    ImageView full_heart;
    ImageView Review_profilePicture; //프로필 이미지 아이디
    ImageView mCommentPicture;
    TextView nickname;

    String strNickname;
    String strCmt;
    String strEmail;
    String UserEmail;

    Context context;
    Context c;

    ArrayList<ReviewItem> list = new ArrayList<>(); //아이템들 추가되어있음.

    @Override
    public int getCount() {
        return list.size();
    } //할 방법이 없을까...?

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
        SharedPreferences auto = c.getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = auto.getString("inputId", "null");
        //이메일을 가져왔잖아? 그걸 보내서 꺼내오라는 의미 아닌가요


        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.review_listview_itemview, parent, false);
        }

        //아이템들 getItem
        TextView ReviewId = convertView.findViewById(R.id.User_id);
        ReviewImg = convertView.findViewById(R.id.User_img);

        ImageView Review_profilePicture = convertView.findViewById(R.id.User_profilePicture); //프로필 사진
        final ImageView heart_filled = convertView.findViewById(R.id.love_filed);
        TextView watch_comment = convertView.findViewById(R.id.watching_comment);
        final TextView User_description = convertView.findViewById(R.id.User_Description);
        TextView User_store = convertView.findViewById(R.id.User_Store);
        final TextView Like = convertView.findViewById(R.id.like);
        final EditText Cmt = convertView.findViewById(R.id.comment); //editText
        mCommentPicture = convertView.findViewById(R.id.mCommentImg);
        empty_love = convertView.findViewById(R.id.love_emtpy);
        full_heart = convertView.findViewById(R.id.love_filed);

        nickname = (TextView) View.inflate(c, R.layout.activity_edit_profile, null).findViewById(R.id.cetNickname);

        //set profile 파일에서 설정된 프로필 사진
        final TextView input_cmt = convertView.findViewById(R.id.Input_comment);
        input_cmt.setVisibility(View.INVISIBLE);

        Review_profilePicture.setBackground(new ShapeDrawable(new OvalShape()));
        Review_profilePicture.setClipToOutline(true); //프사 동그랗게 만들기

        final ReviewItem Ri = list.get(position); //위젯에 대한 참조 기능 획득
        ReviewAdapter adapter = new ReviewAdapter();
        increace_heart = Ri.getLike(); //like를 불러옴
        Like.setText("좋아요 " + increace_heart + "회");


        //user 테이블에서 가져온 이미지를 써야함

        ; // 사진이 덮여씀
        //Glide.with(c).load(Ri).into()
        ReviewId.setText(Ri.getUserId());
        User_description.setText(Ri.getUserDes());
        User_store.setText(Ri.getStoreName());
        strNickname = nickname.getText().toString(); //Nickname 가져와씁ㄴ디ㅏㅇ
        Glide.with(c).load(Ri.getUserProfilePicture()).into(Review_profilePicture); //완성

        Log.e("scollView", String.valueOf(list.get(position)));

        //  Cmt_Id.setText(Ri.getCmt_Id());
        Cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                input_cmt.setVisibility(View.VISIBLE);
                input_cmt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //cmt에 담길 시간이 필요한가
                        ReviewItem item_ = new ReviewItem();
                        item_ = list.get(position);
                        innumber = item_.getNumber(); //위치가 확실한 값(댓글)

                        Log.e("입력을 눌렀습니다.", String.valueOf(list.get(position)));
                        strCmt = Cmt.getText().toString();
                        if (strCmt.equals("")) { //
                            Context context = parent.getContext();
                            Toast.makeText(context, "댓글을 써 주세요!", Toast.LENGTH_LONG).show();
                        } else {
                            //db랑 연결하는 코드를 작성합시다^^...

                            Context context = parent.getContext();
                            Toast.makeText(context, "댓글을 저장했습니다!", Toast.LENGTH_LONG).show();
                            Log.e("댓글 저장", "들어왔어용");
                            new JSONTaskStoreComment().execute("http://192.168.43.102:3000/StoreComment");
                            Cmt.setText("");

                        }
                    }
                });
            }
        });

        watch_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //db에 좋아요(like) 컬럼 추가
                //만약 댓글이 없을 경우 띄우는 화면도 설정
                ReviewItem Cm = new ReviewItem();

                Cm = list.get(position); //리뷰 아이템이므로 ㅇㅇ ㄱㅊ
                int a = Cm.getNumber();
                Log.e("11111112222", String.valueOf(a));
                Context context = parent.getContext();

                Intent intent = new Intent(context, Comment.class);
                intent.putExtra("ReviewStory", User_description.getText().toString());
                intent.putExtra("Position", a);
                ((Activity) context).startActivity(intent);
                //Like.setText("좋아요 "+like+"회");

            }
        });

        //
        // new JSONTaskgetImg().execute("http://192.168.43.102:3000/getCommentProfileImg");
        heart_filled.setVisibility(View.INVISIBLE);
        full_heart.setVisibility(View.INVISIBLE);
        final ReviewItem list_number = list.get(position);
        ReviewImg.setOnClickListener(new View.OnClickListener() { //하트 애니메이션
            @Override
            public void onClick(View v) { //db에 좋아요(like) 컬럼 추가
                //아이디랑 상관 없는데..
                heartNum = list_number.getNumber(); //좋아요 얻는 거
                increace_heart = list_number.getLike();
                increace_heart++;
                Like.setText("좋아요 " + increace_heart + "회"); //고정을 어케함
                list_number.setLike(increace_heart); //like 저장*/
                new JSONTaskaddLike().execute("http://192.168.43.102:3000/addLike");

                Animation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(1000);
                heart_filled.setVisibility(View.INVISIBLE);
                heart_filled.setAnimation(animation2);
                heart_filled.animate().setDuration(100).start();//count를 linear layout마다 따로.
                //like의 개수가 나오겠죠?

                /*empty_love.setImageResource(R.drawable.canheart);
                empty_love.invalidate(); //화면 뿌려주는 역할*/

            }
        });

        return convertView;
    }

    //item 추가
    public void addItem(String img2, String str3, String str4, String str5, int like, int Numbering, String profileImg) {
        //프로필 이미지, 메인이미지 ,id, 스토리, 상점, 좋아요, 댓글 아이디, 댓글
        ReviewItem ri = new ReviewItem();


        ri.setUserImage(img2);
        ri.setUserId(str3);
        ri.setUserDes(str4);
        ri.setStoreName(str5);
        ri.setLike(like);
        ri.setNumber(Numbering);
        ri.setUserProfilePicture(profileImg);
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

    //하트 추가
    public class JSONTaskaddLike extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) { //테이블을 하나 만드는 것도 생각해보자
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다. //코멘트르 집어
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("like", String.valueOf(increace_heart));//String형으로 해야 함
                jsonObject.accumulate("numbering", String.valueOf(heartNum));
                //jsonObject.accumulate("comment_Id", comment_Id);
                Log.d("increace_heart : ", String.valueOf(increace_heart));

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
            super.onPostExecute(result);

        }
    }

    public class JSONTaskStoreComment extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) { //테이블을 하나 만드는 것도 생각해보자
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다. //코멘트르 집어
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Comment", strCmt);
                jsonObject.accumulate("CommentUser_Id", strNickname);
                jsonObject.accumulate("position", innumber); //게시물 id값
                jsonObject.accumulate("Email", strEmail);

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
            super.onPostExecute(result);

        }

    }

/*    class JSONTaskgetImg extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("inputId", strEmail); //현재의 값은 코멘트

                HttpURLConnection con = null;
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
        protected void onPostExecute(String s) { //가져오는 거죠
            super.onPostExecute(s); //s로 image를 받음
            if (!s.equals("")) { //내 프로필
                Glide.with(context).load(s).into(mCommentPicture); //프로필에
            }
        }
    }*/

}





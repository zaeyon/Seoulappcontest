package com.example.seoulapp.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.seoulapp.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import com.example.seoulapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Comment extends AppCompatActivity {

    String[] array_commentInfo;
    String[] comment_arr;
    String[] commentUser_Id;
    String[] PageNumber;
    String[] commentImg;

    int input_commentNumber;
    TextView comment_get;
    TextView commentId_get;
    TextView story;
    ListView CmtList;
    TextView TextId;
    ImageView CmtUserImg;
    String strEmail;
    String pictureUrl;
    ImageView PictureAtComment;
    String[] allView;
    Context context;
    int positionget;
    LinearLayout commentLinearLay;
    ArrayList<CommentItem> cmt_list = new ArrayList<CommentItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment); //


        story = findViewById(R.id.Comment_ReviewStory);
        comment_get = findViewById(R.id.comment_content);
        commentId_get = findViewById(R.id.comment_id);
        CmtList = findViewById(R.id.commentListView);
        commentLinearLay = findViewById(R.id.UserCommentAndId);
        CmtUserImg = findViewById(R.id.ivMyProfile); //댓글 이미지당 ㅇㅇ..

        Log.e("text", String.valueOf(cmt_list.size()));
        Intent intent = getIntent();
        story.setText(intent.getStringExtra("ReviewStory"));
        input_commentNumber = intent.getIntExtra("Position", 0); //위치를 받아옴
        pictureUrl = intent.getStringExtra("PictureUrl");


        new JSONTaskCommentInfo().execute("http://dongdong.com.ngrok.io/getCommentInfo");
                SharedPreferences auto = this.getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = auto.getString("inputId", "null");
    }

    class JSONTaskCommentInfo extends AsyncTask<String, String, String> {
        // 댓글 불러오기
        @Override
        protected String doInBackground(String... urls) { //끌어오는 거
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("PositionOfCmt", String.valueOf(input_commentNumber));

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
        protected void onPostExecute(String result) { //데이터 불러오기
            super.onPostExecute(result); //undefined라고 떠
            Log.e("결과", result);
            CommentAdapter adapter = new CommentAdapter();

            if (result.equals("")) {
                TextView TextId;
                ListView CmtList;
                TextId = findViewById(R.id.commentTextView);
                CmtList = findViewById(R.id.commentListView);
                TextId.setVisibility(View.VISIBLE);
                CmtList.setVisibility(View.INVISIBLE);
                  } else {
                TextId = findViewById(R.id.commentTextView);
                TextId.setVisibility(View.INVISIBLE);

                array_commentInfo = result.split("/");
                Log.e("숫자 알아보기 ", String.valueOf(array_commentInfo.length)); //1
                comment_arr = array_commentInfo[0].split("\\|");
                commentUser_Id = array_commentInfo[1].split("\\|");
                PageNumber = array_commentInfo[2].split("\\|"); //getposition의 값이 남아 있음.
                commentImg = array_commentInfo[3].split("\\|");

                for (int i = 0; i < comment_arr.length; i++) { //처음엔 2니까..
                    String _comment = comment_arr[i];
                    String commentId = commentUser_Id[i];
                    String pageNum = PageNumber[i];
                    String CmtImg = commentImg[i];

                    int Page_num = Integer.parseInt(pageNum);
                    adapter.addItem(_comment, commentId, Page_num, CmtImg, pictureUrl);
                    CmtList.setAdapter(adapter); //
                }
            }
        }
    }

}
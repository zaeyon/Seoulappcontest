package com.example.seoulapp.ui.dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

    int input_commentNumber;
    TextView comment_get;
    TextView commentId_get;
    TextView story;
    ListView CmtList;
    TextView TextId;

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

        Log.e("text", String.valueOf(cmt_list.size()));
            Intent intent = getIntent();
            story.setText(intent.getStringExtra("ReviewStory"));
            input_commentNumber = intent.getIntExtra("Position",0); //위치를 받아옴
            new JSONTaskCommentInfo().execute("http://192.168.43.102:3000/getCommentInfo");
            
        // positionget = (int) new CommentAdapter().getItemId();
        /*delete_cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTaskDeleteCmt().execute("http://192.168.43.102:3000/DeleteCmt");
            }
        });*/

        //CommentAdapter adapter = new CommentAdapter();
        //여기서 어떻게 서버에 get(position)을 넣을 수 있는 지를 생각해보자;;
        //setAdapter(adapter); //댓글 펼치기

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
            Log.e("결과" , result);
            CommentAdapter adapter = new CommentAdapter();

            if(result.equals("")){
                TextView TextId; ListView CmtList;
                TextId = findViewById(R.id.commentTextView);
                CmtList= findViewById(R.id.commentListView);
                TextId.setVisibility(View.VISIBLE);
                CmtList.setVisibility(View.INVISIBLE);
                /*Intent intent = new Intent(Comment.this, empty_comment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);*/
            }
            else{

                TextId = findViewById(R.id.commentTextView);
                TextId.setVisibility(View.INVISIBLE);
            array_commentInfo = result.split("\\/");

            Log.e("숫자 알아보기 ", String.valueOf(array_commentInfo.length)); //1
            comment_arr = array_commentInfo[0].split("\\|");
            commentUser_Id = array_commentInfo[1].split("\\|");
            PageNumber = array_commentInfo[2].split("\\|"); //getposition의 값이 남아 있음.


            for (int i = 0; i <comment_arr.length; i++) { //처음엔 2니까..
                    String _comment = comment_arr[i];
                    String commentId = commentUser_Id[i];
                    String pageNum = PageNumber[i];

                    int Page_num = Integer.parseInt(pageNum);
                    adapter.addItem(_comment, commentId, Page_num);
                    CmtList.setAdapter(adapter); //
                }
            }
        }
    }
}

    /*class JSONTaskDeleteCmt extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
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
        }
    }*/

//코멘트 추가



package com.example.seoulapp.ui.dashboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class CommentFrag extends ListFragment {

    String[] array_commentInfo;
    String[] comment;
    String[] commentUser_Id;
    private static final int MODE_PRIVATE = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.commentlayout, container, false);

        //Intent intent = getActivity().getIntent();
        //String comment = intent.getExtras().getString("comment");
        //comment를 adapter 거치지 않고 그대로 여기로 오기 때문에 item에 저장을 해봤자 null겠ㅈ?
        //String comment_Id = intent.getExtras().getString("comment_Id");

        new JSONTaskCommentInfo().execute("http://172.30.1.28:3000/getCommentInfo");
        CommentAdapter adapter = new CommentAdapter();
        setListAdapter(adapter); //adapter에는 지금 comment들이 달려있음요

        /*SharedPreferences cmt = getActivity().getSharedPreferences(comment, MODE_PRIVATE); //sharedPreference 생성
        SharedPreferences.Editor editor = cmt.edit();

        editor.putString("First", comment); //First라는 key값으로 infoFirst 데이터를 저장한다.
        editor.putString("Second", comment_Id); //Second라는 key값으로 infoSecond 데이터를 저장한다.
        editor.commit();

        cmt.getString("First", "");
        cmt.getString("Second", ""); //차피 얘가 몇 번째의 게시물에서 댓글 썼는 지 알아야 해서 db에 꼭 들어가야함 */

        // ReviewAdapter review_adapter = new ReviewAdapter(CommentAct.this, this);;
        //여기서 comment를 뿌려준다.

        return view;

    }

    public class JSONTaskCommentInfo extends AsyncTask<String, String, String> {

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
        protected void onPostExecute(String result) { //result 값은 2임.
            super.onPostExecute(result); //undefined라고 떠

            CommentAdapter adapter = new CommentAdapter();
            array_commentInfo = result.split("\\/");

            comment = array_commentInfo[0].split("\\|");
            commentUser_Id= array_commentInfo[1].split("\\|");

            for(int i=0; i<2; i++){
                String _comment = comment[i];
                String commentId = commentUser_Id[i];
                adapter.addItem(_comment, commentId);
            }
        }

    }
}


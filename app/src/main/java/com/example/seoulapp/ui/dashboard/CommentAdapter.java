package com.example.seoulapp.ui.dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

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
public class CommentAdapter extends BaseAdapter {

    ArrayList<CommentItem> cmt_list = new ArrayList<>();

    TextView comment;
    TextView comment_Id;
    LinearLayout forDeleteClick;

    int clickNumber;
    Context context;


    int commentNumber;
    @Override
    public int getCount() {
        return cmt_list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    } //댓글에 대한 위치를 조정!

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext(); //Context는 app에 대한 구분을 짓는 정보들..
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = li.inflate(R.layout.comment_item, parent, false);
        }

        comment = convertView.findViewById(R.id.comment_content);
        comment_Id = convertView.findViewById(R.id.comment_id);
        forDeleteClick = convertView.findViewById(R.id.CommentforClick);
        commentNumber = (int) getItemId(position)+1;


        forDeleteClick.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CommentItem Ca= new CommentItem(); //ca는 아이템이잖소
                Ca = cmt_list.get(position); //ca는 아이템에 대한 정보를 얻습니당,
                clickNumber = Ca.getPageNumber(); //여기서의 정보를 얻어야 함.

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new JSONTaskCommentDelete().execute("http://192.168.43.102:3000/CommentDelete");
                                forDeleteClick.setVisibility(View.INVISIBLE);
                                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        });
                builder.show();
                return false;
            }
        });

            CommentItem CA = cmt_list.get(position);
            //positionget = (int) getItem(position)+1; //댓글 간의 구분용임 게시물의 구분용이 아님
            comment.setText(CA.getComment_content());
            comment_Id.setText(CA.getComment_id());

        // nickname = (TextView) View.inflate(context,R.layout.activity_edit_profile, null).findViewById(R.id.cetNickname);
        return convertView;
    }
    public void addItem(String strcmt, String strcmtId, int Num) {
        CommentItem Cmt_item = new CommentItem();

        Cmt_item.setComment_id(strcmtId);
        Cmt_item.setComment_content(strcmt);
        Cmt_item.setPageNumber(Num);

        cmt_list.add(Cmt_item); //list에 저장
    }

    class JSONTaskCommentDelete extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("dis_number", String.valueOf(clickNumber));
                HttpURLConnection con = null;
                BufferedReader reader = null;
 // 리스트 아이템을 생성하고, 리스트의 아이템 포지션을 들고온 다음,
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
            super.onPostExecute(result);
        }
    }
}
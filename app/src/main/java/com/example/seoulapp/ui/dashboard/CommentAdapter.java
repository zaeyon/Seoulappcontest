package com.example.seoulapp.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.seoulapp.R;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    ArrayList<CommentItem> cmt_list = new ArrayList<>();

    TextView comment;
    TextView comment_Id;
    int pos_get;
    Context context;
    TextView story;
    String descriotion;
    int DbNum;
    String strNum;
    int compareNum;
    int positionget;
    TextView nickname;


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
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext(); //Context는 app에 대한 구분을 짓는 정보들..
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = li.inflate(R.layout.comment_item, parent, false);
        }

        comment = convertView.findViewById(R.id.comment_content);
        comment_Id = convertView.findViewById(R.id.comment_id);

        CommentItem CA = cmt_list.get(position); //
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
}
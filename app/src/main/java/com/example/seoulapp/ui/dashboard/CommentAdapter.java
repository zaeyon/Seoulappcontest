package com.example.seoulapp.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.seoulapp.R;

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    ArrayList<CommentItem> cmt_list = new ArrayList<>();
    TextView comment;
    TextView comment_Id;

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context= parent.getContext(); //Context는 app에 대한 구분을 짓는 정보들..

        if(convertView ==null){
            LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.comment_item, parent, false);
        }

        comment = convertView.findViewById(R.id.comment_content);
        comment_Id = convertView.findViewById(R.id.comment_id); //근데 뭐냐하면 db에 있는 값을 여기로 가져와줘야하지 않나?

        CommentItem CA = cmt_list.get(position);

        comment.setText(CA.getComment_content());
        comment_Id.setText(CA.getComment_id());

        return convertView;
    }
    public void addItem(String strcmt, String strcmtId){
        CommentItem Cmt_item = new CommentItem();

        Cmt_item.setComment_id(strcmtId);
        Cmt_item.setComment_content(strcmt);

        cmt_list.add(Cmt_item); //list에 저장
    }
}

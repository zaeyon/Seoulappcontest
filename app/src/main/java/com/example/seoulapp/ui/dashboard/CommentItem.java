package com.example.seoulapp.ui.dashboard;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentItem {
    String commentImg;
    String comment_id;
    String comment_content;

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setCommentImg(String commentImg) {
        this.commentImg = commentImg;
    }

    public String getCommentImg() {
        return commentImg;
    }

    public String getComment_content() {
        return comment_content;
    }

    public String getComment_id() {
        return comment_id;
    }
}

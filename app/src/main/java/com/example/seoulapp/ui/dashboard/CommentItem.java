package com.example.seoulapp.ui.dashboard;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentItem {
    String commentImg;
    String comment_id;
    String comment_content;
    String url;
    int PageNumber;

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setCommentImg(String commentImg) {
        this.commentImg = commentImg;
    }

    public void setPageNumber(int pageNumber) {
        PageNumber = pageNumber;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPageNumber() {
        return PageNumber;
    }

    public String getCommentImg() {
        return commentImg;
    } //url 자리임

    public String getComment_content() {
        return comment_content;
    }

    public String getComment_id() {
        return comment_id;
    }

    public String getUrl() {
        return url;
    }
}
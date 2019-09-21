package com.example.seoulapp.ui.dashboard;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ReviewItem {

    private Drawable UserImage;
    private Drawable UserProfileimg;
    private String UserId;
    private String UserDes;

    public void setUserImage(Drawable userImage) {
        UserImage = userImage;
    }
    public void setUserProfileimg(Drawable userProfileimg) {
        UserProfileimg = userProfileimg;
    }
    public void setUserId(String userId) {
        UserId = userId;
    }
    public void setUserDes(String userDes) {
        UserDes = userDes;
    }
    public String getUserId(){
        return UserId;
    }
    public String getUserDes(){
        return UserDes;
    }
    public Drawable getUserimg(){
        return UserImage;
    }
    public Drawable getUserProfileimg() {
        return UserProfileimg;
    }
}


package com.example.seoulapp.ui.dashboard;

public class ReviewItem {

    private String UserImage;
    private String UserProfileimg;
    private String UserId;
    private String UserDes;
    private String StoreName;
    private int Like;
    private String Cmt_Id;
    private String Cmt;

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }


    public void setUserId(String userId) {
        UserId = userId;
    }
    public void setUserDes(String userDes) {
        UserDes = userDes;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public void setLike(int like) {
        Like = like;
    }
    public String getUserId(){
        return UserId;
    }
    public String getUserDes(){
        return UserDes;
    }


    public String getUserimg(){
        return UserImage;
    }


    public String getStoreName() {
        return StoreName;
    }

    public int getLike() {
        return Like;
    }

}


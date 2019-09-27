package com.example.seoulapp.ui.dashboard;

import android.util.Log;

public class ReviewItem {

    private String UserImage;
    private String UserId;
    private String UserDes;
    private String StoreName;
    private String SplitStoreName;
    private String[] splitStore;
    private int number;
    private int Like;
    private String StorePack;

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setUserDes(String userDes) {
        UserDes = userDes;
    }

    public void setStoreName(String storeName){ //하나,둘
        SplitStoreName = "";
        if (storeName.contains(",")) {
            splitStore = storeName.split("\\,"); //split[0] split[1] 이거 두 개

            for(int i=0; i<splitStore.length; i++){
                SplitStoreName = SplitStoreName + "#"+splitStore[i]+"  ";
            } //이제 여기선 splitStore를 반환해야 함.
        } else {
            StoreName = "#" + storeName +" ";
        }
    }

    public void setNumber(int number) {
        this.number = number;
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
        if (splitStore == null) {
            return StoreName;
        } else { //여러 값이 있다면
            Log.e("111111", String.valueOf(splitStore));
            return SplitStoreName;
        }
    }

    public int getLike() {
        return Like;
    }

    public int getNumber() {
        return number;
    }
}



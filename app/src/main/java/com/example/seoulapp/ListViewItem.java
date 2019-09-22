package com.example.seoulapp;

import android.graphics.drawable.Drawable;

public class ListViewItem {
  private String shopProfileImage;
  private String shopName;
  private String shopBuilding;
  private String shopFloor;
  private String shopRocation;
  private String shopCategory;
  private String shopStyle;
  private String shopIntro;
  private String shopRepresentation1;
  private String shopRepresentation2;
  private String shopRepresentation3;


  public void setShopCategory(String category) { shopCategory = category; }
  public void setShopStyle(String style) { shopStyle = style; }
  public void setShopIntro(String intro) { shopIntro = intro; }
  public void setShopBuilding(String build)
  {
    shopBuilding = build;
  }
  public void setShopFloor(String floor) { this.shopFloor = floor; }
  public void setProfileImage(String image) {
    this.shopProfileImage = image;
  }
  public void setShopName(String name) {
    this.shopName = name;
  }
  public void setShopRocation(String rocation){
    shopRocation = rocation;
  }
  public void setShopRepresentation1(String rep) {
    shopRepresentation1 = rep;
  }
  public void setShopRepresentation2(String rep) {
    shopRepresentation2 = rep;
  }
  public void setShopRepresentation3(String rep) {
    shopRepresentation3 = rep;
  }

  public String getShopCategory() { return this.shopCategory; }
  public String getShopStyle() { return this.shopStyle; }
  public String getShopIntro() { return this.shopIntro; }
  public String getProfileImage() {
    return this.shopProfileImage;
  }
  public String getShopName() {
    return this.shopName;
  }
  public String getShopRocation() {
    return this.shopRocation;
  }
  public String getShopRepresentation1() {
    return this.shopRepresentation1;
  }
  public String getShopRepresentation2() {
    return this.shopRepresentation2;
  }
  public String getShopRepresentation3() {
    return this.shopRepresentation3;
  }
  public java.lang.String getShopBuilding() {
    return this.shopBuilding;
  }
  public String getShopFloor() { return this.shopFloor; }

}

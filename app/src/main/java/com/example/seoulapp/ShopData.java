package com.example.seoulapp;

public class ShopData {
    private String shopName;
    private String shopRocation;

    public ShopData(){

    }

    public ShopData(String shopName, String shopRocation)
    {
        this.shopName = shopName;
        this.shopRocation = shopRocation;
    }

    public String getName() {
        return shopName;
    }

    public void setName(String name) {
        this.shopName = name;
    }

    public String getRocation() {
        return shopRocation;
    }

    public void setRocation(String rocation) {
        this.shopRocation = rocation;
    }
}

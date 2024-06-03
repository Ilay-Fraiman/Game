package com.example.firebasetest;

import android.graphics.Bitmap;

public class GameObject {
    private Bitmap sprite;

    private float widthPercentage;

    private float heightPercentage;

    private float xPercentage;

    private float yPercentage;

    protected int roomID;

    public GameObject(Bitmap sprite, int ID, float xLocation, float yLocation, float width, float height){}//add location

    public GameObject(Bitmap sprite, int ID, float xLocation, float yLocation)//character
    {
        //rest of it
        this.widthPercentage = (GameView.width / 15);
        this.heightPercentage = this.widthPercentage;
    }

    public float getWidthPercentage() {
        return widthPercentage;
    }

    public void setWidthPercentage(float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }

    public float getHeightPercentage() {
        return heightPercentage;
    }

    public void setHeightPercentage(float heightPercentage) {
        this.heightPercentage = heightPercentage;
    }

    public float getXPercentage() {
        return xPercentage;
    }

    public void setXPercentage(float xPercentage) {
        this.xPercentage = xPercentage;
    }

    public float getYPercentage() {
        return yPercentage;
    }

    public void setYPercentage(float yPercentage) {
        this.yPercentage = yPercentage;
    }
}

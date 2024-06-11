package com.example.firebasetest;

import android.graphics.Bitmap;

public class GameObject {
    private String spriteName;

    private float widthPercentage;

    private float heightPercentage;

    private float xPercentage;

    private float yPercentage;

    public String getSpriteName() {
        return spriteName;
    }

    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }

    protected int roomID;

    public GameObject(String spriteName, int ID, float xLocation, float yLocation, float width, float height){
        this.spriteName = spriteName;
        this.roomID = ID;
        this.xPercentage = xLocation;
        this.yPercentage = yLocation;
        this.widthPercentage = width;
        this.heightPercentage = height;
    }

    public GameObject(String spriteName, int ID, float xLocation, float yLocation)//character
    {
        this.spriteName = spriteName;
        this.roomID = ID;
        this.xPercentage = xLocation;
        this.yPercentage = yLocation;
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

    public float getDirection()
    {
        float directionAngle = 0;
        if(this instanceof Character)
        {
            double angle = ((Character) this).getDirectionAngle();
            directionAngle = (float) angle;
        }
        else if(this instanceof Projectile)
        {
            double angle = ((Projectile) this).getAngle();
            directionAngle = (float) angle;
        }
        return directionAngle;
    }
}

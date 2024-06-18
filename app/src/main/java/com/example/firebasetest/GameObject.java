package com.example.firebasetest;

import android.graphics.Bitmap;

public class GameObject {
    private String spriteName;

    private float width;

    private float height;

    private float xLocation;

    private float yLocation;

    public String getSpriteName() {
        return spriteName;
    }

    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }

    protected int roomID;

    public GameObject(String spriteName, int ID, float x, float y, float w, float h){
        this.spriteName = spriteName;
        this.roomID = ID;
        this.xLocation = x;
        this.yLocation = y;
        this.width = w;
        this.height = h;
    }

    public GameObject(String spriteName, int ID, float x, float y)//character
    {
        this.spriteName = spriteName;
        this.roomID = ID;
        this.xLocation = x;
        this.yLocation = y;
        this.width = (GameView.width / 15);
        this.height = this.width;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float w) {
        this.width = w;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float h) {
        this.height = h;
    }

    public float getXLocation() {
        return xLocation;
    }

    public void setXLocation(float x) {
        this.xLocation = x;
    }

    public float getYLocation() {
        return yLocation;
    }

    public void setYLocation(float y) {
        this.yLocation = y;
    }

    public float getDirection()
    {
        float directionAngle = 0;
        if(this instanceof Projectile)
        {
            double angle = ((Projectile) this).getAngle();
            directionAngle = (float) angle;
        }
        return directionAngle;
    }

    public int getRoomID()
    {
        return this.roomID;
    }
}

package com.example.firebasetest;

import android.graphics.Bitmap;

public class Projectile extends GameObject{
    protected float horizontalSpeed;
    protected float verticalSpeed;
    protected float power;
    protected Character creator; // is it protected?
    public Projectile(Bitmap sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height){
        super(sprite, ID, xLocation, yLocation, width, height);
        this.power = power;
        //switch sizes like in character
    }

    public float getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public void setHorizontalSpeed(float horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
    }

    public float getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(float verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public void setCreator(Character creator) {
        this.creator = creator;
    }
}

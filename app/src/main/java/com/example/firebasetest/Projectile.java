package com.example.firebasetest;

import android.graphics.Bitmap;

public class Projectile extends GameObject{
    protected float horizontalSpeed;
    protected float verticalSpeed;
    protected float power;
    protected Character creator; // is it protected?
    protected double angle;
    protected String ailment;//poison, freeze, shock, fire
    public Projectile(Bitmap sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, double direction, String effect){
        super(sprite, ID, xLocation, yLocation, width, height);
        this.power = power;
        this.angle = direction;
        this.ailment = effect;
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

    public double getAngle()
    {
        return angle;
    }

    public Character getCreator() {
        return creator;
    }

    public String getAilment() {
        return ailment;
    }

    public void SetAilment(String effect)
    {
        this.ailment = effect;
    }
}

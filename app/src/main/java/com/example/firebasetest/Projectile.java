package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Projectile extends GameObject{
    protected float horizontalSpeed;
    protected float verticalSpeed;
    protected float power;
    protected Character creator; // is it protected?
    protected double angle;
    protected String ailment;//poison, freeze, shock, fire
    protected boolean isTimed;
    protected long TTD;
    private ArrayList<Character> alreadyHit;
    protected boolean oneTimeHit;
    protected boolean moving;
    public Projectile(String sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, double direction, String effect){
        super(sprite, ID, xLocation, yLocation, width, height);
        this.creator = creator;
        this.power = power;
        this.angle = direction;
        this.ailment = effect;
        this.isTimed = false;
        this.horizontalSpeed = hSPD;
        this.verticalSpeed = vSPD;
        this.TTD = 0;
        alreadyHit = new ArrayList<Character>();
        oneTimeHit = false;
        moving = false;
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
    public boolean isTimeUp()
    {
        return ((this.isTimed) && (System.currentTimeMillis() >= this.TTD));
    }

    public boolean canHit(Character chr)
    {
        return !alreadyHit.contains(chr);
    }

    public void hasHit(Character chr)
    {
        if(this.oneTimeHit)
            this.alreadyHit.add(chr);
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public boolean isMoving()
    {
        return this.moving;
    }

    public boolean isTimed()
    {
        return this.isTimed;
    }
}

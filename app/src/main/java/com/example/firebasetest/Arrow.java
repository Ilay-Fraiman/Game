package com.example.firebasetest;

import android.graphics.Bitmap;

public class Arrow extends Projectile{
    private boolean ricochet;
    private boolean homing;
    private boolean poison;
    private double angle;
    public Arrow(Bitmap sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, boolean isRicochet, boolean isHoming, boolean isPoison, double direction)
    {
        super(sprite, ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height);
        this.ricochet = isRicochet;
        this.homing = isHoming;
        this.poison = isPoison;
        this.angle = direction;
    }
    public boolean isPoison()
    {
        return poison;
    }

    public boolean isRicochet() {
        return ricochet;
    }

    public boolean isHoming() {
        return homing;
    }

    public double getAngle() {
        return angle;
    }
}

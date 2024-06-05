package com.example.firebasetest;

import android.graphics.Bitmap;

public class LightLine extends Projectile{
    private String ailment;
    private double angle;
    public LightLine(Bitmap sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, String effect, double direction)
    {
        super(sprite, ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height);
        this.ailment = effect;
        this.angle = direction;
    }

    public String getAilment() {
        return ailment;
    }

    public double getAngle() {
        return angle;
    }
}

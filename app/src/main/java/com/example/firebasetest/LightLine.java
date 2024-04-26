package com.example.firebasetest;

import android.graphics.Bitmap;

public class LightLine extends Projectile{
    private boolean isLaser;
    public LightLine(Bitmap sprite, int ID, boolean lsr, Character creator, float power, float xLocation, float yLocation, float width, float height)
    {
        super(sprite, ID, creator, power, xLocation, yLocation, width, height);
    }
}

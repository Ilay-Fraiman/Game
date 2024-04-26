package com.example.firebasetest;

import android.graphics.Bitmap;

public class Arrow extends Projectile{
    private boolean ricochet;
    private boolean homing;
    private boolean poison;
    public Arrow(Bitmap sprite, int ID, Character creator, float power, float xLocation, float yLocation, float width, float height)
    {
        super(sprite, ID, creator, power, xLocation, yLocation, width, height);
    }
}

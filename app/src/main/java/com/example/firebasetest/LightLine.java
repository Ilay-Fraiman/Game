package com.example.firebasetest;

import android.graphics.Bitmap;

public class LightLine extends Projectile{
    private boolean isLaser;
    public LightLine(Bitmap sprite, int ID, boolean lsr, Character creator)
    {
        super(sprite, ID, creator);
    }
}

package com.example.firebasetest;

import android.graphics.Bitmap;

public class Arrow extends Projectile{
    private boolean ricochet;
    private boolean homing;
    private boolean poison;
    public Arrow(Bitmap sprite, int ID, Character creator)
    {
        super(sprite, ID, creator);
    }
}

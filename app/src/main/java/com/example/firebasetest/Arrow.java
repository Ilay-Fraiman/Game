package com.example.firebasetest;

import android.graphics.Bitmap;

public class Arrow extends Projectile{
    private boolean ricochet;
    private boolean homing;
    private boolean poison;
    public Arrow(Bitmap sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, boolean isRicochet, boolean isHoming, boolean isPoison)
    {
        super(sprite, ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height);
        this.ricochet = isRicochet;
        this.homing = isHoming;
        this.poison = isPoison;
    }
    public boolean isPoison()
    {
        return poison;
    }
}

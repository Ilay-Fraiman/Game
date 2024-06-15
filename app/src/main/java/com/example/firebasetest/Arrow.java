package com.example.firebasetest;

import android.graphics.Bitmap;

public class Arrow extends Projectile{
    private boolean ricochet;
    private boolean homing;
    private boolean toHome;
    private Character homingOn;
    public Arrow(Bitmap sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, boolean isRicochet, boolean isHoming, boolean isPoison, double direction)
    {
        super(sprite, ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height, direction, "none");
        this.ricochet = isRicochet;
        this.homing = isHoming;
        if(isPoison)
            this.ailment = "poison";
        moving = true;
        toHome = true;
        homingOn = null;
    }

    public boolean isRicochet() {
        return ricochet;
    }

    public boolean isHoming() {
        return homing;
    }

    public boolean needsToHome()
    {
        return (this.homing && this.toHome);
    }

    public void home(Character character)
    {
        if(this.homing && this.toHome)
        {
            this.homingOn = character;
            this.toHome = false;
        }
    }

}

package com.example.firebasetest;

import android.graphics.Bitmap;

public class GroundFire extends Projectile{//only if hit wall
    public GroundFire(Bitmap sprite, int ID, Character creator, float power, float xLocation, float yLocation, float width, float height)
    {
        super(sprite, ID, creator, power, 0, 0, xLocation, yLocation, width, height, 0, "fire");
        this.isTimed = true;
        this.power /= 30;
        this.TTD = System.currentTimeMillis() + 2500L;
    }
}

package com.example.firebasetest;

import android.graphics.Bitmap;

public class Mist extends Projectile{
    private long TTD;
    public Mist(Bitmap sprite, int ID, Character creator, float power, float xLocation, float yLocation, float width, float height)
    {
        super(sprite, ID, creator, power, 0, 0, xLocation, yLocation, width, height, 0, "freeze");
        this.TTD = System.currentTimeMillis() + 2500L;
    }

    public long getTTD() {
        return TTD;
    }
}

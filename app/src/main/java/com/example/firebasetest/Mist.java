package com.example.firebasetest;

import android.graphics.Bitmap;

public class Mist extends Projectile{
    public Mist(int ID, Character creator, double power, float xLocation, float yLocation, float width, float height)
    {
        super("mist", ID, creator, power, 0, 0, xLocation, yLocation, width, height, 0, "freeze");
        this.isTimed = true;
        this.power /= 30d;
        this.TTD = System.currentTimeMillis() + 2500L;
    }
}

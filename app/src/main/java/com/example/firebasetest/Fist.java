package com.example.firebasetest;

import android.graphics.Bitmap;

public class Fist extends Projectile{
    public Fist(Bitmap sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, String effect, double direction)
    {
        super(sprite, ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height, direction, effect);
        moving = true;
    }
}

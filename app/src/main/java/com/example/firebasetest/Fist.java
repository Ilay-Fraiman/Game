package com.example.firebasetest;

import android.graphics.Bitmap;

public class Fist extends Projectile{
    public Fist(int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, String effect, double direction)
    {
        super("fist", ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height, direction, effect);
        moving = true;
    }

    @Override
    public String getSpriteName() {
        String name = super.getSpriteName();
        String sprite = ailment + name;
        return sprite;
    }
}

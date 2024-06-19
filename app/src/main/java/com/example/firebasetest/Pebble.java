package com.example.firebasetest;

import android.graphics.Bitmap;

public class Pebble extends Projectile{
    public Pebble(int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, double direction)
    {
        super("pebble", ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height, direction, "none");
        moving = true;
    }
}

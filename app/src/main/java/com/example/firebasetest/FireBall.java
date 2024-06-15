package com.example.firebasetest;

import android.graphics.Bitmap;

public class FireBall extends Projectile{
    public FireBall(Bitmap sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, double direction)
    {
        super(sprite, ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height, direction, "fire");
        moving = true;
    }
}

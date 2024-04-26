package com.example.firebasetest;

import android.graphics.Bitmap;

public class FireBall extends Projectile{
    public FireBall(Bitmap sprite, int ID, Character creator, float power, float xLocation, float yLocation, float width, float height)
    {
        super(sprite, ID, creator, power, xLocation, yLocation, width, height);
    }
}

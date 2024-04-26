package com.example.firebasetest;

import android.graphics.Bitmap;

public class BladeAttack extends Projectile{
    public BladeAttack(Bitmap sprite, int ID, Character creator, float power, float xLocation, float yLocation, float width, float height)
    {
        super(sprite, ID, creator, power, xLocation, yLocation, width, height);
    }
}

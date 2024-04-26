package com.example.firebasetest;

import android.graphics.Bitmap;

public class Projectile extends GameObject{
    protected float horizontalSpeed;
    protected float verticalSpeed;
    protected Character creator; // is it protected?
    public Projectile(Bitmap sprite, int ID, Character creator, float power, float xLocation, float yLocation, float width, float height){
        super(sprite, ID, xLocation, yLocation, width, height);
    }
}

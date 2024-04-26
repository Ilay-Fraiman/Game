package com.example.firebasetest;

import android.graphics.Bitmap;

public class Mage extends Character{
    public static Bitmap mageSprite;
    public Mage(int level, int characterGrade, int ID, float xLocation, float yLocation, float width, float height)
    {
        super(level,2,3,5, mageSprite, ID, xLocation, yLocation, width, height);
    }
}

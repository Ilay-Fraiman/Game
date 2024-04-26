package com.example.firebasetest;

import android.graphics.Bitmap;

public class Archer extends Character{
    public static Bitmap archerSprite;
    public Archer(int level, int characterGrade, int ID, float xLocation, float yLocation, float width, float height){
        super(level,3,5,2, archerSprite, ID, xLocation, yLocation, width, height);
    }
}

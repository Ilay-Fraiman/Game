package com.example.firebasetest;

import android.graphics.Bitmap;

public class Archer extends Character{
    public static Bitmap archerSprite;
    public Archer(int level, int characterGrade, int ID){
        super(level,3,5,2, archerSprite, ID);
    }
}

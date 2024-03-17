package com.example.firebasetest;

import android.graphics.Bitmap;

public class Knight extends Character{
    public static Bitmap knightSprite;
    public Knight(int level, int characterGrade, int ID){
        super(level,5,2,3, knightSprite, ID);
    }
}

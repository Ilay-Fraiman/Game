package com.example.firebasetest;

import android.graphics.Bitmap;

public class GameObject {
    private Bitmap sprite;

    private float widthPercentage;

    private float heightPercentage;

    private float xPercentage;

    private float yPercentage;

    protected float directionAngle;//replace with x and y

    protected int roomID;

    public GameObject(Bitmap sprite, int ID){}//add location
}

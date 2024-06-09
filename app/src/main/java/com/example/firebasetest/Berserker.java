package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//block lasts for 5 seconds, negates all damage and lets berserker still use his x, not his a tho
//should it work like that? what combination of allowing and not allowing movement, aiming and specific moves
//should we use for block, how similar will it be to laser?
public class Berserker extends Character{
    public static Bitmap knightSprite;//temporary
    private float fistSpeed;
    private float physicalFistSpeed;
    private boolean block;
    public Berserker(int level, int ID, float xLocation, float yLocation)//both pebble and flying fist aren't affected by gravity
    {
        super(level,3,3,3, knightSprite, ID, xLocation, yLocation, 6);//final boss
        double geometricalFistSpeed = Math.sqrt((10.53 * 10));
        physicalFistSpeed = (float) geometricalFistSpeed;
        float transitionNum = GameView.pixelWidth / 100;//transition from centimeters to meters
        transitionNum *= 30;//transition from frames to seconds
        fistSpeed = physicalFistSpeed / transitionNum;//transition from meters per second to pixels per frame
        //this is a speed at which the arrow's max horizontal distance (at a 45 degree angle) is half of the screen
        setYPercentage(this.getYPercentage() - this.getHeightPercentage());
        setWidthPercentage(getWidthPercentage() * 2);
        setHeightPercentage(getHeightPercentage() * 2);
        this.block = false;
    }

    public void melee()
    {

    }

    @Override
    public void run() {
        super.run();
    }
}

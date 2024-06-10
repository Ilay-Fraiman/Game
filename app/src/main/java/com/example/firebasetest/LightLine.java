package com.example.firebasetest;

import android.graphics.Bitmap;

public class LightLine extends Projectile{
    public LightLine(Bitmap sprite, int ID, Character creator, float power, float xLocation, float yLocation, float width, float height, String effect, double direction)
    {
        super(sprite, ID, creator, power, 0, 0, xLocation, yLocation, width, height, direction, effect);
        this.TTD = System.currentTimeMillis() + 500L;//currently half a second. maybe a full? maybe 0.75?
        this.isTimed = true;
        this.oneTimeHit = true;
        if(effect.equals("laser"))
        {
            this.TTD += 4500L;//it should hit a specific num of times per second / per 5 idk
            this.oneTimeHit = false;
            this.power /= 30;//same dps, hits once per frame
        }
    }
}

package com.example.firebasetest;

import android.graphics.Bitmap;

public class GroundFire extends Projectile{//only if hit wall
    public GroundFire(Projectile p)
    {
        super(p.getSpriteName(), p.getRoomID(), p.getCreator(), p.getPower(), 0, 0, p.getXPercentage(), p.getYPercentage(), p.getWidthPercentage(), p.getHeightPercentage(), 0, "fire");
        this.isTimed = true;
        this.power /= 30;
        this.TTD = System.currentTimeMillis() + 2500L;
    }
}

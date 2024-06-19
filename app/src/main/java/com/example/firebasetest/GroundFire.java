package com.example.firebasetest;

import android.graphics.Bitmap;

public class GroundFire extends Projectile{//only if hit wall
    public GroundFire(Projectile p)
    {
        super("groundFire", p.getRoomID(), p.getCreator(), p.getPower(), 0, 0, p.getXLocation(), p.getYLocation(), p.getWidth(), p.getHeight(), 0, "fire");
        this.isTimed = true;
        this.power /= 30;
        this.TTD = System.currentTimeMillis() + 2500L;
    }
}

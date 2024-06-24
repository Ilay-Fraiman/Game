package com.example.firebasetest;

import android.graphics.Bitmap;

public class GroundFire extends Projectile{//only if hit wall
    public GroundFire(Projectile p)
    {
        super("groundFire", p.getRoomID(), p.getCreator(), p.getPower(), 0, 0, p.getXLocation(), p.getYLocation(), p.getWidth(), p.getHeight(), 0, "fire");
        this.isTimed = true;
        this.power /= 30;
        this.TTD = System.currentTimeMillis() + 2500L;
        if(p.getEdge(0) <= 0)
        {
            setXLocation(p.getHeight() / 2);
            setAngle(270);
        }
        else if(p.getEdge(1) >= GameView.width)
        {
            setXLocation(GameView.width - (p.getHeight() / 2));
            setAngle(90);
        }
        else if(p.getEdge(2) <= 0)
        {
            setYLocation(p.getHeight() / 2);
            setAngle(180);
        }
        else if(p.getEdge(3) >= GameView.height)
        {
            setYLocation(GameView.height - (p.getHeight() / 2));
            setAngle(0);
        }
    }
}
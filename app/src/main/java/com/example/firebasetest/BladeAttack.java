package com.example.firebasetest;

import android.graphics.Bitmap;

public class BladeAttack extends Projectile{
    public BladeAttack(String name, int ID, Character creator, double power, float xLocation, float yLocation, float width, float height, String effect, double direction)
    {
        super(name, ID, creator, power, 0, 0, xLocation, yLocation, width, height, direction, effect);
        this.isTimed = true;
        this.oneTimeHit = true;
        this.TTD = System.currentTimeMillis() + 500L;
        //summon animation
    }
}

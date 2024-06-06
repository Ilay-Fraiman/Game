package com.example.firebasetest;

import android.graphics.Bitmap;

public class BladeAttack extends Projectile{
    private long TTD; //time to delete from room
    public BladeAttack(Bitmap sprite, int ID, Character creator, float power, float xLocation, float yLocation, float width, float height)
    {
        super(sprite, ID, creator, power, 0, 0, xLocation, yLocation, width, height, 0, "none");
        this.TTD = System.currentTimeMillis() + 500L;
        //summon animation
    }

    public long getTTD() {
        return TTD;
    }
}

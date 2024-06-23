package com.example.firebasetest;

import java.util.ArrayList;

public class Teleport extends Projectile{
    public Teleport(int ID, Character creator, float x, float y, float width, float height)
    {
        super("teleport", ID, creator, 0, 0, 0, x, y, width, height, 0, "none");
        this.isTimed = true;
        this.TTD = 333L;
    }
}

package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.Timer;
import java.util.TimerTask;

public class Archer extends Character{
    public static Bitmap archerSprite;
    private static Bitmap arrowSprite;//temporary
    private boolean homing;
    private boolean ricochet;
    public Archer(int level, int characterGrade, int ID, float xLocation, float yLocation, float width, float height){
        super(level,3,5,2, archerSprite, ID, xLocation, yLocation, width, height, characterGrade);
    }
    public void shoot(boolean isPoison)
    {
        String button = (isPoison) ? "B" : "X";

        if(useAbility(button))
        {
            float locationX = this.getXPercentage();
            float locationY = this.getYPercentage();
            float xDiffrential = this.getWidthPercentage() * this.horizontalDirection;
            float yDiffrential = this.getHeightPercentage() * this.verticalDirection;//these are false. the width, height sjould be of arrow
            if (this.horizontalDirection > 0)
                locationX += this.getWidthPercentage();
            if (this.verticalDirection > 0)
                locationY += this.getHeightPercentage();

            locationX += xDiffrential;
            locationY += yDiffrential;

            Arrow arrow = new Arrow(arrowSprite, roomID, this, attackPower, xDiffrential, yDiffrential, locationX, locationY, xDiffrential, yDiffrential, ricochet, homing, isPoison);
            this.projectiles.add(arrow);
            resetAbility(button);
        }
    }

    public void stab()
    {
        if(useAbility("A"))
        {
            super.Attack();
            resetAbility("A");
        }
    }

    public void homing()
    {
        if(useAbility("Y")) {
            this.homing = true;
            class NotHoming extends TimerTask {
                private Archer archer;

                NotHoming(Archer a)
                {
                    this.archer = a;
                }

                @Override
                public void run() {
                    archer.notHoming();
                }
            }
            Timer timer = new Timer();
            TimerTask task = new NotHoming(this);
            timer.schedule(task, 10000L);
            resetAbility("Y");
        }
    }

    public void notHoming()
    {
        this.homing = false;
        resetAbility("Y");
    }
}

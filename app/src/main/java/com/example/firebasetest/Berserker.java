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
    public Berserker(int level, int ID, float xLocation, float yLocation)//both fist and flying fist aren't affected by gravity
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
        //this.itemSprite = fistSprite
    }

    public void melee()
    {
        if(useAbility("X"))
        {
            Attack();
            resetAbility("X");
        }
    }

    public void flyingFist()
    {
        if(useAbility("A") && !block) {
            String effect = ailment();
            float locationX = this.getXPercentage();
            float locationY = this.getYPercentage();
            float myWidth = this.getWidthPercentage();
            float myHeight = this.getHeightPercentage();
            float xDiffrential = itemWidth * this.horizontalDirection;
            float yDiffrential = itemHeight * this.verticalDirection;//false. width, height should be of fireball
            if (this.horizontalDirection > 0)
                locationX += myWidth;
            if (this.verticalDirection > 0)
                locationY += myHeight;

            locationX += xDiffrential;
            locationY += yDiffrential;

            xDiffrential = fistSpeed * horizontalDirection;
            yDiffrential = fistSpeed * verticalDirection;

            Fist fist = new Fist(knightSprite, roomID, this, attackPower, xDiffrential, yDiffrential, locationX, locationY, itemWidth, itemHeight, effect, directionAngle);
            this.projectiles.add(fist);
            resetAbility("A");
        }
    }

    public void block()
    {
        if(useAbility("B"))
        {
            block = true;
            resetAbility("B");
            class UnBlock extends TimerTask {
                private Berserker berserker;

                UnBlock(Berserker b)
                {
                    this.berserker = b;
                }

                @Override
                public void run() {
                    berserker.unBlock();
                }
            }
            Timer timer = new Timer();
            TimerTask task = new UnBlock(this);
            timer.schedule(task, 5000L);
        }
    }

    @Override
    public boolean hit(Projectile p) {
        if(!block)
            return super.hit(p);
        return false;
    }

    public void unBlock()
    {
        this.block = false;
        resetAbility("B");
    }
    //add earth shatter and run

    @Override
    public void run() {
        super.run();
    }
}

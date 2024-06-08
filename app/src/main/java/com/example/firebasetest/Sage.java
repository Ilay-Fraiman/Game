package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Sage extends Character {
    public static Bitmap knightSprite;//temporary
    private float pebbleSpeed;
    private float physicalPebbleSpeed;
    private float pebbleWidth;
    private float pebbleHeight;
    public Sage(int level, int ID, float xLocation, float yLocation)
    {
        super(level,3,3,3, knightSprite, ID, xLocation, yLocation, 4);
        double geometricalArrowSpeed = Math.sqrt((10.53 * 10));
        physicalPebbleSpeed = (float) geometricalArrowSpeed;
        float transitionNum = GameView.pixelWidth / 100;//transition from centimeters to meters
        transitionNum *= 30;//transition from frames to seconds
        pebbleSpeed = physicalPebbleSpeed / transitionNum;//transition from meters per second to pixels per frame
        //this is a speed at which the arrow's max horizontal distance (at a 45 degree angle) is half of the screen
    }

    public void pebble()
    {
        float locationX = this.getXPercentage();
        float locationY = this.getYPercentage();
        float myWidth = this.getWidthPercentage();
        float myHeight = this.getHeightPercentage();
        float pebbleWidth = myWidth / 5;
        float pebbleHeight = myHeight / 5;
        float xDiffrential = pebbleWidth * this.horizontalDirection;
        float yDiffrential = pebbleHeight * this.verticalDirection;//false. width, height should be of fireball
        if (this.horizontalDirection > 0)
            locationX += myWidth;
        if (this.verticalDirection > 0)
            locationY += myHeight;

        locationX += xDiffrential;
        locationY += yDiffrential;

        xDiffrential = pebbleSpeed * horizontalDirection;
        yDiffrential = pebbleSpeed * verticalDirection;

        Pebble pebble = new Pebble(knightSprite, roomID, this, attackPower, xDiffrential, yDiffrential, locationX, locationY, pebbleWidth, pebbleHeight, directionAngle);
        this.projectiles.add(pebble);
        resetAbility("X");
    }

    public void scepterBash()
    {
        if(useAbility("A"))
        {
            Attack();
            resetAbility("A");
        }
    }

    public void teleport()
    {
        if(useAbility("B"))
        {
            float values[] = aimAtPlayer();//this works in both boss mode and pvp mode because we use
            //character.setPlayer() when initializing the room in both cases
            float enemyX = values[0];
            float enemyY = values[1];
            float width = values[2];
            float enemyWidth = values[4];
            float x = values[6];
            this.setHeightPercentage(enemyY);

            if(x < getPlayerX())
                this.setXPercentage(x + enemyWidth);
            else
                this.setXPercentage(enemyX - width);

            resetAbility("B");
        }
    }

    public void laser()
    {
        if(useAbility("Y"))
        {
            magicLine("laser", knightSprite);//sprite is temporary
            resetAbility("Y");
        }
    }


    @Override
    public void run() {
        while (running) {
            if (this.HP <= 0)
            {
                this.running = false;
                //show transformation to berserker king animation
            }
            else {
                float[] values = aimAtPlayer();
                float playerX = values[0];
                float width = values[2];
                float height = values[3];
                float xLocation = values[6];
                float yLocation = values[7];
                moveBack = true;
                boolean backed = false;

                boolean range = inRange();
                if(range)
                    backed = true;

                if(useAbility("A") && locked <= 0)
                {
                    if(range)
                    {
                        scepterBash();
                        locked = 15;
                    }
                    else
                    {
                        if(useAbility("B"))
                            teleport();
                        else
                            moveBack = false;
                    }
                }
                if (locked <= 0)
                {
                    if(useAbility("X") && (this.distanceVector < width * 10))
                    {
                        laser();
                        locked = 150;
                    }
                    else
                        pebble();
                }

                float side = (moveBack) ? -1 : 1;
                this.horizontalMovement = this.horizontalDirection * side;
                if(yLocation + height == GameView.height)
                    this.verticalMovement = this.verticalDirection * side * this.movementSpeed;
                if(backed)
                    backedIntoWall(xLocation, yLocation, width, height, playerX);
                moving = true;
                move(xLocation, yLocation, width, height);
                locked--;
                try {
                    thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

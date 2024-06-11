package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Sage extends Character {
    public static Bitmap knightSprite;//temporary
    private float pebbleSpeed;
    private float physicalPebbleSpeed;
    private boolean laser;
    public Sage(int level, int ID, float xLocation, float yLocation)//both pebble and flying fist aren't affected by gravity
    {
        super(level,3,3,3, knightSprite, ID, xLocation, yLocation, 6);
        double geometricalPebbleSpeed = Math.sqrt((10.53 * 10));
        physicalPebbleSpeed = (float) geometricalPebbleSpeed;
        float transitionNum = GameView.pixelWidth / 100;//transition from centimeters to meters
        transitionNum *= 30;//transition from frames to seconds
        pebbleSpeed = physicalPebbleSpeed / transitionNum;//transition from meters per second to pixels per frame
        //this is a speed at which the arrow's max horizontal distance (at a 45 degree angle) is half of the screen
        this.laser = false;
        if (threadStart)
            thread.start();
    }

    public void pebble()
    {
        if(useAbility("X") && !laser)
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
    }

    public void scepterBash()
    {
        if(useAbility("A") && !laser)
        {
            Attack();
            resetAbility("A");
        }
    }

    public boolean teleport()//for ending confirmation
    {
        if(useAbility("B") && !laser)
        {
            float values[] = aimAtPlayer();//this works in both boss mode and pvp mode because we use
            //character.setPlayer() when initializing the room in both cases
            float enemyX = values[0];
            float enemyY = values[1];
            float width = values[2];
            float enemyWidth = values[4];
            float x = values[6];
            this.setYPercentage(enemyY);

            float addition = 0;
            float addition1 = enemyWidth;
            float addition2 = width * (-1);

            if(x < enemyX)
                addition = ((enemyX + enemyWidth + width) < GameView.width)? addition1 : addition2;
            else
                addition = ((enemyX - width) > 0)? addition2 : addition1;

            this.setXPercentage(enemyX + addition);
            resetAbility("B");
            return true;
        }
        return false;
    }

    public void laser()
    {
        if(useAbility("Y") && (getYPercentage() + getHeightPercentage() == GameView.height))
        {
            magicLine("laser", knightSprite);//sprite is temporary
            laser = true;
            resetAbility("Y");
            class Release extends TimerTask {
                private Sage sage;

                Release(Sage s)
                {
                    this.sage = s;
                }

                @Override
                public void run() {
                    sage.release();
                }
            }
            Timer timer = new Timer();
            TimerTask task = new Release(this);
            timer.schedule(task, 5000L);
        }
    }

    public void release()
    {
        this.laser = false;
        resetAbility("Y");
    }

    @Override
    protected void move(float x, float y, float tWidth, float tHeight) {
        if(!laser)
            super.move(x, y, tWidth, tHeight);
    }

    @Override
    public void setUpDirection(float x, float y) {
        if(!laser)
            super.setUpDirection(x, y);
    }

    @Override
    public void setUpMovement(float x, float y) {
        if(!laser)
            super.setUpMovement(x, y);
    }

    @Override
    public void run() {
        while (running) {
            if (this.HP <= 0)
            {
                this.running = false;
                //show transformation to berserker king animation
            }
            else if(laser)
            {
                try {
                    thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                boolean grounded = (yLocation + height == GameView.height);
                boolean laserStart = false;

                boolean range = inRange();
                if(range)
                    backed = true;

                if(useAbility("A") && locked <= 0)
                {
                    boolean bash = false;
                    if(!range)
                    {
                        if(teleport())
                        {
                            bash = true;
                            float[] values2 = aimAtPlayer();
                            xLocation = values2[6];
                            yLocation = values2[7];
                            laserStart = true;//stops movement
                            inRange();
                        }
                        else
                            moveBack = false;
                    }
                    else
                        bash = true;

                    if (bash)
                    {
                        scepterBash();
                        locked = 15;
                    }
                }

                if (locked <= 0)
                {
                    if(useAbility("Y") && ((this.distanceVector < width * 10) && grounded))
                    {
                        laser();
                        laserStart = true;
                    }
                    else if(useAbility("X"))
                        pebble();
                }

                if (!laserStart)
                {
                    float side = (moveBack) ? -1 : 1;
                    this.horizontalMovement = this.horizontalDirection * side;
                    if(grounded)
                        this.verticalMovement = this.verticalDirection * side * this.movementSpeed;
                    if(backed)
                        backedIntoWall(xLocation, yLocation, width, height, playerX);
                    moving = true;
                    move(xLocation, yLocation, width, height);
                    locked--;
                }
                try {
                    thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

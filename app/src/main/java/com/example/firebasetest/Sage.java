package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Sage extends Character {
    private float pebbleSpeed;
    private float physicalPebbleSpeed;
    private boolean laser;
    public Sage(int level, int ID, float xLocation, float yLocation)//both pebble and flying fist aren't affected by gravity
    {
        super(level,3,3,3, "sage", ID, xLocation, yLocation, 6);
        double geometricalPebbleSpeed = Math.sqrt((10.53 * 10));
        physicalPebbleSpeed = (float) geometricalPebbleSpeed;
        float transitionNum = GameView.pixelWidth / 100;//transition from centimeters to meters
        transitionNum *= 30;//transition from frames to seconds
        pebbleSpeed = physicalPebbleSpeed / transitionNum;//transition from meters per second to pixels per frame
        this.laser = false;
        this.itemSprite = "scepter";
        if (threadStart)
            thread.start();
    }

    public void pebble()
    {
        if(useAbility("X") && ((!laser) && (!performingAction)))
        {
            spriteState = "waving";
            performingAction = true;
            float locationX = this.getXLocation();
            float locationY = this.getYLocation();
            float myWidth = this.getWidth();
            float myHeight = this.getHeight();
            float pebbleWidth = myWidth / 3;
            float pebbleHeight = myHeight / 3;
            float xDiffrential = pebbleSpeed * horizontalDirection;
            float yDiffrential = pebbleSpeed * verticalDirection;

            Pebble pebble = new Pebble(roomID, this, attackPower, xDiffrential, yDiffrential, locationX, locationY, pebbleWidth, pebbleHeight, directionAngle);
            this.projectiles.add(pebble);
            resetAbility("X");
            idleAgain(spriteState);
        }
    }

    public void scepterBash()
    {
        if(useAbility("A") && ((!laser) && (!performingAction)))
        {
            Attack();
            resetAbility("A");
        }
    }

    public boolean teleport()//for ending confirmation
    {
        if(useAbility("B") && ((!laser) && (!performingAction)))
        {
            float values[] = aimAtPlayer();//this works in both boss mode and pvp mode because we use
            //character.setPlayer() when initializing the room in both cases
            float enemyX = values[0];
            float enemyY = values[1];
            float width = values[2];
            float height = values[3];
            float enemyWidth = values[4];
            float x = values[6];
            float y = values[7];
            Teleport tp = new Teleport(roomID, this, x, y, width, height);
            this.projectiles.add(tp);
            performingAction = true;
            spriteState = "teleporting";

            this.setYLocation(enemyY);

            float addition = (enemyWidth + width) / 2;
            float multiplication = 1;

            if(x < enemyX)
                multiplication = ((enemyX + (enemyWidth / 2) + width) < GameView.width)? 1 : (-1);
            else
                multiplication = ((enemyX - (enemyWidth / 2) - width) > 0)? (-1) : 1;

            addition *= multiplication;
            this.setXLocation(enemyX + addition);
            resetAbility("B");
            idleAgain(spriteState);
            return true;
        }
        return false;
    }

    public void laser()
    {
        if(useAbility("Y") && ((getYLocation() + (getHeight() / 2)) == GameView.height))
        {
            magicLine("laser");
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
                boolean grounded = ((yLocation + (height / 2)) == GameView.height);
                boolean laserStart = false;
                boolean acted = performingAction;

                boolean range = inRange();
                if(range)
                    backed = true;

                if(useAbility("A") && (!acted))
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
                        performingAction = false;
                        scepterBash();
                        acted = true;
                    }
                }

                if (!acted)
                {
                    if(useAbility("Y") && ((this.distanceVector < (width * 10)) && grounded))
                    {
                        laser();
                        laserStart = true;
                    }
                    else if(useAbility("X"))
                    {
                        pebble();
                    }
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
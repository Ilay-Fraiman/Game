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
        double geometricalPebbleSpeed = Math.sqrt((10.53d * 10d));
        physicalPebbleSpeed = (float) geometricalPebbleSpeed;
        float transitionNum = GameView.pixelHeight / 100f;//transition from centimeters to meters
        transitionNum *= 30f;//transition from frames to seconds
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
            float pebbleWidth = itemWidth / 3f;
            float pebbleHeight = itemHeight / 3f;
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

            float addition = (enemyWidth + width) / 2f;
            float multiplication = 1f;

            if(x < enemyX)
                multiplication = ((enemyX + (enemyWidth / 2f) + width) < GameView.width)? 1f : (-1f);
            else
                multiplication = ((enemyX - (enemyWidth / 2f) - width) > 0)? (-1f) : 1f;

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
        if(useAbility("Y") && ((getYLocation() + (getHeight() / 2f)) == GameView.height))
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
    protected void move() {
        if(!laser)
            super.move();
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
            if (this.HP <= 0d)
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
                boolean grounded = ((yLocation + (height / 2f)) == GameView.height);
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
                    if(useAbility("Y") && (((float) this.distanceVector < (width * 10d)) && grounded))
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
                    float side = (moveBack) ? -1f : 1f;
                    this.horizontalMovement = this.horizontalDirection * side;
                    if(grounded)
                        this.verticalMovement = this.verticalDirection * side * this.movementSpeed;
                    if(backed)
                        backedIntoWall(xLocation, yLocation, width, height, playerX);
                    moving = true;
                    move();
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
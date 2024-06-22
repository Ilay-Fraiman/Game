package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.Timer;
import java.util.TimerTask;

public class Archer extends Character{
    private boolean homing;
    private boolean ricochet;
    private float arrowSpeed;
    private boolean isPoison;
    private float physicalArrowSpeed;
    public Archer(int level, int characterGrade, int ID, float xLocation, float yLocation){
        super(level,3,5,2, "archer", ID, xLocation, yLocation, characterGrade);
        double geometricalArrowSpeed = Math.sqrt((10.53 * 10));
        physicalArrowSpeed = (float) geometricalArrowSpeed;
        float transitionNum = GameView.pixelWidth / 100;//transition from centimeters to meters
        transitionNum *= 30;//transition from frames to seconds
        arrowSpeed = physicalArrowSpeed / transitionNum;//transition from meters per second to pixels per frame
        //this is a speed at which the arrow's max horizontal distance (at a 45 degree angle) is half of the screen
        itemHeight /= 3;
        itemWidth /= 2;
        this.homing = false;
        this.ricochet = false;
        this.isPoison = false;
        this.itemSprite = "bow";
        this.secondItemSprite = "arrow";
        switch(characterGrade)
        {
            case 1:
                movementSpeed /= 1.5;
                attackCooldown *= 1.5;
                attackPower *= 2;
                double multiplication = Math.sqrt(1.5); //docs
                float fMultiplication = (float) multiplication;
                arrowSpeed *= fMultiplication;
                physicalArrowSpeed *= fMultiplication;
                float width = this.getWidth();
                this.setYLocation(this.getYLocation() - (width / 2));
                this.setWidth(width * 2);
                this.setHeight(width * 2);
                break;
            case 2:
                isPoison = true;
                break;
            case 3:
                ricochet = true;
                break;
            case 4:
                homing = true;
                break;
        }
        if (threadStart)
            thread.start();
    }
    public void shoot()
    {
        if(useAbility("X"))
        {
            this.spriteState = "shooting";
            float locationX = this.getXLocation();
            float locationY = this.getYLocation();
            float xDiffrential = arrowSpeed * horizontalDirection;
            float yDiffrential = arrowSpeed * verticalDirection;

            Arrow arrow = new Arrow(roomID, this, attackPower, xDiffrential, yDiffrential, locationX, locationY, itemWidth, itemHeight, ricochet, homing, isPoison, this.directionAngle);
            this.projectiles.add(arrow);

            if(isPoison && characterGrade!=2)
            {
                isPoison = false;
                resetAbility("B");
            }

            resetAbility("X");
            idleAgain(spriteState);
        }
    }

    public void stab()
    {
        if(useAbility("A"))
        {
            Attack();
            resetAbility("A");
        }
    }

    public void poison()
    {
        if(useAbility("B"))
            isPoison = true;
    }

    public boolean homing()
    {
        if(useAbility("Y") && !homing) {
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
        return homing;
    }

    public void notHoming()
    {
        this.homing = false;
        resetAbility("Y");
    }

    public float getPhysicalSpeed()
    {
        return this.physicalArrowSpeed;
    }

    @Override
    public void run() {
        while (running)
        {
            if(this.HP <= 0)
                this.running = false;
            else
            {
                float[] values = aimAtPlayer();
                float playerX = values[0];
                float width = values[2];
                float height = values[3];
                float xLocation = values[6];
                float yLocation = values[7];
                float horizontalDistance = values[8];
                float verticalDistance = values[9];
                moveBack = true;
                boolean backed = false;

                boolean range = inRange();
                if (range)
                    backed = true;

                if(locked<=0)
                {
                    if(useAbility("A") && range)
                    {
                        locked = 15;
                        stab();
                    }
                    else if(useAbility("X"))
                    {
                        if(!homing() && (characterGrade != 3 && !aim(horizontalDistance, verticalDistance, physicalArrowSpeed)))
                            moveBack = false;
                        else
                        {
                            poison();
                            locked = 10;
                            shoot();
                        }
                    }
                }
                float side = (moveBack) ? -1 : 1;
                this.horizontalMovement = this.horizontalDirection * side;
                if((yLocation + (height / 2)) == GameView.height)
                    this.verticalMovement = this.verticalDirection * side * this.movementSpeed;
                if(backed)
                    backedIntoWall(xLocation, yLocation, width, height, playerX);
                moving = true;
                move(xLocation, yLocation, width, height);
                locked--;
                try {
                    thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

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
        super(level,5,5,5, knightSprite, ID, xLocation, yLocation, 6);//final boss
        double geometricalFistSpeed = Math.sqrt((10.53 * 10));
        physicalFistSpeed = (float) geometricalFistSpeed;
        float transitionNum = GameView.pixelWidth / 100;//transition from centimeters to meters
        transitionNum *= 30;//transition from frames to seconds
        fistSpeed = physicalFistSpeed / transitionNum;//transition from meters per second to pixels per frame
        //this is a speed at which the arrow's max horizontal distance (at a 45 degree angle) is half of the screen
        setYPercentage(this.getYPercentage() - this.getHeightPercentage());
        setWidthPercentage(getWidthPercentage() * 2);
        setHeightPercentage(getHeightPercentage() * 2);
        this.movementSpeed /= 2;
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
            movementSpeed /= 2;
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
        if(!block || p.getAilment().equals("shatter"))
            return super.hit(p);
        return false;
    }

    public void unBlock()
    {
        this.block = false;
        resetAbility("B");
    }

    public void earthShatter()
    {
        if(useAbility("Y") && (!block && (getYPercentage() + getHeightPercentage() == GameView.height)))
        {
            double direction = (this.horizontalDirection > 0)? 1 : -1;//only on the x axis
            float height = this.getHeightPercentage() / 4;//this height is double normal, shatter is half normal
            float yLocation = GameView.canvasPixelHeight - height;//only on the floor
            float width = GameView.width * (3/40);//max width is 3/8 canvas width, starting with is 1/5 of that
            float xLocation = this.getXPercentage();
            EarthShatter earthShatter = new EarthShatter(knightSprite, roomID, this, attackPower * 2, xLocation, yLocation, width, height, direction);//knight sprite is temporary
            this.projectiles.add(earthShatter);
        }
    }

    @Override
    public void run() {
        while (running)
        {
            if (this.HP <= 0)
            {
                this.running = false;
            }
            else
            {
                float[] values = aimAtPlayer();
                float width = values[2];
                float height = values[3];
                float yLocation = values[7];
                float xLocation = values[6];
                float horizontalDistance = values[8];
                float verticalDistance = values[9];
                moveBack = false;
                boolean grounded = (yLocation + height == GameView.height);
                float range = GameView.width * (3/8);

                if(locked <= 0)
                {
                    if((useAbility("Y") && !block) && (grounded && ((horizontalDistance <= range) && (verticalDistance == 0))))
                    {
                        earthShatter();
                        locked = 15;
                    }
                    else if(useAbility("X") && inRange())
                    {
                        if(useAbility("B"))
                            block();
                        melee();
                        locked = 15;
                    }
                    else if(useAbility("A"))
                    {
                        flyingFist();
                        locked = 15;
                    }
                }

                this.horizontalMovement = this.horizontalDirection;
                if(grounded)
                    this.verticalMovement = this.verticalDirection * this.movementSpeed;

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

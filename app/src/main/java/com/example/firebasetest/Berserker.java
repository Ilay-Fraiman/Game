package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//block lasts for 5 seconds, negates all damage and lets berserker still use his x, not his a tho
//should it work like that? what combination of allowing and not allowing movement, aiming and specific moves
//should we use for block, how similar will it be to laser?
public class Berserker extends Character{
    private float fistSpeed;
    private float physicalFistSpeed;
    private boolean block;
    public Berserker(int level, int ID, float xLocation, float yLocation)//both fist and flying fist aren't affected by gravity
    {
        super(level,5,5,5, "berserker", ID, xLocation, yLocation, 6);//final boss
        double geometricalFistSpeed = Math.sqrt((10.53 * 10));
        physicalFistSpeed = (float) geometricalFistSpeed;
        float transitionNum = GameView.pixelWidth / 100;//transition from centimeters to meters
        transitionNum *= 30;//transition from frames to seconds
        fistSpeed = physicalFistSpeed / transitionNum;//transition from meters per second to pixels per frame
        setYLocation(this.getYLocation() - (this.getHeight() / 2));
        setWidth(getWidth() * 2);
        setHeight(getHeight() * 2);
        this.movementSpeed /= 2;
        this.block = false;
        if (threadStart)
            thread.start();
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
            spriteState = "punching";
            String effect = ailment();
            float locationX = this.getXLocation();
            float locationY = this.getYLocation();
            float xDiffrential = fistSpeed * horizontalDirection;
            float yDiffrential = fistSpeed * verticalDirection;

            Fist fist = new Fist(roomID, this, attackPower, xDiffrential, yDiffrential, locationX, locationY, itemWidth, itemHeight, effect, directionAngle);
            this.projectiles.add(fist);
            resetAbility("A");
            idleAgain(spriteState);
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
        this.movementSpeed *= 2;
        resetAbility("B");
    }

    public void earthShatter()
    {
        if(useAbility("Y") && (!block && ((getYLocation() + (getHeight() / 2)) == GameView.height)))
        {
            spriteState = "smashing";
            double direction = (this.horizontalDirection > 0)? 1 : -1;//only on the x axis
            float height = this.getHeight() / 4;//this height is double normal, shatter is half normal
            float yLocation = GameView.height - (height / 2);//only on the floor
            float width = GameView.width * (3/40);//max width is 3/8 canvas width, starting with is 1/5 of that
            float xLocation = this.getXLocation();
            xLocation += ((this.getWidth() / 2) * ((float) direction));
            xLocation += ((width / 2) * ((float) direction));
            EarthShatter earthShatter = new EarthShatter(roomID, this, attackPower * 2, xLocation, yLocation, width, height, direction);
            this.projectiles.add(earthShatter);
            resetAbility("Y");
            idleAgain(spriteState);
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
                boolean grounded = ((yLocation + (height / 2)) == GameView.height);
                float range = GameView.width * (3/8);

                if(locked <= 0)
                {
                    if((useAbility("Y") && !block) && (grounded && ((horizontalDistance <= range) && (Math.abs(verticalDistance) == Math.abs(height / 4)))))
                    {
                        locked = 15;
                        earthShatter();
                    }
                    else if(useAbility("X") && inRange())
                    {
                        if(useAbility("B"))
                            block();
                        locked = 15;
                        melee();
                    }
                    else if(useAbility("A"))
                    {
                        locked = 15;
                        flyingFist();
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

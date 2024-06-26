package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Berserker extends Character{
    private float fistSpeed;
    private float physicalFistSpeed;
    private boolean block;
    public Berserker(int level, int ID, float xLocation, float yLocation)//both fist and flying fist aren't affected by gravity
    {
        super(level,5,5,5, "berserker", ID, xLocation, yLocation, 6);//final boss
        double geometricalFistSpeed = Math.sqrt((10.53d * 10d));
        physicalFistSpeed = (float) geometricalFistSpeed;
        float transitionNum = GameView.pixelHeight / 100f;//transition from centimeters to meters
        transitionNum *= 30f;//transition from frames to seconds
        fistSpeed = physicalFistSpeed / transitionNum;//transition from meters per second to pixels per frame
        float height = this.getHeight();
        float y = this.getYLocation();
        y += (height / 2f);
        height *= 3f;
        y -= (height / 2f);
        this.setYLocation(y);
        this.setHeight(height);
        this.setWidth(this.getWidth() * 3f);
        itemWidth *= 1.5f;
        itemHeight *= 1.5f;
        this.movementSpeed /= 2f;
        this.block = false;
        this.itemSprite = "arm";
        if (threadStart)
            thread.start();
    }

    public void melee()
    {
        if(useAbility("X") && (!performingAction))
        {
            Attack();
            resetAbility("X");
        }
    }

    public void flyingFist()
    {
        if(useAbility("A") && ((!block) && (!performingAction))) {
            spriteState = "punching";
            performingAction = true;
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
        if(useAbility("B") && ((!block) && (!performingAction)))
        {
            block = true;
            movementSpeed /= 2f;
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
        if((!block) || p.getAilment().equals("shatter"))
            return super.hit(p);
        return p.canHit(this);
    }

    public void unBlock()
    {
        this.block = false;
        this.movementSpeed *= 2f;
        resetAbility("B");
    }

    public void earthShatter()
    {
        if(useAbility("Y") && (((!block) && (!performingAction)) && ((getYLocation() + (getHeight() / 2f)) == GameView.height)))
        {
            spriteState = "smashing";
            performingAction = true;
            double direction = (this.horizontalDirection > 0)? 1d : -1d;//only on the x axis
            float height = GameView.height / 15f;//this height is double normal, shatter is half normal
            float yLocation = GameView.height - (height / 2f);//only on the floor
            float width = GameView.width * (3f/40f);//max width is 3/8 canvas width, starting with is 1/5 of that
            float xLocation = this.getXLocation();
            xLocation += ((this.getWidth() / 2f) * ((float) direction));
            xLocation += ((width / 2) * ((float) direction));
            EarthShatter earthShatter = new EarthShatter(roomID, this, (attackPower * 2d), xLocation, yLocation, width, height, direction);
            this.projectiles.add(earthShatter);
            resetAbility("Y");
            idleAgain(spriteState);
        }
    }

    @Override
    protected void move() {
        if(!(spriteState.equals("smashing")))
            super.move();
    }

    @Override
    public void setUpMovement(float x, float y) {
        if(!(spriteState.equals("smashing")))
            super.setUpMovement(x, y);
    }

    @Override
    public void setUpDirection(float x, float y) {
        if(!(spriteState.equals("smashing")))
            super.setUpDirection(x, y);
    }

    @Override
    public boolean[] hasItems() {
        boolean[] check = super.hasItems();
        if(spriteState.equals("smashing"))
        {
            check[0] = false;
            check[1] = false;
        }
        return check;
    }

    @Override
    public String getSpriteName() {
        String name = super.getSpriteName();
        String action = "";
        String sprite = "";
        if(block)
            action = "blocking";
        else if(spriteState.equals("smashing"))
            action = "smashing";
        else
            return name;
        sprite = action + name;
        return sprite;
    }

    @Override
    public void run() {
        while (running)
        {
            if (this.HP <= 0d)
            {
                this.running = false;
            }
            else if(spriteState.equals("smashing"))
            {
                try {
                    thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                float[] values = aimAtPlayer();
                float playerY = values[1];
                float width = values[2];
                float height = values[3];
                float playerHeight = values[5];
                float yLocation = values[7];
                float xLocation = values[6];
                float horizontalDistance = values[8];
                moveBack = false;
                boolean grounded = ((yLocation + (height / 2f)) == GameView.height);
                boolean enemyGrounded = ((playerY + (playerHeight / 2f)) == GameView.height);
                float range = GameView.width * (3f/8f);
                boolean doNotMove = false;

                if(!performingAction)
                {
                    if((useAbility("Y") && (!block)) && (grounded && ((horizontalDistance <= range) && (enemyGrounded))))
                    {
                        earthShatter();
                        doNotMove = true;
                    }
                    else if(useAbility("X") && inRange())
                    {
                        if(useAbility("B"))
                            block();
                        melee();
                    }
                    else if(useAbility("A") && (!block))
                    {
                        flyingFist();
                    }
                }

                if(!doNotMove)
                {
                    this.horizontalMovement = this.horizontalDirection;
                    if(grounded)
                        this.verticalMovement = this.verticalDirection * this.movementSpeed;

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
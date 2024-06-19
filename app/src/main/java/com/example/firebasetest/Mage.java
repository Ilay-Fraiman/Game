package com.example.firebasetest;

import android.graphics.Bitmap;
import android.widget.Switch;

public class Mage extends Character{
    private double maxHealth;
    private float fireBallSpeed;
    private float physicalFireBallSpeed;
    private String effect;
    private Mage clone;
    public Mage(int level, int characterGrade, int ID, float xLocation, float yLocation)
    {
        super(level,2,3,5, "mage", ID, xLocation, yLocation, characterGrade);
        this.maxHealth=this.HP;
        double multiplicationNum = 21.06 / 8;
        multiplicationNum *= 5;//middle between 3/4(long arrow) and 1/2(arrow)
        double geometricalFireBallSpeed = Math.sqrt((multiplicationNum * 10));
        physicalFireBallSpeed = (float) geometricalFireBallSpeed;
        float transitionNum = GameView.pixelWidth / 100;//transition from centimeters to meters
        transitionNum *= 30;//transition from frames to seconds
        fireBallSpeed = physicalFireBallSpeed / transitionNum;
        this.effect = "none";
        if (threadStart)
            thread.start();
    }
    public void lightLine()
    {
        if(useAbility("X"))
        {
            magicLine(this.effect);
            this.effect = "none";
            resetAbility("X");
        }
    }

    public void mist() {
        if (useAbility("A")) {
            this.spriteState = "waving";
            float width = this.getWidth();
            float height = this.getHeight();
            float xAxis = horizontalDirection * width;
            float yAxis = verticalDirection *  height;
            float locationHor = this.getXLocation() + xAxis;
            float locationVert = this.getYLocation() + yAxis;
            if (this.horizontalDirection > 0)
                locationHor += (width / 2);
            else
                locationHor -= (width / 2);
            if (this.verticalDirection > 0)
                locationVert += (height / 2);
            else
                locationVert -= (height / 2);

            Mist mist = new Mist(roomID, this, attackPower/2, locationHor, locationVert, width, height);
            this.projectiles.add(mist);
            resetAbility("A");
            idleAgain(spriteState);
        }
    }
    public void fireball()
    {
        if(useAbility("B"))
        {
            spriteState = "waving";
            float locationX = this.getXLocation();
            float locationY = this.getYLocation();
            float myWidth = this.getWidth();
            float myHeight = this.getHeight();
            float fireBallWidth = myWidth * 0.75f;
            float fireBallHeight = myHeight * 0.75f;
            float xDiffrential = fireBallSpeed * horizontalDirection;
            float yDiffrential = fireBallSpeed * verticalDirection;

            FireBall fireBall = new FireBall(roomID, this, attackPower*2, xDiffrential, yDiffrential, locationX, locationY, fireBallWidth, fireBallHeight, directionAngle);
            this.projectiles.add(fireBall);
            resetAbility("B");
            idleAgain(spriteState);
        }
    }

    public void heal()
    {
        if(useAbility("Y"))
        {
            double healing = this.maxHealth / 2;
            if((this.HP + healing) > maxHealth)
                healing = maxHealth - this.HP;
            this.HP += healing;
            if(characterGrade == 2)
                this.clone.HP += healing;
            resetAbility("Y");
        }
    }

    public void setClone(Mage c)
    {
        this.clone = c;
    }

    @Override
    public boolean hit(Projectile p) {
        boolean hit = super.hit(p);
        if((characterGrade == 2) && hit)
        {
            double power = (double) p.getPower();
            this.clone.HP -= power;
        }
        return hit;
    }

    @Override
    public boolean poison(int power) {
        if(characterGrade == 2)
        {
            double damage = (double) power;
            this.clone.HP -= damage;
        }
        return super.poison(power);
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
                if(characterGrade == 4)
                    this.HP += this.maxHealth / 900;

                if(useAbility("Y"))
                    heal();

                boolean range = inRange();
                if(range)
                    backed = true;


                if(locked<=0)
                {
                    if (useAbility("A") && range)
                    {
                        locked = 10;
                        mist();
                    }
                    else if(useAbility("X") && (this.distanceVector < (width * 5)))
                    {
                        if(characterGrade == 1)
                            this.effect = ailment();
                        locked = 10;
                        lightLine();
                    }
                    else if(useAbility("B"))
                    {
                        if(!aim(horizontalDistance, verticalDistance, physicalFireBallSpeed))
                            moveBack = false;
                        else
                        {
                            locked = 10;
                            fireball();
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

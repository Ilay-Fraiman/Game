package com.example.firebasetest;

import android.graphics.Bitmap;
import android.widget.Switch;

public class Mage extends Character{
    public static Bitmap mageSprite;
    private Bitmap lineSprite;//temporary
    private Bitmap fireSprite;//temporary
    private Bitmap mistSprite;//temporary
    private double maxHealth;
    private float fireBallSpeed;
    private float physicalFireBallSpeed;
    private String effect;
    private Mage clone;
    public Mage(int level, int characterGrade, int ID, float xLocation, float yLocation)
    {
        super(level,2,3,5, mageSprite, ID, xLocation, yLocation, characterGrade);
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
            magicLine(this.effect, lineSprite);
            this.effect = "none";
            resetAbility("X");
        }
    }

    public void mist() {
        if (useAbility("A")) {
            float locationX = this.getXPercentage();
            float locationY = this.getYPercentage();
            float xDiffrential = this.getWidthPercentage() * this.horizontalDirection;
            float yDiffrential = this.getHeightPercentage() * this.verticalDirection;
            if (this.horizontalDirection > 0)
                locationX += this.getWidthPercentage();
            if (this.verticalDirection > 0)
                locationY += this.getHeightPercentage();

            locationX += xDiffrential;
            locationY += yDiffrential;

            Mist mist = new Mist(mistSprite, roomID, this, attackPower/2, locationX, locationY, xDiffrential, yDiffrential);
            this.projectiles.add(mist);
            resetAbility("A");
        }
    }
    public void fireball()
    {
        if(useAbility("B"))
        {
            float locationX = this.getXPercentage();
            float locationY = this.getYPercentage();
            float myWidth = this.getWidthPercentage();
            float myHeight = this.getHeightPercentage();
            float fireBallWidth = myWidth * 0.75f;
            float fireBallHeight = myHeight * 0.75f;
            float xDiffrential = fireBallWidth * this.horizontalDirection;
            float yDiffrential = fireBallHeight * this.verticalDirection;//false. width, height should be of fireball
            if (this.horizontalDirection > 0)
                locationX += myWidth;
            if (this.verticalDirection > 0)
                locationY += myHeight;

            locationX += xDiffrential;
            locationY += yDiffrential;

            xDiffrential = fireBallSpeed * horizontalDirection;
            yDiffrential = fireBallSpeed * verticalDirection;

            FireBall fireBall = new FireBall(fireSprite, roomID, this, attackPower*2, xDiffrential, yDiffrential, locationX, locationY, fireBallWidth, fireBallHeight, directionAngle);
            this.projectiles.add(fireBall);
            resetAbility("B");
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
        if(characterGrade == 2)
        {
            double power = (double) p.getPower();
            this.clone.HP -= power;
        }
        return super.hit(p);
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
                float playerY = values[1];
                float width = values[2];
                float height = values[3];
                float playerWidth = values[4];
                float playerHeight = values[5];
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
                        mist();
                        locked = 10;
                    }
                    else if(useAbility("X") && (this.distanceVector < width * 5))
                    {
                        if(characterGrade == 1)
                            this.effect = ailment();
                        lightLine();
                        locked = 10;
                    }
                    else if(useAbility("B"))
                    {
                        if(!aim(horizontalDistance, verticalDistance, physicalFireBallSpeed))
                            moveBack = false;
                        else
                        {
                            fireball();
                            locked = 10;
                        }
                    }
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
                    thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package com.example.firebasetest;

import android.graphics.Bitmap;

public class Mage extends Character{
    public static Bitmap mageSprite;
    private Bitmap lineSprite;//temporary
    private Bitmap fireSprite;//temporary
    private Bitmap mistSprite;//temporary
    private int maxHealth;
    public Mage(int level, int characterGrade, int ID, float xLocation, float yLocation, float width, float height)
    {
        super(level,2,3,5, mageSprite, ID, xLocation, yLocation, width, height);
        this.maxHealth=this.HP;
    }
    public void lightLine()
    {
        if(useAbility("X"))
        {
            float locationX = this.getXPercentage();
            float locationY = this.getYPercentage();
            float xDiffrential = this.getWidthPercentage() * this.horizontalDirection;
            float yDiffrential = this.getHeightPercentage() * this.verticalDirection;
            if(this.horizontalDirection>0)
                locationX += this.getWidthPercentage();
            if(this.verticalDirection>0)
                locationY += this.getHeightPercentage();

            locationX += xDiffrential;
            locationY += yDiffrential;

            for (int i = 0; i<5;i++)
            {
                LightLine lightLine = new LightLine(lineSprite, roomID, false, this, attackPower, 0, 0, locationX, locationY, xDiffrential, yDiffrential);
                locationX += xDiffrential;
                locationY += yDiffrential;
                this.projectiles.add(lightLine);
            }

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
            float xDiffrential = this.getWidthPercentage() * this.horizontalDirection;
            float yDiffrential = this.getHeightPercentage() * this.verticalDirection;//false. width, height should be of fireball
            if (this.horizontalDirection > 0)
                locationX += this.getWidthPercentage();
            if (this.verticalDirection > 0)
                locationY += this.getHeightPercentage();

            locationX += xDiffrential;
            locationY += yDiffrential;
            yDiffrential += (xDiffrential / 2);

            FireBall fireBall = new FireBall(fireSprite, roomID, this, attackPower*2, xDiffrential, yDiffrential, locationX, locationY, xDiffrential, yDiffrential);
            this.projectiles.add(fireBall);
            resetAbility("B");
        }
    }

    public void heal()
    {
        if(useAbility("Y"))
        {
            this.HP += (this.maxHealth / 2);
            resetAbility("Y");
        }
    }
}

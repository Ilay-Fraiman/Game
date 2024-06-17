package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Arrow extends Projectile{
    private boolean ricochet;
    private boolean homing;
    private boolean toHome;
    private Character homingOn;
    private long timeToAim;
    public Arrow(Bitmap sprite, int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, boolean isRicochet, boolean isHoming, boolean isPoison, double direction)
    {
        super(sprite, ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height, direction, "none");
        this.ricochet = isRicochet;
        this.homing = isHoming;
        if(isPoison)
            this.ailment = "poison";
        moving = true;
        toHome = true;
        homingOn = null;
        timeToAim = 0;
    }

    public boolean isRicochet() {
        return ricochet;
    }

    public boolean isHoming() {
        return homing;
    }

    public boolean needsToHome()
    {
        return (this.homing && this.toHome);
    }

    public void home(ArrayList<Character> characterArrayList)
    {
        if(this.homing && this.toHome)
        {
            if(this.getCreator().getCharacterGrade() == 5)
            {
                boolean done = false;
                for (Character c:
                     characterArrayList) {
                    if((!done) && (c.getCharacterGrade()!= 5))
                    {
                        this.homingOn = c;
                        this.toHome = false;
                        done = true;
                    }
                }
            }
            else
            {
                this.homingOn = Character.getPlayer();
                this.toHome = false;
            }
        }
    }

    public boolean aimAtTarget()
    {
        long now = System.currentTimeMillis();
        if((now >= this.timeToAim) && ((this.homing) && (!this.toHome)))
            return aimArrow();
        return false;
    }

    private boolean aimArrow()
    {
        Character target = this.homingOn;
        if(target.isAlive())
        {
            float myX = this.getXPercentage();
            float myY = this.getYPercentage();
            float myWidth = this.getWidthPercentage();
            float myHeight = this.getHeightPercentage();
            float targetX = target.getXPercentage();
            float targetY = target.getYPercentage();
            float targetWidth = target.getWidthPercentage();
            float targetHeight = target.getHeightPercentage();
            float horizontalDistance = 0;
            float verticalDistance = 0;

            if(targetX < myX)
                horizontalDistance = (targetX + targetWidth) - myX;
            else if(targetX > myX)
                horizontalDistance = targetX - (myX + myWidth);

            if(targetY < myY)
                verticalDistance = (targetY + targetHeight) - myY;
            else if(targetY > myY)
                verticalDistance = targetY - (myY + myHeight);

            double horSpeed = (double) this.getHorizontalSpeed();
            double vertSpeed = (double) this.getVerticalSpeed();
            double speedVector = Math.sqrt(Math.pow(horSpeed, 2) + Math.pow(vertSpeed, 2));

            double newVert = (double) verticalDistance * (-1);
            double aimingAngle = Math.atan2(newVert, (double) horizontalDistance);//angle for aiming(complex numbers)
            this.setAngle(Math.toDegrees(aimingAngle));
            horSpeed = Math.cos(angle) * speedVector;
            vertSpeed = Math.sin(angle) * (-1) * speedVector;//complex numbers are in the gauss plane.
            //the results work for positive right and up. in here, positive is right and down
            this.setHorizontalSpeed((float) horSpeed);
            this.setVerticalSpeed((float) vertSpeed);

            long time = System.currentTimeMillis();
            this.timeToAim = time + 333L;

            return true;
        }
        else
        {
            this.homingOn = null;
            this.toHome = true;
            return false;
        }
    }

}

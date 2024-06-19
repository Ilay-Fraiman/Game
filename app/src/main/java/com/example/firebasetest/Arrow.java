package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Arrow extends Projectile{
    private boolean ricochet;
    private boolean homing;
    private boolean toHome;
    private Character homingOn;
    private long timeToAim;
    private long readyToRicochet;
    public Arrow(int ID, Character creator, float power, float hSPD, float vSPD, float xLocation, float yLocation, float width, float height, boolean isRicochet, boolean isHoming, boolean isPoison, double direction)
    {
        super("Arrow", ID, creator, power, hSPD, vSPD, xLocation, yLocation, width, height, direction, "none");
        this.ricochet = isRicochet;
        this.homing = isHoming;
        if(isPoison)
            this.ailment = "poison";
        moving = true;
        toHome = true;
        homingOn = null;
        timeToAim = 0;
        this.readyToRicochet = System.currentTimeMillis() + 300L;
    }

    public boolean isRicochet() {
        return (ricochet && (readyToRicochet < System.currentTimeMillis()));
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
            float myX = this.getXLocation();
            float myY = this.getYLocation();
            float myWidth = this.getWidth();
            float myHeight = this.getHeight();
            float targetX = target.getXLocation();
            float targetY = target.getYLocation();
            float targetWidth = target.getWidth();
            float targetHeight = target.getHeight();
            float horizontalDistance = 0;
            float verticalDistance = 0;

            horizontalDistance = (targetX != myX)? (targetX - myX): 0;
            verticalDistance = (targetY != myY)? (targetY - myY): 0;

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

    @Override
    public String getSpriteName() {
        String name = super.getSpriteName();
        String type = "";
        if(ricochet)
            type += "ricochet";
        if(homing)
            type += "homing";
        if(this.ailment.equals("poison"))
            type += "poison";
        if(type.isEmpty())
            return name;
        else
        {
            String sprite = type + name;
            return sprite;
        }
    }
}

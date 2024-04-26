package com.example.firebasetest;

import android.graphics.Bitmap;
import java.util.Timer;
import java.util.TimerTask;

public class Knight extends Character{
    public static Bitmap knightSprite;//different sprites actually
    private boolean shielded = false;//parry reflects(?), not only shields. shield dosent take damage.
    private boolean parry = false;
    private int shieldHP;//half knight hp
    public Knight(int level, int characterGrade, int ID, float xLocation, float yLocation, float width, float height){
        super(level,5,2,3, knightSprite, ID, xLocation, yLocation, width, height);
        //change stats for character grade
    }
    public void Attack()//maybe virtual
    {
        if(useAbility("X") && !shielded)
        {
            if(Math.abs(verticalDirection)>Math.abs(horizontalDirection))
                switchSizes();

            float xAxis = horizontalDirection * itemWidth;
            float yAxis = verticalDirection *  itemHeight;
            float locationHor = this.getXPercentage() + xAxis;
            float locationVert = this.getYPercentage() + yAxis;

            BladeAttack bladeAttack = new BladeAttack(itemSprite, roomID, this, attackPower, locationHor, locationVert, itemWidth, itemHeight);
            this.projectiles.add(bladeAttack);

            if(Math.abs(verticalDirection)>Math.abs(horizontalDirection))
                switchSizes();

            resetAbility("X");
        }
        else
        {
            //maybe prompt something, maybe also have ui for cooldown.
        }
    }

    public void shieldHeld()
    {
        if(useAbility("A") && shieldHP > 0) {
            shielded = true;
        }
    }

    public void shieldReleased()
    {
        shielded = false;
        resetAbility("A");
    }

    public void parry()
    {
        if(useAbility("A") && shieldHP > 0) {
            parry = true;
            class UnParry extends TimerTask {
                private Knight knight;

                UnParry(Knight k)
                {
                    this.knight = k;
                }

                @Override
                public void run() {
                    knight.unParry();
                }
            }
            Timer timer = new Timer();
            TimerTask task = new UnParry(this);
            timer.schedule(task, 500L);
        }
    }

    public void unParry()
    {
        this.parry = false;
    }

    public void buff()
    {
        if(useAbility("B")) {
            this.attackPower *= 2;
            class DeBuff extends TimerTask {
                private Knight knight;

                DeBuff(Knight k)
                {
                    this.knight = k;
                }

                @Override
                public void run() {
                    knight.deBuff();
                    knight.resetAbility("B");
                }
            }
            Timer timer = new Timer();
            TimerTask task = new DeBuff(this);
            timer.schedule(task, 5000L);
            resetAbility("B");
        }
    }

    public void deBuff()
    {
        this.attackPower /= 2;
    }

    public void Horse(Bitmap horsedKnight)
    {
        if(useAbility("Y"))//and ult charged?
        {
            //change sprite
            this.setYPercentage(this.getYPercentage() - (this.getHeightPercentage() * 2));
            this.setHeightPercentage(this.getHeightPercentage() * 3);
            this.setWidthPercentage(this.getWidthPercentage() * 2);
            //double or triple movement speed
            //add hp to the horse
            //range probably changes.
        }
    }
}

package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Knight extends Character{
    public static Bitmap knightSprite;//different sprites actually
    private boolean shielded = false;//parry reflects(?), not only shields. shield dosent take damage.
    private boolean parry = false;
    private int shieldHP;//half knight hp
    private int horseHP;//same hp as knight
    private boolean mounted = false;
    public Knight(int level, int characterGrade, int ID, float xLocation, float yLocation, float width, float height){
        super(level,5,2,3, knightSprite, ID, xLocation, yLocation, width, height);
        //change stats for character grade
        //is there thread for enemy?
    }
    public void Attack()//maybe virtual
    {
        if(useAbility("X") && !shielded)
        {
            if(Math.abs(verticalDirection)<Math.abs(horizontalDirection))//(?)
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

    public void Mount(Bitmap horsedKnight)
    {
        if(useAbility("Y"))//and ult charged?
        {
            this.mounted = true;
            //change sprite
            this.setYPercentage(this.getYPercentage() - (this.getHeightPercentage() * 2));
            this.setHeightPercentage(this.getHeightPercentage() * 3);
            this.setWidthPercentage(this.getWidthPercentage() * 2);
            this.movementSpeed *=4;//(?)
            //horse has a seperate hitbox
            this.itemHeight *= 2;
            this.itemWidth *= 2;
        }
    }

    public void dismount()
    {
        //ult ends
        this.setHeightPercentage(this.getHeightPercentage() / 3);
        this.setWidthPercentage(this.getWidthPercentage() / 2);
        this.movementSpeed /=4;
        this.itemHeight /= 2;
        this.itemWidth /= 2;
    }

    @Override
    public boolean hit(Projectile p) {//this and its continues should be in character
        //to block an attack both directions need to be opposite (only on shield, not parry)
        boolean shieldBroke = false;
        int damageSustained=(int)p.getPower();
        if(shielded){
            float horizontal = p.getHorizontalSpeed() * horizontalDirection;
            float vertical = p.getVerticalSpeed() * verticalDirection;
            if (horizontal < 0 && vertical < 0)
            {
                shieldHP -=damageSustained;
                if (shieldHP < 0)
                {
                    shieldBroke = true;
                    damageSustained = Math.abs(shieldHP);
                }
            }
        }
        if(parry)
        {
            p.setHorizontalSpeed((p.getHorizontalSpeed() * (-1)));
            p.setHorizontalSpeed((p.getHorizontalSpeed() * (-1)));
        }
        else if(shieldBroke || !shielded)
        {
            if (p instanceof Mist)
            {
                this.movementSpeed /= 2;
                class UnFreeze extends TimerTask {
                    private Knight knight;

                    UnFreeze(Knight k)
                    {
                        this.knight = k;
                    }

                    @Override
                    public void run() {
                        knight.unFreeze();
                    }
                }
                Timer timer = new Timer();
                TimerTask task = new UnFreeze(this);
                timer.schedule(task, 5000L);
            }
            if (p instanceof Arrow && ((Arrow) p).isPoison())
                this.poisoned();
        }
        if(mounted)
        {
            //check if hit horse. if so:
            horseHP -= damageSustained;
            if(horseHP<0)
                dismount();
            return false;//alive
        }
        else
        {
            this.HP-=damageSustained;
            return this.HP<0;//is dead?
        }
    }

    private void poisoned()
    {
        class Poison extends TimerTask {
            private Knight knight;
            private int repeats;

            Poison(Knight k, int r)
            {
                this.knight = k;
                this.repeats = r;
            }

            @Override
            public void run() {
                //find a way to make it repeat a set number of times and stop
            }
        }
        Timer timer = new Timer();
        TimerTask task = new Poison(this, 10);
        timer.schedule(task, 5000L);
    }

    public void unFreeze()
    {
        this.movementSpeed *=2;
    }
}

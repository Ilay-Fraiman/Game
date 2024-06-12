package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Knight extends Character{
    public String knightSprite;//different sprites actually
    private boolean shielded;//parry reflects(?), not only shields. shield dosent take damage.
    private boolean parry;
    private double shieldHP;//half knight hp
    private double horseHP;//same hp as knight
    private double maxShieldHP;
    private double maxHorseHP;
    private float horseDirection;
    private boolean mounted;
    private boolean shieldHealing;
    private boolean horseHealing;
    public Knight(int level, int characterGrade, int ID, float xLocation, float yLocation){
        super(level,5,2,3, "knight", ID, xLocation, yLocation, characterGrade);
        //change stats for character grade
        //is there thread for enemy?
        this.maxShieldHP = this.HP / 2;
        this.maxHorseHP = this.HP;
        this.shielded = false;
        this.parry = false;
        this.horseDirection = -1;
        this.mounted = false;
        this.shieldHealing = false;
        this.horseHealing = false;
        switch (characterGrade)//temporary. need to add sprites and threads
        {
            case 1:
                this.itemHeight *= 3;
                break;
            case 2:
                this.attackCooldown /= 2;
                break;
            case 3:
                this.maxShieldHP *= 2;
                this.itemHeight *= 2;
                this.itemWidth *= 2;
                this.attackPower *= 2;
                this.attackCooldown *= 2;
                break;
            case 4:
                mount();
                mounted = false;
                break;
        }
        this.shieldHP=this.maxShieldHP;
        this.horseHP=this.maxHorseHP;
        this.running = true;
        if (threadStart)
            thread.start();
    }
    public void attack()
    {
        if(useAbility("X") && !shielded)
        {
            Attack();

            resetAbility("X");
        }
        else
        {
            //maybe prompt something, maybe also have ui for cooldown.
        }
    }

    public boolean shieldOrParry(int num)//1 shield 2 parry
    {
        if((useAbility("A") && shieldHP > 0) && (!shielded))
        {
            if(num == 1)
                shieldHeld();
            else
                parry();
            return true;
        }
        return false;
    }

    public void shieldHeld()
    {
        shielded = true;
    }

    public void shieldReleased()
    {
        if(shielded)
        {
            shielded = false;
            resetAbility("A");
        }
        if (shieldHP <= 0 && !shieldHealing)
        {
            shieldHealing = true;
            class RestoreShield extends TimerTask {
                private Knight knight;
                RestoreShield(Knight k)
                {
                    this.knight = k;
                }

                @Override
                public void run() {
                    knight.shieldRestore();
                }
            }
            Timer timer = new Timer();
            TimerTask task = new RestoreShield(this);
            timer.schedule(task, 10000L);
        }
    }

    public void parry()
    {
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
        resetAbility("B");
    }

    public void mount()
    {
        if((characterGrade == 4) || (useAbility("Y") && (!mounted && horseHP>0)))//and ult charged?
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
        else if(mounted)
            dismount();
    }

    public void dismount()
    {
        //ult ends
        //change sprite back
        this.mounted=false;
        this.setHeightPercentage(this.getHeightPercentage() / 3);
        this.setWidthPercentage(this.getWidthPercentage() / 2);
        this.movementSpeed /=4;
        this.itemHeight /= 2;
        this.itemWidth /= 2;
        resetAbility("Y");
        if(horseHP<=0 && !horseHealing)
        {
            horseHealing = true;
            class RestoreHorse extends TimerTask {
                private Knight knight;
                RestoreHorse(Knight k)
                {
                    this.knight = k;
                }

                @Override
                public void run() {
                    knight.horseRestore();
                }
            }
            Timer timer = new Timer();
            TimerTask task = new RestoreHorse(this);
            timer.schedule(task, 60000L);
        }
    }

    @Override
    public boolean hit(Projectile p) {//this and its continues should be in character
        //to block an attack both directions need to be opposite (only on shield, not parry)
        boolean shieldBroke = false;
        float damageSustained = p.getPower();
        String ailment = p.getAilment();
        boolean isDead = false;
        if(ailment.equals("shatter"))
        {
            if(mounted)
            {
                this.horseHP -= damageSustained;
                if (horseHP <= 0)
                {
                    damageSustained = (float) horseHP * (-1);
                    dismount();
                    p.setPower(damageSustained);
                    return super.hit(p);
                }
                else
                {
                    this.shatter();
                    return false;
                }
            }
            else
                return super.hit(p);
        }
        if(shielded){
            float horizontal = p.getHorizontalSpeed() * horizontalDirection;
            float vertical = p.getVerticalSpeed() * verticalDirection;
            if (horizontal < 0 && vertical < 0)
            {
                shieldHP -= damageSustained;
                if (ailment == "fire")
                    burningShield(damageSustained / 10);
                if (shieldHP <= 0)
                {
                    shieldBroke = true;
                    damageSustained = (float) Math.abs(shieldHP);
                }
            }
        }
        if(parry)
        {
            p.setHorizontalSpeed((p.getHorizontalSpeed() * (-1)));
            p.setVerticalSpeed((p.getVerticalSpeed() * (-1)));
            p.setCreator(this);
            this.projectiles.add(p);
        }
        else if(shieldBroke || !shielded)
        {
            p.setPower(damageSustained);
            if(mounted)
            {
                this.horseHP -= damageSustained;

                switch (ailment)
                {
                    case "poison":
                        this.poisonedHorse(damageSustained / 10);
                        break;
                    case "freeze":
                        this.freeze();
                        break;
                    case "shock":
                        dismount();
                        shock();
                        break;
                }

                if(this.horseHP<=0)
                    dismount();
            }
            else
            {
                isDead = super.hit(p);
            }
        }
        if(shieldBroke)
        {
            shieldReleased();
        }
        return isDead;
    }

    private void poisonedHorse(double power)
    {
        class Poison extends TimerTask {
            private Knight knight;
            private int repeats;
            private double power;

            Poison(Knight k, int r, double p)
            {
                this.knight = k;
                this.repeats = r;
                this.power = p;
            }

            @Override
            public void run() {
                if(repeats>0)
                {
                    if(!this.knight.horsePoison(power))
                        repeats = 0;
                    repeats--;
                    Timer timer = new Timer();
                    TimerTask task = new Poison(knight, repeats, power);
                    timer.schedule(task, 1000L);
                }
            }
        }
        Timer timer = new Timer();
        TimerTask task = new Poison(this, 10, power);
        timer.schedule(task, 1000L);
    }

    public boolean horsePoison(double power)
    {
        this.horseHP -= power;
        if(horseHP<=0)
        {
            dismount();
            return false;
        }
        return true;
    }

    public boolean shieldBurn(double power)
    {
        this.shieldHP -= power;
        if(shieldHP<=0)
        {
            shieldReleased();
            return false;
        }
        return true;
    }

    private void burningShield(double power)
    {
        class Burn extends TimerTask {
            private Knight knight;
            private int repeats;
            private double power;

            Burn(Knight k, int r, double p)
            {
                this.knight = k;
                this.repeats = r;
                this.power = p;
            }

            @Override
            public void run() {
                if(repeats>0)
                {
                    if(!this.knight.shieldBurn(power))
                        repeats = 0;
                    repeats--;
                    Timer timer = new Timer();
                    TimerTask task = new Burn(knight, repeats, power);
                    timer.schedule(task, 1000L);
                }
            }
        }
        Timer timer = new Timer();
        TimerTask task = new Burn(this, 10, power);
        timer.schedule(task, 1000L);
    }

    public void shieldRestore()
    {
        this.shieldHP = this.maxShieldHP;
    }

    public void horseRestore()
    {
        this.horseHP=this.maxHorseHP;
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
                moving = false;
                moveBack = false;

                //if locked = 0, reset sprite

                if((characterGrade != 4) && (useAbility("Y") && (!mounted && horseHP>0)))
                    mount();//temp sprite

                if((useAbility("B") && (locked <= 0)) && (!shocked))
                {
                    shieldReleased();
                    buff();
                    locked = 10;
                }

                if(inRange())
                {
                    if (locked <= 0)
                    {
                        if(useAbility("X"))
                        {
                            moveBack = false;
                            shieldReleased();
                            attack();
                            locked = 15;
                        }
                        else
                        {
                            moving = true;
                            moveBack = true;

                            if (this.characterGrade != 2 && shieldOrParry(this.getRandomNumber(1, 2)))
                                locked = 15;
                        }
                    }
                    else {
                        moveBack = true;
                        moving = true;
                    }
                }
                else
                {
                    if(this.characterGrade != 2)
                    {
                        shieldOrParry(1);
                        locked = 15;
                    }

                    if(useAbility("X"))
                        moving = true;
                }

                if(moving)
                {
                    float side = (moveBack) ? -1 : 1;
                    this.horizontalMovement = this.horizontalDirection * side;
                    if(yLocation + height == GameView.height)
                        this.verticalMovement = this.verticalDirection * side * this.movementSpeed;
                }

                if(characterGrade == 4)
                {
                    if((xLocation <= 0) || (xLocation + width >= GameView.width))
                        horseDirection *= -1;
                    horizontalMovement = horseDirection;
                    moving = true;
                }
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

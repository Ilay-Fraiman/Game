package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Knight extends Character{
    private boolean shielded;//parry reflects, not only shields. shield doesn't take damage.
    private boolean parry;
    private double shieldHP;//half knight hp
    private double horseHP;//same hp as knight
    private double maxShieldHP;
    private double maxHorseHP;
    private float horseDirection;
    private boolean mounted;
    private boolean shieldHealing;
    private boolean horseHealing;
    private String bladeType;
    private String shieldType;
    public Knight(int level, int characterGrade, int ID, float xLocation, float yLocation){
        super(level,5,2,3, "knight", ID, xLocation, yLocation, characterGrade);
        this.maxShieldHP = this.HP / 2;
        this.maxHorseHP = this.HP;
        this.shielded = false;
        this.parry = false;
        this.horseDirection = -1;
        this.mounted = false;
        this.shieldHealing = false;
        this.horseHealing = false;
        this.bladeType = "sword";
        this.shieldType = "shield";
        switch (characterGrade)
        {
            case 1:
                this.itemWidth *= 3;
                this.bladeType = "spear";
                break;
            case 2:
                this.attackCooldown /= 2;
                this.shieldType = "sword";
                break;
            case 3:
                this.maxShieldHP *= 2;
                this.itemHeight *= 2;
                this.itemWidth *= 2;
                this.attackPower *= 2;
                this.attackCooldown *= 2;
                float height = this.getHeight();
                this.setYLocation(this.getYLocation() - (height / 2));
                this.setWidth(this.getWidth() * 2);
                this.setHeight(height * 2);
                this.bladeType = "greatSword";
                this.shieldType = "greatShield";
                break;
            case 4:
                mount();
                mounted = false;
                break;
        }
        this.itemSprite = this.bladeType;
        this.secondItemSprite = this.shieldType;
        this.shieldHP=this.maxShieldHP;
        this.horseHP=this.maxHorseHP;
        this.running = true;
        if (threadStart)
            thread.start();
    }

    public void setItems(int num)//keep changing the items' names (according to newest phone disc)
    {
        this.itemSprite = this.bladeType;
        this.secondItemSprite = this.bladeType;
        switch (num)
        {
            case 1:
                this.secondItemSprite = this.shieldType;
                break;
            case 2:
                this.itemSprite = this.shieldType;
                break;
            case 3:
                this.itemSprite = "potion";
                break;
        }
    }

    public void attack()
    {
        if((useAbility("X") && (!shielded)) && (!performingAction))
        {
            setItems(1);
            Attack();
            resetAbility("X");
        }
    }

    public boolean shieldOrParry(int num)//1 shield 2 parry
    {
        if((((useAbility("A") && shieldHP > 0) && ((!shielded) && (!parry))) && (characterGrade != 2)) && (!performingAction))
        {
            setItems(2);
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
        spriteState = "shielding";
        performingAction = true;
        idleAgain(spriteState);
    }

    public void shieldReleased()
    {
        if(shielded)
        {
            shielded = false;
            if(itemSprite.equals(shieldType))
                setItems(1);
            resetAbility("A");
            reIdle("releaseShield");
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
        spriteState = "parrying";
        performingAction = true;
        idleAgain(spriteState);
        addStatus(4);
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
        if(itemSprite.equals(shieldType))
            setItems(1);
        removeStatus(4);
    }

    public void buff()
    {
        if(useAbility("B") && ((!shielded) && (!performingAction))) {
            this.attackPower *= 2;
            spriteState = "drinking";
            setItems(3);
            performingAction = true;
            addStatus(5);
            idleAgain(spriteState);
            class DeBuff extends TimerTask {
                private Knight knight;
                private int type;

                DeBuff(Knight k, int t)
                {
                    this.knight = k;
                    this.type = t;
                }

                @Override
                public void run() {
                    knight.deBuff(type);
                }
            }
            Timer timer2 = new Timer();
            TimerTask task2 = new DeBuff(this, 1);
            timer2.schedule(task2, 330L);
            Timer timer = new Timer();
            TimerTask task = new DeBuff(this, 2);
            timer.schedule(task, 5000L);
            resetAbility("B");
        }
    }

    public void deBuff(int typeCode)
    {
        if(typeCode == 1)
        {
            if(itemSprite.equals("potion"))
                setItems(1);
        }
        else
        {
            this.attackPower /= 2;
            removeStatus(5);
            resetAbility("B");
        }
    }

    public void mount()
    {
        if((characterGrade == 4) || ((useAbility("Y")) && (!mounted && horseHP>0)))
        {
            this.mounted = true;
            float height = this.getHeight();
            this.setYLocation(this.getYLocation() - (height / 2));
            this.setHeight(height * 2);
            this.setWidth(this.getWidth() * 3);
            this.movementSpeed *= 3;
            float multiplier = (characterGrade == 4)? 2f: 1.5f;
            this.attackPower *= multiplier;
            this.itemHeight *= multiplier;
            this.itemWidth *= multiplier;
            if(characterGrade == 4)
            {
                bladeType = "greatSword";
                shieldType = "greatShield";
                maxShieldHP *= 2;
                this.HP *= 2;
            }
        }
        else if(mounted)
            dismount();
    }

    public void dismount()
    {
        if(mounted)
        {
            this.mounted=false;
            float height = this.getHeight();
            this.setYLocation(this.getYLocation() + (height / 4));
            this.setHeight(height / 2);
            this.setWidth(this.getWidth() / 3);
            this.movementSpeed /= 3;
            this.itemHeight /= 1.5;
            this.itemWidth /= 1.5;
            this.attackPower /= 1.5;
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
    }

    @Override
    public boolean hit(Projectile p) {
        //to block an attack both directions need to be opposite (only on shield, not parry)
        if (p.canHit(this))
        {
            boolean shieldBroke = false;
            float damageSustained = p.getPower();
            String ailment = p.getAilment();
            boolean hasHit = true;
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
                        return true;
                    }
                }
                else
                    return super.hit(p);
            }
            if(shielded){
                float horizontal = horizontalDirection;
                float vertical = verticalDirection;
                float pHor = horseDirection;
                float pVer = verticalDirection;
                boolean collision = false;
                if(p.isMoving() || p instanceof LightLine)
                {
                    double angle = p.getAngle();
                    pHor = (float) Math.cos(Math.toDegrees(angle));
                    pVer = (float) Math.sin(Math.toDegrees(angle));
                    pVer *= (-1);
                }
                else if(p instanceof BladeAttack)
                {
                    Character c = p.getCreator();
                    float x = c.getXLocation();
                    float y = c.getYLocation();
                    pHor = x - this.getXLocation();
                    pVer = y - this.getYLocation();
                }
                else if(p instanceof GroundFire || p instanceof Mist)
                {
                    GameObject shield = this.getItem(0);
                    collision = GameObject.collision(shield, p);
                }
                horizontal = (pHor == 0)? -1: horizontal * pHor;
                vertical = (pVer == 0)? -1: vertical * pVer;
                if (((horizontal < 0) && (vertical < 0)) || (collision))
                {
                    shieldHP -= damageSustained;
                    if (ailment.equals("fire"))
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
                if(!((p instanceof  GroundFire || p instanceof Mist) || (p.getAilment().equals("laser"))))
                {
                    p.setHorizontalSpeed((p.getHorizontalSpeed() * (-1)));
                    p.setVerticalSpeed((p.getVerticalSpeed() * (-1)));
                    p.setCreator(this);
                    this.projectiles.add(p);
                }
            }
            else if(shieldBroke || !shielded)
            {
                p.setPower(damageSustained);
                if(mounted)
                {
                    this.horseHP -= damageSustained;
                    if(ailment.equals("poison"))
                        poisonedHorse(damageSustained / 10);
                    else if(ailment.equals("freeze"))
                        freeze();
                    else if(ailment.equals("shock")) {
                        dismount();
                        shock();
                    }

                    if(this.horseHP<=0)
                        dismount();
                }
                else
                {
                    hasHit = super.hit(p);
                }
            }
            if(shieldBroke)
            {
                shieldReleased();
            }
            return hasHit;
        }
        return false;
    }

    private void poisonedHorse(double power)
    {
        addStatus(0);
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
                else
                    this.knight.removeStatus(0);
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
        addStatus(6);
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
                else
                    this.knight.removeStatus(6);
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

    public String getItemName()
    {
        return (this.bladeType.equals("spear"))? "stab": "slash";
    }

    @Override
    public String getSpriteName() {
        String name = super.getSpriteName();
        if(mounted || characterGrade == 4)
            name.replace("walking", "riding");
        return name;
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
                float width = values[2];
                float height = values[3];
                float xLocation = values[6];
                float yLocation = values[7];
                moving = false;
                moveBack = false;
                boolean range = inRange();
                boolean acted = performingAction;

                if((characterGrade != 4) && (useAbility("Y") && (!mounted && horseHP>0)))
                    mount();

                if(useAbility("B") && ((!acted) && (range)))
                {
                    shieldReleased();
                    if(shielded)
                        shielded = false;
                    acted = true;
                    buff();
                }

                if(range)
                {
                    if (!acted)
                    {
                        if(useAbility("X"))
                        {
                            moveBack = false;
                            shieldReleased();
                            if(shielded)
                                shielded = false;
                            acted = true;
                            attack();
                        }
                        else
                        {
                            moving = true;
                            moveBack = true;
                            if (this.characterGrade != 2 && shieldOrParry(this.getRandomNumber(1, 2)))
                                acted = true;
                        }
                    }
                    else {
                        moveBack = true;
                        moving = true;
                    }
                }
                else
                {
                    if((this.characterGrade != 2) && (!acted))
                    {
                        if(shieldOrParry(1))
                            acted = true;
                    }

                    if(useAbility("X"))
                        moving = true;
                }

                if(moving)
                {
                    float side = (moveBack) ? -1 : 1;
                    this.horizontalMovement = this.horizontalDirection * side;
                    if((yLocation + (height / 2)) == GameView.height)
                        this.verticalMovement = this.verticalDirection * side * this.movementSpeed;
                }

                if(characterGrade == 4)
                {
                    if(((xLocation - (width / 2)) <= 0) || ((xLocation + (width / 2)) >= GameView.width))
                        horseDirection *= -1;
                    horizontalMovement = horseDirection;
                    moving = true;
                }
                move(xLocation, yLocation, width, height);
                try {
                    thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
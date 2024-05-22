package com.example.firebasetest;

import android.graphics.Bitmap;
import java.util.*;

public class Character extends GameObject implements Runnable {
    private static Character player;
    protected int HP;
    protected long attackCooldown = 1150L;
    protected int attackPower;
    protected Bitmap itemSprite;//draw in gameobject draws the character, this class has override that adds the itemsprite on top with direction.
    protected float itemWidth;
    protected float itemHeight;
    protected ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    private boolean listSent = false;
    protected float horizontalDirection;
    protected float verticalDirection;
    private long resetX;
    private long resetY;
    private long resetB;
    private long resetA;
    protected int movementSpeed;
    protected float horizontalMovement;
    protected float verticalMovement;
    protected Thread thread;
    protected int characterGrade;
    protected int locked;
    protected boolean running;
    protected boolean threadStart = true;
    public Character(int level, int HPD, int ACD, int APD, Bitmap sprite, int ID, float xLocation, float yLocation, float width, float height, int characterGrade) //HPD, ACD, APD=2/3/5
    {
        super(sprite, ID, xLocation, yLocation, width, height);
        HP = HPD * 10 * level;
        attackCooldown-= (level*ACD);
        attackPower = level*APD;
        //set movement speed
        this.resetX = 0;
        this.resetB = 0;
        this.resetA = 0;
        this.resetY = System.currentTimeMillis() + 30000L;
        this.characterGrade = characterGrade;
        if (characterGrade == 5)
            threadStart = false;
        thread = new Thread(this);
    }

    protected ArrayList<Projectile> getProjectiles()
    {
        ArrayList<Projectile> sentList = new ArrayList<Projectile>();
        if(!listSent)
        {
            sentList.addAll(this.projectiles);
            listSent = true;
        }
        return sentList;
    }

    protected void emptyList()
    {
        if(listSent)
        {
            this.projectiles.clear();
            listSent = false;
        }
    }

    public ArrayList<Projectile> getProjectileList(int ID, ArrayList<Character> characters)
    {
        if(characters.contains(this) && ID == this.roomID)
        {
            ArrayList<Projectile> projectilesList = this.getProjectiles();
            this.emptyList();
            return projectilesList;
        }
        else
        {
            boolean sentValue = listSent;
            listSent = false;
            ArrayList<Projectile> emptiedList = this.getProjectiles();
            listSent = sentValue;
            return emptiedList;
        }
    }
    public void switchSizes()
    {
        float width = this.itemHeight;
        this.itemHeight = this.itemWidth;
        this.itemWidth = width;
    }

    public boolean useAbility(String button)
    {
        long resetTime = 0;
        switch (button){
            case "A":
                resetTime = resetA;
                break;
            case "B":
                resetTime = resetB;
                break;
            case "X":
                resetTime = resetX;
                break;
            case "Y":
                resetTime = resetY;
                break;
        }
        return System.currentTimeMillis() >= resetTime;
    }

    public void resetAbility(String button)
    {
        long start = System.currentTimeMillis();
        long time = attackCooldown;
        switch (button){
            case "A":
                time *= 5;
                resetA = start + time;
                break;
            case "B":
                time *= 10;
                resetB = start + time;
                break;
            case "X":
                resetX = start + time;
                break;
            case "Y":
                time *= 30;
                resetY = start + time;
                break;
        }
    }
    public boolean hit(Projectile p)
    {
        int power = (int)p.getPower();
        this.HP-=power;
        if (p instanceof Mist)
        {
            this.freeze();
        }
        if (p instanceof Arrow && ((Arrow) p).isPoison())
            this.poisoned(power / 10);

        return this.HP<=0;//is dead
    }

    private void poisoned(int power)
    {
        class Poison extends TimerTask {
            private Character chr;
            private int repeats;
            private int power;

            Poison(Character c, int r, int p)
            {
                this.chr = c;
                this.repeats = r;
                this.power = p;
            }

            @Override
            public void run() {
                if(repeats>0)
                {
                    this.chr.poison(power);
                    repeats--;
                    Timer timer = new Timer();
                    TimerTask task = new Poison(chr, repeats, power);
                    timer.schedule(task, 1000L);
                }
            }
        }
        Timer timer = new Timer();
        TimerTask task = new Poison(this, 10, power);
        timer.schedule(task, 1000L);
    }
    private void poison(int power)
    {
        this.HP -= power;
    }

    public void freeze()
    {
        this.movementSpeed /= 2;
        class UnFreeze extends TimerTask {
            private Character chr;

            UnFreeze(Character c)
            {
                this.chr = c;
            }

            @Override
            public void run() {
                chr.unFreeze();
            }
        }
        Timer timer = new Timer();
        TimerTask task = new UnFreeze(this);
        timer.schedule(task, 5000L);
    }

    public void unFreeze()
    {
        this.movementSpeed *=2;
    }

    public void Attack()
    {
        if((Math.abs(verticalDirection)<Math.abs(horizontalDirection))^(itemHeight<itemWidth))//(?)
            switchSizes();

        float xAxis = horizontalDirection * itemWidth;
        float yAxis = verticalDirection *  itemHeight;
        float locationHor = this.getXPercentage() + xAxis;
        float locationVert = this.getYPercentage() + yAxis;
        if (this.horizontalDirection > 0)
            locationHor += this.getWidthPercentage();
        if (this.verticalDirection > 0)
            locationVert += this.getHeightPercentage();

        BladeAttack bladeAttack = new BladeAttack(itemSprite, roomID, this, attackPower, locationHor, locationVert, itemWidth, itemHeight);
        this.projectiles.add(bladeAttack);
    }
    public static void setPlayer(Character p)
    {
        Character.player = p;
    }

    protected static float getPlayerX()
    {
        return Character.player.getXPercentage();
    }
    protected static float getPlayerY()
    {
        return Character.player.getYPercentage();
    }

    protected void move(float x, float y, float tWidth, float tHeight)
    {
        float xLocation = x + (movementSpeed * horizontalMovement);
        if(xLocation < 0)
            xLocation = 0;
        else if ((xLocation + tWidth) > GameView.width)
            xLocation = GameView.width - tWidth;
        setXPercentage(xLocation);
        horizontalMovement = 0;

        if(y + tHeight != GameView.height)
        {
            float yLocation = y + (movementSpeed * verticalMovement);
            if(yLocation < 0) {
                yLocation = 0;
                verticalMovement = -0.1f;
            }
            else if ((yLocation + tHeight) > GameView.height)
            {
                yLocation = GameView.height - tHeight;
                verticalMovement = -0.1f;
            }
            setYPercentage(yLocation);
            verticalMovement += 0.1;
        }
    }

    @Override
    public void run() {

    }
}

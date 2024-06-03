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
    protected float movementSpeed;
    protected float horizontalMovement;
    protected float verticalMovement;//should actually just be like movement speed. not 0.1-1
    protected Thread thread;
    protected int characterGrade;
    protected int locked;
    protected boolean running;
    protected boolean threadStart = true;
    protected boolean moving;
    protected int legsPos;
    public Character(int level, int HPD, int ACD, int APD, Bitmap sprite, int ID, float xLocation, float yLocation, int characterGrade) //HPD, ACD, APD=2/3/5
    {
        super(sprite, ID, xLocation, yLocation);
        this.moving = false;
        this.legsPos = 1;
        float myWidth = this.getWidthPercentage();
        itemWidth = myWidth;
        itemHeight = myWidth;
        movementSpeed = myWidth / 10;//speed is screen within 5 seconds. this is the speed for every frame (1/30 of a second)
        HP = HPD * 10 * level;
        attackCooldown-= (level*ACD);
        attackPower = level*APD;
        this.resetX = 0;
        this.resetB = 0;
        this.resetA = System.currentTimeMillis() + 10000L;
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
                time = 10000L;
                resetB = start + time;
                break;
            case "X":
                resetX = start + time;
                break;
            case "Y":
                time = 30000L;
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
    protected static float getPlayerWidth()
    {
        return Character.player.getWidthPercentage();
    }
    protected static float getPlayerHeight()
    {
        return Character.player.getHeightPercentage();
    }

    protected void move(float x, float y, float tWidth, float tHeight)//height should fit canvas
    {
        float accelerationNum = GameView.pixelHeight / 100;//transition from centimeters to meters
        accelerationNum *= 30;//transition from frames to seconds
        float accelerationDiff = 10 / 30;//acceleration in one frame
        float speedToZero = accelerationDiff / accelerationNum;//speed that after acceleration becomes 0
        speedToZero *= (-1);//it has to be negative

        if(moving)
        {
            float xLocation = x + (movementSpeed * horizontalMovement);
            if(xLocation < 0)
            {
                xLocation = 0;
                moving = false;
                legsPos = 1;
            }
            else if ((xLocation + tWidth) > GameView.width)
            {
                xLocation = GameView.width - tWidth;
                moving = false;
                legsPos = 1;
            }
            else
            {
                legsPos++;
                if (legsPos > 4)
                    legsPos = 1;
            }
            setXPercentage(xLocation);
        }
        else
        {
            legsPos = 1;
        }

        float yLocation = y + verticalMovement;
        if ((yLocation + tHeight) >= GameView.height)
        {
            yLocation = GameView.height - tHeight;
            verticalMovement = speedToZero;
        }
        else
        {
            legsPos = 5;
            if(yLocation < 0) {
                yLocation = 0;
                verticalMovement = speedToZero;
            }
        }
        setYPercentage(yLocation);
        verticalMovement *= accelerationNum;//transition from pixels per frame to meters per second
        verticalMovement += accelerationDiff;//down is positive, up is negative
        verticalMovement /= accelerationNum;//transition from meters per second to pixels per frame
    }

    public boolean inRange()//is the player in range of attack
    {
        double radius = Math.sqrt(Math.pow((double) horizontalDirection, 2) + Math.pow((double) verticalDirection, 2));//is rad 1
        if (radius <= 1)
            return true;

        double newVert = (double) verticalDirection * (-1);
        double angle = Math.atan2(newVert, (double) horizontalDirection);//converts as if radius is 1
        horizontalDirection = (float) Math.cos(angle);
        verticalDirection = (float) Math.sin(angle) * (-1);//complex numbers are in the gauss plane.
        //the results work for positive right and up. in here, positive is right and down
        return false;
    }

    public float[] aimAtPlayer()
    {
        float[] values = new float[10];
        float playerX = getPlayerX();
        float playerY = getPlayerY();
        float width = this.getWidthPercentage();
        float height = this.getHeightPercentage();
        float playerWidth = getPlayerWidth();
        float playerHeight = getPlayerHeight();
        float xLocation = this.getXPercentage();
        float yLocation = this.getYPercentage();
        float horizontalDistance = 0;
        float verticalDistance = 0;

        if(playerX < xLocation)
            this.horizontalDirection = (playerX + playerWidth) - xLocation;
        else
            this.horizontalDirection = playerX - (xLocation + width);
        if(playerY < yLocation)
            this.verticalDirection = (playerY + playerHeight) - yLocation;
        else
            this.verticalDirection = playerY - (yLocation + height);

        horizontalDistance = horizontalDirection;
        verticalDistance = verticalDirection;

        if((Math.abs(verticalDirection)<Math.abs(horizontalDirection))^(itemHeight<itemWidth))
            switchSizes();
        horizontalDirection /= itemWidth;
        verticalDirection /= itemHeight;

        values[0] = playerX;
        values[1] = playerY;
        values[2] = width;
        values[3] = height;
        values[4] = playerWidth;
        values[5] = playerHeight;
        values[6] = xLocation;
        values[7] = yLocation;
        values[8] = horizontalDistance;
        values[9] = verticalDistance;

        return values;
    }

    @Override
    public void run() {

    }
}

package com.example.firebasetest;

import android.graphics.Bitmap;
import java.util.*;

public class Character extends GameObject implements Runnable {
    private static Character player;
    protected double HP;
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
    protected double directionAngle;
    protected boolean shocked;
    protected int toShock;
    protected boolean moveBack;
    public double distanceVector;
    protected boolean shatter;
    private long parryCountdown;
    private boolean isParrying;
    public Character(int level, int HPD, int ACD, int APD, String spriteName, int ID, float xLocation, float yLocation, int characterGrade) //HPD, ACD, APD=2/3/5
    {//obviously change all sprites
        super(spriteName, ID, xLocation, yLocation);
        this.moving = false;
        this.legsPos = 1;
        float myWidth = this.getWidthPercentage();
        itemWidth = myWidth;
        itemHeight = myWidth;
        movementSpeed = myWidth / 10;//speed is screen within 5 seconds. this is the speed for every frame (1/30 of a second)
        HP = HPD * 15 * level;
        attackCooldown -= (level *ACD * 10);
        attackPower = level*APD;
        this.resetX = 0;
        this.resetB = 0;
        this.resetA = System.currentTimeMillis() + 10000L;
        this.resetY = System.currentTimeMillis() + 30000L;
        this.characterGrade = characterGrade;
        this.shocked = false;
        this.toShock = 0;
        this.moveBack = true;
        this.distanceVector = 0;
        this.shatter = false;
        this.parryCountdown = System.currentTimeMillis() + 500L;
        this.isParrying = false;
        if (characterGrade == 5)
            threadStart = false;
        thread = new Thread(this);
    }

    public int getCharacterGrade() {
        return characterGrade;
    }

    public void setCharacterGrade(int characterGrade) {
        this.characterGrade = characterGrade;
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
        if((characters.contains(this) && ID == this.roomID) || (this.characterGrade == 5))
        {
            //character grade 5 is player. sometimes granted special access.
            //character grade 5 is also characters that summon projectiles but shouldn't be drawn.
            //in order to not draw, you leave them out of the characters array of the room
            ArrayList<Projectile> projectilesList = this.getProjectiles();
            this.emptyList();
            return projectilesList;
        }
        else//accessed by a room that doesn't contain this
        {
            boolean sentValue = listSent;
            listSent = true;
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
        switch (button){
            case "A":
                resetA = start + 5000L;
                break;
            case "B":
                resetB = start + 10000L;
                break;
            case "X":
                resetX = start + attackCooldown;
                break;
            case "Y":
                resetY = start + 30000L;
                break;
        }
    }
    public boolean hit(Projectile p)
    {
        int power = (int)p.getPower();
        this.HP-=power;

        switch (p.getAilment())
        {
            case "poison":
                this.poisoned(power / 10);
                break;
            case "freeze":
                this.freeze();
                break;
            case "shock":
                shock();
                break;
            case "life steal":
                p.getCreator().HP += power;
                break;
            case "shatter":
                shatter();
                break;
        }

        if(this.HP <= 0)
        {
            this.running = false;
            return true;//is dead
        }
        return false;
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
                    if(!this.chr.poison(power))
                        repeats = 0;
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
    public boolean poison(int power)
    {
        this.HP -= power;
        if(this.HP <= 0)
        {
            this.running = false;
            return false;
        }
        return true;
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
        if(this instanceof Sage)
            bladeAttack.SetAilment("life steal");
        else if(this instanceof Archer)
            bladeAttack.setPower(attackPower / 2);
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
        if(!shatter)
        {
            float accelerationNum = GameView.canvasPixelHeight / 100;//transition from centimeters to meters
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
    }

    public static Character getPlayer() {
        return player;
    }

    public boolean inRange()//is the player in range of attack
    {
        double radius = Math.sqrt(Math.pow((double) horizontalDirection, 2) + Math.pow((double) verticalDirection, 2));//is rad 1
        this.distanceVector = radius;
        if (radius <= 1)
            return true;

        double newVert = (double) verticalDirection * (-1);
        double angle = Math.atan2(newVert, (double) horizontalDirection);//converts as if radius is 1
        this.directionAngle = Math.toDegrees(angle);
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
        else if(playerX > xLocation)
            this.horizontalDirection = playerX - (xLocation + width);
        else
            this.horizontalDirection = 0;
        if(playerY < yLocation)
            this.verticalDirection = (playerY + playerHeight) - yLocation;
        else if(playerY > yLocation)
            this.verticalDirection = playerY - (yLocation + height);
        else
            this.verticalDirection = 0;

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

    public boolean aim(float xDistance, float yDistance, float physicalSpeed)//aims the arrow and returns if will hit
    {
        double xDist = (double) xDistance;
        yDistance *= (-1);//here up is negative, in geometry up is positive
        double yDist = (double) yDistance;
        //using physics based formulas(in documentation), we wrote a quadratic formula for tan(theta)
        //and the following calculations are made according to and in order to fit said quadratic formula
        double pSpeed = (double) physicalSpeed;
        double speed = Math.pow(pSpeed, 2);//speed by meters per second, squared according to docs
        double a = 5 * xDist;//a in quadratic formula
        a /= speed;
        double ratio = yDist / xDist;//in docs
        double c = a + ratio;//c in quadratic formula
        double disc = a * c;//discriminanta (value below the root)
        disc *= 4;//in quadratic formula (b^2-4ac)
        double toRoot = 1 - disc;//in documentation, b = -1
        double bottom = 2 * a;//divider in quadratic formula
        if(toRoot < 0)//no value under the root, no solution=too far
            return false;
        else if(toRoot == 0)
        {
            double[] angledTime = arcTan((1/bottom), xDist, pSpeed);
            return faster(pSpeed, angledTime[0], angledTime[1], angledTime[0], angledTime[1]);
        }
        double rooted = Math.sqrt(toRoot);
        double f = 1 + rooted;//quadratic formula has 2 solutions (+-)
        f /= bottom;//division in quadratic formula
        double g = 1 - rooted;
        g /= bottom;
        double[] angledTime1 = arcTan(f, xDist, pSpeed);
        double[] angledTime2 = arcTan(g, xDist, pSpeed);
        return faster(pSpeed, angledTime1[0], angledTime1[1], angledTime2[0], angledTime2[1]);
    }

    private boolean ceiling(double speed, double angle)//checks if projectile hits the ceiling (it shouldn't)
    {
        double direction = Math.sin(angle);
        if (direction < 0)//meaning the vertical direction is down
            return false;//thus, it cannot touch the ceiling
        double vector = speed * direction;
        double time = vector / 10;//speed as a function of acceleration, gravity, documentation
        double vertMove = vector * time;//first half of function
        double vertAcc = Math.pow(time, 2);//second half of function
        vertAcc *= 5;//half acceleration
        double maxHeight = vertMove - vertAcc;//height as function of speed and acceleration, docs
        float location = this.getYPercentage();//current vertical position
        float fHeight = (float) maxHeight;
        float maxPixels = fHeight / GameView.pixelHeight;//convert meters to pixels
        float finalPixel = location - maxPixels;//in canvas up is negative
        return (finalPixel <= 0);//is it above or at the ceiling
    }

    private boolean faster(double speed, double angle1, double time1, double angle2, double time2)
    {
        boolean ceiling1 = ceiling(speed, angle1);
        boolean ceiling2 = ceiling(speed, angle2);
        double trueAngle = 0;
        if(ceiling1 && ceiling2)
            return false;
        if(ceiling1 || angle1 == angle2)
            trueAngle = angle2;
        else if(ceiling2)
            trueAngle = angle1;
        else
            trueAngle = (time1 < time2) ? angle1 : angle2;
        this.directionAngle = Math.toDegrees(trueAngle);
        double horAngle = Math.cos(trueAngle);
        horizontalDirection = (float) horAngle;
        double vertAngle = Math.sin(trueAngle);
        verticalDirection = (float) vertAngle;
        verticalDirection *= (-1);//up is negative
        return true;
    }

    private double[] arcTan(double value, double xDist, double speed)
    {
        double[] angledTime = new double[2];
        double angle = Math.atan(value);
        if(xDist < 0)//if enemy is on the left
        {
            double deg = Math.toDegrees(angle);
            deg += 180;//explained in docs
            angle = Math.toRadians(deg);
        }
        angledTime[0] = angle;
        double vector = Math.cos(angle);
        vector *= speed;
        double time = xDist / vector;
        angledTime[1] = time;
        return angledTime;
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void shock()
    {
        if(shocked)
            this.toShock++;
        else
        {
            this.shocked = true;

            long now = System.currentTimeMillis();
            resetX = now + 5000L;

            class UnShock extends TimerTask {
                private Character character;

                UnShock(Character c)
                {
                    this.character = c;
                }

                @Override
                public void run() {
                    character.unShock();
                }
            }
            Timer timer = new Timer();
            TimerTask task = new UnShock(this);
            timer.schedule(task, 5000L);
        }
    }

    public void unShock()
    {
        this.shocked = false;
        if(toShock > 0)
        {
            toShock--;
            shock();
        }
    }

    public void backedIntoWall(float x, float y, float width, float height, float otherX)
    {
        boolean jump = false;
        if (otherX < x)
        {
            if ((x + (width * 2)) >= GameView.width)
            {
                horizontalMovement = -1;
                jump = true;
            }
        }
        else
        {
            if ((x - width) <= 0)
            {
                horizontalMovement = 1;
                jump = true;
            }
        }
        if ((y + height == GameView.canvasPixelHeight) && jump)
            this.verticalMovement = (-1) * this.movementSpeed;
    }

    public void magicLine(String effect, Bitmap lineSprite)//sprite is temporary
    {
        float locationX = this.getXPercentage();
        float locationY = this.getYPercentage();
        float xDiffrential = this.getWidthPercentage() * this.horizontalDirection * 5;
        float yDiffrential = this.getHeightPercentage() * this.verticalDirection / 5;
        if((Math.abs(verticalDirection)<Math.abs(horizontalDirection))^(yDiffrential<xDiffrential))
        {
            xDiffrential /= 25;
            yDiffrential *= 25;
        }

        if(effect == "laser")
        {
            if (xDiffrential > yDiffrential)
            {
                xDiffrential *= 2;
                yDiffrential *= 5;
            }
            else
            {
                xDiffrential *= 5;
                yDiffrential *= 2;
            }
        }

        if(this.horizontalDirection>0)
            locationX += this.getWidthPercentage();
        else
            locationX += xDiffrential;
        if(this.verticalDirection>0)
            locationY += this.getHeightPercentage();
        else
            locationY += yDiffrential;

        LightLine lightLine = new LightLine(lineSprite, roomID, this, attackPower, locationX, locationY, xDiffrential, yDiffrential, effect, this.directionAngle);
        this.projectiles.add(lightLine);
    }

    public void setUpMovement(float x, float y)
    {
        //stand in for controller input
    }

    public void setUpDirection(float x, float y)
    {
        //stand in for controller input
    }

    public String ailment()
    {
        int endNum = (this instanceof Berserker)? 3 : 4;
        int ailmentNum = getRandomNumber(1, endNum);
        String ailment = "none";
        switch (ailmentNum)
        {
            case 1:
                ailment = "freeze";
                break;
            case 2:
                ailment = "poison";
                break;
            case 3:
                ailment = "shock";
                break;
            case 4:
                ailment = "fire";
        }
        return ailment;
    }

    public void shatter()
    {
        this.shatter = true;
        class UnShatter extends TimerTask {
            private Character character;

            UnShatter(Character c)
            {
                this.character = c;
            }

            @Override
            public void run() {
                character.unShatter();
            }
        }
        Timer timer = new Timer();
        TimerTask task = new UnShatter(this);
        timer.schedule(task, 5000L);
    }

    public void unShatter()
    {
        this.shatter = false;
    }

    public double getDirectionAngle()
    {
        return this.directionAngle;
    }

    public void parryChallenge(int parryStage)
    {
        if(parryCountdown <= System.currentTimeMillis())
        {
            isParrying = true;
            this.parryCountdown = System.currentTimeMillis() + 1000L;
            long scheduleDelay = 500L;
            long parryWindow = 20L * parryStage;
            scheduleDelay -= parryWindow;
            class Unparry extends TimerTask {
                private Character character;

                Unparry(Character c)
                {
                    this.character = c;
                }

                @Override
                public void run() {
                    character.unparry();
                }
            }
            Timer timer = new Timer();
            TimerTask task = new Unparry(this);
            timer.schedule(task, scheduleDelay);
        }
    }
    public void unparry()
    {
        this.isParrying = false;
    }
    public boolean IsParrying()
    {
        return this.isParrying;
    }

    public boolean isAlive()
    {
        return (this.HP > 0);
    }
    @Override
    public void run() {

    }
}

package com.example.firebasetest;

import android.graphics.Bitmap;

import org.checkerframework.checker.units.qual.K;

import java.util.*;

public class Character extends GameObject implements Runnable {
    private static Character player;
    protected double HP;
    protected long attackCooldown;
    protected double attackPower;
    protected String spriteState;//draw in gameobject draws the character, this class has override that adds the itemsprite on top with direction.
    protected float itemWidth;
    protected float itemHeight;
    protected ArrayList<Projectile> projectiles;
    private boolean listSent;
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
    protected boolean running;
    protected boolean threadStart;
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
    protected boolean performingAction;
    protected String itemSprite;
    protected String secondItemSprite;
    private int[] statusNum;//[poison, freeze, shock, shatter, parry, buff, shieldFire, heal]
    public Character(int level, int HPD, int ACD, int APD, String spriteName, int ID, float xLocation, float yLocation, int characterGrade) //HPD, ACD, APD=2/3/5
    {
        super(spriteName, ID, xLocation, yLocation);
        this.projectiles = new ArrayList<Projectile>();
        this.attackCooldown = 1150L;
        this.moving = false;
        this.listSent = false;
        this.threadStart = true;
        this.legsPos = 1;
        float myWidth = this.getWidth();
        itemWidth = myWidth;
        itemHeight = myWidth;
        movementSpeed = myWidth / 10f;//speed is screen within 5 seconds. this is the speed for every frame (1/30 of a second)
        HP = HPD * 15d * level;
        attackCooldown -= (level *ACD * 10);
        attackPower = (double) level*APD;
        this.resetX = 0;
        this.resetB = 0;
        this.resetA = System.currentTimeMillis() + 5000L;
        this.resetY = System.currentTimeMillis() + 30000L;
        this.characterGrade = characterGrade;
        this.shocked = false;
        this.toShock = 0;
        this.moveBack = true;
        this.distanceVector = 0d;
        this.shatter = false;
        this.parryCountdown = System.currentTimeMillis() + 500L;
        this.isParrying = false;
        this.spriteState = "idle";
        this.statusNum = new int[8];
        this.itemSprite = "none";
        this.secondItemSprite = "none";
        for(int i = 0; i < 5; i++)
            statusNum[i] = 0;
        this.performingAction = false;
        running = true;
        if (characterGrade == 5)
            threadStart = false;
        thread = new Thread(this);
    }

    public int getCharacterGrade() {
        return characterGrade;
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
            listSent = false;
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

    public boolean useAbility(String button)
    {
        long resetTime = 0L;
        if(button.equals("A"))
            resetTime = resetA;
        else if(button.equals("B"))
            resetTime = resetB;
        else if(button.equals("X"))
            resetTime = resetX;
        else if(button.equals("Y"))
            resetTime = resetY;
        return System.currentTimeMillis() >= resetTime;
    }

    public void resetAbility(String button)
    {
        long start = System.currentTimeMillis();
        if(button.equals("A"))
            resetA = start + 5000L;
        else if(button.equals("B"))
            resetB = start + 10000L;
        else if(button.equals("X"))
            resetX = start + attackCooldown;
        else if(button.equals("Y"))
            resetY = start + 30000L;
    }
    public boolean hit(Projectile p)
    {
        if(p.canHit(this))
        {
            double power = p.getPower();
            this.HP -= power;
            String ailment = p.getAilment();

            if(ailment.equals("poison"))
                poisoned(power / 10d);
            else if(ailment.equals("freeze"))
                freeze();
            else if(ailment.equals("shock"))
                shock();
            else if(ailment.equals("life steal"))
                p.getCreator().HP += power;
            else if(ailment.equals("shatter"))
                shatter();
            return true;//hit
        }
        else
            return false;
    }

    private void poisoned(double power)
    {
        addStatus(0);
        class Poison extends TimerTask {
            private Character chr;
            private int repeats;
            private double power;

            Poison(Character c, int r, double p)
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
                else
                    this.chr.removeStatus(0);
            }
        }
        Timer timer = new Timer();
        TimerTask task = new Poison(this, 10, power);
        timer.schedule(task, 1000L);
    }
    public boolean poison(double power)
    {
        this.HP -= power;
        if(this.HP <= 0d)
        {
            this.running = false;
            return false;
        }
        return true;
    }

    public void freeze()
    {
        this.movementSpeed /= 2f;
        addStatus(1);
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
        timer.schedule(task, 2500L);
    }

    public void unFreeze()
    {
        this.movementSpeed *= 2f;
        removeStatus(1);
    }

    public void Attack()
    {
        this.spriteState = "attacking";
        this.performingAction = true;
        float xAxis = horizontalDirection * itemWidth;
        float yAxis = verticalDirection *  itemHeight;
        float locationHor = this.getXLocation() + xAxis;
        float locationVert = this.getYLocation() + yAxis;
        if (this.horizontalDirection > 0f)
            locationHor += (this.getWidth() / 2f);
        else
            locationHor -= (this.getWidth() / 2f);
        if (this.verticalDirection > 0f)
            locationVert += (this.getHeight() / 2f);
        else
            locationVert -= (this.getHeight() / 2f);

        String spriteName = "slash";
        String ailment = "none";
        double power = attackPower;

        if(this instanceof Sage)
        {
            ailment = "life steal";
            spriteName = "miasma";
        }
        else if(this instanceof Archer)
        {
            power /= 2d;
            spriteName = "stab";
        }
        else if(this instanceof Knight)
            spriteName = ((Knight) this).getItemName();
        else if(this instanceof Berserker)
            spriteName = "fistHit";

        BladeAttack bladeAttack = new BladeAttack(spriteName, roomID, this, power, locationHor, locationVert, itemWidth, itemHeight, ailment, directionAngle);
        this.projectiles.add(bladeAttack);
        idleAgain(spriteState);
    }
    public static void setPlayer(Character p)
    {
        Character.player = p;
    }
    protected static float getPlayerX()
    {
        return Character.player.getXLocation();
    }
    protected static float getPlayerY()
    {
        return Character.player.getYLocation();
    }
    protected static float getPlayerWidth()
    {
        return Character.player.getWidth();
    }
    protected static float getPlayerHeight()
    {
        return Character.player.getHeight();
    }

    protected void move()
    {
        if(!shatter)
        {
            float x = this.getXLocation();
            float y = this.getYLocation();
            float tWidth = this.getWidth();
            float tHeight = this.getHeight();
            float accelerationNum = GameView.pixelHeight / 100f;//transition from centimeters to meters
            accelerationNum *= 30f;//transition from frames to seconds
            float accelerationDiff = 10f / 30f;//acceleration in one frame
            float speedToZero = accelerationDiff / accelerationNum;//speed that after acceleration becomes 0
            speedToZero *= (float) (-1);//it has to be negative

            if(moving)
            {
                float xLocation = x + (movementSpeed * horizontalMovement);
                if((xLocation - (tWidth / 2f)) < 0f)
                {
                    xLocation = (tWidth / 2f);
                    moving = false;
                    legsPos = 1;
                }
                else if ((xLocation + (tWidth / 2f)) > GameView.width)
                {
                    xLocation = GameView.width - (tWidth / 2f);
                    moving = false;
                    legsPos = 1;
                }
                else
                {
                    legsPos++;
                    if (legsPos > 4)
                        legsPos = 1;
                }
                setXLocation(xLocation);
            }
            else
            {
                legsPos = 1;
            }

            float yLocation = y + verticalMovement;
            if ((yLocation + (tHeight / 2f)) >= GameView.height)
            {
                yLocation = GameView.height - (tHeight / 2f);
                verticalMovement = speedToZero;
            }
            else
            {
                legsPos = 5;
                if((yLocation - (tHeight / 2f)) < 0) {
                    yLocation = (tHeight / 2f);
                    verticalMovement = speedToZero;
                }
            }
            setYLocation(yLocation);
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
        double radius = Math.sqrt(Math.pow((double) horizontalDirection, 2d) + Math.pow((double) verticalDirection, 2d));//is rad 1
        this.distanceVector = radius;
        if (radius <= 1d)
            return true;

        double newVert = (double) verticalDirection * (-1d);
        double angle = Math.atan2(newVert, (double) horizontalDirection);//converts as if radius is 1
        this.directionAngle = Math.toDegrees(angle);
        horizontalDirection = (float) Math.cos(angle);
        verticalDirection = (float) Math.sin(angle) * (-1f);//complex numbers are in the gauss plane.
        //the results work for positive right and up. in here, positive is right and down
        return false;
    }

    public float[] aimAtPlayer()
    {
        float[] values = new float[10];
        float playerX = getPlayerX();
        float playerY = getPlayerY();
        float width = this.getWidth();
        float height = this.getHeight();
        float playerWidth = getPlayerWidth();
        float playerHeight = getPlayerHeight();
        float xLocation = this.getXLocation();
        float yLocation = this.getYLocation();

        this.horizontalDirection = (playerX != xLocation)? (playerX - xLocation): 0f;
        this.verticalDirection = (playerY != yLocation)? (playerY - yLocation): 0f;

        float horizontalDistance = horizontalDirection;
        float verticalDistance = verticalDirection;

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
        double speed = Math.pow(pSpeed, 2d);//speed by meters per second, squared according to docs
        double a = 5d * xDist;//a in quadratic formula
        a /= speed;
        double ratio = yDist / xDist;//in docs
        double c = a + ratio;//c in quadratic formula
        double disc = a * c;//discriminanta (value below the root)
        disc *= 4d;//in quadratic formula (b^2-4ac)
        double toRoot = 1d - disc;//in documentation, b = -1
        double bottom = 2d * a;//divider in quadratic formula
        if(toRoot < 0)//no value under the root, no solution=too far
            return false;
        else if(toRoot == 0d)
        {
            double[] angledTime = arcTan((1d/bottom), xDist, pSpeed);
            return faster(pSpeed, angledTime[0], angledTime[1], angledTime[0], angledTime[1]);
        }
        double rooted = Math.sqrt(toRoot);
        double f = 1d + rooted;//quadratic formula has 2 solutions (+-)
        f /= bottom;//division in quadratic formula
        double g = 1d - rooted;
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
        double time = vector / 10d;//speed as a function of acceleration, gravity, documentation
        double vertMove = vector * time;//first half of function
        double vertAcc = Math.pow(time, 2d);//second half of function
        vertAcc *= 5d;//half acceleration
        double maxHeight = vertMove - vertAcc;//height as function of speed and acceleration, docs
        float location = this.getYLocation();//current vertical position
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
        if(ceiling1 || (angle1 == angle2))
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
        verticalDirection *= (-1f);//up is negative
        return true;
    }

    private double[] arcTan(double value, double xDist, double speed)
    {
        double[] angledTime = new double[2];
        double angle = Math.atan(value);
        if(xDist < 0)//if enemy is on the left
        {
            double deg = Math.toDegrees(angle);
            deg += 180d;//explained in docs
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
            addStatus(2);

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
        removeStatus(2);
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
            if ((x + (width * 1.5f)) >= GameView.width)
            {
                horizontalMovement = -1f;
                jump = true;
            }
        }
        else
        {
            if ((x - (width * 1.5f)) <= 0f)
            {
                horizontalMovement = 1f;
                jump = true;
            }
        }
        if ((y + (height / 2f) == GameView.height) && jump)
            this.verticalMovement = (-1f) * this.movementSpeed;
    }

    public void magicLine(String effect)
    {
        float locationX = this.getXLocation();
        float locationY = this.getYLocation();
        float xDiffrential = this.getWidth() * this.horizontalDirection * 5f;
        float yDiffrential = this.getHeight() * this.verticalDirection;

        if(effect.equals("laser"))
        {
            xDiffrential *= 2f;
            yDiffrential *= 2.5f;
        }

        locationX += (xDiffrential / 2f);
        locationY += (yDiffrential / 2f);

        LightLine lightLine = new LightLine(roomID, this, attackPower, locationX, locationY, xDiffrential, yDiffrential, effect, this.directionAngle);
        this.projectiles.add(lightLine);
        spriteState = (effect.equals("laser"))? "lasering": "waving";
        performingAction = true;
        idleAgain(spriteState);
    }

    public void setUpMovement(float x, float y)
    {
        this.horizontalMovement = x;
        this.moving = (x != 0f);
        if(((this.getYLocation() + (this.getHeight() / 2f)) == GameView.height) && (y <= 0f))
            this.verticalMovement = this.movementSpeed * y;
    }

    public void toMove()
    {
        if((horizontalMovement != 0f) || ((verticalMovement != 0f) || ((this.getYLocation() + (this.getHeight() / 2f)) != GameView.height)))
            move();
    }

    public void setUpDirection(float x, float y)
    {
        this.horizontalDirection = x;
        this.verticalDirection = y;
        double angle = Math.atan2(y, x);
        this.directionAngle = Math.toDegrees(angle);
    }

    public String ailment()
    {
        int endNum = (this instanceof Berserker)? 4 : 3;
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
        legsPos = 1;
        addStatus(3);
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
        removeStatus(3);
    }

    public void parryChallenge(int parryStage)
    {
        if(parryCountdown <= System.currentTimeMillis())
        {
            isParrying = true;
            addStatus(4);
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
        removeStatus(4);
    }
    public boolean IsParrying()
    {
        return this.isParrying;
    }

    public boolean isAlive()
    {
        return (this.HP > 0);
    }

    public String getLegs()
    {
        String legs = "walking";
        legs += legsPos;
        return legs;
    }

    @Override
    public String getSpriteName() {
        String name = super.getSpriteName();
        String sprite = name + this.getLegs();
        return sprite;
    }

    public boolean[] hasItems()
    {
        boolean[] itemCheck = new boolean[2];
        itemCheck[0] = ((!(this.itemSprite.equals("none"))) && ((horizontalDirection != 0f) || (verticalDirection != 0f)));
        itemCheck[1] = (!(this.secondItemSprite.equals("none")));
        return itemCheck;
    }

    public Projectile getItem(int index)
    {
        String name = secondItemSprite;
        float x = this.getXLocation();
        float y = this.getYLocation();
        float width = this.getWidth();
        float height = this.getHeight();
        double radius = Math.sqrt(Math.pow((width / 2d), 2d) + Math.pow((height / 2d), 2d));

        if(!performingAction)
            radius /= 1.5d;
        width /= 2f;
        height /= 4f;
        float addY = 0;
        float usedRadius = (float) radius;
        float addX = usedRadius;
        double itemDirection = facingAngle();
        itemDirection = 180d - itemDirection;
        float cosAxis = (float) Math.cos(Math.toRadians(itemDirection));
        if(index == 0)
        {
            name = itemSprite;
            cosAxis *= (-1f);
            itemDirection = this.directionAngle;
            double angle = Math.toRadians(itemDirection);
            float axisX = (float) Math.cos(angle);
            addX *= axisX;
            float axisY = (float) Math.sin(angle);
            axisY *= (-1f);
            addY = axisY * usedRadius;
        }
        addX *= cosAxis;
        x += addX;
        y += addY;
        Projectile item = new Projectile(name, roomID, this, 0, 0, 0, x, y, width, height, itemDirection, "none");
        return item;
    }

    public double facingAngle()
    {
        double face = (this.horizontalDirection < 0f)? 180d: 0d;
        return face;
    }

    public void idleAgain(String originalState)
    {
        class ReIdle extends TimerTask {
            private Character chr;
            private String state;

            ReIdle(Character c, String s)
            {
                this.chr = c;
                this.state = s;
            }

            @Override
            public void run() {
                chr.reIdle(state);
            }
        }
        Timer timer = new Timer();
        TimerTask task = new ReIdle(this, originalState);
        long scheduleTime = (originalState.equals("lasering"))? 5000L: 333L;
        timer.schedule(task, scheduleTime);
    }

    public void addStatus(int index)
    {
        statusNum[index]++;
    }
    public void removeStatus(int index)
    {
        statusNum[index]--;
    }

    public boolean isStatus(int index)
    {
        return (statusNum[index] > 0);
    }

    public GameObject getStatus(int index)
    {
        String statusEffect = "";
        switch (index)
        {
            case 0:
                statusEffect = "poison";
                break;
            case 1:
                statusEffect = "freeze";
                break;
            case 2:
                statusEffect = "shock";
                break;
            case 3:
                statusEffect = "shatter";
                break;
            case 4:
                statusEffect = "parry";
                break;
            case 5:
                statusEffect = "buff";
                break;
            case 6:
                statusEffect = "shieldFire";
                break;
            case 7:
                statusEffect = "heal";
                break;
        }
        GameObject statusBubble = new GameObject(statusEffect, roomID, this.getXLocation(), this.getYLocation(), this.getWidth(), this.getHeight());
        return statusBubble;
    }

    public void reIdle(String originalState)
    {
        String fauxShield = (originalState.equals("releaseShield"))? "shielding": originalState;
        if(spriteState.equals(fauxShield))
        {
            performingAction = false;
            if(!(originalState.equals("shielding")))
                spriteState = "idle";
        }
    }

    @Override
    public void run() {

    }
}
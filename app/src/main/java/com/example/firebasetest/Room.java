package com.example.firebasetest;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Room implements Runnable {//fill this logic
    /*an object of this class is sent 3 parameters in its constructor/has access to them in User:
        1.current section
        2.current level
        3.current room number
      It uses them in an equation to determine how many enemies and what level they are/level of
      challenge
      It also checks with User/gameActivity what section it is.


    */
    private int sectionNum;
    private int floorNum;
    private int roomNum;
    private String roomClass;
    private Thread roomThread;
    private ArrayList<Character> characters;
    private ArrayList<Projectile> projectiles;
    private ArrayList<GameObject> objects;
    private int misses;
    private int enemiesPerWave;
    private int currentWave;
    private int ID;
    private int blocksLeft;
    private int enemyDifficulty;
    private int difficultyScaling;
    private int challengeDifficulty;
    private int challengeDifficultyScaling;
    private int[][] missesArray = {{10, 7, 5}, {7, 5, 4}, {6, 4, 3}, {5, 3, 2}, {3, 2, 1}};//missesArray[scaling][section]
    private int length;

    public Room(User user)
    {
        sectionNum = user.getCurrentSection();
        roomClass = user.getOrder().get(sectionNum - 1);
        floorNum = user.getCurrentFloor();
        roomNum = user.getCurrentRoom();
        this.misses = 0;
        this.length = 0;
        ID = (sectionNum * 100) + (floorNum * 10) + roomNum;
        characters = new ArrayList<>();
        projectiles = new ArrayList<>();
        objects = new ArrayList<>();

        enemyDifficulty = user.getEnemyDifficulty();
        difficultyScaling = user.getDifficultyScaling();
        challengeDifficulty = user.getChallengeDifficulty();
        challengeDifficultyScaling = user.getChallengeDifficultyScaling();

        roomThread = new Thread(this);
        roomThread.start();
    }

    public ArrayList<GameObject> getObjects()
    {
        ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
        gameObjects.addAll(objects);
        gameObjects.addAll(characters);
        gameObjects.addAll(projectiles);
        return gameObjects;
    }

    @Override
    public void run() {
        if(roomNum == 2)
        {
            this.misses = this.missesArray[challengeDifficultyScaling - 1][sectionNum - 1];
            this.length = challengeDifficulty * floorNum;
            if(roomClass.equals("Knight"))
                knightChallenge();
            else if (roomClass.equals("Archer"))
                archerChallenge();
            else
                mageChallenge();
        }
    }

    public void enemyRoom()//1, 3, 4
    {

    }

    public void knightChallenge()
    {
        Character player = new Character(1, 3, 3, 3, "Character", ID, GameView.width * (1/4), GameView.canvasPixelHeight - (GameView.width / 15), 5);
        Character.setPlayer(player);
        Archer a = new Archer(1, 5, ID, GameView.width * (3/4), GameView.canvasPixelHeight - (GameView.width / 15));
        characters.add(player);
        blocksLeft = challengeDifficulty * floorNum;
        long timeToHit = 0;
        a.shoot();
        while (misses > 0 && length > 0)
        {
            ArrayList<Projectile> currentProjectiles = null;
            currentProjectiles = a.getProjectileList(this.ID, characters);//wrong. archer isn't in that list
            projectiles.addAll(currentProjectiles);
            for (Projectile p:
                 projectiles) {
                boolean[] hit = hit(player, p);
                if (hit[0])
                {
                    projectiles.remove(p);
                    if(hit[1])
                        misses--;
                    else
                        length--;
                }
            }
            if(timeToHit < System.currentTimeMillis())
            {
                timeToHit = System.currentTimeMillis() + 2000L;
                rePositionArcher(a, player);
            }
            try {
                roomThread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void archerChallenge()
    {

    }

    public void mageChallenge()
    {

    }

    public boolean[] hit(Character c, Projectile p)
    {
        boolean[] hitting = new boolean[2];
        //first one is collision, second one is actual hit
        if(hitting[0])
        {
            if(floorNum == 2)
                hitting[1] = c.IsParrying();
        }
        return hitting;
    }

    public void rePositionArcher(Archer a, Character character)
    {
        boolean above = (getRandomNumber(1, 2) == 1);
        boolean right = (getRandomNumber(1, 2) == 1);
        float newY = above? (character.getYPercentage() - (a.getHeightPercentage() * 2)): character.getYPercentage();
        float newX = right? (character.getXPercentage() + character.getWidthPercentage() + (a.getWidthPercentage() * 2)): (character.getXPercentage() - (a.getWidthPercentage() * 2));
        a.setXPercentage(newX);
        a.setYPercentage(newY);
        float[] values = a.aimAtPlayer();
        float horizontalDistance = values[8];
        float verticalDistance = values[9];
        float speed = a.getPhysicalSpeed();
        a.inRange();
        a.aim(horizontalDistance, verticalDistance, speed);
        a.shoot();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void moveProjectile(Projectile projectile)
    {
        if(projectile.isMoving())
        {
            //homing guide:
            //if projectile instance of arrow and (Arrow) projectile needsToHome.
            //search for characters. if projectile's creator's character Grade is 5, use the first enemy in the list.
            //if grade isn't 5, use the player in character.
            //if it is homing and has a target, use the aiming methods you have
            float x = projectile.getXPercentage();
            float y = projectile.getYPercentage();
            float horizontalSpeed = projectile.getHorizontalSpeed();
            float verticalSpeed = projectile.getVerticalSpeed();
            x += horizontalSpeed;
            y += verticalSpeed;
            //now vertical acceleration
            if(!(projectile instanceof Fist) && !(projectile instanceof Pebble))
            {
                float accelerationNum = GameView.canvasPixelHeight / 100;//transition from centimeters to meters
                accelerationNum *= 30;//transition from frames to seconds
                float accelerationDiff = 10 / 30;//acceleration in one frame
                verticalSpeed *= accelerationNum;//transition from pixels per frame to meters per second
                verticalSpeed += accelerationDiff;//down is positive, up is negative
                verticalSpeed /= accelerationNum;//transition from meters per second to pixels per frame
            }
            if(x < 0 || x >= GameView.width)
                hitWall(projectile, true);
            if(y < 0 || y >= GameView.canvasPixelHeight)
                hitWall(projectile, false);
            //also need homing for arrow
            //angle change
        }

    }

    public void hitWall(Projectile projectile, boolean horizontalWall)
    {
        if(projectile.getAilment().equals("fire"))
        {
            GroundFire gF = new GroundFire(projectile);
            projectiles.add(gF);
        }
        else if((projectile instanceof Arrow) && (((Arrow) projectile).isRicochet()))
        {
            if(horizontalWall)
                projectile.setHorizontalSpeed(projectile.getHorizontalSpeed() * (-1));
            else
                projectile.setVerticalSpeed(projectile.getVerticalSpeed() * (-1));
        }
        else
            projectiles.remove(projectile);
    }


    //also get difficulty in some manner. no need to save it. calculate
    //different challenge rooms. what do?
}

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
    private int enemyDifficulty;
    private int difficultyScaling;
    private int challengeDifficulty;
    private int challengeDifficultyScaling;
    private int parryWindow;//send it when pressed (knight challenge)
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
        //only 2 buttons work: direction joystick and parry button
        Character player = new Character(1, 3, 3, 3, "Character", ID, GameView.width * (1/4), GameView.canvasPixelHeight - (GameView.width / 15), 5);
        Character.setPlayer(player);
        Archer a = new Archer(1, 5, ID, GameView.width * (3/4), GameView.canvasPixelHeight - (GameView.width / 15));
        characters.add(player);
        parryWindow = 0;
        long timeToHit = 0;
        a.shoot();
        while (misses > 0 && length > 0)
        {
            ArrayList<Projectile> currentProjectiles = null;
            currentProjectiles = a.getProjectileList(this.ID, characters);
            projectiles.addAll(currentProjectiles);
            for (Projectile p:
                 projectiles) {
                moveProjectile(p);
                boolean[] hit = hit(player, p);
                if (hit[0])
                {
                    projectiles.remove(p);
                    if(hit[1])
                        misses--;
                    else
                    {
                        length--;
                        parryWindow++;
                    }
                }
            }
            if(timeToHit < System.currentTimeMillis())
            {
                timeToHit = System.currentTimeMillis() + 2000L;
                rePositionArcher(a);
            }
            try {
                roomThread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(misses <= 0)
            failure();
        else
            roomEnd();
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

    public void rePositionArcher(Archer a)
    {
        boolean tooClose = true;
        boolean tooFar = true;
        while (tooClose || tooFar)
        {
            float horizontalEdge = GameView.width - a.getWidthPercentage();
            float verticalEdge = GameView.canvasPixelHeight - a.getHeightPercentage();
            float newY = getRandomNumber(0, (int)horizontalEdge);
            float newX = getRandomNumber(0, (int)verticalEdge);
            a.setXPercentage(newX);
            a.setYPercentage(newY);
            float[] values = a.aimAtPlayer();
            float horizontalDistance = values[8];
            float verticalDistance = values[9];
            float speed = a.getPhysicalSpeed();
            tooClose = a.inRange();
            tooFar = !a.aim(horizontalDistance, verticalDistance, speed);
        }
        a.shoot();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void moveProjectile(Projectile projectile)
    {
        if(projectile.isMoving())
        {
            boolean homed = false;
            if(projectile instanceof Arrow)
            {
                Arrow arrowProjectile = (Arrow) projectile;
                if(arrowProjectile.isHoming())
                {
                    if(arrowProjectile.needsToHome())
                    {
                        arrowProjectile.home(characters);
                    }
                    else
                        homed = arrowProjectile.aimAtTarget();//so we could wait
                }
            }

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
                projectile.setVerticalSpeed(verticalSpeed);
            }
            String wallResult = "noHit";
            if(x < 0 || x >= GameView.width)
                wallResult = hitWall(projectile, true);
            if(y < 0 || y >= GameView.canvasPixelHeight)
                wallResult = hitWall(projectile, false);

            if(wallResult.equals("changed"))
            {
                horizontalSpeed = projectile.getHorizontalSpeed();
                verticalSpeed = projectile.getVerticalSpeed();
            }

            if(!(wallResult.equals("removed")))
            {
                double newVert = (double) verticalSpeed * (-1);
                double angle = Math.atan2(newVert, (double) horizontalSpeed);//converts as if radius is 1
                double degreesAngle = Math.toDegrees(angle);
                projectile.setAngle(degreesAngle);
            }
        }
        else if(projectile.isTimeUp())
            projectiles.remove(projectile);
    }

    public String hitWall(Projectile projectile, boolean horizontalWall)//result code for actions
    {
        String resultCode = "changed";
        if((projectile instanceof Arrow) && (((Arrow) projectile).isRicochet()))
        {
            if(horizontalWall)
                projectile.setHorizontalSpeed(projectile.getHorizontalSpeed() * (-1));
            else
                projectile.setVerticalSpeed(projectile.getVerticalSpeed() * (-1));
        }
        else
        {
            resultCode = "removed";
            if(projectile.getAilment().equals("fire"))
            {
                GroundFire gF = new GroundFire(projectile);
                projectiles.add(gF);
            }
            projectiles.remove(projectile);
        }
        return resultCode;
    }

    public void roomEnd()
    {
        //reset none moving buttons to nothing
        //open start button
        //check for movement, once reached edge, use gameview's next room function
    }

    public void failure()
    {
        //show game over screen
        //allow two button presses(close/continue?)
        //check sudden death if you need to change the room you're putting the player in.
        //find a way to use next room with the same room
    }


    //also get difficulty in some manner. no need to save it. calculate
    //different challenge rooms. what do?
}

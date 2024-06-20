package com.example.firebasetest;

import android.graphics.Path;

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
    private boolean nextSimon;
    private int nextSimonNum;
    private ArrayList<Integer> boxPresses;
    private ArrayList<Integer> buttonPresses;
    private ArrayList<Box> boxes;
    private int currentSimonLength;

    public Room(User user)
    {
        sectionNum = user.getCurrentSection();
        roomClass = user.getOrder().get(sectionNum - 1);
        floorNum = user.getCurrentFloor();
        roomNum = user.getCurrentRoom();
        this.misses = 0;
        this.length = 0;
        this.nextSimon = false;
        this.nextSimonNum = 0;
        boxPresses = null;
        buttonPresses = null;
        boxes = null;
        currentSimonLength = 0;
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
        Character player = new Character(1, 3, 3, 3, "character", ID, GameView.width * (1/4), GameView.height - (GameView.width / 30), 5);
        Character.setPlayer(player);
        Archer a = new Archer(1, 5, ID, GameView.width * (3/4), GameView.height - (GameView.width / 30));
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
        Archer player = new Archer(1, 5, ID, GameView.width / 2, GameView.height - (GameView.width / 30));
        Character.setPlayer(player);
        characters.add(player);
        ArrayList<Projectile> keptProjectiles = new ArrayList<>();
        ArrayList<Projectile> temporaryHolder = new ArrayList<>();
        int left = ((int) (GameView.width / 20));
        int right = ((int) ((GameView.width / 20) * 19));
        int bottom = ((int) (GameView.height)) - left;
        int top = ((int) (GameView.height / 2));
        Target target = new Target(ID, GameView.width / 2, GameView.height * (19/20), length,0, 0);
        boolean check = initializeTarget(target, left, right, top, bottom);
        objects.add(target);
        while (length > 0 && misses > 0)
        {
            temporaryHolder.addAll(player.getProjectileList(ID, characters));
            projectiles.addAll(temporaryHolder);
            keptProjectiles.addAll(temporaryHolder);
            temporaryHolder.clear();
            target.moveTarget();
            for (Projectile p:
                 projectiles) {
                moveProjectile(p);
            }
            for (Projectile p:
                 keptProjectiles) {
                if(collision(target, p) && (misses > 0))
                {
                    length--;
                    check = initializeTarget(target, left, right, top, bottom);
                    projectiles.remove(p);
                    keptProjectiles.remove(p);
                }
                else if((!(projectiles.contains(p))) && (check))
                {
                    misses--;
                    if(misses > 0)
                        check = initializeTarget(target, left, right, bottom, top);
                    keptProjectiles.remove(p);
                }
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

    public boolean initializeTarget(Target target, int left, int right, int top, int bottom)
    {
        boolean done = false;
        while (!done)
        {
            float x = ((float) getRandomNumber(left, right));
            float y = ((float) getRandomNumber(top, bottom));
            target.setXLocation(x);
            target.setYLocation(y);
            done = !(target.incorrectRange());
        }
        target.setSpeedPerFrame(length);
        if(floorNum != 2)
            target.setHorizontalDirection();
        if(floorNum != 1)
            target.setVerticalDirection();
        return (length != 0);
    }

    public void mageChallenge()
    {
        Character player = new Character(1, 3, 3, 3, "character", ID, GameView.width * (1/4), GameView.height - (GameView.width / 30), 5);
        Character.setPlayer(player);
        characters.add(player);
        boxPresses = new ArrayList<>();
        buttonPresses = new ArrayList<>();
        boxes = new ArrayList<>();
        float boxDistance = GameView.width / 5;
        for(int i = 1; i < 5; i++)
        {
            float width = GameView.width / 10;
            float x = boxDistance * i;
            float y = GameView.height - (width / 2);
            Box box = new Box(ID, x, y, width, width, i);
            boxes.add(box);
        }
        objects.addAll(boxes);
        currentSimonLength = 4;
        for(int i = 0; i < 3; i++)
        {
            int boxPressed = getRandomNumber(1, 4);
            int buttonPressed = getRandomNumber(1, 4);
            boxPresses.add(boxPressed);
            buttonPresses.add(buttonPressed);
        }
        nextSimon = true;
        while (misses > 0 && length > 0)
        {
            if(nextSimon)
            {
                nextSimonCycle();
                currentSimonLength++;
                nextSimon = false;
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

    public boolean[] hit(Character c, Projectile p)
    {
        boolean[] hitting = new boolean[2];
        hitting[0] = false;
        hitting[1] = false;
        Character creator = p.getCreator();
        boolean grade1 = (creator.getCharacterGrade() == 5);
        boolean grade2 = (c.getCharacterGrade() == 5);
        boolean knightChallenge = (roomNum == 2 && roomClass.equals("Knight"));
        boolean ricochet = false;
        if(p instanceof Arrow)
            ricochet = ((Arrow) p).isRicochet();
        if((grade1 ^ grade2) || (knightChallenge || ricochet))
        {
            hitting[0] = collision(c, p);
        }
        //first one is collision, second one is actual hit
        if(hitting[0])
        {
            if(roomNum == 2 && roomClass.equals("Knight"))
                hitting[1] = c.IsParrying();
            else
            {
                if(c.hit(p))
                    p.hasHit(c);
                if(p.isTimeUp())
                    projectiles.remove(p);//here? maybe use the array
                hitting[1] = true;
            }
        }
        return hitting;
    }

    public void rePositionArcher(Archer a)
    {
        boolean tooClose = true;
        boolean tooFar = true;
        while (tooClose || tooFar)
        {
            float horizontalEdge = GameView.width - (a.getWidth() / 2);
            float verticalEdge = GameView.height - (a.getHeight() / 2);
            float newY = getRandomNumber(0, (int)horizontalEdge);
            float newX = getRandomNumber(0, (int)verticalEdge);
            a.setXLocation(newX);
            a.setYLocation(newY);
            float[] values = a.aimAtPlayer();
            float horizontalDistance = values[8];
            float verticalDistance = values[9];
            float speed = a.getPhysicalSpeed();
            tooClose = a.inRange();
            tooFar = !a.aim(horizontalDistance, verticalDistance, speed);
        }
        a.shoot();
    }

    public void nextSimonCycle()
    {
        int boxPressed = getRandomNumber(1, 4);
        int buttonPressed = getRandomNumber(1, 4);
        boxPresses.add(boxPressed);
        buttonPresses.add(buttonPressed);
        nextSimonNum = 0;
        for(int i = 0; i < currentSimonLength; i++)
        {
            int boxNum = boxPresses.get(i).intValue();
            int buttonNum = buttonPresses.get(i).intValue();
            Box toPress = boxes.get(boxNum);
            toPress.shineResult("button", buttonNum);
            try {
                roomThread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pressedBox(String button)
    {
        Character player = Character.getPlayer();
        if (player.getYLocation() == (GameView.height - (player.getHeight() / 2)))
        {
            float x = player.getXLocation();
            float width = player.getWidth();
            Box toPress = null;
            int boxNum = 0;
            for(int i = 0; i < 4; i++)
            {
                Box box = boxes.get(i);
                float boxX = box.getXLocation();
                float boxWidth = box.getWidth();
                if(((boxX / (boxWidth / 2)) <= (x + (width / 2))) && ((boxX + (boxWidth / 2)) >= (x - (width / 2))))
                {
                    toPress = box;
                    boxNum = i + 1;
                }
            }
            if(boxNum != 0)
            {
                int pressNum = toPress.shineResult(button, 0);
                int pressedButton = pressNum % 10;
                int pressedBox = pressNum - pressedButton;
                pressedBox /= 10;
                if((boxPresses.get(nextSimonNum).intValue() == pressedBox) && (buttonPresses.get(nextSimonNum).intValue() == pressedButton))
                {
                    nextSimonNum++;
                    if(nextSimonNum == currentSimonLength)
                    {
                        length--;
                        nextSimon = true;
                    }
                }
                else
                {
                    misses--;
                    currentSimonLength--;
                    nextSimon = true;
                }
            }
        }
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

            float x = projectile.getXLocation();
            float y = projectile.getYLocation();
            float horizontalSpeed = projectile.getHorizontalSpeed();
            float verticalSpeed = projectile.getVerticalSpeed();
            x += horizontalSpeed;
            y += verticalSpeed;
            projectile.setXLocation(x);
            projectile.setYLocation(y);
            //now vertical acceleration
            if((!(projectile instanceof Fist) && !(projectile instanceof Pebble)) && (!homed))
            {
                float accelerationNum = GameView.height / 100;//transition from centimeters to meters
                accelerationNum *= 30;//transition from frames to seconds
                float accelerationDiff = 10 / 30;//acceleration in one frame
                verticalSpeed *= accelerationNum;//transition from pixels per frame to meters per second
                verticalSpeed += accelerationDiff;//down is positive, up is negative
                verticalSpeed /= accelerationNum;//transition from meters per second to pixels per frame
                projectile.setVerticalSpeed(verticalSpeed);
            }
            String wallResult = "noHit";
            if((projectile.getEdge("left") <= 0) || (projectile.getEdge("right") >= GameView.width))
                wallResult = hitWall(projectile, true);
            if((projectile.getEdge("top") <= 0) || (projectile.getEdge("bottom") >= GameView.height))
                wallResult = hitWall(projectile, false);
            //fix edge
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

    public boolean collision(GameObject character, Projectile projectile)
    {
        Path characterPath = character.getBoundingBox();
        Path projectilePath = projectile.getBoundingBox();
        Path intersection = new Path();

        if (intersection.op(characterPath, projectilePath, Path.Op.INTERSECT)) {
            // Intersection path is now stored in the "intersection" object
            return !intersection.isEmpty();
        }
        return false;
    }


    //also get difficulty in some manner. no need to save it. calculate
    //different challenge rooms. what do?
}

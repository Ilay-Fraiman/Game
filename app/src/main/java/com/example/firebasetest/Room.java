package com.example.firebasetest;

import android.app.AlertDialog;
import android.graphics.Path;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Room implements Runnable {
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
    private int waves;
    private int ID;
    private int enemyDifficulty;
    private int difficultyScaling;
    private int challengeDifficulty;
    private int challengeDifficultyScaling;
    private GameView creator;
    private int parryWindow;//send it when pressed (knight challenge)
    private int[][] missesArray = {{10, 7, 5}, {7, 5, 4}, {6, 4, 3}, {5, 3, 2}, {3, 2, 1}};//missesArray[scaling][section]
    private int length;
    private boolean nextSimon;
    private int nextSimonNum;
    private ArrayList<Integer> boxPresses;
    private ArrayList<Integer> buttonPresses;
    private ArrayList<Box> boxes;
    private int currentSimonLength;
    private boolean roomDone;
    private int finalStage;
    private Character myPlayer;
    private String playerClass;
    private int playerLevel;
    private ArrayList<Character> charactersToRemove;
    private ArrayList<Projectile> projectilesToRemove;
    private boolean failed;
    private boolean holdingA;

    public Room(User user, GameView gameView)
    {
        enemyDifficulty = user.getEnemyDifficulty();
        difficultyScaling = user.getDifficultyScaling();
        challengeDifficulty = user.getChallengeDifficulty();
        challengeDifficultyScaling = user.getChallengeDifficultyScaling();
        sectionNum = user.getCurrentSection();
        roomClass = (sectionNum == 4)? "Sage": user.getOrder().get(sectionNum - 1);
        floorNum = user.getCurrentFloor();
        roomNum = user.getCurrentRoom();
        finalStage = (sectionNum == 4)? 1: 0;
        this.misses = this.missesArray[challengeDifficultyScaling - 1][sectionNum - 1];
        this.length = challengeDifficulty * floorNum;
        this.nextSimon = false;
        this.nextSimonNum = 0;
        boxPresses = null;
        buttonPresses = null;
        boxes = null;
        holdingA = false;
        currentSimonLength = 0;
        ID = (sectionNum * 100) + (floorNum * 10) + roomNum;
        characters = new ArrayList<>();
        projectiles = new ArrayList<>();
        objects = new ArrayList<>();
        charactersToRemove = new ArrayList<>();
        projectilesToRemove = new ArrayList<>();
        roomDone = false;
        failed = false;
        creator = gameView;
        myPlayer = null;
        enemiesPerWave = floorNum + (enemyDifficulty - 1);
        waves = sectionNum;
        playerClass = user.getClassName();
        playerLevel = user.getLevel();

        roomThread = new Thread(this);
        roomThread.start();
    }

    public ArrayList<GameObject> getObjects()
    {
        ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
        for (Character character:
             charactersToRemove) {
            characters.remove(character);
        }
        for (Projectile projectile:
             projectilesToRemove) {
            projectiles.remove(projectile);
        }
        charactersToRemove.clear();
        projectilesToRemove.clear();
        gameObjects.addAll(objects);
        gameObjects.addAll(characters);
        gameObjects.addAll(projectiles);
        return gameObjects;
    }

    @Override
    public void run() {
        switch (roomNum)
        {
            case 1:
                enemyRoom();
                break;
            case 2:
                if(roomClass.equals("Knight"))
                    knightChallenge();
                else if (roomClass.equals("Archer"))
                    archerChallenge();
                else
                    mageChallenge();
                break;
            case 3:
            case 4:
                bossRoom();
                break;
        }
    }

    public void initializePlayer(boolean boss)
    {
        int quarter = (boss)? 10: 2;
        float x = GameView.width / quarter;
        float y = GameView.height - (GameView.width / 15f);
        if(playerClass.equals("Knight"))
        {
            Knight player = new Knight(playerLevel, 5, ID, x, y);
            myPlayer = player;
        }
        else if(playerClass.equals("Archer"))
        {
            Archer player = new Archer(playerLevel, 5, ID, x, y);
            myPlayer = player;
        }
        else if(playerClass.equals("Mage"))
        {
            Mage player = new Mage(playerLevel, 5, ID, x, y);
            myPlayer = player;
        }
        Character.setPlayer(myPlayer);
        characters.add(myPlayer);
    }

    public void enemyRoom()
    {
        initializePlayer(false);
        int currentWave = 0;
        int enemiesLeft = 0;
        boolean playerAlive = true;

        float[] xArray = new float[7];
        xArray[0] = 1f;
        xArray[1] = 9f;
        xArray[2] = 2f;
        xArray[3] = 8f;
        xArray[4] = 3f;
        xArray[5] = 7f;
        xArray[6] = 4f;

        float baseX = GameView.width / 10f;
        float y = GameView.height - (GameView.width / 15f);

        while((currentWave < waves) && (playerAlive))
        {
            for(int i = 0; i < enemiesPerWave; i++)
            {
                float currentX = baseX * xArray[i];
                Character enemy = summonEnemy(false, currentX, y);
                characters.add(enemy);
            }
            enemiesLeft = enemiesPerWave;

            while ((enemiesLeft > 0) && (playerAlive))
            {
                for (Character character:
                        characters) {
                    projectiles.addAll(character.getProjectileList(ID, characters));
                }
                myPlayer.toMove();
                for (Projectile projectile:
                        projectiles) {
                    moveProjectile(projectile);
                }
                for (Projectile projectile:
                        projectiles) {
                    for (Character character:
                            characters) {
                        hit(character, projectile);
                    }
                }
                for (Character character:
                        characters) {
                    if(!character.isAlive())
                    {
                        if(addToRemove(character, null))
                        {
                            if(character.getCharacterGrade() == 5)
                                playerAlive = false;
                            else
                                enemiesLeft--;
                        }
                    }
                }
                try {
                    roomThread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(enemiesLeft <= 0)
            {
                currentWave++;
            }
        }
        if(!playerAlive)
            failure();
        else if(currentWave >= waves)
            roomEnd();
    }

    public boolean addToRemove(Character removableCharacter, Projectile removableProjectile)//to stop mistakes by polymorphism
    {
        if(removableCharacter != null)
        {
            if(!charactersToRemove.contains(removableCharacter))
            {
                charactersToRemove.add(removableCharacter);
                return true;
            }
        }
        if(removableProjectile != null)
        {
            if(!projectilesToRemove.contains(removableProjectile))
            {
                projectilesToRemove.add(removableProjectile);
                return true;
            }
        }
        return false;
    }

    public void bossRoom()
    {
        initializePlayer(true);
        float x = GameView.width / 10f;
        x *= 8f;
        float y = GameView.height - (GameView.width / 15f);
        Character boss = summonEnemy(false, x, y);
        characters.add(boss);
        boolean playerDead = false;
        boolean bossDead = false;
        boolean summonCorpse = false;
        long toSummon = 0;
        if(roomClass.equals("Mage"))
        {
            if(floorNum == 2)
            {
                x /= 8f;
                x *= 6f;
                Mage bossClone = (Mage) summonEnemy(false, x, y);
                Mage tempBoss = (Mage) boss;
                tempBoss.setClone(bossClone);
                bossClone.setClone(tempBoss);
                characters.add(bossClone);
            }
            else if(floorNum == 3)
            {
                summonCorpse = true;
            }
        }
        while ((!playerDead) && (!bossDead))
        {
            if(summonCorpse && (System.currentTimeMillis() >= toSummon))
            {
                float corpseX = boss.getXLocation();
                float add = boss.getWidth();
                float multiplication = (boss.facingAngle() == 0d)? 1f: (-1f);
                add *= multiplication;
                float finalX = corpseX + (add * 1.5f);
                if(finalX <= 0f || finalX >= GameView.width)
                    add *= (-1f);
                corpseX += add;
                Character corpse = summonEnemy(true, corpseX, y);
                corpse.setSpriteName("corpse");
                characters.add(corpse);
                toSummon = System.currentTimeMillis() + 5000L;
            }
            for (Character character:
                 characters) {
                projectiles.addAll(character.getProjectileList(ID, characters));
            }
            myPlayer.toMove();
            for (Projectile projectile:
                 projectiles) {
                moveProjectile(projectile);
            }
            for (Projectile projectile:
                 projectiles) {
                for (Character character:
                     characters) {
                    hit(character, projectile);
                }
            }
            for (Character character:
                 characters) {
                if(!character.isAlive())
                    addToRemove(character, null);
            }
            if(!boss.isAlive())
            {
                charactersToRemove.remove(boss);
                characters.remove(boss);
                if(finalStage == 1)
                {
                    finalStage = 2;
                    x = boss.getXLocation();
                    y = boss.getYLocation();
                    boss = summonEnemy(false, x, y);
                    characters.add(boss);
                }
                else
                    bossDead = true;
            }
            else if(!myPlayer.isAlive())
            {
                charactersToRemove.remove(myPlayer);
                playerDead = true;
            }
            else
            {
                try {
                    roomThread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(playerDead)
            failure();
        else if(bossDead)
            roomEnd();
    }

    public Character summonEnemy(boolean corpse, float x, float y)
    {
        String className = roomClass;
        int characterGrade = 0;
        int level = sectionNum;
        if(className.equals("Mage") && (((roomNum == 3) && (floorNum == 3)) && (corpse)))
        {
            int classNum = getRandomNumber(1, 3);
            switch (classNum)
            {
                case 1:
                    className = "Knight";
                    break;
                case 2:
                    className = "Archer";
                    break;
                case 3:
                    className = "Mage";
                    break;
            }
        }
        else if(finalStage != 0)
        {
            className = (finalStage == 1)? "Sage": "Berserker";
            level = 12;
        }
        else if(roomNum == 3)
        {
            characterGrade = floorNum;
            level += 4;
        }
        else if(roomNum == 4)
        {
            characterGrade = 4;
            level += 8;
        }

        if(className.equals("Knight"))
        {
            Knight knight = new Knight(level, characterGrade, ID, x, y);
            return knight;
        }
        else if(className.equals("Archer"))
        {
            Archer archer = new Archer(level, characterGrade, ID, x, y);
            return archer;
        }
        else if(className.equals("Mage"))
        {
            Mage mage = new Mage(level, characterGrade, ID, x, y);
            return mage;
        }
        else if(className.equals("Sage"))
        {
            Sage sage = new Sage(level, ID, x, y);
            return sage;
        }
        else// if(className.equals("Berserker"))
        {
            Berserker berserker = new Berserker(level, ID, x, y);
            return berserker;
        }
    }

    public void knightChallenge()
    {
        //only 2 buttons work: direction joystick and parry button
        String name = playerClass;
        name = name.toLowerCase();
        myPlayer = new Character(1, 3, 3, 3, name, ID, GameView.width * (1f/4f), GameView.height - (GameView.width / 15f), 5);
        Character.setPlayer(myPlayer);
        Archer a = new Archer(1, 5, ID, GameView.width * (3f/4f), GameView.height - (GameView.width / 15f));
        characters.add(myPlayer);
        parryWindow = 0;
        long timeToHit = 0;
        while (misses > 0 && length > 0)
        {
            ArrayList<Projectile> currentProjectiles = null;
            currentProjectiles = a.getProjectileList(this.ID, characters);
            projectiles.addAll(currentProjectiles);
            for (Projectile p:
                 projectiles) {
                moveProjectile(p);
                boolean[] hit = hit(myPlayer, p);
                if (hit[0])
                {
                    addToRemove(null, p);
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
        myPlayer = new Archer(1, 5, ID, GameView.width / 2f, GameView.height - (GameView.width / 15f));
        Character.setPlayer(myPlayer);
        characters.add(myPlayer);
        ArrayList<Projectile> keptProjectiles = new ArrayList<>();
        ArrayList<Projectile> temporaryHolder = new ArrayList<>();
        int left = ((int) (GameView.width / 20f));
        int right = ((int) ((GameView.width / 20f) * 19f));
        int bottom = ((int) (GameView.height)) - left;
        int top = ((int) (GameView.height / 2f));
        Target target = new Target(ID, GameView.width / 2f, GameView.height * (19f/20f), length,0, 0);
        boolean check = initializeTarget(target, left, right, top, bottom);
        objects.add(target);
        while (length > 0 && misses > 0)
        {
            temporaryHolder.addAll(myPlayer.getProjectileList(ID, characters));
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
                if(GameObject.collision(target, p) && (misses > 0))
                {
                    length--;
                    check = initializeTarget(target, left, right, top, bottom);
                    addToRemove(null, p);
                    keptProjectiles.remove(p);
                }
                else if(((projectilesToRemove.contains(p)) || (!projectiles.contains(p))) && (check))
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
        String name = playerClass;
        name = name.toLowerCase();
        myPlayer = new Character(1, 3, 3, 3, name, ID, GameView.width * (1f/4f), GameView.height - (GameView.width / 15f), 5);
        Character.setPlayer(myPlayer);
        characters.add(myPlayer);
        boxPresses = new ArrayList<>();
        buttonPresses = new ArrayList<>();
        boxes = new ArrayList<>();
        float boxDistance = GameView.width / 5f;
        for(int i = 1; i < 5; i++)
        {
            float width = GameView.width / 10f;
            float x = boxDistance * i;
            float y = GameView.height - (width / 2f);
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
            myPlayer.toMove();
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
            hitting[0] = GameObject.collision(c, p);
        }
        //first one is collision, second one is actual hit
        if(hitting[0])
        {
            if(roomNum == 2 && roomClass.equals("Knight"))
                hitting[1] = c.IsParrying();
            else
            {
                if(c.hit(p))
                {
                    p.hasHit(c);
                    if(!p.isTimed())
                        addToRemove(null, p);
                }
                if(p.isTimeUp())
                    addToRemove(null, p);
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
            float horizontalEdge = GameView.width - (a.getWidth() / 2f);
            float verticalEdge = GameView.height - (a.getHeight() / 2f);
            float newY = (float) getRandomNumber(0, (int)horizontalEdge);
            float newX = (float) getRandomNumber(0, (int)verticalEdge);
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
        if (myPlayer.getYLocation() == (GameView.height - (myPlayer.getHeight() / 2f)))
        {
            float x = myPlayer.getXLocation();
            float width = myPlayer.getWidth();
            Box toPress = null;
            int boxNum = 0;
            for(int i = 0; i < 4; i++)
            {
                Box box = boxes.get(i);
                float boxX = box.getXLocation();
                float boxWidth = box.getWidth();
                if(((boxX / (boxWidth / 2f)) <= (x + (width / 2f))) && ((boxX + (boxWidth / 2f)) >= (x - (width / 2f))))
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
                float accelerationNum = GameView.pixelHeight / 100f;//transition from centimeters to meters
                accelerationNum *= 30f;//transition from frames to seconds
                float accelerationDiff = 10f / 30f;//acceleration in one frame
                verticalSpeed *= accelerationNum;//transition from pixels per frame to meters per second
                verticalSpeed += accelerationDiff;//down is positive, up is negative
                verticalSpeed /= accelerationNum;//transition from meters per second to pixels per frame
                projectile.setVerticalSpeed(verticalSpeed);
            }
            String wallResult = "noHit";
            if((projectile.getEdge(0) <= 0f) || (projectile.getEdge(1) >= GameView.width))
                wallResult = hitWall(projectile, true);
            if((projectile.getEdge(2) <= 0f) || (projectile.getEdge(3) >= GameView.height))
                wallResult = hitWall(projectile, false);

            if(wallResult.equals("changed"))
            {
                horizontalSpeed = projectile.getHorizontalSpeed();
                verticalSpeed = projectile.getVerticalSpeed();
            }

            if(!(wallResult.equals("removed")))
            {
                double newVert = (double) verticalSpeed * (-1d);
                double angle = Math.atan2(newVert, (double) horizontalSpeed);//converts as if radius is 1
                double degreesAngle = Math.toDegrees(angle);
                projectile.setAngle(degreesAngle);
            }
        }
        else if(projectile.isTimeUp())
            addToRemove(null, projectile);
    }

    public String hitWall(Projectile projectile, boolean horizontalWall)//result code for actions
    {
        String resultCode = "changed";
        if((projectile instanceof Arrow) && (((Arrow) projectile).isRicochet()))
        {
            if(horizontalWall)
                projectile.setHorizontalSpeed(projectile.getHorizontalSpeed() * (-1f));
            else
                projectile.setVerticalSpeed(projectile.getVerticalSpeed() * (-1f));
        }
        else
        {
            resultCode = "removed";
            if(projectile.getAilment().equals("fire"))
            {
                GroundFire gF = new GroundFire(projectile);
                projectiles.add(gF);
            }
            addToRemove(null, projectile);
        }
        return resultCode;
    }

    public void roomEnd()
    {
        roomDone = true;
        characters.clear();
        projectiles.clear();
        objects.clear();
        creator.twilight();
    }

    public void failure()
    {
        failed = true;
        characters.clear();
        projectiles.clear();
        objects.clear();
        creator.setFailureDialog();
    }

    public void pressedA()
    {
        if((!roomDone) && (!failed))
        {
            if(roomNum == 2)
            {
                if(roomClass.equals("Knight"))
                    myPlayer.parryChallenge(parryWindow);
                else if(roomClass.equals("Mage"))
                    pressedBox("A");
            }
            else
            {
                if(myPlayer instanceof Knight)
                {
                    Knight player = (Knight) myPlayer;
                    player.shieldOrParry(2);
                }
                else if(myPlayer instanceof Archer)
                {
                    Archer player = (Archer) myPlayer;
                    player.stab();
                }
                else if(myPlayer instanceof Mage)
                {
                    Mage player = (Mage) myPlayer;
                    player.mist();
                }
            }
        }
    }

    public void pressedB()
    {
        if((!roomDone) && (!failed))
        {
            if(roomNum == 2)
            {
                if(roomClass.equals("Mage"))
                    pressedBox("B");
            }
            else
            {
                if(myPlayer instanceof Knight)
                {
                    Knight player = (Knight) myPlayer;
                    player.buff();
                }
                else if(myPlayer instanceof Archer)
                {
                    Archer player = (Archer) myPlayer;
                    player.poison();
                }
                else if(myPlayer instanceof Mage)
                {
                    Mage player = (Mage) myPlayer;
                    player.fireball();
                }
            }
        }
    }

    public void pressedX()
    {
        if((!roomDone) && (!failed))
        {
            if(roomNum == 2)
            {
                if(roomClass.equals("Mage"))
                    pressedBox("X");
            }
            else
            {
                if(myPlayer instanceof Knight)
                {
                    Knight player = (Knight) myPlayer;
                    player.attack();
                }
                else if(myPlayer instanceof Archer)
                {
                    Archer player = (Archer) myPlayer;
                    player.shoot();
                }
                else if(myPlayer instanceof Mage)
                {
                    Mage player = (Mage) myPlayer;
                    player.lightLine();
                }
            }
        }
    }

    public void pressedY()
    {
        if((!roomDone) && (!failed))
        {
            if(roomNum == 2)
            {
                if(roomClass.equals("Mage"))
                    pressedBox("Y");
            }
            else
            {
                if(myPlayer instanceof Knight)
                {
                    Knight player = (Knight) myPlayer;
                    player.mount();
                }
                else if(myPlayer instanceof Archer)
                {
                    Archer player = (Archer) myPlayer;
                    player.homing();
                }
                else if(myPlayer instanceof Mage)
                {
                    Mage player = (Mage) myPlayer;
                    player.heal();
                }
            }
        }
    }

    public boolean pressedStart()
    {
        return roomDone;
    }

    public void holdA()
    {
        if(((!roomDone) && (!failed)) && ((roomNum != 2) && (myPlayer instanceof Knight)))
        {
            Knight player = (Knight) myPlayer;
            holdingA = player.shieldOrParry(1);
        }
    }

    public void releaseA()
    {
        if(holdingA)
        {
            holdingA = false;
            Knight player = (Knight) myPlayer;
            player.shieldReleased();
        }
    }

    public void initializeMovement(float x, float y)
    {
        if(((!roomDone) && (!failed)) && (!((roomNum == 2) && (!roomClass.equals("Mage")))))
            myPlayer.setUpMovement(x, y);
    }

    public void initializeDirection(float x, float y)
    {
        if((!roomDone) && (!failed))
            myPlayer.setUpDirection(x, y);
    }
}

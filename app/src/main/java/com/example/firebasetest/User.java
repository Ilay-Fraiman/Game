package com.example.firebasetest;

public class User {
    private String email;//add somewhere checks for existing firebase user, incorrect password.
    private int level;
    private String className;
    private int order;
    private int enemyDifficulty;
    private int difficultyScaling;
    private int challengeDifficulty;
    private int challengeDifficultyScaling;
    private int doomsdayClock;//int(?)
    private int suddenDeath;//sudden death type
    private int currentSection;
    private int currentLevel;
    private int currentRoom;

    //add all database values amd get set and initially set them in constructor
    public User(String mail) {
        email = mail;
        level = 5;
        className = "";
        order = 1;
        enemyDifficulty = 5;
        difficultyScaling = 3;
        challengeDifficulty = 0;//?
        challengeDifficultyScaling = 0;//?
        suddenDeath = 0;//0=off,1-3=type
        doomsdayClock = 0;//0=off, 1-10(actual slider maybe instead of 4 buttons)=hours
        currentSection = -1;//-1=starting area
        currentLevel = 0;
        currentRoom = 0;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getEnemyDifficulty() {
        return enemyDifficulty;
    }

    public void setEnemyDifficulty(int enemyDifficulty) {
        this.enemyDifficulty = enemyDifficulty;
    }

    public int getDifficultyScaling() {
        return difficultyScaling;
    }

    public void setDifficultyScaling(int difficultyScaling) {
        this.difficultyScaling = difficultyScaling;
    }

    public int getChallengeDifficulty() {
        return challengeDifficulty;
    }

    public void setChallengeDifficulty(int challengeDifficulty) {
        this.challengeDifficulty = challengeDifficulty;
    }

    public int getChallengeDifficultyScaling() {
        return challengeDifficultyScaling;
    }

    public void setChallengeDifficultyScaling(int challengeDifficultyScaling) {
        this.challengeDifficultyScaling = challengeDifficultyScaling;
    }

    public int getDoomsdayClock() {
        return doomsdayClock;
    }

    public void setDoomsdayClock(int doomsdayClock) {
        this.doomsdayClock = doomsdayClock;
    }

    public int getSuddenDeath() {
        return suddenDeath;
    }

    public void setSuddenDeath(int suddenDeath) {
        this.suddenDeath = suddenDeath;
    }

    public int getCurrentSection() {
        return currentSection;
    }

    public void setCurrentSection(int currentSection) {
        this.currentSection = currentSection;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(int currentRoom) {
        this.currentRoom = currentRoom;
    }

    public String getEmail() {
        return email;
    }
}

package com.example.firebasetest;

import java.util.ArrayList;

public class User {
    private String email;//add somewhere checks for existing firebase user, incorrect password.
    private int level;
    private String className;
    private ArrayList<String> order;
    private int difficulty;
    private int enemyDifficulty;
    private int difficultyScaling;
    private int challengeDifficulty;
    private int challengeDifficultyScaling;
    private int currentSection;
    private int currentFloor;
    private int currentRoom;

    //add all database values amd get set and initially set them in constructor
    //note: while both suddenDeath and doomsdayClock exist here, we ignore their existance
    //in the project until further notice
    public User(String mail) {
        email = mail;
        level = 6;
        className = "";
        difficulty = 3;
        enemyDifficulty = 3;
        difficultyScaling = 3;
        challengeDifficulty = 3;
        challengeDifficultyScaling = 3;
        currentSection = -1;//-1=starting area
        currentFloor = 0;
        currentRoom = 0;
        order = new ArrayList<String>();
        order.add("Knight");
        order.add("Archer");
        order.add("Mage");
    }

    public User() {
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
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

    public ArrayList<String> getOrder() {
        return order;
    }

    public void setOrder(String firstTower) {
        this.order.clear();
        switch (firstTower)
        {
            case "Knight":
                order.add("Knight");
                order.add("Archer");
                order.add("Mage");
                break;
            case "Archer":
                order.add("Archer");
                order.add("Mage");
                order.add("Knight");
                break;
            case "Mage":
                order.add("Mage");
                order.add("Knight");
                order.add("Archer");
                break;
        }
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
        setLevel(9 - difficultyScaling);
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


    public int getCurrentSection() {
        return currentSection;
    }

    public void setCurrentSection(int currentSection) {
        this.currentSection = currentSection;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
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

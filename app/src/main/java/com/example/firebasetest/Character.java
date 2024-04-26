package com.example.firebasetest;

import android.graphics.Bitmap;
import java.util.*;

public class Character extends GameObject {
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
    public Character(int level, int HPD, int ACD, int APD, Bitmap sprite, int ID, float xLocation, float yLocation, float width, float height) //HPD, ACD, APD=2/3/5
    {
        super(sprite, ID, xLocation, yLocation, width, height);
        HP = HPD * 10 * level;
        attackCooldown-= (level*ACD);
        attackPower = level*APD;
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

}

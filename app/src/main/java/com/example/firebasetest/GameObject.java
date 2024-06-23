package com.example.firebasetest;

import android.graphics.Bitmap;
import android.graphics.Path;

public class GameObject {
    private String spriteName;

    private float width;

    private float height;

    private float xLocation;

    private float yLocation;

    private Path boundingBox;
    private float[] edges;//[left, right, top, bottom]

    public String getSpriteName() {
        return spriteName;
    }

    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }

    protected int roomID;

    public GameObject(String spriteName, int ID, float x, float y, float w, float h){
        this.spriteName = spriteName;
        this.roomID = ID;
        this.xLocation = x;
        this.yLocation = y;
        this.width = w;
        this.height = h;
        for(int i = 0; i < 4; i++)
        {
            if(i < 2)
                this.edges[i] = x;
            else
                this.edges[i]  = y;
        }
        this.boundingBox = this.getBoundingBox();
    }

    public GameObject(String spriteName, int ID, float x, float y)//character
    {
        this.spriteName = spriteName;
        this.roomID = ID;
        this.xLocation = x;
        this.yLocation = y;
        this.width = (GameView.width / 15);
        this.height = this.width;
        for(int i = 0; i < 4; i++)
        {
            if(i < 2)
                this.edges[i] = x;
            else
                this.edges[i]  = y;
        }
        this.boundingBox = this.getBoundingBox();
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float w) {
        this.width = w;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float h) {
        this.height = h;
    }

    public float getXLocation() {
        return xLocation;
    }

    public void setXLocation(float x) {
        this.xLocation = x;
    }

    public float getYLocation() {
        return yLocation;
    }

    public void setYLocation(float y) {
        this.yLocation = y;
    }

    public float getDirection()
    {
        float directionAngle = 0;
        if(this instanceof Projectile)
        {
            double angle = ((Projectile) this).getAngle();
            directionAngle = (float) angle;
        }
        if(this instanceof Character)
        {
            double angle = ((Character) this).facingAngle();
            directionAngle = (float) angle;
        }
        return directionAngle;
    }

    public int getRoomID()
    {
        return this.roomID;
    }

    public Path getBoundingBox()
    {
        boundingBox = null;
        boundingBox = new Path();

        double radius = Math.sqrt(Math.pow((width / 2), 2) + Math.pow((height / 2), 2));
        double currentAngle = getDirection();
        double toB = Math.toDegrees(Math.atan2(height, width));
        double BToA = 180 - (toB * 2);
        double AToD = 2 * toB;
        //DToC is BToA
        double[] addAngles = new double[4];
        addAngles[0] = toB;
        addAngles[1] = BToA;
        addAngles[2] = AToD;
        addAngles[3] = BToA;

        for(int i = 0; i < 4; i++)
        {
            currentAngle += addAngles[i];
            float x = xLocation + ((float) (Math.cos(Math.toRadians(currentAngle)) * radius));
            float y = yLocation - ((float) (Math.sin(Math.toRadians(currentAngle)) * radius));
            if(i == 0)
                boundingBox.moveTo(x, y);
            else
                boundingBox.lineTo(x, y);

            if(x < edges[0])
                edges[0] = x;
            if(x > edges[1])
                edges[1] = x;
            if(y < edges[2])
                edges[2] = y;
            if(y > edges[3])
                edges[3] = y;
        }
        return boundingBox;
    }

    public float getEdge(int num)
    {
        return edges[num];
    }

    public static boolean collision(GameObject character, GameObject projectile)
    {
        if(projectile instanceof Teleport)
            return false;
        Path characterPath = character.getBoundingBox();
        Path projectilePath = projectile.getBoundingBox();
        Path intersection = new Path();

        if (intersection.op(characterPath, projectilePath, Path.Op.INTERSECT)) {
            // Intersection path is now stored in the "intersection" object
            return !intersection.isEmpty();
        }
        return false;
    }
}

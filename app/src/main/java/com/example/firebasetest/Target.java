package com.example.firebasetest;

public class Target extends GameObject{
    private Archer aimer = null;
    private Character myPlayer = null;
    private float speedPerFrame;//starts at width / 300, end at (15th) width / 75
    private int horizontalDirection;
    private int verticalDirection;
    public Target(int ID, float x, float y, float length, int xMove, int yMove)//max height == half canvas
    {
        super("target", ID, x, y, GameView.width / 20, GameView.width / 20);
        float divider = 75 * length;
        speedPerFrame = GameView.width / divider;
        horizontalDirection = xMove;
        verticalDirection = yMove;
        float pX = Character.getPlayerX();
        float pY = Character.getPlayerY();
        aimer = new Archer(1, 5, ID, pX, pY);
        myPlayer = new Character(1, 3, 3, 3,"none", ID, x, y ,5);
    }

    public boolean incorrectRange()
    {
        aimer.setXLocation(Character.getPlayerX());
        aimer.setYLocation(Character.getPlayerY());
        myPlayer.setXLocation(this.getXLocation());
        myPlayer.setYLocation(this.getYLocation());

        Character tempPlayer = Character.getPlayer();
        Character.setPlayer(myPlayer);

        float[] values = aimer.aimAtPlayer();
        float horizontalDistance = values[8];
        float verticalDistance = values[9];
        float speed = aimer.getPhysicalSpeed();
        boolean tooClose = aimer.inRange();
        boolean tooFar = !aimer.aim(horizontalDistance, verticalDistance, speed);

        Character.setPlayer(tempPlayer);
        return (tooClose || tooFar);
    }

    public void moveTarget()
    {
        float formerX = this.getXLocation();
        float formerY = this.getYLocation();
        float width = this.getWidth();
        float height = this.getHeight();
        float newX = formerX + (speedPerFrame * horizontalDirection);
        float newY = formerY + (speedPerFrame * verticalDirection);
        float horizontalEdge = newX + ((width / 2) * horizontalDirection);
        float verticalEdge = newY + ((height / 2) * verticalDirection);

        this.setXLocation(newX);
        if (incorrectRange() || ((horizontalEdge <= 0) || (horizontalEdge >= GameView.width)))
        {
            horizontalDirection *= (-1);
            newX = formerX + (speedPerFrame * horizontalDirection);
            this.setXLocation(newX);
        }

        this.setYLocation(newY);
        if (incorrectRange() || ((verticalEdge <= (GameView.height / 2)) || (verticalEdge >= GameView.height)))
        {
            verticalDirection *= (-1);
            newY = formerY + (speedPerFrame * verticalDirection);
            this.setYLocation(newY);
        }
    }

    public void setSpeedPerFrame(int length) {
        float divider = 75 * length;
        this.speedPerFrame = GameView.width / divider;
    }

    public void setHorizontalDirection() {
        int plus = getRandomNumber(1, 2);
        this.horizontalDirection = (plus == 1)? 1: (-1);
    }

    public void setVerticalDirection() {
        int plus = getRandomNumber(1, 2);
        this.verticalDirection = (plus == 1)? 1: (-1);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}

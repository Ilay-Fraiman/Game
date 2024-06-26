package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.Timer;
import java.util.TimerTask;

public class EarthShatter extends Projectile{
    private float add;
    private double expansionDirection;
    public EarthShatter(int ID, Character creator, double power, float xLocation, float yLocation, float width, float height, double direction)
    {
        super("earthshatter", ID, creator, power, 0, 0, xLocation, yLocation, width, height, 0, "shatter");
        //height is half chr, its creator is 2 chr; only moves on x axis and on the floor; max width is 3/8 canvas width, starts at a 1/5 of that and widens
        this.expansionDirection = direction;
        this.isTimed = true;
        this.oneTimeHit = true;
        this.TTD = 1000L;
        this.add = this.getWidth();
        this.widen();
    }

    private void widen()
    {
        class Extend extends TimerTask {
            private EarthShatter earthShatter;
            private int repeats;

            Extend(EarthShatter eS, int r)
            {
                this.earthShatter = eS;
                this.repeats = r;
            }

            @Override
            public void run() {
                if(repeats>0)
                {
                    if(!this.earthShatter.strech())
                        repeats = 0;
                    repeats--;
                    Timer timer = new Timer();
                    TimerTask task = new Extend(earthShatter, repeats);
                    timer.schedule(task, 100L);
                }
            }
        }
        Timer timer = new Timer();
        TimerTask task = new Extend(this, 4);
        timer.schedule(task, 100L);
    }

    public boolean strech()
    {
        float width = this.getWidth();
        float x = this.getXLocation();
        boolean advance = true;
        float start = x - (width / 2f);
        float end = x + (width / 2f);
        width += (add * expansionDirection);
        x += ((add * expansionDirection) / 2f);
        if(this.expansionDirection == 1d)
        {
            if((x + (width / 2f)) >= GameView.width)
            {
                advance = false;
                width = GameView.width - start;
                x = start + (width / 2f);
            }
        }
        else
        {
            if((x - (width / 2f)) <= 0f)
            {
                advance = false;
                width = end;
                x = width / 2f;
            }
        }
        this.setXLocation(x);
        this.setWidth(width);
        return advance;
    }
}

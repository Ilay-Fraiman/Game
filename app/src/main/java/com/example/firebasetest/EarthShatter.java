package com.example.firebasetest;

import android.graphics.Bitmap;

import java.util.Timer;
import java.util.TimerTask;

public class EarthShatter extends Projectile{
    //is timed, one time = true
    private float add;
    public EarthShatter(Bitmap sprite, int ID, Character creator, float power, float xLocation, float yLocation, float width, float height, double direction)
    {
        super(sprite, ID, creator, power, 0, 0, xLocation, yLocation, width, height, direction, "shatter");
        //height is half chr, its creator is 2 chr; only moves on x axis and on the floor; max width is 3/8 canvas width, starts at a 1/5 of that and widens
        this.isTimed = true;
        this.oneTimeHit = true;
        this.TTD = 1000L;
        this.add = this.getWidthPercentage();
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
        float width = this.getWidthPercentage();
        float x = this.getXPercentage();
        boolean advance = true;
        if(this.angle == 1)
        {
            width += add;
            if(x + width >= GameView.width)
            {
                advance = false;
                width = GameView.width - x;
            }
            this.setWidthPercentage(width);
        }
        else
        {
            x -= add;
            if(x <= 0)
            {
                advance = false;
                x = 0;
            }
            this.setXPercentage(x);
        }
        return advance;
    }
}

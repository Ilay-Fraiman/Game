package com.example.firebasetest;

import java.util.Timer;
import java.util.TimerTask;

public class Box extends GameObject{
    private int boxNum;
    private String shine;
    public Box(int ID, float xLocation, float yLocation, float width, float height, int num)
    {
        super("box", ID, xLocation, yLocation, width, height);
        this.boxNum = num;
        this.shine = "plain";
    }

    public int shineResult(String button, int buttonCode)
    {
        int shineNum = boxNum * 10;//HASH MAP? MAYBE FOR MIST SWITCH CASES.
        //THIS hash should be 1 number for each
        if(button.equals("A") || buttonCode == 1)
        {
            shine = "green";
            shineNum += 1;
        }
        else if(button.equals("B") || buttonCode == 2)
        {
            shine = "red";
            shineNum += 2;
        }
        else if(button.equals("X") || buttonCode == 3)
        {
            shine = "blue";
            shineNum += 3;
        }
        else if(button.equals("Y") || buttonCode == 4)
        {
            shine = "yellow";
            shineNum += 4;
        }
        unShine();
        return shineNum;
    }

    public void unShine()
    {
        class UnShine extends TimerTask {
            private Box box;

            UnShine(Box b)
            {
                this.box = b;
            }

            @Override
            public void run() {
                box.cancelShine();
            }
        }
        Timer timer = new Timer();
        TimerTask task = new UnShine(this);
        timer.schedule(task, 500L);
    }

    public void cancelShine()
    {
        this.shine = "plain";
    }

    @Override
    public String getSpriteName() {
        String name = shine;
        name += "box";
        return name;
    }
}

package com.example.firebasetest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable
{
    Context context;

    public static float width = 0;
    public static float height = 0;
    public static float pixelWidth = 0;
    public static float pixelHeight = 0;
    public static float canvasPixelHeight = 0;//how many pixels high is the canvas
    private Thread gameThread;
    private Canvas canvas;
    private Bitmap background;
    private Paint p;
    private Room currentRoom;
    private User playerUser;
    private boolean running;
    Dpad dpad;
    public GameView(Context context)
    {
        super(context);
        this.context = context;
        width = this.getResources().getDisplayMetrics().widthPixels;
        pixelWidth = 2106 / width;//in centimeters
        float pixelRatio = pixelWidth / 16;
        pixelHeight = pixelRatio * 8.2f;
        height = this.getResources().getDisplayMetrics().heightPixels;
        float canvasRatio = width / 16;
        canvasRatio *= 8.2;
        if(height != canvasRatio)
        {
            canvasPixelHeight = 1080 / pixelHeight;
        }
        currentRoom = null;
    }

    @Override
    public void run() {
        if(playerUser.getCurrentSection() == -1)
        {
            //these are all still pictures
            //flip your phone
            //this game uses a bluetooth enabled controller
            //controls
            //show eyes opening(black back ground, then white in the middle, then full white, then see
            //player character at one end half naked
            //man starts talking
            //shows picture of classes
            //changes value of button presses for a and d pad to choose
            //send to difficulty activity
            //choose tower(needs an activity)
            //start the game
        }
        this.currentRoom = new Room(playerUser);
        //seperate function for last room(after)
        while (running)
        {
            ArrayList<GameObject> gameObjectArrayList = this.currentRoom.getObjects();
            //intersections can be done with rectangles. ask gemini
        }
    }
    public void resume(User user, Dpad dPad){
        playerUser = user;
        dpad = dPad;
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void nextRoom()
    {
        Room nextRoom = new Room(playerUser);
        this.currentRoom = nextRoom;
    }
}

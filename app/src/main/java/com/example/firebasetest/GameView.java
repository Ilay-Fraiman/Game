package com.example.firebasetest;
import static android.graphics.Bitmap.createScaledBitmap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable
{
    Context context;

    public static float width = 0;
    public static float height = 0;
    public static float pixelWidth = 0;
    public static float pixelHeight = 0;
    private Thread gameThread;
    private Canvas canvas;
    private Paint p;
    private Room currentRoom;
    private User playerUser;
    private boolean running;
    private SurfaceHolder ourHolder;
    private int pictureNum;
    private boolean holdingA;
    Dpad dpad;
    public GameView(Context context)
    {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        pictureNum = 0;
        holdingA = false;
        width = this.getResources().getDisplayMetrics().widthPixels;
        pixelWidth = 2106 / width;//in centimeters
        height = this.getResources().getDisplayMetrics().heightPixels;
        pixelHeight = 1080 / height;
        currentRoom = null;
    }

    @Override
    public void run() {
        String name = "background";
        if(playerUser.getCurrentSection() == -1)
        {
            while (pictureNum < 7)//7 is a random number. it should be last pic
            {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT);
                String sprite = name + pictureNum;
                draw(sprite, 0, 0, (int)GameView.width, (int)GameView.height, 0);
                ourHolder.unlockCanvasAndPost(canvas);
            }
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
        else
            pictureNum = 7;
        this.currentRoom = new Room(playerUser, this);
        //seperate function for last room(after)
        while (running)
        {
            ArrayList<GameObject> gameObjectArrayList = this.currentRoom.getObjects();
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT);
            //draw background
            for (GameObject gameObject: gameObjectArrayList)
            {
                String sprite = gameObject.getSpriteName();
                float x = gameObject.getXLocation();
                float y = gameObject.getYLocation();
                float width = gameObject.getWidth();
                float height = gameObject.getHeight();
                int desiredWidth = (int) width;
                int desiredHeight = (int) height;
                float angle = gameObject.getDirection();
                if(gameObject instanceof Character)
                {
                    Character object = (Character) gameObject;
                    boolean[] itemCheck = object.hasItems();
                    for(int n = 0; n < 2; n++)
                    {
                        if(itemCheck[n])
                        {
                            GameObject item = object.getItem(n);
                            gameObjectArrayList.add(item);
                        }
                    }
                    for(int i = 0; i < 8; i++)
                    {
                        if(object.isStatus(i))
                        {
                            GameObject statusBubble = object.getStatus(i);
                            gameObjectArrayList.add(statusBubble);
                        }
                    }
                }
                draw(sprite, x, y, desiredWidth, desiredHeight, angle);
            }
            ourHolder.unlockCanvasAndPost(canvas);
            try {
                gameThread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void resume(User user, Dpad dPad){
        playerUser = user;
        dpad = dPad;
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void nextRoom(boolean loser)
    {
        if(loser)
        {
            //display gameover
        }
        this.currentRoom = null;
        Room nextRoom = new Room(playerUser, this);
        this.currentRoom = nextRoom;
    }

    public void draw(String spriteName, float xLocation, float yLocation, int width, int height, float rotationAngle)
    {
        int bitmapID = getResources().getIdentifier(spriteName, "drawable", this.context.getPackageName());
        Bitmap bitmapObject = BitmapFactory.decodeResource(getResources(), bitmapID);
        int bitmapWidth = bitmapObject.getWidth();
        int bitmapHeight = bitmapObject.getHeight();
        bitmapObject = createScaledBitmap(bitmapObject, width, height, false);

        // Calculate scaling factors to fit the arrow within the desired area
        float scaleX = (float) width / bitmapWidth;
        float scaleY = (float) height / bitmapHeight;

        // Create a new Bitmap with the scaled dimensions
        Bitmap rotatedObject = Bitmap.createBitmap((int) (width * scaleX), (int) (height * scaleY), Bitmap.Config.ARGB_8888);

        if(rotationAngle == 180 && (!spriteName.equals("groundFire")))//rotate on x axis
        {
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1, xLocation, yLocation);
            rotatedObject =  Bitmap.createBitmap(rotatedObject, 0, 0, rotatedObject.getWidth(), rotatedObject.getHeight(), matrix, true);
            rotationAngle = 0;
        }

        Canvas rotatedCanvas = new Canvas(rotatedObject);

        // Rotate the Canvas with pivot point at center
        rotatedCanvas.rotate(rotationAngle);

        // Calculate offset based on scaling
        int offsetX = (int) ((bitmapWidth - width) / 2 * scaleX);
        int offsetY = (int) ((bitmapHeight - height) / 2 * scaleY);

        // Draw the original object bitmap onto the rotated Canvas with negative offsets
        rotatedCanvas.drawBitmap(bitmapObject, -offsetX, -offsetY, p);

        // Draw the rotated Bitmap onto the main Canvas at the desired position
        canvas.drawBitmap(rotatedObject, xLocation, yLocation, p);
    }

    public boolean pressedA() {
        if(pictureNum < 8)
        {
            pictureNum++;
            if(pictureNum == 4)//random num, represents difficulty
                return true;
        }
        return false;
    }

    public void longPressedA()
    {
        if(currentRoom != null)
        {
            holdingA = true;
            currentRoom.pressedBox("A");//fake. substitute for pressedA(long)
        }
    }

    public void canceledA()
    {
        if(currentRoom != null)
        {
            holdingA = false;
            currentRoom.pressedBox("A");//fake. substitute for CancelpressedA(long)
        }
    }

    public void pressedB() {

    }

    public void pressedX() {

    }

    public void pressedY() {
    }

    public void pressedLeftOrRight(String direction)
    {

    }

    public void pressedUpOrDown(String direction)
    {

    }

    public void processMovement(float x, float y)
    {

    }

    public void processDirection(float x, float y)
    {

    }

    public void pressedStart()//shows controls
    {

    }
}

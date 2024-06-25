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
import android.view.View;
import android.widget.FrameLayout;
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
    private int classNum;
    Dpad dpad;
    private int subMenu;//work on this
    private boolean subMenuOpen;
    private String className;
    private boolean challengeBackground;
    private boolean difficultyActivityOn;
    private boolean controlsMenu;
    private boolean roomTwilight;
    public GameView(Context context)
    {
        super(context);
        this.context = context;
        pictureNum = 0;
        className = "none";
        difficultyActivityOn = false;
        subMenuOpen = false;
        holdingA = false;
        subMenu = 1;
        controlsMenu = false;
        roomTwilight = false;
        classNum = 1;
        width = this.getResources().getDisplayMetrics().widthPixels;
        pixelWidth = 2106 / width;//in centimeters
        height = this.getResources().getDisplayMetrics().heightPixels;
        pixelHeight = 1080 / height;
        currentRoom = null;
    }

    @Override
    public void run() {
        String name = "background";
        ourHolder = getHolder();
        if((playerUser.getCurrentSection() == (-1)) || (height > width))
        {
            int backgroundOpen = 0;
            while (pictureNum < 20)
            {
                if(!difficultyActivityOn)
                {
                    if(pictureNum == 0)
                    {
                        width = this.getResources().getDisplayMetrics().widthPixels;
                        height = this.getResources().getDisplayMetrics().heightPixels;
                        if(width > height)
                        {
                            pictureNum++;
                            pixelWidth = 2106 / width;
                            pixelHeight = 1080 / height;
                        }
                    }
                    else if(pictureNum == 1)
                    {
                        ArrayList<Integer> gameControllerIds = GameActivity.getGameControllerIds();
                        if(!gameControllerIds.isEmpty())
                            pictureNum++;
                    }
                    else if(playerUser.getCurrentSection() != (-1))
                        pictureNum = 21;
                    else if((pictureNum > 2) && (pictureNum < 7))
                    {
                        backgroundOpen++;
                        if(backgroundOpen >= 10)
                        {
                            backgroundOpen = 0;
                            pictureNum++;
                        }
                    }
                    else if(pictureNum == 18)
                    {
                        switch (classNum)
                        {
                            case 1:
                                className = "Knight";
                                break;
                            case 2:
                                className = "Archer";
                                break;
                            case 3:
                                className = "Mage";
                                break;
                        }
                    }
                    if(ourHolder.getSurface().isValid() && ((pictureNum < 20) && (!difficultyActivityOn)))
                    {
                        canvas = ourHolder.lockCanvas();
                        String sprite = name + pictureNum;
                        if(!className.equals("none"))
                            sprite += className;
                        draw(sprite, 0, 0, (int)width, (int)height, 0);
                        ourHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    gameThread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(playerUser.getCurrentRoom() == 2)
        {
            challengeBackground = true;
            while (challengeBackground)
            {
                if(!difficultyActivityOn)
                {
                    if(ourHolder.getSurface().isValid())
                    {
                        canvas = ourHolder.lockCanvas();
                        String sprite = playerUser.getOrder().get(playerUser.getCurrentSection());
                        sprite += "Challenge";
                        draw(sprite, 0, 0, (int)width, (int)height, 0);
                        ourHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    gameThread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(subMenuOpen)
        {
            while (subMenuOpen)
            {
                if(!difficultyActivityOn)
                {
                    if(ourHolder.getSurface().isValid())
                    {
                        canvas = ourHolder.lockCanvas();
                        String extra = (controlsMenu)? "2": ("Menu" + subMenu);
                        String sprite = "background";
                        sprite += extra;
                        draw(sprite, 0, 0, (int)width, (int)height, 0);
                        ourHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    gameThread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(roomTwilight)
        {
            while (roomTwilight)
            {
                if(!difficultyActivityOn)
                {
                    if(ourHolder.getSurface().isValid())
                    {
                        canvas = ourHolder.lockCanvas();
                        String extra = "Twilight";
                        String sprite = "background";
                        sprite += extra;
                        draw(sprite, 0, 0, (int)width, (int)height, 0);
                        ourHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    gameThread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else
            pictureNum = 21;

        this.currentRoom = new Room(playerUser, this);
        //seperate function for last room(after)
        while (running)
        {
            if(!difficultyActivityOn)
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
            }
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
        if(!loser)
        {
            int roomNum = playerUser.getCurrentRoom();
            int floorNum = playerUser.getCurrentFloor();
            int sectionNum = playerUser.getCurrentSection();
            roomNum++;
            if(roomNum > 3)
            {
                roomNum = 1;
                floorNum++;
            }
            if(floorNum == 4)
                roomNum = 4;
            else if(floorNum == 5)
            {
                sectionNum++;
                floorNum = 1;
                roomNum = 1;
            }
            if(sectionNum == 4)
            {
                floorNum = 4;
                roomNum = 4;
            }
            playerUser.setCurrentRoom(roomNum);
            playerUser.setCurrentFloor(floorNum);
            playerUser.setCurrentSection(sectionNum);
            GameActivity.updateUser(playerUser);
        }
        this.currentRoom = null;
        Room nextRoom = new Room(playerUser, this);
        this.currentRoom = nextRoom;
    }

    public void resetDifficulty(FrameLayout frameLayout)
    {
        boolean inFrame = false;
        while (!inFrame)
        {
            int childCount = frameLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = frameLayout.getChildAt(i);
                if (childView.equals(this)) {
                    inFrame = true;
                }
            }
        }
        if(inFrame)
            difficultyActivityOn = false;
    }

    public void draw(String spriteName, float xLocation, float yLocation, int width, int height, float rotationAngle)
    {
        int bitmapID = getResources().getIdentifier(spriteName, "drawable", this.context.getPackageName());
        Bitmap bitmapObject = BitmapFactory.decodeResource(getResources(), bitmapID);
        int bitmapWidth = bitmapObject.getWidth();
        int bitmapHeight = bitmapObject.getHeight();
        bitmapObject = createScaledBitmap(bitmapObject, width, height, false);

        // Calculate scaling factors to fit the image within the desired area
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

        //Canvas rotatedCanvas = new Canvas(rotatedObject);==original
        Canvas rotatedCanvas = new Canvas(bitmapObject);//works for background

        // Rotate the Canvas with pivot point at center
        rotatedCanvas.rotate(rotationAngle);

        // Calculate offset based on scaling
        int offsetX = (int) ((bitmapWidth - width) / 2 * scaleX);
        int offsetY = (int) ((bitmapHeight - height) / 2 * scaleY);

        if(spriteName.contains("background") || spriteName.contains("challenge"))
        {
            offsetX = 0;
            offsetY = 0;
        }

        // Draw the original object bitmap onto the rotated Canvas with negative offsets
        rotatedCanvas.drawBitmap(bitmapObject, -offsetX, -offsetY, p);

        // Draw the rotated Bitmap onto the main Canvas at the desired position
        //canvas.drawBitmap(rotatedObject, xLocation, yLocation, p);=original
        canvas.drawBitmap(bitmapObject, xLocation, yLocation, p);//works for background
    }

    public boolean pressedA() {
        if((pictureNum == 2) || ((pictureNum > 6) && (pictureNum < 20)))
        {
            if(pictureNum == 18)
            {
                playerUser.setClassName(className);
                GameActivity.updateUser(playerUser);
            }

            pictureNum++;
            return false;
        }
        else if(pictureNum == 20)
        {
            difficultyActivityOn = true;
            pictureNum++;
            return true;
        }
        else if(challengeBackground)
            challengeBackground = false;
        else if(subMenuOpen)
        {
            if(subMenu == 1)
                return true;
            else
                controlsMenu = !controlsMenu;
        }
        else if(roomTwilight)
        {
            roomTwilight = false;
            nextRoom(false);
        }
        else if(currentRoom != null)
            currentRoom.pressedA();
        return false;
    }

    public void longPressedA()
    {
        if(currentRoom != null)
        {
            holdingA = true;
            currentRoom.holdA();
        }
    }

    public void canceledA()
    {
        if(currentRoom != null)
        {
            holdingA = false;
            currentRoom.releaseA();
        }
    }

    public void pressedB() {
        if(currentRoom != null)
            currentRoom.pressedB();
    }

    public void pressedX() {
        if(currentRoom != null)
            currentRoom.pressedX();
    }

    public void pressedY() {
        if(currentRoom != null)
            currentRoom.pressedY();
    }

    public void pressedLeftOrRight(String direction)
    {
        if(pictureNum == 18)
        {
            if(direction.equals("left") && (classNum != 1))
                classNum--;
            else if(direction.equals("right") && (classNum != 3))
                classNum++;
        }
        else if(pictureNum == 19)
        {
            String tower = (direction.equals("left"))? "Knight": "Mage";
            if(playerUser.initializeOrder(tower))
                pictureNum++;
        }
    }

    public void pressedUpOrDown(String direction)
    {
        if((pictureNum == 19) && direction.equals("up"))
            if(playerUser.initializeOrder("Archer"))
                pictureNum++;
    }

    public void processMovement(float x, float y)
    {
        if(currentRoom != null)
            currentRoom.initializeMovement(x, y);
    }

    public void processDirection(float x, float y)
    {
        if(currentRoom != null)
            currentRoom.initializeDirection(x, y);
    }

    public void pressedStart()//shows controls
    {
        if(currentRoom != null)
        {
            if(!subMenuOpen)
            {
                if(currentRoom.pressedStart())
                {
                    roomTwilight = false;
                    subMenuOpen = true;
                }
            }
            else
            {
                controlsMenu = false;
                subMenu = 1;
                subMenuOpen = false;
                roomTwilight = true;
            }
        }
    }

    public void twilight()
    {
        roomTwilight = true;
    }

    public void setFailureDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GAME OVER");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Main Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nextRoom(true);
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}

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
    }

    @Override
    public void run() {
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
        //start the game
    }
    public void resume(){
        gameThread = new Thread(this);
        gameThread.start();
    }
}

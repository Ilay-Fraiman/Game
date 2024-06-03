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
    Thread gameThread;
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
        //check if user started
        //do the extra screens optional
        //then start loop
    }
    public void resume(){
        gameThread = new Thread(this);
        gameThread.start();
    }
}

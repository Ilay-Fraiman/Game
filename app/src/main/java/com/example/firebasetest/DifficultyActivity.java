package com.example.firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class DifficultyActivity extends AppCompatActivity {
    int[] temporaryDifficulties;
    int line;
    boolean selected;
    Intent returnIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//need to set pictures and background as well
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        line = 0;
        selected = false;
        temporaryDifficulties = new int[5];
        Intent intent = getIntent();
        returnIntent = intent;
        loadValues();
    }
    public void loadValues()
    {
        temporaryDifficulties[0] = returnIntent.getIntExtra("difficulty", 3);
        temporaryDifficulties[1] = returnIntent.getIntExtra("enemyDifficulty", 3);
        temporaryDifficulties[2] = returnIntent.getIntExtra("difficultyScaling", 3);
        temporaryDifficulties[3] = returnIntent.getIntExtra("challengeDifficulty", 3);
        temporaryDifficulties[4] = returnIntent.getIntExtra("challengeDifficultyScaling", 3);
        updateDifficulty(line);
    }

    public void pressedA(){
        selected = !selected;
        int color = selected? 3: 2;//selected is 3(white), unselected but looking is 2(light gray)
        updateImage(0, line, color);
    }

    public void pressedBOrX(String button){
        returnIntent.removeExtra("difficulty");
        returnIntent.removeExtra("enemyDifficulty");
        returnIntent.removeExtra("difficultyScaling");
        returnIntent.removeExtra("challengeDifficulty");
        returnIntent.removeExtra("challengeDifficultyScaling");
        int resultCode = Activity.RESULT_CANCELED;

        if(button.equals("X"))
        {
            resultCode = Activity.RESULT_OK;
            returnIntent.putExtra("difficulty", temporaryDifficulties[0]);
            returnIntent.putExtra("enemyDifficulty", temporaryDifficulties[1]);
            returnIntent.putExtra("difficultyScaling", temporaryDifficulties[2]);
            returnIntent.putExtra("challengeDifficulty", temporaryDifficulties[3]);
            returnIntent.putExtra("challengeDifficultyScaling", temporaryDifficulties[4]);
        }

        setResult(resultCode,returnIntent);
        finish();
    }

    public void pressedY(){
        for(int i = 0; i < 5; i++)
            temporaryDifficulties[i] = 3;
        int temporaryLine = line;
        updateDifficulty(temporaryLine);
    }

    public void pressedLeftOrRight(String direction){
        if(selected)
        {
            int multiplication = (direction.equals("right"))? 1 : -1;
            int num = temporaryDifficulties[line] + multiplication;
            if((0 < num) && (num < 6))
            {
                temporaryDifficulties[line] = num;
                if(line == 0)
                {
                    for(int i = 1; i < 5; i++)
                        temporaryDifficulties[i] = num;
                }
                updateDifficulty(line);
            }
        }
    }

    public void pressedUpOrDown(String direction){
        if(!selected)
        {
            int multiplication = (direction.equals("down"))? 1 : -1;
            int num = line + multiplication;
            if((-1 < num) && (num < 5))
            {
                updateImage(0, line, 1);//dark gray, not at this line
                line = num;
                updateImage(0, num, 2);//light gray, looking (num to make sure no mistakes)
            }
        }
    }
    public void updateDifficulty(int temp)
    {
        if(line == 0)
        {
            for(int i = 0; i < 5; i++)
            {
                updateImage(1, i, 0);
            }
        }
        else
        {
            updateImage(1, line, 0);
        }
        line = temp;
    }

    public void updateImage(int type, int index, int color)
    {
        String imgID = "difficultyImg";
        String newIMG = "difficulty";

        if(type == 0)
        {
            imgID = "imgDifficulty";
            newIMG += index;
            newIMG += color;
        }
        else
        {
            newIMG += temporaryDifficulties[index];
        }
        imgID += index;

        int imageID = getResources().getIdentifier(imgID, "id", getPackageName());
        ImageView imageView = (ImageView) findViewById(imageID);
        //String imageFilename = String.format("%s%s%s", newIMG, ".", "png");
        int newImgID = getResources().getIdentifier(newIMG, "drawable", getPackageName());
        imageView.setImageResource(newImgID);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {//stops the run into here if still pressed
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_BUTTON_A:
                        pressedA();
                        break;
                    case KeyEvent.KEYCODE_BUTTON_B:
                        pressedBOrX("B");
                        break;
                    case KeyEvent.KEYCODE_BUTTON_X:
                        pressedBOrX("X");
                        break;
                    case KeyEvent.KEYCODE_BUTTON_Y:
                        pressedY();
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        pressedUpOrDown("up");
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        pressedUpOrDown("down");
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        pressedLeftOrRight("left");
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        pressedLeftOrRight("right");
                        break;
                }
                handled = true;//needs to be for each key code.
            }
            if (handled) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
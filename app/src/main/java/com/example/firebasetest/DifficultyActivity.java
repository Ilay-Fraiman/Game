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
    }

    public void pressedA(){
        selected = !selected;
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
    }

    public void pressedLeftOrRight(String direction){
        if(selected)
        {
            int multiplication = (direction.equals("right"))? 1 : -1;
            int num = temporaryDifficulties[line] + multiplication;
            if((0 < num) && (num < 6))
                temporaryDifficulties[line] = num;
        }
    }

    public void pressedUpOrDown(String direction){
        if(!selected)
        {
            int multiplication = (direction.equals("down"))? 1 : -1;
            int num = line + multiplication;
            if((-1 < num) && (num < 5))
                line = num;
        }
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
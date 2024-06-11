package com.example.firebasetest;

import static com.example.firebasetest.Dpad.DOWN;
import static com.example.firebasetest.Dpad.LEFT;
import static com.example.firebasetest.Dpad.RIGHT;
import static com.example.firebasetest.Dpad.UP;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    GameView gameView;
    ActivityResultLauncher<Intent> mStartForResult;
    int difficulty;
    int enemyDifficulty;
    int difficultyScaling;
    int challengeDifficulty;
    int challengeDifficultyScaling;
    private User playerUser;
    private boolean initializingComplete;
    private boolean resumed;
    Dpad dpad;
    //private int doomsdayClock;int(?) (i dont know if i'm doing this after all)
    // private int suddenDeath;sudden death type (i dont know if i'm doing this after all)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        dpad = new Dpad();
        initializingComplete = false;
        resumed = false;
        gameView = new GameView(this);
        setContentView(gameView);
        ReadDataFromFB();
        //everything else
        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>(){
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    difficultyActivityDone(intent);
                }
            }
        });
    }

    public void difficultyScreen()//may need more functions, or to do more stuff in the function
    {
        //this should probably do more things. like stop threads and stuff
        Intent intent = new Intent(this, DifficultyActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("enemyDifficulty", enemyDifficulty);
        intent.putExtra("difficultyScaling", difficultyScaling);
        intent.putExtra("challengeDifficulty", challengeDifficulty);
        intent.putExtra("challengeDifficultyScaling", challengeDifficultyScaling);
        mStartForResult.launch(intent);
    }

    public void difficultyActivityDone(Intent intent)//seperate function to do everything
    {
        difficulty = intent.getIntExtra("difficulty", difficulty);
        enemyDifficulty = intent.getIntExtra("enemyDifficulty", enemyDifficulty);
        difficultyScaling = intent.getIntExtra("difficultyScaling", difficultyScaling);
        challengeDifficulty = intent.getIntExtra("challengeDifficulty", challengeDifficulty);
        challengeDifficultyScaling = intent.getIntExtra("challengeDifficultyScaling", challengeDifficultyScaling);

        playerUser.setDifficulty(difficulty);
        playerUser.setEnemyDifficulty(enemyDifficulty);
        playerUser.setDifficultyScaling(difficultyScaling);
        playerUser.setChallengeDifficulty(challengeDifficulty);
        playerUser.setChallengeDifficultyScaling(challengeDifficultyScaling);
        updateUser();
        //probably more things to reset
    }

    public void updateUser()
    {
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference ref = fb.collection("Users").document(id);
        ref.set(playerUser);
    }

    public void ReadDataFromFB()
    {
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        fb.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    ArrayList<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot: task.getResult())
                    {
                        users.add(snapshot.toObject(User.class));
                    }
                    for (User user: users)
                    {
                        if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(user.getEmail())) {
                            setPlayerUser(user);
                        }
                    }
                    continueFromAsyncRead();
                }
            }
        });
    }

    private void continueFromAsyncRead() {
        difficulty = playerUser.getDifficulty();
        enemyDifficulty = playerUser.getEnemyDifficulty();
        difficultyScaling = playerUser.getDifficultyScaling();
        challengeDifficulty = playerUser.getChallengeDifficulty();
        challengeDifficultyScaling = playerUser.getChallengeDifficultyScaling();
        //difficultyScreen();
        this.initializingComplete = true;
        if(resumed)
            this.onResume();
    }

    public void setPlayerUser(User user)
    {
        this.playerUser = user;
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
                        pressedB();
                        break;
                    case KeyEvent.KEYCODE_BUTTON_X:
                        pressedX();
                        break;
                    case KeyEvent.KEYCODE_BUTTON_Y:
                        pressedY();
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

    public void pressedA() {

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

    public boolean onGenericMotionEvent(MotionEvent event) {

        // Check that the event came from a game controller
        if (Dpad.isDpadDevice(event)) {

            int press = dpad.getDirectionPressed(event);
            switch (press) {
                case LEFT:
                    pressedLeftOrRight("left");
                    return true;
                case RIGHT:
                    pressedLeftOrRight("right");
                    return true;
                case UP:
                    pressedUpOrDown("up");
                    return true;
                case DOWN:
                    pressedUpOrDown("down");
                    return true;
            }
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        if(initializingComplete)
            gameView.resume(playerUser, dpad);
    }
}
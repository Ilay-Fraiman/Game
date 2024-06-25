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
import android.widget.FrameLayout;

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
    private int difficulty;
    private int enemyDifficulty;
    private int difficultyScaling;
    private int challengeDifficulty;
    private int challengeDifficultyScaling;
    private User playerUser;
    private boolean initializingComplete;
    private boolean resumed;
    private int konamiIndex;
    private boolean aHold;
    private FrameLayout frameLayout;
    private boolean outOfFocus;
    private boolean addedView;
    Dpad dpad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        frameLayout = findViewById(R.id.frmView);
        konamiIndex = 0;
        aHold = false;
        outOfFocus = false;
        addedView = false;
        dpad = new Dpad();
        initializingComplete = false;
        resumed = false;
        gameView = new GameView(this);
        ReadDataFromFB();
        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>(){
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    difficultyActivityDone(intent);
                }
            }
        });
    }

    public void difficultyScreen()
    {
        outOfFocus = true;
        addedView = false;
        frameLayout.removeView(gameView);
        Intent intent = new Intent(this, DifficultyActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("enemyDifficulty", enemyDifficulty);
        intent.putExtra("difficultyScaling", difficultyScaling);
        intent.putExtra("challengeDifficulty", challengeDifficulty);
        intent.putExtra("challengeDifficultyScaling", challengeDifficultyScaling);
        mStartForResult.launch(intent);
    }

    public void difficultyActivityDone(Intent intent)
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
        updateUser(playerUser);
        outOfFocus = false;
        if(!addedView)
            onWindowFocusChanged(true);
    }

    public static void updateUser(User user)
    {
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference ref = fb.collection("Users").document(id);
        ref.set(user);
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
        this.initializingComplete = true;
        if(resumed)
            this.onResume();
    }

    public void setPlayerUser(User user)
    {
        this.playerUser = user;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_BUTTON_A:
                        event.startTracking();
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
                    case KeyEvent.KEYCODE_BUTTON_START:
                        pressedStart();
                        break;
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (keyCode == KeyEvent.KEYCODE_BUTTON_A){
                if(initializingComplete)
                {
                    aHold = true;
                    gameView.longPressedA();
                }
            }
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
                if (keyCode == KeyEvent.KEYCODE_BUTTON_A){
                    if(initializingComplete) {
                        if(aHold)
                            gameView.canceledA();
                        else
                        {
                            if(pressedA())
                                difficultyScreen();
                        }
                    }
                }
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean pressedA()
    {
        if(konamiIndex == 9)
            konamiIndex++;
        if(initializingComplete)
            return gameView.pressedA();
        else
            return false;
    }

    public void pressedB() {
        if(konamiIndex == 8)
            konamiIndex++;
        if (initializingComplete)
            gameView.pressedB();
    }

    public void pressedX() {
        if (initializingComplete)
            gameView.pressedX();
    }

    public void pressedY() {
        if(initializingComplete)
            gameView.pressedY();
    }

    public void pressedLeftOrRight(String direction)
    {
        if(direction.equals("left") && ((konamiIndex == 4) || (konamiIndex == 6)))
            konamiIndex++;
        if(direction.equals("right") && ((konamiIndex == 5) || (konamiIndex == 7)))
            konamiIndex++;
        if (initializingComplete)
            gameView.pressedLeftOrRight(direction);
    }

    public void pressedUpOrDown(String direction)
    {
        if(direction.equals("up") && (konamiIndex < 2))
            konamiIndex++;
        if(direction.equals("down") && ((konamiIndex > 1) && (konamiIndex < 4)))
            konamiIndex++;
        if(initializingComplete)
            gameView.pressedUpOrDown(direction);
    }

    public void pressedStart()
    {
        if(konamiIndex == 10)
        {
            playerUser.godMode();
            updateUser(playerUser);
            konamiIndex = 0;
        }
        if(initializingComplete)
            gameView.pressedStart();
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
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

        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private static float getCenteredAxis(MotionEvent event,
                                         InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private void processJoystickInput(MotionEvent event,
                                      int historyPos) {
        InputDevice inputDevice = event.getDevice();
        float left_x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_X, historyPos);

        float right_x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Z, historyPos);

        float left_y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y, historyPos);

        float right_y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_RZ, historyPos);

        if(initializingComplete)
        {
            gameView.processDirection(right_x, right_y);
            gameView.processMovement(left_x, left_y);
        }
    }

    public static ArrayList<Integer> getGameControllerIds() {
        ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    && ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        return gameControllerDeviceIds;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if((gameView != null) && (((initializingComplete) && (hasFocus)) && ((!addedView) && (!outOfFocus))))
        {
            addedView = true;
            frameLayout.addView(gameView);
            gameView.resetDifficulty(frameLayout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        if(initializingComplete)
        {
            if(!addedView)
            {
                onWindowFocusChanged(true);
                boolean inFrame = false;
                while (!inFrame)
                {
                    int childCount = frameLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childView = frameLayout.getChildAt(i);
                        if (childView.equals(gameView)) {
                            inFrame = true;
                        }
                    }
                }
                gameView.resume(playerUser, dpad);
            }
            else
                gameView.resume(playerUser, dpad);
        }
    }
}
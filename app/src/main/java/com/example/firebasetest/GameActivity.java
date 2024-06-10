package com.example.firebasetest;

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
    //private int doomsdayClock;int(?) (i dont know if i'm doing this after all)
    // private int suddenDeath;sudden death type (i dont know if i'm doing this after all)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameView = new GameView(this);
        setContentView(gameView);
        ReadDataFromFB();
        difficulty = playerUser.getDifficulty();
        enemyDifficulty = playerUser.getEnemyDifficulty();
        difficultyScaling = playerUser.getDifficultyScaling();
        challengeDifficulty = playerUser.getChallengeDifficulty();
        challengeDifficultyScaling = playerUser.getChallengeDifficultyScaling();
        //everything else
        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>(){
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    difficultyActivityDone(intent);
                }
            }
        });
        //difficultyScreen();
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

        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference ref = fb.collection("Users").document(id);
        ref.set(playerUser);
        //probably more things to reset
    }


    public void ReadDataFromFB()//originally this recieved a view, idk why
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
                }
            }
        });
    }

    //after(?) choise of tower

    public void setPlayerUser(User user)
    {
        this.playerUser = user;
    }
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
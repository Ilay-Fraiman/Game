package com.example.firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameView = new GameView(this);
        setContentView(gameView);
    }

    User playerUser;

    public void ReadDataFromFB(View view)
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
                        if(FirebaseAuth.getInstance().getCurrentUser().getEmail() == user.getEmail())
                            playerUser = user;
                    }
                }
            }
        });
    }

    //after(?) choise of tower


    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
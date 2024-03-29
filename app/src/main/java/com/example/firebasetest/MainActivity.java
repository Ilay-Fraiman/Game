package com.example.firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    public static boolean signIn = true;
    public static boolean success = false;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mAuth.getCurrentUser()!=null)
        {
            moveToNextActivity();
        }
    }

    private void moveToNextActivity() {
        if((!MainActivity.signIn)&&(MainActivity.success))
        {
            addUser();
        }
        Intent intent  = new Intent(MainActivity.this,GameActivity.class);
        startActivity(intent);
    }

    public void signUp(View view)
    {
        MainActivity.signIn = false;
        signUpOrIn(view);
    }

    public void signIn(View view)
    {
        signUpOrIn(view);
    }

    private void signUpOrIn(View view) {
        EditText etMail = findViewById(R.id.editTextTextEmailAddress);
        EditText etPassword =findViewById(R.id.editTextTextPassword);

        String email = etMail.getText().toString();
        String password = etPassword.getText().toString();

        OnCompleteListener<AuthResult> authResultOnCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) // register success
                {
                    Toast.makeText(MainActivity.this,"register success",Toast.LENGTH_SHORT).show();
                    // can be done only register SUCCESS!!!
                    MainActivity.success = true;
                    MainActivity.this.moveToNextActivity();
                }
                else // register fail
                {
                    String failureReason = task.getException().toString();
                    Toast.makeText(MainActivity.this,"register failed " + failureReason,Toast.LENGTH_SHORT).show();
                }

            }
        };

        if(!MainActivity.signIn)
        {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authResultOnCompleteListener);
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(authResultOnCompleteListener);
        }


        Toast.makeText(this," after ",Toast.LENGTH_SHORT).show();
    }

    private void addUser()
    {
        String email = mAuth.getCurrentUser().getEmail();
        User user = new User(email);
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference ref = fb.collection("Users").document(uid);
        ref.set(user);
    }
}
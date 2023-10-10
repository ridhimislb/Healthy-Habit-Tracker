package com.example.healthyhabittracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    private Button forgetButton;
    private EditText email;
    private String emailStr;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.forgetEmail);

        forgetButton = findViewById(R.id.gotoLogin);

        progressBar = findViewById(R.id.progress_bar);

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                validateData();
            }
        });
    }

    private void validateData() {
        emailStr = email.getText().toString();

        if(emailStr.isEmpty()){
            email.setError("Required");
            forgetButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
        else{
            forgetPass();
        }
    }

    private void forgetPass() {
        auth.sendPasswordResetEmail(emailStr).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ResetActivity.this, "Check your mail,spam too", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetActivity.this,SignInActivity.class));
                    finish();
                }
                else
                {
                    forgetButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ResetActivity.this, "Error  "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onBackPressed() {
        startActivity(new Intent(ResetActivity.this,SignInActivity.class));
    }
}
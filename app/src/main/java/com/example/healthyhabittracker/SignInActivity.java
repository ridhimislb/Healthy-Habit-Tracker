package com.example.healthyhabittracker;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    private TextView join,resetpassword;
    private EditText aemail,apassword;
    private Button login;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SharedPreferences preferences = getSharedPreferences("PREFERENCE",MODE_PRIVATE);
        String FirstTime = preferences.getString("FirstTimeInstall","");
        String account = preferences.getString("Account","");

        if(FirstTime.equals("Yes"))
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("workCount",0);
            startActivity(intent);
        }

        join = findViewById(R.id.join);
        resetpassword = findViewById(R.id.resetpassword);
        aemail = findViewById(R.id.email);
        apassword = findViewById(R.id.password);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progress_bar);

        auth = FirebaseAuth.getInstance();

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                login.setVisibility(View.INVISIBLE);


                String str_email = aemail.getText().toString();
                String str_password = apassword.getText().toString();

                if(TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password))
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);

                    Toast.makeText(SignInActivity.this, "All fields are required...", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    auth.signInWithEmailAndPassword(str_email,str_password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(auth.getCurrentUser().getUid());

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        if(user.isEmailVerified())
                                        {
                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {

                                                }
                                            });
                                            Toast.makeText(SignInActivity.this, "Please wait loggin in...", Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("FirstTimeInstall", "Yes");
                                            editor.putString("Account","student");
                                            editor.apply();


                                            progressBar.setVisibility(View.INVISIBLE);
                                            login.setVisibility(View.VISIBLE);
                                        }
                                        else
                                        {

                                            progressBar.setVisibility(View.INVISIBLE);
                                            login.setVisibility(View.VISIBLE);
                                            Toast.makeText(SignInActivity.this, "Please verify your mail before logging in...,check spam too", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    else
                                    {

                                        progressBar.setVisibility(View.INVISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                        Toast.makeText(SignInActivity.this, "Authentication failed\nPlease check all fields once.", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                }
            }
        });

        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,ResetActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
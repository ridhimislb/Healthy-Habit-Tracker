package com.example.healthyhabittracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private TextView signin;
    EditText fname,MobileNumber,apassword,aemail;
    private Button register;
    DatabaseReference reference;
    FirebaseAuth auth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fname = findViewById(R.id.fname);
        MobileNumber = findViewById(R.id.MobileNumber);
        apassword = findViewById(R.id.apassword);
        aemail = findViewById(R.id.aemail);
        register = findViewById(R.id.register);
        signin = findViewById(R.id.signin);
        progressBar = findViewById(R.id.progress_bar);

        auth = FirebaseAuth.getInstance();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fname.getText().toString();
                String mnumber = MobileNumber.getText().toString();
                String password = apassword.getText().toString();
                String mail = aemail.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                register.setVisibility(View.INVISIBLE);

                if(TextUtils.isEmpty(name) ||
                        TextUtils.isEmpty(mnumber) ||
                        TextUtils.isEmpty(password) ||
                        TextUtils.isEmpty(mail))
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    register.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(), "All fields are required...", Toast.LENGTH_SHORT).show();
                }
                else if (mnumber.length() != 10 || password.length()<6)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    register.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(), "Once check Mobile Number\nAnd password must have atleast 7 characters", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    register(name,mnumber,password,mail);
                }
            }
        });

    }

    private void register(String name,String mnumber,String password,String mail)
    {
        auth.createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("name",name.toLowerCase());
                            hashMap.put("mnumber",mnumber);
                            hashMap.put("password",password);
                            hashMap.put("mail",mail);
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/healthy-habit-tracker-3a2aa.appspot.com/o/placeholder.jpeg?alt=media&token=da39475f-26b8-4e94-a6fe-05c60cb11182&_gl=1*hj3hyx*_ga*MjE5NTk3Njg2LjE2OTY5MjM3OTM.*_ga_CW55HF8NVT*MTY5NjkyMzc5My4xLjEuMTY5NjkyNjA5OC40OC4wLjA.");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        register.setVisibility(View.VISIBLE);

                                        FirebaseUser user = auth.getCurrentUser();
                                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                Toast.makeText(SignUpActivity.this, "We are unable to send verification to provided mail", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                        Toast.makeText(SignUpActivity.this, "You are sucessfully registered", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(SignUpActivity.this, "Please verify your mail...", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        register.setVisibility(View.VISIBLE);

                                        Toast.makeText(SignUpActivity.this, "We are facing problem\nPlease try again after sometime...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),SignInActivity.class));
    }

}
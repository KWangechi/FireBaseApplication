package com.example.firebaseapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText emailField, usernameField, passwordField;
    private TextView loginTextView;

    //Declare an instance of FireBase Application
    private FirebaseAuth firebaseAuth;
    //Declare an instance of the Firebase Database
    private FirebaseDatabase mDatabase;
    //Declare an instance of Firebase reference
    //A database Inference is a node in our database
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar my_toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(my_toolbar);

        //initialize the values
        emailField = findViewById(R.id.emailField);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginTextView = findViewById(R.id.loginTxtView);
        registerBtn = findViewById(R.id.registerBtn);

        //call the getInstance method on the FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();
        //call the getInstance method on the FirebaseDatabase instance
        mDatabase = FirebaseDatabase.getInstance();
        /*call the getReferences on the FirebaseReference instance and
        create a new child node, in this case Users which will be store registered users
        */

        reference = mDatabase.getReference().child("Users");

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "LOADING..." , Toast.LENGTH_SHORT).show();

                final String username = usernameField.getText().toString().trim();
                final String email = emailField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();

                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) &&
                        !TextUtils.isEmpty(password)){

                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String user_id= firebaseAuth.getCurrentUser().getUid();

                            DatabaseReference current_user_db = reference.child(user_id);
                            current_user_db.child("Username").setValue(username);
                            current_user_db.child("Image").setValue("Default");

                            Toast.makeText(RegisterActivity.this, "Registration successful",
                                    Toast.LENGTH_SHORT).show();

                            Intent profileIntent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(profileIntent);


                        }
                    });


                }

                        else{
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                         }
            }
        });

    }
}

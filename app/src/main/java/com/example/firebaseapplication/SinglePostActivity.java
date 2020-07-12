package com.example.firebaseapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SinglePostActivity extends AppCompatActivity {
private ImageView singleImage;
private TextView singleTitle, singleDesc;
String postKey = null;
private DatabaseReference mDataBase;
private Button deleteBtn;
private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        singleImage = findViewById(R.id.singleImageview);
        singleTitle = findViewById(R.id.singleTitle);
        singleDesc = findViewById(R.id.singleDesc);

        mDataBase = FirebaseDatabase.getInstance().getReference().child("Posts");
        postKey = getIntent().getExtras().getString("PostID");
        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataBase.child(postKey).removeValue();
                Intent mainIntent = new Intent(SinglePostActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

mDataBase.child(postKey).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String post_title= (String) dataSnapshot.child("title").getValue();
        String post_desc= (String) dataSnapshot.child("desc").getValue();
        String post_image= (String) dataSnapshot.child("postImage").getValue();
        String post_id= (String) dataSnapshot.child("uid").getValue();



        singleTitle.setText(post_title);
        singleDesc.setText(post_desc);
        Picasso.with(SinglePostActivity.this).load(post_image).into(singleImage);
        if(mAuth.getInstance().getCurrentUser().getUid().equals(post_id)){
            deleteBtn.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});


    }
}

package com.example.firebaseapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.*;


public class PostActivity extends AppCompatActivity {
private ImageButton imageBtn;
private EditText textTitle;
private EditText textDesc;
private Button postBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabaseUsers;


    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        textTitle = findViewById(R.id.textTitle);
        textDesc = findViewById(R.id.textDesc);
        postBtn = findViewById(R.id.postBtn);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        imageBtn = findViewById(R.id.imgBtn);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PostActivity.this, "POSTING...", Toast.LENGTH_LONG).show();
                final String title = textTitle.getText().toString().trim();
                final String description = textDesc.getText().toString().trim();

                Calendar calendar = Calendar.getInstance();
                final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                final String saveCurrentDate = currentDate.format(calendar.getTime());

                Calendar calendar1 = Calendar.getInstance();
                final SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                final String saveCurrentTime = currentTime.format(calendar1.getTime());

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)){
                    StorageReference postImagePath = mStorageRef.child("post_images").child(uri.getLastPathSegment());
                    postImagePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(taskSnapshot.getMetadata()!= null){
                                if(taskSnapshot.getMetadata().getReference()!= null){
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();

                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String imageUri = uri.toString();
                                            Toast.makeText(PostActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                             final DatabaseReference newPost = databaseReference.push();

                             mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                     newPost.child("title").setValue(title);
                                     newPost.child("description").setValue(description);
                                     newPost.child("postImage").setValue(imageUri);
                                     newPost.child("uid").setValue(currentUser.getUid());
                                     newPost.child("time").setValue(saveCurrentTime);
                                     newPost.child("date").setValue(saveCurrentDate);
                                     newPost.child("profilePhoto").setValue(dataSnapshot.child("profilePhoto").getValue());
                                     newPost.child("displayName").setValue(dataSnapshot.child("displayName").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful()){
                                                 Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                                 startActivity(intent);
                                             }
                                         }
                                     });

                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });

                                        }
                                    });
                }
            }
        }

                        });
}
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            imageBtn.setImageURI(uri);
        }

    }
}





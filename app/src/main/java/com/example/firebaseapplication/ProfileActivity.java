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
import android.widget.Toast;

import com.google.android.gms.common.api.internal.TaskUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
//Declare instances of the photo and username
    private EditText profUserName;
    private Button saveProfile;
    private ImageButton imageButton;

    //Declare an instance of FireBase Application
    private FirebaseAuth firebaseAuth;
    //Declare an instance of Firebase reference
    //A database Inference is a node in our database
    private DatabaseReference reference;
    //declare a storage reference for storing the user images
    private StorageReference storageReference;
    //declare an instance URI for getting images from our phone's storage
    private Uri profileURI = null;
    //declare a static final request code
    private final static int REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        profUserName = findViewById(R.id.profUserName);
        saveProfile = findViewById(R.id.doneBtn);
        imageButton = findViewById(R.id.imagebutton);

        firebaseAuth = FirebaseAuth.getInstance();
        final String userID = firebaseAuth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent, REQUEST_CODE);

            }
        });


        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = profUserName.getText().toString().trim();

                if (!TextUtils.isEmpty(name) && profileURI != null){
                    StorageReference profileImagePath = storageReference.child("profile_images").child(profileURI.getLastPathSegment());
                    profileImagePath.putFile(profileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(taskSnapshot.getMetadata()!= null){
                                if(taskSnapshot.getMetadata().getReference()!= null){
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();

                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String profileImage = uri.toString();
                                            reference.push();
                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                reference.child("displayName").setValue(name);
                                                reference.child("profilePhoto").setValue(profileImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                                            Intent login = new Intent(ProfileActivity.this, LoginActivity.class);
                                                            startActivity(login);

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
        if(requestCode == REQUEST_CODE && requestCode == RESULT_OK){
profileURI = data.getData();
imageButton.setImageURI(profileURI);
        }
    }
}

package com.example.firebaseapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference likesRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Boolean likeChecker = false;
    private FirebaseRecyclerAdapter adapter;
    String currentUserID = null;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
            registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(registerIntent);

        }

    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            updateUI(currentUser);
            adapter.startListening();


        }
    }

    private void updateUI(final FirebaseUser currentUser) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts");

        FirebaseRecyclerOptions<Attic> options = new FirebaseRecyclerOptions.Builder<Attic>().
                setQuery(query, new SnapshotParser<Attic>() {
                    @NonNull
                    @Override
                    public Attic parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Attic((String) snapshot.child("title").getValue(),
                                snapshot.child("description").getValue(),
                                snapshot.child("postImage").getValue(),
                                snapshot.child("displayName").getValue(),
                                snapshot.child("profilePhoto").getValue(),
                                snapshot.child("time").getValue(),
                                snapshot.child("date").getValue());
                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<Attic, AtticViewHolder>(options){
            @NonNull
            @Override
            public AtticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_items, parent,false);
            return new AtticViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AtticViewHolder holder, int position, @NonNull Attic model) {
                final String postKey = getRef(position).getKey();

                holder.setTitle(model.getTitle());
                holder.setDescription(model.getDescription());
                holder.setPostImage(getApplicationContext(), model.getPostImage());
                holder.setUserName(model.getDisplayName());
                holder.setProfilePhoto(getApplicationContext(),model.getProfilePhoto());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setLikeButtonStatus(postKey);


                holder.post_layout.setOnClickListener(new View.OnClickListener(){
                @Override
                    public void onClick(View view){
                    Intent singlePost = new Intent(MainActivity.this, SinglePostActivity.class);
                    singlePost.putExtra("Post ID", postKey);
                    startActivity(singlePost);

                }
                });

                holder.likePostButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        likeChecker = true;

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user != null){
                            currentUserID = user.getUid();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();

                        }

                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(likeChecker.equals(true)){
                                    if(dataSnapshot.child(postKey).hasChild(currentUserID)){
                                        likesRef.child(postKey).child(currentUserID).removeValue();
                                        likeChecker = false;

                                    }
                                    else{
                                        likesRef.child(postKey).child(currentUserID).setValue(true);
                                        likeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });


            }
        };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStop(){
        super.onStop();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            adapter.stopListening();

        }
    }



    public class AtticViewHolder extends RecyclerView.ViewHolder {
        public TextView post_title;
        public TextView post_desc;
        public ImageView post_image;
        public TextView post_userName;
        public ImageView user_image;
        public TextView postTime;
        public TextView postDate;
        public LinearLayout post_layout;
        public ImageButton likePostButton, commentPostButton;
        public TextView displayLikes;

        int countLikes;
        String currentUserID;
        FirebaseAuth mAuth;
        DatabaseReference likesRef;

        public AtticViewHolder(View itemView) {
            super(itemView);
        post_title = itemView.findViewById(R.id.post_title_txtview);
        post_desc = itemView.findViewById(R.id.post_desc_txtview);
            post_image = itemView.findViewById(R.id.post_image);
            post_userName = itemView.findViewById(R.id.post_user);
            user_image = itemView.findViewById(R.id.userImage);
            postTime = itemView.findViewById(R.id.time);
            postDate = itemView.findViewById(R.id.date);
            post_layout = itemView.findViewById(R.id.linear_layout_post);
            likePostButton = itemView.findViewById(R.id.like_button);
            commentPostButton = itemView.findViewById(R.id.comment);
            displayLikes = itemView.findViewById(R.id.likes_display);

            likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        }

        public void setTitle(String title) {
            post_title.setText(title);
        }

        public void setDescription(String description) {
            post_desc.setText(description);
        }

        public void setPostImage(Context applicationContext, String postImage) {
            Picasso.with(applicationContext).load(postImage).into(post_image);
        }

        public void setUserName(String displayName) {
        post_userName.setText(displayName);
        }

        public void setProfilePhoto(Context context, String profilePhoto) {
            Picasso.with(context).load(profilePhoto).into(user_image);

        }

        public void setTime(String time) {
            postTime.setText(time);
        }

        public void setDate(String date) {
            postDate.setText(date);
        }

        public void setLikeButtonStatus(final String postKey) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(currentUser != null){
                currentUserID = currentUser.getUid();
            }

            else{
                Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
            }
    likesRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.child(postKey).hasChild(currentUserID)){
                countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                likePostButton.setImageResource(R.drawable.like);
                displayLikes.setText(Integer.toString(countLikes));
            }
            else{
                countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                likePostButton.setImageResource(R.drawable.dislike);
                displayLikes.setText(Integer.toString(countLikes));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        else if(id == R.id.action_add){
            Intent postIntent = new Intent(this, PostActivity.class);
            startActivity(postIntent);
        }

        else if(id == R.id.logout){
            mAuth.signOut();
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logoutIntent);
            
        }

        return super.onOptionsItemSelected(item);
    }

}


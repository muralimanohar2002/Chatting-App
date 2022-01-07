package com.example.abreak.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;

import com.example.abreak.Adapters.AdapterForUser;
import com.example.abreak.Adapters.StoryAdapter;
import com.example.abreak.ModelClass.status;
import com.example.abreak.ModelClass.stories;
import com.example.abreak.R;
import com.example.abreak.databinding.ActivityMainBinding;
import com.example.abreak.ModelClass.newUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<newUser> users;
    AdapterForUser adapterForUser;
    StoryAdapter storyAdapter;
    ArrayList<stories> newStory;

    newUser person;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Story...");
        dialog.setCancelable(false);

        //...................................................User recyclerview set
        users = new ArrayList<>();
        adapterForUser = new AdapterForUser(this, users);
        binding.recylerview.setAdapter(adapterForUser);

        //........................................Story recycler view set
        newStory = new ArrayList<>();
        storyAdapter = new StoryAdapter(this, newStory);
        binding.storyRecyclerview.setAdapter(storyAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.storyRecyclerview.setLayoutManager(layoutManager);

        //......................................................Database uploading of stories and user profile info
        database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        person = snapshot.getValue(newUser.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        database.getReference().child("STORY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    newStory.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        stories sto = new stories();
                        sto.setName(snapshot1.child("name").getValue(String.class));
                        sto.setpPic(snapshot1.child("pPic").getValue(String.class));
                        sto.setLatestStory(snapshot1.child("latestStory").getValue(Long.class));
                        ArrayList<status> statuses = new ArrayList<>();
                        for(DataSnapshot snapshot2: snapshot1.child("statuses").getChildren()){
                            status state = snapshot2.getValue(status.class);
                            statuses.add(state);
                        }
                        sto.setStatuses(statuses);
                        newStory.add(sto);
                    }
                    storyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    newUser user = snapshot1.getValue(newUser.class);
                    if(!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        users.add(user);
                }
                adapterForUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //..............................................................Bottom item selection for chat and story updating
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.story:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,55);
                }
                return false;
            }
        });
    }

       @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(data.getData() != null){
                Date date = new Date();
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReference().child("Story").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    stories stories = new stories();
                                    stories.setName(person.getName());
                                    stories.setpPic(person.getProfilePicture());
                                    stories.setLatestStory(date.getTime());

                                    HashMap<String, Object> sto = new HashMap<>();
                                    sto.put("name",stories.getName());
                                    sto.put("pPic",stories.getpPic());
                                    sto.put("latestStory",stories.getLatestStory());

                                    status status = new status(stories.getLatestStory(), uri.toString());

                                    database.getReference().child("STORY")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(sto);

                                    database.getReference().child("STORY")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);

                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutop,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
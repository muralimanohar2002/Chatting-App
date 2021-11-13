package com.example.abreak.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.abreak.databinding.ActivityProfileBinding;
import com.example.abreak.ModelClass.newUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class profile extends AppCompatActivity {

    ActivityProfileBinding binding;
    FirebaseAuth fauth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri dp_image;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);


        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fauth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        getSupportActionBar().hide();

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,15);
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = binding.pname.getText().toString();
                if(fname.isEmpty()){
                    binding.pname.setError("Please write your name");
                    return;
                }
                dialog.show();
                if(dp_image!=null){
                    StorageReference reference = storage.getReference().child("Profile").child(fauth.getUid());
                    reference.putFile(dp_image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imgUrl = uri.toString();
                                        String name = binding.pname.getText().toString();
                                        String uid = fauth.getUid();
                                        String phone = fauth.getCurrentUser().getPhoneNumber();

                                        newUser person = new newUser(uid,name,imgUrl,phone);

                                        database.getReference().child("Users").child(uid).setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                                startActivity(new Intent(profile.this,MainActivity.class));
                                                finish();
                                            }
                                        });

                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    String name = binding.pname.getText().toString();
                    String uid = fauth.getUid();
                    String phone = fauth.getCurrentUser().getPhoneNumber();

                    newUser person = new newUser(uid,name,"No Image",phone);

                    database.getReference().child("Users").child(uid).setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            startActivity(new Intent(profile.this,MainActivity.class));
                            finish();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            if(data.getData()!=null){
                binding.imageView.setImageURI(data.getData());
                dp_image = data.getData();
            }
        }
    }
}
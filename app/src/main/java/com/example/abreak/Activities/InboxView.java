package com.example.abreak.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.abreak.Adapters.MessageAdapter;
import com.example.abreak.ModelClass.messageInbox;
import com.example.abreak.databinding.ActivityInboxViewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class InboxView extends AppCompatActivity {

    private ActivityInboxViewBinding binding;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String sender, receiver;
    String receiver_uid;
    ProgressDialog dial;
    String sender_uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboxViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("name");
        receiver_uid = getIntent().getStringExtra("uid");
        sender_uid = FirebaseAuth.getInstance().getUid();

        dial = new ProgressDialog(this);
        dial.setMessage("Sending Image...");
        dial.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        sender = sender_uid + receiver_uid;
        receiver = receiver_uid + sender_uid;

        ArrayList<messageInbox> messages = new ArrayList<>();
        MessageAdapter messageAdapter = new MessageAdapter(this, messages, sender, receiver);

        binding.inboxRecyclerview.setAdapter(messageAdapter);
        binding.inboxRecyclerview.setLayoutManager(new LinearLayoutManager(this));



        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat = binding.messagebox.getText().toString();
                if (TextUtils.isEmpty(chat)){
                    Toast.makeText(InboxView.this, "Enter something to send..", Toast.LENGTH_SHORT).show();
                }else{
                    Date date = new Date();
                    messageInbox message = new messageInbox(sender_uid, chat, date.getTime());
                    binding.messagebox.setText("");

                    database.getReference().child("chats")
                            .child(sender)
                            .child("messages").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            messages.clear();
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                messageInbox newChat = snapshot1.getValue(messageInbox.class);
                                newChat.setMessageId(snapshot1.getKey());
                                messages.add(newChat);
                            }
                            messageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    String key = database.getReference().push().getKey();
                    HashMap<String, Object> recentMsg = new HashMap<>();
                    recentMsg.put("recentmsg", message.getMessage());
                    recentMsg.put("recentmsgTime", date.getTime());

                    database.getReference().child("chats").child(sender).updateChildren(recentMsg);
                    database.getReference().child("chats").child(receiver).updateChildren(recentMsg);
                    database.getReference().child("chats")
                            .child(sender)
                            .child("messages")
                            .child(key)
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference().child("chats")
                                    .child(receiver)
                                    .child("messages")
                                    .child(key)
                                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void bVoid) {

                                }
                            });

                        }
                    });
                }
            }
        });

        binding.attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 30);
            }
        });



        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==30){
            if(data!=null){
                if(data.getData()!=null){
                    Uri chosenImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("Image-chats").child(calendar.getTimeInMillis() + "");
                    dial.show();
                    reference.putFile(chosenImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dial.dismiss();
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imagePath = uri.toString();

                                        String chat = binding.messagebox.getText().toString();
                                        Date date = new Date();
                                        messageInbox message = new messageInbox(sender_uid, chat, date.getTime());
                                        message.setMessage("Photo");
                                        message.setImageUrl(imagePath);
                                        binding.messagebox.setText("");

                                        String key = database.getReference().push().getKey();
                                        HashMap<String, Object> recentMsg = new HashMap<>();
                                        recentMsg.put("recentmsg", message.getMessage());
                                        recentMsg.put("recentmsgTime", date.getTime());

                                        database.getReference().child("chats").child(sender).updateChildren(recentMsg);
                                        database.getReference().child("chats").child(receiver).updateChildren(recentMsg);
                                        database.getReference().child("chats")
                                                .child(sender)
                                                .child("messages")
                                                .child(key)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                database.getReference().child("chats")
                                                        .child(receiver)
                                                        .child("messages")
                                                        .child(key)
                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void bVoid) {

                                                    }
                                                });

                                            }
                                        });
                                        Toast.makeText(InboxView.this, imagePath, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
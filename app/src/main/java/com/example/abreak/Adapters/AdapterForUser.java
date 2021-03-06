package com.example.abreak.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.abreak.Activities.InboxView;
import com.example.abreak.R;
import com.example.abreak.databinding.ChatsrowBinding;
import com.example.abreak.ModelClass.newUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AdapterForUser extends RecyclerView.Adapter<AdapterForUser.friendViewHolder> {

    Context context;
    ArrayList<newUser> users;

    public AdapterForUser(Context context, ArrayList<newUser> users){
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public friendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chatsrow, parent, false);
        return new friendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull friendViewHolder holder, int position) {
        newUser person = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();
        String sender = senderId + person.getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(sender)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            String recentmsg = snapshot.child("recentmsg").getValue(String.class);
                            long  recentmsgTime = snapshot.child("recentmsgTime").getValue(Long.class);

                            holder.binding.convo.setText(recentmsg);
                        }
                        else holder.binding.convo.setText("Tap to chat");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.friend.setText(person.getName());
        Glide.with(context).load(person.getProfilePicture()).placeholder(R.drawable.profilepic).into(holder.binding.dp);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InboxView.class);
                intent.putExtra("name", person.getName());
                intent.putExtra("uid", person.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return users.size();
    }

    public static class friendViewHolder extends RecyclerView.ViewHolder{

        ChatsrowBinding binding;
        public friendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatsrowBinding.bind(itemView);
        }
    }
}

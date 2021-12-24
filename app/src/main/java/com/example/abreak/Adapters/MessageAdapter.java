package com.example.abreak.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.abreak.ModelClass.messageInbox;
import com.example.abreak.R;
import com.example.abreak.databinding.RecievedChatLayoutBinding;
import com.example.abreak.databinding.SentChatLayoutBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<messageInbox> messageInboxes;

    String sender,receiver;
    final int SENT = 1, RECEIVE = 2;
    public MessageAdapter(Context context, ArrayList<messageInbox> messageInboxes, String sender, String receiver){
        this.context = context;
        this.messageInboxes = messageInboxes;
        this.sender = sender;
        this.receiver = receiver;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.sent_chat_layout, parent, false);
            return new sentViewHolder(view);
        }
        else if(viewType == RECEIVE){
            View view = LayoutInflater.from(context).inflate(R.layout.recieved_chat_layout, parent, false);
            return new receiveViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        messageInbox message = messageInboxes.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return SENT;
        }
        else{
            return RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        messageInbox newMessage = messageInboxes.get(position);
        int reactions[] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (positioning) -> {
            if(holder.getClass() == sentViewHolder.class){
                sentViewHolder viewHolder = (sentViewHolder)holder;
                if(positioning>=0) {
                    viewHolder.binding.feelingFromSender.setImageResource(reactions[positioning]);
                    viewHolder.binding.feelingFromSender.setVisibility(View.VISIBLE);
                }
            }
            else{
                receiveViewHolder viewHolder = (receiveViewHolder)holder;
                if(positioning>=0) {
                    viewHolder.binding.feelingFrom.setImageResource(reactions[positioning]);
                    viewHolder.binding.feelingFrom.setVisibility(View.VISIBLE);
                }
            }

            newMessage.setFeelings(positioning);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(sender)
                    .child("messages")
                    .child(newMessage.getMessageId())
                    .setValue(newMessage);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiver)
                    .child("messages")
                    .child(newMessage.getMessageId())
                    .setValue(newMessage);


            return true; // true is closing popup, false is requesting a new selection
        });




        if(holder.getClass() == sentViewHolder.class){
            sentViewHolder viewHolder = (sentViewHolder)holder;

            if(newMessage.getMessage().equals("Photo")){
                viewHolder.binding.sentImage.setVisibility(View.VISIBLE);
                viewHolder.binding.messageSent.setVisibility(View.GONE);
                Glide.with(context).load(newMessage.getImageUrl()).placeholder(R.drawable.googleg_disabled_color_18).into(viewHolder.binding.sentImage);
            }

            viewHolder.binding.messageSent.setText(newMessage.getMessage());
            if(newMessage.getFeelings() >=0){
                //newMessage.setFeelings(reactions[(int)newMessage.getFeelings()]);
                viewHolder.binding.feelingFromSender.setImageResource(reactions[(int)newMessage.getFeelings()]);
                viewHolder.binding.feelingFromSender.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.binding.feelingFromSender.setVisibility(View.GONE);
            }
            viewHolder.binding.messageSent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
            viewHolder.binding.sentImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
        else{
            receiveViewHolder viewHolder = (receiveViewHolder)holder;

            if(newMessage.getMessage().equals("Photo")){
                viewHolder.binding.recievedImage.setVisibility(View.VISIBLE);
                viewHolder.binding.messageRecieve.setVisibility(View.GONE);
                Glide.with(context).load(newMessage.getImageUrl()).placeholder(R.drawable.googleg_disabled_color_18).into(viewHolder.binding.recievedImage);
            }

            viewHolder.binding.messageRecieve.setText(newMessage.getMessage());
            if(newMessage.getFeelings() >=0){
                //newMessage.setFeelings(reactions[(int)newMessage.getFeelings()]);
                viewHolder.binding.feelingFrom.setImageResource(reactions[(int)newMessage.getFeelings()]);
                viewHolder.binding.feelingFrom.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.binding.feelingFrom.setVisibility(View.GONE);
            }
            viewHolder.binding.messageRecieve.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
            viewHolder.binding.recievedImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageInboxes.size();
    }

    public static class sentViewHolder extends RecyclerView.ViewHolder{
        SentChatLayoutBinding binding;
        public sentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SentChatLayoutBinding.bind(itemView);
        }
    }

    public static class receiveViewHolder extends RecyclerView.ViewHolder{
        RecievedChatLayoutBinding binding;
        public receiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecievedChatLayoutBinding.bind(itemView);
        }
    }
}

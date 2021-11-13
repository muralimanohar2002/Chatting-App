package com.example.abreak.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.abreak.Activities.MainActivity;
import com.example.abreak.ModelClass.status;
import com.example.abreak.ModelClass.stories;
import com.example.abreak.R;
import com.example.abreak.databinding.StoryBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.statusViewHolder> {

    Context context;
    ArrayList<stories> userstories;

    public StoryAdapter(Context context, ArrayList<stories> userstories){
        this.context = context;
        this.userstories = userstories;
    }

    @NonNull
    @Override
    public statusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story, parent, false);
        return new statusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull statusViewHolder holder, int position) {
        stories tales = userstories.get(position);

        status lastStory = tales.getStatuses().get(tales.getStatuses().size() - 1);

        Glide.with(context).load(lastStory.getImgUrl()).into(holder.binding.storydp);

        holder.binding.circularStatusView.setPortionsCount(tales.getStatuses().size());

        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(status status : tales.getStatuses()){
                    myStories.add(new MyStory(status.getImgUrl()));
                }
                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(tales.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(tales.getpPic()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userstories.size();
    }



    public static class statusViewHolder extends RecyclerView.ViewHolder{

        StoryBinding binding;
        public statusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StoryBinding.bind(itemView);
        }
    }


}

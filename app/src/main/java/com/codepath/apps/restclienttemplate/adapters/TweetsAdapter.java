package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.DetailActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    // Pass in the context and lis of tweets
    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(TimelineActivity context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based in the position of the element
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list){
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    // Define a viewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTweetBinding binding;

        public ViewHolder(@NonNull @NotNull final View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //We need the position to know wich item was clicked
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION ){
                        // Get the tweet in that position
                        Tweet tweet = tweets.get(position);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("Tweet", Parcels.wrap(tweet));
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context, "Something is going wrong, choose another tweet", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            binding.tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //We need the position to know wich item was clicked
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION ){
                        // Get the tweet in that position
                        Tweet tweet = tweets.get(position);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("Tweet", Parcels.wrap(tweet));
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context, "Something is going wrong, choose another tweet", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        public void bind(Tweet tweet) {
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText(tweet.user.screenName);
            Glide.with(context).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
            binding.tvRTime.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
            int radius = 30; //corner radius, higher value = more rounded
            int margin = 10; //crop margin, set to 0 for corners with no crop
            String imageURL = tweet.media.getUrlMedia();
            if(!imageURL.equals("")){
                Log.d("MAGEN", imageURL +" "+ tweet.user.profileImageUrl);
                Glide.with(context).load(imageURL).into(binding.ivMedia);
                //.transform(new RoundedCornersTransformation(radius, margin))

                // binding.ivMedia.setMinimumHeight(300);
            }else{
                Glide.with(context).load("").into(binding.ivMedia);
            }


        }
    }
}

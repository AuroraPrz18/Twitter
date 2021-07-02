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
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.codepath.apps.restclienttemplate.ComposeActivity;
import com.codepath.apps.restclienttemplate.DetailActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    // Pass in the context and lis of tweets
    Context context;
    List<Tweet> tweets;
    public final int REQUEST_CODE = 20; // NOte: This can be any value and is used to determine the result type later

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
                    //We need the position to know which item was clicked
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

        public void bind(final Tweet tweet) {
            binding.tvUserName.setText("@"+tweet.user.screenName);
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText(tweet.user.name);

            binding.tvRetweetCount.setText(tweet.retweetCount+"");
            binding.tvLikeCount.setText(tweet.favoriteCount+"");

            if(tweet.favorited){
                binding.ibLike.setImageResource(R.drawable.ic_vector_heart);
            }else{
                binding.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            }

            if(tweet.retweeted){
                binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
            }else{
                binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }


            Glide.with(context).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
            binding.tvRTime.setText("· "+tweet.getRelativeTimeAgo(tweet.createdAt));
            String imageURL = tweet.media.getUrlMedia();
            if(!imageURL.equals("")){
                int radius = 30; //corner radius, higher value = more rounded
                int margin = 10; //crop margin, set to 0 for corners with no crop

                MultiTransformation multiLeft = new MultiTransformation(
                        new FitCenter(),
                        new RoundedCornersTransformation(radius, margin, RoundedCornersTransformation.CornerType.ALL));

                Glide.with(context)
                        .load(imageURL)
                        .apply(bitmapTransform(multiLeft))
                        .into(binding.ivMedia);

                //Glide.with(context).load(imageURL).transform(new RoundedCornersTransformation(radius, margin)).into(binding.ivMedia);
            }else{
                Glide.with(context).load("").into(binding.ivMedia);
            }
            binding.icComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Compose icon has been selected
                    // Navigate to the compose activity
                    Intent intent = new Intent(context, ComposeActivity.class);
                    intent.putExtra("Tweet", Parcels.wrap(tweet));
                    //context.startActivityForResult(intent, REQUEST_CODE);
                    context.startActivity(intent);
                    
                }
            });

            binding.ibLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    TwitterClient client = TwitterApp.getRestClient(context); //Return an instance of the Twitter client
                    final String TAG = "like";
                    if(!tweet.favorited){
                        // Call the API method using our client to like it
                        client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess " + json.toString());
                                JSONArray result = json.jsonArray;
                                tweet.favorited = true;
                                binding.ibLike.setImageResource(R.drawable.ic_vector_heart);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure", throwable);
                            }
                        });
                    }else{
                        // Call the API method using our client to not like it
                        client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess " + json.toString());
                                JSONArray result = json.jsonArray;
                                tweet.favorited = false;
                                binding.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure", throwable);
                            }
                        });
                    }



                }
            });

        }
    }
}

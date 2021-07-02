package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
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
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the tweet in that position
                        Tweet tweet = tweets.get(position);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("Tweet", Parcels.wrap(tweet));
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Something is going wrong, choose another tweet", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            binding.tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //We need the position to know wich item was clicked
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the tweet in that position
                        Tweet tweet = tweets.get(position);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("Tweet", Parcels.wrap(tweet));
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Something is going wrong, choose another tweet", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        public void bind(final Tweet tweet) {

            // -------------- User info
            binding.tvUserName.setText("@" + tweet.user.screenName);
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText(tweet.user.name);
            binding.tvRTime.setText("· " + tweet.getRelativeTimeAgo(tweet.createdAt));

            // -------------- Tweet info
            binding.tvRetweetCount.setText(tweet.retweetCount + "");
            binding.tvLikeCount.setText(tweet.favoriteCount + "");

            // Determine if it was liked by the user
            if (tweet.favorited) {
                binding.ibLike.setImageResource(R.drawable.ic_vector_heart);
            } else {
                binding.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            }
            // Determine if it was retweeted by the user
            if (tweet.retweeted) {
                binding.ibRetweet.setImageResource(R.drawable.ic_retweet_ready);
            } else {
                binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }

            // In case that the tweet contains an image, it should show it
            Glide.with(context).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
            String imageURL = tweet.media.getUrlMedia();
            if (!imageURL.equals("")) {
                int radius = 30; //corner radius, higher value = more rounded
                int margin = 10; //crop margin, set to 0 for corners with no crop
                // To round the corners
                MultiTransformation multiLeft = new MultiTransformation(
                        new FitCenter(),
                        new RoundedCornersTransformation(radius, margin, RoundedCornersTransformation.CornerType.ALL));
                // Load the image with Glide
                Glide.with(context)
                        .load(imageURL)
                        .apply(bitmapTransform(multiLeft))
                        .into(binding.ivMedia);
            } else {
                Glide.with(context).load("").into(binding.ivMedia);
            }

            // -------------- Set listeners (on click icons)
            setListenersForEachItem(tweet);
        }

        private void setListenersForEachItem(final Tweet tweet) {

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
                    //Delegate the work to Tweet
                    if (!tweet.favorited) {
                        Tweet.onLikeListener(context, tweet);
                        binding.ibLike.setImageResource(R.drawable.ic_vector_heart);
                        binding.tvLikeCount.setText(tweet.favoriteCount + 1 + "");
                    } else {
                        Tweet.onUnlikeListener(context, tweet);
                        binding.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                        binding.tvLikeCount.setText(tweet.favoriteCount - 1 + "");
                    }

                }
            });

            binding.ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Delegate the work to Tweet
                    if (!tweet.retweeted) {
                        Tweet.onRetweetListener(context, tweet);
                        binding.ibRetweet.setImageResource(R.drawable.ic_retweet_ready);
                        binding.tvRetweetCount.setText(tweet.retweetCount + 1 + "");
                    } else {
                        Tweet.onUnretweetListener(context, tweet);
                        binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                        binding.tvRetweetCount.setText(tweet.retweetCount - 1 + "");
                    }
                }
            });
        }
    }
}

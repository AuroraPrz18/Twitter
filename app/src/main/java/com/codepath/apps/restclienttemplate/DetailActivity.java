package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {
    Tweet tweet;
    Context context;
    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        if (getIntent() != null) {
            tweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));
            binding.tvUserName.setText("@" + tweet.user.screenName);
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText(tweet.user.name);
            Glide.with(this).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);

            binding.tvRetweetCount.setText(tweet.retweetCount + "");
            binding.tvLikeCount.setText(tweet.favoriteCount + "");

            // Get the date of the tweet, to do it, it should be parced to the local time zone
            binding.tvRTime.setText(Tweet.getDate(tweet));

            String imageURL = tweet.media.getUrlMedia();
            if (!imageURL.equals("")) {
                Glide.with(this).load(imageURL).into(binding.ivMedia);
            } else {
                Glide.with(this).load("").into(binding.ivMedia);
            }

            if (tweet.favorited) {
                binding.ibLike.setImageResource(R.drawable.ic_vector_heart);
            } else {
                binding.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            }

            if (tweet.retweeted) {
                binding.ibRetweet.setImageResource(R.drawable.ic_retweet_ready);
            } else {
                binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }

            binding.ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Delegate the work to Tweet
                    if (!tweet.favorited) {
                        Tweet.onLikeListener(getBaseContext(), tweet);
                        binding.ibLike.setImageResource(R.drawable.ic_vector_heart);
                        binding.tvLikeCount.setText(tweet.favoriteCount + 1 + "");
                    } else {
                        Tweet.onUnlikeListener(getBaseContext(), tweet);
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
                        Tweet.onRetweetListener(getBaseContext(), tweet);
                        binding.ibRetweet.setImageResource(R.drawable.ic_retweet_ready);
                        binding.tvRetweetCount.setText(tweet.retweetCount + 1 + "");
                    } else {
                        Tweet.onUnretweetListener(getBaseContext(), tweet);
                        binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                        binding.tvRetweetCount.setText(tweet.retweetCount - 1 + "");
                    }
                }
            });

            binding.icComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Compose icon has been selected
                    // Navigate to the compose activity
                    Intent intent = new Intent(context, ComposeActivity.class);
                    intent.putExtra("Tweet", Parcels.wrap(tweet));
                    //context.startActivityForResult(intent, REQUEST_CODE);
                    startActivity(intent);

                }
            });
        }


    }
}
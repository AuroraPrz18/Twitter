package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {
    Tweet tweet;
    ActivityDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getIntent() != null){
            tweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));
            binding.tvUserName.setText("@"+tweet.user.screenName);
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText(tweet.user.name);
            Glide.with(this).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);

            binding.tvRetweetCount.setText(tweet.retweetCount+"");
            binding.tvLikeCount.setText(tweet.favoriteCount+"");

            // Get the date of the tweet, to do it, it should be parced to the local time zone
            binding.tvRTime.setText(Tweet.getDate(tweet));

            String imageURL = tweet.media.getUrlMedia();
            if(!imageURL.equals("")){
                Glide.with(this).load(imageURL).into(binding.ivMedia);
            }else{
                Glide.with(this).load("").into(binding.ivMedia);
            }
        }


    }
}
package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;

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
            // Next line should be more efficient if I make it be in the format of each locale, with an library, if I have time I'm going to try it
            Log.d("TIME", tweet.createdAt);
            binding.tvRTime.setText(tweet.createdAt.substring(11,16) + " · "+ tweet.createdAt.substring(4,10) + ", " + tweet.createdAt.substring(25)); // ??????????????
            String imageURL = tweet.media.getUrlMedia();
            if(!imageURL.equals("")){
                Glide.with(this).load(imageURL).into(binding.ivMedia);
            }else{
                Glide.with(this).load("").into(binding.ivMedia);
            }
        }


    }
}
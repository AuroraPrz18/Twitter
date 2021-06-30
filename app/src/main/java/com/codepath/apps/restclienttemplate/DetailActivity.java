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
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText(tweet.user.screenName);
            Glide.with(this).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
            binding.tvRTime.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
            int radius = 30; //corner radius, higher value = more rounded
            int margin = 10; //crop margin, set to 0 for corners with no crop
            String imageURL = tweet.media.getUrlMedia();
            if(!imageURL.equals("")){
                Log.d("MAGEN", imageURL +" "+ tweet.user.profileImageUrl);
                Glide.with(this).load(imageURL).into(binding.ivMedia);
                //.transform(new RoundedCornersTransformation(radius, margin))

                // binding.ivMedia.setMinimumHeight(300);
            }else{
                Glide.with(this).load("").into(binding.ivMedia);
            }
        }


    }
}
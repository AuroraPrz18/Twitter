package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.parceler.Parcels;

import okhttp3.Headers;

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

            if(tweet.favorited){
                binding.ibLike.setImageResource(R.drawable.ic_vector_heart);
            }else{
                binding.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            }

            if(tweet.retweeted){
                binding.ibRetweet.setImageResource(R.drawable.ic_retweet_ready);
            }else{
                binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }

            binding.ibLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    TwitterClient client = TwitterApp.getRestClient(getBaseContext()); //Return an instance of the Twitter client
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
            binding.ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TwitterClient client = TwitterApp.getRestClient(getBaseContext()); //Return an instance of the Twitter client
                    final String TAG = "retweet";
                    if(!tweet.retweeted){
                        // Call the API method using our client to like it
                        client.retweetTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess " + json.toString());
                                JSONArray result = json.jsonArray;
                                tweet.retweeted = true;
                                binding.ibRetweet.setImageResource(R.drawable.ic_retweet_ready);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure", throwable);
                            }
                        });
                    }else{
                        // Call the API method using our client to not like it
                        client.unretweetTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess " + json.toString());
                                JSONArray result = json.jsonArray;
                                tweet.retweeted  = false;
                                binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
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
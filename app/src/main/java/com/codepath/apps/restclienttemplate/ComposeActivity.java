package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;
    private Tweet tweet;

    ActivityComposeBinding binding;
    TwitterClient client; // Reference to twitter client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = TwitterApp.getRestClient(this); //Return an instance of the Twitter client

        // Define if it is a reply or not
        if(getIntent()!=null && getIntent().hasExtra("Tweet")){
            tweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));
            binding.tvReplyTo.setText("Reply to @"+tweet.user.screenName);
        }else{
            tweet = null;
        }

        //Set the counter Max length to the tweet
        binding.etComposeParent.setCounterMaxLength(MAX_TWEET_LENGTH);

        // Set click listener on button
        binding.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onClickSumbit();   }
        });

    }

    private void onClickSumbit() {
        String tweetContent = binding.etCompose.getText().toString();
        if(tweetContent.isEmpty()){
            Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        if(tweetContent.length() > MAX_TWEET_LENGTH) {
            Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
            return;
        }
        if(tweet == null){
            // Make an API call to Twitter to publish the tweet, sending the content to the publishTweet method
            client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    onSuccessTweet(json);
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure to publish tweet", throwable);
                }
            });
        }else{
            // Make an API call to Twitter to publish the tweet as a reply, sending the content to the publishTweet method
            client.replyTweet(tweetContent, tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    onSuccessTweet(json);
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure to publish and reply tweet", throwable);
                }
            });
        }

    }

    private void onSuccessTweet(JsonHttpResponseHandler.JSON json) {
        Log.i(TAG, "onSuccess to publish tweet");
        try{
            Tweet tweetNew = Tweet.fromJson(json.jsonObject);
            Log.i(TAG, "Published tweet says: " + tweet.body);
            Intent intent = new Intent();
            // Pass the data that the parent activity is waiting for with its result code
            intent.putExtra("tweet", Parcels.wrap(tweetNew));
            setResult(RESULT_OK, intent);
            // Close the activity, pass data to parent
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
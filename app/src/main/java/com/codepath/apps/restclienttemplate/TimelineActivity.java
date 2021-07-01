package com.codepath.apps.restclienttemplate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimeLineActivity";
    public final int REQUEST_CODE = 20; // NOte: This can be any value and is used to determine the result type later

    ActivityTimelineBinding binding;
    MenuItem pbIndicator;
    TwitterClient client;
    List <Tweet> tweets;
    TweetsAdapter adapter;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        client = TwitterApp.getRestClient(this); //Return an instance of the Twitter client

        // Scheme colors for animation
        binding.swipeContainer.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        // Add an onRefreshListener to the swipeContainer to refresh the data
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Show progress item
                pbIndicator.setVisible(true);
                // To keep animation for 3 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Stop animation (This will be after 3 seconds)
                        binding.swipeContainer.setRefreshing(false);
                    }
                }, 3000);
                populateHomeTimeLine();
            }
        });

        // Init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        // Recycler view setup: layout manager and adapter
        binding.rvTweets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTweets.setAdapter(adapter);

        populateHomeTimeLine();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout){
            onClickLogout();
            return true; // true because we want to consume the processing here
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateHomeTimeLine() {
        // Show progress item
        if(pbIndicator!=null){
            pbIndicator.setVisible(true);
        }
        // Call the API method using our client
        client.getHomeTimeLine(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess " + json.toString());
                JSONArray result = json.jsonArray;
                try {
                    // Clear out items before appending in the new ones
                    adapter.clear();
                    // Add new items to your adapter
                    adapter.addAll(Tweet.fromJsonArray(result));
                    // Call setRefreshing (false) to signal refresh has finished
                    binding.swipeContainer.setRefreshing(false);
                    // Hide progress item
                    pbIndicator.setVisible(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });

    }

    public void onClickLogout() {
        client.clearAccessToken(); // forget who'' logged in

        // navigate backwards to Login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);

        finish(); // navigate backwards to Login
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        //Check if the operation has succeded
        if( requestCode == REQUEST_CODE && resultCode== RESULT_OK){
            // Show progress item
            pbIndicator.setVisible(true);
            // Get data from the intent (get intent)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Update the RV with this tweet
            // Modify data source of tweets
            tweets.add(0, tweet);
            // Update the adapter notifying it that an item has been inserted
            adapter.notifyItemInserted(0);
            // Scroll to the very top of the RV
            binding.rvTweets.smoothScrollToPosition(0);
            pbIndicator.setVisible(false);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        pbIndicator = menu.findItem(R.id.pbIndicatorInMenu);
        pbIndicator.setActionView(R.layout.action_view_progress);
        pbIndicator = menu.findItem(R.id.pbIndicatorInMenu);
        return super.onPrepareOptionsMenu(menu);
    }

    public void onClickCompose(View view) {
        // Compose icon has been selected
        // Navigate to the compose activity
        Intent intent = new Intent(this, ComposeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
}
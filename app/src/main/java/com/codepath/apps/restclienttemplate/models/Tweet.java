package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

@Parcel
public class Tweet {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public String id;
    public String body;
    public String createdAt;
    public User user;
    public Media media;
    public int retweetCount;
    public int favoriteCount;
    public int replyCount;
    public boolean retweeted;
    public boolean favorited;

    // Empty constructor needed by the Parceler library
    public Tweet() {
    }

    // Method to build the tweet as per the fields in the JSON object
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.id = jsonObject.getString("id_str");
        if (jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");
        //tweet.replyCount= jsonObject.getInt("reply_count");
        //We need to do this because each tweet has an user (who is another JSONObject), with a lot of info in it
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities.has("media")) {
            JSONObject media = (JSONObject) entities.getJSONArray("media").get(0);
            JSONObject sizes = media.getJSONObject("sizes").getJSONObject("thumb");
            //if(media.getString("type").equals("photo")){ // If it has a photo
            tweet.media = new Media(media.getString("media_url_https"), sizes.getInt("h"), sizes.getInt("h"));
            /*}else{
                tweet.media = new Media("");
            }*/

        } else {
            tweet.media = new Media("");
        }
        return tweet;
    }


    // Method to get the relative time ago for each tweet
    public String getRelativeTimeAgo(String tweetDate) {
        // Convert string into date for the given locale
        SimpleDateFormat simpleFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        // Set date/time parsing to be lenient
        simpleFormat.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = simpleFormat.parse(tweetDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

            long time = simpleFormat.parse(tweetDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return diff / SECOND_MILLIS + "s";
            } else if (diff < 60 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h";
            } else if (diff < 7 * 24 * HOUR_MILLIS) {
                return diff / DAY_MILLIS + "d";
            } else {
                String date = tweetDate.substring(8, 9) + " " + tweetDate.substring(4, 6) + ".";
                if (date.charAt(0) == '0') {
                    date = date.substring(1);
                }
                return date;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "· " + relativeDate;
    }

    // Method that returns the date in the local time zone
    public static String getDate(Tweet tweet) {
        String date = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
            Date parcedDate = format.parse(tweet.createdAt);
            String parced = parcedDate.toString();
            date = parced.substring(11, 16) + " · " + parced.substring(4, 10) + ", " + parced.substring(25);

        } catch (ParseException e) {
            date = "";
        } finally {
            return date;
        }
    }

    //Method to return a list of tweet object from a JSON array
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static void onLikeListener(Context context, final Tweet tweet) {
        TwitterClient client = TwitterApp.getRestClient(context); //Return an instance of the Twitter client
        final String TAG = "like";
        // Call the API method using our client to like it
        client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess " + json.toString());
                JSONArray result = json.jsonArray;
                tweet.favorited = true;
                tweet.favoriteCount++;

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });
    }

    public static void onUnlikeListener(Context context, final Tweet tweet) {
        TwitterClient client = TwitterApp.getRestClient(context); //Return an instance of the Twitter client
        final String TAG = "like";
        // Call the API method using our client to not like it
        client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess " + json.toString());
                JSONArray result = json.jsonArray;
                tweet.favorited = false;
                tweet.favoriteCount--;
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });
    }

    public static void onRetweetListener(Context context, final Tweet tweet) {
        TwitterClient client = TwitterApp.getRestClient(context); //Return an instance of the Twitter client
        final String TAG = "retweet";
        // Call the API method using our client to like it
        client.retweetTweet(tweet.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess " + json.toString());
                JSONArray result = json.jsonArray;
                tweet.retweeted = true;
                tweet.retweetCount++;
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });

    }

    public static void onUnretweetListener(Context context, final Tweet tweet) {
        TwitterClient client = TwitterApp.getRestClient(context); //Return an instance of the Twitter client
        final String TAG = "retweet";
        // Call the API method using our client to not like it
        client.unretweetTweet(tweet.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess " + json.toString());
                JSONArray result = json.jsonArray;
                tweet.retweeted = false;
                tweet.retweetCount--;
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });

    }

}

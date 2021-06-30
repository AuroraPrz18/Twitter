package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String body;
    public String createdAt;
    public User user;

    // Empty constructor needed by the Parceler library
    public Tweet() { }

    // Method to build the tweet as per the fields in the JSON object
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        //We need to do this because each tweet has an user (who is another JSONObject), with a lot of info in it
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
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
            }else {
                String date = tweetDate.substring(8,9)+ " "+ tweetDate.substring(4,6)+ ".";
                if(date.charAt(0)=='0'){
                    date = date.substring(1);
                }
                return date;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "· " + relativeDate;
    }

    //Method to return a list of tweet object from a JSON array
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

}

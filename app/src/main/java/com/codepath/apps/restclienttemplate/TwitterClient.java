package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/*
 *
 * This is the object responsible for communicating with a REST API.
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes:
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 *
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 *
 */
public class TwitterClient extends OAuthBaseClient {
    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
    public static final String REST_URL = "https://api.twitter.com/1.1"; // base API URL
    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
    public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public TwitterClient(Context context) {
        super(context, REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                null,  // OAuth2 scope, null for OAuth1
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                        context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }

    // Purpose : Obtain the home timeline
    public void getHomeTimeLine(JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("count", 25); // Optional parameter to specify the number of records to retrieve.
        params.put("since_id", 1); // Optional parameter to return results with an ID grater than the specified ID
        params.put("tweet_mode", "extended"); // To force Twitter'' API to return the full text and entities object for each tweet
        client.get(apiUrl, params, handler);
    }

    // Purpose: Publish a tweet
    public void publishTweet(String tweetContent, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("status", tweetContent); // Required parameter to specify the status update
        client.post(apiUrl, params, "", handler); // In this case we need a post request
    }

    // Purpose: Reply a tweet
    public void replyTweet(String tweetContent, String tweetId, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("status", tweetContent); // Required parameter to specify the status update
        params.put("in_reply_to_status_id", tweetId); // Required parameter to specify the status update
        params.put("auto_populate_reply_metadata", true); // Leading @mentions will be looked up from the original Tweet, and added to the new Tweet from there.
        client.post(apiUrl, params, "", handler); // In this case we need a post request
    }

    // Purpose: Like a tweet
    public void likeTweet(String tweetId, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("id", tweetId); // Required parameter to specify the tweet liked
        client.post(apiUrl, params, "", handler); // In this case we need a post request
    }

    // Purpose: Unlike a tweet
    public void unlikeTweet(String tweetId, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/destroy.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("id", tweetId); // Required parameter to specify the tweet unliked
        client.post(apiUrl, params, "", handler); // In this case we need a post request
    }

    // Purpose: Retweet a tweet
    public void retweetTweet(String tweetId, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/retweet.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("id", tweetId); // Required parameter to specify the tweet retweeted
        params.put("trim_user", true);
        client.post(apiUrl, params, "", handler); // In this case we need a post request
    }

    // Purpose: Unretweet a tweet
    public void unretweetTweet(String tweetId, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/unretweet.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("id", tweetId); // Required parameter to specify the tweet unretweeted
        params.put("trim_user", true);
        client.post(apiUrl, params, "", handler); // In this case we need a post request
    }
}

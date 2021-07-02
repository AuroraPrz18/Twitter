# Project 3 - *Twitter App*

**Twitter App** is an android app that allows a user to view their Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **23** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x]	User can **sign in to Twitter** using OAuth login
* [x]	User can **view tweets from their home timeline**
  * [x] User is displayed the username, name, and body for each tweet
  * [x] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
* [x] User can **compose and post a new tweet**
  * [x] User can click a “Compose” icon in the Action Bar on the top right -> I decided to change it to a floating bottom
  * [x] User can then enter a new tweet and post this to Twitter
  * [x] User is taken back to home timeline with **new tweet visible** in timeline
  * [x] Newly created tweet should be manually inserted into the timeline and not rely on a full refresh
* [x] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [x] User can **pull down to refresh tweets timeline**
* [x] User can **see embedded image media within a tweet** on list or detail view.

The following **optional** features are implemented:

* [x] User is using **"Twitter branded" colors and styles**
* [x] User sees an **indeterminate progress indicator** when any background or network task is happening
* [x] User can **select "reply" from home timeline to respond to a tweet**
  * [x] User that wrote the original tweet is **automatically "@" replied in compose**
* [x] User can tap a tweet to **open a detailed tweet view**
  * [x] User can **take favorite (and unfavorite) or retweet** actions on a tweet
* [ ] User can view more tweets as they scroll with infinite pagination
* [ ] Compose tweet functionality is built using modal overlay
* [x] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [x] Replace all icon drawables and other static image assets with [vector drawables](http://guides.codepath.org/android/Drawables#vector-drawables) where appropriate.
* [ ] User can view following / followers list through any profile they view.
* [x] Use the View Binding library to reduce view boilerplate.
* [ ] On the Twitter timeline, apply scrolling effects such as [hiding/showing the toolbar](http://guides.codepath.org/android/Using-the-App-ToolBar#reacting-to-scroll) by implementing [CoordinatorLayout](http://guides.codepath.org/android/Handling-Scrolls-with-CoordinatorLayout#responding-to-scroll-events).
* [ ] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.


## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='http://i.imgur.com/link/to/your/gif/file.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [Kap](https://getkap.co/).

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright 2021 Aurora Perez Calderon

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

# -----------------------------------------------------------------------------

# RestClientTemplate [![Build Status](https://travis-ci.org/codepath/android-rest-client-template.svg?branch=master)](https://travis-ci.org/codepath/android-rest-client-template)

## Overview

RestClientTemplate is a skeleton Android project that makes writing Android apps sourced from OAuth JSON REST APIs as easy as possible. This skeleton project
combines the best libraries and structure to enable quick development of rich API clients. The following things are supported out of the box:

 * Authenticating with any OAuth 1.0a or OAuth 2 API
 * Sending requests for and parsing JSON API data using a defined client
 * Persisting data to a local SQLite store through an ORM layer
 * Displaying and caching remote image data into views

The following libraries are used to make this possible:

 * [scribe-java](https://github.com/fernandezpablo85/scribe-java) - Simple OAuth library for handling the authentication flow.
 * [Android Async HTTP](https://github.com/codepath/AsyncHttpClient) - Simple asynchronous HTTP requests with JSON parsing
 * [codepath-oauth](https://github.com/thecodepath/android-oauth-handler) - Custom-built library for managing OAuth authentication and signing of requests
 * [Glide](https://github.com/bumptech/glide) - Used for async image loading and caching them in memory and on disk.
 * [Room](https://developer.android.com/training/data-storage/room/index.html) - Simple ORM for persisting a local SQLite database on the Android device

## Usage

### 1. Configure the REST client

Open `src/com.codepath.apps.restclienttemplate/RestClient.java`. Configure the `REST_API_INSTANCE` and`REST_URL`.
 
For example if I wanted to connect to Twitter:

```java
// RestClient.java
public class RestClient extends OAuthBaseClient {
    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
    public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties
    // ...constructor and endpoints
}
```

Rename the `apikey.properties.example` file to `apikey.properties`.   Replace the `CONSUMER_KEY` and `CONSUMER_SECRET` to the values specified in the Twitter console:

CONSUMER_KEY="adsflfajsdlfdsajlafdsjl"
CONSUMER_SECRET="afdsljkasdflkjsd"

Next, change the `intent_scheme` and `intent_host` in `strings.xml` to a unique name that is special for this application.
This is used for the OAuth authentication flow for launching the app through web pages through an [Android intent](https://developer.chrome.com/multidevice/android/intents).

```xml
<string name="intent_scheme">oauth</string>
<string name="intent_host">codepathtweets</string>
```

Next, you want to define the endpoints which you want to retrieve data from or send data to within your client:

```java
// RestClient.java
public void getHomeTimeline(int page, JsonHttpResponseHandler handler) {
  String apiUrl = getApiUrl("statuses/home_timeline.json");
  RequestParams params = new RequestParams();
  params.put("page", String.valueOf(page));
  getClient().get(apiUrl, params, handler);
}
```

Note we are using `getApiUrl` to get the full URL from the relative fragment and `RequestParams` to control the request parameters.
You can easily send post requests (or put or delete) using a similar approach:

```java
// RestClient.java
public void postTweet(String body, JsonHttpResponseHandler handler) {
    String apiUrl = getApiUrl("statuses/update.json");
    RequestParams params = new RequestParams();
    params.put("status", body);
    getClient().post(apiUrl, params, handler);
}
```

These endpoint methods will automatically execute asynchronous requests signed with the authenticated access token. To use JSON endpoints, simply invoke the method
with a `JsonHttpResponseHandler` handler:

```java
// SomeActivity.java
RestClient client = RestApplication.getRestClient();
client.getHomeTimeline(1, new JsonHttpResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Headers headers, JSON json) {
    // json.jsonArray.getJSONObject(0).getLong("id");
  }
});
```

Based on the JSON response (array or object), you need to declare the expected type inside the OnSuccess signature i.e
`public void onSuccess(JSONObject json)`. If the endpoint does not return JSON, then you can use the `AsyncHttpResponseHandler`:

```java
RestClient client = RestApplication.getRestClient();
client.getSomething(new JsonHttpResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Headers headers, String response) {
        System.out.println(response);
    }
});
```
Check out [Android Async HTTP Docs](https://github.com/codepath/AsyncHttpClient) for more request creation details.

### 2. Define the Models

In the `src/com.codepath.apps.restclienttemplate.models`, create the models that represent the key data to be parsed and persisted within your application.

For example, if you were connecting to Twitter, you would want a Tweet model as follows:

```java
// models/Tweet.java
package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
public class Tweet {
  // Define database columns and associated fields
  @PrimaryKey
  @ColumnInfo
  Long id;
  @ColumnInfo
  String userHandle;
  @ColumnInfo
  String timestamp;
  @ColumnInfo
  String body;

  // Use @Embedded to keep the column entries as part of the same table while still
  // keeping the logical separation between the two objects.
  @Embedded
  User user;
}
```

Note there is a separate `User` object but it will not actually be declared as a separate table.  By using the `@Embedded` annotation, the fields in this class will be stored as part of the Tweet table.  Room specifically does not load references between two different entities for performance reasons (see https://developer.android.com/training/data-storage/room/referencing-data), so declaring it this way causes the data to be denormalized as one table.

```java
// models/User.java

public class User {

    @ColumnInfo
    String name;

    // normally this field would be annotated @PrimaryKey because this is an embedded object
    // it is not needed
    @ColumnInfo  
    Long twitter_id;
}
```
Notice here we specify the SQLite table for a resource, the columns for that table, and a constructor for turning the JSON object fetched from the API into this object. For more information on creating a model, check out the [Room guide](https://developer.android.com/training/data-storage/room/).

In addition, we also add functions into the model to support parsing JSON attributes in order to instantiate the model based on API data.  For the User object, the parsing logic would be:

```java
// Parse model from JSON
public static User parseJSON(JSONObject tweetJson) {

    User user = new User();
    this.twitter_id = tweetJson.getLong("id");
    this.name = tweetJson.getString("name");
    return user;
}
```

For the Tweet object, the logic would would be:

```java
// models/Tweet.java
@Entity
public class Tweet {
  // ...existing code from above...

  // Add a constructor that creates an object from the JSON response
  public Tweet(JSONObject object){
    try {
      this.user = User.parseJSON(object.getJSONObject("user"));
      this.userHandle = object.getString("user_username");
      this.timestamp = object.getString("timestamp");
      this.body = object.getString("body");
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
    ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

    for (int i=0; i < jsonArray.length(); i++) {
        JSONObject tweetJson = null;
        try {
            tweetJson = jsonArray.getJSONObject(i);
        } catch (Exception e) {
            e.printStackTrace();
            continue;
        }

        Tweet tweet = new Tweet(tweetJson);
        tweets.add(tweet);
    }

    return tweets;
  }
}
```


Now you have a model that supports proper creation based on JSON. Create models for all the resources necessary for your mobile client.

### 4. Define your queries

Next, you will need to define the queries by creating a Data Access Object (DAO) class.   Here is an example of declaring queries to return a Tweet by the post ID, retrieve the most recent tweets, and insert tweets.   

```java

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TwitterDao {
    // Record finders
    @Query("SELECT * FROM Tweet WHERE post_id = :tweetId")
    Tweet byTweetId(Long tweetId);

    @Query("SELECT * FROM Tweet ORDER BY created_at")
    List<Tweet> getRecentTweets();

    // Replace strategy is needed to ensure an update on the table row.  Otherwise the insertion will
    // fail.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTweet(Tweet... tweets);
}
```

The examples here show how to perform basic queries on the Tweet table.  If you need to declare one-to-many or many-to-many relations, see the guides on using the [@Relation](https://developer.android.com/reference/android/arch/persistence/room/Relation) and [@ForeignKey](https://developer.android.com/reference/android/arch/persistence/room/ForeignKey) annotations.

### 5. Create database

We need to define a database that extends `RoomDatabase` and describe which entities as part of this database. We also need to include what data access objects are to be included.  If the entities are modified or additional ones are included, the version number will need to be changed.  Note that only the `Tweet` class is declared:

```java
// bump version number if your schema changes
@Database(entities={Tweet.class}, version=1)
public abstract class MyDatabase extends RoomDatabase {
  // Declare your data access objects as abstract
  public abstract TwitterDao twitterDao();

  // Database name to be used
  public static final String NAME = "MyDataBase";

```    

When compiling the code, the schemas will be stored in a `schemas/` directory assuming this statement
has been included your `app/build.gradle` file.  These schemas should be checked into your code based.

```gradle
android {
    defaultConfig {

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ["room.schemaLocation": "$projectDir/schemas".toString()]
            }
        }
    }

}
```

### 6. Initialize database

Inside your application class, you will need to initialize the database and specify a name for it.

```java
public class RestClientApp extends Application {

  MyDatabase myDatabase;

  @Override
  public void onCreate() {
    // when upgrading versions, kill the original tables by using fallbackToDestructiveMigration()
    myDatabase = Room.databaseBuilder(this, MyDatabase.class, MyDatabase.NAME).fallbackToDestructiveMigration().build();
  }

  public MyDatabase getMyDatabase() {
    return myDatabase;
  }

}
```

### 7. Setup Your Authenticated Activities

Open `src/com.codepath.apps.restclienttemplate/LoginActivity.java` and configure the `onLoginSuccess` method
which fires once your app has access to the authenticated API. Launch an activity and begin using your REST client:

```java
// LoginActivity.java
@Override
public void onLoginSuccess() {
  Intent i = new Intent(this, TimelineActivity.class);
  startActivity(i);
}
```

In your new authenticated activity, you can access your client anywhere with:

```java
RestClient client = RestApplication.getRestClient();
client.getHomeTimeline(1, new JsonHttpResponseHandler() {
  public void onSuccess(int statusCode, Headers headers, JSON json) {
    Log.d("DEBUG", "timeline: " + json.jsonArray.toString());
    // Load json array into model classes
  }
});
```

You can then load the data into your models from a `JSONArray` using:

```java
ArrayList<Tweet> tweets = Tweet.fromJSON(jsonArray);
```

or load the data from a single `JSONObject` with:

```java
Tweet t = new Tweet(json);
// t.body = "foo"
```

To save, you will need to perform the database operation on a separate thread by creating an `AsyncTask` and adding the item:

```java
AsyncTask<Tweet, Void, Void> task = new AsyncTask<Tweet, Void, Void>() {
    @Override
    protected Void doInBackground(Tweet... tweets) {
      TwitterDao twitterDao = ((RestApplication) getApplicationContext()).getMyDatabase().twitterDao();
      twitterDao.insertModel(tweets);
      return null;
    };
  };
  task.execute(tweets);
```

That's all you need to get started. From here, hook up your activities and their behavior, adjust your models and add more REST endpoints.

### Extras

#### Loading Images with Glide

If you want to load a remote image url into a particular ImageView, you can use Glide to do that with:

```java
Glide.with(this).load(imageUrl)
     .into(imageView);
```

This will load an image into the specified ImageView and resize the image to fit.

#### Logging Out

You can log out by clearing the access token at any time through the client object:

```java
RestClient client = RestApplication.getRestClient();
client.clearAccessToken();
```

### Viewing SQL table

You can use `chrome://inspect` to view the SQL tables once the app is running on your emulator.  See [this guide](https://guides.codepath.com/android/Debugging-with-Stetho) for more details.

### Adding OAuth2 support

Google uses OAuth2 APIs so make sure to use the `GoogleApi20` instance:

```java
public static final BaseApi REST_API_INSTANCE = GoogleApi20.instance();
```

Change `REST_URL` to use the Google API:

```java
public static final String REST_URL = "https://www.googleapis.com/calendar/v3"; // Change this, base API URL
```

The consumer and secret keys should be retrieved via [the credentials section](https://console.developers.google.com/apis/credentials) in the Google developer console  You will need to create an OAuth2 client ID and client secret.

Create a file called `apikey.properties`: 

```java
REST_CONSUMER_KEY="XXX-XXX.apps.googleusercontent.com"
REST_CONSUMER_SECRET="XX-XXXXXXX"
```

The OAuth2 scopes should be used according to the ones defined in [the OAuth2 scopes](https://developers.google.com/identity/protocols/googlescopes):

```java
public static final String OAUTH2_SCOPE = "https://www.googleapis.com/auth/calendar.readonly";
```

Make sure to pass this value into the scope parameter:

```java
public RestClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				OAUTH2_SCOPE,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
```
Google only accepts `http://` or `https://` domains, so your `REST_CALLBACK_URL_TEMPLATE` will need to be adjusted:

```java
public static final String REST_CALLBACK_URL_TEMPLATE = "https://localhost";
```

Make sure to update the `cprest` and `intent_host` to match this callback URL . 

### Troubleshooting

* If you receive the following error `org.scribe.exceptions.OAuthException: Cannot send unauthenticated requests for TwitterApi client. Please attach an access token!` then check the following:
 * Is your intent-filter with `<data>` attached to the `LoginActivity`? If not, make sure that the `LoginActivity` receives the request after OAuth authorization.
 * Is the `onLoginSuccess` method being executed in the `LoginActivity`. On launch of your app, be sure to start the app on the LoginActivity so authentication routines execute on launch and take you to the authenticated activity.
 * If you are plan to test with Android API 24 or above, you will need to use Chrome to launch the OAuth flow.  
 * Note that the emulators (both the Google-provided x86 and Genymotion versions) for API 24+ versions can introduce intermittent issues when initiating the OAuth flow for the first time.  For best results, use an device for this project.

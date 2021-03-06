package com.parse.anywall;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class Application extends android.app.Application {
    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "AnyWall";

    // Used to pass location from MainActivity to PostActivity
    public static final String INTENT_EXTRA_LOCATION = "location";

    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";

    private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;

    private static SharedPreferences preferences;

    private static ConfigHelper configHelper;

    public Application() {
    }

    public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }

    public static void setSearchDistance(float value) {
        preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
    }

    public static ConfigHelper getConfigHelper() {
        return configHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(AnywallPost.class);
        ParseObject.registerSubclass(AnywallMessage.class);
        Parse.initialize(this, "0K85aM0HnZl6Md3aticYVbUCOAuoJBsZ8BzVSr5K",
                "2xTyrm1xPcd4jmZ2K2QjttNc5X76gkSANZiLX7x8");
        ParseFacebookUtils.initialize("622395781231182");
        //ParseFacebookUtils.initialize(context);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        preferences = getSharedPreferences("com.parse.anywall", Context.MODE_PRIVATE);

        configHelper = new ConfigHelper();
        configHelper.fetchConfigIfNeeded();
    }

}

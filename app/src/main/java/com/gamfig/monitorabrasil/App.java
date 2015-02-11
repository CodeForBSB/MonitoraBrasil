/**
 * Copyright (C) 2014 Twitter Inc and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gamfig.monitorabrasil;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * This class represents the Application and extends Application it is used to initiate the
 * application.
 */

public class App extends Application {
    public final static String BROADCAST_POEM_CREATION_RESULT = "POEM_CREATION_RESULT";
    public final static String BROADCAST_POEM_DELETION_RESULT = "POEM_DELETION_RESULT";
    public final static String BROADCAST_POEM_CREATION = "POEM_CREATION";
    public final static String BROADCAST_POEM_DELETION = "POEM_DELETION";

    public final static String CRASHLYTICS_KEY_THEME = "theme";
    public final static String CRASHLYTICS_KEY_SESSION_ACTIVATED = "session_activated";
    public final static String CRASHLYTICS_KEY_SEARCH_COUNT = "last_twitter_search_result_count";
    public final static String CRASHLYTICS_KEY_COUNTDOWN = "countdown_timer_remaining_sec";
    public final static String CRASHLYTICS_KEY_WORDBANK_COUNT = "word_bank_count_loaded";
    public final static String CRASHLYTICS_KEY_POEM_TEXT = "saving_poem_text";
    public final static String CRASHLYTICS_KEY_POEM_IMAGE = "saving_poem_image";
    public final static String CRASHLYTICS_KEY_CRASHES = "are_crashes_enabled";
    public final static String POEM_PIC_DIR = "cannonball";

    private static App singleton;
//    private TwitterAuthConfig authConfig;
//    private Typeface avenirFont;

    public static App getInstance() {
        return singleton;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

//        authConfig = new TwitterAuthConfig(BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET);
        Fabric.with(this, new Crashlytics());

       // Crashlytics.setBool(CRASHLYTICS_KEY_CRASHES, areCrashesEnabled());
    }



    public boolean areCrashesEnabled() {
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getBoolean("are_crashes_enabled", false);
    }

    public void setCrashesStatus(boolean status) {
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("are_crashes_enabled", status);
        editor.apply();
    }
}

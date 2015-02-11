package com.gamfig.monitorabrasil.classes.twitter;

import com.google.gson.annotations.SerializedName;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(Session session) {
        super(session);
    }

    /**
     * Provide TimeLine
     */
    public TimelineService getTimellineService() {
        return getService(TimelineService.class);
    }

    public interface TimelineService {
        @GET("/1.1/lists/statuses.json")
        void getTweets(@Query("slug") String slug,
                       @Query("owner_screen_name") String owner_screen_name,
                       Callback<List<Tweet>> cb);

    }

    /**
     * Provide ListTweets
     */
    public ListService getListService() {
        return getService(ListService.class);
    }

    public interface ListService {
        @GET("/1.1/lists/statuses.json")
        void getTweets(@Query("slug") String slug,
                 @Query("owner_screen_name") String owner_screen_name,
                 Callback<List<Tweet>> cb);

    }

    /**
     * Provide FriendsService with ids
     */
    public FriendsService getFriendsService() {
        return getService(FriendsService.class);
    }

    public interface FriendsService {
        @GET("/1.1/friends/ids.json")
        void ids(@Query("user_id") Long userId,
                 @Query("screen_name") String screenName,
                 @Query("cursor") Long cursor,
                 @Query("stringify_ids") Boolean stringifyIds,
                 @Query("count") Integer count,
                 Callback<Ids> cb);

        void idsByUserId(@Query("user_id") Long userId,
                         Callback<Ids> cb);
    }

    public class Ids {
        @SerializedName("previous_cursor")
        public final int previousCursor;

        @SerializedName("ids")
        public final int[] ids;

        @SerializedName("next_cursor")
        public final int nextCursor;


        public Ids(int previousCursor, int[] ids, int nextCursor) {
            this.previousCursor = previousCursor;
            this.ids = ids;
            this.nextCursor = nextCursor;
        }
    }
}
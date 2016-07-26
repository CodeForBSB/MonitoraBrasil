package com.gamfig.monitorabrasil.classes.twitter;

import android.app.Activity;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetui.CompactTweetView;

import java.util.List;

/**
 * Created by geral_000 on 10/02/2015.
 */
public class TwitterAPI extends TwitterFabric{


    public android.view.View getTweetTelaInicial(final LinearLayout myLayout, final Activity activity){
        final SearchService service = Twitter.getApiClient().getSearchService();
        service.tweets("#monitoraBrasil", null, null, null, "recent", 1, null, null,
                null, true, new Callback<Search>() {
                    @Override
                    public void success(Result<Search> searchResult) {

                        final List<Tweet> tweets = searchResult.data.tweets;
                        myLayout.addView(
                                new CompactTweetView(
                                        activity,
                                        tweets.get(0)));
                    }

                    @Override
                    public void failure(TwitterException error) {
                        Crashlytics.logException(error);

                    }
                }
        );
        return null;
    }
}

package com.gamfig.monitorabrasil.activitys;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.RankAdapter;
import com.gamfig.monitorabrasil.adapter.TwitterAdapter;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.gamfig.monitorabrasil.classes.twitter.MyTwitterApiClient;
import com.gamfig.monitorabrasil.classes.twitter.TwitterProxy;
import com.gamfig.monitorabrasil.fragments.PontuacaoFragment;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class TwittterActivity extends ListActivity {
    private boolean flagLoading;
    private boolean endOfSearchResults;
    private static final String SEARCH_QUERY = "#monitoraBrasil";
    private TweetViewAdapter adapter;
    private static final String SEARCH_RESULT_TYPE = "recent";
    private static final int SEARCH_COUNT = 20;
    private long maxId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twittter);

        setUpViews();

        loadTweets();


    }

    private void setUpViews() {
        // setUpBack();
        setUpPopularList();
    }

    private void setUpPopularList() {
//        adapter = new TweetViewAdapter(TwittterActivity.this);
//        setListAdapter(adapter);

        //getListView().setEmptyView(findViewById(R.id.loading));
  /*      getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount == totalItemCount) &&
                        totalItemCount != 0) {
                    Crashlytics.log("Populando lista");
                    if (!flagLoading && !endOfSearchResults) {
                        flagLoading = true;
                        loadTweets();
                    }
                }
            }
        });*/
    }
    private void loadTweets() {
        Crashlytics.log("Buscando Tweets");
        setProgressBarIndeterminateVisibility(true);





        new buscaTweets(this).execute();

        /*MyTwitterApiClient client = new MyTwitterApiClient(Twitter.getSessionManager().getActiveSession());
        client.getListService().getTweets("deputados-federais","monitorabrasil", new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> listTweets) {
                setProgressBarIndeterminateVisibility(false);
                final List<Tweet> tweets = listTweets.data;
                adapter.getTweets().addAll(tweets);
                adapter.notifyDataSetChanged();
                if (tweets.size() > 0) {
                    maxId = tweets.get(tweets.size() - 1).id - 1;
                } else {
                    endOfSearchResults = true;
                }
                flagLoading = false;
            }



            @Override
            public void failure(TwitterException error) {
                Crashlytics.logException(error);

                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(TwittterActivity.this,"error",
                        Toast.LENGTH_SHORT).show();

                flagLoading = false;
            }
        });
*/
    }

    public class buscaTweets extends AsyncTask<Void, Void, List<com.gamfig.monitorabrasil.classes.Twitter>> {
        Activity mActivity;
        ArrayList<Usuario> users;

        public buscaTweets(Activity listaProjetosActivity) {
            this.mActivity = listaProjetosActivity;

        }

        @Override
        protected List<com.gamfig.monitorabrasil.classes.Twitter> doInBackground(Void... params) {

            // buscar os projetos da lista do user

            return new TwitterProxy().getTwitterLista();

        }

        protected void onPostExecute(List<com.gamfig.monitorabrasil.classes.Twitter> tweets) {
            //configurando o imageloader
            DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).build();
            ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(TwittterActivity.this)
                    .defaultDisplayImageOptions(mDisplayImageOptions)
                    .memoryCacheSize(50*1024*1024)
                    .build();
            ImageLoader mImagemLoader = ImageLoader.getInstance();
            mImagemLoader.init(conf);


            try {
                if (tweets != null) {
                    TwitterAdapter adapt = new TwitterAdapter(mActivity,R.layout.listview_item_twitter,tweets,mImagemLoader);
                    setListAdapter(adapt);

                    setProgressBarIndeterminateVisibility(false);
                    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


                        }
                    });
                }

            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }


}
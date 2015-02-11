package com.gamfig.monitorabrasil.fragments.ficha;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.BemAdapter;
import com.gamfig.monitorabrasil.adapter.TwitterAdapter;
import com.gamfig.monitorabrasil.classes.Bem;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.gamfig.monitorabrasil.classes.twitter.TwitterProxy;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetViewAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TwitterFragment extends TabFactory {
	private ProgressBar pb;

	public TwitterFragment() {

	}

    public void montaLayout() {
        String twitter =  getBundle().getString("twitter");
        RelativeLayout rlSemConta = (RelativeLayout) getActivity().findViewById(R.id.rlSemConta);
        if (twitter.length() > 0){
            rlSemConta.setVisibility(View.INVISIBLE);
            Crashlytics.log("Buscando Tweets");
            getActivity().setProgressBarIndeterminateVisibility(true);
            new buscaTweets(twitter).execute();
            /*
            final StatusesService service = com.twitter.sdk.android.Twitter.getApiClient().getStatusesService();
            service.userTimeline(null,twitter,null,null,null,null,null,null,null ,  new Callback<List<Tweet>>() {
                        @Override
                        public void success(Result<List<Tweet>> listTweets) {
                            //   setProgressBarIndeterminateVisibility(false);
                            final List<Tweet> tweets = listTweets.data;

                            TweetViewAdapter adapter = new TweetViewAdapter(getActivity());
                            adapter.getTweets().addAll(tweets);
                            adapter.notifyDataSetChanged();
                            ListView lvTwitter = (ListView) getActivity().findViewById(R.id.listTwitter);
                            lvTwitter.setVisibility(View.VISIBLE);
                            lvTwitter.setAdapter(adapter);
                        }

                        @Override
                        public void failure(TwitterException error) {
                            Crashlytics.logException(error);


                            Toast.makeText(getActivity(), "error",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
            );*/
        }else{

            rlSemConta.setVisibility(View.VISIBLE);
        }
    }

    public class buscaTweets extends AsyncTask<Void, Void, List<com.gamfig.monitorabrasil.classes.Twitter>> {
        String twitter;

        public buscaTweets(String twitter) {
            this.twitter = twitter;

        }

        @Override
        protected List<com.gamfig.monitorabrasil.classes.Twitter> doInBackground(Void... params) {

            // buscar os projetos da lista do user

            return new TwitterProxy().getTimeline(twitter);

        }

        protected void onPostExecute(List<com.gamfig.monitorabrasil.classes.Twitter> tweets) {

            try {
                if (tweets != null) {
                    TwitterAdapter adapt = new TwitterAdapter(getActivity(),R.layout.listview_item_twitter,tweets);
                    ListView lvTwitter = (ListView) getActivity().findViewById(R.id.listTwitter);
                    lvTwitter.setVisibility(View.VISIBLE);
                    lvTwitter.setAdapter(adapt);


                    getActivity().setProgressBarIndeterminateVisibility(false);
                    lvTwitter.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

package com.wingify.ashishgoel.wingifysample.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.wingify.ashishgoel.wingifysample.R;
import com.wingify.ashishgoel.wingifysample.adapters.HomeActivityRecylerViewAdapter;
import com.wingify.ashishgoel.wingifysample.extras.AppConstants;
import com.wingify.ashishgoel.wingifysample.preferences.ZPreferences;

import java.util.Timer;
import java.util.TimerTask;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ashish Goel on 11/28/2015.
 */
public class HomeActivity extends BaseActivity {

    Twitter twitter;
    Long nextIDForLaZyLoading;
    Long iDForPageRefresh;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    HomeActivityRecylerViewAdapter adapter;
    private boolean isRequestRunning;
    private long timeDurationBetweenPageRefresh = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerhomm);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(AppConstants.TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(AppConstants.TWITTER_CONSUMER_SECRET);

        AccessToken accessToken = new AccessToken(ZPreferences.getAccessToken(HomeActivity.this), ZPreferences.getAccessToeknSecret(HomeActivity.this));
        twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastitem = layoutManager.findLastVisibleItemPosition();
                int totalitems = recyclerView.getAdapter().getItemCount();
                int diff = totalitems - lastitem;
                if (diff < 6 && !isRequestRunning) {
                    new SearchAsyncTask().execute();
                }
            }
        });

        new SearchAsyncTask().execute();
    }

    class SearchAsyncTask extends AsyncTask<String, String, QueryResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRequestRunning = true;
        }

        @Override
        protected QueryResult doInBackground(String... params) {
            try {
                Query query = new Query("#blackfriday");
                query.setCount(20);
                if (nextIDForLaZyLoading != null) {
                    query.setMaxId(nextIDForLaZyLoading);
                }
                QueryResult result = twitter.search(query);

                for (twitter4j.Status status : result.getTweets()) {
                    System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
                }

                return result;
            } catch (final Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(QueryResult queryResult) {
            super.onPostExecute(queryResult);
            isRequestRunning = false;
            if (queryResult != null) {
                setAdapterData(queryResult);
            }
        }
    }

    private void setAdapterData(QueryResult result) {
        long biggestTweetID = 0;
        for (Status status : result.getTweets()) {
            if (nextIDForLaZyLoading == null)
                nextIDForLaZyLoading = status.getId();
            else if (nextIDForLaZyLoading > status.getId()) {
                nextIDForLaZyLoading = status.getId();
            }
            if (status.getId() > biggestTweetID) {
                biggestTweetID = status.getId();
            }
        }
        nextIDForLaZyLoading = nextIDForLaZyLoading - 1;

        if (adapter == null) {
            iDForPageRefresh = biggestTweetID;
            adapter = new HomeActivityRecylerViewAdapter(this, result.getTweets());
            recyclerView.setAdapter(adapter);
            Toast.makeText(this, "Loaded 20 items for the first time", Toast.LENGTH_SHORT).show();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new SearchAsyncTaskForRefreshingAfter2Secondsnterval().execute();
                        }
                    });
                }
            }, timeDurationBetweenPageRefresh);
        } else {
            adapter.addData(result.getTweets());
            Toast.makeText(this, "Added 20 items (lazy loading) on list scroll", Toast.LENGTH_SHORT).show();
        }
    }

    class SearchAsyncTaskForRefreshingAfter2Secondsnterval extends AsyncTask<String, String, QueryResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected QueryResult doInBackground(String... params) {
            try {
                Query query = new Query("#blackfriday");
                query.setCount(20);
                query.setSinceId(iDForPageRefresh);
                QueryResult result = twitter.search(query);

                for (twitter4j.Status status : result.getTweets()) {
                    System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(QueryResult queryResult) {
            super.onPostExecute(queryResult);
            addDataToAdapterAtBeginning(queryResult);
        }
    }

    private void addDataToAdapterAtBeginning(QueryResult result) {
        if (result != null) {
            long biggestTweetID = 0;
            for (Status status : result.getTweets()) {
                if (status.getId() > biggestTweetID) {
                    biggestTweetID = status.getId();
                }
            }

            if (adapter != null) {
                adapter.addDataAtBeginningOfList(result);
                Toast.makeText(this, "Added " + result.getTweets().size() + " items at beginning of list after asynchronous refreshing", Toast.LENGTH_SHORT).show();
            }
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new SearchAsyncTaskForRefreshingAfter2Secondsnterval().execute();
                    }
                });
            }
        }, timeDurationBetweenPageRefresh);
    }
}

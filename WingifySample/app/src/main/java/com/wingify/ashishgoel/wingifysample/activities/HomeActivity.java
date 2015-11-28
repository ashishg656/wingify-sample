package com.wingify.ashishgoel.wingifysample.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
public class HomeActivity extends BaseActivity implements View.OnClickListener {

    Twitter twitter;
    Long nextIDForLaZyLoading;
    Long iDForPageRefresh;
    Toolbar toolbar;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    HomeActivityRecylerViewAdapter adapter;
    private boolean isRequestRunning;
    private long timeDurationBetweenPageRefresh = 4000;

    SearchAsyncTask searchAsyncTask;
    SearchAsyncTaskForRefreshingAfter2Secondsnterval searchAsyncTaskForRefreshingAfter2Secondsnterval;
    boolean activityPaused = false;
    private String stringToSearch;

    LinearLayout searchButton;
    EditText searchEditText;
    int pageSize = 20;
    boolean isMoreAllowed = true;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);

        setProgressBarVariables();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerhomm);
        searchButton = (LinearLayout) findViewById(R.id.searchbutton);
        searchEditText = (EditText) findViewById(R.id.stringtosearch);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                if (diff < 6 && !isRequestRunning && isMoreAllowed) {
                    new SearchAsyncTask().execute();
                }
            }
        });

        searchButton.setOnClickListener(this);
        hideLoadingLayout();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.searchbutton) {
            isMoreAllowed = true;
            if (searchEditText.getText().toString().trim().length() > 0) {
                if (searchAsyncTask != null)
                    searchAsyncTask.cancel(true);
                if (searchAsyncTaskForRefreshingAfter2Secondsnterval != null)
                    searchAsyncTaskForRefreshingAfter2Secondsnterval.cancel(true);

                adapter = null;

                stringToSearch = searchEditText.getText().toString().trim();
                searchAsyncTask = new SearchAsyncTask();
                searchAsyncTask.execute();
            } else {
                makeToast("Please enter the string to search");
            }
        }
    }

    void makeToast(String text) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    class SearchAsyncTask extends AsyncTask<String, String, QueryResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRequestRunning = true;
            if (adapter == null) {
                showLoadingLayout();
            }
        }

        @Override
        protected QueryResult doInBackground(String... params) {
            try {
                if (isCancelled())
                    return null;
                Query query = new Query("#" + stringToSearch);
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
                        makeToast(e.getMessage());
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(QueryResult queryResult) {
            super.onPostExecute(queryResult);
            if (adapter == null) {
                hideLoadingLayout();
            }
            isRequestRunning = false;
            if (queryResult != null) {
                setAdapterData(queryResult);
            } else if (adapter == null && queryResult == null) {

            }
        }
    }

    private void setAdapterData(QueryResult result) {
        if (result.getTweets().size() < pageSize)
            isMoreAllowed = false;
        else
            isMoreAllowed = true;

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
        if (nextIDForLaZyLoading != null)
            nextIDForLaZyLoading = nextIDForLaZyLoading - 1;

        if (adapter == null) {
            iDForPageRefresh = biggestTweetID;
            adapter = new HomeActivityRecylerViewAdapter(this, result.getTweets(), isMoreAllowed);
            recyclerView.setAdapter(adapter);
            makeToast("Loaded " + result.getTweets().size() + " items for the first time");

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchAsyncTaskForRefreshingAfter2Secondsnterval = new SearchAsyncTaskForRefreshingAfter2Secondsnterval();
                            searchAsyncTaskForRefreshingAfter2Secondsnterval.execute();
                        }
                    });
                }
            }, timeDurationBetweenPageRefresh);
        } else {
            adapter.addData(result.getTweets(), isMoreAllowed);
            makeToast("Added " + result.getTweets().size() + " items (lazy loading) on list scroll");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityPaused = true;
        if (searchAsyncTaskForRefreshingAfter2Secondsnterval != null)
            searchAsyncTaskForRefreshingAfter2Secondsnterval.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityPaused = false;
        if (searchAsyncTaskForRefreshingAfter2Secondsnterval != null && stringToSearch != null) {
            searchAsyncTaskForRefreshingAfter2Secondsnterval = new SearchAsyncTaskForRefreshingAfter2Secondsnterval();
            searchAsyncTaskForRefreshingAfter2Secondsnterval.execute();
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
                if (isCancelled())
                    return null;
                Query query = new Query("#" + stringToSearch);
                query.setCount(20);
                query.setSinceId(iDForPageRefresh);
                QueryResult result = twitter.search(query);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(QueryResult queryResult) {
            if (!isCancelled()) {
                super.onPostExecute(queryResult);
                addDataToAdapterAtBeginning(queryResult);
            }
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
            if (biggestTweetID != 0)
                iDForPageRefresh = biggestTweetID;

            if (adapter != null) {
                adapter.addDataAtBeginningOfList(result);
                makeToast("Added " + result.getTweets().size() + " items at beginning of list after asynchronous refreshing");
            }
        }

        if (!activityPaused) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchAsyncTaskForRefreshingAfter2Secondsnterval = new SearchAsyncTaskForRefreshingAfter2Secondsnterval();
                            searchAsyncTaskForRefreshingAfter2Secondsnterval.execute();
                        }
                    });
                }
            }, timeDurationBetweenPageRefresh);
        }
    }
}

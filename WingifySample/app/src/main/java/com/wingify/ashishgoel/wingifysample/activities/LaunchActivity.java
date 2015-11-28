package com.wingify.ashishgoel.wingifysample.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.wingify.ashishgoel.wingifysample.R;
import com.wingify.ashishgoel.wingifysample.extras.AppConstants;
import com.wingify.ashishgoel.wingifysample.preferences.ZPreferences;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ashish Goel on 11/27/2015.
 */
public class LaunchActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout loginButton;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_activity_layout);

        loginButton = (LinearLayout) findViewById(R.id.loginlayout);
        loginButton.setOnClickListener(this);

        checkIfRedirectedFromBrowserAfterLogin();
    }

    private void checkIfRedirectedFromBrowserAfterLogin() {
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(AppConstants.TWITTER_CALLBACK_URL)) {
            final String verifier = uri
                    .getQueryParameter(AppConstants.URL_TWITTER_OAUTH_VERIFIER);
            try {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LaunchActivity.this.accessToken = twitter.getOAuthAccessToken(
                                    requestToken, verifier);

                            ZPreferences.setAccessToken(LaunchActivity.this, accessToken.getToken());
                            ZPreferences.setAccessToeknSecret(LaunchActivity.this, accessToken.getTokenSecret());
                            long userID = accessToken.getUserId();
                            ZPreferences.setUserProfileID(LaunchActivity.this, userID + "");
                            User user = twitter.showUser(userID);
                            String username = user.getName();
                            ZPreferences.setUserName(LaunchActivity.this, username);

                            switchToHomeActivity();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            } catch (Exception e) {
                Log.e("Twitter Login Error", "> " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void switchToHomeActivity() {
        ZPreferences.setIsUserLogin(this, true);

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginlayout:
                requestForLoginThroughTwitter();
                break;
        }
    }

    private void requestForLoginThroughTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(AppConstants.TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(AppConstants.TWITTER_CONSUMER_SECRET);
        Configuration configuration = builder.build();

        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    requestToken = twitter
                            .getOAuthRequestToken(AppConstants.TWITTER_CALLBACK_URL);
                    LaunchActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(requestToken.getAuthenticationURL())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


}

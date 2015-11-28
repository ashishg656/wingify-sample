package com.wingify.ashishgoel.wingifysample.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.wingify.ashishgoel.wingifysample.R;
import com.wingify.ashishgoel.wingifysample.extras.AppConstants;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
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

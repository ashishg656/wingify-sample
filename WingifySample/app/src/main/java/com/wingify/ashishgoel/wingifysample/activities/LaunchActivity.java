package com.wingify.ashishgoel.wingifysample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.wingify.ashishgoel.wingifysample.R;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ashish Goel on 11/27/2015.
 */
public class LaunchActivity extends BaseActivity implements View.OnClickListener {

    private static final int LOGIN_REQUEST_CODE_WEBVIEW_ACTIVITY = 105;
    LinearLayout loginButton;

    private static Twitter twitter;
    private static RequestToken requestToken;

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
        final ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(getString(R.string.twitter_consumer_key));
        builder.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));

        final Configuration configuration = builder.build();
        final TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();

        try {
            requestToken = twitter.getOAuthRequestToken(getString(R.string.twitter_callback));

            final Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
            startActivityForResult(intent, LOGIN_REQUEST_CODE_WEBVIEW_ACTIVITY);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}

package com.wingify.ashishgoel.wingifysample.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.wingify.ashishgoel.wingifysample.R;
import com.wingify.ashishgoel.wingifysample.extras.AppConstants;
import com.wingify.ashishgoel.wingifysample.utils.TwitterUtil;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;

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
        new TwitterAuthenticateTask().execute();
    }

    class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            if (requestToken != null) {
                Intent intent = new Intent(LaunchActivity.this, OAuthActivity.class);
                intent.putExtra(AppConstants.STRING_EXTRA_AUTHENCATION_URL, requestToken.getAuthenticationURL());
                startActivity(intent);
            }
        }

        @Override
        protected RequestToken doInBackground(String... params) {
            return TwitterUtil.getInstance().getRequestToken();
        }
    }
}

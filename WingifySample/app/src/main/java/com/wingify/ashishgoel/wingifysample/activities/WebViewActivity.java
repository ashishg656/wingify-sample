package com.wingify.ashishgoel.wingifysample.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.webkit.WebView;

import com.wingify.ashishgoel.wingifysample.R;

/**
 * Created by Ashish Goel on 11/27/2015.
 */
public class WebViewActivity extends BaseActivity {

    WebView webView;
    public static String EXTRA_URL = "extra_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.webview_activity_layout);

        webView = (WebView) findViewById(R.id.webview);
    }
}

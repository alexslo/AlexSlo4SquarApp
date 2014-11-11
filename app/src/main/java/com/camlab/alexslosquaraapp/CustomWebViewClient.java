package com.camlab.alexslosquaraapp;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by alex on 10.11.2014.
 */
public class CustomWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}

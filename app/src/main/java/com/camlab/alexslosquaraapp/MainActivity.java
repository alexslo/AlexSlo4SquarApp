package com.camlab.alexslosquaraapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {
    private WebView wv;
    public final String clientID = "OB3GHFSZRCRA0ZVDA4WZXVK3K2L3XWMIYUWOH1ANJC4M5XEU";
    public final String clientSecret = "TAK0TN11DYUL5QHKWNVBDIUXU5MXKZBQEG0IH4LRIJD3PIT3";
    public final String redirectUri = "http://xarxameg.cat/sites/default/files/ok.png";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.placesListButton).setOnClickListener(this);
        findViewById(R.id.exitButton).setOnClickListener(this);
        wv = (WebView) findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new CustomWebViewClient());
        //get access token
        /*
        https://foursquare.com/oauth2/authenticate
        ?client_id=YOUR_CLIENT_ID
        &response_type=code
        &redirect_uri=YOUR_REGISTERED_REDIRECT_URI
         */
        wv.loadUrl("https://foursquare.com/oauth2/authenticate?" +
                "client_id=" + clientID + "&" +
                "response_type=token&" +
                "redirect_uri=" + redirectUri );

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.placesListButton){
            if (wv.getUrl().substring(47, 59).equals("access_token")) {
                String accessToken = wv.getUrl().split("=")[1];
                Intent intent = new Intent(MainActivity.this, PlacesListActivity.class);
                intent.putExtra("AccessToken", accessToken);
                intent.putExtra("ClientID", clientID);
                intent.putExtra("ClientSecret", clientSecret);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Authorization error. access_denied. Please try again. ", Toast.LENGTH_LONG).show();
            }
        }
        if(v.getId() == R.id.exitButton){
            finish();
            System.exit(0);
        }
    }
}
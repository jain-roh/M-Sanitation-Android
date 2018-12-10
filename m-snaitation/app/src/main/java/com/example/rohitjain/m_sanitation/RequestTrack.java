package com.example.rohitjain.m_sanitation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class RequestTrack extends AppCompatActivity {
WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_track);
        setTitle("Track you Request");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        final Button trackButton=(Button)findViewById(R.id.trackButton);
        final EditText trackingNoText=(EditText)findViewById(R.id.trackingNoText);
        webview=(WebView)findViewById(R.id.webview);
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!trackingNoText.getText().toString().trim().equals("")) {
                    trackButton.setVisibility(View.GONE);
                    trackingNoText.setVisibility(View.GONE);
                    webview.setVisibility(View.VISIBLE);
                    webview.getSettings().setJavaScriptEnabled(true);

                    webview.loadUrl("http://bhartiyamattress.com/showStatus.php?" + trackingNoText.getText().toString().trim());

                    webview.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
//                        webview.loadUrl(
//                                "javascript:(function doHide(){" +
//                                        "document.getElementById( \"animation\" ).style.display = \"none\" ;\n" +
//                                        "document.getElementById( \"restRoom\" ).style.display = \"none\" ;\n" +
//                                        "document.getElementById(\"progressBar\").style.display=\"block\"\n" +
//                                        "}" +
//
//                                        "function hideImage(){" +
//
//                                        "setTimeout( \"doHide()\", 5000 ) ;" +
//                                        "})()");
                            //   webview.loadUrl("javascript:doHide();");
                        }
                    });

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please enter Tracking Number",Toast.LENGTH_LONG).show();
                }
            }

        });
    }



}

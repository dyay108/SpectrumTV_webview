package com.workaround.spectv;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.FragmentActivity;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    WebView myWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.spectv);
        myWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                Log.d("**************************", consoleMessage.message() + " -- From line " +
//                        consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
//                return true;
//            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                String[] resources = request.getResources();
                for (int i = 0; i <+ resources.length; i++) {
                    if (PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID.equals(resources[i])) {
                        request.grant(resources);
                        return;
                    }
                }

                super.onPermissionRequest(request);
            }


        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                myWebView.evaluateJavascript("var loopVar = setInterval(function() {" +
                        "try{" +
                        // Accept initial prompts
                        "document.querySelector('[aria-label=\"Continue and accept terms and conditions to go to Spectrum TV\"]')?.click();" +
                        "[...document.querySelectorAll(\"button\")]?.find(btn => btn.textContent.includes(\"Got It\"))?.click();" +
                        // Max volume
                        "document.getElementById('spectrum-player').getElementsByTagName('video')[0].volume = 1.0;" +
                        // Hide html elements except video player
                        "$('.site-header').attr('style', 'display: none');" +
                        "$('#video-controls').attr('style', 'display: none');" +
                        "$('.nav-triangle-pattern').attr('style', 'display: none');" +
                        "$('channels-filter').attr('style', 'display: none');" +
                        "$('.transparent-header').attr('style', 'display: none');" +
                        // Style mini channel guide
                        "$('#channel-browser').attr('style', 'height: 100%');" +
                        "$('.mini-guide').attr('style', 'height: 100%');" +
                        // To help with navigation with remote. Doesn't seem to do anything though
                        "$('#channel-browser').attr('style', 'tabindex: 1');" +
                        "$('#spectrum-player').attr('style', 'tabindex: 0');" +
                        "}" +
                        "catch(e){" +
                        "console.log(e)" +
                        "}" +
                        "}, 2000);" , null);

            }

        });
        myWebView.setVerticalScrollBarEnabled(false);
        myWebView.loadUrl("https://watch.spectrum.net/?sessionOverride=true");
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Handle key events to consistently bring up the mini channel guide
        if (event.getAction() == KeyEvent.ACTION_DOWN ) {
            if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                // Simulate clicking on the video player which brings up the mini channel guide (just like on desktop)
                myWebView.evaluateJavascript("$('#spectrum-player').focus().click();", null);
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }


}
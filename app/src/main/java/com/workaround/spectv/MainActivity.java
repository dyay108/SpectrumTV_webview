package com.workaround.spectv;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.FragmentActivity;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    final String uaString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    final String guideUrl = "https://watch.spectrum.net/guide";
    WebView spectrumPlayer;
    WebView spectrumGuide;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPlayer();
        initGuide();

        spectrumPlayer.addJavascriptInterface(this, "Spectv");
        spectrumGuide.addJavascriptInterface(this, "Spectv");

//        spectrumPlayer = (WebView) findViewById(R.id.spectv);
//        spectrumGuide = (WebView) findViewById(R.id.spectv_guide);
//        spectrumGuide.setVisibility(View.GONE);
//
//        spectrumPlayer.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                Log.d("**************************", consoleMessage.message() + " -- From line " +
//                        consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
//                return true;
//            }
//
//            @Override
//            public void onPermissionRequest(PermissionRequest request) {
//                String[] resources = request.getResources();
//                for (int i = 0; i < +resources.length; i++) {
//                    if (PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID.equals(resources[i])) {
//                        request.grant(resources);
//                        return;
//                    }
//                }
//
//                super.onPermissionRequest(request);
//            }
//
//
//        });
//        WebSettings spectrumPlayerWebSettings = spectrumPlayer.getSettings();
//        WebSettings spectrumGuideWebSettings = spectrumGuide.getSettings();
//
//        initWebviews(spectrumPlayerWebSettings);
//        initWebviews(spectrumGuideWebSettings);
//
//        spectrumGuide.addJavascriptInterface(this, "Specguide");
//        spectrumGuide.loadUrl(guideUrl);
//
//        spectrumPlayer.addJavascriptInterface(this, "Spectv");
//        spectrumGuide.addJavascriptInterface(this, "Spectv");
//
//        spectrumPlayer.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                spectrumPlayer.evaluateJavascript("var loopVar = setInterval(function() {" +
//                                "try{" +
//                                // Accept initial prompts
//                                "document.querySelector('[aria-label=\"Continue and accept terms and conditions to go to Spectrum TV\"]')?.click();" +
//                                "[...document.querySelectorAll(\"button\")]?.find(btn => btn.textContent.includes(\"Got It\"))?.click();" +
//                                // Max volume
//                                "document.getElementById('spectrum-player').getElementsByTagName('video')[0].volume = 1.0;" +
//                                // Hide html elements except video player
//                                "$('.site-header').attr('style', 'display: none');" +
//                                "$('#video-controls').attr('style', 'display: none');" +
//                                "$('.nav-triangle-pattern').attr('style', 'display: none');" +
//                                "$('channels-filter').attr('style', 'display: none');" +
//                                "$('.transparent-header').attr('style', 'display: none');" +
//                                // Style mini channel guide
//                                "$('#channel-browser').attr('style', 'height: 100%');" +
//                                "$('.mini-guide').attr('style', 'height: 100%');" +
//                                // To help with navigation with remote. Doesn't seem to do anything though
//                                "$('#channel-browser').attr('style', 'tabindex: 1');" +
//                                "$('#spectrum-player').attr('style', 'tabindex: 0');" +
//                                "}" +
//                                "catch(e){" +
//                                "console.log(e)" +
//                                "}" +
//                                "}, 2000);" +
//                                "function toggleGuide(s) {Spectv.channelGuide(s)}"
//                        , null);
//
//            }
//
//        });
//        spectrumPlayer.setVerticalScrollBarEnabled(false);
//        spectrumPlayer.loadUrl("https://watch.spectrum.net/?sessionOverride=true");
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Handle key events to consistently bring up the mini channel guide
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP  && spectrumGuide.getVisibility() == View.GONE ) {
                // Simulate clicking on the video player which brings up the mini channel guide (just like on desktop)
//                spectrumPlayer.evaluateJavascript("$('#spectrum-player').focus().click();", null);

                spectrumGuide.evaluateJavascript("window.location.href;", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String currentURL) {
                        currentURL = currentURL.replaceAll("^\"|\"$", "");
                        if(!currentURL.equals(guideUrl)) {
                            Log.d("!!!!!!!!!!!!!!!!!!!!!", guideUrl);
//                            spectrumGuide.loadUrl(guideUrl);
                            spectrumGuide.evaluateJavascript("history.go(-(history.length -1))", null);
                        }
                        spectrumPlayer.evaluateJavascript("toggleGuide('SHOW');", null);
                    }
                });

                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK  && spectrumGuide.getVisibility() != View.GONE ) {
                // Simulate clicking on the video player which brings up the mini channel guide (just like on desktop)
//                spectrumPlayer.evaluateJavascript("$('#spectrum-player').focus().click();", null);


                spectrumPlayer.evaluateJavascript("toggleGuide('HIDE');", null);

                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @JavascriptInterface
    public void channelGuide(String action) {
        switch (action) {
            case "SHOW":
                Log.d("***********showing ", action);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spectrumGuide.setVisibility(View.VISIBLE);
                            spectrumGuide.requestFocus();
                        }
                    });

                } catch (Exception e) {
                    Log.d("%%%%%%%%%%%%%%ERROR in showing", e.toString());
                }
                break;
            case "HIDE":
                Log.d("***********hiding ", action);
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spectrumGuide.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    Log.d("%%%%%%%%%%%%%%ERROR in hiding", e.toString());
                }
                break;
        }
    }

    private void initWebviews(WebSettings wv) {
        wv.setJavaScriptEnabled(true);
        wv.setDomStorageEnabled(true);
        wv.setMediaPlaybackRequiresUserGesture(false);
        wv.setMixedContentMode(wv.MIXED_CONTENT_ALWAYS_ALLOW);
        wv.setUserAgentString(uaString);
    }

    private void initPlayer() {
        spectrumPlayer = (WebView) findViewById(R.id.spectv);

        spectrumPlayer.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("**************PLAYER************", consoleMessage.message() + " -- From line " +
                        consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                String[] resources = request.getResources();
                for (int i = 0; i < +resources.length; i++) {
                    if (PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID.equals(resources[i])) {
                        request.grant(resources);
                        return;
                    }
                }

                super.onPermissionRequest(request);
            }


        });
        WebSettings spectrumPlayerWebSettings = spectrumPlayer.getSettings();

        initWebviews(spectrumPlayerWebSettings);

        spectrumPlayer.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                spectrumPlayer.evaluateJavascript("var loopVar = setInterval(function() {" +
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
                                "}, 2000);" +
                                "function toggleGuide(s) {Spectv.channelGuide(s)}"
                        , null);

            }

        });
        spectrumPlayer.setVerticalScrollBarEnabled(false);
        spectrumPlayer.loadUrl("https://watch.spectrum.net/?sessionOverride=true");
    }

    private void initGuide() {
        spectrumGuide = (WebView) findViewById(R.id.spectv_guide);
        spectrumGuide.setVisibility(View.GONE);

        spectrumGuide.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("**************GUIDE************", consoleMessage.message() + " -- From line " +
                        consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                String[] resources = request.getResources();
                for (int i = 0; i < +resources.length; i++) {
                    if (PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID.equals(resources[i])) {
                        request.grant(resources);
                        return;
                    }
                }

                super.onPermissionRequest(request);
            }
        });

        WebSettings spectrumGuideWebSettings = spectrumGuide.getSettings();
        initWebviews(spectrumGuideWebSettings);

        spectrumGuide.loadUrl(guideUrl);

        spectrumGuide.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                spectrumGuide.evaluateJavascript(
                                "var loopVar = null;//setInterval(function() {console.log('##########################'), 2000});"
                        , null);
            }
        });
    }


}
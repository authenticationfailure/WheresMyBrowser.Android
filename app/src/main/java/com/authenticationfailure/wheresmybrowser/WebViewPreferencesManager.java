package com.authenticationfailure.wheresmybrowser;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class WebViewPreferencesManager {

    private WebView webView;
    private WebSettings webViewSettings;
    private SharedPreferences settings;
    private WebViewPreferencesChangeListener preferencesChangeListener;

    public WebViewPreferencesManager(WebView webView, Context context) {
        this.webView = webView;
        webViewSettings = webView.getSettings();
        settings = PreferenceManager.getDefaultSharedPreferences(context);

        if (settings.getBoolean("is_first_run",true)) {
            // Set first_run defaults
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("enable_javascript", true);
            editor.putBoolean("enable_webview_client", true);
            editor.putBoolean("enable_webview_debugging", true);
            editor.putBoolean("enable_file_access", true);
            editor.putBoolean("enable_file_access_from_file_url", true);
            editor.putBoolean("enable_universal_access_from_file_url", true);
            editor.putBoolean("enable_javascript_interface", true);
            editor.putBoolean("is_first_run", false);
            editor.commit();
        }

        setAll();

        preferencesChangeListener = new WebViewPreferencesChangeListener();
        settings.registerOnSharedPreferenceChangeListener(preferencesChangeListener);
    }

    public void setAll() {
        setEnableJavaScript();
        setEnableWebViewClient();
        setEnableWebViewDebugging();
        setEnableAllowFileAccess();
        setEnableAllowFileAccessFromFileURLs();
        setEnableUniversalAccessFromFileURLs();
        setEnableJavaScriptInterface();
    }

    public void setPreference(String key) {
        Log.i("Preferences Manager","setting preference '" + key + "'");
        switch (key) {
            case "enable_javascript":
                setEnableJavaScript();
                break;
            case "enable_webview_client":
                setEnableWebViewClient();
                break;
            case "enable_webview_debugging":
                setEnableWebViewDebugging();
                break;
            case "enable_file_access":
                setEnableAllowFileAccess();
                break;
            case "enable_file_access_from_file_url":
                setEnableAllowFileAccessFromFileURLs();
                break;
            case "enable_universal_access_from_file_url":
                setEnableUniversalAccessFromFileURLs();
                break;
            case "enable_javascript_interface":
                setEnableJavaScriptInterface();
                break;

        }
    }

    public void setEnableJavaScript() {
        // Android default is false
        Boolean enableJavaScript = settings.getBoolean("enable_javascript",true);
        webViewSettings.setJavaScriptEnabled(enableJavaScript);
    }

    public void setEnableWebViewClient() {
        Boolean enableWebViewClient = settings.getBoolean("enable_webview_client", true);
        // Debugging can be enabled from KitKat onwards only
        // See @TargetAPI(19)
        if (enableWebViewClient) {
            webView.setWebViewClient(new WebViewClient());
        } else {
            webView.setWebViewClient(null);
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setEnableWebViewDebugging() {
        Boolean enableWebViewDebugging =
                settings.getBoolean("enable_webview_debugging", true);
        // Debugging can be enabled from KitKat onwards only
        // See @TargetAPI(19)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(enableWebViewDebugging);
        }
    }

    public void setEnableAllowFileAccess() {
        Boolean enableFileAccess = settings.getBoolean("enable_file_access", true);
        // Allow the webview to access local files and resources
        // Need android.permission.READ_EXTERNAL_STORAGE to access all
        // files on the sdcard
        webViewSettings.setAllowFileAccess(enableFileAccess);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setEnableAllowFileAccessFromFileURLs() {

        Boolean enableFileAccessFromFileURL =
                settings.getBoolean("enable_file_access_from_file_url", true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webViewSettings.setAllowFileAccessFromFileURLs(enableFileAccessFromFileURL);
            webView.reload();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setEnableUniversalAccessFromFileURLs() {

        Boolean enableUniversalAccessFromFileURL =
                settings.getBoolean("enable_universal_access_from_file_url", true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webViewSettings.setAllowUniversalAccessFromFileURLs(enableUniversalAccessFromFileURL);
            webView.reload();
        }
    }


    public void setEnableJavaScriptInterface() {

        Boolean enableJavaScriptInterface =
                settings.getBoolean("enable_javascript_interface", true);

        if (enableJavaScriptInterface) {
            // Invoke exported methods from JavascriptBridge from JavaScript using:
            // javascriptBridge.getSecret()
            // Android <= 4.1 (JELLY_BEAN, API 16) is affected by CVE-2012-6636
            // It is possible to use reflection from JavaScript to execute code remotely
            webView.addJavascriptInterface(new JavascriptBridge(), "javascriptBridge");
            webView.reload();
        } else {
            webView.removeJavascriptInterface("javascriptBridge");
            webView.reload();
        }
    }

    class WebViewPreferencesChangeListener implements OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(android.content.SharedPreferences
                                                      sharedPreferences, String s) {
            Log.i("WebView settings", "change of preference " + s);

            setPreference(s);
        }
    };
}

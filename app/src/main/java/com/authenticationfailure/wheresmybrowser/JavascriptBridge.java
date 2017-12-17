package com.authenticationfailure.wheresmybrowser;

import android.webkit.JavascriptInterface;

public class JavascriptBridge {
        // In API 17+ methods not annotated with @JavascriptInterface
        // are not visible from JavaScript
        @JavascriptInterface
        public String getSecret() {
            return "SuperSecretPassword";
        };

}

package com.authenticationfailure.wheresmybrowser;

import android.webkit.JavascriptInterface;

public class JavascriptBridge {
        // Since Android 4.2 (JELLY_BEAN_MR1, API 17) methods
        // not annotated with @JavascriptInterface are not visible from JavaScript
        @JavascriptInterface
        public String getSecret() {
            return "SuperSecretPassword";
        };

}

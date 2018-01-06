/**
 *
 * Where's My Browser
 *
 * Copyright (C) 2017, 2018	   David Turco
 *
 * This program can be distributed under the terms of the GNU GPL.
 * See the file COPYING.
 *
 * WARNING: This code is VULNERABLE-BY-DESIGN and it is intended as a learning tool
 *          DO NOT USE THIS CODE IN YOUR PROJECTS!!!
 *
 */

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

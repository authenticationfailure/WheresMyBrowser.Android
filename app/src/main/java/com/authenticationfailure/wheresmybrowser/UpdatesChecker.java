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

import android.app.AlertDialog;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Where's My Browser
 *
 * Copyright (C) 2017, 2018	   David Turco
 *
 * This program can be distributed under the terms of the GNU GPL.
 * See the file COPYING.
 *
 * WARNING: This code is VULNERABLE-BY-DESIGN and  it is intended as a learning tool
 *          DO NOT USE THIS CODE IN YOUR PROJECTS!!!
 *
 */

public class UpdatesChecker {

    static String UPDATES_URL = "https://www.authenticationfailure.com/wmb/releases/Android.latest";
    RequestQueue queue;
    JsonObjectRequest jsonObjectRequest;

    class UpdatesResponseListener implements Response.Listener<JSONObject> {

        Context ctx;

        public UpdatesResponseListener(Context ctx) {
            this.ctx = ctx;
        }
        private String currentVersion;
        private String latestVersion;

        @Override
        public void onResponse(JSONObject response) {
            try {
                currentVersion = BuildConfig.VERSION_NAME;
                latestVersion = response.getString("latest_version");

                if (versionCompare(latestVersion,currentVersion)>0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    String downloadUrl =  response.getString("download_url");
                    String message = ctx.getResources().getString(R.string.updates_available);
                    builder.setMessage(message.concat(downloadUrl));
                    builder.setTitle(R.string.updates_available_title);
                    builder.setPositiveButton("Ok", null);
                    builder.create().show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public UpdatesChecker(Context ctx) {
        UpdatesResponseListener updatesResponseListener = new UpdatesResponseListener(ctx);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                UPDATES_URL, null, updatesResponseListener, null);
        queue = Volley.newRequestQueue(ctx);
    }

    public void check() {
        queue.add(jsonObjectRequest);
    }

    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

}


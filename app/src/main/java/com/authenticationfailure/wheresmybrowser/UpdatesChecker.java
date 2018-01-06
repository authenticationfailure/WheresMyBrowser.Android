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

        @Override
        public void onResponse(JSONObject response) {
            try {
                if (!BuildConfig.VERSION_NAME.equals(response.getString("latest_version"))) {
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

}


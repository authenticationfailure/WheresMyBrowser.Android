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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView versionTextView = findViewById(R.id.versionTextView);
        versionTextView.setText(BuildConfig.VERSION_NAME);
    }

}

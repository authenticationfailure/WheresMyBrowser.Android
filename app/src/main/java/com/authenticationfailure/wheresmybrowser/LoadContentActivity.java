package com.authenticationfailure.wheresmybrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class LoadContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Switch baseUrlSwitch = findViewById(R.id.baseUrlSwitch);
        final EditText baseUrlEditText = findViewById(R.id.baseUrlEditText);
        baseUrlEditText.setVisibility(View.GONE);
        final EditText htmlContentEditText = findViewById(R.id.htmlContentEditText);

        Button loadButton = findViewById(R.id.okButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("use_base_url", baseUrlSwitch.isChecked());
                String baseUrl = baseUrlEditText.getText().toString();
                if (baseUrl.equals("")) {
                    baseUrl = null;
                }
                resultIntent.putExtra("base_url", baseUrl);
                resultIntent.putExtra("html_content", htmlContentEditText.getText().toString());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        baseUrlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                baseUrlEditText.setEnabled(b);
                if (b) {
                    baseUrlEditText.setVisibility(View.VISIBLE);
                } else {
                    baseUrlEditText.setVisibility(View.GONE);
                }
            }
        });

    }

}

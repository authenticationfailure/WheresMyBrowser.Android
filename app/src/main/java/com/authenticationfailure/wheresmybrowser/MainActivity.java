package com.authenticationfailure.wheresmybrowser;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageButton goButton;
    WebView webview;
    EditText urlBar;
    WebViewPreferencesManager webViewPreferencesManager;
    Editor settingsEditor;
    ProgressBar webViewProgressBar;
    int REQUEST_SD_WRITE_PERMISSION = 1;

    static String DEFAULT_URL = "http://www.example.com";
    static int LOAD_CONTENT_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        goButton = findViewById(R.id.goButton);
        webview = findViewById(R.id.webView);
        urlBar = findViewById(R.id.urlText);
        webViewProgressBar = findViewById(R.id.webViewProgressBar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutIntent = new Intent(getApplicationContext(), LoadContentActivity.class);
                startActivityForResult(aboutIntent, LOAD_CONTENT_ACTIVITY_REQUEST_CODE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //Hide keyboard on drawer opening
                webview.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Learn about web views here:
        // https://developer.android.com/guide/webapps/webview.html

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadURLFromBar();
            }
        });

        /*
        *  webview settings are managed by the WebViewPreferencesManager class
        *  go check the code
        */
        webViewPreferencesManager = new WebViewPreferencesManager(webview, getApplicationContext());

        settingsEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        requestExternalStoragePrivileges();

        setupData();

        setupUrlBar();


        // Manage the progress bar and update URL bar
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) { newProgress = 0; };
                webViewProgressBar.setProgress(newProgress);
                urlBar.setText(webview.getUrl());
            }
        });

        // Workaround for limitations in the use of file:// URI in
        // SDK >= Android N (API v24). This prevents the application from crashing.
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle response from LoadContentActivity
        if (requestCode == LOAD_CONTENT_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Boolean useBaseUrl = data.getBooleanExtra("use_base_url", false);
            String baseUrl = data.getStringExtra("base_url");
            String htmlContent = data.getStringExtra("html_content");
            if (useBaseUrl) {
                webview.loadDataWithBaseURL(baseUrl, htmlContent,"text/html","UTF-8", baseUrl);
            } else {
                webview.loadData(htmlContent, "text/html", "UTF-8");
            }
        }
    }

    private boolean requestExternalStoragePrivileges() {
        // Ask for external SD card permission for Android 6.0 API 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int storagePermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (storagePermission != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_SD_WRITE_PERMISSION);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private void setupData() {
        SecretDatabaseHelper secretDb = new SecretDatabaseHelper(getApplicationContext());
        Cursor cursor = secretDb.getReadableDatabase().query("SECRET_TABLE",
                new String[]{"id","secret"},
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        Log.i("SecretDatabase", cursor.getString(cursor.getColumnIndex("secret")));

    }

    private void loadURLFromBar() {
        webview.loadUrl(urlBar.getText().toString());
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void setupUrlBar() {
        urlBar.setHint(DEFAULT_URL);

        TextView.OnEditorActionListener urlBarEditorListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_NULL
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    loadURLFromBar();
                }
                return false;
            }
        };

        urlBar.setOnEditorActionListener(urlBarEditorListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.action_history_back) {
            if (webview.canGoBack()) webview.goBack();
        } else if (id == R.id.action_request_external_storage) {
            if (requestExternalStoragePrivileges()) {
                Toast.makeText(getApplicationContext(), R.string.has_access_to_external_storage,
                        Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        } else if (id == R.id.nav_documentation) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.authenticationfailure.com/wmb/"));
            startActivity(intent);

        } else if (id == R.id.nav_scenario_1) {

            settingsEditor.putBoolean("enable_javascript", true);
            settingsEditor.putBoolean("enable_webview_client", true);
            settingsEditor.putBoolean("enable_webview_debugging", true);
            settingsEditor.putBoolean("enable_file_access", true);
            settingsEditor.putBoolean("enable_file_access_from_file_url", true);
            settingsEditor.putBoolean("enable_universal_access_from_file_url", true);
            settingsEditor.putBoolean("enable_javascript_interface", false);
            settingsEditor.commit();

            try {
                String scenario1Asset = "web/scenario1.html";
                String scenario1File = getFilesDir().getCanonicalPath().concat("/scenario1.html");

                FileUtils.copyAssetToFile(getApplicationContext(), scenario1Asset, scenario1File);

                urlBar.setText("file://" + scenario1File);
                loadURLFromBar();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FILE_CREATE", "Error creating file");
            }
            loadURLFromBar();

        } else if (id == R.id.nav_scenario_2) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(R.string.scenario_2_description).setTitle(R.string.scenario_2);
            builder.setPositiveButton("Ok",null);
            builder.create().show();

            settingsEditor.putBoolean("enable_javascript", true);
            settingsEditor.putBoolean("enable_webview_client", true);
            settingsEditor.putBoolean("enable_webview_debugging", true);
            settingsEditor.putBoolean("enable_file_access", false);
            settingsEditor.putBoolean("enable_file_access_from_file_url", false);
            settingsEditor.putBoolean("enable_universal_access_from_file_url", false);
            settingsEditor.putBoolean("enable_javascript_interface", true);
            settingsEditor.commit();

            urlBar.setText("http://www.example.com/");
            loadURLFromBar();

        } else if (id == R.id.nav_scenario_3) {

            settingsEditor.putBoolean("enable_javascript", true);
            settingsEditor.putBoolean("enable_webview_client", true);
            settingsEditor.putBoolean("enable_webview_debugging", true);
            settingsEditor.putBoolean("enable_file_access", true);
            settingsEditor.putBoolean("enable_file_access_from_file_url", true);
            settingsEditor.putBoolean("enable_universal_access_from_file_url", true);
            settingsEditor.putBoolean("enable_javascript_interface", true);
            settingsEditor.commit();

            try {
                String scenario3Asset = "web/scenario3.html";
                String scenario3File = getFilesDir().getCanonicalPath().concat("/scenario3.html");

                FileUtils.copyAssetToFile(getApplicationContext(), scenario3Asset, scenario3File);

                urlBar.setText("file://" + scenario3File);
                loadURLFromBar();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FILE_CREATE", "Error creating file");
            }

        } else if (id == R.id.nav_scenario_4) {

            settingsEditor.putBoolean("enable_javascript", true);
            settingsEditor.putBoolean("enable_webview_client", true);
            settingsEditor.putBoolean("enable_webview_debugging", true);
            settingsEditor.putBoolean("enable_file_access", true);
            settingsEditor.putBoolean("enable_file_access_from_file_url", true);
            settingsEditor.putBoolean("enable_universal_access_from_file_url", true);
            settingsEditor.putBoolean("enable_javascript_interface", false);
            settingsEditor.commit();

            try {
                String scenario4Asset = "web/scenario4.html";
                String scenario4HTML =
                FileUtils.readAssetToString(getApplicationContext(), scenario4Asset);

                //webview.loadData(scenario4HTML, "text/html", "UTF-8");
                webview.loadDataWithBaseURL(null, scenario4HTML,"text/html", "UTF-8", null);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FILE_CREATE", "Error reading scenario 4 file");
            }
        }
        else if (id == R.id.nav_share) {
            // TODO: add sharing actions
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

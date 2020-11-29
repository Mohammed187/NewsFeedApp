package com.example.newsfeed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    private static final String TAG = "MainActivity";

    public static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search";
    public static final int NEWS_LOADER_ID = 1;
    private NewsAdapter newsAdapter;
    private TextView emptyText, headerTitle;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.empty_txt);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (isConnected) {
            LoaderManager.getInstance(this).initLoader(NEWS_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyText.setText(getString(R.string.no_internet));
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, @Nullable Bundle args) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String section = preferences.getString(
                getString(R.string.section_key),
                getString(R.string.section_value)
        );
        String orderBy = preferences.getString(
                getString(R.string.order_by_key),
                getString(R.string.order_by_value)
        );

        String api_key = getString(R.string.API_KEY);

        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendQueryParameter("show-fields", "thumbnail");
        builder.appendQueryParameter("q", section);
        builder.appendQueryParameter("order-by", orderBy);
        builder.appendQueryParameter("api-key", api_key);

        Log.d(TAG, "URL : " + builder.toString());

        return new NewsLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<News>> loader, ArrayList<News> data) {

        progressBar.setVisibility(View.GONE);

        if (data != null && !data.isEmpty()) {
            updateUi(data);
            newsAdapter.addAll(data);
        } else {
            emptyText.setText(getString(R.string.no_result));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<News>> loader) {
        newsAdapter.clear();
    }

    private void updateUi(ArrayList<News> news) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String header = preferences.getString(
                getString(R.string.section_key),
                getString(R.string.section_value)
        );

        // set main activity header title
        headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.header_title, header));

        ListView newsList = findViewById(R.id.news_list);

        newsAdapter = new NewsAdapter(this, news);

        newsList.setAdapter(newsAdapter);

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News newsItem = newsAdapter.getItem(position);

                Uri uri = Uri.parse(newsItem.getUrl());

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
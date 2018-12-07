package com.example.android.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Loader;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    private TextView mEmptyStateTextView;
    private NewsAdapter nAdapter;
    private static final int NEWS_LOADER_ID = 1;
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_main);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        ListView newsListView = (ListView) findViewById(R.id.list);

        nAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(nAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               News currentNews = nAdapter.getItem(position);
               Uri newsUri = Uri.parse(currentNews.getUrl());
               Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
               startActivity(websiteIntent);
           }
        });

        if(networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loadManager = getLoaderManager();
            loadManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(
                "order_by",
                "@string/settings_order_by_most_recent_label"
        );

        String sectionType = sharedPrefs.getString(
                "section_id",
                "@string/settings_section_id_politics_label"
        );
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //https://content.guardianapis.com/search?q=technology&show-tags=contributor&api-key=3aea33b3-aea1-4653-bc35-7ec08c63bad2

        uriBuilder.appendQueryParameter("q", sectionType);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "3aea33b3-aea1-4653-bc35-7ec08c63bad2");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-references", "author");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> n) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_news);
        nAdapter.clear();
        if( n != null && !n.isEmpty()) {
            nAdapter.addAll(n);
        }
    }

    @Override
    public void onLoaderReset (Loader <List<News>> loader) {
        nAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

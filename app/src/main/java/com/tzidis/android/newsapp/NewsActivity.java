package com.tzidis.android.newsapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.empty;
import static java.util.Collections.emptyList;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsArticle>> {
    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static final String LOG_TAG = NewsActivity.class.getName();

    //URL for book data from the guardian site
    private static final String BASE_REQUEST_URL =
            "http://content.guardianapis.com/search?api-key=cdc88897-3aff-4bed-accf-4e9dd0b1d448" +
                    "&show-fields=firstPublicationDate%2Cthumbnail%2Cstandfirst%2Cheadline";

    //Variable for the EmptyStateTextView
    private TextView mEmptyStateTextView;

    //Progress Bar
    private View loadingIndicator;

    //URL for the Loader
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Create a NewsAdapter, whose data source is a list of
        // NewsArticle s. The adapter knows how to create list item views for each item
        // in the list.
        mAdapter = new NewsAdapter(this, new ArrayList<NewsArticle>());
        mRecyclerView.setAdapter(mAdapter);

        loadingIndicator = findViewById(R.id.loading_spinner);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        if (isConnected()) {

            loadingIndicator.setVisibility(View.VISIBLE);
            url = BASE_REQUEST_URL;
            mEmptyStateTextView.setVisibility(View.GONE);
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(1, null, NewsActivity.this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                try {
                    URLEncoder.encode(query, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Uri baseUri = Uri.parse(BASE_REQUEST_URL);
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("q", query);
                url = uriBuilder.toString();

                // Get a reference to the LoaderManager, in order to interact with loaders.
                LoaderManager loaderManager = getLoaderManager();

                // Restart the loader. Pass in the int ID constant defined above and pass in null for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).
                loaderManager.restartLoader(1, null, NewsActivity.this);

                // Reset SearchView
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();

                // Set activity title to search query
               NewsActivity.this.setTitle(query);

                //Reset the adapter and restart the loader
                mAdapter.clear();
                loadingIndicator.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setText("");
                getLoaderManager().restartLoader(1, null, NewsActivity.this);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });
        return true;
    }

    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> newsArticles) {

        //Set visibility of progress bar to GONE
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link NewsArticle}s, then add them to the adapter's
        // data set. This will trigger the RecyclerView to update.
        if (newsArticles != null && !newsArticles.isEmpty()) {
            mAdapter.addAll(newsArticles);
        } else {
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_articles);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {

        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    //Helper method to check network service
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }



}


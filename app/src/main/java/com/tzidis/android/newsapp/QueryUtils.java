package com.tzidis.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.tzidis.android.newsapp.NewsActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving book data from Google Books.
 */

public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link NewsArticle} objects.
     */
    public static List<NewsArticle> fetchNewsArticleData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsArticle}s
        List<NewsArticle> newsArticles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link NewsArticle}s
        return newsArticles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link NewsArticle} object by parsing out information
     * about the first article from the input newsArticleJSON string.
     */
    private static List<NewsArticle> extractFeatureFromJson(String newsArticleJSON) {

        String sectionName;
        String firstPublicationDate;
        String webUrl;
        String headline;
        String thumbnail;

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsArticleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding NewsArticles to
        List<NewsArticle> newsArticles = new ArrayList<>();

        // Try to parse the JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject jsonObj = new JSONObject(newsArticleJSON);
            JSONObject response = jsonObj.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            // looping through all books
            for (int i = 0; i < results.length(); i++) {
                JSONObject c = results.getJSONObject(i);
                sectionName = c.getString("sectionName");
                webUrl = c.getString("webUrl");
                JSONObject d = c.getJSONObject("fields");
                headline = d.getString("headline");
                firstPublicationDate = d.getString("firstPublicationDate");
                if (d.has("thumbnail")) {
                    thumbnail = d.getString("thumbnail");
                }else{
                    thumbnail = "";
                }

                // adding strings to books array list
                newsArticles.add(new NewsArticle(sectionName, firstPublicationDate, webUrl, headline, thumbnail));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Guardian JSON results", e);
        }

        // Return the list of earthquakes
        return newsArticles;
    }

}

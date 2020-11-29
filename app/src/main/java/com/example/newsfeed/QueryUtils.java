package com.example.newsfeed;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class QueryUtils {

    private static final String TAG = "QueryUtils";

    private QueryUtils() {
    }

    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(TAG, "makeHttpRequest: Error, ", e);
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

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<News> extractResultsFromJson(String newsJSON) {
        ArrayList<News> newsList = new ArrayList<>();

        if (TextUtils.isEmpty(newsJSON)) return null;

        try {
            JSONObject root = new JSONObject(newsJSON);
            JSONObject response = root.getJSONObject("response");
            JSONArray resultsArray = response.getJSONArray("results");

            if (resultsArray.length() > 0) {
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject newsDetails = resultsArray.getJSONObject(i);

                    String title = newsDetails.getString("webTitle");
                    String date = newsDetails.getString("webPublicationDate");
                    String url = newsDetails.getString("webUrl");

                    JSONObject fields;
                    String thumbnail;
                    if (!newsDetails.has("fields")) {
                        thumbnail = "https://th.bing.com/th/id/OIP.otU8lwbjzEgH4MB7dKtrjgHaHa?pid=Api&rs=1";
                    } else {
                        fields = newsDetails.getJSONObject("fields");
                        thumbnail = fields.getString("thumbnail");
                    }

                    News news = new News(title, thumbnail, date, url);
                    Log.d(TAG, "News: " + title + date);
                    newsList.add(news);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "extractResultsFromJson: Error, ", e);
        }

        return newsList;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static ArrayList<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractResultsFromJson(jsonResponse);
    }
}

package com.example.android.newsfeed;

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

import java.text.SimpleDateFormat;
import java.util.Date;

public final class NewsUtil {

    private NewsUtil() {

    }

    public static List<News> fetchNewsData (String requestUrl) {
        try {
            Thread.sleep(3000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch(IOException e) {
            Log.e("Error", "Error closing input stream", e);
        }

        List<News> news = extractFeatureFromJson(jsonResponse);

        return news;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Error", "Error with creating URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream input = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200) {
                input = urlConnection.getInputStream();
                jsonResponse = readFromStream(input);
            } else {
                Log.e("Error", "Error response code " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Error", "Problem retrieving the news JSON results. ", e);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(input != null) {
                input.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader isr = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(isr);
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<News> extractFeatureFromJson (String newsJSON) {
        ArrayList<News> news = new ArrayList<>();
        if(TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        try {
            JSONObject jsonRootObject = new JSONObject(newsJSON);
            JSONObject jsonObj = jsonRootObject.getJSONObject("response");
            JSONArray jsonArray = jsonObj.getJSONArray("results");
            for(int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.getString("webTitle");
                String section = jsonObject.getString("sectionName");
                String time = jsonObject.getString("webPublicationDate");
                String url = jsonObject.getString("webUrl");
                StringBuilder author = new StringBuilder();

                JSONArray newstags = jsonObject.getJSONArray("tags");

                /*
                    This if/else statement is to test whether the author of this articles exists or not
                    The first one tests if there is no author name, it is stated as "Anonymous
                    The second one tests if there is only 1 author, it is automatically used on the first array list
                    The third one tests if there is more than 1 author contributing to this article and to list them.
                 */

                if(newstags.length() == 0) {
                    author.append("Anonymous");
                }else if(newstags.length() == 1) {
                    JSONObject authorObject = newstags.getJSONObject(0);
                    String anAuthor = authorObject.getString("webTitle");
                    author.append(anAuthor);
                }
                else {
                    for(int j = 0;j < newstags.length(); j++) {
                        JSONObject jObject = newstags.getJSONObject(j);
                        String anAuthor = jObject.getString("webTitle");
                        author.append(anAuthor);
                        if(j == newstags.length()) {
                            author.append("");
                        }else {
                            author.append(", ");
                        }
                    }
                }

                news.add(new News(title, author.toString(), section, time, url));
            }
            return news;
        } catch(JSONException e) {
            Log.e("NewsUtil", "Problem parsing the news JSON results", e);
        }

        return null;
    }
}

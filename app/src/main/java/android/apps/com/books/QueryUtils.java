package android.apps.com.books;

import android.graphics.Bitmap;
import android.net.Uri;
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

/**
 * Helper methods related to requesting and receiving book data from USGS.
 */
public class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Base URL for fetching books JSON response
     */
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google book dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBooks(String item) throws IOException {
        // Create URL object
        URL url = createUrl(item);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }

    private static URL createUrl(String item) {
        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", item);
        uriBuilder.appendQueryParameter("printTypes", "books");
        uriBuilder.appendQueryParameter("maxResults", "40");

        URL url = null;
        try {
            url = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
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
            Log.e(LOG_TAG, "Problem retrieving the Book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
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
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            if(baseJsonResponse.getInt("totalItems") == 0){
                return null;
            }
            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or books).
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            // For each book in the bookArray, create an {@link Book} object
            for (int i = 0; i < bookArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of info
                // for that book.
                JSONObject info = currentBook.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                String title = "";
                if (info.has("title")) {
                    title = info.getString("title");
                }
                // Extract the value for the key called "authors"
                List<String> authors = authors = new ArrayList<>();
                if (info.has("authors")) {
                    JSONArray jsonAuthors = info.getJSONArray("authors");
                    for (int a = 0; a < jsonAuthors.length(); a++) {
                        authors.add(jsonAuthors.getString(a));
                    }
                } else {
                    authors.add("N/A");
                }

                // Extract the value for the key called "publisher"
                String publisher = "";
                if (info.has("publisher")) {
                    publisher = info.getString("publisher");
                }

                // Extract the value for the key called "publishedDate"
                String publishedDate = "";
                if (info.has("publishedDate")) {
                    publishedDate = info.getString("publishedDate");
                }

                // Extract the value for the key called "description"
                String description = "";
                if (info.has("description")) {
                    description = "\"" + info.getString("description") + "\"";
                }

                // Extract the value for the key called "industryIdentifiers"
                // getting all identifiers from the JSONArray
                List<String> identifiers = identifiers = new ArrayList<>();
                if (info.has("industryIdentifiers")) {
                    JSONArray jsonIsbn = info.getJSONArray("industryIdentifiers");
                    for (int a = 0; a < jsonIsbn.length(); a++) {
                        JSONObject identity = jsonIsbn.getJSONObject(a);
                        String type = identity.getString("type");
                        String identifier = identity.getString("identifier");
                        String id = "";
                        if (type.contains("ISBN")) {
                            id = type + ": ";
                        }
                        id += identifier;
                        identifiers.add(id);
                    }
                } else {
                    identifiers.add("N/A");
                }

                // Extract the value for the key called "pageCount"
                int pageCount = 0;
                if (info.has("pageCount")) {
                    pageCount = info.getInt("pageCount");
                }
                // Extract the value for the key called "averageRating"
                float averageRating = 0;
                if (info.has("averageRating")) {
                    averageRating = (float) info.getInt("averageRating");
                }

                // Extract the value for the key called "imageLinks"
                String imageLink = "";
                Bitmap bitmap = null;
                if (info.has("imageLinks")) {
                    JSONObject imagelinkJson = info.getJSONObject("imageLinks");
                    if (imagelinkJson.has("thumbnail")) {
                        imageLink = imagelinkJson.getString("thumbnail");
                        imageLink = imageLink.substring(0, 4) + "s" + imageLink.substring(4);
                    }
                }

                // Extract the value for the key called "previewLink"
                String previewLink = "";
                if (info.has("previewLink")) {
                    previewLink = info.getString("previewLink");
                    previewLink = previewLink.substring(0, 4) + "s" + previewLink.substring(4);
                }


                // Create a new {@link Book} object with the helper Builder class
                Book book = new Book.Builder(title)
                        .addAuthors(authors)
                        .addPublisher(publisher)
                        .addPublishedDate(publishedDate)
                        .adddescription(description)
                        .addidentifiers(identifiers)
                        .addpageCount(pageCount)
                        .addratings(averageRating)
                        .addImageLink(imageLink)
                        .addpreviewLink(previewLink)
                        .build();

                // Add the new {@link book} to the list of books.
                books.add(book);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }
}

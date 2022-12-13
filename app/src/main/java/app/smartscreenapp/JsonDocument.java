package app.smartscreenapp;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JsonDocument {
    static final String TAG_DOCUMENT = "TAG_document";

    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private final StringBuilder bufferSB = new StringBuilder();
    private final ArrayList<String> titleStringArrays = new ArrayList<>();
    private final ArrayList<String> videoURLArrays = new ArrayList<>();
    private final ArrayList<Uri> posterURIArrays = new ArrayList<>();
    private int lineCount = 0;

    public JsonDocument(Application application) {
        super();
        Log.d(TAG_DOCUMENT, "Created Json Document Class");

        if (application != null) {
            try {
                inputStream = application.getAssets().open("jsons/data.json");
            } catch (IOException e) {
                e.printStackTrace();
            }

            readLine();
            getAllData();
        } else {
            Log.e(TAG_DOCUMENT, "Context is Null");
        }
    }

    private void makeBuffer() {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
    }

    private void readLine() {
        try {
            makeBuffer();
            String lineStr = bufferedReader.readLine();
            while (lineStr != null) {
                bufferSB.append(lineStr).append("\n");
                lineStr = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG_DOCUMENT, "IOException Occurred");
        }
    }

    private void getAllData() {
        try {
            String bufferStr = bufferSB.toString();
            JSONObject videoJsonObject = new JSONObject(bufferStr);
            JSONArray videoJsonArray = videoJsonObject.getJSONArray("videos"); //배열의 이름
            lineCount = videoJsonArray.length();

            for (int i = 0; i < lineCount; i++) {
                JSONObject Object = videoJsonArray.getJSONObject(i);
                String title = Object.getString("title");
                titleStringArrays.add(title);
                String videoURL = Object.getString("sources")
                        .substring(2, Object.getString("sources").length() - 2)
                        .replace("\\/", "/");
                videoURLArrays.add(videoURL);
                posterURIArrays.add(Uri.parse(Object.getString("poster")));
            }
        } catch (JSONException e) {
            Log.e(TAG_DOCUMENT, "JSONException Occurred");
        }
    }

    public ArrayList<String> getTitles() {
        return titleStringArrays;
    }

    public ArrayList<String> getVideoURLs() {
        return videoURLArrays;
    }

    public ArrayList<Uri> getPosterURIs() {
        return posterURIArrays;
    }

    public int getItemCount() {
        return lineCount;
    }
}

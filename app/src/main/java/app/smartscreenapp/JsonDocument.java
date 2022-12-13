package app.smartscreenapp;

import android.app.Activity;
import android.content.Context;
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
    Context context;
    static final String TAG_DOCUMENT = "TAG_document";

    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private StringBuilder bufferSB = new StringBuilder();
    private JSONObject videoJsonObject;
    private ArrayList<String> titleStringArrays = new ArrayList<>();
    private ArrayList<String> videoURLArrays = new ArrayList<>();
    private ArrayList<Uri> posterURIArrays = new ArrayList<>();
    private int lineCount = 0;

    public JsonDocument(Activity activity) {
        super();
        context = activity.getApplicationContext();
        Log.d(TAG_DOCUMENT, "Created Json Document Class");

        if (context != null) {
            readLine();
            getAllData();
        } else {
            Log.e(TAG_DOCUMENT, "Context is Null");
        }
    }

    private void readFile() throws IOException {
        inputStream = context.getAssets().open("json/data.json");
    }

    private void makeBuffer() {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
    }

    private void readLine() {
        try {
            readFile();
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
            if (bufferSB != null) {
                String bufferStr = bufferSB.toString();
                videoJsonObject = new JSONObject(bufferStr);
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
            } else {
                Log.e(TAG_DOCUMENT,"buffer is empty");
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

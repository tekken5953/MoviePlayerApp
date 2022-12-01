package app.smartscreenapp;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<VideoListItem> mList = new ArrayList<>();
    VideoListAdapter adapter = new VideoListAdapter(mList);
    ArrayList<String> enter_uri = new ArrayList<>();
    ArrayList<String> enter_title = new ArrayList<>();
    MediaMetadataRetriever retriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.main_recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        retriever = new MediaMetadataRetriever();

        try {
            InputStream is = getAssets().open("jsons/data.json");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder buffer = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line).append("\n");
                line = reader.readLine();
            }

            //json파일명을 가져와서 String 변수에 담음
            String json = buffer.toString();
            JSONObject jsonObject = new JSONObject(json);

            //배열로된 자료를 가져올때
            JSONArray Array = jsonObject.getJSONArray("videos");//배열의 이름
            for (int i = 0; i < Array.length(); i++) {
                JSONObject Object = Array.getJSONObject(i);
                String title = Object.getString("title");
                enter_title.add(title);
                String url = Object.getString("sources").substring(2, Object.getString("sources").length() - 2);
                String url_final = url.replace("\\/", "/");
                enter_uri.add(url_final);
                Uri uri = Uri.parse("http://storage.googleapis.com/gtv-videos-bucket/sample/" + Object.getString("thumb"));
                String runtime = Object.getString("runtime");

                addItem(uri,
                        Object.getString("title"),
                        runtime);
                adapter.notifyItemInserted(i);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(MainActivity.this, PlayerViewActivity.class);
            intent.putExtra("uri", enter_uri.get(position));
            intent.putExtra("title", enter_title.get(position));
            startActivity(intent);
        });
    }

    private void addItem(Uri thumbnail, String title, String time) {
        VideoListItem item = new VideoListItem(thumbnail, title, time);
        item.setThumbnail(thumbnail);
        item.setTitle(title);
        item.setTime(time);

        mList.add(item);
    }

    private String getPlayTime(String path) {
        retriever.setDataSource(path);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);
        long duration = timeInmillisec / 1000;
        long minutes = (duration) / 60;
        long seconds = duration - (minutes * 60);
        if (minutes == 0) {
            return "0:" + seconds;
        } else if (seconds == 0) {
            return minutes + ":00";
        } else {
            return minutes + ":" + seconds;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
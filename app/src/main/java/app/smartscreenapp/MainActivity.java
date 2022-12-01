package app.smartscreenapp;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import app.smartscreenapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<VideoListItem> mList = new ArrayList<>();
    VideoListAdapter adapter = new VideoListAdapter(mList);
    ArrayList<String> enter_uri = new ArrayList<>();
    ArrayList<String> enter_title = new ArrayList<>();
    MediaMetadataRetriever retriever;
    int currentItemPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retriever = new MediaMetadataRetriever();
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPager.setOffscreenPageLimit(3); // 관리하는 페이지 수. default = 1
        // item_view 간의 양 옆 여백을 상쇄할 값
//        int offsetBetweenPages = getResources().getDimensionPixelOffset(R.dimen.offsetBetweenPages);
        binding.viewPager.setPageTransformer((page, position) -> {
//            float myOffset = position * -(2 * offsetBetweenPages);
            if (position < -1) {
//                page.setTranslationX(-myOffset);
            } else if (position <= 1) {
                // Paging 시 Y축 Animation 배경색을 약간 연하게 처리
                float min = 0.8f;
                float scaleFactor = Math.min(min, 1 - Math.abs(position));
//                page.setTranslationX(myOffset);
                page.setScaleY(scaleFactor);
//                page.setAlpha(scaleFactor);
            } else {
//                page.setAlpha(0f);
//                page.setTranslationX(myOffset);
            }
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentItemPosition = position;
                Log.d("tag_main", "position is " + position);
                binding.viewPagerTitle.setText(enter_title.get(position));
                if (position == 0) {
                    binding.leftArrow.setVisibility(View.INVISIBLE);
                } else if (position == adapter.getItemCount() - 1) {
                    binding.rightArrow.setVisibility(View.INVISIBLE);
                } else {
                    binding.leftArrow.setVisibility(View.VISIBLE);
                    binding.rightArrow.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.leftArrow.bringToFront();
        binding.rightArrow.bringToFront();

        binding.rightArrow.setOnClickListener(v -> {
            if (currentItemPosition < adapter.getItemCount() - 1) {
                binding.viewPager.setCurrentItem(currentItemPosition + 1, true);
            }
        });
        binding.leftArrow.setOnClickListener(v -> {
            if (currentItemPosition > 0) {
                binding.viewPager.setCurrentItem(currentItemPosition - 1, true);
            }
        });

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
                Uri uri = Uri.parse(Object.getString("poster"));
                addItem(uri);

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

    private void addItem(Uri thumbnail) {
        VideoListItem item = new VideoListItem(thumbnail);
        item.setThumbnail(thumbnail);

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
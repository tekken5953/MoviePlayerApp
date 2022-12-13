package app.smartscreenapp;

import static app.smartscreenapp.PlayerViewActivity.TAG_LIFECYCLE;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import java.util.concurrent.CompletableFuture;

import app.smartscreenapp.databinding.ActivityMainBinding;
//https://developer.android.com/training/transitions/start-activity?hl=ko 공유가 있는 애니메이션

public class MainActivity extends AppCompatActivity {
    //https://furang-note.tistory.com/25
    ActivityMainBinding binding;
    ArrayList<VideoListItem> mList = new ArrayList<>();
    VideoListAdapter adapter = new VideoListAdapter(mList);
    Handler handler;
    JsonDocument jsonDocument = new JsonDocument(MainActivity.this);
    final String TAG_MAIN = "tag_main";

    int currentItemPosition = 0;

    @Override
    protected void onResume() {
        super.onResume();
        binding.viewPager.setVisibility(View.GONE);
        makeScreenPortrait();
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed((Runnable) () -> binding.viewPager.setVisibility(View.VISIBLE), 300);
        binding.leftArrow.bringToFront();
        binding.rightArrow.bringToFront();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LifeCycleChecker checker = new LifeCycleChecker();
        checker.onCreate();

        settingViewPager();

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

        adapter.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(MainActivity.this, PlayerViewActivity.class);
            intent.putExtra("url", jsonDocument.getVideoURLs().get(position));
            intent.putExtra("title", binding.viewPagerTitle.getText().toString());
            SharedPreferenceManager.setString(this, "recent", jsonDocument.getVideoURLs().get(position));
            startActivity(intent);
            makeScreenLandscape();
        });


        for (int i = 0; i < jsonDocument.getItemCount(); i++) {
            binding.viewPagerTitle.setText(jsonDocument.getTitles().get(i));
            addItem(jsonDocument.getPosterURIs().get(i));
            adapter.notifyItemInserted(i);
        }
    }

    private void settingViewPager() {
        // Setting ViewPager
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPager.setOffscreenPageLimit(3); // 관리하는 페이지 수. default = 1
        // item_view 간의 양 옆 여백을 상쇄할 값
        binding.viewPager.setPageTransformer((page, position) -> {
            Log.d(TAG_MAIN, "position : " + position);
            if (position % 1 != 0) {
                float min = 0.8f;
                float scaleFactor = Math.min(min, 1 - Math.abs(position));
                page.setScaleY(scaleFactor);
//                binding.viewPager.setAlpha(0.8f);
            }
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentItemPosition = position;
                Log.d(TAG_MAIN, "position is " + position);
                binding.viewPagerTitle.setText(jsonDocument.getTitles().get(position));
                if (position == 0) {
                    binding.leftArrow.setVisibility(View.GONE);
                } else if (position == adapter.getItemCount() - 1) {
                    binding.rightArrow.setVisibility(View.GONE);
                } else {
                    binding.leftArrow.setVisibility(View.VISIBLE);
                    binding.rightArrow.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addItem(Uri imgUri) {
        VideoListItem item = new VideoListItem(imgUri);
        item.setThumbnail(imgUri);
        mList.add(item);
    }

    private void makeScreenPortrait() {
        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void makeScreenLandscape() {
        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
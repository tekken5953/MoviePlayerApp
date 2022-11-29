package app.smartscreenapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import app.smartscreenapp.databinding.ActivityPlayerViewBinding;

public class PlayerViewActivity extends AppCompatActivity {
    //https://kmight0518.tistory.com/50 ExoPlayer 참조
    //https://velog.io/@eia51/Android-ExoPlayer-%EC%BD%94%EB%8D%B1-%EA%B2%80%EC%A6%9D%ED%95%98%EA%B8%B0 TrackSelector
    //https://jinha3211.tistory.com/28 샘플 무료 동영상 URL
    ActivityPlayerViewBinding binding;

    ExoPlayer exoPlayer;
    String uri, title;
    boolean isPlaying = false;
    final String TAG_PLAYING = "tag_playing";

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getInsetsController().hide(WindowInsets.Type.statusBars());
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FullScreenMode(this);

        uri = getIntent().getExtras().getString("uri");
        title = getIntent().getExtras().getString("title");
        exoPlayer = new ExoPlayer.Builder(this).build();
        MediaItem firstItem = MediaItem.fromUri(Uri.parse(uri));
        exoPlayer.addMediaItem(0, firstItem);
        binding.back.setOnClickListener(v -> finish());
        binding.title.setText(title);
        binding.playbtn.setOnClickListener(v -> {
            if (isPlaying) {
                isPlaying = false;
                exoPlayer.pause();
            } else {
                isPlaying = true;
                exoPlayer.play();
            }
            isPlayVideo();
        });

        binding.dashPrev.setOnClickListener(v-> {
            int rewind = (int) exoPlayer.getCurrentPosition();
            rewind = rewind - 10000; // 10000 = 10 Seconds
            exoPlayer.seekTo(rewind);
            binding.playerView.showController();
            Log.d(TAG_PLAYING,"10초 뒤로");
        });

        binding.dashForward.setOnClickListener(v-> {
            int forward = (int) exoPlayer.getCurrentPosition();
            forward = forward + 10000; // 10000 = 10 Seconds
            exoPlayer.seekTo(forward);
            binding.playerView.showController();
            Log.d(TAG_PLAYING,"10초 앞으로");
        });

        binding.playerView.setControllerShowTimeoutMs(3000);
        binding.playerView.setPlayer(exoPlayer);
        ControllerVisibleChanged();

        exoPlayer.prepare();
        exoPlayer.play();
        isPlaying = true;
        isPlayVideo();
    }

    private void isPlayVideo() {
        if (isPlaying) {
            Log.d(TAG_PLAYING, "동영상 재생 됨");
            binding.playbtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null));
        } else {
            Log.d(TAG_PLAYING, "동영상 일시정지 됨");
            binding.playbtn.setVisibility(View.VISIBLE);
            binding.playbtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.play, null));
        }
    }


    private void ControllerVisibleChanged() {
        binding.playerView.setControllerVisibilityListener(visibility -> {
            if (visibility == View.VISIBLE) {
                Log.d(TAG_PLAYING, "컨트롤러 보임");
                binding.back.setVisibility(View.VISIBLE);
                binding.title.setVisibility(View.VISIBLE);
                binding.dashForward.setVisibility(View.VISIBLE);
                binding.dashPrev.setVisibility(View.VISIBLE);
                binding.playbtn.setVisibility(View.VISIBLE);
            } else if (visibility == View.GONE) {
                Log.d(TAG_PLAYING, "컨트롤러 사라짐");
                binding.back.setVisibility(View.GONE);
                binding.title.setVisibility(View.GONE);
                binding.dashForward.setVisibility(View.GONE);
                binding.dashPrev.setVisibility(View.GONE);
                binding.playbtn.setVisibility(View.GONE);
            }
        });
    }

    // 화면을 풀 스크린으로 사용합니다
    public void FullScreenMode(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
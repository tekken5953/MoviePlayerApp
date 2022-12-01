package app.smartscreenapp;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.SeekBar;

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
    private ActivityPlayerViewBinding binding;
    ExoPlayer exoPlayer;
    String uri, title;
    boolean isPlaying = false;
    boolean isControllerShowing = true;
    final String TAG_PLAYING = "tag_playing";
    Handler handler;
    AudioManager audioManager;

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.stop();
        SharedPreferenceManager.setInt(this, "bright", binding.mySeekBar.getProgress());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                binding.setControlText.setText(String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));
                AutoHideSettingControl(R.drawable.volume);
                Log.d(TAG_PLAYING, "볼륨 업");
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                binding.setControlText.setText(String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
                } else {
                    AutoHideSettingControl(R.drawable.no_volume);
                }
                Log.d(TAG_PLAYING, "볼륨 다운");
                return true;
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FullScreenMode();
        uri = getIntent().getExtras().getString("uri");
        title = getIntent().getExtras().getString("title");
        exoPlayer = new ExoPlayer.Builder(this).build();
        MediaItem firstItem = MediaItem.fromUri(Uri.parse(uri));
        handler = new Handler();
        exoPlayer.addMediaItem(0, firstItem);
        binding.title.setText(title);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        int progress = SharedPreferenceManager.getInt(this, "bright");

        if (progress != -1) {
            binding.mySeekBar.setProgress(progress);
        } else {
            binding.mySeekBar.setProgress(70);
        }

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

        binding.mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 10) {
                    progress = 10;
                }
                lp.screenBrightness = (float) progress / 100;
                getWindow().setAttributes(lp);
                binding.setControlText.setText(progress + "%");
                AutoHideSettingControl(R.drawable.bright);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.dashPrev.setOnClickListener(v -> {
            int rewind = (int) exoPlayer.getCurrentPosition();
            rewind = rewind - 10000; // 10000 = 10 Seconds
            exoPlayer.seekTo(rewind);
            binding.playerView.showController();
            Log.d(TAG_PLAYING, "10초 뒤로");
        });

        binding.dashForward.setOnClickListener(v -> {
            int forward = (int) exoPlayer.getCurrentPosition();
            forward = forward + 10000; // 10000 = 10 Seconds
            exoPlayer.seekTo(forward);
            binding.playerView.showController();
            Log.d(TAG_PLAYING, "10초 앞으로");
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
                isControllerShowing = true;
                Log.d(TAG_PLAYING, "컨트롤러 보임");
                showSystemUI();
                showSettings();
            } else if (visibility == View.GONE) {
                isControllerShowing = false;
                Log.d(TAG_PLAYING, "컨트롤러 사라짐");
                FullScreenMode();
                hideSettings();
            }
        });
    }

    private void showSettings() {
        binding.title.setVisibility(View.VISIBLE);
        binding.dashForward.setVisibility(View.VISIBLE);
        binding.dashPrev.setVisibility(View.VISIBLE);
        binding.playbtn.setVisibility(View.VISIBLE);
        binding.mySeekBar.setVisibility(View.VISIBLE);
    }

    private void hideSettings() {
        binding.title.setVisibility(View.GONE);
        binding.dashForward.setVisibility(View.GONE);
        binding.dashPrev.setVisibility(View.GONE);
        binding.playbtn.setVisibility(View.GONE);
        binding.mySeekBar.setVisibility(View.GONE);
    }

    // 화면을 풀 스크린으로 사용합니다
    public void FullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    private void AutoHideSettingControl(int drawable) {
        binding.setControlImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                drawable, null));
        if (binding.setControlGroup.getVisibility() == View.GONE) {
            binding.setControlGroup.setVisibility(View.VISIBLE);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                binding.setControlGroup.setVisibility(View.GONE);
            }, 1000);
        }
    }
}
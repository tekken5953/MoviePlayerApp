package app.smartscreenapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import app.smartscreenapp.databinding.ActivityPlayerViewBinding;

public class PlayerViewActivity extends AppCompatActivity {
    //https://kmight0518.tistory.com/50 ExoPlayer 참조
    //https://velog.io/@eia51/Android-ExoPlayer-%EC%BD%94%EB%8D%B1-%EA%B2%80%EC%A6%9D%ED%95%98%EA%B8%B0 TrackSelector
    //https://jinha3211.tistory.com/28 샘플 무료 동영상 URL
    private ActivityPlayerViewBinding binding;
    ExoPlayer exoPlayer;
    boolean isPlaying = false;
    boolean isControllerShowing = true;
    final String TAG_PLAYING = "tag_playing";
    static final String TAG_LIFECYCLE = "tag_lifecycle";
    AudioManager audioManager;
    long currentPlayTime = 0;
    MediaItem firstItem;
    boolean isTouch = false;
    float saveUY, saveDY;
    Context context = PlayerViewActivity.this;

    @Override
    protected void onStop() {
        Log.d(TAG_LIFECYCLE, "onStop");
        super.onStop();
        exoPlayer.stop();
        SharedPreferenceManager.setInt(context,"bright", binding.mySeekBar.getProgress());
        if ((int) exoPlayer.getCurrentPosition() / 1000 != (int) exoPlayer.getDuration() / 1000) {
            if ((int) exoPlayer.getCurrentPosition() / 1000 != 0) {
                SharedPreferenceManager.setLong(context,binding.title.getText().toString() + "_time",
                        exoPlayer.getCurrentPosition());
            } else {
                SharedPreferenceManager.setLong(context,binding.title.getText().toString() + "_time",
                        0);
            }
        } else {
            if (SharedPreferenceManager.getAllList(context).containsKey(binding.title.getText().toString() + "_time"))
                SharedPreferenceManager.removeKey(context,binding.title.getText().toString() + "_time");
            else
                Log.e(TAG_PLAYING, "that KeySet is Empty");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_LIFECYCLE, "onResume");
        playLoadTime();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (currentPlayTime != 0) {
                exoPlayer.clearMediaItems();
                exoPlayer.setMediaItem(firstItem, currentPlayTime);
                if (!exoPlayer.isPlaying()) {
                    exoPlayer.prepare();
                    exoPlayer.play();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                volumeSetting(AudioManager.ADJUST_RAISE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                volumeSetting(AudioManager.ADJUST_LOWER);
                return true;
            case KeyEvent.KEYCODE_BACK:
                closePlayer();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LIFECYCLE, "onCreate");
        binding = ActivityPlayerViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FullScreenMode();
        String title = null;
        String uriStr = null;
        if (getIntent().getExtras().getString("url") != null &&
                getIntent().getExtras().getString("title") != null) {
            uriStr = getIntent().getExtras().getString("url");
            title = getIntent().getExtras().getString("title");
        }
        exoPlayer = new ExoPlayer.Builder(this).build();
        if (uriStr != null)
            firstItem = MediaItem.fromUri(Uri.parse(uriStr));
        exoPlayer.addMediaItem(0, firstItem);
        if (title != null)
            binding.title.setText(title);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        int progress = SharedPreferenceManager.getInt(context,"bright");

        if (progress != SharedPreferenceManager.DEFAULT_VALUE_INT) {
            binding.mySeekBar.setProgress(progress);
        } else {
            // default bright
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

        binding.playerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float width = binding.playerView.getWidth();
                float getX = event.getX();
                float getY = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isTouch) {
                            if (getX >= width * 3 / 4) {
                                isTouch = true;
                                Log.d("testtest", "DOWN y : " + getY);
                                saveDY = getY;
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (isTouch) {
                            isTouch = false;
                            Log.d("testtest", "UP y : " + getY);
                            saveUY = getY;
                            if (saveUY - saveDY > 300) {
                                closePlayer();
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.d("testtest", "Move X is " + event.getX());
                        Log.d("testtest", "Move Y is " + event.getX());

                }
                return false;
            }
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
        isPlayVideo();

        exoPlayer.prepare();
        exoPlayer.play();
        binding.playbtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.pause, null));
        isPlaying = true;
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
                ShowNavBarOnly();
                showSettings();
            } else if (visibility == View.GONE) {
                isControllerShowing = false;
                Log.d(TAG_PLAYING, "컨트롤러 사라짐");
                FullScreenMode();
                hideSettings();
            }
        });
    }

    private void volumeSetting(int up_down) {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, up_down, AudioManager.FLAG_SHOW_UI);
        binding.setControlText.setText(String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));

        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
            AutoHideSettingControl(R.drawable.volume);
        } else {
            AutoHideSettingControl(R.drawable.no_volume);
        }

        if (up_down == AudioManager.ADJUST_LOWER)
            Log.d(TAG_PLAYING, "볼륨 다운");
        else if (up_down == AudioManager.ADJUST_RAISE)
            Log.d(TAG_PLAYING, "볼륨 업");
    }

    private void playLoadTime() {
        int playTime = (int) SharedPreferenceManager.getLong(context,binding.title.getText().toString() + "_time") / 1000;
        if (playTime != 0 && playTime != SharedPreferenceManager.DEFAULT_VALUE_LONG)
            currentPlayTime = SharedPreferenceManager.getLong(context,binding.title.getText().toString() + "_time");
        else
            currentPlayTime = 0;
    }

    private void showSettings() {
        binding.title.setVisibility(View.VISIBLE);
        binding.dashForward.setVisibility(View.VISIBLE);
        binding.dashPrev.setVisibility(View.VISIBLE);
        binding.playbtn.setVisibility(View.VISIBLE);
        binding.mySeekBar.setVisibility(View.VISIBLE);
    }

    private void closePlayer() {
        PlayerViewActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        finish();
        overridePendingTransition(0, R.anim.slide_down);
    }

    private void hideSettings() {
        binding.title.setVisibility(View.GONE);
        binding.dashForward.setVisibility(View.GONE);
        binding.dashPrev.setVisibility(View.GONE);
        binding.playbtn.setVisibility(View.GONE);
        binding.mySeekBar.setVisibility(View.GONE);
    }


    // 몰입모드로 전환됩니다
    public void FullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    // 몰입모드에서 시스템 네비게이션바만 보여줍니다
    private void ShowNavBarOnly() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    private void AutoHideSettingControl(int drawable) {
        binding.setControlImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                drawable, null));
        if (binding.setControlGroup.getVisibility() == View.GONE) {
            binding.setControlGroup.setVisibility(View.VISIBLE);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> binding.setControlGroup.setVisibility(View.GONE), 1000);
        }
    }
}
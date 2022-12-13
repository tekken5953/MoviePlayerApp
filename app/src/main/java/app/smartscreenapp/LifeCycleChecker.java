package app.smartscreenapp;

import static app.smartscreenapp.PlayerViewActivity.TAG_LIFECYCLE;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

public class LifeCycleChecker extends Application implements LifecycleEventObserver {

    public static boolean isForeground = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_STOP) {
            isForeground = false;
            Log.d(TAG_LIFECYCLE, "앱이 백그라운드로 전환");
        } else if (event == Lifecycle.Event.ON_START) {
            isForeground = true;
            Log.d(TAG_LIFECYCLE, "앱이 포그라운드로 전환");
        }
    }
}

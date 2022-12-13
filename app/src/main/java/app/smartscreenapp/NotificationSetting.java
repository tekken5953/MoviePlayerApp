package app.smartscreenapp;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class NotificationSetting {
    NotificationManager manager;
    public void showNotification(Context context) {
        if (context != null) {
            manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            RemoteViews smallLayout = new RemoteViews(context.getPackageName(), R.layout.noti_small_layout);
            RemoteViews largeLayout = new RemoteViews(context.getPackageName(), R.layout.noti_large_layout);
            final String CHANNEL_ID = "channel_player";
            final String CHANNEL_NAME = "Channel_Player";

            Intent intent = new Intent(context, PlayerViewActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(mChannel);
                new NotificationCompat.Builder(context, CHANNEL_ID);
            } else {
                new NotificationCompat.Builder(context);
            }

            builder.setOngoing(true)
                    .setAutoCancel(false)
                    .setSmallIcon(R.drawable.noti_icon)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(smallLayout)
                    .setCategory("AppBarNotification")
                    .setCustomBigContentView(largeLayout);


            Notification notification = builder.build();
            manager.notify(1, notification);
        }
    }
}

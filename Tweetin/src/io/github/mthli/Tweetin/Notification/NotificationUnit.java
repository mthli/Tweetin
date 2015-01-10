package io.github.mthli.Tweetin.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import io.github.mthli.Tweetin.Flag.FlagUnit;

public class NotificationUnit {
    public static void show(
            Context context,
            int smallIconResId,
            int contentTitleResId,
            String contentText
    ) {
        show(context, smallIconResId, contentTitleResId, contentText, null);
    }

    public static void show(
            Context context,
            int smallIconResId,
            int contentTitleResId,
            String contentText,
            PendingIntent pendingIntent
    ) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(smallIconResId)
                .setContentTitle(context.getString(contentTitleResId))
                .setContentText(contentText);
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(FlagUnit.NOTIFICATION_ID, notification);
    }

    public static void cancel(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(FlagUnit.NOTIFICATION_ID);
    }
}
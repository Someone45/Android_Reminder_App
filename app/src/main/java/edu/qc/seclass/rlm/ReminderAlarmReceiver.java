package edu.qc.seclass.rlm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.app.Notification;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String reminderTitle = intent.getStringExtra("REMINDER_TITLE");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "REMINDER_CHANNEL")
                    .setSmallIcon(R.drawable.ic_reminder) // Replace with your icon
                    .setContentTitle("Reminder")
                    .setContentText(reminderTitle)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build()); // 1 is a unique ID for the notification
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}

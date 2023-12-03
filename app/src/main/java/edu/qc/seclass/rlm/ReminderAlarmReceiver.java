package edu.qc.seclass.rlm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.app.Notification;
import androidx.core.app.NotificationCompat;

public class ReminderAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract reminder details from the Intent
        String reminderTitle = intent.getStringExtra("REMINDER_TITLE");

        // Create a notification
        Notification notification = new NotificationCompat.Builder(context, "REMINDER_CHANNEL")
                .setSmallIcon(R.drawable.ic_reminder) // Set your icon here
                .setContentTitle("Reminder")
                .setContentText(reminderTitle)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification); // 1 is a unique id for the notification
    }
}

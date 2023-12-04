package edu.qc.seclass.rlm;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.content.DialogInterface;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.app.PendingIntent;



public class ManageRemindersActivity extends AppCompatActivity {

    private ReminderDbQueries dbQueries;
    private ReminderAdapter reminderAdapter;
    private RecyclerView recyclerView;
    private long listId; // Assume this is passed from the previous activity

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1; // Request code for location permission


    private void checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            boolean hasPermission = alarmManager.canScheduleExactAlarms();
            if (!hasPermission) {
                // Direct user to the system settings for your app
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name); // Define a name for the channel
            String description = getString(R.string.channel_description); // Define the channel description
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("REMINDER_CHANNEL", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder_list);

        // Initialize the database queries helper
        dbQueries = new ReminderDbQueries(this);

        // Get the list ID from the intent (passed from MainActivity)
        listId = getIntent().getLongExtra("LIST_ID", -1);

        // Setup RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderAdapter = new ReminderAdapter(new ArrayList<>(), dbQueries);
        recyclerView.setAdapter(reminderAdapter);

        checkAndRequestExactAlarmPermission();
        createNotificationChannel();

        updateReminders(); // Load reminders

        Button addButton = findViewById(R.id.buttonAddReminder);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddReminderDialog();
            }
        });
    }


    private void showAddReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Reminder");

        // Container for the dialog views
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Radio buttons for selecting reminder type (one-time vs repeating)
        RadioGroup reminderTypeGroup = new RadioGroup(this);
        RadioButton oneTimeButton = new RadioButton(this);
        oneTimeButton.setText("One-time");
        RadioButton repeatButton = new RadioButton(this);
        repeatButton.setText("Repeat Weekly");
        reminderTypeGroup.addView(oneTimeButton);
        reminderTypeGroup.addView(repeatButton);
        layout.addView(reminderTypeGroup);

        // Checkbox for enabling repeat
        CheckBox repeatCheckbox = new CheckBox(this);
        repeatCheckbox.setText("Repeat");
        repeatCheckbox.setVisibility(View.GONE); // Initially hidden
        layout.addView(repeatCheckbox);

        // Container for day selection, initially invisible
        LinearLayout daySelectionLayout = new LinearLayout(this);
        daySelectionLayout.setOrientation(LinearLayout.VERTICAL);
        daySelectionLayout.setVisibility(View.GONE);

        // Container for date selection, initially invisible
        LinearLayout dateSelectionLayout = new LinearLayout(this);
        dateSelectionLayout.setOrientation(LinearLayout.VERTICAL);
        dateSelectionLayout.setVisibility(View.GONE);

        // Checkboxes for each day of the week
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayList<CheckBox> dayCheckboxes = new ArrayList<>();
        for (String day : days) {
            CheckBox dayCheckbox = new CheckBox(this);
            dayCheckbox.setText(day);
            daySelectionLayout.addView(dayCheckbox);
            dayCheckboxes.add(dayCheckbox);
        }

        // Spinner for selecting reminder type
        final Spinner typeSpinner = new Spinner(this);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Appointment", "Chores", "Meetings"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        layout.addView(typeSpinner);

        // Input field for reminder title
        final EditText inputTitle = new EditText(this);
        inputTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        inputTitle.setHint("Reminder Title");
        layout.addView(inputTitle);

        // Input field for date with a DatePickerDialog
        final EditText inputDate = new EditText(this);
        inputDate.setFocusable(false);
        inputDate.setHint("Select Date");
        inputDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ManageRemindersActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> inputDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
            datePickerDialog.show();
        });
        dateSelectionLayout.addView(inputDate);

        // Input field for time with a TimePickerDialog
        final EditText inputTime = new EditText(this);
        inputTime.setFocusable(false);
        inputTime.setHint("Select Time");
        inputTime.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(ManageRemindersActivity.this,
                    (view12, hourOfDay, minute1) -> inputTime.setText(hourOfDay + ":" + minute1), hour, minute, true);
            timePickerDialog.show();
        });
        layout.addView(inputTime);

        // Adding date and day selection layouts to the main layout
        layout.addView(dateSelectionLayout);
        layout.addView(daySelectionLayout);

        // Listener to show/hide day and date selection based on reminder type choice
        reminderTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == repeatButton.getId()) {
                repeatCheckbox.setVisibility(View.VISIBLE);
                dateSelectionLayout.setVisibility(View.GONE);
                daySelectionLayout.setVisibility(View.VISIBLE);
            } else {
                repeatCheckbox.setVisibility(View.GONE);
                dateSelectionLayout.setVisibility(View.VISIBLE);
                daySelectionLayout.setVisibility(View.GONE);
            }
        });

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String type = typeSpinner.getSelectedItem().toString();
            String date = inputDate.getText().toString();
            String time = inputTime.getText().toString();
            boolean isRepeat = repeatButton.isChecked();
            ArrayList<String> selectedDays = new ArrayList<>();
            if (isRepeat) {
                for (CheckBox dayCheckbox : dayCheckboxes) {
                    if (dayCheckbox.isChecked()) {
                        selectedDays.add(dayCheckbox.getText().toString());
                    }
                }
            }

            if (isRepeat && repeatCheckbox.isChecked()){
                for( String day: selectedDays){
                    scheduleRepeatingNotification(title, day, time);
                }
            } else {
                scheduleNotification(time, date, time);
            }

            addNewReminder(title, type, date, time, isRepeat, selectedDays);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void addNewReminder(String title, String type, String date, String time, boolean isRepeat, ArrayList<String> selectedDays) {
        // Check notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            NotificationChannel channel = manager.getNotificationChannel("YOUR_CHANNEL_ID");
            if (channel != null && channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                promptUserToEnableNotifications();
                return; // Stop execution
            }
        }

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return; // Stop execution
        }

        // Proceed with adding the reminder and scheduling notification
        long newRowId = dbQueries.addNewReminder(title, type, date, time, listId, isRepeat, selectedDays);
        if (newRowId != -1) {
            Toast.makeText(this, "Reminder added!", Toast.LENGTH_SHORT).show();
            scheduleNotification(title, date, time); // Schedule the notification
            updateReminders(); // Refresh the data in the RecyclerView
        } else {
            Toast.makeText(this, "Error adding reminder", Toast.LENGTH_SHORT).show();
        }

        if (isRepeat) {
            for (String day : selectedDays) {
                scheduleRepeatingNotification(title, day, time); // New method to schedule repeating notifications
            }
        } else {
            scheduleNotification(title, date, time); // Existing method for one-time notifications
        }
    }

    private void scheduleRepeatingNotification(String title, String dayOfWeek, String time) {
        Calendar nextAlarmTime = getNextAlarmTime(dayOfWeek, time);

        if (nextAlarmTime == null) {
            Toast.makeText(this, "Error setting reminder for " + dayOfWeek, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ReminderAlarmReceiver.class);
        intent.putExtra("REMINDER_TITLE", title);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flags);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long repeatInterval = AlarmManager.INTERVAL_DAY * 7; // Repeat every week

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextAlarmTime.getTimeInMillis(), repeatInterval, pendingIntent);
    }

    private Calendar getNextAlarmTime(String dayOfWeekStr, String timeStr) {
        int dayOfWeek = getDayOfWeek(dayOfWeekStr);
        if (dayOfWeek == -1) return null; // Invalid day of week

        String[] timeParts = timeStr.split(":");
        int hourOfDay = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar now = Calendar.getInstance();
        Calendar nextAlarm = Calendar.getInstance();
        nextAlarm.set(Calendar.HOUR_OF_DAY, hourOfDay);
        nextAlarm.set(Calendar.MINUTE, minute);
        nextAlarm.set(Calendar.SECOND, 0);
        nextAlarm.set(Calendar.MILLISECOND, 0);

        int today = now.get(Calendar.DAY_OF_WEEK);
        int daysUntilNext = (dayOfWeek - today + 7) % 7;
        if (daysUntilNext == 0) { // Today, but check if time has already passed
            if (nextAlarm.before(now)) {
                daysUntilNext = 7;
            }
        }

        nextAlarm.add(Calendar.DAY_OF_YEAR, daysUntilNext);

        return nextAlarm;
    }

    private int getDayOfWeek(String dayOfWeekStr) {
        switch (dayOfWeekStr.toLowerCase()) {
            case "sunday": return Calendar.SUNDAY;
            case "monday": return Calendar.MONDAY;
            case "tuesday": return Calendar.TUESDAY;
            case "wednesday": return Calendar.WEDNESDAY;
            case "thursday": return Calendar.THURSDAY;
            case "friday": return Calendar.FRIDAY;
            case "saturday": return Calendar.SATURDAY;
            default: return -1;
        }
    }

    private void promptUserToEnableNotifications() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Notifications")
                .setMessage("Notifications are disabled. Please enable them in the app settings.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Intent to open app settings
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                // You can re-attempt adding a reminder here if necessary
            } else {
                Toast.makeText(this, "Location permission is required.", Toast.LENGTH_LONG).show();
                finish(); // Close the activity
            }
        }
    }


    private void updateReminders() {
        List<Reminder> updatedReminders = dbQueries.getRemindersForList(listId);
        reminderAdapter.updateData(updatedReminders); // Ensure this is a List<Reminder>
    }

    private void scheduleNotification(String title, String date, String time) {
        Calendar calendar = Calendar.getInstance();
        // Parse the date and time strings and set the calendar object
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date parsedDate = dateFormat.parse(date);
            Date parsedTime = timeFormat.parse(time);
            calendar.setTime(parsedDate);
            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(parsedTime);
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        Intent intent = new Intent(this, ReminderAlarmReceiver.class);
        intent.putExtra("REMINDER_TITLE", title);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flags);


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void clearAllCheckOffs() {
        List<Reminder> reminders = dbQueries.getRemindersForList(listId);
        for (Reminder reminder : reminders) {
            dbQueries.updateReminderCheckOffState(reminder.getId(), false);
        }
        updateReminders(); // Refresh the list
    }
}

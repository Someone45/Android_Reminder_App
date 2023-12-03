package edu.qc.seclass.rlm;
import android.app.AlarmManager;
import android.app.PendingIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
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

        // Input field for reminder title
        final EditText inputTitle = new EditText(this);
        inputTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        inputTitle.setHint("Reminder Title");
        layout.addView(inputTitle);

        // Spinner for selecting reminder type
        final Spinner typeSpinner = new Spinner(this);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Appointment", "Chores", "Meetings"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        layout.addView(typeSpinner);

        // Input field for date with a DatePickerDialog
        final EditText inputDate = new EditText(this);
        inputDate.setFocusable(false);
        inputDate.setHint("Select Date");
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ManageRemindersActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                inputDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        layout.addView(inputDate);

        // Input field for time with a TimePickerDialog
        final EditText inputTime = new EditText(this);
        inputTime.setFocusable(false);
        inputTime.setHint("Select Time");
        inputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageRemindersActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                inputTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });
        layout.addView(inputTime);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = inputTitle.getText().toString();
                String type = typeSpinner.getSelectedItem().toString();
                String date = inputDate.getText().toString();
                String time = inputTime.getText().toString();

                addNewReminder(title, type, date, time);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addNewReminder(String title, String type, String date, String time) {
        long newRowId = dbQueries.addNewReminder(title, type, date, time, listId);
        if (newRowId != -1) {
            Toast.makeText(ManageRemindersActivity.this, "Reminder added!", Toast.LENGTH_SHORT).show();
            updateReminders(); // Refresh the data in the RecyclerView
        } else {
            Toast.makeText(ManageRemindersActivity.this, "Error adding reminder", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateReminders() {
        List<Reminder> updatedReminders = dbQueries.getRemindersForList(listId);
        reminderAdapter.updateData(updatedReminders); // Ensure this is a List<Reminder>
    }

    private void scheduleNotification(String title, String date, String time) {
        Calendar calendar = Calendar.getInstance();
        // Parse the date and time strings and set the calendar object
        // Assuming date and time are in the format "dd/MM/yyyy" and "HH:mm"
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


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

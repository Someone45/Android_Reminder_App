package edu.qc.seclass.rlm;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class EditReminderListActivity extends AppCompatActivity {

    private ReminderDbQueries dbQueries;
    private ReminderAdapter reminderAdapter;
    private RecyclerView recyclerView;
    private long listId; // if you pass the list ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder_list);

        dbQueries = new ReminderDbQueries(this);

        String listName = getIntent().getStringExtra("LIST_NAME");
        // If you're passing the list ID, retrieve it as well
         listId = getIntent().getLongExtra("LIST_ID", -1);

        recyclerView = findViewById(R.id.recyclerViewReminders); // Update this ID as per your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderAdapter = new ReminderAdapter(new ArrayList<>(), dbQueries);
        recyclerView.setAdapter(reminderAdapter);

        updateReminders(); // Load reminders for the selected list
    }

    private void updateReminders() {
        // Fetch reminders for the selected list and update the adapter
        List<Reminder> reminders = dbQueries.getRemindersForList(listId); // Update this call as needed
        reminderAdapter.updateData(reminders);
    }

}

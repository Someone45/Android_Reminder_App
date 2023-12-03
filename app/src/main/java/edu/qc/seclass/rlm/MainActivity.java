package edu.qc.seclass.rlm;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.DialogInterface;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {
    private ReminderDbQueries dbQueries;
    private ReminderListAdapter reminderListAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database queries helper
        dbQueries = new ReminderDbQueries(this);

        // Setup RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerViewReminderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderListAdapter = new ReminderListAdapter(new ArrayList<>());
        recyclerView.setAdapter(reminderListAdapter);

        reminderListAdapter.setOnItemClickListener(new ReminderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String listName) {
                Intent intent = new Intent(MainActivity.this, ManageRemindersActivity.class);

                long listId = dbQueries.getListIdByName(listName);

                intent.putExtra("LIST_NAME", listName);
                intent.putExtra("LIST_ID", listId);
                startActivity(intent);
            }
        });

        // Setup FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fabNewList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddListDialog();
            }
        });

        updateReminderLists(); // Initial load of data
    }

    private void showAddListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Reminder List");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String listName = input.getText().toString();
                addNewReminderList(listName);
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

    private void addNewReminderList(String listName) {
        long newRowId = dbQueries.addNewReminderList(listName);
        if (newRowId != -1) {
            Toast.makeText(MainActivity.this, "List added!", Toast.LENGTH_SHORT).show();
            updateReminderLists(); // Refresh the data in the RecyclerView
        } else {
            Toast.makeText(MainActivity.this, "Error adding list", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateReminderLists() {
        List<String> updatedLists = dbQueries.getAllReminderLists();
        reminderListAdapter.updateData(updatedLists);
    }

}
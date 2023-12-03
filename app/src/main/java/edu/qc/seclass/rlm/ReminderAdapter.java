package edu.qc.seclass.rlm;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;
import android.widget.CompoundButton;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private List<Reminder> reminders;
    private ReminderDbQueries dbQueries;

    public ReminderAdapter(List<Reminder> reminders, ReminderDbQueries dbQueries) {
        this.reminders = reminders;
        this.dbQueries = dbQueries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.textView.setText(reminder.getTitle()); // Assuming getTitle() method exists
        holder.checkBoxReminder.setChecked(reminder.isChecked());

        holder.checkBoxReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbQueries.updateReminderCheckOffState(reminder.getId(), isChecked); // Assuming getId() method exists
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public CheckBox checkBoxReminder;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.textViewReminder);
            checkBoxReminder = v.findViewById(R.id.checkBoxReminder);
        }
    }

    // Method to update the data of the adapter
    public void updateData(List<Reminder> newReminders) {
        reminders.clear();
        reminders.addAll(newReminders);
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}

package edu.qc.seclass.rlm;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ViewHolder> {
    private List<String> reminderLists;

    // Constructor
    public ReminderListAdapter(List<String> reminderLists) {
        this.reminderLists = reminderLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_list_item, parent, false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String list = reminderLists.get(position);
        holder.textView.setText(list);
    }

    @Override
    public int getItemCount() {
        return reminderLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View v, final OnItemClickListener listener) {
            super(v);
            textView = v.findViewById(R.id.textViewReminderListTitle);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onItemClick(textView.getText().toString());
                    }
                }
            });
        }
    }
    public void updateData(List<String> newLists) {
        reminderLists.clear();
        reminderLists.addAll(newLists);
        notifyDataSetChanged();
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String listName);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
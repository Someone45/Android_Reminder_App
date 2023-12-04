package edu.qc.seclass.rlm;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ViewHolder> {
    private List<String> reminderLists;
    private Set<Integer> selectedItems = new HashSet<>();
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public ReminderListAdapter(List<String> reminderLists) {
        this.reminderLists = reminderLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_list_item, parent, false);
        return new ViewHolder(itemView, listener, longClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String list = reminderLists.get(position);
        holder.textView.setText(list);
        holder.itemView.setSelected(selectedItems.contains(position)); // Highlight selected items
    }

    @Override
    public int getItemCount() {
        return reminderLists.size();
    }

    public void toggleItemSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View v, final OnItemClickListener listener, final OnItemLongClickListener longClickListener) {
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

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(getAdapterPosition());
                        return true;
                    }
                    return false;
                }
            });


        }
    }

    public void updateData(List<String> newLists) {
        reminderLists.clear();
        reminderLists.addAll(newLists);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(String listName);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public String getSelectedItemName() {
        if (getSelectedItemCount() == 1) {
            int selectedPosition = selectedItems.iterator().next();
            return reminderLists.get(selectedPosition);
        }
        return null;
    }

    public Set<Integer> getSelectedItems() {
        return new HashSet<>(selectedItems);
    }

    public String getReminderListNameAtPosition(int position) {
        return reminderLists.get(position);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}

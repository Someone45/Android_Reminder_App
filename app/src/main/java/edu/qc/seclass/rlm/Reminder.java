package edu.qc.seclass.rlm;

import java.util.List;

public class Reminder {
    private long id;
    private String title;
    private String type;
    private String date;
    private String time;
    private boolean isChecked;
    private boolean isRepeat; // Indicates if the reminder is repeating
    private List<String> repeatDays; // Days of the week for repeating reminder

    // Constructor
    public Reminder(long id, String title, String type, String date, String time, boolean isChecked, boolean isRepeat, List<String> repeatDays) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.date = date;
        this.time = time;
        this.isChecked = isChecked;
        this.isRepeat = isRepeat;
        this.repeatDays = repeatDays;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isChecked() {
        return isChecked;
    }

    // Setter for isChecked
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public List<String> getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(List<String> repeatDays) {
        this.repeatDays = repeatDays;
    }
}

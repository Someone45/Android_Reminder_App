package edu.qc.seclass.rlm;

public class Reminder {
    private long id;
    private String title;
    private String type;
    private String date;
    private String time;
    private boolean isChecked;

    // Constructor
    public Reminder(long id, String title, String type, String date, String time, boolean isChecked) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.date = date;
        this.time = time;
        this.isChecked = isChecked;
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
}

package edu.qc.seclass.rlm;
import android.provider.BaseColumns;

public final class ReminderContract {
    // Prevents someone from accidentally instantiating the contract class
    private ReminderContract() {}

    /* This is used to define the contents ot the table for the reminder list */
    public static class ReminderListEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminderList";
        public static final String COLUMN_NAME_TITLE = "title";
    }

    /* This is used to define the contents of each reminder*/
    public static class ReminderEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_CHECKED = "checked"; // 0 for unchecked, 1 for checked
        public static final String COLUMN_NAME_LIST_ID = "listId"; // Foreign key to reminder list
    }
}


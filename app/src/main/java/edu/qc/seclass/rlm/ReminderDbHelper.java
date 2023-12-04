package edu.qc.seclass.rlm;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReminderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1; // Increment the database version
    public static final String DATABASE_NAME = "Reminder.db";

    private static final String SQL_CREATE_REMINDER_LIST_ENTRIES =
            "CREATE TABLE " + ReminderContract.ReminderListEntry.TABLE_NAME + " (" +
                    ReminderContract.ReminderListEntry._ID + " INTEGER PRIMARY KEY," +
                    ReminderContract.ReminderListEntry.COLUMN_NAME_TITLE + " TEXT)";

    private static final String SQL_CREATE_REMINDER_ENTRIES =
            "CREATE TABLE " + ReminderContract.ReminderEntry.TABLE_NAME + " (" +
                    ReminderContract.ReminderEntry._ID + " INTEGER PRIMARY KEY," +
                    ReminderContract.ReminderEntry.COLUMN_NAME_TITLE + " TEXT," +
                    ReminderContract.ReminderEntry.COLUMN_NAME_TYPE + " TEXT," +
                    ReminderContract.ReminderEntry.COLUMN_NAME_DATE + " TEXT," +
                    ReminderContract.ReminderEntry.COLUMN_NAME_TIME + " TEXT," +
                    ReminderContract.ReminderEntry.COLUMN_NAME_LIST_ID + " INTEGER," +
                    ReminderContract.ReminderEntry.COLUMN_NAME_CHECKED + " BOOLEAN," +
                    "FOREIGN KEY(" + ReminderContract.ReminderEntry.COLUMN_NAME_LIST_ID + ") REFERENCES " +
                    ReminderContract.ReminderListEntry.TABLE_NAME + "(" + ReminderContract.ReminderListEntry._ID + "))";

    private static final String SQL_DELETE_REMINDER_LIST_ENTRIES =
            "DROP TABLE IF EXISTS " + ReminderContract.ReminderListEntry.TABLE_NAME;

    private static final String SQL_DELETE_REMINDER_ENTRIES =
            "DROP TABLE IF EXISTS " + ReminderContract.ReminderEntry.TABLE_NAME;

    public ReminderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_REMINDER_LIST_ENTRIES);
        db.execSQL(SQL_CREATE_REMINDER_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_REMINDER_LIST_ENTRIES);
        db.execSQL(SQL_DELETE_REMINDER_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

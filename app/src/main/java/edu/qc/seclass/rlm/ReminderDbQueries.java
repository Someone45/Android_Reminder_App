package edu.qc.seclass.rlm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class ReminderDbQueries {
    private ReminderDbHelper dbHelper;

    public ReminderDbQueries(Context context) {
        dbHelper = new ReminderDbHelper(context);
    }

    public void updateReminderCheckOffState(long reminderId, boolean isChecked) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ReminderContract.ReminderEntry.COLUMN_NAME_CHECKED, isChecked ? 1 : 0);

        String selection = ReminderContract.ReminderEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(reminderId) };

        db.update(ReminderContract.ReminderEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public long addNewReminderList(String title) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ReminderContract.ReminderListEntry.COLUMN_NAME_TITLE, title);

        long newRowId = db.insert(ReminderContract.ReminderListEntry.TABLE_NAME, null, values);
        return newRowId;
    }

    public List<String> getAllReminderLists() {
        List<String> reminderLists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ReminderContract.ReminderListEntry._ID,
                ReminderContract.ReminderListEntry.COLUMN_NAME_TITLE
        };

        Cursor cursor = db.query(
                ReminderContract.ReminderListEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        while (cursor.moveToNext()) {
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(ReminderContract.ReminderListEntry.COLUMN_NAME_TITLE));
            reminderLists.add(title);
        }
        cursor.close();

        return reminderLists;
    }

    public long getListIdByName(String listName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long listId = -1; // Default value indicating not found

        String[] projection = {
                ReminderContract.ReminderListEntry._ID, // Include the ID column in the projection
                ReminderContract.ReminderListEntry.COLUMN_NAME_TITLE
        };

        // Selection criteria to find the row with the given list name
        String selection = ReminderContract.ReminderListEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { listName };

        Cursor cursor = db.query(
                ReminderContract.ReminderListEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // Don't group the rows
                null,                   // Don't filter by row groups
                null                   // The sort order
        );

        if (cursor.moveToFirst()) { // If the query returned a result
            listId = cursor.getLong(cursor.getColumnIndexOrThrow(ReminderContract.ReminderListEntry._ID));
        }
        cursor.close();

        return listId;
    }


    public long addNewReminder(String title, String type, String date, String time, long listId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ReminderContract.ReminderEntry.COLUMN_NAME_TITLE, title);
        values.put(ReminderContract.ReminderEntry.COLUMN_NAME_TYPE, type);
        values.put(ReminderContract.ReminderEntry.COLUMN_NAME_DATE, date);
        values.put(ReminderContract.ReminderEntry.COLUMN_NAME_TIME, time);
        values.put(ReminderContract.ReminderEntry.COLUMN_NAME_LIST_ID, listId);

        return db.insert(ReminderContract.ReminderEntry.TABLE_NAME, null, values);
    }

    public List<Reminder> getRemindersForList(long listId) {
        List<Reminder> reminders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ReminderContract.ReminderEntry._ID,
                ReminderContract.ReminderEntry.COLUMN_NAME_TITLE,
                ReminderContract.ReminderEntry.COLUMN_NAME_TYPE,
                ReminderContract.ReminderEntry.COLUMN_NAME_DATE,
                ReminderContract.ReminderEntry.COLUMN_NAME_TIME,
                ReminderContract.ReminderEntry.COLUMN_NAME_CHECKED
        };

        String selection = ReminderContract.ReminderEntry.COLUMN_NAME_LIST_ID + " = ?";
        String[] selectionArgs = { String.valueOf(listId) };


        Cursor cursor = db.query(
                ReminderContract.ReminderEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(ReminderContract.ReminderEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderEntry.COLUMN_NAME_TITLE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderEntry.COLUMN_NAME_TYPE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderEntry.COLUMN_NAME_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderEntry.COLUMN_NAME_TIME));
            boolean isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(ReminderContract.ReminderEntry.COLUMN_NAME_CHECKED)) == 1;

            reminders.add(new Reminder(id, title, type, date, time, isChecked));
        }
        cursor.close();

        return reminders;
    }

    public void updateReminderListName(String oldName, String newName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ReminderContract.ReminderListEntry.COLUMN_NAME_TITLE, newName);

        String selection = ReminderContract.ReminderListEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { oldName };

        db.update(ReminderContract.ReminderListEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void deleteReminderListByName(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Retrieve the list ID corresponding to the name
        long listId = getListIdByName(name);

        // Delete all reminders associated with this list ID
        String reminderSelection = ReminderContract.ReminderEntry.COLUMN_NAME_LIST_ID + " = ?";
        String[] selectionArgs2 = { String.valueOf(listId) }; // Convert listId to String
        db.delete(ReminderContract.ReminderEntry.TABLE_NAME, reminderSelection, selectionArgs2);

        // Delete the reminder list itself
        String selection = ReminderContract.ReminderListEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { name };
        db.delete(ReminderContract.ReminderListEntry.TABLE_NAME, selection, selectionArgs);
    }
}
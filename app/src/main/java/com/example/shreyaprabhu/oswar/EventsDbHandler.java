package com.example.shreyaprabhu.oswar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Shreya Prabhu on 8/28/2016.
 */
public class EventsDbHandler extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;
    public static final String EVENT_TABLE_NAME = "contactdetails";
    public static final String _id = "_id";
    public static final String EVENT_COLUMN_NAME = "contact_name";
    public static final String EVENT_COLUMN_PHONE = "contact_phone";
    private static final String TAG = "EventsDbHelper.java";

    public EventsDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + EVENT_TABLE_NAME + "(" +
                        _id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        EVENT_COLUMN_NAME + " TEXT, " +
                        EVENT_COLUMN_PHONE + " TEXT " +
                        ");"

        );
        Log.v(TAG, "CREATE" + "CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertEvent(String eventname, String eventphone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT_COLUMN_NAME, eventname);
        contentValues.put(EVENT_COLUMN_PHONE, eventphone);
        db.insert(EVENT_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateEvent(Integer id, String eventname, String eventphone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT_COLUMN_NAME, eventname);
        contentValues.put(EVENT_COLUMN_PHONE, eventphone);
        db.update(EVENT_TABLE_NAME, contentValues, _id + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + EVENT_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public Cursor getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME + " WHERE " +
                _id + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME, null);
        return res;
    }

    public Integer deleteEvent(Long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(EVENT_TABLE_NAME,
                _id + " = ? ",
                new String[]{Long.toString(id)});
    }


}

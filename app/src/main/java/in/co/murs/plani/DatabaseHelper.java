package in.co.murs.plani;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.co.murs.plani.models.Address;
import in.co.murs.plani.models.Event;
import in.co.murs.plani.models.Note;

/**
 * Created by Ujjwal on 2/19/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String TAG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "planIManager";

    // Table Names
    private static final String TABLE_NOTES= "notes";
    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_ADDRESS = "address";

    // column names notes
    private static final String KEY_TIME = "time";
    private static final String KEY_EVENT = "event";

    //column names events
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_LOCATION = "location";

    //column name address
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_ADDRESS = "address";


    //column names common
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_ID = "_id";


    // Table Create Statements
    private static final String CREATE_TABLE_NOTES = "CREATE TABLE "
            + TABLE_NOTES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE
            + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_TIME + " INTEGER," + KEY_EVENT
            + " INTEGER )";

    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE "
            + TABLE_EVENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE
            + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_START_TIME + " INTEGER," + KEY_END_TIME
            + " INTEGER," + KEY_LOCATION + " INTEGER )";

    private static final String CREATE_TABLE_ADDRESS = "CREATE TABLE "
            + TABLE_ADDRESS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LATITUDE
            + " DOUBLE," + KEY_LONGITUDE + " DOUBLE," + KEY_ADDRESS + " TEXT )";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_NOTES);
        db.execSQL(CREATE_TABLE_ADDRESS);
        db.execSQL(CREATE_TABLE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

        // create new tables
        onCreate(db);
    }

    /*****************************Insert Table*************************************/

    public long addNote(Note note) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_DESCRIPTION, note.getDescription());
        values.put(KEY_TIME, note.getTime());
        values.put(KEY_EVENT, note.getEvent());

        // insert row
        long _id = db.insert(TABLE_NOTES, null, values);
        return _id;
    }

    public long addEvent(Event event, long location) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, event.getTitle());
        values.put(KEY_DESCRIPTION, event.getDescription());
        values.put(KEY_START_TIME, event.getStartTime());
        values.put(KEY_END_TIME, event.getEndTime());
        values.put(KEY_LOCATION, location);

        // insert row
        long _id = db.insert(TABLE_EVENTS, null, values);
        return _id;
    }

    public long addAddress(Address address) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, address.getLatitude());
        values.put(KEY_LONGITUDE, address.getLongitude());
        values.put(KEY_ADDRESS, address.getAddress());

        // insert row
        long _id = db.insert(TABLE_ADDRESS, null, values);
        return _id;
    }

   /*************************************Update Table**********************************/

   public long updateEventAddress(Address address, long _id) throws Exception{
       SQLiteDatabase db = this.getWritableDatabase();

       long addressId = addAddress(address);
       ContentValues values = new ContentValues();
       values.put(KEY_ADDRESS, addressId);

       // insert row
       return db.update(TABLE_EVENTS, values, KEY_ID + " = " + _id, null);
   }

    public long updateEventStartTime(long time, long _id) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_START_TIME, time);

        // insert row
        return db.update(TABLE_EVENTS, values, KEY_ID + " = " + _id, null);
    }

    public long updateEventEndTime(long endTime, long _id) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_END_TIME, endTime);

        // insert row
        return db.update(TABLE_EVENTS, values, KEY_ID + " = " + _id, null);
    }

    public long updateEventDescription(String description, long _id) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, description);

        // insert row
        return db.update(TABLE_EVENTS, values, KEY_ID + " = " + _id, null);
    }



    /************************************Get Table*************************************/

    public Note getNote(long _id) throws Exception{
        Note note = new Note();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES + " WHERE " + KEY_ID + "=" + _id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            note.set_id(cursor.getLong(0));
            note.setTitle(cursor.getString(1));
            note.setDescription(cursor.getString(2));
            note.setTime(cursor.getLong(3));
            note.setEvent(cursor.getLong(4));
        }
        // return contact list
        return note;
    }

    public Address getAddress(long _id) throws Exception{
        Address address = new Address();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ADDRESS + " WHERE " + KEY_ID + "=" + _id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            address.set_id(cursor.getLong(0));
            address.setLatitude(cursor.getDouble(1));
            address.setLongitude(cursor.getDouble(2));
            address.setAddress(cursor.getString(3));
        }
        // return address
        return address;
    }

    public String getEventTitle(long _id) throws Exception{
        String title = "";
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE " + KEY_ID + "=" + _id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            title = cursor.getString(1);
        }
        return title;
    }

    public List<Note> getAllNotes(Long event) throws Exception{
        List<Note> notes = new ArrayList<Note>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES + " WHERE " + KEY_EVENT + "=" + event;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.set_id(cursor.getLong(0));
                note.setTitle(cursor.getString(1));
                note.setDescription(cursor.getString(2));
                note.setTime(cursor.getLong(3));
                note.setEvent(cursor.getLong(4));

                // Adding note to list
                notes.add(note);
            } while (cursor.moveToNext());
        }

        if (notes.size() > 1) {
            Collections.sort(notes, new Comparator<Note>() {
                @Override
                public int compare(final Note object1, final Note object2) {
                    return Long.compare(object1.getTime(), object2.getTime());
                }
            });
        }
        // return contact list
        return notes;
    }

    public List<Note> getAllNotes() throws Exception{
        List<Note> notes = new ArrayList<Note>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.set_id(cursor.getLong(0));
                note.setTitle(cursor.getString(1));
                note.setDescription(cursor.getString(2));
                note.setTime(cursor.getLong(3));
                note.setEvent(cursor.getLong(4));

                // Adding note to list
                notes.add(note);
            } while (cursor.moveToNext());
        }

        if (notes.size() > 1) {
            Collections.sort(notes, new Comparator<Note>() {
                @Override
                public int compare(final Note object1, final Note object2) {
                    return Long.compare(object1.getTime(), object2.getTime());
                }
            });
        }
        // return contact list
        return notes;
    }

    public Event getEvent(long _id) throws Exception{
        Event event = new Event();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE " + KEY_ID + "=" + _id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
                event.set_id(cursor.getLong(0));
                event.setTitle(cursor.getString(1));
                event.setDescription(cursor.getString(2));
                event.setStartTime(cursor.getLong(3));
                event.setEndTime(cursor.getLong(4));
                Address address = null;
                try{
                    address = getAddress(cursor.getLong(5));
                    event.setLocation(address);
                }catch (Exception e) {
                    event.setLocation(null);
                }
        }
        // return contact list
        return event;
    }

    public List<Event> getAllEvents() throws Exception{
        List<Event> events = new ArrayList<Event>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.set_id(cursor.getLong(0));
                event.setTitle(cursor.getString(1));
                event.setDescription(cursor.getString(2));
                event.setStartTime(cursor.getLong(3));
                event.setEndTime(cursor.getLong(4));
                Address address = null;
                try{
                    address = getAddress(cursor.getLong(5));
                    event.setLocation(address);
                }catch (Exception e){
                    event.setLocation(null);
                }
                // Adding contact to list
                events.add(event);
            } while (cursor.moveToNext());
        }
        // return contact list
        return events;
    }

    public List<Event> getEventsForInterval(long start, long end) throws Exception{
        List<Event> events = new ArrayList<Event>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE ( " + KEY_START_TIME + ">" + start + " AND "
                + KEY_START_TIME + "<" + end + " ) OR ( " + KEY_END_TIME + ">" + start + " AND "
                + KEY_END_TIME + "<" + end + " )";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.set_id(cursor.getLong(0));
                event.setTitle(cursor.getString(1));
                event.setDescription(cursor.getString(2));
                event.setStartTime(cursor.getLong(3));
                event.setEndTime(cursor.getLong(4));
                Address address = null;
                try{
                    address = getAddress(cursor.getLong(5));
                    event.setLocation(address);
                }catch (Exception e){
                    event.setLocation(null);
                }
                // Adding contact to list
                events.add(event);
            } while (cursor.moveToNext());
        }
        // return contact list
        return events;
    }



    /**************************************************Deleting Table***************************************/

    public void deleteAddress(long _id) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADDRESS, KEY_ID + " = ?", new String[]{String.valueOf(_id)});
    }

    public void deleteNote(long _id) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?", new String[]{String.valueOf(_id)});
    }

    public void deleteEvent(long _id, long location) throws Exception{

        List<Note> notes = getAllNotes(_id);
        while(notes!=null && notes.size() > 0){
            deleteNote(notes.get(0).get_id());
            notes.remove(0);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_ID + " = ?", new String[]{String.valueOf(_id)});

        if(location > 0l)
            deleteAddress(location);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}


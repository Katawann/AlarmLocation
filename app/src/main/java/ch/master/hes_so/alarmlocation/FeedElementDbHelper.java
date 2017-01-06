package ch.master.hes_so.alarmlocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ch.master.hes_so.alarmlocation.List.Element;
import ch.master.hes_so.alarmlocation.List.Position;
import ch.master.hes_so.alarmlocation.List.Rule;


/**
 * Created by quent on 05/01/2017.
 */
public class FeedElementDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedPosition.db";

    public static final String TABLE_NAME = "position";
    public static final String KEY_ID = "id";
    public static final String KEY_TYPE = "type";
    public static final String KEY_NAME = "name";
    public static final String KEY_ENABLED = "is_enabled";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_RADIUS = "radius";

    //TODO add end and start time for Rule (peut être plus encore à voir...)

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_TYPE + " INTEGER,"
                    + KEY_NAME + " TEXT,"
                    + KEY_ENABLED + " INTEGER,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_ADDRESS + " TEXT,"
                    + KEY_RADIUS + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public FeedElementDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addNewElement(Element element) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, element.getElementName());
        newValues.put(KEY_TYPE, element.getType());
        newValues.put(KEY_ENABLED, element.isEnabled());
        newValues.put(KEY_DESCRIPTION, element.getDescription());
        newValues.put(KEY_ADDRESS, element.getAddress());
        newValues.put(KEY_RADIUS,element.getRadius());

        db.insert(TABLE_NAME, null, newValues);
        db.close();
    }

    public void modifyElement(Element element){
        ContentValues updatedValues = new ContentValues();

        updatedValues.put(KEY_NAME, element.getElementName());
        updatedValues.put(KEY_TYPE, element.getType());
        updatedValues.put(KEY_ENABLED, element.isEnabled());
        updatedValues.put(KEY_DESCRIPTION, element.getDescription());
        updatedValues.put(KEY_ADDRESS, element.getAddress());
        updatedValues.put(KEY_RADIUS,element.getRadius());

        String where = KEY_ID + "=" + element.getId();
        String whereArgs[] = null;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.update(TABLE_NAME,updatedValues,where,whereArgs);

    }

    public void deleteElement(int id) {
        String where = KEY_ID + "=" + id;
        String whereArgs[] = null;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, where, whereArgs);
    }

    public Element getElementWithId(int _id){

        String[] result_column = new String[] { KEY_ID, KEY_NAME,
                KEY_TYPE, KEY_ENABLED, KEY_DESCRIPTION, KEY_ADDRESS, KEY_RADIUS};
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,result_column,null,null,null,null,null);

        while (cursor.moveToNext()){
            int id = Integer.parseInt(cursor.getString(0));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TYPE));
            boolean isEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ENABLED)) > 0;

            if (_id == id){
                if (type == Globals.TYPE_POSITION) {
                    return new Position(id,
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                            isEnabled,
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RADIUS)));
                }else if(type == Globals.TYPE_RULE){
                    return new Rule(id,
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                            isEnabled,
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RADIUS)));
                }
            }
        }

        cursor.close();

        return null;
    }


    public ArrayList<Element> getElementFromDB(){
        ArrayList<Element> taskList = new ArrayList<>();

        String[] result_column = new String[] { KEY_ID, KEY_NAME,
                KEY_TYPE, KEY_ENABLED, KEY_DESCRIPTION, KEY_ADDRESS,KEY_RADIUS};
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,result_column,null,null,null,null,null);

        while (cursor.moveToNext()){
            int id = Integer.parseInt(cursor.getString(0));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TYPE));
            boolean isEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ENABLED)) > 0;

            if (type == Globals.TYPE_POSITION){
                taskList.add(new Position(id,
                                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                                        isEnabled,
                                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)),
                                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RADIUS))));
            }else if (type == Globals.TYPE_RULE){
                taskList.add(new Rule(id,
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        isEnabled,
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RADIUS))));
            }
        }

        cursor.close();

        return taskList;

    }

}

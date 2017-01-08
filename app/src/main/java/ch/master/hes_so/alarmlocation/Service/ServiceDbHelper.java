package ch.master.hes_so.alarmlocation.Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ch.master.hes_so.alarmlocation.Globals;
import ch.master.hes_so.alarmlocation.List.Element;


/**
 * Created by quent on 05/01/2017.
 */
public class ServiceDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ServicePosition.db";

    public static final String TABLE_NAME = "service";
    public static final String KEY_ID = "id";
    public static final String KEY_TYPE = "type";
    public static final String KEY_NAME = "name";
    public static final String KEY_ENABLED = "is_enabled";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_RADIUS = "radius";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_RESTAURANT = "restaurant";
    public static final String KEY_SUPERMARKET = "supermarket";
    public static final String KEY_FOODSTORE = "foodStore";
    public static final String KEY_HAIRDRESSER = "hairdresser";
    public static final String KEY_BAR = "bar";
    public static final String KEY_CAFE = "cafe";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_TYPE + " INTEGER,"
                    + KEY_NAME + " TEXT,"
                    + KEY_ENABLED + " INTEGER,"
                    + KEY_RESTAURANT + " INTEGER,"
                    + KEY_SUPERMARKET + " INTEGER,"
                    + KEY_FOODSTORE + " INTEGER,"
                    + KEY_HAIRDRESSER + " INTEGER,"
                    + KEY_BAR + " INTEGER,"
                    + KEY_CAFE + " INTEGER,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_ADDRESS + " TEXT,"
                    + KEY_LATITUDE + " REAL,"
                    + KEY_LONGITUDE + " REAL,"
                    + KEY_RADIUS + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    private final static String LOGTAG = "ServiceDbHelper";

    public ServiceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d(LOGTAG,"Table created: " + SQL_CREATE_ENTRIES);
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
        newValues.put(KEY_LATITUDE, element.getLatLng().latitude);
        newValues.put(KEY_LONGITUDE, element.getLatLng().longitude);
        newValues.put(KEY_RADIUS,element.getRadius());
        newValues.put(KEY_RESTAURANT,element.getRestaurant());
        newValues.put(KEY_SUPERMARKET,element.getSupermarket());
        newValues.put(KEY_FOODSTORE, element.getFoodStore());
        newValues.put(KEY_HAIRDRESSER,element.getHairdresser());
        newValues.put(KEY_BAR,element.getBar());
        newValues.put(KEY_CAFE, element.getCafe());

        db.insert(TABLE_NAME, null, newValues);
        db.close();

        Log.d(LOGTAG,"Latitude and longitude added: " + element.getLatLng().toString());
    }

    public void modifyElement(Element element){
        ContentValues updatedValues = new ContentValues();

        updatedValues.put(KEY_NAME, element.getElementName());
        updatedValues.put(KEY_TYPE, element.getType());
        updatedValues.put(KEY_ENABLED, element.isEnabled());
        updatedValues.put(KEY_DESCRIPTION, element.getDescription());
        updatedValues.put(KEY_ADDRESS, element.getAddress());
        updatedValues.put(KEY_LATITUDE, element.getLatLng().latitude);
        updatedValues.put(KEY_LONGITUDE, element.getLatLng().longitude);
        updatedValues.put(KEY_RADIUS,element.getRadius());
        updatedValues.put(KEY_RESTAURANT,element.getRestaurant());
        updatedValues.put(KEY_SUPERMARKET,element.getSupermarket());
        updatedValues.put(KEY_FOODSTORE, element.getFoodStore());
        updatedValues.put(KEY_HAIRDRESSER,element.getHairdresser());
        updatedValues.put(KEY_BAR,element.getBar());
        updatedValues.put(KEY_CAFE, element.getCafe());

        String where = KEY_ID + "=" + element.getId();
        String whereArgs[] = null;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.update(TABLE_NAME,updatedValues,where,whereArgs);

        Log.d(LOGTAG,"Latitude and longitude modified: " + element.getLatLng().toString());

    }

    public void deleteElement(int id) {
        String where = KEY_ID + "=" + id;
        String whereArgs[] = null;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, where, whereArgs);
    }

    public Element getElementWithId(int _id){

        String[] result_column = new String[] { KEY_ID, KEY_NAME,
                KEY_TYPE, KEY_ENABLED, KEY_DESCRIPTION, KEY_ADDRESS, KEY_RADIUS, KEY_LATITUDE,
                KEY_LONGITUDE, KEY_RESTAURANT, KEY_SUPERMARKET, KEY_FOODSTORE, KEY_HAIRDRESSER,
                KEY_BAR, KEY_CAFE};
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,result_column,null,null,null,null,null);

        while (cursor.moveToNext()){
            int id = Integer.parseInt(cursor.getString(0));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TYPE));
            boolean isEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ENABLED)) > 0;
            boolean restaurant = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESTAURANT)) > 0;
            boolean supermarket = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SUPERMARKET)) > 0;
            boolean foodStore = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FOODSTORE)) > 0;
            boolean hairdresser = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HAIRDRESSER)) > 0;
            boolean bar = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BAR)) > 0;
            boolean cafe = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CAFE)) > 0;
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE));

            Log.d(LOGTAG, "Element get: " + latitude + " " + longitude);

            if (_id == id){
                if (type == Globals.TYPE_POSITION) {
                    return new Element(id,
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                            isEnabled,
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RADIUS)),
                            new LatLng(latitude,longitude),Globals.TYPE_POSITION);
                }else if(type == Globals.TYPE_RULE){
                    return new Element(id,
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                            isEnabled,
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RADIUS)),
                            restaurant,
                            supermarket,
                            foodStore,
                            hairdresser,
                            bar,cafe,Globals.TYPE_RULE);
                }
            }
        }

        cursor.close();

        return null;
    }


    public ArrayList<Element> getElementFromDB(){
        ArrayList<Element> taskList = new ArrayList<>();

        String[] result_column = new String[] { KEY_ID, KEY_NAME,
                KEY_TYPE, KEY_ENABLED, KEY_DESCRIPTION, KEY_ADDRESS, KEY_RADIUS, KEY_LATITUDE,
                KEY_LONGITUDE, KEY_RESTAURANT, KEY_SUPERMARKET, KEY_FOODSTORE, KEY_HAIRDRESSER,
                KEY_BAR, KEY_CAFE};
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,result_column,null,null,null,null,null);

        while (cursor.moveToNext()){
            int id = Integer.parseInt(cursor.getString(0));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TYPE));
            boolean isEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ENABLED)) > 0;
            boolean restaurant = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESTAURANT)) > 0;
            boolean supermarket = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SUPERMARKET)) > 0;
            boolean foodStore = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FOODSTORE)) > 0;
            boolean hairdresser = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HAIRDRESSER)) > 0;
            boolean bar = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BAR)) > 0;
            boolean cafe = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CAFE)) > 0;
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE));

            if (type == Globals.TYPE_POSITION){
                taskList.add(new Element(id,
                                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                                        isEnabled,
                                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)),
                                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RADIUS)),new LatLng(latitude,longitude),Globals.TYPE_POSITION));
            }else if (type == Globals.TYPE_RULE){
                taskList.add(new Element(id,
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        isEnabled,
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RADIUS)),
                        restaurant,
                        supermarket,
                        foodStore,
                        hairdresser,
                        bar,cafe,Globals.TYPE_RULE));
            }
        }

        cursor.close();

        return taskList;

    }

}

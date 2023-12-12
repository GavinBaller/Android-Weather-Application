package com.example.weatherapplicationfinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Resource Used - Android Training - https://developer.android.com/training/data-storage/sqlite

/** Application Database Class which facilitates database creation, updating and reading. */
public class ApplicationDatabase extends SQLiteOpenHelper {

    // used for the constructor for the database name.
    public static final String databaseName = "userInformation.db";

    /** Application Database constructor as required for the class*/
    public ApplicationDatabase(Context context) {
        super(context, databaseName , null, 1);
    }

    /** Application Database on create method */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // sql statement for creating the table and executed below.
        String createTableStatement = "CREATE TABLE USER_INFORMATION (ID INTEGER, DEFAULT_LOCATION STRING, USER_USES INT)";
        sqLiteDatabase.execSQL(createTableStatement);

        // creates a content values object for accessing and writing to the database.
        ContentValues contentValues = new ContentValues();

        // using the content values object add to each column the certain values.
        contentValues.put("ID", 1);
        contentValues.put("DEFAULT_LOCATION", "");
        contentValues.put("USER_USES", 0);

        // inserts the information into the database. essentially inserting into the table user_info, if content values is null then it inserts null else inserts content values.
        sqLiteDatabase.insert("USER_INFORMATION", null, contentValues);
    }

    /** Application Database on upgrade method */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // sql statement for deleting the table and executed below.
        String deleteTableOnUpgrade = "DROP TABLE IF EXISTS USER_INFORMATION";

        sqLiteDatabase.execSQL(deleteTableOnUpgrade);

        // then call the on create to create a database again.
        onCreate(sqLiteDatabase);
    }

    /** Method for incrementing the database with the number of uses. */
    public void incrementUserUses(){

        // initiating a variable for uses.
        int userUses = 0;

        // getting access to the database and allowing writing to it.
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // sql statement for getting access to the whole table.
        String queryString = "SELECT * FROM USER_INFORMATION";

        // cursor object than provides read write access to the database. used raw query instead of query as it was simpler
        Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        // asking the cursor to move to th first item.
        cursor.moveToFirst();

        // then getting the int for uses at index 2 of the row.
        userUses = cursor.getInt(2);

        // creates a content values object for accessing and writing to the database.
        ContentValues contentValues = new ContentValues();

        // using the content values object to add the number of uses plus one back to the database.
        contentValues.put("USER_USES", userUses + 1);

        // updates the database. as only one row didn't need to specify where.
        sqLiteDatabase.update("USER_INFORMATION", contentValues, null, null);

        // closing the cursor and the database once done updating.
        cursor.close();
        sqLiteDatabase.close();
    }

    /** Setter method for setting the default location of the application. */
    public void setDefaultLocation(String defaultLocation){

        // getting access to the database and allowing writing to it.
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // creates a content values object for accessing and writing to the database.
        ContentValues contentValues = new ContentValues();

        // using the passed default location from the settings activity and it to the object.
        contentValues.put("DEFAULT_LOCATION", defaultLocation);

        // updates the database. as only one row didn't need to specify where.
        sqLiteDatabase.update("USER_INFORMATION", contentValues, null, null);

        // closing access to the database once done updating.
        sqLiteDatabase.close();
    }

    /** Getter method for the location in settings fragment */
    public String getUserLocation(){

        // sql statement for getting access to the whole table.
        String queryString = "SELECT * FROM USER_INFORMATION";

        // getting access to the database and allowing writing to it.
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // cursor object than provides read write access to the database. used raw query instead of query as it was simpler
        Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        // asking the cursor to move to th first item.
        cursor.moveToFirst();

        // then getting the string for location at index 1 of the row.
        String getUserLocation = cursor.getString(1);

        // closing the cursor and the database once done updating.
        cursor.close();
        sqLiteDatabase.close();

        // return the value to be displayed in the settings.
        return getUserLocation;
    }

    /** Getter method for the total number of logins in settings fragment */
    public int getUserTotalLogins(){

        // sql statement for getting access to the whole table.
        String queryString = "SELECT * FROM USER_INFORMATION";

        // getting access to the database and allowing writing to it.
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // cursor object than provides read write access to the database. used raw query instead of query as it was simpler
        Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        // asking the cursor to move to th first item.
        cursor.moveToFirst();

        // then getting the int for location at index 2 of the row.
        int getUserTotalLogins =  cursor.getInt(2);

        // closing the cursor and the database once done updating.
        cursor.close();
        sqLiteDatabase.close();

        // return the value to be displayed in the settings.
        return getUserTotalLogins;
    }
}

/**
 * DatabaseHandler.java
 * Wellness-App-MQP
 *
 * @version 1.0.0
 *
 * @author Jake Haas
 * @author Evan Safford
 * @author Nate Ford
 * @author Haley Andrews
 *
 * Copyright (c) 2014, 2015. Wellness-App-MQP. All Rights Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package com.starboardland.pedometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
//import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String TABLE_STEPS_TAKEN = "StepsTaken";
    public static final String COLUMN_STEPS = "Steps";
    private static final String DATABASE_NAME = "StepsTaken";
    private static final int DATABASE_VERSION = 1;

    private static final String KEY_ID = "id";

    private static final String[] COLUMNS = { KEY_ID, COLUMN_STEPS };

    private static final String DATABASE_CREATE = "CREATE TABLE StepsTaken ( "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "steps TEXT )";

    /**
     * DatabaseHandler Constructor
     * DatabaseHandler(Context context)
     *
     * @param context	The context
     */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * onCreate(SQLiteDatabase db)
     *
     * Create the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE);

    }

    /**
     * onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
     *
     * Upgrade the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS_TAKEN);
        onCreate(db);
    }

    /**
     * addStepsTaken(StepsTaken stepsTaken)
     *
     * Update the database with the new number of steps taken
     */
    public void addStepsTaken(StepsTaken stepsTaken) {
        // for logging
        Log.d("addStepsTaken", stepsTaken.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("steps", stepsTaken.getSteps()); // get author

        // 3. insert
        db.insert(TABLE_STEPS_TAKEN, // table
                null, // nullColumnHack
                values); // key/value -> keys = column names/ values = column
        // values

        // 4. close
        db.close();
    }

    /**
     * StepsTaken getStepsTaken(int id)
     *
     * Get the ammount of steps taken out of the database
     */
    public StepsTaken getStepsTaken(int id) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.query(TABLE_STEPS_TAKEN, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();


        StepsTaken stepsTaken = new StepsTaken();
        stepsTaken.setId(Integer.parseInt(cursor.getString(0)));
        stepsTaken.setSteps(Integer.parseInt(cursor.getString(1)));

        // log
        Log.d("getStepsTaken(" + id + ")", stepsTaken.toString());

        // 5. return book
        return stepsTaken;
    }

    public int updateStepsTaken(StepsTaken stepsTaken) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("steps", stepsTaken.getSteps()); // get author

        // 3. updating row
        int i = db.update(TABLE_STEPS_TAKEN, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(stepsTaken.getId()) }); //selection args
        Log.d("updateStepsTaken", stepsTaken.toString());
        // 4. close
        db.close();

        return i;

    }

}
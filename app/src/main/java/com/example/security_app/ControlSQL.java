package com.example.security_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ControlSQL extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "android.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "table1";
    private static final String DATAPASS_TABLE = "table2";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RESOURCE = "RESOURCE";
    public static final String COLUMN_LOGIN = "LOGIN";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_NOTES = "NOTES";
    public static final String COLUMN_USER = "USER";
    public static final String COLUMN_HASH = "HASH";
    private static final String DATABASE_CREATE = "create table IF NOT EXISTS "
            + DATABASE_TABLE + "(" + COLUMN_ID
            + " integer primary key, " + COLUMN_RESOURCE
            + " text not null, " + COLUMN_LOGIN + " text not null, "
            + COLUMN_PASSWORD + " text not null, " + COLUMN_NOTES + " text not null " + ");";
    private static final String DATAPASS_CREATE = "create table IF NOT EXISTS "
            + DATAPASS_TABLE + "(" + COLUMN_ID
            + " integer primary key, " + COLUMN_USER
            + " text not null, " +  COLUMN_HASH + " text not null " + ");";

    public ControlSQL(@Nullable Context context) {
        super(context, DATABASE_TABLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATAPASS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ DATAPASS_TABLE);
        onCreate(db);
    }

    public long insertRow(String RES, String LOG,
                          String PASS, String NOT) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = createContentValues(RES, LOG, PASS, NOT);

        long row = db.insert(DATABASE_TABLE, null, initialValues);
        db.close();
        return row;
    }

    public boolean updateTable(long rowId, String RES, String LOG,
                               String PASS, String NOT) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = createContentValues(RES, LOG, PASS, NOT);

        return db.update(DATABASE_TABLE, updateValues, COLUMN_ID + "=" + rowId,
                null) > 0;
    }

    public void deleteRow(long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, COLUMN_ID + "=" + rowId, null);
        db.close();
    }

    public Cursor getFullTable(String table, String [] columns) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(table, columns, null,
                null, null, null, null);
    }

    public void delete_table(String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+ table);
    }

    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DATABASE_CREATE);
    }

    public void createTablePass(String user, String hash) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DATAPASS_CREATE);
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER, user);
        values.put(COLUMN_HASH, hash);
        db.insert(DATAPASS_TABLE, null, values);
    }

    public boolean updateTablePass (String user, String hash, long rowId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER, user);
        values.put(COLUMN_HASH, hash);
        return db.update(DATAPASS_TABLE, values, COLUMN_ID+ "="+ rowId,
                null) > 0;
    }

    private ContentValues createContentValues(String RES, String LOG,
                                              String PASS, String NOT) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESOURCE, RES);
        values.put(COLUMN_LOGIN, LOG);
        values.put(COLUMN_PASSWORD, PASS);
        values.put(COLUMN_NOTES, NOT);
        return values;
    }


}

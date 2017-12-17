package com.authenticationfailure.wheresmybrowser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Populate a local database with secret data.
 */

public class SecretDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "super_secret.db";
    private static final String SQL_CREATE_TABLES = "CREATE TABLE SECRET_TABLE " +
            "(id INTEGER PRIMARY KEY, secret TEXT)";
    private static final String SQL_ADD_DATA = "INSERT INTO SECRET_TABLE VALUES" +
            "(1, 'The secret is: CXX21E5TSC')";

    public SecretDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLES);
        sqLiteDatabase.execSQL(SQL_ADD_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

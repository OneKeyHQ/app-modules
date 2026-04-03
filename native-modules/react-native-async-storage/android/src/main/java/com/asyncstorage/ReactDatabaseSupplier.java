/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.asyncstorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import javax.annotation.Nullable;

/**
 * Database supplier of the database used by react native async storage.
 * Ported from upstream @react-native-async-storage/async-storage ReactDatabaseSupplier.java.
 */
public class ReactDatabaseSupplier extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RKStorage";

    private static final int DATABASE_VERSION = 1;
    private static final int SLEEP_TIME_MS = 30;

    static final String TABLE_CATALYST = "catalystLocalStorage";
    static final String KEY_COLUMN = "key";
    static final String VALUE_COLUMN = "value";

    static final String VERSION_TABLE_CREATE =
        "CREATE TABLE " + TABLE_CATALYST + " (" +
            KEY_COLUMN + " TEXT PRIMARY KEY, " +
            VALUE_COLUMN + " TEXT NOT NULL" +
            ")";

    private static @Nullable ReactDatabaseSupplier sReactDatabaseSupplierInstance;

    private Context mContext;
    private @Nullable SQLiteDatabase mDb;
    private long mMaximumDatabaseSize = 6L * 1024L * 1024L;

    private ReactDatabaseSupplier(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static ReactDatabaseSupplier getInstance(Context context) {
        if (sReactDatabaseSupplierInstance == null) {
            sReactDatabaseSupplierInstance = new ReactDatabaseSupplier(context.getApplicationContext());
        }
        return sReactDatabaseSupplierInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VERSION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            deleteDatabase();
            onCreate(db);
        }
    }

    synchronized boolean ensureDatabase() {
        if (mDb != null && mDb.isOpen()) {
            return true;
        }
        SQLiteException lastSQLiteException = null;
        for (int tries = 0; tries < 2; tries++) {
            try {
                if (tries > 0) {
                    deleteDatabase();
                }
                mDb = getWritableDatabase();
                break;
            } catch (SQLiteException e) {
                lastSQLiteException = e;
            }
            try {
                Thread.sleep(SLEEP_TIME_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (mDb == null) {
            throw lastSQLiteException;
        }
        mDb.setMaximumSize(mMaximumDatabaseSize);
        return true;
    }

    public synchronized SQLiteDatabase get() {
        ensureDatabase();
        return mDb;
    }

    public synchronized void clearAndCloseDatabase() throws RuntimeException {
        try {
            clear();
            closeDatabase();
        } catch (Exception e) {
            if (deleteDatabase()) {
                return;
            }
            throw new RuntimeException("Clearing and deleting database " + DATABASE_NAME + " failed");
        }
    }

    synchronized void clear() {
        get().delete(TABLE_CATALYST, null, null);
    }

    public synchronized void setMaximumSize(long size) {
        mMaximumDatabaseSize = size;
        if (mDb != null) {
            mDb.setMaximumSize(mMaximumDatabaseSize);
        }
    }

    private synchronized boolean deleteDatabase() {
        closeDatabase();
        return mContext.deleteDatabase(DATABASE_NAME);
    }

    public synchronized void closeDatabase() {
        if (mDb != null && mDb.isOpen()) {
            mDb.close();
            mDb = null;
        }
    }

    public static void deleteInstance() {
        sReactDatabaseSupplierInstance = null;
    }
}

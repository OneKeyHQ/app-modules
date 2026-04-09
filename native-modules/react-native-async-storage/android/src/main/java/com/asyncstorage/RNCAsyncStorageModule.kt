package com.asyncstorage

import android.database.Cursor
import android.database.sqlite.SQLiteStatement
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableArray
import com.facebook.react.module.annotations.ReactModule
import org.json.JSONObject
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Ported from upstream @react-native-async-storage/async-storage AsyncStorageModule.java
 * Adapted to use Promise (TurboModule) instead of Callback.
 * Uses SQLite via ReactDatabaseSupplier (same as upstream).
 */
@ReactModule(name = RNCAsyncStorageModule.NAME)
class RNCAsyncStorageModule(reactContext: ReactApplicationContext) :
    NativeAsyncStorageSpec(reactContext) {

    companion object {
        const val NAME = "RNCAsyncStorage"
        // SQL variable number limit, defined by SQLITE_LIMIT_VARIABLE_NUMBER
        private const val MAX_SQL_KEYS = 999
    }

    private val dbSupplier: ReactDatabaseSupplier = ReactDatabaseSupplier.getInstance(reactContext)
    private val executor: Executor = SerialExecutor(Executors.newSingleThreadExecutor())
    @Volatile
    private var shuttingDown = false

    override fun getName(): String = NAME

    override fun initialize() {
        super.initialize()
        shuttingDown = false
    }

    override fun invalidate() {
        shuttingDown = true
        dbSupplier.closeDatabase()
    }

    override fun multiGet(keys: ReadableArray, promise: Promise) {
        executor.execute {
            try {
                if (!ensureDatabase()) {
                    promise.reject("ASYNC_STORAGE_ERROR", "Database Error")
                    return@execute
                }

                val columns = arrayOf(ReactDatabaseSupplier.KEY_COLUMN, ReactDatabaseSupplier.VALUE_COLUMN)
                val keysRemaining = HashSet<String>()
                val data: WritableArray = Arguments.createArray()

                var keyStart = 0
                while (keyStart < keys.size()) {
                    val keyCount = Math.min(keys.size() - keyStart, MAX_SQL_KEYS)
                    val cursor: Cursor = dbSupplier.get().query(
                        ReactDatabaseSupplier.TABLE_CATALYST,
                        columns,
                        buildKeySelection(keyCount),
                        buildKeySelectionArgs(keys, keyStart, keyCount),
                        null, null, null
                    )

                    keysRemaining.clear()
                    try {
                        if (cursor.count != keys.size()) {
                            for (keyIndex in keyStart until keyStart + keyCount) {
                                keysRemaining.add(keys.getString(keyIndex))
                            }
                        }
                        if (cursor.moveToFirst()) {
                            do {
                                val row = Arguments.createArray()
                                row.pushString(cursor.getString(0))
                                row.pushString(cursor.getString(1))
                                data.pushArray(row)
                                keysRemaining.remove(cursor.getString(0))
                            } while (cursor.moveToNext())
                        }
                    } finally {
                        cursor.close()
                    }

                    for (key in keysRemaining) {
                        val row = Arguments.createArray()
                        row.pushString(key)
                        row.pushNull()
                        data.pushArray(row)
                    }
                    keysRemaining.clear()
                    keyStart += MAX_SQL_KEYS
                }

                promise.resolve(data)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }
    }

    override fun multiSet(keyValuePairs: ReadableArray, promise: Promise) {
        if (keyValuePairs.size() == 0) {
            promise.resolve(null)
            return
        }

        executor.execute {
            try {
                if (!ensureDatabase()) {
                    promise.reject("ASYNC_STORAGE_ERROR", "Database Error")
                    return@execute
                }

                val sql = "INSERT OR REPLACE INTO ${ReactDatabaseSupplier.TABLE_CATALYST} VALUES (?, ?);"
                val statement: SQLiteStatement = dbSupplier.get().compileStatement(sql)
                try {
                    dbSupplier.get().beginTransaction()
                    for (idx in 0 until keyValuePairs.size()) {
                        val pair = keyValuePairs.getArray(idx)
                        if (pair == null || pair.size() != 2) {
                            promise.reject("ASYNC_STORAGE_ERROR", "Invalid Value")
                            return@execute
                        }
                        val key = pair.getString(0)
                        val value = pair.getString(1)
                        if (key == null) {
                            promise.reject("ASYNC_STORAGE_ERROR", "Invalid key")
                            return@execute
                        }
                        if (value == null) {
                            promise.reject("ASYNC_STORAGE_ERROR", "Invalid Value")
                            return@execute
                        }
                        statement.clearBindings()
                        statement.bindString(1, key)
                        statement.bindString(2, value)
                        statement.execute()
                    }
                    dbSupplier.get().setTransactionSuccessful()
                } finally {
                    try {
                        dbSupplier.get().endTransaction()
                    } catch (e: Exception) {
                        promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
                        return@execute
                    }
                }
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }
    }

    override fun multiRemove(keys: ReadableArray, promise: Promise) {
        if (keys.size() == 0) {
            promise.resolve(null)
            return
        }

        executor.execute {
            try {
                if (!ensureDatabase()) {
                    promise.reject("ASYNC_STORAGE_ERROR", "Database Error")
                    return@execute
                }

                try {
                    dbSupplier.get().beginTransaction()
                    var keyStart = 0
                    while (keyStart < keys.size()) {
                        val keyCount = Math.min(keys.size() - keyStart, MAX_SQL_KEYS)
                        dbSupplier.get().delete(
                            ReactDatabaseSupplier.TABLE_CATALYST,
                            buildKeySelection(keyCount),
                            buildKeySelectionArgs(keys, keyStart, keyCount)
                        )
                        keyStart += MAX_SQL_KEYS
                    }
                    dbSupplier.get().setTransactionSuccessful()
                } finally {
                    try {
                        dbSupplier.get().endTransaction()
                    } catch (e: Exception) {
                        promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
                        return@execute
                    }
                }
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }
    }

    override fun multiMerge(keyValuePairs: ReadableArray, promise: Promise) {
        executor.execute {
            try {
                if (!ensureDatabase()) {
                    promise.reject("ASYNC_STORAGE_ERROR", "Database Error")
                    return@execute
                }

                try {
                    dbSupplier.get().beginTransaction()
                    for (idx in 0 until keyValuePairs.size()) {
                        val pair = keyValuePairs.getArray(idx)
                        if (pair == null || pair.size() != 2) {
                            promise.reject("ASYNC_STORAGE_ERROR", "Invalid Value")
                            return@execute
                        }
                        val key = pair.getString(0)
                        val value = pair.getString(1)
                        if (key == null) {
                            promise.reject("ASYNC_STORAGE_ERROR", "Invalid key")
                            return@execute
                        }
                        if (value == null) {
                            promise.reject("ASYNC_STORAGE_ERROR", "Invalid Value")
                            return@execute
                        }
                        if (!mergeImpl(key, value)) {
                            promise.reject("ASYNC_STORAGE_ERROR", "Database Error")
                            return@execute
                        }
                    }
                    dbSupplier.get().setTransactionSuccessful()
                } finally {
                    try {
                        dbSupplier.get().endTransaction()
                    } catch (e: Exception) {
                        promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
                        return@execute
                    }
                }
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }
    }

    override fun getAllKeys(promise: Promise) {
        executor.execute {
            try {
                if (!ensureDatabase()) {
                    promise.reject("ASYNC_STORAGE_ERROR", "Database Error")
                    return@execute
                }

                val columns = arrayOf(ReactDatabaseSupplier.KEY_COLUMN)
                val cursor: Cursor = dbSupplier.get().query(
                    ReactDatabaseSupplier.TABLE_CATALYST,
                    columns, null, null, null, null, null
                )

                val data: WritableArray = Arguments.createArray()
                try {
                    if (cursor.moveToFirst()) {
                        do {
                            data.pushString(cursor.getString(0))
                        } while (cursor.moveToNext())
                    }
                } finally {
                    cursor.close()
                }
                promise.resolve(data)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }
    }

    override fun clear(promise: Promise) {
        executor.execute {
            try {
                if (!dbSupplier.ensureDatabase()) {
                    promise.reject("ASYNC_STORAGE_ERROR", "Database Error")
                    return@execute
                }
                dbSupplier.clear()
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }
    }

    // --- Private helpers (ported from AsyncLocalStorageUtil.java) ---

    private fun ensureDatabase(): Boolean {
        return !shuttingDown && dbSupplier.ensureDatabase()
    }

    private fun buildKeySelection(count: Int): String {
        val list = Array(count) { "?" }
        return "${ReactDatabaseSupplier.KEY_COLUMN} IN (${list.joinToString(", ")})"
    }

    private fun buildKeySelectionArgs(keys: ReadableArray, start: Int, count: Int): Array<String> {
        return Array(count) { keys.getString(start + it) }
    }

    private fun getItemImpl(key: String): String? {
        val columns = arrayOf(ReactDatabaseSupplier.VALUE_COLUMN)
        val selectionArgs = arrayOf(key)
        val cursor = dbSupplier.get().query(
            ReactDatabaseSupplier.TABLE_CATALYST,
            columns,
            "${ReactDatabaseSupplier.KEY_COLUMN}=?",
            selectionArgs,
            null, null, null
        )
        try {
            return if (!cursor.moveToFirst()) null else cursor.getString(0)
        } finally {
            cursor.close()
        }
    }

    private fun setItemImpl(key: String, value: String): Boolean {
        val contentValues = android.content.ContentValues()
        contentValues.put(ReactDatabaseSupplier.KEY_COLUMN, key)
        contentValues.put(ReactDatabaseSupplier.VALUE_COLUMN, value)
        val inserted = dbSupplier.get().insertWithOnConflict(
            ReactDatabaseSupplier.TABLE_CATALYST,
            null,
            contentValues,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
        return inserted != -1L
    }

    private fun mergeImpl(key: String, value: String): Boolean {
        val oldValue = getItemImpl(key)
        val newValue: String
        if (oldValue == null) {
            newValue = value
        } else {
            val oldJSON = JSONObject(oldValue)
            val newJSON = JSONObject(value)
            deepMergeInto(oldJSON, newJSON)
            newValue = oldJSON.toString()
        }
        return setItemImpl(key, newValue)
    }

    private fun deepMergeInto(oldJSON: JSONObject, newJSON: JSONObject) {
        val keys = newJSON.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val newJSONObject = newJSON.optJSONObject(key)
            val oldJSONObject = oldJSON.optJSONObject(key)
            if (newJSONObject != null && oldJSONObject != null) {
                deepMergeInto(oldJSONObject, newJSONObject)
                oldJSON.put(key, oldJSONObject)
            } else {
                oldJSON.put(key, newJSON.get(key))
            }
        }
    }
}

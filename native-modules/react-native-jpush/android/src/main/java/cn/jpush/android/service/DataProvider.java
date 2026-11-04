package cn.jpush.android.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.internal.JCoreInternalHelper;
import java.util.Set;

public class DataProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        JCoreStorage.setAppContext(getContext());
        return true;
    }

    @Override
    public Cursor query(
        Uri uri,
        String[] projection,
        String selection,
        String[] selectionArgs,
        String sortOrder
    ) {
        String key = selection;
        String defaultVal = "";
        if ((key == null || key.isEmpty()) && uri != null) {
            try {
                key = uri.getQueryParameter("key");
                String defParam = uri.getQueryParameter("default");
                if (defParam != null) {
                    defaultVal = defParam;
                }
            } catch (Throwable ignored) {
            }
        }
        if (selectionArgs != null && selectionArgs.length > 0 && selectionArgs[0] != null) {
            if (key == null || key.isEmpty()) {
                key = selectionArgs[0];
            } else {
                defaultVal = selectionArgs[0];
            }
        }
        if (key == null || key.isEmpty()) {
            return null;
        }
        String value = JCoreStorage.getSpValue(getContext(), key, defaultVal);
        MatrixCursor cursor = new MatrixCursor(new String[] { "key", "value" }, 1);
        cursor.addRow(new Object[] { key, value });
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        if (uri != null) {
            try {
                String key = uri.getQueryParameter("key");
                if (key != null && !key.isEmpty()) {
                    return JCoreStorage.getSpValue(getContext(), key, "");
                }
            } catch (Throwable ignored) {
            }
        }
        return "vnd.android.cursor.item/jpush_data";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (values != null) {
            try {
                if (values.containsKey("key") && values.containsKey("value")) {
                    JCoreStorage.putSpValue(
                        getContext(),
                        values.getAsString("key"),
                        values.getAsString("value")
                    );
                } else {
                    Set<String> keys = values.keySet();
                    if (keys != null) {
                        for (String k : keys) {
                            Object v = values.get(k);
                            JCoreStorage.putSpValue(getContext(), k, v != null ? String.valueOf(v) : "");
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        } else if (uri != null) {
            try {
                String k = uri.getQueryParameter("key");
                String v = uri.getQueryParameter("value");
                if (k != null && !k.isEmpty()) {
                    JCoreStorage.putSpValue(getContext(), k, v != null ? v : "");
                }
            } catch (Throwable ignored) {
            }
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String key = selection;
        if ((key == null || key.isEmpty()) && uri != null) {
            try {
                key = uri.getQueryParameter("key");
            } catch (Throwable ignored) {
            }
        }
        if (key != null && !key.isEmpty()) {
            JCoreStorage.putSpValue(getContext(), key, "");
            return 1;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        insert(uri, values);
        return 1;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        return JCoreInternalHelper.getInstance().directHandle(getContext(), method, arg, extras);
    }
}

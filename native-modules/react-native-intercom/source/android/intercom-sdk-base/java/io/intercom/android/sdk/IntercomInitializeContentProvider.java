package io.intercom.android.sdk;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IntercomInitializeContentProvider extends ContentProvider {
   @Override
   public boolean onCreate() {
      Context var1 = this.getContext();
      if (var1 instanceof Application) {
         Application var2 = (Application)var1;
         LateInitializationPreparer.getInstance().register(var2);
      }

      return false;
   }

   @Nullable
   @Override
   public Cursor query(@NonNull Uri var1, @Nullable String[] var2, @Nullable String var3, @Nullable String[] var4, @Nullable String var5) {
      return null;
   }

   @Nullable
   @Override
   public String getType(@NonNull Uri var1) {
      return null;
   }

   @Nullable
   @Override
   public Uri insert(@NonNull Uri var1, @Nullable ContentValues var2) {
      return null;
   }

   @Override
   public int delete(@NonNull Uri var1, @Nullable String var2, @Nullable String[] var3) {
      return 0;
   }

   @Override
   public int update(@NonNull Uri var1, @Nullable ContentValues var2, @Nullable String var3, @Nullable String[] var4) {
      return 0;
   }
}

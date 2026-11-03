package cn.jpush.android.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.internal.JCoreInternalHelper;

public class DataProvider extends ContentProvider {
   private static final String TAG = "DataProvider";

   @Override
   public boolean onCreate() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DataProvider", "onCreate");
      return true;
   }

   @Override
   public Cursor query(Uri var1, String[] var2, String var3, String[] var4, String var5) {
      return null;
   }

   @Override
   public String getType(Uri var1) {
      return null;
   }

   @Override
   public Uri insert(Uri var1, ContentValues var2) {
      return null;
   }

   @Override
   public int delete(Uri var1, String var2, String[] var3) {
      return 0;
   }

   @Override
   public int update(Uri var1, ContentValues var2, String var3, String[] var4) {
      return 0;
   }

   @Override
   public Bundle call(String var1, String var2, Bundle var3) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DataProvider", "method:" + var1 + ", arg=" + var2);
         return JCoreInternalHelper.getInstance().directHandle(this.getContext(), var1, var2, var3);
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DataProvider", "call e:" + var5);
         return null;
      }
   }
}

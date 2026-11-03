package cn.jpush.android.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.internal.JCoreInternalHelper;
import cn.jiguang.m.RecoverType5a6aed9;

public class DownloadProvider extends ContentProvider {
   private static final String TAG = "DownloadProvider";

   @Override
   public boolean onCreate() {
      return false;
   }

   @Override
   public Cursor query(Uri var1, String[] var2, String var3, String[] var4, String var5) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DownloadProvider", "DownloadProvider query:" + var1);

      try {
         String var6 = var1.getQueryParameter("from_package");
         Bundle var7 = new Bundle();
         var7.putString("from_package", var6);
         var7.putInt("type", 4);
         RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.getContext(), "waked", var7);
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DownloadProvider", "wake error:" + var8.getMessage());
      }

      return null;
   }

   @Override
   public String getType(Uri var1) {
      return "";
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
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DownloadProvider", "method:" + var1 + ", arg=" + var2);
         return JCoreInternalHelper.getInstance().directHandle(this.getContext(), var1, var2, var3);
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DownloadProvider", "call e:" + var5);
         return null;
      }
   }
}

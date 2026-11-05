package com.intercom.twig;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Twig {
   private static final int MAX_LOG_LENGTH = 4000;
   @NonNull
   private final String tag;
   private int logLevel;
   private final boolean internalLoggingEnabled;
   public static final int DISABLED = 8;

   public Twig(int var1, @Nullable String var2, boolean var3) {
      this.logLevel = var1;
      this.tag = var2 != null ? var2 : "Twig";
      this.internalLoggingEnabled = var3;
   }

   public void v(String var1, Object... var2) {
      this.prepareLog(2, null, var1, var2);
   }

   public void v(Throwable var1, String var2, Object... var3) {
      this.prepareLog(2, var1, var2, var3);
   }

   public void v(Throwable var1) {
      this.prepareLog(2, var1, null);
   }

   public void d(String var1, Object... var2) {
      this.prepareLog(3, null, var1, var2);
   }

   public void d(Throwable var1, String var2, Object... var3) {
      this.prepareLog(3, var1, var2, var3);
   }

   public void d(Throwable var1) {
      this.prepareLog(3, var1, null);
   }

   public void i(String var1, Object... var2) {
      this.prepareLog(4, null, var1, var2);
   }

   public void i(Throwable var1, String var2, Object... var3) {
      this.prepareLog(4, var1, var2, var3);
   }

   public void i(Throwable var1) {
      this.prepareLog(4, var1, null);
   }

   public void w(String var1, Object... var2) {
      this.prepareLog(5, null, var1, var2);
   }

   public void w(Throwable var1, String var2, Object... var3) {
      this.prepareLog(5, var1, var2, var3);
   }

   public void w(Throwable var1) {
      this.prepareLog(5, var1, null);
   }

   public void e(String var1, Object... var2) {
      this.prepareLog(6, null, var1, var2);
   }

   public void e(Throwable var1, String var2, Object... var3) {
      this.prepareLog(6, var1, var2, var3);
   }

   public void e(Throwable var1) {
      this.prepareLog(6, var1, null);
   }

   public void wtf(String var1, Object... var2) {
      this.prepareLog(7, null, var1, var2);
   }

   public void wtf(Throwable var1, String var2, Object... var3) {
      this.prepareLog(7, var1, var2, var3);
   }

   public void wtf(Throwable var1) {
      this.prepareLog(7, var1, null);
   }

   public void internal(String var1) {
      this.internal(this.tag, var1);
   }

   public void internal(String var1, String var2) {
      if (this.internalLoggingEnabled) {
         Log.d(var1, "INTERNAL: " + var2);
      }
   }

   public void setLogLevel(int var1) {
      this.logLevel = var1;
   }

   @VisibleForTesting
   int getLogLevel() {
      return this.logLevel;
   }

   public String getTag() {
      return this.tag;
   }

   private void prepareLog(int var1, @Nullable Throwable var2, String var3, Object... var4) {
      if (var1 >= this.logLevel) {
         String var5 = this.getTag();
         if (var3 != null && var3.length() == 0) {
            var3 = null;
         }

         if (var3 == null) {
            if (var2 == null) {
               return;
            }

            var3 = this.getStackTraceString(var2);
         } else {
            if (var4.length > 0) {
               var3 = String.format(var3, var4);
            }

            if (var2 != null) {
               var3 = var3 + "\n" + this.getStackTraceString(var2);
            }
         }

         this.log(var1, var5, var3);
      }
   }

   @VisibleForTesting
   void log(int var1, String var2, Object... var3) {
      this.prepareLog(var1, null, var2, var3);
   }

   private void log(int var1, String var2, String var3) {
      if (var3.length() < 4000) {
         this.printLog(var1, var2, var3);
      } else {
         int var4 = 0;

         for (int var5 = var3.length(); var4 < var5; var4++) {
            int var6 = var3.indexOf(10, var4);
            var6 = var6 != -1 ? var6 : var5;

            do {
               int var7 = Math.min(var6, var4 + 4000);
               String var8 = var3.substring(var4, var7);
               this.printLog(var1, var2, var8);
               var4 = var7;
            } while (var4 < var6);
         }
      }
   }

   private void printLog(int var1, String var2, String var3) {
      if (var1 == 7) {
         Log.wtf(var2, var3);
      } else {
         Log.println(var1, var2, var3);
      }
   }

   private String getStackTraceString(Throwable var1) {
      StringWriter var2 = new StringWriter(256);
      PrintWriter var3 = new PrintWriter(var2, false);
      var1.printStackTrace(var3);
      var3.flush();
      return var2.toString();
   }
}

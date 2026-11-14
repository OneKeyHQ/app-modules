package io.intercom.android.sdk.errorreporting;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.ArrayList;
import java.util.List;

class ExceptionParser {
   private static final String INTERCOM_SDK_PACKAGE = "io.intercom.android.sdk";
   private static final String[] ALLOWED_PACKAGES = new String[]{
      "io.intercom.android.sdk", "java.", "kotlin.", "kotlinx.", "android.", "androidx.", "com.android.", "com.google."
   };
   private static final String REDACTION_LINE = "[Non Intercom/OS method]";
   private static final String ERROR_REPORTING_PACKAGE = "io.intercom.android.sdk.errorreporting.";
   private static final String[] SKIP_PREFIXES = new String[]{
      "java.",
      "javax.inject.",
      "kotlin.",
      "kotlinx.",
      "_COROUTINE.",
      "android.",
      "androidx.",
      "com.android.",
      "com.google.",
      "okhttp3.",
      "okio.",
      "retrofit2.",
      "coil.",
      "io.ably.",
      "org.msgpack.",
      "com.davidehrmann.vcdiff.",
      "org.slf4j.",
      "com.facebook.shimmer.",
      "com.jakewharton.retrofit2.",
      "org.jetbrains.",
      "org.intellij.",
      "org.jspecify.",
      "com.intercom.",
      "io.intercom."
   };
   private static final int MAX_UNKNOWN_FRAMES = 1;

   ErrorReport createReportFrom(Throwable var1, @Nullable String var2, boolean var3) {
      return new ErrorReport(this.createExceptionReports(var1), TimeProvider.SYSTEM.currentTimeMillis(), var2, var3);
   }

   private List createExceptionReports(Throwable var1) {
      List var2 = new ArrayList();

      for (Throwable var3 = var1; var3 != null; var3 = var3.getCause()) {
         StackTraceElement[] var4 = var3.getStackTrace();
         var2.add(new ExceptionReport(var3.getClass().getName(), var3.getLocalizedMessage(), this.getStacktraceString(var4), this.getStructuredFrames(var4)));
      }

      return var2;
   }

   private List getStructuredFrames(StackTraceElement[] var1) {
      List var2 = new ArrayList();

      for (StackTraceElement var6 : var1) {
         if (this.isFromAllowedPackage(var6)) {
            String var7 = var6.getClassName();
            int var8 = var6.getLineNumber();
            var2.add(new StackFrame(var7, var6.getMethodName(), var6.getFileName(), var8 >= 0 ? var8 : null, var7.startsWith("io.intercom.android.sdk")));
         } else {
            var2.add(new StackFrame("[Non Intercom/OS method]", null, null, null, false));
         }
      }

      return var2;
   }

   private String getStacktraceString(StackTraceElement[] var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < var1.length; var3++) {
         StackTraceElement var4 = var1[var3];
         var2.append(this.isFromAllowedPackage(var4) ? var4.toString() : "[Non Intercom/OS method]");
         if (var3 < var1.length - 1) {
            var2.append("\n");
         }
      }

      return var2.toString();
   }

   private boolean isFromAllowedPackage(StackTraceElement var1) {
      String var2 = var1.getClassName();

      for (String var6 : ALLOWED_PACKAGES) {
         if (var2.startsWith(var6)) {
            return true;
         }
      }

      return false;
   }

   boolean attributesCrashToIntercom(Throwable var1) {
      for (Throwable var2 = var1; var2 != null; var2 = var2.getCause()) {
         if (attributesToIntercom(var2.getStackTrace())) {
            return true;
         }
      }

      return false;
   }

   private static boolean attributesToIntercom(StackTraceElement[] var0) {
      int var1 = 0;

      for (StackTraceElement var5 : var0) {
         String var6 = var5.getClassName();
         if (var6.startsWith("io.intercom.android.sdk")) {
            if (!var6.startsWith("io.intercom.android.sdk.errorreporting.")) {
               return true;
            }
         } else if (!startsWithAnySkipPrefix(var6)) {
            if (++var1 > 1) {
               return false;
            }
         }
      }

      return false;
   }

   private static boolean startsWithAnySkipPrefix(String var0) {
      for (String var4 : SKIP_PREFIXES) {
         if (var0.startsWith(var4)) {
            return true;
         }
      }

      return false;
   }
}

package io.intercom.android.sdk.errorreporting;

import android.content.Context;
import com.google.gson.Gson;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ErrorReporter {
   static final int MAX_HANDLED_REPORTS_PER_SESSION = 1;
   private final ErrorReportStore reportStore;
   private final Provider apiProvider;
   private final ExceptionParser exceptionParser;
   private final IntercomDataLayer dataLayer;
   private final Set reportedHandledKeys = Collections.synchronizedSet(new LinkedHashSet());

   public static ErrorReporter create(Context var0, Gson var1, Provider var2, IntercomDataLayer var3) {
      ErrorReportStore var4 = ErrorReportStore.create(var0.getApplicationContext(), var1);
      return new ErrorReporter(var4, var2, new ExceptionParser(), var3);
   }

   ErrorReporter(ErrorReportStore var1, Provider var2, ExceptionParser var3, IntercomDataLayer var4) {
      this.reportStore = var1;
      this.apiProvider = var2;
      this.exceptionParser = var3;
      this.dataLayer = var4;
   }

   void saveReport(Throwable var1) {
      if (this.exceptionParser.attributesCrashToIntercom(var1)) {
         String var2 = this.dataLayer.getSessionId();
         this.reportStore.saveToDisk(this.exceptionParser.createReportFrom(var1, var2, false));
      }
   }

   public void reportHandledException(Throwable var1) {
      String var2 = var1.getClass().getName() + ":" + var1.getMessage();
      if (!this.reportedHandledKeys.contains(var2)) {
         if (this.reportedHandledKeys.size() < 1) {
            this.reportedHandledKeys.add(var2);
            String var3 = this.dataLayer.getSessionId();
            ErrorReport var4 = this.exceptionParser.createReportFrom(var1, var3, true);
            this.reportStore.sendImmediately(var4, this.apiProvider);
         }
      }
   }

   public void sendSavedReport() {
      this.reportStore.sendSavedReport(this.apiProvider);
   }

   public void enableExceptionHandler() {
      IntercomExceptionHandler.enable(this);
   }

   public void disableExceptionHandler() {
      IntercomExceptionHandler.disable();
      this.reportStore.deleteFromDisk();
   }
}

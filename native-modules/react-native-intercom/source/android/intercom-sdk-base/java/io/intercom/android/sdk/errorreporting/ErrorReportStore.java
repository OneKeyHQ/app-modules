package io.intercom.android.sdk.errorreporting;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.VisibleForTesting;
import com.google.gson.Gson;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.persistence.JsonStorage;
import java.io.File;
import java.util.concurrent.RejectedExecutionException;

class ErrorReportStore {
   private static final String REPORT_FILE_PATH = "intercom-error.json";
   private static final Twig TWIG = LumberMill.getLogger();
   private final File reportFile;
   private final JsonStorage storage;

   public static ErrorReportStore create(Context var0, Gson var1) {
      File var2 = new File(var0.getCacheDir(), "intercom-error.json");
      return new ErrorReportStore(var2, new JsonStorage(var1));
   }

   @VisibleForTesting
   ErrorReportStore(File var1, JsonStorage var2) {
      this.reportFile = var1;
      this.storage = var2;
   }

   void sendSavedReport(Provider var1) {
      try {
         AsyncTask.execute(new ErrorReportStore$1(this, var1));
      } catch (RejectedExecutionException var3) {
         TWIG.internal("Couldn't queue up sending of event: " + var3);
      }
   }

   private void readAndSendReport(Api var1) {
      this.storage.loadThenDelete(this.reportFile, ErrorReport.class, new ErrorReportStore$2(this, var1));
   }

   void saveToDisk(ErrorReport var1) {
      this.storage.saveToFileAsJson(var1, this.reportFile);
   }

   void sendImmediately(ErrorReport var1, Provider var2) {
      try {
         AsyncTask.execute(() -> ((Api)var2.get()).sendErrorReport(var1));
      } catch (RejectedExecutionException var4) {
         TWIG.internal("Couldn't send handled exception report: " + var4);
      }
   }

   void deleteFromDisk() {
      this.storage.loadThenDelete(this.reportFile, ErrorReport.class, new ErrorReportStore$3(this));
   }

   // $VF: synthetic method
   static File access$000(ErrorReportStore var0) {
      return var0.reportFile;
   }

   // $VF: synthetic method
   static void access$100(ErrorReportStore var0, Api var1) {
      var0.readAndSendReport(var1);
   }
}

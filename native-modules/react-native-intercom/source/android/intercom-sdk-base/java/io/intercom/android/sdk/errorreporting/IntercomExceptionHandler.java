package io.intercom.android.sdk.errorreporting;

import java.lang.Thread.UncaughtExceptionHandler;

class IntercomExceptionHandler implements UncaughtExceptionHandler {
   private final UncaughtExceptionHandler originalHandler;
   private final ErrorReporter reporter;

   static void enable(ErrorReporter var0) {
      UncaughtExceptionHandler var1 = Thread.getDefaultUncaughtExceptionHandler();
      if (!(var1 instanceof IntercomExceptionHandler)) {
         Thread.setDefaultUncaughtExceptionHandler(new IntercomExceptionHandler(var1, var0));
      }
   }

   static void disable() {
      UncaughtExceptionHandler var0 = Thread.getDefaultUncaughtExceptionHandler();
      if (var0 instanceof IntercomExceptionHandler) {
         IntercomExceptionHandler var1 = (IntercomExceptionHandler)var0;
         Thread.setDefaultUncaughtExceptionHandler(var1.originalHandler);
      }
   }

   IntercomExceptionHandler(UncaughtExceptionHandler var1, ErrorReporter var2) {
      this.originalHandler = var1;
      this.reporter = var2;
   }

   @Override
   public void uncaughtException(Thread var1, Throwable var2) {
      this.reporter.saveReport(var2);
      if (this.originalHandler != null) {
         this.originalHandler.uncaughtException(var1, var2);
      } else {
         System.err.printf("Exception in thread \"%s\" ", var1.getName());
         var2.printStackTrace(System.err);
      }
   }
}

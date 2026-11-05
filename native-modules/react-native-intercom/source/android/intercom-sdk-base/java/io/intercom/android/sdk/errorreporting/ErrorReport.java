package io.intercom.android.sdk.errorreporting;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ErrorReport {
   @SerializedName("exception_reports")
   private final List exceptionReports;
   @SerializedName("session_id")
   @Nullable
   private final String sessionId;
   private final long timestamp;
   private final boolean handled;

   ErrorReport(List var1, long var2, @Nullable String var4, boolean var5) {
      this.exceptionReports = var1;
      this.timestamp = var2;
      this.sessionId = var4;
      this.handled = var5;
   }

   public List getExceptionReports() {
      return this.exceptionReports;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   @Nullable
   public String getSessionId() {
      return this.sessionId;
   }
}

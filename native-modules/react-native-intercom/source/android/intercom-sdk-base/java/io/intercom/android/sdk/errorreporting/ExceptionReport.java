package io.intercom.android.sdk.errorreporting;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExceptionReport {
   @SerializedName("class_name")
   private final String className;
   private final String message;
   private final String stacktrace;
   @Nullable
   private final List frames;

   ExceptionReport(String var1, String var2, String var3, @Nullable List var4) {
      this.className = var1;
      this.message = var2;
      this.stacktrace = var3;
      this.frames = var4;
   }

   public String getClassName() {
      return this.className;
   }

   public String getMessage() {
      return this.message;
   }

   public String getStacktrace() {
      return this.stacktrace;
   }

   @Nullable
   List getFrames() {
      return this.frames;
   }
}

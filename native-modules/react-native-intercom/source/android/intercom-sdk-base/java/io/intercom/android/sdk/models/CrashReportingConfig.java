package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u00032\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\f\u001a\u00020\rH×\u0001J\t\u0010\u000e\u001a\u00020\u000fH×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/models/CrashReportingConfig;",
         "",
         "enabled",
         "",
         "<init>",
         "(Z)V",
         "getEnabled",
         "()Z",
         "component1",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class CrashReportingConfig {
   @SerializedName("enabled")
   private final boolean enabled;
   public static int $stable;

   static {}

   public CrashReportingConfig(boolean var1) {
      this.enabled = var1;
   }

   // $VF: synthetic method
   public CrashReportingConfig(boolean var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (boolean) (true) : var1));
   }

   public final boolean getEnabled() {
      return this.enabled;
   }

   public final boolean component1() {
      return this.enabled;
   }

   @NotNull
   public final CrashReportingConfig copy(boolean var1) {
      return new CrashReportingConfig(var1);
   }

   // $VF: synthetic method
   public static CrashReportingConfig copy$default(CrashReportingConfig var0, boolean var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.enabled;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "CrashReportingConfig(enabled=" + this.enabled + ')';
   }

   @Override
   public int hashCode() {
      return Boolean.hashCode(this.enabled);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof CrashReportingConfig)) {
         return false;
      }

      CrashReportingConfig var2 = (CrashReportingConfig)var1;
      return this.enabled == var2.enabled;
   }

   public CrashReportingConfig() {
      this(false, 1, null);
   }
}

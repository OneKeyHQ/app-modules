package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\r\u001a\u00020\u000eH×\u0001J\t\u0010\u000f\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/models/UploadMetadata;",
         "",
         "safeAppId",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "getSafeAppId",
         "()Ljava/lang/String;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class UploadMetadata {
   @SerializedName("safe_app_id")
   @NotNull
   private final String safeAppId;
   public static int $stable;

   static {}

   public UploadMetadata(@NotNull String var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "safeAppId");
      this.safeAppId = var1;
   }

   // $VF: synthetic method
   public UploadMetadata(String var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (String) ("") : var1));
   }

   @NotNull
   public final String getSafeAppId() {
      return this.safeAppId;
   }

   @NotNull
   public final String component1() {
      return this.safeAppId;
   }

   @NotNull
   public final UploadMetadata copy(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "safeAppId");
      return new UploadMetadata(var1);
   }

   // $VF: synthetic method
   public static UploadMetadata copy$default(UploadMetadata var0, String var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.safeAppId;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "UploadMetadata(safeAppId=" + this.safeAppId + ')';
   }

   @Override
   public int hashCode() {
      return this.safeAppId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof UploadMetadata)) {
         return false;
      }

      UploadMetadata var2 = (UploadMetadata)var1;
      return Intrinsics.areEqual(this.safeAppId, var2.safeAppId);
   }

   public UploadMetadata() {
      this(null, 1, null);
   }
}

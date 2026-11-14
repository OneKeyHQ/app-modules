package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\r\u001a\u00020\u000eH×\u0001J\t\u0010\u000f\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveySenderAvatar;",
         "",
         "squareImg128",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "getSquareImg128",
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
public final class SurveySenderAvatar {
   @SerializedName("square_128")
   @NotNull
   private final String squareImg128;
   public static int $stable;

   static {}

   public SurveySenderAvatar(@NotNull String var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "squareImg128");
      this.squareImg128 = var1;
   }

   // $VF: synthetic method
   public SurveySenderAvatar(String var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (String) ("") : var1));
   }

   @NotNull
   public final String getSquareImg128() {
      return this.squareImg128;
   }

   @NotNull
   public final String component1() {
      return this.squareImg128;
   }

   @NotNull
   public final SurveySenderAvatar copy(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "squareImg128");
      return new SurveySenderAvatar(var1);
   }

   // $VF: synthetic method
   public static SurveySenderAvatar copy$default(SurveySenderAvatar var0, String var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.squareImg128;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "SurveySenderAvatar(squareImg128=" + this.squareImg128 + ')';
   }

   @Override
   public int hashCode() {
      return this.squareImg128.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof SurveySenderAvatar)) {
         return false;
      }

      SurveySenderAvatar var2 = (SurveySenderAvatar)var1;
      return Intrinsics.areEqual(this.squareImg128, var2.squareImg128);
   }

   public SurveySenderAvatar() {
      this(null, 1, null);
   }
}

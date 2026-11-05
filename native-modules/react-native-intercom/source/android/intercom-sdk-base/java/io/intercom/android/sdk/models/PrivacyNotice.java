package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000e\u001a\u00020\u00032\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0010\u001a\u00020\u0011H×\u0001J\t\u0010\u0012\u001a\u00020\u0005H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\bR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/models/PrivacyNotice;",
         "",
         "isDismissed",
         "",
         "text",
         "",
         "<init>",
         "(ZLjava/lang/String;)V",
         "()Z",
         "getText",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class PrivacyNotice {
   @SerializedName("is_dismissed")
   private final boolean isDismissed;
   @SerializedName("paragraph")
   @NotNull
   private final String text;
   public static int $stable;

   static {}

   public PrivacyNotice(boolean var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var2, "text");
      this.isDismissed = var1;
      this.text = var2;
   }

   public final boolean isDismissed() {
      return this.isDismissed;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   public final boolean component1() {
      return this.isDismissed;
   }

   @NotNull
   public final String component2() {
      return this.text;
   }

   @NotNull
   public final PrivacyNotice copy(boolean var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var2, "text");
      return new PrivacyNotice(var1, var2);
   }

   // $VF: synthetic method
   public static PrivacyNotice copy$default(PrivacyNotice var0, boolean var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.isDismissed;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.text;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "PrivacyNotice(isDismissed=" + this.isDismissed + ", text=" + this.text + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.isDismissed);
      return var1 * 31 + this.text.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof PrivacyNotice)) {
         return false;
      }

      PrivacyNotice var2 = (PrivacyNotice)var1;
      return this.isDismissed != var2.isDismissed ? false : Intrinsics.areEqual(this.text, var2.text);
   }
}

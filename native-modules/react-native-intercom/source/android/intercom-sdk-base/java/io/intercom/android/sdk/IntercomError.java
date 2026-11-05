package io.intercom.android.sdk;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0003H×\u0001J\t\u0010\u0013\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/IntercomError;",
         "",
         "errorCode",
         "",
         "errorMessage",
         "",
         "<init>",
         "(ILjava/lang/String;)V",
         "getErrorCode",
         "()I",
         "getErrorMessage",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomError {
   private final int errorCode;
   @NotNull
   private final String errorMessage;
   public static int $stable;

   static {}

   public IntercomError(int var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var2, "errorMessage");
      this.errorCode = var1;
      this.errorMessage = var2;
   }

   // $VF: synthetic method
   public IntercomError(int var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (int) (0) : var1), (((var3 & 2) != 0) ? (String) ("") : var2));
   }

   public final int getErrorCode() {
      return this.errorCode;
   }

   @NotNull
   public final String getErrorMessage() {
      return this.errorMessage;
   }

   public final int component1() {
      return this.errorCode;
   }

   @NotNull
   public final String component2() {
      return this.errorMessage;
   }

   @NotNull
   public final IntercomError copy(int var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var2, "errorMessage");
      return new IntercomError(var1, var2);
   }

   // $VF: synthetic method
   public static IntercomError copy$default(IntercomError var0, int var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.errorCode;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.errorMessage;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "IntercomError(errorCode=" + this.errorCode + ", errorMessage=" + this.errorMessage + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.errorCode);
      return var1 * 31 + this.errorMessage.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof IntercomError)) {
         return false;
      }

      IntercomError var2 = (IntercomError)var1;
      return this.errorCode != var2.errorCode ? false : Intrinsics.areEqual(this.errorMessage, var2.errorMessage);
   }

   public IntercomError() {
      this(0, null, 3, null);
   }
}

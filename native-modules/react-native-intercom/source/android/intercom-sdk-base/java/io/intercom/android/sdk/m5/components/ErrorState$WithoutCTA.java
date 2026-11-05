package io.intercom.android.sdk.m5.components;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B'\u0012\b\b\u0003\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0003\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0003\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\fJ.\u0010\u0011\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00032\n\b\u0003\u0010\u0005\u001a\u0004\u0018\u00010\u0003HÇ\u0001¢\u0006\u0002\u0010\u0012J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H×\u0003J\t\u0010\u0017\u001a\u00020\u0003H×\u0001J\t\u0010\u0018\u001a\u00020\u0019H×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\u0004\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u0003X\u0096\u0004¢\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\f¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/m5/components/ErrorState$WithoutCTA;",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "iconId",
         "",
         "messageResId",
         "additionalMessageResId",
         "<init>",
         "(IILjava/lang/Integer;)V",
         "getIconId",
         "()I",
         "getMessageResId",
         "getAdditionalMessageResId",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "component1",
         "component2",
         "component3",
         "copy",
         "(IILjava/lang/Integer;)Lio/intercom/android/sdk/m5/components/ErrorState$WithoutCTA;",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ErrorState$WithoutCTA extends ErrorState {
   private final int iconId;
   private final int messageResId;
   @Nullable
   private final Integer additionalMessageResId;
   public static int $stable;

   static {}

   public ErrorState$WithoutCTA(@DrawableRes int var1, @StringRes int var2, @StringRes @Nullable Integer var3) {
      super(null);
      this.iconId = var1;
      this.messageResId = var2;
      this.additionalMessageResId = var3;
   }

   // $VF: synthetic method
   public ErrorState$WithoutCTA(int var1, int var2, Integer var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (int) (drawable.intercom_ic_warning) : var1), (((var4 & 2) != 0) ? (int) (string.intercom_spaces_error_title) : var2), (((var4 & 4) != 0) ? (Integer) (string.intercom_spaces_error_description) : var3));
   }

   @Override
   public int getIconId() {
      return this.iconId;
   }

   @Override
   public int getMessageResId() {
      return this.messageResId;
   }

   @Nullable
   @Override
   public Integer getAdditionalMessageResId() {
      return this.additionalMessageResId;
   }

   public final int component1() {
      return this.iconId;
   }

   public final int component2() {
      return this.messageResId;
   }

   @Nullable
   public final Integer component3() {
      return this.additionalMessageResId;
   }

   @NotNull
   public final ErrorState$WithoutCTA copy(@DrawableRes int var1, @StringRes int var2, @StringRes @Nullable Integer var3) {
      return new ErrorState$WithoutCTA(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ErrorState$WithoutCTA copy$default(ErrorState$WithoutCTA var0, int var1, int var2, Integer var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.iconId;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.messageResId;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.additionalMessageResId;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "WithoutCTA(iconId=" + this.iconId + ", messageResId=" + this.messageResId + ", additionalMessageResId=" + this.additionalMessageResId + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.iconId);
      var1 = var1 * 31 + Integer.hashCode(this.messageResId);
      return var1 * 31 + (this.additionalMessageResId == null ? 0 : this.additionalMessageResId.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ErrorState$WithoutCTA)) {
         return false;
      } else {
         ErrorState$WithoutCTA var2 = (ErrorState$WithoutCTA)var1;
         if (this.iconId != var2.iconId) {
            return false;
         } else {
            return this.messageResId != var2.messageResId ? false : Intrinsics.areEqual(this.additionalMessageResId, var2.additionalMessageResId);
         }
      }
   }

   public ErrorState$WithoutCTA() {
      this(0, 0, null, 7, null);
   }
}

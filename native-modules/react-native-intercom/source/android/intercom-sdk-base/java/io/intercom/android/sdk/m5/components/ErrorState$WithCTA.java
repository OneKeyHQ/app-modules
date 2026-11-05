package io.intercom.android.sdk.m5.components;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B?\u0012\b\b\u0003\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0003\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0003\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0003\u0010\u0006\u001a\u00020\u0003\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\u0010J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\t0\bHÆ\u0003JH\u0010\u001a\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00032\n\b\u0003\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\u0006\u001a\u00020\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bHÇ\u0001¢\u0006\u0002\u0010\u001bJ\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH×\u0003J\t\u0010 \u001a\u00020\u0003H×\u0001J\t\u0010!\u001a\u00020\"H×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0004\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u0003X\u0096\u0004¢\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006#",
   d2 = {
         "Lio/intercom/android/sdk/m5/components/ErrorState$WithCTA;",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "iconId",
         "",
         "messageResId",
         "additionalMessageResId",
         "ctaResId",
         "onCtaClick",
         "Lkotlin/Function0;",
         "",
         "<init>",
         "(IILjava/lang/Integer;ILkotlin/jvm/functions/Function0;)V",
         "getIconId",
         "()I",
         "getMessageResId",
         "getAdditionalMessageResId",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "getCtaResId",
         "getOnCtaClick",
         "()Lkotlin/jvm/functions/Function0;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "(IILjava/lang/Integer;ILkotlin/jvm/functions/Function0;)Lio/intercom/android/sdk/m5/components/ErrorState$WithCTA;",
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
public final class ErrorState$WithCTA extends ErrorState {
   private final int iconId;
   private final int messageResId;
   @Nullable
   private final Integer additionalMessageResId;
   private final int ctaResId;
   @NotNull
   private final Function0 onCtaClick;
   public static int $stable;

   static {}

   public ErrorState$WithCTA(@DrawableRes int var1, @StringRes int var2, @StringRes @Nullable Integer var3, @StringRes int var4, @NotNull Function0 var5) {
      Intrinsics.checkNotNullParameter(var5, "onCtaClick");
      super(null);
      this.iconId = var1;
      this.messageResId = var2;
      this.additionalMessageResId = var3;
      this.ctaResId = var4;
      this.onCtaClick = var5;
   }

   // $VF: synthetic method
   public ErrorState$WithCTA(int var1, int var2, Integer var3, int var4, Function0 var5, int var6, DefaultConstructorMarker var7) {
      this((((var6 & 1) != 0) ? (int) (drawable.intercom_ic_warning) : var1), (((var6 & 2) != 0) ? (int) (string.intercom_spaces_error_title) : var2), (((var6 & 4) != 0) ? (Integer) (string.intercom_spaces_error_description) : var3), (((var6 & 8) != 0) ? (int) (string.intercom_reload) : var4), var5);
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

   public final int getCtaResId() {
      return this.ctaResId;
   }

   @NotNull
   public final Function0 getOnCtaClick() {
      return this.onCtaClick;
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

   public final int component4() {
      return this.ctaResId;
   }

   @NotNull
   public final Function0 component5() {
      return this.onCtaClick;
   }

   @NotNull
   public final ErrorState$WithCTA copy(
      @DrawableRes int var1, @StringRes int var2, @StringRes @Nullable Integer var3, @StringRes int var4, @NotNull Function0 var5
   ) {
      Intrinsics.checkNotNullParameter(var5, "onCtaClick");
      return new ErrorState$WithCTA(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static ErrorState$WithCTA copy$default(ErrorState$WithCTA var0, int var1, int var2, Integer var3, int var4, Function0 var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.iconId;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.messageResId;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.additionalMessageResId;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.ctaResId;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.onCtaClick;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "WithCTA(iconId="
         + this.iconId
         + ", messageResId="
         + this.messageResId
         + ", additionalMessageResId="
         + this.additionalMessageResId
         + ", ctaResId="
         + this.ctaResId
         + ", onCtaClick="
         + this.onCtaClick
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.iconId);
      var1 = var1 * 31 + Integer.hashCode(this.messageResId);
      var1 = var1 * 31 + (this.additionalMessageResId == null ? 0 : this.additionalMessageResId.hashCode());
      var1 = var1 * 31 + Integer.hashCode(this.ctaResId);
      return var1 * 31 + this.onCtaClick.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ErrorState$WithCTA)) {
         return false;
      } else {
         ErrorState$WithCTA var2 = (ErrorState$WithCTA)var1;
         if (this.iconId != var2.iconId) {
            return false;
         } else if (this.messageResId != var2.messageResId) {
            return false;
         } else if (!Intrinsics.areEqual(this.additionalMessageResId, var2.additionalMessageResId)) {
            return false;
         } else {
            return this.ctaResId != var2.ctaResId ? false : Intrinsics.areEqual(this.onCtaClick, var2.onCtaClick);
         }
      }
   }
}

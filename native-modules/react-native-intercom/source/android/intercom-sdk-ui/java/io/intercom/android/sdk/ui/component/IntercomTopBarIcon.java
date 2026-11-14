package io.intercom.android.sdk.ui.component;

import androidx.annotation.DrawableRes;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B)\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J/\u0010\u0014\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0003H×\u0001J\t\u0010\u0019\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/ui/component/IntercomTopBarIcon;",
         "",
         "iconRes",
         "",
         "contentDescription",
         "",
         "onClick",
         "Lkotlin/Function0;",
         "",
         "<init>",
         "(ILjava/lang/String;Lkotlin/jvm/functions/Function0;)V",
         "getIconRes",
         "()I",
         "getContentDescription",
         "()Ljava/lang/String;",
         "getOnClick",
         "()Lkotlin/jvm/functions/Function0;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomTopBarIcon {
   private final int iconRes;
   @Nullable
   private final String contentDescription;
   @NotNull
   private final Function0 onClick;
   public static int $stable;

   static {}

   public IntercomTopBarIcon(@DrawableRes int var1, @Nullable String var2, @NotNull Function0 var3) {
      super();
      Intrinsics.checkNotNullParameter(var3, "onClick");
      this.iconRes = var1;
      this.contentDescription = var2;
      this.onClick = var3;
   }

   public final int getIconRes() {
      return this.iconRes;
   }

   @Nullable
   public final String getContentDescription() {
      return this.contentDescription;
   }

   @NotNull
   public final Function0 getOnClick() {
      return this.onClick;
   }

   public final int component1() {
      return this.iconRes;
   }

   @Nullable
   public final String component2() {
      return this.contentDescription;
   }

   @NotNull
   public final Function0 component3() {
      return this.onClick;
   }

   @NotNull
   public final IntercomTopBarIcon copy(@DrawableRes int var1, @Nullable String var2, @NotNull Function0 var3) {
      Intrinsics.checkNotNullParameter(var3, "onClick");
      return new IntercomTopBarIcon(var1, var2, var3);
   }

   // $VF: synthetic method
   public static IntercomTopBarIcon copy$default(IntercomTopBarIcon var0, int var1, String var2, Function0 var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.iconRes;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.contentDescription;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.onClick;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "IntercomTopBarIcon(iconRes=" + this.iconRes + ", contentDescription=" + this.contentDescription + ", onClick=" + this.onClick + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.iconRes);
      var1 = var1 * 31 + (this.contentDescription == null ? 0 : this.contentDescription.hashCode());
      return var1 * 31 + this.onClick.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomTopBarIcon)) {
         return false;
      } else {
         IntercomTopBarIcon var2 = (IntercomTopBarIcon)var1;
         if (this.iconRes != var2.iconRes) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.contentDescription, var2.contentDescription) ? false : Intrinsics.areEqual(this.onClick, var2.onClick);
         }
      }
   }
}

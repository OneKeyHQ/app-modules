package io.intercom.android.sdk.tickets;

import androidx.annotation.DrawableRes;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.ui.common.StringProvider;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0001\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010\rJ\t\u0010\u0013\u001a\u00020\u0007HÆ\u0003J.\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0003\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001¢\u0006\u0002\u0010\u0015J\u0013\u0010\u0016\u001a\u00020\u00032\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0005H×\u0001J\t\u0010\u0019\u001a\u00020\u001aH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/tickets/ConversationButtonState;",
         "",
         "showButton",
         "",
         "iconRes",
         "",
         "text",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "<init>",
         "(ZLjava/lang/Integer;Lio/intercom/android/sdk/ui/common/StringProvider;)V",
         "getShowButton",
         "()Z",
         "getIconRes",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "getText",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "component1",
         "component2",
         "component3",
         "copy",
         "(ZLjava/lang/Integer;Lio/intercom/android/sdk/ui/common/StringProvider;)Lio/intercom/android/sdk/tickets/ConversationButtonState;",
         "equals",
         "other",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ConversationButtonState {
   private final boolean showButton;
   @Nullable
   private final Integer iconRes;
   @NotNull
   private final StringProvider text;
   public static final int $stable = StringProvider.$stable;

   public ConversationButtonState(boolean var1, @DrawableRes @Nullable Integer var2, @NotNull StringProvider var3) {
      super();
      Intrinsics.checkNotNullParameter(var3, "text");
      this.showButton = var1;
      this.iconRes = var2;
      this.text = var3;
   }

   public final boolean getShowButton() {
      return this.showButton;
   }

   @Nullable
   public final Integer getIconRes() {
      return this.iconRes;
   }

   @NotNull
   public final StringProvider getText() {
      return this.text;
   }

   public final boolean component1() {
      return this.showButton;
   }

   @Nullable
   public final Integer component2() {
      return this.iconRes;
   }

   @NotNull
   public final StringProvider component3() {
      return this.text;
   }

   @NotNull
   public final ConversationButtonState copy(boolean var1, @DrawableRes @Nullable Integer var2, @NotNull StringProvider var3) {
      Intrinsics.checkNotNullParameter(var3, "text");
      return new ConversationButtonState(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ConversationButtonState copy$default(ConversationButtonState var0, boolean var1, Integer var2, StringProvider var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.showButton;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.iconRes;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.text;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationButtonState(showButton=" + this.showButton + ", iconRes=" + this.iconRes + ", text=" + this.text + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.showButton);
      var1 = var1 * 31 + (this.iconRes == null ? 0 : this.iconRes.hashCode());
      return var1 * 31 + this.text.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ConversationButtonState)) {
         return false;
      } else {
         ConversationButtonState var2 = (ConversationButtonState)var1;
         if (this.showButton != var2.showButton) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.iconRes, var2.iconRes) ? false : Intrinsics.areEqual(this.text, var2.text);
         }
      }
   }
}

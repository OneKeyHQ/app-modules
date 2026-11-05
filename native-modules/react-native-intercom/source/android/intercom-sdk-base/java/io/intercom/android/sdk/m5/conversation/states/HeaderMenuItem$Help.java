package io.intercom.android.sdk.m5.conversation.states;

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
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00032\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0015H×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$Help;",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem;",
         "enabled",
         "",
         "label",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "<init>",
         "(ZLio/intercom/android/sdk/ui/common/StringProvider;)V",
         "getEnabled",
         "()Z",
         "getLabel",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "component1",
         "component2",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HeaderMenuItem$Help extends HeaderMenuItem {
   private final boolean enabled;
   @NotNull
   private final StringProvider label;
   public static final int $stable = StringProvider.$stable;

   public HeaderMenuItem$Help(boolean var1, @NotNull StringProvider var2) {
      Intrinsics.checkNotNullParameter(var2, "label");
      super(null);
      this.enabled = var1;
      this.label = var2;
   }

   @Override
   public boolean getEnabled() {
      return this.enabled;
   }

   @NotNull
   @Override
   public StringProvider getLabel() {
      return this.label;
   }

   public final boolean component1() {
      return this.enabled;
   }

   @NotNull
   public final StringProvider component2() {
      return this.label;
   }

   @NotNull
   public final HeaderMenuItem$Help copy(boolean var1, @NotNull StringProvider var2) {
      Intrinsics.checkNotNullParameter(var2, "label");
      return new HeaderMenuItem$Help(var1, var2);
   }

   // $VF: synthetic method
   public static HeaderMenuItem$Help copy$default(HeaderMenuItem$Help var0, boolean var1, StringProvider var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.enabled;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.label;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Help(enabled=" + this.enabled + ", label=" + this.label + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.enabled);
      return var1 * 31 + this.label.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof HeaderMenuItem$Help)) {
         return false;
      }

      HeaderMenuItem$Help var2 = (HeaderMenuItem$Help)var1;
      return this.enabled != var2.enabled ? false : Intrinsics.areEqual(this.label, var2.label);
   }
}

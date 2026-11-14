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
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0006HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00032\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0018H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0004\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0014\u0010\u0005\u001a\u00020\u0006X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$Tickets;",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem;",
         "hasUnreadTickets",
         "",
         "enabled",
         "label",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "<init>",
         "(ZZLio/intercom/android/sdk/ui/common/StringProvider;)V",
         "getHasUnreadTickets",
         "()Z",
         "getEnabled",
         "getLabel",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "component1",
         "component2",
         "component3",
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
public final class HeaderMenuItem$Tickets extends HeaderMenuItem {
   private final boolean hasUnreadTickets;
   private final boolean enabled;
   @NotNull
   private final StringProvider label;
   public static final int $stable = StringProvider.$stable;

   public HeaderMenuItem$Tickets(boolean var1, boolean var2, @NotNull StringProvider var3) {
      Intrinsics.checkNotNullParameter(var3, "label");
      super(null);
      this.hasUnreadTickets = var1;
      this.enabled = var2;
      this.label = var3;
   }

   public final boolean getHasUnreadTickets() {
      return this.hasUnreadTickets;
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
      return this.hasUnreadTickets;
   }

   public final boolean component2() {
      return this.enabled;
   }

   @NotNull
   public final StringProvider component3() {
      return this.label;
   }

   @NotNull
   public final HeaderMenuItem$Tickets copy(boolean var1, boolean var2, @NotNull StringProvider var3) {
      Intrinsics.checkNotNullParameter(var3, "label");
      return new HeaderMenuItem$Tickets(var1, var2, var3);
   }

   // $VF: synthetic method
   public static HeaderMenuItem$Tickets copy$default(HeaderMenuItem$Tickets var0, boolean var1, boolean var2, StringProvider var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.hasUnreadTickets;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.enabled;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.label;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Tickets(hasUnreadTickets=" + this.hasUnreadTickets + ", enabled=" + this.enabled + ", label=" + this.label + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.hasUnreadTickets);
      var1 = var1 * 31 + Boolean.hashCode(this.enabled);
      return var1 * 31 + this.label.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HeaderMenuItem$Tickets)) {
         return false;
      } else {
         HeaderMenuItem$Tickets var2 = (HeaderMenuItem$Tickets)var1;
         if (this.hasUnreadTickets != var2.hasUnreadTickets) {
            return false;
         } else {
            return this.enabled != var2.enabled ? false : Intrinsics.areEqual(this.label, var2.label);
         }
      }
   }
}

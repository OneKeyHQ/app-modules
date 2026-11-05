package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.ui.common.StringProvider;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0017\u001a\u00020\tHÆ\u0003J1\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u00072\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH×\u0003J\t\u0010\u001c\u001a\u00020\u001dH×\u0001J\t\u0010\u001e\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\b\u001a\u00020\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$Messages;",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem;",
         "totalCountIndicator",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$TotalCountIndicator;",
         "unreadMessagesCount",
         "",
         "enabled",
         "",
         "label",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$TotalCountIndicator;Ljava/lang/String;ZLio/intercom/android/sdk/ui/common/StringProvider;)V",
         "getTotalCountIndicator",
         "()Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$TotalCountIndicator;",
         "getUnreadMessagesCount",
         "()Ljava/lang/String;",
         "getEnabled",
         "()Z",
         "getLabel",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HeaderMenuItem$Messages extends HeaderMenuItem {
   @NotNull
   private final HeaderMenuItem$TotalCountIndicator totalCountIndicator;
   @NotNull
   private final String unreadMessagesCount;
   private final boolean enabled;
   @NotNull
   private final StringProvider label;
   public static final int $stable = StringProvider.$stable;

   public HeaderMenuItem$Messages(@NotNull HeaderMenuItem$TotalCountIndicator var1, @NotNull String var2, boolean var3, @NotNull StringProvider var4) {
      Intrinsics.checkNotNullParameter(var1, "totalCountIndicator");
      Intrinsics.checkNotNullParameter(var2, "unreadMessagesCount");
      Intrinsics.checkNotNullParameter(var4, "label");
      super(null);
      this.totalCountIndicator = var1;
      this.unreadMessagesCount = var2;
      this.enabled = var3;
      this.label = var4;
   }

   // $VF: synthetic method
   public HeaderMenuItem$Messages(
      HeaderMenuItem$TotalCountIndicator var1, String var2, boolean var3, StringProvider var4, int var5, DefaultConstructorMarker var6
   ) {
      this(var1, var2, (((var5 & 4) != 0) ? (boolean) (true) : var3), var4);
   }

   @NotNull
   public final HeaderMenuItem$TotalCountIndicator getTotalCountIndicator() {
      return this.totalCountIndicator;
   }

   @NotNull
   public final String getUnreadMessagesCount() {
      return this.unreadMessagesCount;
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

   @NotNull
   public final HeaderMenuItem$TotalCountIndicator component1() {
      return this.totalCountIndicator;
   }

   @NotNull
   public final String component2() {
      return this.unreadMessagesCount;
   }

   public final boolean component3() {
      return this.enabled;
   }

   @NotNull
   public final StringProvider component4() {
      return this.label;
   }

   @NotNull
   public final HeaderMenuItem$Messages copy(@NotNull HeaderMenuItem$TotalCountIndicator var1, @NotNull String var2, boolean var3, @NotNull StringProvider var4) {
      Intrinsics.checkNotNullParameter(var1, "totalCountIndicator");
      Intrinsics.checkNotNullParameter(var2, "unreadMessagesCount");
      Intrinsics.checkNotNullParameter(var4, "label");
      return new HeaderMenuItem$Messages(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static HeaderMenuItem$Messages copy$default(
      HeaderMenuItem$Messages var0, HeaderMenuItem$TotalCountIndicator var1, String var2, boolean var3, StringProvider var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.totalCountIndicator;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.unreadMessagesCount;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.enabled;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.label;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Messages(totalCountIndicator="
         + this.totalCountIndicator
         + ", unreadMessagesCount="
         + this.unreadMessagesCount
         + ", enabled="
         + this.enabled
         + ", label="
         + this.label
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.totalCountIndicator.hashCode();
      var1 = var1 * 31 + this.unreadMessagesCount.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.enabled);
      return var1 * 31 + this.label.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HeaderMenuItem$Messages)) {
         return false;
      } else {
         HeaderMenuItem$Messages var2 = (HeaderMenuItem$Messages)var1;
         if (!Intrinsics.areEqual(this.totalCountIndicator, var2.totalCountIndicator)) {
            return false;
         } else if (!Intrinsics.areEqual(this.unreadMessagesCount, var2.unreadMessagesCount)) {
            return false;
         } else {
            return this.enabled != var2.enabled ? false : Intrinsics.areEqual(this.label, var2.label);
         }
      }
   }
}

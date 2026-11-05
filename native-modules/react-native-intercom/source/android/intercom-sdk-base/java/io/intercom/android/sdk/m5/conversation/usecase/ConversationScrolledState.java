package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0007HÆ\u0003J1\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0013\u001a\u00020\u00032\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0007H×\u0001J\t\u0010\u0016\u001a\u00020\u0017H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/ConversationScrolledState;",
         "",
         "scrolled",
         "",
         "isLandscape",
         "isLargeFont",
         "lastSeenItemIndex",
         "",
         "<init>",
         "(ZZZI)V",
         "getScrolled",
         "()Z",
         "getLastSeenItemIndex",
         "()I",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ConversationScrolledState {
   private final boolean scrolled;
   private final boolean isLandscape;
   private final boolean isLargeFont;
   private final int lastSeenItemIndex;
   public static int $stable;

   static {}

   public ConversationScrolledState(boolean var1, boolean var2, boolean var3, int var4) {
      this.scrolled = var1;
      this.isLandscape = var2;
      this.isLargeFont = var3;
      this.lastSeenItemIndex = var4;
   }

   // $VF: synthetic method
   public ConversationScrolledState(boolean var1, boolean var2, boolean var3, int var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, var3, (((var5 & 8) != 0) ? (int) (0) : var4));
   }

   public final boolean getScrolled() {
      return this.scrolled;
   }

   public final boolean isLandscape() {
      return this.isLandscape;
   }

   public final boolean isLargeFont() {
      return this.isLargeFont;
   }

   public final int getLastSeenItemIndex() {
      return this.lastSeenItemIndex;
   }

   public final boolean component1() {
      return this.scrolled;
   }

   public final boolean component2() {
      return this.isLandscape;
   }

   public final boolean component3() {
      return this.isLargeFont;
   }

   public final int component4() {
      return this.lastSeenItemIndex;
   }

   @NotNull
   public final ConversationScrolledState copy(boolean var1, boolean var2, boolean var3, int var4) {
      return new ConversationScrolledState(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static ConversationScrolledState copy$default(
      ConversationScrolledState var0, boolean var1, boolean var2, boolean var3, int var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.scrolled;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.isLandscape;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.isLargeFont;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.lastSeenItemIndex;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationScrolledState(scrolled="
         + this.scrolled
         + ", isLandscape="
         + this.isLandscape
         + ", isLargeFont="
         + this.isLargeFont
         + ", lastSeenItemIndex="
         + this.lastSeenItemIndex
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.scrolled);
      var1 = var1 * 31 + Boolean.hashCode(this.isLandscape);
      var1 = var1 * 31 + Boolean.hashCode(this.isLargeFont);
      return var1 * 31 + Integer.hashCode(this.lastSeenItemIndex);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ConversationScrolledState)) {
         return false;
      } else {
         ConversationScrolledState var2 = (ConversationScrolledState)var1;
         if (this.scrolled != var2.scrolled) {
            return false;
         } else if (this.isLandscape != var2.isLandscape) {
            return false;
         } else {
            return this.isLargeFont != var2.isLargeFont ? false : this.lastSeenItemIndex == var2.lastSeenItemIndex;
         }
      }
   }
}

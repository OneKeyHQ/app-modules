package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0010\u001a\u00020\u0003H×\u0001J\t\u0010\u0011\u001a\u00020\u0012H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/JumpToBottomButtonState;",
         "",
         "unreadMessages",
         "",
         "lastSeenItemIndex",
         "<init>",
         "(II)V",
         "getUnreadMessages",
         "()I",
         "getLastSeenItemIndex",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class JumpToBottomButtonState {
   private final int unreadMessages;
   private final int lastSeenItemIndex;
   public static int $stable;

   static {}

   public JumpToBottomButtonState(int var1, int var2) {
      this.unreadMessages = var1;
      this.lastSeenItemIndex = var2;
   }

   // $VF: synthetic method
   public JumpToBottomButtonState(int var1, int var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (int) (0) : var1), (((var3 & 2) != 0) ? (int) (0) : var2));
   }

   public final int getUnreadMessages() {
      return this.unreadMessages;
   }

   public final int getLastSeenItemIndex() {
      return this.lastSeenItemIndex;
   }

   public final int component1() {
      return this.unreadMessages;
   }

   public final int component2() {
      return this.lastSeenItemIndex;
   }

   @NotNull
   public final JumpToBottomButtonState copy(int var1, int var2) {
      return new JumpToBottomButtonState(var1, var2);
   }

   // $VF: synthetic method
   public static JumpToBottomButtonState copy$default(JumpToBottomButtonState var0, int var1, int var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.unreadMessages;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.lastSeenItemIndex;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "JumpToBottomButtonState(unreadMessages=" + this.unreadMessages + ", lastSeenItemIndex=" + this.lastSeenItemIndex + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.unreadMessages);
      return var1 * 31 + Integer.hashCode(this.lastSeenItemIndex);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof JumpToBottomButtonState)) {
         return false;
      }

      JumpToBottomButtonState var2 = (JumpToBottomButtonState)var1;
      return this.unreadMessages != var2.unreadMessages ? false : this.lastSeenItemIndex == var2.lastSeenItemIndex;
   }

   public JumpToBottomButtonState() {
      this(0, 0, 3, null);
   }
}

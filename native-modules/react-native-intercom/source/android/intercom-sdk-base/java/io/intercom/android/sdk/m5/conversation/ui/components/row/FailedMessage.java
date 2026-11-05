package io.intercom.android.sdk.m5.conversation.ui.components.row;

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
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J#\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÇ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FailedMessage;",
         "",
         "message",
         "",
         "onRetryMessageClicked",
         "Lkotlin/Function0;",
         "",
         "<init>",
         "(Ljava/lang/String;Lkotlin/jvm/functions/Function0;)V",
         "getMessage",
         "()Ljava/lang/String;",
         "getOnRetryMessageClicked",
         "()Lkotlin/jvm/functions/Function0;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class FailedMessage {
   @NotNull
   private final String message;
   @NotNull
   private final Function0 onRetryMessageClicked;
   public static int $stable;

   static {}

   public FailedMessage(@NotNull String var1, @NotNull Function0 var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "message");
      Intrinsics.checkNotNullParameter(var2, "onRetryMessageClicked");
      this.message = var1;
      this.onRetryMessageClicked = var2;
   }

   @NotNull
   public final String getMessage() {
      return this.message;
   }

   @NotNull
   public final Function0 getOnRetryMessageClicked() {
      return this.onRetryMessageClicked;
   }

   @NotNull
   public final String component1() {
      return this.message;
   }

   @NotNull
   public final Function0 component2() {
      return this.onRetryMessageClicked;
   }

   @NotNull
   public final FailedMessage copy(@NotNull String var1, @NotNull Function0 var2) {
      Intrinsics.checkNotNullParameter(var1, "message");
      Intrinsics.checkNotNullParameter(var2, "onRetryMessageClicked");
      return new FailedMessage(var1, var2);
   }

   // $VF: synthetic method
   public static FailedMessage copy$default(FailedMessage var0, String var1, Function0 var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.message;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.onRetryMessageClicked;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "FailedMessage(message=" + this.message + ", onRetryMessageClicked=" + this.onRetryMessageClicked + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.message.hashCode();
      return var1 * 31 + this.onRetryMessageClicked.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof FailedMessage)) {
         return false;
      }

      FailedMessage var2 = (FailedMessage)var1;
      return !Intrinsics.areEqual(this.message, var2.message) ? false : Intrinsics.areEqual(this.onRetryMessageClicked, var2.onRetryMessageClicked);
   }
}

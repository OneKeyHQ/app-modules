package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0003\b\u0001\u0018\u00002\u00060\u0001j\u0002`\u0002B\u001b\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\b¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/RecordingException;",
         "Ljava/lang/Exception;",
         "Lkotlin/Exception;",
         "message",
         "",
         "cause",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/Throwable;)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class RecordingException extends Exception {
   public static int $stable;

   static {}

   public RecordingException(@NotNull String var1, @Nullable Throwable var2) {
      Intrinsics.checkNotNullParameter(var1, "message");
      super(var1, var2);
   }

   // $VF: synthetic method
   public RecordingException(String var1, Throwable var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (Throwable) (null) : var2));
   }
}

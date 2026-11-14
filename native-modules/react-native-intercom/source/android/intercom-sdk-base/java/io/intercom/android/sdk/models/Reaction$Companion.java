package io.intercom.android.sdk.models;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0007¨\u0006\n",
   d2 = {
         "Lio/intercom/android/sdk/models/Reaction$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/models/Reaction;",
         "index",
         "",
         "imageUrl",
         "",
         "intercom-sdk-base_release"
   }
)
public final class Reaction$Companion {
   private Reaction$Companion() {
   }

   @JvmStatic
   @NotNull
   public final Reaction create(int var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var2, "imageUrl");
      return new Reaction(var1, var2);
   }

   // $VF: synthetic method
   public Reaction$Companion(DefaultConstructorMarker var1) {
      this();
   }
}

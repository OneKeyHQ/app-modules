package io.intercom.android.sdk;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "AblyManager.kt",
   l = {216, 226},
   i = {0, 1, 1},
   s = {"L$0", "L$0", "L$1"},
   n = {"this", "this", "connectConfig"},
   m = "getAblyConfiguration",
   c = "io.intercom.android.sdk.AblyManager"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class AblyManager$getAblyConfiguration$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final AblyManager this$0;
   int label;

   AblyManager$getAblyConfiguration$1(AblyManager var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return AblyManager.access$getAblyConfiguration(this.this$0, this);
   }
}

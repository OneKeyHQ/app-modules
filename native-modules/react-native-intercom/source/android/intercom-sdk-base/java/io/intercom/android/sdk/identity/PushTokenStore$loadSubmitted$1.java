package io.intercom.android.sdk.identity;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(f = "PushTokenStore.kt", l = 111, i = {}, s = {}, n = {}, m = "loadSubmitted", c = "io.intercom.android.sdk.identity.PushTokenStore")
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PushTokenStore$loadSubmitted$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final PushTokenStore this$0;
   int label;

   PushTokenStore$loadSubmitted$1(PushTokenStore var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return PushTokenStore.access$loadSubmitted(this.this$0, this);
   }
}

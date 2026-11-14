package io.intercom.android.sdk.api;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(f = "WrapperPrefsStore.kt", l = 151, i = 0, s = "L$0", n = "action", m = "writeSafely", c = "io.intercom.android.sdk.api.WrapperPrefsStore")
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class WrapperPrefsStore$writeSafely$1 extends ContinuationImpl {
   Object L$0;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final WrapperPrefsStore this$0;
   int label;

   WrapperPrefsStore$writeSafely$1(WrapperPrefsStore var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return WrapperPrefsStore.access$writeSafely(this.this$0, null, null, this);
   }
}

package io.intercom.android.sdk.utilities;

import io.intercom.android.sdk.m5.data.IntercomEvent$BaseResponseReceived;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;

@DebugMetadata(
   f = "FirstMessageUtils.kt",
   l = 28,
   i = 0,
   s = "L$0",
   n = "this",
   m = "emit",
   c = "io.intercom.android.sdk.utilities.FirstMessageUtilsKt$handleFirstMessageToast$1$1"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class FirstMessageUtilsKt$handleFirstMessageToast$1$1$emit$1 extends ContinuationImpl {
   Object L$0;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final FirstMessageUtilsKt$handleFirstMessageToast$1$1 this$0;
   int label;

   FirstMessageUtilsKt$handleFirstMessageToast$1$1$emit$1(FirstMessageUtilsKt$handleFirstMessageToast$1$1 var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.emit((IntercomEvent$BaseResponseReceived)null, this);
   }
}

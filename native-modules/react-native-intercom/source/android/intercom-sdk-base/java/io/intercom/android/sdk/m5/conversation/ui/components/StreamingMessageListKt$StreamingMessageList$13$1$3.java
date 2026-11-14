package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.MutableState;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;

@DebugMetadata(
   f = "StreamingMessageList.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$StreamingMessageList$13$1$3"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "it", ""}
)
final class StreamingMessageListKt$StreamingMessageList$13$1$3 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   boolean Z$0;
   // $VF: synthetic field
   final MutableState $isListAtTheBottom$delegate;

   StreamingMessageListKt$StreamingMessageList$13$1$3(MutableState var1, Continuation var2) {
      super(2, var2);
      this.$isListAtTheBottom$delegate = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            boolean var2 = this.Z$0;
            StreamingMessageListKt.access$StreamingMessageList$lambda$32(this.$isListAtTheBottom$delegate, var2);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      StreamingMessageListKt$StreamingMessageList$13$1$3 var3 = new StreamingMessageListKt$StreamingMessageList$13$1$3(this.$isListAtTheBottom$delegate, var2);
      var3.Z$0 = (Boolean)var1;
      return var3;
   }

   public final Object invoke(boolean var1, Continuation var2) {
      return ((StreamingMessageListKt$StreamingMessageList$13$1$3)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke(((Boolean)var1).booleanValue(), (Continuation)var2);
   }
}

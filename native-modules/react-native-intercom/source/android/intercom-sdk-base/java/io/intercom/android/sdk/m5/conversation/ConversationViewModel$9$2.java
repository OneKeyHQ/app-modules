package io.intercom.android.sdk.m5.conversation;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.text.StringsKt;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$9$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\b\n\u0000\n\u0002\u0010\u000e\n\u0000\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\n",
   d2 = {"<anonymous>", "", "it"}
)
final class ConversationViewModel$9$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   Object L$0;

   ConversationViewModel$9$2(Continuation var1) {
      super(2, var1);
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            String var2 = (String)this.L$0;
            return StringsKt.trim(var2).toString();
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      ConversationViewModel$9$2 var3 = new ConversationViewModel$9$2(var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(String var1, Continuation var2) {
      return ((ConversationViewModel$9$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((String)var1, (Continuation)var2);
   }
}

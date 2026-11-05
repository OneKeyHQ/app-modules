package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function3;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$5$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\n",
   d2 = {"<anonymous>", "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent;", "nexusEvent", "resumed", ""}
)
final class ConversationViewModel$5$2 extends SuspendLambda implements Function3 {
   int label;
   // $VF: synthetic field
   Object L$0;
   // $VF: synthetic field
   boolean Z$0;

   ConversationViewModel$5$2(Continuation var1) {
      super(3, var1);
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            ParsedNexusEvent$ConversationNexusEvent var2 = (ParsedNexusEvent$ConversationNexusEvent)this.L$0;
            boolean var3 = this.Z$0;
            return var3 ? var2 : null;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   public final Object invoke(ParsedNexusEvent$ConversationNexusEvent var1, boolean var2, Continuation var3) {
      ConversationViewModel$5$2 var4 = new ConversationViewModel$5$2(var3);
      var4.L$0 = var1;
      var4.Z$0 = var2;
      return var4.invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      return this.invoke((ParsedNexusEvent$ConversationNexusEvent)var1, ((Boolean)var2).booleanValue(), (Continuation)var3);
   }
}

package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent;
import io.intercom.android.sdk.m5.conversation.usecase.RefreshUnreadConversationsCountUseCase;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = 365,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$5$3"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "it", "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent;"}
)
final class ConversationViewModel$5$3 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationViewModel this$0;

   ConversationViewModel$5$3(ConversationViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            RefreshUnreadConversationsCountUseCase var10000 = ConversationViewModel.access$getRefreshUnreadConversationsCountUseCase$p(this.this$0);
            MutableStateFlow var10001 = ConversationViewModel.access$getClientState$p(this.this$0);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.invoke(var10001, var10002) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ConversationViewModel$5$3(this.this$0, var2);
   }

   public final Object invoke(ParsedNexusEvent$ConversationNexusEvent var1, Continuation var2) {
      return ((ConversationViewModel$5$3)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((ParsedNexusEvent$ConversationNexusEvent)var1, (Continuation)var2);
   }
}

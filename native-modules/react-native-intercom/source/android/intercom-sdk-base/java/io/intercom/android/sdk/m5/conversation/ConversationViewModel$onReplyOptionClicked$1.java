package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.usecase.SendQuickReplyUseCase;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.ReplyOption;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = 511,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$onReplyOptionClicked$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ConversationViewModel$onReplyOptionClicked$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationViewModel this$0;
   // $VF: synthetic field
   final ReplyOption $replyOption;

   ConversationViewModel$onReplyOptionClicked$1(ConversationViewModel var1, ReplyOption var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$replyOption = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Conversation var10000 = ((ConversationClientState)ConversationViewModel.access$getClientState$p(this.this$0).getValue()).getConversation();
            if (var10000 != null) {
               Conversation var2 = var10000;
               ConversationViewModel var3 = this.this$0;
               ReplyOption var4 = this.$replyOption;
               Conversation var5 = var2;
               int var8/* $VF was: $i$a$-let-ConversationViewModel$onReplyOptionClicked$1$1x */ = 0;
               SendQuickReplyUseCase var9 = ConversationViewModel.access$getSendQuickReplyUseCase$p(var3);
               MutableStateFlow var10001 = ConversationViewModel.access$getClientState$p(var3);
               Part var10003 = (Part)CollectionsKt.last(var5.parts());
               this.label = 1;
               if (var9.invoke(var10001, var4, var10003, this) == var7) {
                  return var7;
               }
            }
            break;
         case 1:
            int var6 = 0;
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ConversationViewModel$onReplyOptionClicked$1(this.this$0, this.$replyOption, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationViewModel$onReplyOptionClicked$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.data.GetConversationReason;
import io.intercom.android.sdk.m5.conversation.usecase.Action;
import io.intercom.android.sdk.m5.conversation.usecase.RefreshConversationUseCase;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = 295,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$2$2$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ConversationViewModel$2$2$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationViewModel this$0;
   // $VF: synthetic field
   final Action $notificationAction;

   ConversationViewModel$2$2$1(ConversationViewModel var1, Action var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$notificationAction = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            RefreshConversationUseCase var10000 = ConversationViewModel.access$getRefreshConversationUseCase$p(this.this$0);
            MutableStateFlow var10001 = ConversationViewModel.access$getClientState$p(this.this$0);
            GetConversationReason var10002 = GetConversationReason.NEW_COMMENT;
            Continuation var10003 = this;
            this.label = 1;
            if (var10000.invoke(var10001, var10002, var10003) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      ConversationViewModel.access$getSoundEffectsUseCase$p(this.this$0).invoke$intercom_sdk_base_release(this.$notificationAction);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ConversationViewModel$2$2$1(this.this$0, this.$notificationAction, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationViewModel$2$2$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

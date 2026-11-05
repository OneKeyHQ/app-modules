package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.usecase.SendMessageUseCase;
import java.util.List;
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
   l = 485,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$sendMessage$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ConversationViewModel$sendMessage$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationViewModel this$0;
   // $VF: synthetic field
   final String $messageText;
   // $VF: synthetic field
   final List $mediaUploadItems;

   ConversationViewModel$sendMessage$1(ConversationViewModel var1, String var2, List var3, Continuation var4) {
      super(2, var4);
      this.this$0 = var1;
      this.$messageText = var2;
      this.$mediaUploadItems = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            SendMessageUseCase var10000 = ConversationViewModel.access$getSendMessageUseCase$p(this.this$0);
            MutableStateFlow var10001 = ConversationViewModel.access$getClientState$p(this.this$0);
            String var10002 = this.$messageText;
            List var10003 = this.$mediaUploadItems;
            Continuation var10005 = this;
            this.label = 1;
            if (SendMessageUseCase.invoke$default(var10000, var10001, var10002, var10003, null, var10005, 8, null) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      this.this$0.clearMediaUploadItems();
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ConversationViewModel$sendMessage$1(this.this$0, this.$messageText, this.$mediaUploadItems, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationViewModel$sendMessage$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

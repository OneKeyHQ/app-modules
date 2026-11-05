package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$onInputChange$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ConversationViewModel$onInputChange$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationViewModel this$0;
   // $VF: synthetic field
   final ComposerInputType $inputType;

   ConversationViewModel$onInputChange$1(ConversationViewModel var1, ComposerInputType var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$inputType = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            ConversationViewModel.access$getChangeInputUseCase$p(this.this$0)
               .invoke(ConversationViewModel.access$getClientState$p(this.this$0), this.$inputType);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ConversationViewModel$onInputChange$1(this.this$0, this.$inputType, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationViewModel$onInputChange$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

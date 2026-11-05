package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.State;
import androidx.compose.ui.focus.FocusManager;
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
   f = "MessageComposer.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.composer.MessageComposerKt$MessageComposer$13$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class MessageComposerKt$MessageComposer$13$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final FocusManager $focusManager;
   // $VF: synthetic field
   final State $keyboardAsState$delegate;

   MessageComposerKt$MessageComposer$13$1(FocusManager var1, State var2, Continuation var3) {
      super(2, var3);
      this.$focusManager = var1;
      this.$keyboardAsState$delegate = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (MessageComposerKt.access$MessageComposer$lambda$32(this.$keyboardAsState$delegate).isDismissed()) {
               FocusManager.clearFocus$default(this.$focusManager, false, 1, null);
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new MessageComposerKt$MessageComposer$13$1(this.$focusManager, this.$keyboardAsState$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((MessageComposerKt$MessageComposer$13$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

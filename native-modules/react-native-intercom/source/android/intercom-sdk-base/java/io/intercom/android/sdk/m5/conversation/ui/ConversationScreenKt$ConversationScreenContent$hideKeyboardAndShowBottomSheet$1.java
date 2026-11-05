package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.State;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DelayKt;

@DebugMetadata(
   f = "ConversationScreen.kt",
   l = 533,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$hideKeyboardAndShowBottomSheet$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ConversationScreenKt$ConversationScreenContent$hideKeyboardAndShowBottomSheet$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final MutableState $openBottomSheet;
   // $VF: synthetic field
   final State $keyboardAsState$delegate;

   ConversationScreenKt$ConversationScreenContent$hideKeyboardAndShowBottomSheet$1(MutableState var1, State var2, Continuation var3) {
      super(2, var3);
      this.$openBottomSheet = var1;
      this.$keyboardAsState$delegate = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (ConversationScreenKt.access$ConversationScreenContent$lambda$184(this.$keyboardAsState$delegate).isVisible()) {
               Continuation var10001 = this;
               this.label = 1;
               if (DelayKt.delay(200L, var10001) == var2) {
                  return var2;
               }
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      this.$openBottomSheet.setValue(Boxing.boxBoolean(true));
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ConversationScreenKt$ConversationScreenContent$hideKeyboardAndShowBottomSheet$1(this.$openBottomSheet, this.$keyboardAsState$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationScreenKt$ConversationScreenContent$hideKeyboardAndShowBottomSheet$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

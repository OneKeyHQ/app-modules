package io.intercom.android.sdk.m5.conversation.ui;

import android.content.Context;
import androidx.compose.material3.SnackbarHostState;
import io.intercom.android.sdk.m5.conversation.ConversationViewModel;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content;
import io.intercom.android.sdk.m5.conversation.states.NetworkState;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "ConversationScreen.kt",
   l = 277,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreen$13$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ConversationScreenKt$ConversationScreen$13$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationUiState $conversationUiState;
   // $VF: synthetic field
   final SnackbarHostState $snackbarHostState;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final ConversationViewModel $conversationViewModel;

   ConversationScreenKt$ConversationScreen$13$1(ConversationUiState var1, SnackbarHostState var2, Context var3, ConversationViewModel var4, Continuation var5) {
      super(2, var5);
      this.$conversationUiState = var1;
      this.$snackbarHostState = var2;
      this.$context = var3;
      this.$conversationViewModel = var4;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            NetworkState var10000 = ((ConversationUiState$Content)this.$conversationUiState).getNetworkState();
            SnackbarHostState var10001 = this.$snackbarHostState;
            Context var10002 = this.$context;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredExpressionCapture_377_0 = ((io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreen$13$1) this).$conversationViewModel;
            Function0 var10003 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreen$13$1.invokeSuspend$lambda$0(recoveredExpressionCapture_377_0));
            Continuation var10004 = this;
            this.label = 1;
            if (ConversationScreenKt.access$showNetworkMessage(var10000, var10001, var10002, var10003, var10004) == var2) {
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
      return new ConversationScreenKt$ConversationScreen$13$1(
         this.$conversationUiState, this.$snackbarHostState, this.$context, this.$conversationViewModel, var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationScreenKt$ConversationScreen$13$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$0(ConversationViewModel var0) {
      var0.onNetworkMessageDismissed();
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

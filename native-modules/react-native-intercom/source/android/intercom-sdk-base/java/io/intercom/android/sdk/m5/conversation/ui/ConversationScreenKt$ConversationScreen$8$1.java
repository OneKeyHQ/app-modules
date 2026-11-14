package io.intercom.android.sdk.m5.conversation.ui;

import android.content.Context;
import androidx.compose.material3.SnackbarHostState;
import androidx.compose.runtime.MutableState;
import io.intercom.android.sdk.m5.conversation.ConversationViewModel;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.SharedFlow;

@DebugMetadata(
   f = "ConversationScreen.kt",
   l = 192,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreen$8$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ConversationScreenKt$ConversationScreen$8$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationViewModel $conversationViewModel;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final SnackbarHostState $snackbarHostState;
   // $VF: synthetic field
   final MutableState $showUploadSizeLimitDialog$delegate;
   // $VF: synthetic field
   final MutableState $uploadErrorDialogState$delegate;
   // $VF: synthetic field
   final MutableState $deleteMessageDialogPart$delegate;

   ConversationScreenKt$ConversationScreen$8$1(
      ConversationViewModel var1, Context var2, SnackbarHostState var3, MutableState var4, MutableState var5, MutableState var6, Continuation var7
   ) {
      super(2, var7);
      this.$conversationViewModel = var1;
      this.$context = var2;
      this.$snackbarHostState = var3;
      this.$showUploadSizeLimitDialog$delegate = var4;
      this.$uploadErrorDialogState$delegate = var5;
      this.$deleteMessageDialogPart$delegate = var6;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            SharedFlow var10000 = this.$conversationViewModel.getUiEffect();
            FlowCollector var10001 = new ConversationScreenKt$ConversationScreen$8$1$1(
               this.$context,
               this.$snackbarHostState,
               this.$showUploadSizeLimitDialog$delegate,
               this.$uploadErrorDialogState$delegate,
               this.$deleteMessageDialogPart$delegate
            );
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      throw new KotlinNothingValueException();
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ConversationScreenKt$ConversationScreen$8$1(
         this.$conversationViewModel,
         this.$context,
         this.$snackbarHostState,
         this.$showUploadSizeLimitDialog$delegate,
         this.$uploadErrorDialogState$delegate,
         this.$deleteMessageDialogPart$delegate,
         var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationScreenKt$ConversationScreen$8$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

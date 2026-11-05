package io.intercom.android.sdk.m5.conversation;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = 364,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$5"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nConversationViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$5\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,864:1\n32#2:865\n17#2:866\n19#2:870\n17#2:871\n19#2:875\n46#3:867\n51#3:869\n46#3:872\n51#3:874\n105#4:868\n105#4:873\n*S KotlinDebug\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$5\n*L\n358#1:865\n358#1:866\n358#1:870\n359#1:871\n359#1:875\n358#1:867\n358#1:869\n359#1:872\n359#1:874\n358#1:868\n359#1:873\n*E\n"
)
final class ConversationViewModel$5 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationViewModel this$0;

   ConversationViewModel$5(ConversationViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Flow var2 = ConversationViewModel.access$getConversationRepository$p(this.this$0).realTimeEvents();
            int var3 = 0;
            Flow var4 = var2;
            int var5 = 0;
            Flow var6 = var4;
            int var7 = 0;
            int var8 = 0;
            var2 = new ConversationViewModel$5$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1(var6);
            ConversationViewModel var11 = this.this$0;
            int $i$f$filterx = 0;
            Flow var13 = var2;
            int $i$f$unsafeTransformx = 0;
            var7 = 0;
            Flow var10000 = FlowKt.filterNotNull(
               FlowKt.flowCombine(
                  new ConversationViewModel$5$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1(var13, var11),
                  ConversationViewModel.access$getResumedState$p(this.this$0),
                  new ConversationViewModel$5$2(null)
               )
            );
            Function2 var10001 = new ConversationViewModel$5$3(this.this$0, null);
            Continuation var10002 = this;
            this.label = 1;
            if (FlowKt.collectLatest(var10000, var10001, var10002) == var9) {
               return var9;
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
      return new ConversationViewModel$5(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationViewModel$5)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

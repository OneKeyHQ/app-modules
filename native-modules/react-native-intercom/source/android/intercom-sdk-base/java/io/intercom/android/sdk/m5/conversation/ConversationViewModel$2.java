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
import kotlinx.coroutines.flow.FlowCollector;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = 282,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nConversationViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$2\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,864:1\n32#2:865\n17#2:866\n19#2:870\n17#2:871\n19#2:875\n46#3:867\n51#3:869\n46#3:872\n51#3:874\n105#4:868\n105#4:873\n*S KotlinDebug\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$2\n*L\n277#1:865\n277#1:866\n277#1:870\n278#1:871\n278#1:875\n277#1:867\n277#1:869\n278#1:872\n278#1:874\n277#1:868\n278#1:873\n*E\n"
)
final class ConversationViewModel$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final ConversationViewModel this$0;

   ConversationViewModel$2(ConversationViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var10 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            CoroutineScope var2 = (CoroutineScope)this.L$0;
            Flow var3 = ConversationViewModel.access$getConversationRepository$p(this.this$0).realTimeEvents();
            int var4 = 0;
            Flow var5 = var3;
            int var6 = 0;
            Flow var7 = var5;
            int var8 = 0;
            int var9 = 0;
            var3 = new ConversationViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1(var7);
            ConversationViewModel var12 = this.this$0;
            int $i$f$filterx = 0;
            Flow var14 = var3;
            int $i$f$unsafeTransformx = 0;
            var8 = 0;
            Flow var10000 = new ConversationViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1(var14, var12);
            FlowCollector var10001 = new ConversationViewModel$2$2(this.this$0, var2);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var10) {
               return var10;
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
      ConversationViewModel$2 var3 = new ConversationViewModel$2(this.this$0, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationViewModel$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

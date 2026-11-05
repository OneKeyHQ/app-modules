package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.MutableState;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DelayKt;

@DebugMetadata(
   f = "EnhancedCodeBlock.kt",
   l = 255,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt$CodeBlockHeader$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class EnhancedCodeBlockKt$CodeBlockHeader$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final MutableState $copied$delegate;

   EnhancedCodeBlockKt$CodeBlockHeader$1$1(MutableState var1, Continuation var2) {
      super(2, var2);
      this.$copied$delegate = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (!EnhancedCodeBlockKt.access$CodeBlockHeader_RIQooxk$lambda$12(this.$copied$delegate)) {
               return Unit.INSTANCE;
            }

            long var10000 = EnhancedCodeBlockKt.access$getCOPY_CONFIRMATION_DURATION_MS$p();
            Continuation var10001 = this;
            this.label = 1;
            if (DelayKt.delay(var10000, var10001) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      EnhancedCodeBlockKt.access$CodeBlockHeader_RIQooxk$lambda$13(this.$copied$delegate, false);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new EnhancedCodeBlockKt$CodeBlockHeader$1$1(this.$copied$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((EnhancedCodeBlockKt$CodeBlockHeader$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

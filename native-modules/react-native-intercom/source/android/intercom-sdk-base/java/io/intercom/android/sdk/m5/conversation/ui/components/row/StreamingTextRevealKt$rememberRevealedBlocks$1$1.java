package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.MonotonicFrameClockKt;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.State;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Ref.FloatRef;
import kotlin.jvm.internal.Ref.LongRef;
import kotlin.ranges.RangesKt;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "StreamingTextReveal.kt",
   l = 44,
   i = {0, 0},
   s = {"L$0", "L$1"},
   n = {"rate", "lastFrameNanos"},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingTextRevealKt$rememberRevealedBlocks$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class StreamingTextRevealKt$rememberRevealedBlocks$1$1 extends SuspendLambda implements Function2 {
   Object L$0;
   Object L$1;
   int label;
   // $VF: synthetic field
   final State $currentTotal$delegate;
   // $VF: synthetic field
   final MutableFloatState $revealedUnitsRaw$delegate;

   StreamingTextRevealKt$rememberRevealedBlocks$1$1(State var1, MutableFloatState var2, Continuation var3) {
      super(2, var3);
      this.$currentTotal$delegate = var1;
      this.$revealedUnitsRaw$delegate = var2;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      FloatRef var2;
      LongRef var3;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            var2 = new FloatRef();
            var2.element = 15.0F;
            var3 = new LongRef();
            break;
         case 1:
            var3 = (LongRef)this.L$1;
            var2 = (FloatRef)this.L$0;
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Function1 var10000;
      Continuation var10001;
      do {
         final kotlin.jvm.internal.Ref.LongRef recoveredExpressionCapture_793_0 = var3;
         final kotlin.jvm.internal.Ref.FloatRef recoveredExpressionCapture_793_1 = var2;
         final androidx.compose.runtime.State recoveredExpressionCapture_793_2 = ((io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingTextRevealKt$rememberRevealedBlocks$1$1) this).$currentTotal$delegate;
         final androidx.compose.runtime.MutableFloatState recoveredExpressionCapture_793_3 = ((io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingTextRevealKt$rememberRevealedBlocks$1$1) this).$revealedUnitsRaw$delegate;
         var10000 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_793_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingTextRevealKt$rememberRevealedBlocks$1$1.invokeSuspend$lambda$0(recoveredExpressionCapture_793_0, recoveredExpressionCapture_793_1, recoveredExpressionCapture_793_2, recoveredExpressionCapture_793_3, ((java.lang.Long)recoveredExpressionArgument_793_0).longValue()));
         var10001 = this;
         this.L$0 = var2;
         this.L$1 = var3;
         this.label = 1;
      } while (MonotonicFrameClockKt.withFrameNanos(var10000, var10001) != var4);

      return var4;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new StreamingTextRevealKt$rememberRevealedBlocks$1$1(this.$currentTotal$delegate, this.$revealedUnitsRaw$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((StreamingTextRevealKt$rememberRevealedBlocks$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$0(LongRef var0, FloatRef var1, State var2, MutableFloatState var3, long var4) {
      if (var0.element != 0L) {
         float var6 = RangesKt.coerceAtMost((float)(var4 - var0.element) / 1.0E9F, 0.1F);
         float var7 = StreamingTextRevealKt.access$rememberRevealedBlocks$lambda$2(var2) - StreamingTextRevealKt.access$rememberRevealedBlocks$lambda$4(var3);
         if (var7 > 0.0F) {
            float var8 = RangesKt.coerceIn(var7 / 0.4F, 15.0F, 2000.0F);
            var1.element = var1.element + (var8 - var1.element) * RangesKt.coerceAtMost(var6 / 0.3F, 1.0F);
            StreamingTextRevealKt.access$rememberRevealedBlocks$lambda$5(
               var3,
               RangesKt.coerceAtMost(
                  StreamingTextRevealKt.access$rememberRevealedBlocks$lambda$4(var3) + var1.element * var6,
                  StreamingTextRevealKt.access$rememberRevealedBlocks$lambda$2(var2)
               )
            );
         }
      }

      var0.element = var4;
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

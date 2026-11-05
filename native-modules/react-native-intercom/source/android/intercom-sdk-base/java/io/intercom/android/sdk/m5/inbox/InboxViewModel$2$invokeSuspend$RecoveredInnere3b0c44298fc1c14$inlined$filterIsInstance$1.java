package io.intercom.android.sdk.m5.inbox;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00028\u00000\u0001J\u001c\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u0005H\u0096@¢\u0006\u0002\u0010\u0006¨\u0006\u0007¸\u0006\n",
   d2 = {
         "kotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1",
         "Lkotlinx/coroutines/flow/Flow;",
         "collect",
         "",
         "collector",
         "Lkotlinx/coroutines/flow/FlowCollector;",
         "(Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "kotlinx-coroutines-core",
         "kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$$inlined$unsafeFlow$1",
         "kotlinx/coroutines/flow/FlowKt__TransformKt$filter$$inlined$unsafeTransform$1",
         "kotlinx/coroutines/flow/FlowKt__TransformKt$filterIsInstance$$inlined$filter$1"
   }
)
@SourceDebugExtension(
   "SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n*L\n1#1,108:1\n47#2,5:109\n*E\n"
)
public final class InboxViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1 implements Flow {
   // $VF: synthetic field
   final Flow $this_unsafeTransform$inlined;

   public InboxViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1(Flow var1) {
      this.$this_unsafeTransform$inlined = var1;
   }

   @Override
   public Object collect(FlowCollector var1, Continuation var2) {
      FlowCollector var4 = var1;
      int var5 = 0;
      Object var10000 = this.$this_unsafeTransform$inlined
         .collect(new InboxViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1$2(var4), var2);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }
}

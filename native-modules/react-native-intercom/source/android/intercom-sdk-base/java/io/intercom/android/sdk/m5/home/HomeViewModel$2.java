package io.intercom.android.sdk.m5.home;

import androidx.lifecycle.FlowExtKt;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.State;
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

@DebugMetadata(f = "HomeViewModel.kt", l = 82, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.m5.home.HomeViewModel$2")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nHomeViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel$2\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,214:1\n32#2:215\n17#2:216\n19#2:220\n17#2:221\n19#2:225\n46#3:217\n51#3:219\n46#3:222\n51#3:224\n105#4:218\n105#4:223\n*S KotlinDebug\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel$2\n*L\n80#1:215\n80#1:216\n80#1:220\n81#1:221\n81#1:225\n80#1:217\n80#1:219\n81#1:222\n81#1:224\n80#1:218\n81#1:223\n*E\n"
)
final class HomeViewModel$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final HomeViewModel this$0;
   // $VF: synthetic field
   final Lifecycle $lifecycle;

   HomeViewModel$2(HomeViewModel var1, Lifecycle var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$lifecycle = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Flow var2 = FlowExtKt.flowWithLifecycle(HomeViewModel.access$getHomeRepository$p(this.this$0).realTimeEvents(), this.$lifecycle, State.RESUMED);
            int var3 = 0;
            Flow var4 = var2;
            int var5 = 0;
            Flow var6 = var4;
            int var7 = 0;
            int var8 = 0;
            var2 = new HomeViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1(var6);
            var3 = 0;
            var4 = var2;
            var5 = 0;
            int $i$f$unsafeFlowx = 0;
            Flow var10000 = new HomeViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1(var4);
            FlowCollector var10001 = new HomeViewModel$2$2(this.this$0);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var9) {
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
      return new HomeViewModel$2(this.this$0, this.$lifecycle, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HomeViewModel$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

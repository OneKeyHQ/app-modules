package io.intercom.android.sdk.helpcenter.search;

import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nEmitters.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$1\n*L\n1#1,49:1\n50#2:50\n73#3,2:51\n*E\n"
)
public final class ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2 implements FlowCollector {
   // $VF: synthetic field
   final FlowCollector $this_unsafeFlow;
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;

   public ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2(FlowCollector var1, ArticleSearchViewModel var2) {
      this.$this_unsafeFlow = var1;
      this.this$0 = var2;
   }

   @Override
   public final Object emit(Object var1, Continuation var2) {
      ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1 var3;
      label20: {
         if (var2 instanceof ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1) {
            var3 = (ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1)var2;
            if ((var3.label & -2147483648) != 0) {
               var3.label -= Integer.MIN_VALUE;
               break label20;
            }
         }

         var3 = new ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1(this, var2);
      }

      Object var4 = var3.result;
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var3.label) {
         case 0: {
            ResultKt.throwOnFailure(var4);
            Object var6 = var1;
            FlowCollector var7 = this.$this_unsafeFlow;
            int var12 = 0;
            FlowCollector var9 = var7;
            Pair var10 = (Pair)var6;
            int var11 = 0;
            ArticleSearchViewModel.access$setLastSearchedInput$p(this.this$0, (String)var10.getSecond());
            Object var10001 = var10.getFirst();
            var3.label = 1;
            if (var9.emit(var10001, var3) == var5) {
               return var5;
            }
            break;
         }
         case 1: {
            int var8 = 0;
            ResultKt.throwOnFailure(var4);
            break;
         }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }
}

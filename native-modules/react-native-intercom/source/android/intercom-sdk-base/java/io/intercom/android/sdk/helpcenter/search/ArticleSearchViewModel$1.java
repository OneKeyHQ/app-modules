package io.intercom.android.sdk.helpcenter.search;

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
import kotlinx.coroutines.flow.FlowKt;

@DebugMetadata(
   f = "ArticleSearchViewModel.kt",
   l = 120,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.helpcenter.search.ArticleSearchViewModel$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nArticleSearchViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$1\n+ 2 Merge.kt\nkotlinx/coroutines/flow/FlowKt__MergeKt\n+ 3 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 4 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 5 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,286:1\n189#2:287\n49#3:288\n51#3:292\n49#3:293\n51#3:297\n46#4:289\n51#4:291\n46#4:294\n51#4:296\n105#5:290\n105#5:295\n*S KotlinDebug\n*F\n+ 1 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$1\n*L\n61#1:287\n72#1:288\n72#1:292\n76#1:293\n76#1:297\n72#1:289\n72#1:291\n76#1:294\n76#1:296\n72#1:290\n76#1:295\n*E\n"
)
final class ArticleSearchViewModel$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;

   ArticleSearchViewModel$1(ArticleSearchViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var8 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Flow var2 = ArticleSearchViewModel.access$getSearchInput$p(this.this$0);
            ArticleSearchViewModel var3 = this.this$0;
            int var4 = 0;
            var2 = FlowKt.transformLatest(var2, new ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$flatMapLatest$1(null, var3));
            var3 = this.this$0;
            var4 = 0;
            Flow var5 = var2;
            int var6 = 0;
            int var7 = 0;
            var2 = new ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1(var5, var3);
            var3 = this.this$0;
            var4 = 0;
            var5 = var2;
            var6 = 0;
            var7 = 0;
            Flow var10000 = new ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$2(var5, var3);
            FlowCollector var10001 = new ArticleSearchViewModel$1$4(this.this$0);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var8) {
               return var8;
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
      return new ArticleSearchViewModel$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ArticleSearchViewModel$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

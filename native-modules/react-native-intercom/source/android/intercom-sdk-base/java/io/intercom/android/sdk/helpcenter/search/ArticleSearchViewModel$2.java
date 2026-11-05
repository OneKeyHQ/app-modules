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

@DebugMetadata(
   f = "ArticleSearchViewModel.kt",
   l = 128,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.helpcenter.search.ArticleSearchViewModel$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nArticleSearchViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$2\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,286:1\n32#2:287\n17#2:288\n19#2:292\n46#3:289\n51#3:291\n105#4:290\n*S KotlinDebug\n*F\n+ 1 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$2\n*L\n127#1:287\n127#1:288\n127#1:292\n127#1:289\n127#1:291\n127#1:290\n*E\n"
)
final class ArticleSearchViewModel$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;

   ArticleSearchViewModel$2(ArticleSearchViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Flow var2 = ArticleSearchViewModel.access$getIntercomDataLayer$p(this.this$0).getEvent();
            int var3 = 0;
            Flow var4 = var2;
            int var5 = 0;
            Flow var6 = var4;
            int var7 = 0;
            int var8 = 0;
            Flow var10000 = new ArticleSearchViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1(var6);
            FlowCollector var10001 = new ArticleSearchViewModel$2$1(this.this$0);
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
      return new ArticleSearchViewModel$2(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ArticleSearchViewModel$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

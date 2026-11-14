package io.intercom.android.sdk.helpcenter.search;

import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi$DefaultImpls;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.FlowKt;

@DebugMetadata(
   f = "ArticleSearchViewModel.kt",
   l = {191, 189},
   i = 0,
   s = "L$1",
   n = "it",
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.helpcenter.search.ArticleSearchViewModel$1$invokeSuspend$$inlined$flatMapLatest$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u0018\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00020\u00042\u0015\u0010\u0005\u001a\u0011H\u0003¢\u0006\f\b\u0006\u0012\b\b\u0007\u0012\u0004\b\b(\bH\n¨\u0006\t",
   d2 = {
         "<anonymous>",
         "",
         "R",
         "T",
         "Lkotlinx/coroutines/flow/FlowCollector;",
         "it",
         "Lkotlin/ParameterName;",
         "name",
         "value",
         "kotlinx/coroutines/flow/FlowKt__MergeKt$flatMapLatest$1"
   }
)
@SourceDebugExtension(
   "SMAP\nMerge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Merge.kt\nkotlinx/coroutines/flow/FlowKt__MergeKt$flatMapLatest$1\n+ 2 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$1\n*L\n1#1,189:1\n63#2,6:190\n62#2,9:196\n*E\n"
)
public final class ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$flatMapLatest$1 extends SuspendLambda implements Function3 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   Object L$1;
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;

   public ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$flatMapLatest$1(Continuation var1, ArticleSearchViewModel var2) {
      super(3, var1);
      this.this$0 = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      FlowCollector var5;
      String var6;
      Object var10000;
      switch (this.label) {
         case 0: {
            ResultKt.throwOnFailure(var1);
            FlowCollector var3 = (FlowCollector)this.L$0;
            Object itx = this.L$1;
            var5 = var3;
            var6 = (String)itx;
            int var10 = 0;
            HelpCenterApi recoveredSearchApi = ArticleSearchViewModel.access$getHelpCenterApi$p(this.this$0);
            String var10002 = ArticleSearchViewModel.access$isFromSearchBrowse$p(this.this$0) ? "search_browse" : null;
            this.L$0 = var5;
            this.L$1 = var6;
            this.label = 1;
            var10000 = HelpCenterApi$DefaultImpls.searchForArticles$default(recoveredSearchApi, var6, var10002, null, this, 4, null);
            if (var10000 == var2) {
               return var2;
            }
            break;
         }
         case 1: {
            int var7 = 0;
            var6 = (String)this.L$1;
            var5 = (FlowCollector)this.L$0;
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         }
         case 2:
            ResultKt.throwOnFailure(var1);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      String var8 = var6;
      Object var9 = var10000;
      Flow var10001 = FlowKt.flowOf(new Pair(var9, var8));
      Continuation var12 = this;
      this.L$0 = null;
      this.L$1 = null;
      this.label = 2;
      return FlowKt.emitAll(var5, var10001, var12) == var2 ? var2 : Unit.INSTANCE;
   }

   public final Object invoke(FlowCollector var1, Object var2, Continuation var3) {
      ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$flatMapLatest$1 var4 = new ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$flatMapLatest$1(
         var3, this.this$0
      );
      var4.L$0 = var1;
      var4.L$1 = var2;
      return var4.invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      return this.invoke((FlowCollector)var1, var2, (Continuation)var3);
   }
}

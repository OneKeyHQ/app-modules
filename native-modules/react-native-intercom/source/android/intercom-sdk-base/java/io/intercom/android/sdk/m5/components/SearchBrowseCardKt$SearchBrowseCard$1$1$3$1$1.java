package io.intercom.android.sdk.m5.components;

import io.intercom.android.sdk.m5.home.data.SuggestedArticle;
import io.intercom.android.sdk.metrics.MetricTracker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "SearchBrowseCard.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.components.SearchBrowseCardKt$SearchBrowseCard$1$1$3$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nSearchBrowseCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SearchBrowseCard.kt\nio/intercom/android/sdk/m5/components/SearchBrowseCardKt$SearchBrowseCard$1$1$3$1$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,270:1\n1563#2:271\n1634#2,3:272\n*S KotlinDebug\n*F\n+ 1 SearchBrowseCard.kt\nio/intercom/android/sdk/m5/components/SearchBrowseCardKt$SearchBrowseCard$1$1$3$1$1\n*L\n118#1:271\n118#1:272,3\n*E\n"
)
final class SearchBrowseCardKt$SearchBrowseCard$1$1$3$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final MetricTracker $metricTracker;
   // $VF: synthetic field
   final List $articleSuggestions;

   SearchBrowseCardKt$SearchBrowseCard$1$1$3$1$1(MetricTracker var1, List var2, Continuation var3) {
      super(2, var3);
      this.$metricTracker = var1;
      this.$articleSuggestions = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Iterable var2 = this.$articleSuggestions;
            MetricTracker var11 = this.$metricTracker;
            int var3 = 0;
            Iterable var4 = var2;
            ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
            int var6 = 0;

            for (Object var8 : var4) {
               SuggestedArticle var9 = (SuggestedArticle)var8;
               Collection var12 = var5;
               int var10 = 0;
               var12.add(var9.getId());
            }

            var11.viewedArticleSuggestions((List)var5);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SearchBrowseCardKt$SearchBrowseCard$1$1$3$1$1(this.$metricTracker, this.$articleSuggestions, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SearchBrowseCardKt$SearchBrowseCard$1$1$3$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

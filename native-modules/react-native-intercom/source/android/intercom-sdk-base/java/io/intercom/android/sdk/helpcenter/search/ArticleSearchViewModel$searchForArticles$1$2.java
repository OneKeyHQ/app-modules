package io.intercom.android.sdk.helpcenter.search;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ArticleSearchViewModel$searchForArticles$1$2 implements FlowCollector {
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;

   ArticleSearchViewModel$searchForArticles$1$2(ArticleSearchViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(String var1, Continuation var2) {
      if (var1.length() == 0) {
         ArticleSearchViewModel.access$get_state$p(this.this$0).setValue(ArticleSearchState$Initial.INSTANCE);
         return Unit.INSTANCE;
      } else {
         ArticleSearchViewModel.access$get_state$p(this.this$0).setValue(ArticleSearchState$Loading.INSTANCE);
         Object var10000 = ArticleSearchViewModel.access$getSearchInput$p(this.this$0).emit(var1, var2);
         return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((String)var1, var2);
   }
}

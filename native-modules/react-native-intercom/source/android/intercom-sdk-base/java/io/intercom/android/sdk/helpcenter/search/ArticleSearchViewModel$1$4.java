package io.intercom.android.sdk.helpcenter.search;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ArticleSearchViewModel$1$4 implements FlowCollector {
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;

   ArticleSearchViewModel$1$4(ArticleSearchViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(ArticleSearchState var1, Continuation var2) {
      ArticleSearchViewModel.access$get_state$p(this.this$0).setValue(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ArticleSearchState)var1, var2);
   }
}

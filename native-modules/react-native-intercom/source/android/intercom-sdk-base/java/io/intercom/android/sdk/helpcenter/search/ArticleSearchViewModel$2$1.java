package io.intercom.android.sdk.helpcenter.search;

import io.intercom.android.sdk.m5.data.IntercomEvent$NewConversation;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ArticleSearchViewModel$2$1 implements FlowCollector {
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;

   ArticleSearchViewModel$2$1(ArticleSearchViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(IntercomEvent$NewConversation var1, Continuation var2) {
      Object var10000 = ArticleSearchViewModel.access$updateTeammateHelpRow(this.this$0, var2);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((IntercomEvent$NewConversation)var1, var2);
   }
}

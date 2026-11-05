package io.intercom.android.sdk.helpcenter.articles;

import io.intercom.android.sdk.m5.data.IntercomEvent$NewConversation;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ArticleViewModel$1$1 implements FlowCollector {
   // $VF: synthetic field
   final ArticleViewModel this$0;

   ArticleViewModel$1$1(ArticleViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(IntercomEvent$NewConversation var1, Continuation var2) {
      ArticleViewModel.access$updateTeamPresence(this.this$0, var1.getConversation());
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((IntercomEvent$NewConversation)var1, var2);
   }
}

package io.intercom.android.sdk.helpcenter.search;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "ArticleSearchViewModel.kt",
   l = 154,
   i = {},
   s = {},
   n = {},
   m = "updateTeammateHelpRow",
   c = "io.intercom.android.sdk.helpcenter.search.ArticleSearchViewModel"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ArticleSearchViewModel$updateTeammateHelpRow$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   Object L$2;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;
   int label;

   ArticleSearchViewModel$updateTeammateHelpRow$1(ArticleSearchViewModel var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return ArticleSearchViewModel.access$updateTeammateHelpRow(this.this$0, this);
   }
}

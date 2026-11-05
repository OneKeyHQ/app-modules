package io.intercom.android.sdk.helpcenter.articles;

import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi$DefaultImpls;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "ArticleViewModel.kt",
   l = 266,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.helpcenter.articles.ArticleViewModel$sendReactionToServer$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ArticleViewModel$sendReactionToServer$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ArticleViewModel this$0;
   // $VF: synthetic field
   final String $articleId;
   // $VF: synthetic field
   final int $reactionIndex;
   // $VF: synthetic field
   final String $articleContentId;

   ArticleViewModel$sendReactionToServer$1(ArticleViewModel var1, String var2, int var3, String var4, Continuation var5) {
      super(2, var5);
      this.this$0 = var1;
      this.$articleId = var2;
      this.$reactionIndex = var3;
      this.$articleContentId = var4;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            HelpCenterApi var10000 = ArticleViewModel.access$getHelpCenterApi$p(this.this$0);
            String var10001 = this.$articleId;
            int var10002 = this.$reactionIndex;
            String var10003 = this.$articleContentId;
            String var10005 = ArticleViewModel.access$isFromSearchBrowse$p(this.this$0) ? "search_browse" : null;
            Continuation var10007 = this;
            this.label = 1;
            if (HelpCenterApi$DefaultImpls.reactToArticle$default(var10000, var10001, var10002, var10003, false, var10005, null, var10007, 40, null) == var2) {
               return var2;
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
      return new ArticleViewModel$sendReactionToServer$1(this.this$0, this.$articleId, this.$reactionIndex, this.$articleContentId, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ArticleViewModel$sendReactionToServer$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

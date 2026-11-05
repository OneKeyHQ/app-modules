package io.intercom.android.sdk.helpcenter.articles;

import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi$DefaultImpls;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "ArticleViewModel.kt",
   l = 103,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.helpcenter.articles.ArticleViewModel$fragmentLoaded$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ArticleViewModel$fragmentLoaded$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ArticleViewModel this$0;
   // $VF: synthetic field
   final String $articleId;
   // $VF: synthetic field
   final ArticleViewState$Content $defaultState;

   ArticleViewModel$fragmentLoaded$1(ArticleViewModel var1, String var2, ArticleViewState$Content var3, Continuation var4) {
      super(2, var4);
      this.this$0 = var1;
      this.$articleId = var2;
      this.$defaultState = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var8 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            HelpCenterApi var10 = ArticleViewModel.access$getHelpCenterApi$p(this.this$0);
            String var10001 = this.$articleId;
            Continuation var10003 = this;
            this.label = 1;
            var10000 = HelpCenterApi$DefaultImpls.fetchArticle$default(var10, var10001, null, var10003, 2, null);
            if (var10000 == var8) {
               return var8;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var2 = (NetworkResponse)var10000;
      if (!(var2 instanceof NetworkResponse$ClientError) && !(var2 instanceof NetworkResponse$NetworkError) && !(var2 instanceof NetworkResponse$ServerError)) {
         if (!(var2 instanceof NetworkResponse$Success)) {
            throw new NoWhenBranchMatchedException();
         }

         String var11 = ((ArticleResponse)((NetworkResponse$Success)var2).getBody()).getArticle().getRelatedConversationId();
         ArticleViewState$TeamPresenceState var12;
         if (var11 != null) {
            String var6 = var11;
            int var7 = 0;
            var12 = ArticleViewState$TeamPresenceState.copy$default(
               ArticleViewState$TeamPresenceState.Companion.getDefaultTeamPresenceState(),
               null,
               new ArticleViewState$ConversationState(var6, 0, 2, null),
               null,
               0,
               0,
               0,
               null,
               null,
               false,
               null,
               1021,
               null
            );
         } else {
            var12 = null;
         }

         ArticleViewState$TeamPresenceState var3 = var12;
         MutableStateFlow var13 = ArticleViewModel.access$get_state$p(this.this$0);
         ArticleViewState$WebViewStatus var4 = ArticleViewState$WebViewStatus.Idle;
         ArticleViewState$TeamPresenceState var14 = var3;
         if (var3 == null) {
            var14 = ArticleViewState$TeamPresenceState.Companion.getDefaultTeamPresenceState();
         }

         ArticleViewState$TeamPresenceState var5 = var14;
         ArticleMetadata var9 = new ArticleMetadata(
            ((ArticleResponse)((NetworkResponse$Success)var2).getBody()).getArticle().getCard().getArticleId(),
            ((ArticleResponse)((NetworkResponse$Success)var2).getBody()).getArticle().getCard().getTitle()
         );
         var13.setValue(ArticleViewState$Content.copy$default(this.$defaultState, null, var9, var4, null, var5, 9, null));
      } else {
         ArticleViewModel.access$get_state$p(this.this$0)
            .setValue(ArticleViewState$Content.copy$default(this.$defaultState, null, null, ArticleViewState$WebViewStatus.Idle, null, null, 27, null));
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ArticleViewModel$fragmentLoaded$1(this.this$0, this.$articleId, this.$defaultState, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ArticleViewModel$fragmentLoaded$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

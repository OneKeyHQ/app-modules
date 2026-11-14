package io.intercom.android.sdk.helpcenter.articles;

import io.intercom.android.sdk.helpcenter.component.TeammateHelpKt;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "ArticleViewModel.kt",
   l = 196,
   i = {0, 0, 0, 0},
   s = {"L$0", "L$1", "I$0", "I$1"},
   n = {"$this$invokeSuspend_u24lambda_u240", "teamPresenceState", "teamHelpVisibility", "shouldScrollToBottom"},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.helpcenter.articles.ArticleViewModel$sadReactionTapped$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ArticleViewModel$sadReactionTapped$1 extends SuspendLambda implements Function2 {
   Object L$0;
   Object L$1;
   Object L$2;
   int I$0;
   int I$1;
   int label;
   // $VF: synthetic field
   final ArticleViewModel this$0;

   ArticleViewModel$sadReactionTapped$1(ArticleViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var16 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      ArticleViewState$Content var5;
      int var7;
      int var8;
      ArticleViewState$TeamPresenceState var9;
      MutableStateFlow var15;
      Object var10000;
      switch (this.label) {
         case 0: {
            ResultKt.throwOnFailure(var1);
            ArticleViewState var2 = (ArticleViewState)ArticleViewModel.access$get_state$p(this.this$0).getValue();
            if (!(var2 instanceof ArticleViewState$Content)) {
               if (!Intrinsics.areEqual(var2, ArticleViewState$Initial.INSTANCE) && !(var2 instanceof ArticleViewState$Error)) {
                  throw new NoWhenBranchMatchedException();
               }

               return Unit.INSTANCE;
            }

            MutableStateFlow var18 = ArticleViewModel.access$get_state$p(this.this$0);
            ArticleViewState var3 = var2;
            ArticleViewModel var4 = this.this$0;
            var5 = (ArticleViewState$Content)var3;
            var15 = var18;
            int var17 = 0;
            ArticleViewModel.access$getMetricTracker$p(var4)
               .sentArticleReaction(
                  "reaction_sad",
                  Boxing.boxBoolean(ArticleViewModel.access$getAppConfig$p(var4).isInboundMessages()),
                  ArticleViewModel.access$isFromSearchBrowse$p(var4)
               );
            ArticleViewModel.access$sendReactionToServer(
               var4, ArticleViewModel.access$getArticleId$p(var4), ArticleViewModel.access$getArticleContentId$p(var4), 2
            );
            var7 = 8;
            var8 = 0;
            if (ArticleViewModel.access$shouldAddSendMessageRow(var4)) {
               var7 = 0;
               var8 = 1;
            }

            var9 = TeammateHelpKt.computeViewState(
               var5.getArticleMetadata(),
               var5.getTeamPresenceState(),
               ArticleViewModel.access$getAppConfig$p(var4),
               "article",
               ArticleViewModel.access$isFromSearchBrowse$p(var4)
            );
            CommonRepository var19 = ArticleViewModel.access$getCommonRepository$p(var4);
            this.L$0 = var5;
            this.L$1 = var9;
            this.L$2 = var15;
            this.I$0 = var7;
            this.I$1 = var8;
            this.label = 1;
            var10000 = var19.openMessenger(this);
            if (var10000 == var16) {
               return var16;
            }
            break;
         }
         case 1: {
            int var6 = 0;
            var8 = this.I$1;
            var7 = this.I$0;
            var15 = (MutableStateFlow)this.L$2;
            var9 = (ArticleViewState$TeamPresenceState)this.L$1;
            var5 = (ArticleViewState$Content)this.L$0;
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var10 = (NetworkResponse)var10000;
      if (var10 instanceof NetworkResponse$Success && ((OpenMessengerResponse)((NetworkResponse$Success)var10).getBody()).getNewConversationData() != null) {
         var9 = ArticleViewState$TeamPresenceState.copy$default(
            var9,
            null,
            null,
            null,
            0,
            0,
            0,
            null,
            null,
            false,
            ((OpenMessengerResponse)((NetworkResponse$Success)var10).getBody()).getNewConversationData().getCta(),
            511,
            null
         );
      }

      ArticleViewState$ReactionState var11 = var5.getReactionState();
      int var12 = var7;
      int var13 = var8;
      ArticleViewState$Reaction var14 = ArticleViewState$Reaction.Sad;
      var15.setValue(
         ArticleViewState$Content.copy$default(
            var5, null, null, null, ArticleViewState$ReactionState.copy$default(var11, 0, var14, var12, var13 != 0, 1, null), var9, 7, null
         )
      );
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ArticleViewModel$sadReactionTapped$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ArticleViewModel$sadReactionTapped$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

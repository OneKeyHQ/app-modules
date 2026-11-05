package io.intercom.android.sdk.helpcenter.search;

import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.helpcenter.component.TeammateHelpKt;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nEmitters.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$1\n*L\n1#1,49:1\n50#2:50\n77#3,43:51\n*E\n"
)
public final class ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$2$2 implements FlowCollector {
   // $VF: synthetic field
   final FlowCollector $this_unsafeFlow;
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;

   public ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$2$2(FlowCollector var1, ArticleSearchViewModel var2) {
      this.$this_unsafeFlow = var1;
      this.this$0 = var2;
   }

   @Override
   public final Object emit(Object var1, Continuation var2) {
      ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$2$2 recoveredThis = this;
      ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$2$2$1 var3;
      label80: {
         if (var2 instanceof ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$2$2$1) {
            var3 = (ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$2$2$1)var2;
            if ((var3.label & -2147483648) != 0) {
               var3.label -= Integer.MIN_VALUE;
               break label80;
            }
         }

         var3 = new ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$2$2$1(recoveredThis, var2);
      }

      Object var5;
      FlowCollector var9;
      Object var25;
      label70: {
         label83: {
            Object var4 = var3.result;
            var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            ArticleViewState$TeamPresenceState var15;
            switch (var3.label) {
               case 0: {
                  ResultKt.throwOnFailure(var4);
                  Object var6 = var1;
                  FlowCollector var7 = recoveredThis.$this_unsafeFlow;
                  int var20 = 0;
                  var9 = var7;
                  NetworkResponse var10 = (NetworkResponse)var6;
                  int var22 = 0;
                  NetworkResponse var12 = var10;
                  if (var12 instanceof NetworkResponse$ServerError) {
                     ArticleSearchViewModel.access$sendFailedSearchMetric(recoveredThis.this$0, Boxing.boxInt(((NetworkResponse$ServerError)var10).getCode()));
                     var25 = ArticleSearchState$Error.INSTANCE;
                     break label70;
                  }

                  if (var12 instanceof NetworkResponse$ClientError || var12 instanceof NetworkResponse$NetworkError) {
                     ArticleSearchViewModel.sendFailedSearchMetric$default(recoveredThis.this$0, null, 1, null);
                     var25 = ArticleSearchState$Error.INSTANCE;
                     break label70;
                  }

                  if (!(var12 instanceof NetworkResponse$Success)) {
                     throw new NoWhenBranchMatchedException();
                  }

                  List var13 = (List)((NetworkResponse$Success)var10).getBody();
                  if (!var13.isEmpty()) {
                     ArticleSearchViewModel var26 = recoveredThis.this$0;
                     var3.L$0 = var9;
                     var3.label = 1;
                     var25 = ArticleSearchViewModel.access$transformToUiModel(var26, var13, var3);
                     if (var25 == var5) {
                        return var5;
                     }
                     break label83;
                  }

                  if (!ArticleSearchViewModel.access$shouldAddSendMessageRow(recoveredThis.this$0)) {
                     var25 = new ArticleSearchState$NoResultsNoTeamHelp(ArticleSearchViewModel.access$getLastSearchedInput$p(recoveredThis.this$0));
                     break label70;
                  }

                  var15 = TeammateHelpKt.computeViewState(
                     null,
                     ArticleViewState$TeamPresenceState.Companion.getDefaultTeamPresenceState(),
                     ArticleSearchViewModel.access$getAppConfig$p(recoveredThis.this$0),
                     "search_results",
                     ArticleSearchViewModel.access$isFromSearchBrowse$p(recoveredThis.this$0)
                  );
                  CommonRepository var24 = ArticleSearchViewModel.access$getCommonRepository$p(recoveredThis.this$0);
                  var3.L$0 = recoveredThis;
                  var3.L$1 = var9;
                  var3.L$2 = var15;
                  var3.label = 2;
                  var25 = var24.openMessenger(var3);
                  if (var25 == var5) {
                     return var5;
                  }
                  break;
               }
               case 1: {
                  int var19 = 0;
                  int var21 = 0;
                  var9 = (FlowCollector)var3.L$0;
                  ResultKt.throwOnFailure(var4);
                  var25 = var4;
                  break label83;
               }
               case 2: {
                  int var18 = 0;
                  int var11 = 0;
                  var15 = (ArticleViewState$TeamPresenceState)var3.L$2;
                  var9 = (FlowCollector)var3.L$1;
                  recoveredThis = (ArticleSearchViewModel$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$2$2)var3.L$0;
                  ResultKt.throwOnFailure(var4);
                  var25 = var4;
                  break;
               }
               case 3: {
                  int var8 = 0;
                  ResultKt.throwOnFailure(var4);
                  return Unit.INSTANCE;
               }
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            NetworkResponse var16 = (NetworkResponse)var25;
            if (var16 instanceof NetworkResponse$Success) {
               OpenMessengerResponse$NewConversationData var17 = ((OpenMessengerResponse)((NetworkResponse$Success)var16).getBody()).getNewConversationData();
               if (var17 != null) {
                  var15 = ArticleViewState$TeamPresenceState.copy$default(var15, null, null, null, 0, 0, 0, null, null, false, var17.getCta(), 511, null);
               }
            }

            var25 = new ArticleSearchState$NoResults(var15, ArticleSearchViewModel.access$getLastSearchedInput$p(recoveredThis.this$0));
            break label70;
         }

         List var14 = (List)var25;
         var25 = new ArticleSearchState$Content(var14);
      }

      var3.L$0 = null;
      var3.L$1 = null;
      var3.L$2 = null;
      var3.label = 3;
      return var9.emit(var25, var3) == var5 ? var5 : Unit.INSTANCE;
   }
}

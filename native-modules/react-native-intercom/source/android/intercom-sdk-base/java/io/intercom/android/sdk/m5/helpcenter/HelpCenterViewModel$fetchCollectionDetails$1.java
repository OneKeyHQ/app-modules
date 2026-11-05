package io.intercom.android.sdk.m5.helpcenter;

import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi$DefaultImpls;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.helpcenter.sections.HelpCenterCollectionContent;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$FullHelpCenterRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$SendMessageRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Error;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Loading;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
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
   f = "HelpCenterViewModel.kt",
   l = {255, 297},
   i = {1, 1},
   s = {"L$0", "L$1"},
   n = {"collectionDetailsContent", "newState"},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollectionDetails$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class HelpCenterViewModel$fetchCollectionDetails$1 extends SuspendLambda implements Function2 {
   Object L$0;
   Object L$1;
   Object L$2;
   Object L$3;
   int label;
   // $VF: synthetic field
   final HelpCenterViewModel this$0;
   // $VF: synthetic field
   final String $collectionId;

   HelpCenterViewModel$fetchCollectionDetails$1(HelpCenterViewModel var1, String var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$collectionId = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      MutableStateFlow var14;
      CollectionDetailsUiState var17;
      label61: {
         HelpCenterCollectionContent var5;
         List var6;
         label60: {
            MutableStateFlow var7;
            List var8;
            Object var10000;
            label66: {
               Object var11 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               Object var12;
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(var1);
                     CollectionDetailsUiState var2 = (CollectionDetailsUiState)this.this$0.getCollectionDetailsState().getValue();
                     if (var2 instanceof CollectionDetailsUiState$Content
                        && Intrinsics.areEqual(((CollectionDetailsUiState$Content)var2).getId(), this.$collectionId)) {
                        return Unit.INSTANCE;
                     }

                     HelpCenterViewModel.access$get_collectionDetailsState$p(this.this$0).setValue(CollectionDetailsUiState$Loading.INSTANCE);
                     HelpCenterApi var13 = HelpCenterViewModel.access$getHelpCenterApi$p(this.this$0);
                     String var10001 = this.$collectionId;
                     Continuation var10003 = this;
                     this.label = 1;
                     var12 = HelpCenterApi$DefaultImpls.fetchCollectionDetails$default(var13, var10001, null, var10003, 2, null);
                     if (var12 == var11) {
                        return var11;
                     }
                     break;
                  case 1:
                     ResultKt.throwOnFailure(var1);
                     var12 = var1;
                     break;
                  case 2:
                     var8 = (List)this.L$3;
                     var7 = (MutableStateFlow)this.L$2;
                     var6 = (List)this.L$1;
                     var5 = (HelpCenterCollectionContent)this.L$0;
                     ResultKt.throwOnFailure(var1);
                     var10000 = var1;
                     break label66;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               NetworkResponse var3 = (NetworkResponse)var12;
               var14 = HelpCenterViewModel.access$get_collectionDetailsState$p(this.this$0);
               NetworkResponse var4 = var3;
               if (var4 instanceof NetworkResponse$ServerError) {
                  HelpCenterViewModel.access$sendFailedSingleCollectionMetric(this.this$0, Boxing.boxInt(((NetworkResponse$ServerError)var3).getCode()));
                  var17 = ((NetworkResponse$ServerError)var3).getCode() == 404
                     ? new CollectionDetailsUiState$Error(HelpCenterViewModel.access$getNotFoundError$p(this.this$0))
                     : new CollectionDetailsUiState$Error(HelpCenterViewModel.access$getGenericError$p(this.this$0));
                  break label61;
               }

               if (var4 instanceof NetworkResponse$ClientError) {
                  HelpCenterViewModel.sendFailedSingleCollectionMetric$default(this.this$0, null, 1, null);
                  var17 = new CollectionDetailsUiState$Error(HelpCenterViewModel.access$getGenericError$p(this.this$0));
                  break label61;
               }

               if (var4 instanceof NetworkResponse$NetworkError) {
                  HelpCenterViewModel.sendFailedSingleCollectionMetric$default(this.this$0, null, 1, null);
                  var17 = new CollectionDetailsUiState$Error(
                     HelpCenterViewModel.access$errorWithRetry(this.this$0, io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollectionDetails$1.recoveredExpressionFactory_814(((io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollectionDetails$1) this).this$0, ((io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollectionDetails$1) this).$collectionId))
                  );
                  break label61;
               }

               if (!(var4 instanceof NetworkResponse$Success)) {
                  throw new NoWhenBranchMatchedException();
               }

               var5 = (HelpCenterCollectionContent)((NetworkResponse$Success)var3).getBody();
               var6 = CollectionsKt.toMutableList(HelpCenterViewModel.access$transformToUiModel(this.this$0, var5));
               if (HelpCenterViewModel.access$isPartialHelpCenterLoaded$p(this.this$0)) {
                  var6.add(CollectionDetailsRow$FullHelpCenterRow.INSTANCE);
               }

               HelpCenterViewModel.access$getMetricTracker$p(this.this$0)
                  .viewedNativeHelpCenter(
                     "article_list",
                     Boxing.boxBoolean(HelpCenterViewModel.access$isPartialHelpCenterLoaded$p(this.this$0)),
                     HelpCenterViewModel.access$isFromSearchBrowse(this.this$0)
                  );
               if (!HelpCenterViewModel.access$shouldAddSendMessageRow(this.this$0)) {
                  break label60;
               }

               var8 = var6;
               var7 = var14;
               HelpCenterViewModel var15 = this.this$0;
               Continuation var16 = this;
               this.L$0 = var5;
               this.L$1 = var6;
               this.L$2 = var7;
               this.L$3 = var8;
               this.label = 2;
               var10000 = HelpCenterViewModel.access$getCtaData(var15, var16);
               if (var10000 == var11) {
                  return var11;
               }
            }

            Object var9 = var10000;
            var14 = var7;
            ArticleViewState$TeamPresenceState var10 = (ArticleViewState$TeamPresenceState)var9;
            var8.add(new CollectionDetailsRow$SendMessageRow(var10));
         }

         var17 = new CollectionDetailsUiState$Content(
            var5.getCollectionId(), var5.getTitle(), var5.getSummary(), var5.getArticlesCount(), var5.getAuthors(), var6
         );
      }

      var14.setValue(var17);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new HelpCenterViewModel$fetchCollectionDetails$1(this.this$0, this.$collectionId, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HelpCenterViewModel$fetchCollectionDetails$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$0(HelpCenterViewModel var0, String var1) {
      var0.fetchCollectionDetails(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }

   private static kotlin.jvm.functions.Function0 recoveredExpressionFactory_814(io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredExpressionCapture_814_0, java.lang.String recoveredExpressionCapture_814_1) {
      return ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollectionDetails$1.invokeSuspend$lambda$0(recoveredExpressionCapture_814_0, recoveredExpressionCapture_814_1));
   }
}

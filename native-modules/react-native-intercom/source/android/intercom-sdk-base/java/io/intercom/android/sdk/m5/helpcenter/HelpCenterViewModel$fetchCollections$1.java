package io.intercom.android.sdk.m5.helpcenter;

import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi$DefaultImpls;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.helpcenter.collections.HelpCenterCollection;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow$BrowseAllHelpTopicsAsListRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow$FullHelpCenterRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow$SendMessageRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiEffects$NavigateToCollectionContent;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Content;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Error;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Loading;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "HelpCenterViewModel.kt",
   l = {138, 177, 187, 210},
   i = 3,
   s = "L$0",
   n = "uiModel",
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollections$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nHelpCenterViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterViewModel.kt\nio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel$fetchCollections$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,444:1\n774#2:445\n865#2,2:446\n*S KotlinDebug\n*F\n+ 1 HelpCenterViewModel.kt\nio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel$fetchCollections$1\n*L\n163#1:445\n163#1:446,2\n*E\n"
)
final class HelpCenterViewModel$fetchCollections$1 extends SuspendLambda implements Function2 {
   Object L$0;
   Object L$1;
   Object L$2;
   int label;
   // $VF: synthetic field
   final HelpCenterViewModel this$0;
   // $VF: synthetic field
   final Set $collectionIds;

   HelpCenterViewModel$fetchCollections$1(HelpCenterViewModel var1, Set var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$collectionIds = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      MutableStateFlow var31;
      CollectionsUiState var36;
      label126: {
         List var8;
         label125: {
            MutableStateFlow var16;
            List var17;
            Object var10000;
            label140: {
               Object var20 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               Object var29;
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(var1);
                     if (this.this$0.getCollectionsState().getValue() instanceof CollectionsUiState$Content) {
                        return Unit.INSTANCE;
                     }

                     HelpCenterViewModel.access$get_collectionsState$p(this.this$0).setValue(CollectionsUiState$Loading.INSTANCE);
                     HelpCenterApi var30 = HelpCenterViewModel.access$getHelpCenterApi$p(this.this$0);
                     Continuation var10002 = this;
                     this.label = 1;
                     var29 = HelpCenterApi$DefaultImpls.fetchCollections$default(var30, null, var10002, 1, null);
                     if (var29 == var20) {
                        return var20;
                     }
                     break;
                  case 1:
                     ResultKt.throwOnFailure(var1);
                     var29 = var1;
                     break;
                  case 2:
                     var16 = (MutableStateFlow)this.L$0;
                     ResultKt.throwOnFailure(var1);
                     return Unit.INSTANCE;
                  case 3:
                     var16 = (MutableStateFlow)this.L$0;
                     ResultKt.throwOnFailure(var1);
                     return Unit.INSTANCE;
                  case 4:
                     var17 = (List)this.L$2;
                     var16 = (MutableStateFlow)this.L$1;
                     var8 = (List)this.L$0;
                     ResultKt.throwOnFailure(var1);
                     var10000 = var1;
                     break label140;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               NetworkResponse var2 = (NetworkResponse)var29;
               var31 = HelpCenterViewModel.access$get_collectionsState$p(this.this$0);
               NetworkResponse var3 = var2;
               if (var3 instanceof NetworkResponse$ServerError) {
                  HelpCenterViewModel.access$sendFailedCollectionListMetric(this.this$0, Boxing.boxInt(((NetworkResponse$ServerError)var2).getCode()));
                  var36 = ((NetworkResponse$ServerError)var2).getCode() == 404
                     ? new CollectionsUiState$Error(HelpCenterViewModel.access$getNotFoundError$p(this.this$0))
                     : new CollectionsUiState$Error(HelpCenterViewModel.access$getGenericError$p(this.this$0));
                  break label126;
               }

               if (var3 instanceof NetworkResponse$ClientError) {
                  HelpCenterViewModel.sendFailedCollectionListMetric$default(this.this$0, null, 1, null);
                  var36 = new CollectionsUiState$Error(HelpCenterViewModel.access$getGenericError$p(this.this$0));
                  break label126;
               }

               if (var3 instanceof NetworkResponse$NetworkError) {
                  HelpCenterViewModel.sendFailedCollectionListMetric$default(this.this$0, null, 1, null);
                  var36 = new CollectionsUiState$Error(
                     HelpCenterViewModel.access$errorWithRetry(this.this$0, io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollections$1.recoveredExpressionFactory_815(((io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollections$1) this).this$0, ((io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollections$1) this).$collectionIds))
                  );
                  break label126;
               }

               if (!(var3 instanceof NetworkResponse$Success)) {
                  throw new NoWhenBranchMatchedException();
               }

               List var4 = (List)((NetworkResponse$Success)var2).getBody();
               Iterable var6 = var4;
               Set var7 = this.$collectionIds;
               var16 = var31;
               int var23 = 0;
               Iterable var9 = var6;
               ArrayList var10 = new ArrayList();
               int var11 = 0;

               for (Object var13 : var9) {
                  HelpCenterCollection var14 = (HelpCenterCollection)var13;
                  int var15 = 0;
                  if (var7.contains(var14.getId())) {
                     var10.add(var13);
                  }
               }

               List var10001 = (List)var10;
               var31 = var16;
               List var5 = var10001;
               HelpCenterViewModel.access$setPartialHelpCenterLoaded$p(this.this$0, var5.size() < var4.size() && !var5.isEmpty());
               HelpCenterViewModel.access$getMetricTracker$p(this.this$0)
                  .viewedNativeHelpCenter(
                     "collection_list",
                     Boxing.boxBoolean(HelpCenterViewModel.access$isPartialHelpCenterLoaded$p(this.this$0)),
                     HelpCenterViewModel.access$isFromSearchBrowse(this.this$0)
                  );
               if (var5.size() == 1) {
                  var16 = var16;
                  MutableSharedFlow var34 = HelpCenterViewModel.access$get_effect$p(this.this$0);
                  CollectionsUiEffects$NavigateToCollectionContent var38 = new CollectionsUiEffects$NavigateToCollectionContent(
                     ((HelpCenterCollection)CollectionsKt.first(var5)).getId()
                  );
                  Continuation var40 = this;
                  this.L$0 = var16;
                  this.label = 2;
                  if (var34.emit(var38, var40) == var20) {
                     return var20;
                  }

                  return Unit.INSTANCE;
               }

               if (var4.size() == 1) {
                  var16 = var16;
                  MutableSharedFlow var33 = HelpCenterViewModel.access$get_effect$p(this.this$0);
                  CollectionsUiEffects$NavigateToCollectionContent var37 = new CollectionsUiEffects$NavigateToCollectionContent(
                     ((HelpCenterCollection)CollectionsKt.first(var4)).getId()
                  );
                  Continuation var39 = this;
                  this.L$0 = var16;
                  this.label = 3;
                  if (var33.emit(var37, var39) == var20) {
                     return var20;
                  }

                  return Unit.INSTANCE;
               }

               boolean var21 = !var5.isEmpty();
               List var22 = var5.isEmpty() ? var4 : var5;
               if (var22.isEmpty()) {
                  var36 = new CollectionsUiState$Error(HelpCenterViewModel.access$getGenericError$p(this.this$0));
                  break label126;
               }

               var8 = CollectionsKt.toMutableList(HelpCenterViewModel.access$transformToUiModel(this.this$0, var22));
               if (var21) {
                  var8.add(CollectionsRow$BrowseAllHelpTopicsAsListRow.INSTANCE);
               } else if (HelpCenterViewModel.access$isPartialHelpCenterLoaded$p(this.this$0)) {
                  var8.add(CollectionsRow$FullHelpCenterRow.INSTANCE);
               }

               if (!HelpCenterViewModel.access$shouldAddSendMessageRow(this.this$0)) {
                  break label125;
               }

               var17 = var8;
               var16 = var16;
               HelpCenterViewModel var32 = this.this$0;
               Continuation var35 = this;
               this.L$0 = var8;
               this.L$1 = var16;
               this.L$2 = var17;
               this.label = 4;
               var10000 = HelpCenterViewModel.access$getCtaData(var32, var35);
               if (var10000 == var20) {
                  return var20;
               }
            }

            Object var18 = var10000;
            var31 = var16;
            ArticleViewState$TeamPresenceState var19 = (ArticleViewState$TeamPresenceState)var18;
            var17.add(new CollectionsRow$SendMessageRow(var19));
         }

         var36 = new CollectionsUiState$Content(var8);
      }

      var31.setValue(var36);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new HelpCenterViewModel$fetchCollections$1(this.this$0, this.$collectionIds, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HelpCenterViewModel$fetchCollections$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$0(HelpCenterViewModel var0, Set var1) {
      var0.fetchCollections(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }

   private static kotlin.jvm.functions.Function0 recoveredExpressionFactory_815(io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredExpressionCapture_815_0, java.util.Set recoveredExpressionCapture_815_1) {
      return ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$fetchCollections$1.invokeSuspend$lambda$0(recoveredExpressionCapture_815_0, recoveredExpressionCapture_815_1));
   }
}

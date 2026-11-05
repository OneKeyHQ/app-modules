package io.intercom.android.sdk.m5.helpcenter;

import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Content;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DelayKt;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "HelpCenterViewModel.kt",
   l = {323, 326, 331},
   i = {0, 0, 0, 0, 1, 1},
   s = {"L$0", "L$1", "I$0", "I$1", "L$0", "I$0"},
   n = {
         "collectionDetailsContent",
         "collectionsContent",
         "shouldAddToCollectionDetails",
         "shouldAddToCollections",
         "collectionsContent",
         "shouldAddToCollections"
   },
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$onArticleClicked$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class HelpCenterViewModel$onArticleClicked$1 extends SuspendLambda implements Function2 {
   Object L$0;
   Object L$1;
   Object L$2;
   int I$0;
   int I$1;
   int label;
   // $VF: synthetic field
   final HelpCenterViewModel this$0;

   HelpCenterViewModel$onArticleClicked$1(HelpCenterViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      CollectionsUiState$Content var10;
      MutableStateFlow var11;
      Object var10000;
      label87: {
         CollectionsUiState$Content var3;
         int var5;
         Object var13;
         label81: {
            CollectionDetailsUiState$Content var22;
            label88: {
               var13 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               CollectionDetailsUiState$Content var2;
               int var4;
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(var1);
                     HelpCenterViewModel.access$setHasClickedAtLeastOneArticle$p(this.this$0, true);
                     if (!HelpCenterViewModel.access$shouldAddSendMessageRow(this.this$0)) {
                        return Unit.INSTANCE;
                     }

                     Object var14 = HelpCenterViewModel.access$get_collectionDetailsState$p(this.this$0).getValue();
                     var2 = var14 instanceof CollectionDetailsUiState$Content ? (CollectionDetailsUiState$Content)var14 : null;
                     Object var15 = HelpCenterViewModel.access$get_collectionsState$p(this.this$0).getValue();
                     var3 = var15 instanceof CollectionsUiState$Content ? (CollectionsUiState$Content)var15 : null;
                     var4 = (var2 != null && !var2.hasSendMessageRow() ? 1 : 0);
                     var5 = (var3 != null && !var3.hasSendMessageRow() ? 1 : 0);
                     if (var4 == 0 && var5 == 0) {
                        return Unit.INSTANCE;
                     }

                     Continuation var10001 = this;
                     this.L$0 = var2;
                     this.L$1 = var3;
                     this.I$0 = var4;
                     this.I$1 = var5;
                     this.label = 1;
                     if (DelayKt.delay(2000L, var10001) == var13) {
                        return var13;
                     }
                     break;
                  case 1:
                     var5 = this.I$1;
                     var4 = this.I$0;
                     var3 = (CollectionsUiState$Content)this.L$1;
                     var2 = (CollectionDetailsUiState$Content)this.L$0;
                     ResultKt.throwOnFailure(var1);
                     break;
                  case 2:
                     int var19 = 0;
                     var5 = this.I$0;
                     var11 = (MutableStateFlow)this.L$2;
                     var22 = (CollectionDetailsUiState$Content)this.L$1;
                     var3 = (CollectionsUiState$Content)this.L$0;
                     ResultKt.throwOnFailure(var1);
                     var10000 = var1;
                     break label88;
                  case 3:
                     int var9 = 0;
                     var11 = (MutableStateFlow)this.L$1;
                     var10 = (CollectionsUiState$Content)this.L$0;
                     ResultKt.throwOnFailure(var1);
                     var10000 = var1;
                     break label87;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               if (var4 == 0) {
                  break label81;
               }

               if (var2 == null) {
                  break label81;
               }

               CollectionDetailsUiState$Content var6 = var2;
               HelpCenterViewModel var7 = this.this$0;
               CollectionDetailsUiState$Content var8 = var6;
               int var20 = 0;
               var10000 = HelpCenterViewModel.access$get_collectionDetailsState$p(var7);
               var22 = var8;
               var11 = (MutableStateFlow)var10000;
               this.L$0 = var3;
               this.L$1 = var22;
               this.L$2 = var11;
               this.I$0 = var5;
               this.label = 2;
               var10000 = HelpCenterViewModel.access$getCtaData(var7, this);
               if (var10000 == var13) {
                  return var13;
               }
            }

            Object var12 = var10000;
            var11.setValue(var22.copyWithSingleSendMessageRow((ArticleViewState$TeamPresenceState)var12));
         }

         if (var5 == 0) {
            return Unit.INSTANCE;
         }

         if (var3 == null) {
            return Unit.INSTANCE;
         }

         CollectionsUiState$Content var16 = var3;
         HelpCenterViewModel var17 = this.this$0;
         CollectionsUiState$Content var18 = var16;
         int var21 = 0;
         var10000 = HelpCenterViewModel.access$get_collectionsState$p(var17);
         var10 = var18;
         var11 = (MutableStateFlow)var10000;
         this.L$0 = var10;
         this.L$1 = var11;
         this.L$2 = null;
         this.label = 3;
         var10000 = HelpCenterViewModel.access$getCtaData(var17, this);
         if (var10000 == var13) {
            return var13;
         }
      }

      Object var24 = var10000;
      var11.setValue(var10.copyWithSingleSendMessageRow((ArticleViewState$TeamPresenceState)var24));
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new HelpCenterViewModel$onArticleClicked$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HelpCenterViewModel$onArticleClicked$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

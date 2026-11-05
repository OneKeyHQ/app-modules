package io.intercom.android.sdk.helpcenter.search;

import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DelayKt;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "ArticleSearchViewModel.kt",
   l = {185, 187},
   i = 0,
   s = "L$0",
   n = "currentState",
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.helpcenter.search.ArticleSearchViewModel$addTeammateHelpRow$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nArticleSearchViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$addTeammateHelpRow$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,286:1\n2746#2,3:287\n*S KotlinDebug\n*F\n+ 1 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$addTeammateHelpRow$1\n*L\n182#1:287,3\n*E\n"
)
final class ArticleSearchViewModel$addTeammateHelpRow$1 extends SuspendLambda implements Function2 {
   Object L$0;
   Object L$1;
   Object L$2;
   int label;
   // $VF: synthetic field
   final ArticleSearchViewModel this$0;

   ArticleSearchViewModel$addTeammateHelpRow$1(ArticleSearchViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      MutableStateFlow var9;
      ArticleSearchState$Content var10;
      Collection var11;
      Object var10000;
      label48: {
         Object var13 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         ArticleSearchState var2;
         switch (this.label) {
            case 0:
               ResultKt.throwOnFailure(var1);
               ArticleSearchViewModel.access$setHasClickedAtLeastOneArticle$p(this.this$0, true);
               if (!ArticleSearchViewModel.access$shouldAddSendMessageRow(this.this$0)) {
                  return Unit.INSTANCE;
               }

               var2 = (ArticleSearchState)ArticleSearchViewModel.access$get_state$p(this.this$0).getValue();
               if (!(var2 instanceof ArticleSearchState$Content)) {
                  return Unit.INSTANCE;
               }

               var var3 = ((ArticleSearchState$Content)var2).getSearchResults();
               int var4 = 0;
               boolean recoveredNoTeammateRow;
               if (var3 instanceof Collection && ((Collection)var3).isEmpty()) {
                  recoveredNoTeammateRow = true;
               } else {
                  Iterator var5 = var3.iterator();

                  while (true) {
                     if (!var5.hasNext()) {
                        recoveredNoTeammateRow = true;
                        break;
                     }

                     Object var6 = var5.next();
                     ArticleSearchResultRow var7 = (ArticleSearchResultRow)var6;
                     int var8 = 0;
                     if (var7 instanceof ArticleSearchResultRow$TeammateHelpRow) {
                        recoveredNoTeammateRow = false;
                        break;
                     }
                  }
               }

               if (!recoveredNoTeammateRow) {
                  return Unit.INSTANCE;
               }

               Continuation var10001 = this;
               this.L$0 = var2;
               this.label = 1;
               if (DelayKt.delay(2000L, var10001) == var13) {
                  return var13;
               }
               break;
            case 1:
               var2 = (ArticleSearchState)this.L$0;
               ResultKt.throwOnFailure(var1);
               break;
            case 2:
               var11 = (Collection)this.L$2;
               var10 = (ArticleSearchState$Content)this.L$1;
               var9 = (MutableStateFlow)this.L$0;
               ResultKt.throwOnFailure(var1);
               var10000 = var1;
               break label48;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         MutableStateFlow recoveredArticleState = ArticleSearchViewModel.access$get_state$p(this.this$0);
         var11 = ((ArticleSearchState$Content)var2).getSearchResults();
         var10 = (ArticleSearchState$Content)var2;
         var9 = recoveredArticleState;
         ArticleSearchViewModel recoveredTeammateViewModel = this.this$0;
         Continuation var17 = this;
         this.L$0 = var9;
         this.L$1 = var10;
         this.L$2 = var11;
         this.label = 2;
         var10000 = ArticleSearchViewModel.access$teammateHelpRow(recoveredTeammateViewModel, var17);
         if (var10000 == var13) {
            return var13;
         }
      }

      Object var12 = var10000;
      var9.setValue(var10.copy(CollectionsKt.plus(var11, (Object)var12)));
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ArticleSearchViewModel$addTeammateHelpRow$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ArticleSearchViewModel$addTeammateHelpRow$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

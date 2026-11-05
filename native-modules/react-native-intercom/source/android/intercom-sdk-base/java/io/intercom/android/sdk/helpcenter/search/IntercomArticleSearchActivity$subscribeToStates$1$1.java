package io.intercom.android.sdk.helpcenter.search;

import android.content.Intent;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomArticleSearchActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomArticleSearchActivity.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$subscribeToStates$1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,143:1\n1047#2,6:144\n1047#2,6:150\n1047#2,6:156\n*S KotlinDebug\n*F\n+ 1 IntercomArticleSearchActivity.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$subscribeToStates$1$1\n*L\n91#1:144,6\n92#1:150,6\n93#1:156,6\n*E\n"
)
final class IntercomArticleSearchActivity$subscribeToStates$1$1 implements Function2 {
   // $VF: synthetic field
   final IntercomArticleSearchActivity this$0;

   IntercomArticleSearchActivity$subscribeToStates$1$1(IntercomArticleSearchActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C87@3585L16,90@3734L43,91@3815L53,92@3909L567,88@3618L877:IntercomArticleSearchActivity.kt#fpck24");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1191058574,
               var2,
               -1,
               "io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity.subscribeToStates.<anonymous>.<anonymous> (IntercomArticleSearchActivity.kt:87)"
            );
         }

         State var3 = SnapshotStateKt.collectAsState(IntercomArticleSearchActivity.access$getViewModel(this.this$0).getState(), null, var1, 0, 1);
         IntercomArticleSearchActivity var10000 = this.this$0;
         ArticleSearchState var10001 = (ArticleSearchState)var3.getValue();
         var1.startReplaceGroup(903375816);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomArticleSearchActivity.kt#9igjgp");
         Composer var5 = var1;
         boolean var6 = var1.changedInstance(this.this$0);
         IntercomArticleSearchActivity var7 = this.this$0;
         int var8 = 0;
         Object var9 = var5.rememberedValue();
         int var10 = 0;
         Object var10002;
         if (!var6 && var9 != Composer.Companion.getEmpty()) {
            var10002 = (Function0)var9;
         } else {
            ArticleSearchState var14 = var10001;
            IntercomArticleSearchActivity var13 = var10000;
            int var11 = 0;
            final io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity recoveredCapture_113_0 = var7;
            Function0 var15 = ((kotlin.jvm.functions.Function0) () -> IntercomArticleSearchActivity$subscribeToStates$1$1.invoke$lambda$1$lambda$0(recoveredCapture_113_0));
            var10000 = var13;
            var10001 = var14;
            Object var12 = var15;
            var5.updateRememberedValue(var12);
            var10002 = (Function0)var12;
         }

         Function0 var4 = (kotlin.jvm.functions.Function0) var10002;
         var1.endReplaceGroup();
         var10002 = var4;
         var1.startReplaceGroup(903378418);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomArticleSearchActivity.kt#9igjgp");
         var5 = var1;
         var6 = var1.changedInstance(this.this$0);
         var7 = this.this$0;
         var8 = 0;
         var9 = var5.rememberedValue();
         var10 = 0;
         Object var10003;
         if (!var6 && var9 != Composer.Companion.getEmpty()) {
            var10003 = (Function1)var9;
         } else {
            Function0 var40 = var4;
            ArticleSearchState var38 = var10001;
            IntercomArticleSearchActivity var36 = var10000;
            int var32 = 0;
            final io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity recoveredCapture_111_0 = var7;
            Function1 var16 = ((kotlin.jvm.functions.Function1) (recoveredArgument_111_0) -> IntercomArticleSearchActivity$subscribeToStates$1$1.invoke$lambda$3$lambda$2(recoveredCapture_111_0, (kotlinx.coroutines.flow.MutableStateFlow)recoveredArgument_111_0));
            var10000 = var36;
            var10001 = var38;
            var10002 = var40;
            Object var34 = var16;
            var5.updateRememberedValue(var34);
            var10003 = (Function1)var34;
         }

         Function1 var18 = (kotlin.jvm.functions.Function1) var10003;
         var1.endReplaceGroup();
         var10003 = var18;
         var1.startReplaceGroup(903381940);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomArticleSearchActivity.kt#9igjgp");
         var5 = var1;
         var6 = var1.changedInstance(this.this$0);
         var7 = this.this$0;
         var8 = 0;
         var9 = var5.rememberedValue();
         var10 = 0;
         Object var10004;
         if (!var6 && var9 != Composer.Companion.getEmpty()) {
            var10004 = var9;
         } else {
            Function1 var42 = var18;
            Function0 var41 = (kotlin.jvm.functions.Function0) var10002;
            ArticleSearchState var39 = var10001;
            IntercomArticleSearchActivity var37 = var10000;
            int var33 = 0;
            final io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity recoveredCapture_112_0 = var7;
            Function1 var17 = ((kotlin.jvm.functions.Function1) (recoveredArgument_112_0) -> IntercomArticleSearchActivity$subscribeToStates$1$1.invoke$lambda$5$lambda$4(recoveredCapture_112_0, (java.lang.String)recoveredArgument_112_0));
            var10000 = var37;
            var10001 = var39;
            var10002 = var41;
            var10003 = var42;
            Object var35 = var17;
            var5.updateRememberedValue(var35);
            var10004 = var35;
         }

         Function1 var19 = (Function1)var10004;
         var1.endReplaceGroup();
         IntercomArticleSearchActivity.access$SearchScreenContent(var10000, var10001, (kotlin.jvm.functions.Function0) var10002, (kotlin.jvm.functions.Function1) var10003, var19, var1, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(IntercomArticleSearchActivity var0) {
      var0.getOnBackPressedDispatcher().onBackPressed();
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(IntercomArticleSearchActivity var0, MutableStateFlow var1) {
      Intrinsics.checkNotNullParameter(var1, "textFlow");
      IntercomArticleSearchActivity.access$getViewModel(var0).searchForArticles(var1);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4(IntercomArticleSearchActivity var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      IntercomArticleSearchActivity.access$getViewModel(var0).addTeammateHelpRow();
      IntercomArticleSearchActivity.access$getViewModel(var0).sendClickOnSearchResultMetric();
      Intent var2 = IntercomArticleActivity.Companion
         .buildIntent(var0, new IntercomArticleActivity$ArticleActivityArguments(var1, "search_results", false, false, 12, null));
      var0.startActivity(var2);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}

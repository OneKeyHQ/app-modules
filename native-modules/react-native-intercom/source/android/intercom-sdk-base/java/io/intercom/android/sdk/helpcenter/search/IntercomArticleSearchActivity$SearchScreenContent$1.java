package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomArticleSearchActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomArticleSearchActivity.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$SearchScreenContent$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,143:1\n1047#2,6:144\n*S KotlinDebug\n*F\n+ 1 IntercomArticleSearchActivity.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$SearchScreenContent$1\n*L\n122#1:144,6\n*E\n"
)
final class IntercomArticleSearchActivity$SearchScreenContent$1 implements Function2 {
   // $VF: synthetic field
   final Function0 $onBackClick;
   // $VF: synthetic field
   final Function1 $onTextChanged;

   IntercomArticleSearchActivity$SearchScreenContent$1(Function0 var1, Function1 var2) {
      this.$onBackClick = var1;
      this.$onTextChanged = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C121@4988L40,118@4829L218:IntercomArticleSearchActivity.kt#fpck24");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1018273896,
               var2,
               -1,
               "io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity.SearchScreenContent.<anonymous> (IntercomArticleSearchActivity.kt:118)"
            );
         }

         Function0 var10000 = this.$onBackClick;
         Function1 var10001 = this.$onTextChanged;
         var1.startReplaceGroup(-1252036924);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomArticleSearchActivity.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var11 = var10001;
            Function0 var10 = var10000;
            int var9 = 0;
            Function1 var12 = ((kotlin.jvm.functions.Function1) (recoveredArgument_109_0) -> IntercomArticleSearchActivity$SearchScreenContent$1.invoke$lambda$1$lambda$0((java.lang.String)recoveredArgument_109_0));
            var10000 = var10;
            var10001 = var11;
            Object var13 = var12;
            var4.updateRememberedValue(var13);
            var10002 = var13;
         } else {
            var10002 = var7;
         }

         Function1 var3 = (Function1)var10002;
         var1.endReplaceGroup();
         HelpCenterSearchTopBarKt.HelpCenterSearchTopBar(var10000, var10001, var3, var1, 384);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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

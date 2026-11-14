package io.intercom.android.sdk.m5.helpcenter.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterNavGraph$1$1$4\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,259:1\n1047#2,6:260\n1047#2,6:266\n*S KotlinDebug\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterNavGraph$1$1$4\n*L\n215#1:260,6\n227#1:266,6\n*E\n"
)
final class HelpCenterScreenKt$HelpCenterNavGraph$1$1$4 implements Function4 {
   // $VF: synthetic field
   final HelpCenterViewModel $viewModel;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final NavHostController $navController;

   HelpCenterScreenKt$HelpCenterNavGraph$1$1$4(HelpCenterViewModel var1, Context var2, NavHostController var3) {
      this.$viewModel = var1;
      this.$context = var2;
      this.$navController = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C214@7821L580,226@8441L150,211@7667L939:HelpCenterScreen.kt#ln9mxm");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            2018839094, var4, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterNavGraph.<anonymous>.<anonymous>.<anonymous> (HelpCenterScreen.kt:210)"
         );
      }

      String var32;
      label36: {
         Bundle var10000 = var2.getArguments();
         if (var10000 != null) {
            var32 = var10000.getString("id");
            if (var32 != null) {
               break label36;
            }
         }

         var32 = "";
      }

      String var5 = var32;
      HelpCenterViewModel var33 = this.$viewModel;
      String var10001 = var5;
      var3.startReplaceGroup(-2112929578);
      ComposerKt.sourceInformation(var3, "CC(remember):HelpCenterScreen.kt#9igjgp");
      Composer var7 = var3;
      boolean var8 = var3.changedInstance(this.$viewModel) | var3.changedInstance(this.$context);
      HelpCenterViewModel var9 = this.$viewModel;
      Context var10 = this.$context;
      int var11 = 0;
      Object var12 = var7.rememberedValue();
      int var13 = 0;
      Object var10002;
      if (!var8 && var12 != Composer.Companion.getEmpty()) {
         var10002 = var12;
      } else {
         String var17 = var5;
         HelpCenterViewModel var16 = var33;
         int var14 = 0;
         final io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredExpressionCapture_845_0 = var9;
         final android.content.Context recoveredExpressionCapture_845_1 = var10;
         Function1 var18 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_845_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterScreenKt$HelpCenterNavGraph$1$1$4.invoke$lambda$1$lambda$0(recoveredExpressionCapture_845_0, recoveredExpressionCapture_845_1, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_845_0)));
         var33 = var16;
         var10001 = var17;
         Object var15 = var18;
         var7.updateRememberedValue(var15);
         var10002 = var15;
      }

      Function1 var6 = (Function1)var10002;
      var3.endReplaceGroup();
      Function1 var34 = var6;
      var3.startReplaceGroup(-2112910168);
      ComposerKt.sourceInformation(var3, "CC(remember):HelpCenterScreen.kt#9igjgp");
      Composer var21 = var3;
      boolean var22 = var3.changedInstance(this.$navController);
      NavHostController var23 = this.$navController;
      int $i$f$cachex = 0;
      Object it$ivx = var21.rememberedValue();
      int var26/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var10003;
      if (!var22 && it$ivx != Composer.Companion.getEmpty()) {
         var10003 = it$ivx;
      } else {
         Function1 var31 = var6;
         String var30 = var10001;
         HelpCenterViewModel var29 = var33;
         int var27 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_844_0 = var23;
         Function1 var19 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_844_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterScreenKt$HelpCenterNavGraph$1$1$4.invoke$lambda$3$lambda$2(recoveredExpressionCapture_844_0, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_844_0)));
         var33 = var29;
         var10001 = var30;
         var34 = var31;
         Object var28 = var19;
         var21.updateRememberedValue(var28);
         var10003 = var28;
      }

      Function1 var20 = (Function1)var10003;
      var3.endReplaceGroup();
      HelpCenterCollectionDetailsScreenKt.HelpCenterCollectionDetailsScreen(var33, var10001, var34, var20, var3, 0, 0);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(HelpCenterViewModel var0, Context var1, String var2) {
      Intrinsics.checkNotNullParameter(var2, "articleId");
      var0.onArticleClicked();
      Intent var3 = IntercomArticleActivity.Companion.buildIntent(var1, new io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments(var2, "collection_list", false, false, 8, null));
      var1.startActivity(var3);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(NavHostController var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "subCollectionId");
      NavController.navigate$default(var0, "COLLECTION_DETAILS/" + var1, null, null, 6, null);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((AnimatedContentScope)var1, (NavBackStackEntry)var2, (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}

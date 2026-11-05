package io.intercom.android.sdk.m5.helpcenter.ui;

import android.content.Context;
import android.content.Intent;
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
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterNavGraph$1$1$5\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,259:1\n1047#2,6:260\n1047#2,6:266\n*S KotlinDebug\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterNavGraph$1$1$5\n*L\n238#1:260,6\n250#1:266,6\n*E\n"
)
final class HelpCenterScreenKt$HelpCenterNavGraph$1$1$5 implements Function4 {
   // $VF: synthetic field
   final HelpCenterViewModel $viewModel;
   // $VF: synthetic field
   final List $collectionIds;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final NavHostController $navController;

   HelpCenterScreenKt$HelpCenterNavGraph$1$1$5(HelpCenterViewModel var1, List var2, Context var3, NavHostController var4) {
      this.$viewModel = var1;
      this.$collectionIds = var2;
      this.$context = var3;
      this.$navController = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C237@8891L580,249@9511L150,234@8728L948:HelpCenterScreen.kt#ln9mxm");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -157077227, var4, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterNavGraph.<anonymous>.<anonymous>.<anonymous> (HelpCenterScreen.kt:234)"
         );
      }

      HelpCenterViewModel var10000 = this.$viewModel;
      String var10001 = (String)CollectionsKt.first(this.$collectionIds);
      var3.startReplaceGroup(-2112895338);
      ComposerKt.sourceInformation(var3, "CC(remember):HelpCenterScreen.kt#9igjgp");
      Composer var6 = var3;
      boolean var7 = var3.changedInstance(this.$viewModel) | var3.changedInstance(this.$context);
      HelpCenterViewModel var8 = this.$viewModel;
      Context var9 = this.$context;
      int var10 = 0;
      Object var11 = var6.rememberedValue();
      int var12 = 0;
      Object var10002;
      if (!var7 && var11 != Composer.Companion.getEmpty()) {
         var10002 = var11;
      } else {
         String var16 = var10001;
         HelpCenterViewModel var15 = var10000;
         int var13 = 0;
         final io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredExpressionCapture_847_0 = var8;
         final android.content.Context recoveredExpressionCapture_847_1 = var9;
         Function1 var17 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_847_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterScreenKt$HelpCenterNavGraph$1$1$5.invoke$lambda$1$lambda$0(recoveredExpressionCapture_847_0, recoveredExpressionCapture_847_1, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_847_0)));
         var10000 = var15;
         var10001 = var16;
         Object var14 = var17;
         var6.updateRememberedValue(var14);
         var10002 = var14;
      }

      Function1 var5 = (Function1)var10002;
      var3.endReplaceGroup();
      Function1 var31 = var5;
      var3.startReplaceGroup(-2112875928);
      ComposerKt.sourceInformation(var3, "CC(remember):HelpCenterScreen.kt#9igjgp");
      Composer var20 = var3;
      boolean var21 = var3.changedInstance(this.$navController);
      NavHostController var22 = this.$navController;
      int $i$f$cachex = 0;
      Object it$ivx = var20.rememberedValue();
      int var25/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var10003;
      if (!var21 && it$ivx != Composer.Companion.getEmpty()) {
         var10003 = it$ivx;
      } else {
         Function1 var30 = var5;
         String var29 = var10001;
         HelpCenterViewModel var28 = var10000;
         int var26 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_846_0 = var22;
         Function1 var18 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_846_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterScreenKt$HelpCenterNavGraph$1$1$5.invoke$lambda$3$lambda$2(recoveredExpressionCapture_846_0, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_846_0)));
         var10000 = var28;
         var10001 = var29;
         var31 = var30;
         Object var27 = var18;
         var20.updateRememberedValue(var27);
         var10003 = var27;
      }

      Function1 var19 = (Function1)var10003;
      var3.endReplaceGroup();
      HelpCenterCollectionDetailsScreenKt.HelpCenterCollectionDetailsScreen(var10000, var10001, var31, var19, var3, 0, 0);
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

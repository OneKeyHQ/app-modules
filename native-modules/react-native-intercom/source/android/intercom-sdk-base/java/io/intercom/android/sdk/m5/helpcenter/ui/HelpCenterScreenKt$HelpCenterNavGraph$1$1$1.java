package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.NavOptionsBuilder;
import androidx.navigation.PopUpToBuilder;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterNavGraph$1$1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,259:1\n1047#2,6:260\n1047#2,6:266\n*S KotlinDebug\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterNavGraph$1$1$1\n*L\n191#1:260,6\n194#1:266,6\n*E\n"
)
final class HelpCenterScreenKt$HelpCenterNavGraph$1$1$1 implements Function4 {
   // $VF: synthetic field
   final HelpCenterViewModel $viewModel;
   // $VF: synthetic field
   final List $collectionIds;
   // $VF: synthetic field
   final NavHostController $navController;

   HelpCenterScreenKt$HelpCenterNavGraph$1$1$1(HelpCenterViewModel var1, List var2, NavHostController var3) {
      this.$viewModel = var1;
      this.$collectionIds = var2;
      this.$navController = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C190@6672L144,193@6863L285,187@6521L642:HelpCenterScreen.kt#ln9mxm");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -1869047411, var4, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterNavGraph.<anonymous>.<anonymous>.<anonymous> (HelpCenterScreen.kt:187)"
         );
      }

      HelpCenterViewModel var10000 = this.$viewModel;
      List var10001 = this.$collectionIds;
      var3.startReplaceGroup(-2112966782);
      ComposerKt.sourceInformation(var3, "CC(remember):HelpCenterScreen.kt#9igjgp");
      Composer var6 = var3;
      boolean var7 = var3.changedInstance(this.$navController);
      NavHostController var8 = this.$navController;
      int var9 = 0;
      Object var10 = var6.rememberedValue();
      int var11 = 0;
      Object var10002;
      if (!var7 && var10 != Composer.Companion.getEmpty()) {
         var10002 = (Function1)var10;
      } else {
         List var15 = var10001;
         HelpCenterViewModel var14 = var10000;
         int var12 = 0;
         final androidx.navigation.NavHostController recoveredCapture_841_0 = var8;
         Function1 var16 = ((kotlin.jvm.functions.Function1) (recoveredArgument_841_0) -> HelpCenterScreenKt$HelpCenterNavGraph$1$1$1.invoke$lambda$1$lambda$0(recoveredCapture_841_0, (java.lang.String)recoveredArgument_841_0));
         var10000 = var14;
         var10001 = var15;
         Object var13 = var16;
         var6.updateRememberedValue(var13);
         var10002 = (Function1)var13;
      }

      Function1 var5 = (kotlin.jvm.functions.Function1) var10002;
      var3.endReplaceGroup();
      var10002 = var5;
      var3.startReplaceGroup(-2112960529);
      ComposerKt.sourceInformation(var3, "CC(remember):HelpCenterScreen.kt#9igjgp");
      var6 = var3;
      var7 = var3.changedInstance(this.$navController);
      var8 = this.$navController;
      var9 = 0;
      var10 = var6.rememberedValue();
      var11 = 0;
      Object var10003;
      if (!var7 && var10 != Composer.Companion.getEmpty()) {
         var10003 = var10;
      } else {
         Function1 var29 = var5;
         List var28 = var10001;
         HelpCenterViewModel var27 = var10000;
         int var25 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_840_0 = var8;
         Function1 var17 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_840_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterScreenKt$HelpCenterNavGraph$1$1$1.invoke$lambda$5$lambda$4(recoveredExpressionCapture_840_0, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_840_0)));
         var10000 = var27;
         var10001 = var28;
         var10002 = var29;
         Object var26 = var17;
         var6.updateRememberedValue(var26);
         var10003 = var26;
      }

      var5 = (Function1)var10003;
      var3.endReplaceGroup();
      HelpCenterCollectionsScreenKt.HelpCenterCollectionsScreen(var10000, var10001, (kotlin.jvm.functions.Function1) var10002, var5, var3, 0);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(NavHostController var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "collectionId");
      NavController.navigate$default(var0, "COLLECTION_DETAILS/" + var1, null, null, 6, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4$lambda$3$lambda$2(PopUpToBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$popUpTo");
      var0.setInclusive(true);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4$lambda$3(NavOptionsBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navigate");
      var0.popUpTo("COLLECTIONS", ((kotlin.jvm.functions.Function1) (recoveredArgument_843_0) -> HelpCenterScreenKt$HelpCenterNavGraph$1$1$1.invoke$lambda$5$lambda$4$lambda$3$lambda$2((androidx.navigation.PopUpToBuilder)recoveredArgument_843_0)));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4(NavHostController var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "collectionId");
      var0.navigate("COLLECTION_DETAILS/" + var1 + "?startDestination=true", ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_842_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterScreenKt$HelpCenterNavGraph$1$1$1.invoke$lambda$5$lambda$4$lambda$3((androidx.navigation.NavOptionsBuilder) ((androidx.navigation.NavOptionsBuilder)recoveredExpressionArgument_842_0))));
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

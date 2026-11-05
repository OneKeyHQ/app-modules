package io.intercom.android.sdk.m5.helpcenter.ui;

import android.content.Context;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import com.facebook.shimmer.ShimmerFrameLayout;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.survey.ui.components.LoadingComponentKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0017\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0002\u0010\u0004\u001a\r\u0010\u0005\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0006¨\u0006\u0007",
   d2 = {
         "HelpCenterLoadingScreen",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "HomeLoadingContentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHelpCenterLoadingScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterLoadingScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterLoadingScreenKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,59:1\n1047#2,6:60\n*S KotlinDebug\n*F\n+ 1 HelpCenterLoadingScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterLoadingScreenKt\n*L\n24#1:60,6\n*E\n"
)
public final class HelpCenterLoadingScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterLoadingScreen(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(581033983);
      ComposerKt.sourceInformation(var1, "C(HelpCenterLoadingScreen)20@808L6,23@906L333,21@832L414:HelpCenterLoadingScreen.kt#ln9mxm");
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(581033983, var4, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterLoadingScreen (HelpCenterLoadingScreen.kt:19)");
         }

         long var5 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         Modifier var7 = SizeKt.fillMaxSize$default(var0, 0.0F, 1, null);
         var1.startReplaceGroup(1093772747);
         ComposerKt.sourceInformation(var1, "CC(remember):HelpCenterLoadingScreen.kt#9igjgp");
         Composer var9 = var1;
         boolean var10 = var1.changed(var5);
         int var11 = 0;
         Object var12 = var9.rememberedValue();
         int var13 = 0;
         Object var10000;
         if (!var10 && var12 != Composer.Companion.getEmpty()) {
            var10000 = var12;
         } else {
            int var14 = 0;
            final long recoveredCapture_828_0 = var5;
            Object var15 = ((kotlin.jvm.functions.Function1) (recoveredArgument_828_0) -> HelpCenterLoadingScreenKt.HelpCenterLoadingScreen$lambda$2$lambda$1(recoveredCapture_828_0, (android.content.Context)recoveredArgument_828_0));
            var9.updateRememberedValue(var15);
            var10000 = var15;
         }

         Function1 var8 = (Function1)var10000;
         var1.endReplaceGroup();
         AndroidView_androidKt.AndroidView(var8, var7, null, var1, 0, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var17 = var1.endRestartGroup();
      if (var17 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_829_0 = var0;
         final int recoveredCapture_829_1 = var2;
         final int recoveredCapture_829_2 = var3;
         var17.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_829_0, recoveredArgument_829_1) -> HelpCenterLoadingScreenKt.HelpCenterLoadingScreen$lambda$3(recoveredCapture_829_0, recoveredCapture_829_1, recoveredCapture_829_2, (androidx.compose.runtime.Composer)recoveredArgument_829_0, ((java.lang.Integer)recoveredArgument_829_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HomeLoadingContentPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-192893266);
      ComposerKt.sourceInformation(var0, "C(HomeLoadingContentPreview)54@1650L117:HelpCenterLoadingScreen.kt#ln9mxm");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -192893266, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HomeLoadingContentPreview (HelpCenterLoadingScreen.kt:53)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HelpCenterLoadingScreenKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_830_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_830_0, recoveredArgument_830_1) -> HelpCenterLoadingScreenKt.HomeLoadingContentPreview$lambda$4(recoveredCapture_830_0, (androidx.compose.runtime.Composer)recoveredArgument_830_0, ((java.lang.Integer)recoveredArgument_830_1).intValue())));
      }
   }

   private static final ShimmerFrameLayout HelpCenterLoadingScreen$lambda$2$lambda$1(long var0, Context var2) {
      Intrinsics.checkNotNullParameter(var2, "context");
      ShimmerFrameLayout var3 = LoadingComponentKt.buildLoadingContainer$default(var2, false, 2, null);
      ShimmerFrameLayout var4 = var3;
      int var5 = 0;
      var4.addView(LoadingComponentKt.recovered$b7cb79a556d1e984(var2, var0, drawable.intercom_help_center_loading_state));
      return var3;
   }

   private static final Unit HelpCenterLoadingScreen$lambda$3(Modifier var0, int var1, int var2, Composer var3, int var4) {
      HelpCenterLoadingScreen(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit HomeLoadingContentPreview$lambda$4(int var0, Composer var1, int var2) {
      HomeLoadingContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

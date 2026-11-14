package io.intercom.android.sdk.m5.helpcenter.ui;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigator;
import androidx.navigation.compose.NavHostControllerKt;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterContent$1\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,259:1\n75#2:260\n*S KotlinDebug\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterContent$1\n*L\n122#1:260\n*E\n"
)
final class HelpCenterScreenKt$HelpCenterContent$1 implements Function2 {
   // $VF: synthetic field
   final Function0 $onCloseClick;
   // $VF: synthetic field
   final int $navIcon;
   // $VF: synthetic field
   final HelpCenterViewModel $viewModel;
   // $VF: synthetic field
   final List $collectionIds;

   HelpCenterScreenKt$HelpCenterContent$1(Function0 var1, int var2, HelpCenterViewModel var3, List var4) {
      this.$onCloseClick = var1;
      this.$navIcon = var2;
      this.$viewModel = var3;
      this.$collectionIds = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C120@4128L23,121@4187L7,125@4288L6,126@4356L13,127@4393L959,151@5376L626,123@4204L1809:HelpCenterScreen.kt#ln9mxm");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(222051544, var2, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterContent.<anonymous> (HelpCenterScreen.kt:120)");
         }

         NavHostController var3 = NavHostControllerKt.rememberNavController(new Navigator[0], var1, 0);
         CompositionLocal var5 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var6 = 0;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var8 = var1.consume(var5);
         ComposerKt.sourceInformationMarkerEnd(var1);
         Context var4 = (Context)var8;
         ScaffoldKt.recovered$857cf99fc8161460(
            WindowInsetsPaddingKt.windowInsetsPadding(
               BackgroundKt.recovered$42a3174180b3fb7a(
                  Modifier.Companion, IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
               ),
               WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var1, 6)
            ),
            ComposableLambdaKt.rememberComposableLambda(
               375288220, true, new HelpCenterScreenKt$HelpCenterContent$1$1(var3, this.$onCloseClick, var4, this.$navIcon, this.$viewModel), var1, 54
            ),
            null,
            null,
            null,
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               2077063271, true, new HelpCenterScreenKt$HelpCenterContent$1$2(this.$collectionIds, this.$viewModel, var3), var1, 54
            ),
            var1,
            805306416,
            508
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}

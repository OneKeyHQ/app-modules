package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.IntercomErrorScreenKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\n",
   d2 = {
         "HelpCenterErrorScreen",
         "",
         "state",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/components/ErrorState;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "HelpCenterErrorScreenWithCTAPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "HelpCenterErrorScreenWithoutCTAPreview",
         "intercom-sdk-base_release"
   }
)
public final class HelpCenterErrorScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterErrorScreen(@NotNull ErrorState var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "state");
      var2 = var2.startRestartGroup(-1476576134);
      ComposerKt.sourceInformation(var2, "C(HelpCenterErrorScreen)P(1)13@466L55:HelpCenterErrorScreen.kt#ln9mxm");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1476576134, var5, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterErrorScreen (HelpCenterErrorScreen.kt:13)");
         }

         IntercomErrorScreenKt.IntercomErrorScreen(var0, var1, var2, 14 & var5 | 112 & var5, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.components.ErrorState recoveredCapture_824_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_824_1 = var1;
         final int recoveredCapture_824_2 = var3;
         final int recoveredCapture_824_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_824_0, recoveredArgument_824_1) -> HelpCenterErrorScreenKt.HelpCenterErrorScreen$lambda$0(recoveredCapture_824_0, recoveredCapture_824_1, recoveredCapture_824_2, recoveredCapture_824_3, (androidx.compose.runtime.Composer)recoveredArgument_824_0, ((java.lang.Integer)recoveredArgument_824_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterErrorScreenWithCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1313793298);
      ComposerKt.sourceInformation(var0, "C(HelpCenterErrorScreenWithCTAPreview)18@610L82:HelpCenterErrorScreen.kt#ln9mxm");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1313793298, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterErrorScreenWithCTAPreview (HelpCenterErrorScreen.kt:17)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HelpCenterErrorScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_825_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_825_0, recoveredArgument_825_1) -> HelpCenterErrorScreenKt.HelpCenterErrorScreenWithCTAPreview$lambda$1(recoveredCapture_825_0, (androidx.compose.runtime.Composer)recoveredArgument_825_0, ((java.lang.Integer)recoveredArgument_825_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterErrorScreenWithoutCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1385533872);
      ComposerKt.sourceInformation(var0, "C(HelpCenterErrorScreenWithoutCTAPreview)26@786L84:HelpCenterErrorScreen.kt#ln9mxm");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1385533872, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterErrorScreenWithoutCTAPreview (HelpCenterErrorScreen.kt:25)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HelpCenterErrorScreenKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_826_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_826_0, recoveredArgument_826_1) -> HelpCenterErrorScreenKt.HelpCenterErrorScreenWithoutCTAPreview$lambda$2(recoveredCapture_826_0, (androidx.compose.runtime.Composer)recoveredArgument_826_0, ((java.lang.Integer)recoveredArgument_826_1).intValue())));
      }
   }

   private static final Unit HelpCenterErrorScreen$lambda$0(ErrorState var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      HelpCenterErrorScreen(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterErrorScreenWithCTAPreview$lambda$1(int var0, Composer var1, int var2) {
      HelpCenterErrorScreenWithCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterErrorScreenWithoutCTAPreview$lambda$2(int var0, Composer var1, int var2) {
      HelpCenterErrorScreenWithoutCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

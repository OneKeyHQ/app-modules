package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
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
         "IntercomErrorScreen",
         "",
         "state",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/components/ErrorState;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "ErrorScreenWithCTAPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ErrorScreenWithoutCTAPreview",
         "intercom-sdk-base_release"
   }
)
public final class IntercomErrorScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void IntercomErrorScreen(@NotNull ErrorState var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "state");
      var2 = var2.startRestartGroup(-45428996);
      ComposerKt.sourceInformation(var2, "C(IntercomErrorScreen)P(1)30@1090L1227,28@1032L1285:IntercomErrorScreen.kt#arqjag");
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
            ComposerKt.traceEventStart(-45428996, var5, -1, "io.intercom.android.sdk.m5.components.IntercomErrorScreen (IntercomErrorScreen.kt:27)");
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            SizeKt.fillMaxSize$default(var1, 0.0F, 1, null),
            null,
            0L,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(847888321, true, new IntercomErrorScreenKt$IntercomErrorScreen$1(var0), var2, 54),
            var2,
            12582912,
            126
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.components.ErrorState recoveredCapture_221_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_221_1 = var1;
         final int recoveredCapture_221_2 = var3;
         final int recoveredCapture_221_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_221_0, recoveredArgument_221_1) -> IntercomErrorScreenKt.IntercomErrorScreen$lambda$0(recoveredCapture_221_0, recoveredCapture_221_1, recoveredCapture_221_2, recoveredCapture_221_3, (androidx.compose.runtime.Composer)recoveredArgument_221_0, ((java.lang.Integer)recoveredArgument_221_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ErrorScreenWithCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-2005095861);
      ComposerKt.sourceInformation(var0, "C(ErrorScreenWithCTAPreview)67@2398L72:IntercomErrorScreen.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2005095861, var1, -1, "io.intercom.android.sdk.m5.components.ErrorScreenWithCTAPreview (IntercomErrorScreen.kt:66)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomErrorScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_219_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_219_0, recoveredArgument_219_1) -> IntercomErrorScreenKt.ErrorScreenWithCTAPreview$lambda$1(recoveredCapture_219_0, (androidx.compose.runtime.Composer)recoveredArgument_219_0, ((java.lang.Integer)recoveredArgument_219_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ErrorScreenWithoutCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(753957761);
      ComposerKt.sourceInformation(var0, "C(ErrorScreenWithoutCTAPreview)75@2554L74:IntercomErrorScreen.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(753957761, var1, -1, "io.intercom.android.sdk.m5.components.ErrorScreenWithoutCTAPreview (IntercomErrorScreen.kt:74)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomErrorScreenKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_220_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_220_0, recoveredArgument_220_1) -> IntercomErrorScreenKt.ErrorScreenWithoutCTAPreview$lambda$2(recoveredCapture_220_0, (androidx.compose.runtime.Composer)recoveredArgument_220_0, ((java.lang.Integer)recoveredArgument_220_1).intValue())));
      }
   }

   private static final Unit IntercomErrorScreen$lambda$0(ErrorState var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      IntercomErrorScreen(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit ErrorScreenWithCTAPreview$lambda$1(int var0, Composer var1, int var2) {
      ErrorScreenWithCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ErrorScreenWithoutCTAPreview$lambda$2(int var0, Composer var1, int var2) {
      ErrorScreenWithoutCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

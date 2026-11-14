package io.intercom.android.sdk.ui.component;

import androidx.annotation.DrawableRes;
import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.ButtonColors;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001aA\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0007¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e¨\u0006\u0011",
   d2 = {
         "IntercomButton",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "style",
         "Lio/intercom/android/sdk/ui/component/IntercomButton$Style;",
         "text",
         "",
         "trailingIconId",
         "",
         "onClick",
         "Lkotlin/Function0;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/ui/component/IntercomButton$Style;Ljava/lang/String;Ljava/lang/Integer;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "PrimaryButtonPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "PrimaryButtonWithTrailingIconPreview",
         "OutlinedButtonPreview",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/IntercomButtonKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,128:1\n118#2:129\n*S KotlinDebug\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/IntercomButtonKt\n*L\n38#1:129\n*E\n"
)
public final class IntercomButtonKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void IntercomButton(
      @Nullable Modifier var0,
      @NotNull IntercomButton$Style var1,
      @NotNull String var2,
      @DrawableRes @Nullable Integer var3,
      @NotNull Function0 var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "style");
      Intrinsics.checkNotNullParameter(var2, "text");
      Intrinsics.checkNotNullParameter(var4, "onClick");
      var5 = var5.startRestartGroup(1819808567);
      ComposerKt.sourceInformation(var5, "C(IntercomButton)P(!1,2,3,4)32@1162L130,38@1433L6,39@1453L477,29@1058L872:IntercomButton.kt#yr602f");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changed(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var5.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var7 & 8) != 0) {
            var3 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1819808567, var8, -1, "io.intercom.android.sdk.ui.component.IntercomButton (IntercomButton.kt:28)");
         }

         ButtonColors var9 = ButtonDefaults.INSTANCE
            .recovered$e5c6acecc304a3a6(var1.recovered$934f6183e8132565(), var1.recovered$6557fe71dfd7d063(), 0L, 0L, var5, ButtonDefaults.$stable << 12, 12);
         BorderStroke var10 = var1.getBorder();
         int var12 = 12;
         int var13 = 0;
         float var10000 = Dp.recovered$95b3d103663bf060(var12);
         var12 = 10;
         var13 = 0;
         PaddingValues var11 = PaddingKt.recovered$4ebb4826aa57abc6(var10000, Dp.recovered$95b3d103663bf060(var12));
         CornerBasedShape var16 = IntercomTheme.INSTANCE.getShapes(var5, 6).getSmall();
         ButtonKt.OutlinedButton(
            var4,
            var0,
            false,
            var16,
            var9,
            null,
            var10,
            var11,
            null,
            ComposableLambdaKt.rememberComposableLambda(905924137, true, new IntercomButtonKt$IntercomButton$1(var1, var2, var3), var5, 54),
            var5,
            817889280 | 14 & var8 >> 12 | 112 & var8 << 3,
            292
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var18 = var5.endRestartGroup();
      if (var18 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_10_0 = var0;
         final io.intercom.android.sdk.ui.component.IntercomButton$Style recoveredCapture_10_1 = var1;
         final java.lang.String recoveredCapture_10_2 = var2;
         final java.lang.Integer recoveredCapture_10_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_10_4 = var4;
         final int recoveredCapture_10_5 = var6;
         final int recoveredCapture_10_6 = var7;
         var18.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_10_0, recoveredArgument_10_1) -> IntercomButtonKt.IntercomButton$lambda$0(recoveredCapture_10_0, recoveredCapture_10_1, recoveredCapture_10_2, recoveredCapture_10_3, recoveredCapture_10_4, recoveredCapture_10_5, recoveredCapture_10_6, (androidx.compose.runtime.Composer)recoveredArgument_10_0, ((java.lang.Integer)recoveredArgument_10_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PrimaryButtonPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1818981662);
      ComposerKt.sourceInformation(var0, "C(PrimaryButtonPreview)92@3023L138:IntercomButton.kt#yr602f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1818981662, var1, -1, "io.intercom.android.sdk.ui.component.PrimaryButtonPreview (IntercomButton.kt:91)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomButtonKt.INSTANCE.recovered$a54a3cf5c9caf09f(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_12_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_12_0, recoveredArgument_12_1) -> IntercomButtonKt.PrimaryButtonPreview$lambda$1(recoveredCapture_12_0, (androidx.compose.runtime.Composer)recoveredArgument_12_0, ((java.lang.Integer)recoveredArgument_12_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PrimaryButtonWithTrailingIconPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-340414909);
      ComposerKt.sourceInformation(var0, "C(PrimaryButtonWithTrailingIconPreview)103@3252L193:IntercomButton.kt#yr602f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -340414909, var1, -1, "io.intercom.android.sdk.ui.component.PrimaryButtonWithTrailingIconPreview (IntercomButton.kt:102)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomButtonKt.INSTANCE.recovered$16fb19f16412fc3e(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_13_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_13_0, recoveredArgument_13_1) -> IntercomButtonKt.PrimaryButtonWithTrailingIconPreview$lambda$2(recoveredCapture_13_0, (androidx.compose.runtime.Composer)recoveredArgument_13_0, ((java.lang.Integer)recoveredArgument_13_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void OutlinedButtonPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-2065130974);
      ComposerKt.sourceInformation(var0, "C(OutlinedButtonPreview)115@3521L355:IntercomButton.kt#yr602f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2065130974, var1, -1, "io.intercom.android.sdk.ui.component.OutlinedButtonPreview (IntercomButton.kt:114)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomButtonKt.INSTANCE.recovered$822d0078ea447090(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_11_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_11_0, recoveredArgument_11_1) -> IntercomButtonKt.OutlinedButtonPreview$lambda$3(recoveredCapture_11_0, (androidx.compose.runtime.Composer)recoveredArgument_11_0, ((java.lang.Integer)recoveredArgument_11_1).intValue())));
      }
   }

   private static final Unit IntercomButton$lambda$0(
      Modifier var0, IntercomButton$Style var1, String var2, Integer var3, Function0 var4, int var5, int var6, Composer var7, int var8
   ) {
      IntercomButton(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit PrimaryButtonPreview$lambda$1(int var0, Composer var1, int var2) {
      PrimaryButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PrimaryButtonWithTrailingIconPreview$lambda$2(int var0, Composer var1, int var2) {
      PrimaryButtonWithTrailingIconPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit OutlinedButtonPreview$lambda$3(int var0, Composer var1, int var2) {
      OutlinedButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

package io.intercom.android.sdk.ui.component;

import androidx.compose.animation.core.Animatable;
import androidx.compose.animation.core.AnimatableKt;
import androidx.compose.foundation.layout.BoxWithConstraintsKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.tooling.preview.Preview;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a]\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\u001c\u0010\f\u001a\u0018\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\r¢\u0006\u0002\b\u000f¢\u0006\u0002\b\u0010H\u0007¢\u0006\u0004\b\u0011\u0010\u0012\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0014¨\u0006\u0015",
   d2 = {
         "PulsatingBox",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "pulseSize",
         "",
         "pulseColor",
         "Landroidx/compose/ui/graphics/Color;",
         "pulseShape",
         "Landroidx/compose/ui/graphics/Shape;",
         "enabled",
         "",
         "content",
         "Lkotlin/Function1;",
         "Landroidx/compose/foundation/layout/BoxScope;",
         "Lkotlin/ExtensionFunctionType;",
         "Landroidx/compose/runtime/Composable;",
         "PulsatingBox-FU0evQE",
         "(Landroidx/compose/ui/Modifier;FJLandroidx/compose/ui/graphics/Shape;ZLkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;II)V",
         "PulsatingBoxPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPulsatingBox.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PulsatingBox.kt\nio/intercom/android/sdk/ui/component/PulsatingBoxKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,79:1\n1047#2,6:80\n1047#2,6:86\n*S KotlinDebug\n*F\n+ 1 PulsatingBox.kt\nio/intercom/android/sdk/ui/component/PulsatingBoxKt\n*L\n33#1:80,6\n35#1:86,6\n*E\n"
)
public final class PulsatingBoxKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void recovered$a3e4cf41d9d237f7(
      @Nullable Modifier var0, float var1, long var2, @Nullable Shape var4, boolean var5, @NotNull Function3 var6, @Nullable Composer var7, int var8, int var9
   ) {
      Intrinsics.checkNotNullParameter(var6, "content");
      var7 = var7.startRestartGroup(-63498318);
      ComposerKt.sourceInformation(
         var7, "C(PulsatingBox)P(2,5,3:c#ui.graphics.Color,4,1)27@1090L6,32@1266L27,34@1332L144,34@1299L177,44@1582L459,41@1482L559:PulsatingBox.kt#yr602f"
      );
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var7.changed(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var7.changed(var1) ? 32 : 16;
      }

      if ((var8 & 384) == 0) {
         var10 |= (var9 & 4) == 0 && var7.changed(var2) ? 256 : 128;
      }

      if ((var8 & 3072) == 0) {
         var10 |= (var9 & 8) == 0 && var7.changed(var4) ? 2048 : 1024;
      }

      if ((var9 & 16) != 0) {
         var10 |= 24576;
      } else if ((var8 & 24576) == 0) {
         var10 |= var7.changed(var5) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var7.changedInstance(var6) ? 131072 : 65536;
      }

      if ((var10 & 74899) == 74898 && var7.getSkipping()) {
         var7.skipToGroupEnd();
      } else {
         var7.startDefaults();
         if ((var8 & 1) != 0 && !var7.getDefaultsInvalid()) {
            var7.skipToGroupEnd();
            if ((var9 & 4) != 0) {
               var10 &= -897;
            }

            if ((var9 & 8) != 0) {
               var10 &= -7169;
            }
         } else {
            if ((var9 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var9 & 4) != 0) {
               var2 = IntercomTheme.INSTANCE.getColors(var7, 6).recovered$202acdf3dbb5c020();
               var10 &= -897;
            }

            if ((var9 & 8) != 0) {
               var4 = RoundedCornerShapeKt.RoundedCornerShape(50);
               var10 &= -7169;
            }

            if ((var9 & 16) != 0) {
               var5 = true;
            }
         }

         var7.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-63498318, var10, -1, "io.intercom.android.sdk.ui.component.PulsatingBox (PulsatingBox.kt:31)");
         }

         var7.startReplaceGroup(-1108114118);
         ComposerKt.sourceInformation(var7, "CC(remember):PulsatingBox.kt#9igjgp");
         Composer var13 = var7;
         boolean var14 = false;
         int var15 = 0;
         Object var16 = var13.rememberedValue();
         int var17 = 0;
         Object var10000;
         if (var16 == Composer.Companion.getEmpty()) {
            int var18 = 0;
            Object var28 = AnimatableKt.Animatable$default(1.0F, 0.0F, 2, null);
            var13.updateRememberedValue(var28);
            var10000 = var28;
         } else {
            var10000 = var16;
         }

         Animatable var12 = (Animatable)var10000;
         var7.endReplaceGroup();
         Animatable var11 = var12;
         Float var30 = var1;
         var7.startReplaceGroup(-1108111889);
         ComposerKt.sourceInformation(var7, "CC(remember):PulsatingBox.kt#9igjgp");
         var13 = var7;
         var14 = var7.changedInstance(var11) | (var10 & 112) == 32;
         var15 = 0;
         var16 = var13.rememberedValue();
         var17 = 0;
         Object var32;
         if (!var14 && var16 != Composer.Companion.getEmpty()) {
            var32 = (Function2)var16;
         } else {
            Float var20 = var30;
            int var29 = 0;
            var32 = new PulsatingBoxKt$PulsatingBox$1$1(var11, var1, null);
            var30 = var20;
            Object var19 = var32;
            var13.updateRememberedValue(var19);
            var32 = (Function2)var19;
         }

         Function2 var22 = (Function2)var32;
         var7.endReplaceGroup();
         EffectsKt.LaunchedEffect(var30, var22, var7, 14 & var10 >> 3);
         BoxWithConstraintsKt.BoxWithConstraints(
            var0,
            Alignment.Companion.getCenter(),
            false,
            ComposableLambdaKt.rememberComposableLambda(526284572, true, new PulsatingBoxKt$PulsatingBox$2(var5, var11, var4, var2, var6), var7, 54),
            var7,
            3120 | 14 & var10,
            4
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var31 = var7.endRestartGroup();
      if (var31 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_31_0 = var0;
         final float recoveredCapture_31_1 = var1;
         final long recoveredCapture_31_2 = var2;
         final androidx.compose.ui.graphics.Shape recoveredCapture_31_3 = var4;
         final boolean recoveredCapture_31_4 = var5;
         final kotlin.jvm.functions.Function3 recoveredCapture_31_5 = var6;
         final int recoveredCapture_31_6 = var8;
         final int recoveredCapture_31_7 = var9;
         var31.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_31_0, recoveredArgument_31_1) -> PulsatingBoxKt.PulsatingBox_FU0evQE$lambda$2(recoveredCapture_31_0, recoveredCapture_31_1, recoveredCapture_31_2, recoveredCapture_31_3, recoveredCapture_31_4, recoveredCapture_31_5, recoveredCapture_31_6, recoveredCapture_31_7, (androidx.compose.runtime.Composer)recoveredArgument_31_0, ((java.lang.Integer)recoveredArgument_31_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PulsatingBoxPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-2057763747);
      ComposerKt.sourceInformation(var0, "C(PulsatingBoxPreview)65@2106L270:PulsatingBox.kt#yr602f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2057763747, var1, -1, "io.intercom.android.sdk.ui.component.PulsatingBoxPreview (PulsatingBox.kt:64)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$PulsatingBoxKt.INSTANCE.recovered$16fb19f16412fc3e(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_30_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_30_0, recoveredArgument_30_1) -> PulsatingBoxKt.PulsatingBoxPreview$lambda$3(recoveredCapture_30_0, (androidx.compose.runtime.Composer)recoveredArgument_30_0, ((java.lang.Integer)recoveredArgument_30_1).intValue())));
      }
   }

   private static final Unit PulsatingBox_FU0evQE$lambda$2(
      Modifier var0, float var1, long var2, Shape var4, boolean var5, Function3 var6, int var7, int var8, Composer var9, int var10
   ) {
      recovered$a3e4cf41d9d237f7(var0, var1, var2, var4, var5, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit PulsatingBoxPreview$lambda$3(int var0, Composer var1, int var2) {
      PulsatingBoxPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

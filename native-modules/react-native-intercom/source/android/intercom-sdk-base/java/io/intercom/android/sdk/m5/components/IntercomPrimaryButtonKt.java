package io.intercom.android.sdk.m5.components;

import androidx.annotation.DrawableRes;
import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.ButtonColors;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a9\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0003\u0010\u0006\u001a\u0004\u0018\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0001¢\u0006\u0002\u0010\n\u001a9\u0010\u000b\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0003\u0010\u0006\u001a\u0004\u0018\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003¢\u0006\u0002\u0010\n\u001aM\u0010\u000b\u001a\u00020\u00012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\r2\u001c\u0010\u000e\u001a\u0018\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010\u000f¢\u0006\u0002\b\u0011¢\u0006\u0002\b\u0012H\u0001¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015¨\u0006\u0017",
   d2 = {
         "IntercomPrimaryButton",
         "",
         "text",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "trailingIconId",
         "",
         "onClick",
         "Lkotlin/Function0;",
         "(Ljava/lang/String;Landroidx/compose/ui/Modifier;Ljava/lang/Integer;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "LegacyIntercomPrimaryButton",
         "enabled",
         "",
         "content",
         "Lkotlin/Function1;",
         "Landroidx/compose/foundation/layout/RowScope;",
         "Lkotlin/ExtensionFunctionType;",
         "Landroidx/compose/runtime/Composable;",
         "(Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;ZLkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;II)V",
         "PrimaryButtonPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "PrimaryButtonWithTrailingIconPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomPrimaryButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPrimaryButton.kt\nio/intercom/android/sdk/m5/components/IntercomPrimaryButtonKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,131:1\n118#2:132\n118#2:133\n118#2:134\n*S KotlinDebug\n*F\n+ 1 IntercomPrimaryButton.kt\nio/intercom/android/sdk/m5/components/IntercomPrimaryButtonKt\n*L\n73#1:132\n75#1:133\n88#1:134\n*E\n"
)
public final class IntercomPrimaryButtonKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void IntercomPrimaryButton(
      @NotNull String var0, @Nullable Modifier var1, @DrawableRes @Nullable Integer var2, @NotNull Function0 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "text");
      Intrinsics.checkNotNullParameter(var3, "onClick");
      var4 = var4.startRestartGroup(-801577387);
      ComposerKt.sourceInformation(var4, "C(IntercomPrimaryButton)P(2!1,3)35@1374L68:IntercomPrimaryButton.kt#arqjag");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var6 & 4) != 0) {
            var2 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-801577387, var7, -1, "io.intercom.android.sdk.m5.components.IntercomPrimaryButton (IntercomPrimaryButton.kt:35)");
         }

         LegacyIntercomPrimaryButton(var0, var1, var2, var3, var4, 14 & var7 | 112 & var7 | 896 & var7 | 7168 & var7, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final java.lang.String recoveredCapture_224_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_224_1 = var1;
         final java.lang.Integer recoveredCapture_224_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_224_3 = var3;
         final int recoveredCapture_224_4 = var5;
         final int recoveredCapture_224_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_224_0, recoveredArgument_224_1) -> IntercomPrimaryButtonKt.IntercomPrimaryButton$lambda$0(recoveredCapture_224_0, recoveredCapture_224_1, recoveredCapture_224_2, recoveredCapture_224_3, recoveredCapture_224_4, recoveredCapture_224_5, (androidx.compose.runtime.Composer)recoveredArgument_224_0, ((java.lang.Integer)recoveredArgument_224_1).intValue())));
      }
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated component", replaceWith = @ReplaceWith(expression = "IntercomPrimaryButton() from ui module", imports = {}))
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void LegacyIntercomPrimaryButton(
      String var0, Modifier var1, @DrawableRes Integer var2, Function0 var3, Composer var4, int var5, int var6
   ) {
      var4 = var4.startRestartGroup(398234558);
      ComposerKt.sourceInformation(var4, "C(LegacyIntercomPrimaryButton)P(2!1,3)47@1825L470,45@1763L532:IntercomPrimaryButton.kt#arqjag");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1155) == 1154 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var6 & 4) != 0) {
            var2 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(398234558, var7, -1, "io.intercom.android.sdk.m5.components.LegacyIntercomPrimaryButton (IntercomPrimaryButton.kt:44)");
         }

         LegacyIntercomPrimaryButton(
            var3,
            null,
            false,
            ComposableLambdaKt.rememberComposableLambda(2099872974, true, new IntercomPrimaryButtonKt$LegacyIntercomPrimaryButton$1(var0, var2), var4, 54),
            var4,
            3072 | 14 & var7 >> 9,
            6
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final java.lang.String recoveredCapture_225_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_225_1 = var1;
         final java.lang.Integer recoveredCapture_225_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_225_3 = var3;
         final int recoveredCapture_225_4 = var5;
         final int recoveredCapture_225_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_225_0, recoveredArgument_225_1) -> IntercomPrimaryButtonKt.LegacyIntercomPrimaryButton$lambda$1(recoveredCapture_225_0, recoveredCapture_225_1, recoveredCapture_225_2, recoveredCapture_225_3, recoveredCapture_225_4, recoveredCapture_225_5, (androidx.compose.runtime.Composer)recoveredArgument_225_0, ((java.lang.Integer)recoveredArgument_225_1).intValue())));
      }
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void LegacyIntercomPrimaryButton(
      @NotNull Function0 var0, @Nullable Modifier var1, boolean var2, @NotNull Function3 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "onClick");
      Intrinsics.checkNotNullParameter(var3, "content");
      var4 = var4.startRestartGroup(-1285013288);
      ComposerKt.sourceInformation(
         var4,
         "C(LegacyIntercomPrimaryButton)P(3,2,1)71@2527L6,79@2794L6,82@2930L6,83@3019L6,84@3102L6,85@3158L6,81@2865L319,76@2700L623:IntercomPrimaryButton.kt#arqjag"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var6 & 4) != 0) {
            var2 = true;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1285013288, var7, -1, "io.intercom.android.sdk.m5.components.LegacyIntercomPrimaryButton (IntercomPrimaryButton.kt:70)");
         }

         BorderStroke var10000;
         if (ColorExtensionsKt.recovered$4886b56a29c1c487(IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).recovered$202acdf3dbb5c020())) {
            int var9 = 1;
            int var10 = 0;
            var10000 = BorderStrokeKt.recovered$91879a1bef2fb536(
               Dp.recovered$95b3d103663bf060(var9),
               Color.recovered$41aa0ee03c2282fb(Color.Companion.recovered$235b30c090a399c5(), 0.2F, 0.0F, 0.0F, 0.0F, 14, null)
            );
         } else {
            int var21 = 1;
            int var23 = 0;
            var10000 = BorderStrokeKt.recovered$91879a1bef2fb536(
               Dp.recovered$95b3d103663bf060(var21),
               Color.recovered$41aa0ee03c2282fb(Color.Companion.recovered$95989221dad41c4d(), 0.2F, 0.0F, 0.0F, 0.0F, 14, null)
            );
         }

         BorderStroke var8 = var10000;
         CornerBasedShape var22 = IntercomTheme.INSTANCE.getShapes(var4, IntercomTheme.$stable).getSmall();
         ButtonDefaults var11 = ButtonDefaults.INSTANCE;
         long var12 = Color.recovered$41aa0ee03c2282fb(
            IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.2F, 0.0F, 0.0F, 0.0F, 14, null
         );
         long var14 = Color.recovered$41aa0ee03c2282fb(
            IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.4F, 0.0F, 0.0F, 0.0F, 14, null
         );
         long var16 = IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).recovered$202acdf3dbb5c020();
         long var18 = IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).recovered$8a5d9ad2993bccce();
         ButtonColors var24 = var11.recovered$6a8c28fb0b42a2fc(var16, var18, var12, var14, var4, ButtonDefaults.$stable << 12, 0);
         int var26 = 16;
         int var13 = 0;
         float var29 = Dp.recovered$95b3d103663bf060(var26);
         int $this$dp$ivx = 12;
         var13 = 0;
         PaddingValues var25 = PaddingKt.recovered$4ebb4826aa57abc6(var29, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         ButtonKt.Button(
            var0, var1, var2, var22, var24, null, var8, var25, null, var3, var4, 12582912 | 14 & var7 | 112 & var7 | 896 & var7 | 1879048192 & var7 << 18, 288
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var30 = var4.endRestartGroup();
      if (var30 != null) {
         final kotlin.jvm.functions.Function0 recoveredCapture_226_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_226_1 = var1;
         final boolean recoveredCapture_226_2 = var2;
         final kotlin.jvm.functions.Function3 recoveredCapture_226_3 = var3;
         final int recoveredCapture_226_4 = var5;
         final int recoveredCapture_226_5 = var6;
         var30.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_226_0, recoveredArgument_226_1) -> IntercomPrimaryButtonKt.LegacyIntercomPrimaryButton$lambda$2(recoveredCapture_226_0, recoveredCapture_226_1, recoveredCapture_226_2, recoveredCapture_226_3, recoveredCapture_226_4, recoveredCapture_226_5, (androidx.compose.runtime.Composer)recoveredArgument_226_0, ((java.lang.Integer)recoveredArgument_226_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PrimaryButtonPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1925294537);
      ComposerKt.sourceInformation(var0, "C(PrimaryButtonPreview)115@3915L71:IntercomPrimaryButton.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1925294537, var1, -1, "io.intercom.android.sdk.m5.components.PrimaryButtonPreview (IntercomPrimaryButton.kt:114)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomPrimaryButtonKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_227_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_227_0, recoveredArgument_227_1) -> IntercomPrimaryButtonKt.PrimaryButtonPreview$lambda$3(recoveredCapture_227_0, (androidx.compose.runtime.Composer)recoveredArgument_227_0, ((java.lang.Integer)recoveredArgument_227_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PrimaryButtonWithTrailingIconPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1297682962);
      ComposerKt.sourceInformation(var0, "C(PrimaryButtonWithTrailingIconPreview)123@4077L162:IntercomPrimaryButton.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1297682962, var1, -1, "io.intercom.android.sdk.m5.components.PrimaryButtonWithTrailingIconPreview (IntercomPrimaryButton.kt:122)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomPrimaryButtonKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_228_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_228_0, recoveredArgument_228_1) -> IntercomPrimaryButtonKt.PrimaryButtonWithTrailingIconPreview$lambda$4(recoveredCapture_228_0, (androidx.compose.runtime.Composer)recoveredArgument_228_0, ((java.lang.Integer)recoveredArgument_228_1).intValue())));
      }
   }

   private static final Unit IntercomPrimaryButton$lambda$0(
      String var0, Modifier var1, Integer var2, Function0 var3, int var4, int var5, Composer var6, int var7
   ) {
      IntercomPrimaryButton(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit LegacyIntercomPrimaryButton$lambda$1(
      String var0, Modifier var1, Integer var2, Function0 var3, int var4, int var5, Composer var6, int var7
   ) {
      LegacyIntercomPrimaryButton(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit LegacyIntercomPrimaryButton$lambda$2(
      Function0 var0, Modifier var1, boolean var2, Function3 var3, int var4, int var5, Composer var6, int var7
   ) {
      LegacyIntercomPrimaryButton(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit PrimaryButtonPreview$lambda$3(int var0, Composer var1, int var2) {
      PrimaryButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PrimaryButtonWithTrailingIconPreview$lambda$4(int var0, Composer var1, int var2) {
      PrimaryButtonWithTrailingIconPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

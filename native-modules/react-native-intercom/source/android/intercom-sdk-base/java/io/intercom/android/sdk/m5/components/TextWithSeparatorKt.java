package io.intercom.android.sdk.m5.components;

import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextOverflow;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u001ag\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0001¢\u0006\u0004\b\u0012\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0017\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015¨\u0006\u0018",
   d2 = {
         "TextWithSeparator",
         "",
         "firstText",
         "",
         "secondText",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "separator",
         "style",
         "Landroidx/compose/ui/text/TextStyle;",
         "color",
         "Landroidx/compose/ui/graphics/Color;",
         "overflow",
         "Landroidx/compose/ui/text/style/TextOverflow;",
         "maxLines",
         "",
         "textAlign",
         "Landroidx/compose/ui/text/style/TextAlign;",
         "TextWithSeparator-wV1YYcM",
         "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/Modifier;Ljava/lang/String;Landroidx/compose/ui/text/TextStyle;JIILandroidx/compose/ui/text/style/TextAlign;Landroidx/compose/runtime/Composer;II)V",
         "TextWithSeparatorPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TextWithSeparatorSecondEmptyPreview",
         "TextWithSeparatorFirstEmptyPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTextWithSeparator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextWithSeparator.kt\nio/intercom/android/sdk/m5/components/TextWithSeparatorKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,65:1\n1047#2,6:66\n*S KotlinDebug\n*F\n+ 1 TextWithSeparator.kt\nio/intercom/android/sdk/m5/components/TextWithSeparatorKt\n*L\n32#1:66,6\n*E\n"
)
public final class TextWithSeparatorKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$741c28fc37ac0ca8(
      @NotNull String var0,
      @NotNull String var1,
      @Nullable Modifier var2,
      @Nullable String var3,
      @Nullable TextStyle var4,
      long var5,
      int var7,
      int var8,
      @Nullable TextAlign var9,
      @Nullable Composer var10,
      int var11,
      int var12
   ) {
      Intrinsics.checkNotNullParameter(var0, "firstText");
      Intrinsics.checkNotNullParameter(var1, "secondText");
      var10 = var10.startRestartGroup(46249171);
      ComposerKt.sourceInformation(
         var10,
         "C(TextWithSeparator)P(1,5,3,6,7,0:c#ui.graphics.Color,4:c#ui.text.style.TextOverflow!,8:c#ui.text.style.TextAlign)20@754L10,31@1171L69,28@1048L311:TextWithSeparator.kt#arqjag"
      );
      int var13 = var11;
      if ((var12 & 1) != 0) {
         var13 |= 6;
      } else if ((var11 & 6) == 0) {
         var13 |= var10.changed(var0) ? 4 : 2;
      }

      if ((var12 & 2) != 0) {
         var13 |= 48;
      } else if ((var11 & 48) == 0) {
         var13 |= var10.changed(var1) ? 32 : 16;
      }

      if ((var12 & 4) != 0) {
         var13 |= 384;
      } else if ((var11 & 384) == 0) {
         var13 |= var10.changed(var2) ? 256 : 128;
      }

      if ((var12 & 8) != 0) {
         var13 |= 3072;
      } else if ((var11 & 3072) == 0) {
         var13 |= var10.changed(var3) ? 2048 : 1024;
      }

      if ((var11 & 24576) == 0) {
         var13 |= (var12 & 16) == 0 && var10.changed(var4) ? 16384 : 8192;
      }

      if ((var12 & 32) != 0) {
         var13 |= 196608;
      } else if ((var11 & 196608) == 0) {
         var13 |= var10.changed(var5) ? 131072 : 65536;
      }

      if ((var12 & 64) != 0) {
         var13 |= 1572864;
      } else if ((var11 & 1572864) == 0) {
         var13 |= var10.changed(var7) ? 1048576 : 524288;
      }

      if ((var12 & 128) != 0) {
         var13 |= 12582912;
      } else if ((var11 & 12582912) == 0) {
         var13 |= var10.changed(var8) ? 8388608 : 4194304;
      }

      if ((var12 & 256) != 0) {
         var13 |= 100663296;
      } else if ((var11 & 100663296) == 0) {
         var13 |= var10.changed(var9) ? 67108864 : 33554432;
      }

      if ((var13 & 38347923) == 38347922 && var10.getSkipping()) {
         var10.skipToGroupEnd();
      } else {
         var10.startDefaults();
         if ((var11 & 1) != 0 && !var10.getDefaultsInvalid()) {
            var10.skipToGroupEnd();
            if ((var12 & 16) != 0) {
               var13 &= -57345;
            }
         } else {
            if ((var12 & 4) != 0) {
               var2 = Modifier.Companion;
            }

            if ((var12 & 8) != 0) {
               var3 = "•";
            }

            if ((var12 & 16) != 0) {
               var4 = IntercomTheme.INSTANCE.getTypography(var10, IntercomTheme.$stable).getType04();
               var13 &= -57345;
            }

            if ((var12 & 32) != 0) {
               var5 = Color.Companion.recovered$37fe95da52e29a80();
            }

            if ((var12 & 64) != 0) {
               var7 = TextOverflow.Companion.recovered$96c38bb84ea02354();
            }

            if ((var12 & 128) != 0) {
               var8 = Integer.MAX_VALUE;
            }

            if ((var12 & 256) != 0) {
               var9 = null;
            }
         }

         var10.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(46249171, var13, -1, "io.intercom.android.sdk.m5.components.TextWithSeparator (TextWithSeparator.kt:25)");
         }

         String var14 = !StringsKt.isBlank(var0) && !StringsKt.isBlank(var1) ? ' ' + var3 + ' ' : "";
         String var15 = var0 + var14 + var1;
         Modifier var10000 = var2;
         boolean var10001 = false;
         var10.startReplaceGroup(598373073);
         ComposerKt.sourceInformation(var10, "CC(remember):TextWithSeparator.kt#9igjgp");
         Composer var18 = var10;
         boolean var19 = (var13 & 14) == 4 | (var13 & 112) == 32;
         int var20 = 0;
         Object var21 = var18.rememberedValue();
         int var22 = 0;
         Object var10002;
         if (!var19 && var21 != Composer.Companion.getEmpty()) {
            var10002 = var21;
         } else {
            boolean var26 = false;
            Modifier var25 = var2;
            int var23 = 0;
            final java.lang.String recoveredCapture_247_0 = var0;
            final java.lang.String recoveredCapture_247_1 = var1;
            Function1 var27 = ((kotlin.jvm.functions.Function1) (recoveredArgument_247_0) -> TextWithSeparatorKt.TextWithSeparator_wV1YYcM$lambda$1$lambda$0(recoveredCapture_247_0, recoveredCapture_247_1, (androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredArgument_247_0));
            var10000 = var25;
            var10001 = var26;
            Object var24 = var27;
            var18.updateRememberedValue(var24);
            var10002 = var24;
         }

         Function1 var17 = (Function1)var10002;
         var10.endReplaceGroup();
         Modifier var16 = SemanticsModifierKt.semantics$default(var10000, var10001, var17, 1, null);
         TextKt.recovered$5b114ab32a4f4190(
            var15,
            var16,
            var5,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            var9,
            0L,
            var7,
            false,
            var8,
            0,
            null,
            var4,
            var10,
            896 & var13 >> 9,
            14 & var13 >> 24 | 896 & var13 >> 12 | 57344 & var13 >> 9 | 29360128 & var13 << 9,
            109560
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var29 = var10.endRestartGroup();
      if (var29 != null) {
         final java.lang.String recoveredCapture_248_0 = var0;
         final java.lang.String recoveredCapture_248_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_248_2 = var2;
         final java.lang.String recoveredCapture_248_3 = var3;
         final androidx.compose.ui.text.TextStyle recoveredCapture_248_4 = var4;
         final long recoveredCapture_248_5 = var5;
         final int recoveredCapture_248_6 = var7;
         final int recoveredCapture_248_7 = var8;
         final androidx.compose.ui.text.style.TextAlign recoveredCapture_248_8 = var9;
         final int recoveredCapture_248_9 = var11;
         final int recoveredCapture_248_10 = var12;
         var29.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_248_0, recoveredArgument_248_1) -> TextWithSeparatorKt.TextWithSeparator_wV1YYcM$lambda$2(recoveredCapture_248_0, recoveredCapture_248_1, recoveredCapture_248_2, recoveredCapture_248_3, recoveredCapture_248_4, recoveredCapture_248_5, recoveredCapture_248_6, recoveredCapture_248_7, recoveredCapture_248_8, recoveredCapture_248_9, recoveredCapture_248_10, (androidx.compose.runtime.Composer)recoveredArgument_248_0, ((java.lang.Integer)recoveredArgument_248_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TextWithSeparatorPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1364601736);
      ComposerKt.sourceInformation(var0, "C(TextWithSeparatorPreview)44@1438L93:TextWithSeparator.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1364601736, var1, -1, "io.intercom.android.sdk.m5.components.TextWithSeparatorPreview (TextWithSeparator.kt:43)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TextWithSeparatorKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_245_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_245_0, recoveredArgument_245_1) -> TextWithSeparatorKt.TextWithSeparatorPreview$lambda$3(recoveredCapture_245_0, (androidx.compose.runtime.Composer)recoveredArgument_245_0, ((java.lang.Integer)recoveredArgument_245_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TextWithSeparatorSecondEmptyPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1899699287);
      ComposerKt.sourceInformation(var0, "C(TextWithSeparatorSecondEmptyPreview)52@1621L89:TextWithSeparator.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1899699287, var1, -1, "io.intercom.android.sdk.m5.components.TextWithSeparatorSecondEmptyPreview (TextWithSeparator.kt:51)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TextWithSeparatorKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_246_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_246_0, recoveredArgument_246_1) -> TextWithSeparatorKt.TextWithSeparatorSecondEmptyPreview$lambda$4(recoveredCapture_246_0, (androidx.compose.runtime.Composer)recoveredArgument_246_0, ((java.lang.Integer)recoveredArgument_246_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TextWithSeparatorFirstEmptyPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1116705163);
      ComposerKt.sourceInformation(var0, "C(TextWithSeparatorFirstEmptyPreview)60@1799L84:TextWithSeparator.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1116705163, var1, -1, "io.intercom.android.sdk.m5.components.TextWithSeparatorFirstEmptyPreview (TextWithSeparator.kt:59)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TextWithSeparatorKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_244_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_244_0, recoveredArgument_244_1) -> TextWithSeparatorKt.TextWithSeparatorFirstEmptyPreview$lambda$5(recoveredCapture_244_0, (androidx.compose.runtime.Composer)recoveredArgument_244_0, ((java.lang.Integer)recoveredArgument_244_1).intValue())));
      }
   }

   private static final Unit TextWithSeparator_wV1YYcM$lambda$1$lambda$0(String var0, String var1, SemanticsPropertyReceiver var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var2, var0 + ' ' + var1);
      return Unit.INSTANCE;
   }

   private static final Unit TextWithSeparator_wV1YYcM$lambda$2(
      String var0,
      String var1,
      Modifier var2,
      String var3,
      TextStyle var4,
      long var5,
      int var7,
      int var8,
      TextAlign var9,
      int var10,
      int var11,
      Composer var12,
      int var13
   ) {
      recovered$741c28fc37ac0ca8(var0, var1, var2, var3, var4, var5, var7, var8, var9, var12, RecomposeScopeImplKt.updateChangedFlags(var10 | 1), var11);
      return Unit.INSTANCE;
   }

   private static final Unit TextWithSeparatorPreview$lambda$3(int var0, Composer var1, int var2) {
      TextWithSeparatorPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextWithSeparatorSecondEmptyPreview$lambda$4(int var0, Composer var1, int var2) {
      TextWithSeparatorSecondEmptyPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextWithSeparatorFirstEmptyPreview$lambda$5(int var0, Composer var1, int var2) {
      TextWithSeparatorFirstEmptyPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

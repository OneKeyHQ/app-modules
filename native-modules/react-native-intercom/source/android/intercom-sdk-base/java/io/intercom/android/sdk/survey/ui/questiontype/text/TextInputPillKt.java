package io.intercom.android.sdk.survey.ui.questiontype.text;

import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.relocation.BringIntoViewRequester;
import androidx.compose.foundation.relocation.BringIntoViewRequesterKt;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.foundation.text.KeyboardActionScope;
import androidx.compose.foundation.text.KeyboardActions;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material3.TextFieldColors;
import androidx.compose.material3.TextFieldDefaults;
import androidx.compose.material3.TextFieldKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.focus.FocusEventModifierKt;
import androidx.compose.ui.focus.FocusState;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000V\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\u001aÃ\u0001\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\u0019\b\u0002\u0010\u000e\u001a\u0013\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u0006¢\u0006\u0002\b\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\r2\u0015\b\u0002\u0010\u0016\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0017¢\u0006\u0002\b\u00182\b\b\u0002\u0010\u0019\u001a\u00020\r2\b\b\u0002\u0010\u001a\u001a\u00020\u001bH\u0001¢\u0006\u0004\b\u001c\u0010\u001d\u001a\r\u0010\u001e\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f\u001a\r\u0010 \u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f\u001a\r\u0010!\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f\u001a\r\u0010\"\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f\u001a\r\u0010#\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f¨\u0006$",
   d2 = {
         "TextInputPill",
         "",
         "text",
         "",
         "placeholderText",
         "onTextChanged",
         "Lkotlin/Function1;",
         "strokeColor",
         "Landroidx/compose/ui/graphics/Color;",
         "cursorColor",
         "maxLines",
         "",
         "singleLine",
         "",
         "onImeActionNext",
         "Landroidx/compose/foundation/text/KeyboardActionScope;",
         "Lkotlin/ExtensionFunctionType;",
         "keyboardType",
         "Landroidx/compose/ui/text/input/KeyboardType;",
         "imeAction",
         "Landroidx/compose/ui/text/input/ImeAction;",
         "showTrailingIcon",
         "leadingIcon",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "enabled",
         "minHeight",
         "Landroidx/compose/ui/unit/Dp;",
         "TextInputPill-g5ZjG94",
         "(Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/graphics/Color;JIZLkotlin/jvm/functions/Function1;IIZLkotlin/jvm/functions/Function2;ZFLandroidx/compose/runtime/Composer;III)V",
         "TextInputPillPlaceholder",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TextInputPillUnchecked",
         "TextInputPillValidated",
         "TextInputPillError",
         "TextInputPillDisabled",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTextInputPill.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextInputPill.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/TextInputPillKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 4 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,191:1\n1047#2,6:192\n1047#2,6:198\n1047#2,3:211\n1050#2,3:215\n1047#2,6:219\n615#3:204\n612#3,6:205\n613#4:214\n118#5:218\n*S KotlinDebug\n*F\n+ 1 TextInputPill.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/TextInputPillKt\n*L\n46#1:192,6\n56#1:198,6\n57#1:211,3\n57#1:215,3\n70#1:219,6\n57#1:204\n57#1:205,6\n57#1:214\n64#1:218\n*E\n"
)
public final class TextInputPillKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void recovered$1e363f8e6e037ae9(
      @Nullable String var0,
      @Nullable String var1,
      @NotNull Function1 var2,
      @Nullable Color var3,
      long var4,
      int var6,
      boolean var7,
      @Nullable Function1 var8,
      int var9,
      int var10,
      boolean var11,
      @Nullable Function2 var12,
      boolean var13,
      float var14,
      @Nullable Composer var15,
      int var16,
      int var17,
      int var18
   ) {
      Intrinsics.checkNotNullParameter(var2, "onTextChanged");
      Composer var45 = var15.startRestartGroup(701760197);
      ComposerKt.sourceInformation(
         var45,
         "C(TextInputPill)P(13,9,8,12:c#ui.graphics.Color,0:c#ui.graphics.Color,5,11,7,3:c#ui.text.input.KeyboardType,2:c#ui.text.input.ImeAction,10,4!,6:c#ui.unit.Dp)45@1964L2,55@2434L37,56@2497L24,65@2801L6,67@2861L6,69@2986L198,78@3287L8,82@3503L6,83@3571L6,84@3648L6,85@3713L6,86@3776L6,109@4723L6,110@4771L10,89@3884L93,96@4228L428,57@2526L2269:TextInputPill.kt#z2rnlt"
      );
      int var19 = var16;
      int var20 = var17;
      if ((var18 & 1) != 0) {
         var19 |= 6;
      } else if ((var16 & 6) == 0) {
         var19 |= var45.changed(var0) ? 4 : 2;
      }

      if ((var18 & 2) != 0) {
         var19 |= 48;
      } else if ((var16 & 48) == 0) {
         var19 |= var45.changed(var1) ? 32 : 16;
      }

      if ((var18 & 4) != 0) {
         var19 |= 384;
      } else if ((var16 & 384) == 0) {
         var19 |= var45.changedInstance(var2) ? 256 : 128;
      }

      if ((var18 & 8) != 0) {
         var19 |= 3072;
      } else if ((var16 & 3072) == 0) {
         var19 |= var45.changed(var3) ? 2048 : 1024;
      }

      if ((var18 & 16) != 0) {
         var19 |= 24576;
      } else if ((var16 & 24576) == 0) {
         var19 |= var45.changed(var4) ? 16384 : 8192;
      }

      if ((var18 & 32) != 0) {
         var19 |= 196608;
      } else if ((var16 & 196608) == 0) {
         var19 |= var45.changed(var6) ? 131072 : 65536;
      }

      if ((var18 & 64) != 0) {
         var19 |= 1572864;
      } else if ((var16 & 1572864) == 0) {
         var19 |= var45.changed(var7) ? 1048576 : 524288;
      }

      if ((var18 & 128) != 0) {
         var19 |= 12582912;
      } else if ((var16 & 12582912) == 0) {
         var19 |= var45.changedInstance(var8) ? 8388608 : 4194304;
      }

      if ((var18 & 256) != 0) {
         var19 |= 100663296;
      } else if ((var16 & 100663296) == 0) {
         var19 |= var45.changed(var9) ? 67108864 : 33554432;
      }

      if ((var18 & 512) != 0) {
         var19 |= 805306368;
      } else if ((var16 & 805306368) == 0) {
         var19 |= var45.changed(var10) ? 536870912 : 268435456;
      }

      if ((var18 & 1024) != 0) {
         var20 |= 6;
      } else if ((var17 & 6) == 0) {
         var20 |= var45.changed(var11) ? 4 : 2;
      }

      if ((var18 & 2048) != 0) {
         var20 |= 48;
      } else if ((var17 & 48) == 0) {
         var20 |= var45.changedInstance(var12) ? 32 : 16;
      }

      if ((var18 & 4096) != 0) {
         var20 |= 384;
      } else if ((var17 & 384) == 0) {
         var20 |= var45.changed(var13) ? 256 : 128;
      }

      if ((var18 & 8192) != 0) {
         var20 |= 3072;
      } else if ((var17 & 3072) == 0) {
         var20 |= var45.changed(var14) ? 2048 : 1024;
      }

      if ((var19 & 306783379) == 306783378 && (var20 & 1171) == 1170 && var45.getSkipping()) {
         var45.skipToGroupEnd();
      } else {
         if ((var18 & 1) != 0) {
            var0 = "";
         }

         if ((var18 & 2) != 0) {
            var1 = "";
         }

         if ((var18 & 8) != 0) {
            var3 = null;
         }

         if ((var18 & 32) != 0) {
            var6 = Integer.MAX_VALUE;
         }

         if ((var18 & 64) != 0) {
            var7 = false;
         }

         if ((var18 & 128) != 0) {
            var45.startReplaceGroup(-652118101);
            ComposerKt.sourceInformation(var45, "CC(remember):TextInputPill.kt#9igjgp");
            Composer var22 = var45;
            boolean var23 = false;
            int var24 = 0;
            Object var25 = var22.rememberedValue();
            int var26 = 0;
            Object var10000;
            if (var25 == Composer.Companion.getEmpty()) {
               int var27 = 0;
               Object var59 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1515_0) -> io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillKt.TextInputPill_g5ZjG94$lambda$1$lambda$0((androidx.compose.foundation.text.KeyboardActionScope) ((androidx.compose.foundation.text.KeyboardActionScope)recoveredExpressionArgument_1515_0)));
               var22.updateRememberedValue(var59);
               var10000 = var59;
            } else {
               var10000 = var25;
            }

            Function1 var21 = (Function1)var10000;
            var45.endReplaceGroup();
            var8 = var21;
         }

         if ((var18 & 256) != 0) {
            var9 = KeyboardType.Companion.recovered$29477a01a04c8fe0();
         }

         if ((var18 & 512) != 0) {
            var10 = ImeAction.Companion.recovered$fdcb0d48f452258d();
         }

         if ((var18 & 1024) != 0) {
            var11 = false;
         }

         if ((var18 & 2048) != 0) {
            var12 = null;
         }

         if ((var18 & 4096) != 0) {
            var13 = true;
         }

         if ((var18 & 8192) != 0) {
            var14 = Dp.Companion.recovered$70ffd418071648ea();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(701760197, var19, var20, "io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPill (TextInputPill.kt:52)");
         }

         Color var47 = var3;
         var45.startReplaceGroup(-652108692);
         ComposerKt.sourceInformation(var45, "53@2285L6");
         long var49 = var47 == null
            ? Color.recovered$41aa0ee03c2282fb(
               IntercomTheme.INSTANCE.getColors(var45, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null
            )
            : var47.recovered$45f9de7b85e0e0b5();
         var45.endReplaceGroup();
         long var46 = var49;
         var45.startReplaceGroup(-652103026);
         ComposerKt.sourceInformation(var45, "CC(remember):TextInputPill.kt#9igjgp");
         Composer var52 = var45;
         boolean var54 = false;
         int var60 = 0;
         Object var28 = var52.rememberedValue();
         int var29 = 0;
         Object var85;
         if (var28 == Composer.Companion.getEmpty()) {
            int var30 = 0;
            Object var73 = BringIntoViewRequesterKt.BringIntoViewRequester();
            var52.updateRememberedValue(var73);
            var85 = var73;
         } else {
            var85 = var28;
         }

         BringIntoViewRequester var50 = (BringIntoViewRequester)var85;
         var45.endReplaceGroup();
         BringIntoViewRequester var48 = var50;
         boolean var55 = false;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(var45, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var66 = var45;
         ComposerKt.sourceInformationMarkerStart(var45, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var70 = var45;
         boolean var74 = false;
         int $i$f$cachex = 0;
         Object var32 = var70.rememberedValue();
         int var33 = 0;
         Object var86;
         if (var32 == Composer.Companion.getEmpty()) {
            int var34 = 0;
            int var35 = 0;
            Object var83 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var66);
            var70.updateRememberedValue(var83);
            var86 = var83;
         } else {
            var86 = var32;
         }

         CoroutineScope var36 = (CoroutineScope)var86;
         ComposerKt.sourceInformationMarkerEnd(var45);
         CoroutineScope var37 = var36;
         ComposerKt.sourceInformationMarkerEnd(var45);
         CoroutineScope var51 = var37;
         Modifier var87 = SizeKt.recovered$da9aba2b92020277(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), 0.0F, var14, 1, null);
         int var56 = 1;
         int var62 = 0;
         Modifier var88 = BringIntoViewRequesterKt.bringIntoViewRequester(
            ClipKt.clip(
               BorderKt.recovered$1227359600bc50fd(
                  var87, Dp.recovered$95b3d103663bf060(var56), var46, IntercomTheme.INSTANCE.getShapes(var45, IntercomTheme.$stable).getSmall()
               ),
               IntercomTheme.INSTANCE.getShapes(var45, IntercomTheme.$stable).getSmall()
            ),
            var48
         );
         var45.startReplaceGroup(-652085201);
         ComposerKt.sourceInformation(var45, "CC(remember):TextInputPill.kt#9igjgp");
         Composer var63 = var45;
         boolean var67 = var45.changedInstance(var51) | var45.changedInstance(var48);
         int $i$f$cachexx = 0;
         Object it$ivx = var63.rememberedValue();
         int var78/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var90;
         if (!var67 && it$ivx != Composer.Companion.getEmpty()) {
            var90 = it$ivx;
         } else {
            Modifier var44 = var88;
            int var79 = 0;
            final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_1516_0 = var51;
            final androidx.compose.foundation.relocation.BringIntoViewRequester recoveredExpressionCapture_1516_1 = var48;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1516_0) -> io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillKt.TextInputPill_g5ZjG94$lambda$4$lambda$3(recoveredExpressionCapture_1516_0, recoveredExpressionCapture_1516_1, (androidx.compose.ui.focus.FocusState) ((androidx.compose.ui.focus.FocusState)recoveredExpressionArgument_1516_0)));
            var88 = var44;
            Object var81 = var10001;
            var63.updateRememberedValue(var81);
            var90 = var81;
         }

         Function1 var57 = (Function1)var90;
         var45.endReplaceGroup();
         Modifier var53 = FocusEventModifierKt.onFocusEvent(var88, var57);
         TextFieldColors var64 = TextFieldDefaults.INSTANCE.colors(var45, 6);
         long var68 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var76 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var80 = IntercomTheme.INSTANCE.getColors(var45, IntercomTheme.$stable).recovered$ad18c207ea721de5();
         long var82 = IntercomTheme.INSTANCE.getColors(var45, IntercomTheme.$stable).recovered$f6e7a825ec69c94c();
         long var84 = IntercomTheme.INSTANCE.getColors(var45, IntercomTheme.$stable).recovered$ad18c207ea721de5();
         long var38 = IntercomTheme.INSTANCE.getColors(var45, IntercomTheme.$stable).recovered$ad18c207ea721de5();
         long var40 = IntercomTheme.INSTANCE.getColors(var45, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         long var42 = Color.Companion.recovered$562f21ab17d9b4c7();
         TextFieldColors var58 = TextFieldColors.recovered$4bbf31c5aa9a006c(
            var64,
            0L,
            0L,
            var40,
            0L,
            var80,
            var84,
            var82,
            var38,
            var4,
            0L,
            null,
            var68,
            var76,
            var42,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            -14837,
            2047,
            null
         );
         KeyboardOptions var65 = new KeyboardOptions(0, null, var9, var10, null, null, null, 115, null);
         KeyboardActions var69 = new KeyboardActions(null, null, var8, null, null, null, 59, null);
         CornerBasedShape var72 = IntercomTheme.INSTANCE.getShapes(var45, IntercomTheme.$stable).getSmall();
         TextStyle var77 = IntercomTheme.INSTANCE.getTypography(var45, IntercomTheme.$stable).getType04();
         TextFieldKt.TextField(
            var0,
            var2,
            var53,
            var13,
            false,
            var77,
            null,
            ComposableLambdaKt.rememberComposableLambda(-712506390, true, new TextInputPillKt$TextInputPill$3(var1), var45, 54),
            var12,
            ComposableLambdaKt.rememberComposableLambda(583948524, true, new TextInputPillKt$TextInputPill$4(var11), var45, 54),
            null,
            null,
            null,
            false,
            null,
            var65,
            var69,
            var7,
            var6,
            0,
            null,
            var72,
            var58,
            var45,
            817889280 | 14 & var19 | 112 & var19 >> 3 | 7168 & var20 << 3 | 234881024 & var20 << 21,
            29360128 & var19 << 3 | 234881024 & var19 << 9,
            0,
            1604688
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var89 = var45.endRestartGroup();
      if (var89 != null) {
         final java.lang.String recoveredExpressionCapture_1514_0 = var0;
         final java.lang.String recoveredExpressionCapture_1514_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1514_2 = var2;
         final androidx.compose.ui.graphics.Color recoveredExpressionCapture_1514_3 = var3;
         final long recoveredExpressionCapture_1514_4 = var4;
         final int recoveredExpressionCapture_1514_5 = var6;
         final boolean recoveredExpressionCapture_1514_6 = var7;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1514_7 = var8;
         final int recoveredExpressionCapture_1514_8 = var9;
         final int recoveredExpressionCapture_1514_9 = var10;
         final boolean recoveredExpressionCapture_1514_10 = var11;
         final kotlin.jvm.functions.Function2 recoveredExpressionCapture_1514_11 = var12;
         final boolean recoveredExpressionCapture_1514_12 = var13;
         final float recoveredExpressionCapture_1514_13 = var14;
         final int recoveredExpressionCapture_1514_14 = var16;
         final int recoveredExpressionCapture_1514_15 = var17;
         final int recoveredExpressionCapture_1514_16 = var18;
         var89.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1514_0, recoveredExpressionArgument_1514_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillKt.TextInputPill_g5ZjG94$lambda$5(recoveredExpressionCapture_1514_0, recoveredExpressionCapture_1514_1, recoveredExpressionCapture_1514_2, recoveredExpressionCapture_1514_3, recoveredExpressionCapture_1514_4, recoveredExpressionCapture_1514_5, recoveredExpressionCapture_1514_6, recoveredExpressionCapture_1514_7, recoveredExpressionCapture_1514_8, recoveredExpressionCapture_1514_9, recoveredExpressionCapture_1514_10, recoveredExpressionCapture_1514_11, recoveredExpressionCapture_1514_12, recoveredExpressionCapture_1514_13, recoveredExpressionCapture_1514_14, recoveredExpressionCapture_1514_15, recoveredExpressionCapture_1514_16, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1514_0), ((java.lang.Integer)recoveredExpressionArgument_1514_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextInputPillPlaceholder(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1437391364);
      ComposerKt.sourceInformation(var2, "C(TextInputPillPlaceholder)117@4889L291:TextInputPill.kt#z2rnlt");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1437391364, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillPlaceholder (TextInputPill.kt:116)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$TextInputPillKt.INSTANCE.recovered$eac478aa65045e5d(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1511_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1511_0, recoveredExpressionArgument_1511_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillKt.TextInputPillPlaceholder$lambda$6(recoveredExpressionCapture_1511_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1511_0), ((java.lang.Integer)recoveredExpressionArgument_1511_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextInputPillUnchecked(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-869352961);
      ComposerKt.sourceInformation(var2, "C(TextInputPillUnchecked)131@5272L318:TextInputPill.kt#z2rnlt");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -869352961, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillUnchecked (TextInputPill.kt:130)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$TextInputPillKt.INSTANCE.recovered$1400af5e34386102(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1512_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1512_0, recoveredExpressionArgument_1512_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillKt.TextInputPillUnchecked$lambda$7(recoveredExpressionCapture_1512_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1512_0), ((java.lang.Integer)recoveredExpressionArgument_1512_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextInputPillValidated(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(559292703);
      ComposerKt.sourceInformation(var2, "C(TextInputPillValidated)146@5682L317:TextInputPill.kt#z2rnlt");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(559292703, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillValidated (TextInputPill.kt:145)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$TextInputPillKt.INSTANCE.recovered$fd838d44303763da(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1513_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1513_0, recoveredExpressionArgument_1513_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillKt.TextInputPillValidated$lambda$8(recoveredExpressionCapture_1513_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1513_0), ((java.lang.Integer)recoveredExpressionArgument_1513_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextInputPillError(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-175520743);
      ComposerKt.sourceInformation(var2, "C(TextInputPillError)161@6087L410:TextInputPill.kt#z2rnlt");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-175520743, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillError (TextInputPill.kt:160)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$TextInputPillKt.INSTANCE.recovered$54bdcf11cafa5012(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1510_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1510_0, recoveredExpressionArgument_1510_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillKt.TextInputPillError$lambda$9(recoveredExpressionCapture_1510_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1510_0), ((java.lang.Integer)recoveredExpressionArgument_1510_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextInputPillDisabled(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1100722979);
      ComposerKt.sourceInformation(var2, "C(TextInputPillDisabled)178@6588L351:TextInputPill.kt#z2rnlt");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1100722979, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillDisabled (TextInputPill.kt:177)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$TextInputPillKt.INSTANCE.recovered$265b6f46b79d310a(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1509_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1509_0, recoveredExpressionArgument_1509_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillKt.TextInputPillDisabled$lambda$10(recoveredExpressionCapture_1509_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1509_0), ((java.lang.Integer)recoveredExpressionArgument_1509_1).intValue())));
      }
   }

   private static final Unit TextInputPill_g5ZjG94$lambda$1$lambda$0(KeyboardActionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return Unit.INSTANCE;
   }

   private static final Unit TextInputPill_g5ZjG94$lambda$4$lambda$3(CoroutineScope var0, BringIntoViewRequester var1, FocusState var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      if (var2.isFocused()) {
         BuildersKt.launch$default(var0, null, null, new TextInputPillKt$TextInputPill$2$1$1(var1, null), 3, null);
      }

      return Unit.INSTANCE;
   }

   private static final Unit TextInputPill_g5ZjG94$lambda$5(
      String var0,
      String var1,
      Function1 var2,
      Color var3,
      long var4,
      int var6,
      boolean var7,
      Function1 var8,
      int var9,
      int var10,
      boolean var11,
      Function2 var12,
      boolean var13,
      float var14,
      int var15,
      int var16,
      int var17,
      Composer var18,
      int var19
   ) {
      recovered$1e363f8e6e037ae9(
         var0,
         var1,
         var2,
         var3,
         var4,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var13,
         var14,
         var18,
         RecomposeScopeImplKt.updateChangedFlags(var15 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var16),
         var17
      );
      return Unit.INSTANCE;
   }

   private static final Unit TextInputPillPlaceholder$lambda$6(int var0, Composer var1, int var2) {
      TextInputPillPlaceholder(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextInputPillUnchecked$lambda$7(int var0, Composer var1, int var2) {
      TextInputPillUnchecked(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextInputPillValidated$lambda$8(int var0, Composer var1, int var2) {
      TextInputPillValidated(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextInputPillError$lambda$9(int var0, Composer var1, int var2) {
      TextInputPillError(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextInputPillDisabled$lambda$10(int var0, Composer var1, int var2) {
      TextInputPillDisabled(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

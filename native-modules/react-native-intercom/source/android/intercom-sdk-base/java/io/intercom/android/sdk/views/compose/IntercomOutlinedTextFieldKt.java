package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.interaction.InteractionSourceKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.text.BasicTextFieldKt;
import androidx.compose.foundation.text.KeyboardActions;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material3.OutlinedTextFieldDefaults;
import androidx.compose.material3.TextFieldColors;
import androidx.compose.material3.TextFieldDefaults;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.SolidColor;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.input.VisualTransformation;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000r\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u009b\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\f2\u0015\b\u0002\u0010\r\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000e¢\u0006\u0002\b\u000f2\u0015\b\u0002\u0010\u0010\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000e¢\u0006\u0002\b\u000f2\u0015\b\u0002\u0010\u0011\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000e¢\u0006\u0002\b\u000f2\u0015\b\u0002\u0010\u0012\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000e¢\u0006\u0002\b\u000f2\b\b\u0002\u0010\u0013\u001a\u00020\t2\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001a\u001a\u00020\t2\b\b\u0002\u0010\u001b\u001a\u00020\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u001c2\b\b\u0002\u0010\u001e\u001a\u00020\u001f2\b\b\u0002\u0010 \u001a\u00020!2\b\b\u0002\u0010\"\u001a\u00020#2\b\b\u0002\u0010$\u001a\u00020%H\u0001¢\u0006\u0002\u0010&\"\u0010\u0010'\u001a\u00020(X\u0082\u0004¢\u0006\u0004\n\u0002\u0010)¨\u0006*",
   d2 = {
         "IntercomOutlinedTextField",
         "",
         "value",
         "",
         "onValueChange",
         "Lkotlin/Function1;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "enabled",
         "",
         "readOnly",
         "textStyle",
         "Landroidx/compose/ui/text/TextStyle;",
         "label",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "placeholder",
         "leadingIcon",
         "trailingIcon",
         "isError",
         "visualTransformation",
         "Landroidx/compose/ui/text/input/VisualTransformation;",
         "keyboardOptions",
         "Landroidx/compose/foundation/text/KeyboardOptions;",
         "keyboardActions",
         "Landroidx/compose/foundation/text/KeyboardActions;",
         "singleLine",
         "maxLines",
         "",
         "minLines",
         "interactionSource",
         "Landroidx/compose/foundation/interaction/MutableInteractionSource;",
         "shape",
         "Landroidx/compose/ui/graphics/Shape;",
         "colors",
         "Landroidx/compose/material3/TextFieldColors;",
         "contentPadding",
         "Landroidx/compose/foundation/layout/PaddingValues;",
         "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;ZZLandroidx/compose/ui/text/TextStyle;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function2;ZLandroidx/compose/ui/text/input/VisualTransformation;Landroidx/compose/foundation/text/KeyboardOptions;Landroidx/compose/foundation/text/KeyboardActions;ZIILandroidx/compose/foundation/interaction/MutableInteractionSource;Landroidx/compose/ui/graphics/Shape;Landroidx/compose/material3/TextFieldColors;Landroidx/compose/foundation/layout/PaddingValues;Landroidx/compose/runtime/Composer;IIII)V",
         "OutlinedTextFieldTopPadding",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomOutlinedTextField.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomOutlinedTextField.kt\nio/intercom/android/sdk/views/compose/IntercomOutlinedTextFieldKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 5 Color.kt\nandroidx/compose/ui/graphics/ColorKt\n*L\n1#1,132:1\n75#2:133\n1047#3,6:134\n1047#3,6:143\n118#4:140\n118#4:149\n118#4:150\n646#5:141\n635#5:142\n*S KotlinDebug\n*F\n+ 1 IntercomOutlinedTextField.kt\nio/intercom/android/sdk/views/compose/IntercomOutlinedTextFieldKt\n*L\n38#1:133\n50#1:134,6\n77#1:143,6\n63#1:140\n86#1:149\n131#1:150\n66#1:141\n66#1:142\n*E\n"
)
public final class IntercomOutlinedTextFieldKt {
   private static final float OutlinedTextFieldTopPadding;

   @Composable
   @ComposableInferredTarget(
      scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable][androidx.compose.ui.UiComposable][androidx.compose.ui.UiComposable][androidx.compose.ui.UiComposable]]"
   )
   public static final void IntercomOutlinedTextField(
      @NotNull String var0,
      @NotNull Function1 var1,
      @Nullable Modifier var2,
      boolean var3,
      boolean var4,
      @Nullable TextStyle var5,
      @Nullable Function2 var6,
      @Nullable Function2 var7,
      @Nullable Function2 var8,
      @Nullable Function2 var9,
      boolean var10,
      @Nullable VisualTransformation var11,
      @Nullable KeyboardOptions var12,
      @Nullable KeyboardActions var13,
      boolean var14,
      int var15,
      int var16,
      @Nullable MutableInteractionSource var17,
      @Nullable Shape var18,
      @Nullable TextFieldColors var19,
      @Nullable PaddingValues var20,
      @Nullable Composer var21,
      int var22,
      int var23,
      int var24,
      int var25
   ) {
      Intrinsics.checkNotNullParameter(var0, "value");
      Intrinsics.checkNotNullParameter(var1, "onValueChange");
      var21 = var21.startRestartGroup(967048125);
      ComposerKt.sourceInformation(
         var21,
         "C(IntercomOutlinedTextField)P(19,12,11,2,14,17,7,13,8,18,4,20,6,5,16,9,10,3,15)37@1573L7,49@2179L39,50@2253L6,51@2323L8,52@2384L6,53@2456L6,54@2522L6,55@2604L6,56@2668L6,57@2735L6,58@2800L6,59@2864L6,60@2925L6,99@4543L1090,70@3332L2308:IntercomOutlinedTextField.kt#h0e4f2"
      );
      int var26 = var22;
      int var27 = var23;
      int var28 = var24;
      if ((var25 & 1) != 0) {
         var26 |= 6;
      } else if ((var22 & 6) == 0) {
         var26 |= var21.changed(var0) ? 4 : 2;
      }

      if ((var25 & 2) != 0) {
         var26 |= 48;
      } else if ((var22 & 48) == 0) {
         var26 |= var21.changedInstance(var1) ? 32 : 16;
      }

      if ((var25 & 4) != 0) {
         var26 |= 384;
      } else if ((var22 & 384) == 0) {
         var26 |= var21.changed(var2) ? 256 : 128;
      }

      if ((var25 & 8) != 0) {
         var26 |= 3072;
      } else if ((var22 & 3072) == 0) {
         var26 |= var21.changed(var3) ? 2048 : 1024;
      }

      if ((var25 & 16) != 0) {
         var26 |= 24576;
      } else if ((var22 & 24576) == 0) {
         var26 |= var21.changed(var4) ? 16384 : 8192;
      }

      if ((var22 & 196608) == 0) {
         var26 |= (var25 & 32) == 0 && var21.changed(var5) ? 131072 : 65536;
      }

      if ((var25 & 64) != 0) {
         var26 |= 1572864;
      } else if ((var22 & 1572864) == 0) {
         var26 |= var21.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var25 & 128) != 0) {
         var26 |= 12582912;
      } else if ((var22 & 12582912) == 0) {
         var26 |= var21.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var25 & 256) != 0) {
         var26 |= 100663296;
      } else if ((var22 & 100663296) == 0) {
         var26 |= var21.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var25 & 512) != 0) {
         var26 |= 805306368;
      } else if ((var22 & 805306368) == 0) {
         var26 |= var21.changedInstance(var9) ? 536870912 : 268435456;
      }

      if ((var25 & 1024) != 0) {
         var27 |= 6;
      } else if ((var23 & 6) == 0) {
         var27 |= var21.changed(var10) ? 4 : 2;
      }

      if ((var25 & 2048) != 0) {
         var27 |= 48;
      } else if ((var23 & 48) == 0) {
         var27 |= var21.changed(var11) ? 32 : 16;
      }

      if ((var25 & 4096) != 0) {
         var27 |= 384;
      } else if ((var23 & 384) == 0) {
         var27 |= var21.changed(var12) ? 256 : 128;
      }

      if ((var25 & 8192) != 0) {
         var27 |= 3072;
      } else if ((var23 & 3072) == 0) {
         var27 |= var21.changed(var13) ? 2048 : 1024;
      }

      if ((var25 & 16384) != 0) {
         var27 |= 24576;
      } else if ((var23 & 24576) == 0) {
         var27 |= var21.changed(var14) ? 16384 : 8192;
      }

      if ((var23 & 196608) == 0) {
         var27 |= (var25 & 32768) == 0 && var21.changed(var15) ? 131072 : 65536;
      }

      if ((var25 & 65536) != 0) {
         var27 |= 1572864;
      } else if ((var23 & 1572864) == 0) {
         var27 |= var21.changed(var16) ? 1048576 : 524288;
      }

      if ((var25 & 131072) != 0) {
         var27 |= 12582912;
      } else if ((var23 & 12582912) == 0) {
         var27 |= var21.changed(var17) ? 8388608 : 4194304;
      }

      if ((var23 & 100663296) == 0) {
         var27 |= (var25 & 262144) == 0 && var21.changed(var18) ? 67108864 : 33554432;
      }

      if ((var23 & 805306368) == 0) {
         var27 |= (var25 & 524288) == 0 && var21.changed(var19) ? 536870912 : 268435456;
      }

      if ((var25 & 1048576) != 0) {
         var28 |= 6;
      } else if ((var24 & 6) == 0) {
         var28 |= var21.changed(var20) ? 4 : 2;
      }

      if ((var26 & 306783379) == 306783378 && (var27 & 306783379) == 306783378 && (var28 & 3) == 2 && var21.getSkipping()) {
         var21.skipToGroupEnd();
      } else {
         var21.startDefaults();
         if ((var22 & 1) != 0 && !var21.getDefaultsInvalid()) {
            var21.skipToGroupEnd();
            if ((var25 & 32) != 0) {
               var26 &= -458753;
            }

            if ((var25 & 32768) != 0) {
               var27 &= -458753;
            }

            if ((var25 & 262144) != 0) {
               var27 &= -234881025;
            }

            if ((var25 & 524288) != 0) {
               var27 &= -1879048193;
            }
         } else {
            if ((var25 & 4) != 0) {
               var2 = Modifier.Companion;
            }

            if ((var25 & 8) != 0) {
               var3 = true;
            }

            if ((var25 & 16) != 0) {
               var4 = false;
            }

            if ((var25 & 32) != 0) {
               CompositionLocal var29 = TextKt.getLocalTextStyle();
               int var30 = 0;
               int var31 = 0;
               ComposerKt.sourceInformationMarkerStart(var21, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
               Object var32 = var21.consume(var29);
               ComposerKt.sourceInformationMarkerEnd(var21);
               var5 = (TextStyle)var32;
               var26 &= -458753;
            }

            if ((var25 & 64) != 0) {
               var6 = null;
            }

            if ((var25 & 128) != 0) {
               var7 = null;
            }

            if ((var25 & 256) != 0) {
               var8 = null;
            }

            if ((var25 & 512) != 0) {
               var9 = null;
            }

            if ((var25 & 1024) != 0) {
               var10 = false;
            }

            if ((var25 & 2048) != 0) {
               var11 = VisualTransformation.Companion.getNone();
            }

            if ((var25 & 4096) != 0) {
               var12 = KeyboardOptions.Companion.getDefault();
            }

            if ((var25 & 8192) != 0) {
               var13 = KeyboardActions.Companion.getDefault();
            }

            if ((var25 & 16384) != 0) {
               var14 = false;
            }

            if ((var25 & 32768) != 0) {
               var15 = var14 ? 1 : Integer.MAX_VALUE;
               var27 &= -458753;
            }

            if ((var25 & 65536) != 0) {
               var16 = 1;
            }

            if ((var25 & 131072) != 0) {
               var21.startReplaceGroup(1299414973);
               ComposerKt.sourceInformation(var21, "CC(remember):IntercomOutlinedTextField.kt#9igjgp");
               Composer var57 = var21;
               boolean var61 = false;
               int var64 = 0;
               Object var33 = var57.rememberedValue();
               int var34 = 0;
               Object var10000;
               if (var33 == Composer.Companion.getEmpty()) {
                  int var35 = 0;
                  Object var75 = InteractionSourceKt.MutableInteractionSource();
                  var57.updateRememberedValue(var75);
                  var10000 = var75;
               } else {
                  var10000 = var33;
               }

               MutableInteractionSource var52 = (MutableInteractionSource)var10000;
               var21.endReplaceGroup();
               var17 = var52;
            }

            if ((var25 & 262144) != 0) {
               var18 = IntercomTheme.INSTANCE.getShapes(var21, IntercomTheme.$stable).getSmall();
               var27 &= -234881025;
            }

            if ((var25 & 524288) != 0) {
               TextFieldColors var53 = OutlinedTextFieldDefaults.INSTANCE.colors(var21, 6);
               long var58 = IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).recovered$a35e0f0c0a5b2483();
               long var65 = IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).recovered$a35e0f0c0a5b2483();
               long var71 = Color.recovered$41aa0ee03c2282fb(
                  IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.38F, 0.0F, 0.0F, 0.0F, 14, null
               );
               long var36 = IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
               long var38 = IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
               long var40 = IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).recovered$ad18c207ea721de5();
               long var42 = IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).recovered$ad18c207ea721de5();
               long var44 = IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).recovered$ad18c207ea721de5();
               long var46 = IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).recovered$ad18c207ea721de5();
               var19 = TextFieldColors.recovered$4bbf31c5aa9a006c(
                  var53,
                  var36,
                  var38,
                  var71,
                  0L,
                  var40,
                  var42,
                  var44,
                  var46,
                  0L,
                  0L,
                  null,
                  var58,
                  var65,
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
                  0L,
                  -6392,
                  2047,
                  null
               );
               var27 &= -1879048193;
            }

            if ((var25 & 1048576) != 0) {
               int var54 = 8;
               int var59 = 0;
               float var83 = Dp.recovered$95b3d103663bf060(var54);
               var54 = 8;
               var59 = 0;
               var20 = PaddingKt.recovered$4ebb4826aa57abc6(var83, Dp.recovered$95b3d103663bf060(var54));
            }
         }

         var21.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               967048125, var26, var27, "io.intercom.android.sdk.views.compose.IntercomOutlinedTextField (IntercomOutlinedTextField.kt:63)"
            );
         }

         long var62 = var5.recovered$6f15fcf6c761ad06();
         int var67 = 0;
         long var72 = var62;
         int var77 = 0;
         long var84;
         if (var72 != 16L) {
            var84 = var62;
         } else {
            int var37 = 0;
            var84 = var3 ? var19.recovered$3939e504db209109() : var19.recovered$e3459d3e60d37f5a();
         }

         long var56 = var84;
         TextStyle var63 = var5.merge(
            new TextStyle(
               var56, 0L, null, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, 0L, null, null, null, 0, 0, null, 16777214, null
            )
         );
         var21.startReplaceGroup(1299454039);
         ComposerKt.sourceInformation(var21, "76@3633L2");
         Modifier var86;
         if (var6 != null) {
            Modifier var85 = var2;
            boolean var10001 = true;
            var21.startReplaceGroup(1299461464);
            ComposerKt.sourceInformation(var21, "CC(remember):IntercomOutlinedTextField.kt#9igjgp");
            Composer var76 = var21;
            boolean var78 = false;
            int var79 = 0;
            Object var80 = var76.rememberedValue();
            int var39 = 0;
            Object var10002;
            if (var80 == Composer.Companion.getEmpty()) {
               boolean var49 = true;
               Modifier var48 = var2;
               int var81 = 0;
               Function1 var50 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1642_0) -> IntercomOutlinedTextFieldKt.IntercomOutlinedTextField$lambda$3$lambda$2((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredArgument_1642_0));
               var85 = var48;
               var10001 = var49;
               Object var82 = var50;
               var76.updateRememberedValue(var82);
               var10002 = var82;
            } else {
               var10002 = var80;
            }

            Function1 var73 = (Function1)var10002;
            var21.endReplaceGroup();
            var86 = PaddingKt.recovered$03b481d31d62885a(
               SemanticsModifierKt.semantics(var85, var10001, var73), 0.0F, OutlinedTextFieldTopPadding, 0.0F, 0.0F, 13, null
            );
         } else {
            var86 = var2;
         }

         Modifier var68 = var86;
         var21.endReplaceGroup();
         Modifier var87 = BackgroundKt.recovered$60bc47fb440df483(var68, var3 ? var19.recovered$22a4d3e74a9a01fe() : var19.recovered$4f50c543a06e0b0e(), var18);
         float var89 = TextFieldDefaults.INSTANCE.recovered$355d4d16a540e6e0();
         var67 = 40;
         int var74 = 0;
         Modifier var66 = SizeKt.recovered$07854581e2f2bbaf(var87, var89, Dp.recovered$95b3d103663bf060(var67));
         SolidColor var70 = new SolidColor(var10 ? var19.recovered$93a4baf945a9aa94() : var19.recovered$77684ba9cf1651c4(), null);
         BasicTextFieldKt.BasicTextField(
            var0,
            var1,
            var66,
            var3,
            var4,
            var63,
            var12,
            var13,
            var14,
            var15,
            var16,
            var11,
            null,
            var17,
            var70,
            ComposableLambdaKt.rememberComposableLambda(
               491720992,
               true,
               new IntercomOutlinedTextFieldKt$IntercomOutlinedTextField$3(var0, var3, var14, var11, var17, var10, var6, var7, var8, var9, var19, var20, var18),
               var21,
               54
            ),
            var21,
            14 & var26
               | 112 & var26
               | 7168 & var26
               | 57344 & var26
               | 3670016 & var27 << 12
               | 29360128 & var27 << 12
               | 234881024 & var27 << 12
               | 1879048192 & var27 << 12,
            196608 | 14 & var27 >> 18 | 112 & var27 | 7168 & var27 >> 12,
            4096
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var88 = var21.endRestartGroup();
      if (var88 != null) {
         final java.lang.String recoveredCapture_1643_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredCapture_1643_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_1643_2 = var2;
         final boolean recoveredCapture_1643_3 = var3;
         final boolean recoveredCapture_1643_4 = var4;
         final androidx.compose.ui.text.TextStyle recoveredCapture_1643_5 = var5;
         final kotlin.jvm.functions.Function2 recoveredCapture_1643_6 = var6;
         final kotlin.jvm.functions.Function2 recoveredCapture_1643_7 = var7;
         final kotlin.jvm.functions.Function2 recoveredCapture_1643_8 = var8;
         final kotlin.jvm.functions.Function2 recoveredCapture_1643_9 = var9;
         final boolean recoveredCapture_1643_10 = var10;
         final androidx.compose.ui.text.input.VisualTransformation recoveredCapture_1643_11 = var11;
         final androidx.compose.foundation.text.KeyboardOptions recoveredCapture_1643_12 = var12;
         final androidx.compose.foundation.text.KeyboardActions recoveredCapture_1643_13 = var13;
         final boolean recoveredCapture_1643_14 = var14;
         final int recoveredCapture_1643_15 = var15;
         final int recoveredCapture_1643_16 = var16;
         final androidx.compose.foundation.interaction.MutableInteractionSource recoveredCapture_1643_17 = var17;
         final androidx.compose.ui.graphics.Shape recoveredCapture_1643_18 = var18;
         final androidx.compose.material3.TextFieldColors recoveredCapture_1643_19 = var19;
         final androidx.compose.foundation.layout.PaddingValues recoveredCapture_1643_20 = var20;
         final int recoveredCapture_1643_21 = var22;
         final int recoveredCapture_1643_22 = var23;
         final int recoveredCapture_1643_23 = var24;
         final int recoveredCapture_1643_24 = var25;
         var88.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1643_0, recoveredArgument_1643_1) -> IntercomOutlinedTextFieldKt.IntercomOutlinedTextField$lambda$4(recoveredCapture_1643_0, recoveredCapture_1643_1, recoveredCapture_1643_2, recoveredCapture_1643_3, recoveredCapture_1643_4, recoveredCapture_1643_5, recoveredCapture_1643_6, recoveredCapture_1643_7, recoveredCapture_1643_8, recoveredCapture_1643_9, recoveredCapture_1643_10, recoveredCapture_1643_11, recoveredCapture_1643_12, recoveredCapture_1643_13, recoveredCapture_1643_14, recoveredCapture_1643_15, recoveredCapture_1643_16, recoveredCapture_1643_17, recoveredCapture_1643_18, recoveredCapture_1643_19, recoveredCapture_1643_20, recoveredCapture_1643_21, recoveredCapture_1643_22, recoveredCapture_1643_23, recoveredCapture_1643_24, (androidx.compose.runtime.Composer)recoveredArgument_1643_0, ((java.lang.Integer)recoveredArgument_1643_1).intValue())));
      }
   }

   private static final Unit IntercomOutlinedTextField$lambda$3$lambda$2(SemanticsPropertyReceiver var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$semantics");
      return Unit.INSTANCE;
   }

   private static final Unit IntercomOutlinedTextField$lambda$4(
      String var0,
      Function1 var1,
      Modifier var2,
      boolean var3,
      boolean var4,
      TextStyle var5,
      Function2 var6,
      Function2 var7,
      Function2 var8,
      Function2 var9,
      boolean var10,
      VisualTransformation var11,
      KeyboardOptions var12,
      KeyboardActions var13,
      boolean var14,
      int var15,
      int var16,
      MutableInteractionSource var17,
      Shape var18,
      TextFieldColors var19,
      PaddingValues var20,
      int var21,
      int var22,
      int var23,
      int var24,
      Composer var25,
      int var26
   ) {
      IntercomOutlinedTextField(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var13,
         var14,
         var15,
         var16,
         var17,
         var18,
         var19,
         var20,
         var25,
         RecomposeScopeImplKt.updateChangedFlags(var21 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var22),
         RecomposeScopeImplKt.updateChangedFlags(var23),
         var24
      );
      return Unit.INSTANCE;
   }

   static {
      int var0 = 8;
      int var1 = 0;
      OutlinedTextFieldTopPadding = Dp.recovered$95b3d103663bf060(var0);
   }
}

package io.intercom.android.sdk.survey.ui.questiontype.choice;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.R.string;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\u001ag\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00052\u0006\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\t2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\tH\u0001¢\u0006\u0004\b\u0010\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0017\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013¨\u0006\u0018",
   d2 = {
         "ChoicePill",
         "",
         "selected",
         "",
         "onClicked",
         "Lkotlin/Function1;",
         "",
         "text",
         "strokeColor",
         "Landroidx/compose/ui/graphics/Color;",
         "strokeWidth",
         "Landroidx/compose/ui/unit/Dp;",
         "backgroundColor",
         "fontWeight",
         "Landroidx/compose/ui/text/font/FontWeight;",
         "fontColor",
         "ChoicePill-UdaoDFU",
         "(ZLkotlin/jvm/functions/Function1;Ljava/lang/String;JFJLandroidx/compose/ui/text/font/FontWeight;JLandroidx/compose/runtime/Composer;II)V",
         "EmptyPill",
         "(Landroidx/compose/runtime/Composer;I)V",
         "SelectedPill",
         "SelectedPillDark",
         "SelectedPillLongText",
         "SelectedPillLongBigText",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nChoicePill.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChoicePill.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/ChoicePillKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,131:1\n1047#2,6:132\n1047#2,6:139\n118#3:138\n118#3:145\n118#3:177\n99#4:146\n97#4,8:147\n106#4:181\n81#5,6:155\n88#5,6:170\n96#5:180\n402#6,9:161\n411#6:176\n412#6,2:178\n*S KotlinDebug\n*F\n+ 1 ChoicePill.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/ChoicePillKt\n*L\n34#1:132,6\n53#1:139,6\n37#1:138\n54#1:145\n72#1:177\n44#1:146\n44#1:147,8\n44#1:181\n44#1:155,6\n44#1:170,6\n44#1:180\n44#1:161,9\n44#1:176\n44#1:178,2\n*E\n"
)
public final class ChoicePillKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$b36a01a674e93ba3(
      boolean var0,
      @Nullable Function1 var1,
      @NotNull String var2,
      long var3,
      float var5,
      long var6,
      @Nullable FontWeight var8,
      long var9,
      @Nullable Composer var11,
      int var12,
      int var13
   ) {
      Intrinsics.checkNotNullParameter(var2, "text");
      var11 = var11.startRestartGroup(418926179);
      ComposerKt.sourceInformation(
         var11,
         "C(ChoicePill)P(4,3,7,5:c#ui.graphics.Color,6:c#ui.unit.Dp,0:c#ui.graphics.Color,2,1:c#ui.graphics.Color)33@1391L2,41@1663L6,52@1970L19,43@1681L1126:ChoicePill.kt#w8l19x"
      );
      int var14 = var12;
      if ((var13 & 1) != 0) {
         var14 |= 6;
      } else if ((var12 & 6) == 0) {
         var14 |= var11.changed(var0) ? 4 : 2;
      }

      if ((var13 & 2) != 0) {
         var14 |= 48;
      } else if ((var12 & 48) == 0) {
         var14 |= var11.changedInstance(var1) ? 32 : 16;
      }

      if ((var13 & 4) != 0) {
         var14 |= 384;
      } else if ((var12 & 384) == 0) {
         var14 |= var11.changed(var2) ? 256 : 128;
      }

      if ((var13 & 8) != 0) {
         var14 |= 3072;
      } else if ((var12 & 3072) == 0) {
         var14 |= var11.changed(var3) ? 2048 : 1024;
      }

      if ((var13 & 16) != 0) {
         var14 |= 24576;
      } else if ((var12 & 24576) == 0) {
         var14 |= var11.changed(var5) ? 16384 : 8192;
      }

      if ((var13 & 32) != 0) {
         var14 |= 196608;
      } else if ((var12 & 196608) == 0) {
         var14 |= var11.changed(var6) ? 131072 : 65536;
      }

      if ((var13 & 64) != 0) {
         var14 |= 1572864;
      } else if ((var12 & 1572864) == 0) {
         var14 |= var11.changed(var8) ? 1048576 : 524288;
      }

      if ((var12 & 12582912) == 0) {
         var14 |= (var13 & 128) == 0 && var11.changed(var9) ? 8388608 : 4194304;
      }

      if ((var14 & 4793491) == 4793490 && var11.getSkipping()) {
         var11.skipToGroupEnd();
      } else {
         var11.startDefaults();
         if ((var12 & 1) != 0 && !var11.getDefaultsInvalid()) {
            var11.skipToGroupEnd();
            if ((var13 & 128) != 0) {
               var14 &= -29360129;
            }
         } else {
            if ((var13 & 2) != 0) {
               var11.startReplaceGroup(-8602219);
               ComposerKt.sourceInformation(var11, "CC(remember):ChoicePill.kt#9igjgp");
               Composer var16 = var11;
               boolean var17 = false;
               int var18 = 0;
               Object var19 = var16.rememberedValue();
               int var20 = 0;
               Object var10000;
               if (var19 == Composer.Companion.getEmpty()) {
                  int var21 = 0;
                  Object var70 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1393_0) -> ChoicePillKt.ChoicePill_UdaoDFU$lambda$1$lambda$0((java.lang.String)recoveredArgument_1393_0));
                  var16.updateRememberedValue(var70);
                  var10000 = var70;
               } else {
                  var10000 = var19;
               }

               Function1 var15 = (Function1)var10000;
               var11.endReplaceGroup();
               var1 = var15;
            }

            if ((var13 & 8) != 0) {
               var3 = Color.Companion.recovered$235b30c090a399c5();
            }

            if ((var13 & 16) != 0) {
               int var55 = 1;
               int var57 = 0;
               var5 = Dp.recovered$95b3d103663bf060(var55);
            }

            if ((var13 & 32) != 0) {
               var6 = Color.Companion.recovered$95989221dad41c4d();
            }

            if ((var13 & 64) != 0) {
               var8 = FontWeight.Companion.getNormal();
            }

            if ((var13 & 128) != 0) {
               var9 = ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var6);
               var14 &= -29360129;
            }
         }

         var11.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(418926179, var14, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.ChoicePill (ChoicePill.kt:40)");
         }

         CornerBasedShape var56 = IntercomTheme.INSTANCE.getShapes(var11, IntercomTheme.$stable).getSmall();
         Modifier var75 = BackgroundKt.recovered$42a3174180b3fb7a(
            ClipKt.clip(
               BorderKt.border(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), BorderStrokeKt.recovered$91879a1bef2fb536(var5, var3), var56),
               var56
            ),
            var6,
            null,
            2,
            null
         );
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var11.startReplaceGroup(-8583674);
         ComposerKt.sourceInformation(var11, "CC(remember):ChoicePill.kt#9igjgp");
         Composer var61 = var11;
         boolean var64 = (var14 & 112) == 32 | (var14 & 896) == 256;
         int var66 = 0;
         Object var68 = var61.rememberedValue();
         int var71 = 0;
         Object var10005;
         if (!var64 && var68 != Composer.Companion.getEmpty()) {
            var10005 = var68;
         } else {
            Object var52 = null;
            Object var51 = null;
            Object var50 = null;
            boolean var49 = false;
            Modifier var48 = var75;
            int var22 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_1394_0 = var1;
            final java.lang.String recoveredCapture_1394_1 = var2;
            Function0 var53 = ((kotlin.jvm.functions.Function0) () -> ChoicePillKt.ChoicePill_UdaoDFU$lambda$3$lambda$2(recoveredCapture_1394_0, recoveredCapture_1394_1));
            var75 = var48;
            var10001 = var49;
            var10002 = var50;
            var10003 = var51;
            var10004 = var52;
            Object var23 = var53;
            var61.updateRememberedValue(var23);
            var10005 = var23;
         }

         Function0 var58 = (Function0)var10005;
         var11.endReplaceGroup();
         var75 = ClickableKt.recovered$1c9660c41fd850dc(var75, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var58, 15, null);
         int var59 = 16;
         int var62 = 0;
         Modifier var60 = PaddingKt.recovered$eb6a744cd98cdbca(var75, Dp.recovered$95b3d103663bf060(var59));
         Horizontal var63 = Arrangement.INSTANCE.getSpaceBetween();
         byte var67 = 48;
         var68 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var11, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Vertical var65 = Alignment.Companion.getTop();
         MeasurePolicy var72 = RowKt.rowMeasurePolicy(var63, var65, var11, 14 & var67 >> 3 | 112 & var67 >> 3);
         Modifier var73 = var60;
         MeasurePolicy var74 = var72;
         Composer var24 = var11;
         int var25 = 112 & var67 << 3;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var27 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var24, 0));
         CompositionLocalMap var28 = var24.getCurrentCompositionLocalMap();
         Modifier var29 = ComposedModifierKt.materializeModifier(var24, var73);
         Function0 var30 = ComposeUiNode.Companion.getConstructor();
         Composer var31 = var24;
         int var32 = 6 | 896 & var25 << 6;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var31.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var31.startReusableNode();
         if (var31.getInserting()) {
            var31.createNode(var30);
         } else {
            var31.useNode();
         }

         Composer var34 = Updater.recovered$676aa17b44fd6280(var31);
         int var35 = 0;
         Updater.recovered$89682b199a142880(var34, var74, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var34, var28, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var34, var27, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var34, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var34, var29, ComposeUiNode.Companion.getSetModifier());
         int var36 = 14 & var32 >> 6;
         Composer var37 = var31;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var39 = 6 | 112 & var67 >> 6;
         Composer var40 = var37;
         RowScope var41 = RowScopeInstance.INSTANCE;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(var40, 1313295661, "C63@2358L10,56@2093L293:ChoicePill.kt#w8l19x");
         TextKt.recovered$5b114ab32a4f4190(
            var2,
            var41.align(SizeKt.fillMaxWidth(Modifier.Companion, 0.85F), Alignment.Companion.getCenterVertically()),
            var9,
            null,
            0L,
            null,
            var8,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            IntercomTheme.INSTANCE.getTypography(var40, IntercomTheme.$stable).getType04(),
            var40,
            14 & var14 >> 6 | 896 & var14 >> 15 | 3670016 & var14,
            0,
            131000
         );
         var40.startReplaceGroup(2120583720);
         ComposerKt.sourceInformation(var40, "67@2455L65,68@2559L58,66@2423L368");
         if (var0) {
            Painter var43 = PainterResources_androidKt.painterResource(drawable.intercom_attribute_verified_tick, var40, 0);
            String var44 = StringResources_androidKt.stringResource(string.selected, var40, 0);
            Modifier var78 = Modifier.Companion;
            int $this$dp$ivx = 24;
            int $i$f$getDpx = 0;
            Modifier var47 = var41.align(
               SizeKt.recovered$111ef94c93e78c04(var78, Dp.recovered$95b3d103663bf060($this$dp$ivx)), Alignment.Companion.getCenterVertically()
            );
            IconKt.recovered$e00adffb7fe45b02(var43, var44, var47, var9, var40, Painter.$stable | 7168 & var14 >> 12, 0);
         }

         var40.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var40);
         ComposerKt.sourceInformationMarkerEnd(var37);
         var31.endNode();
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var11);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var77 = var11.endRestartGroup();
      if (var77 != null) {
         final boolean recoveredCapture_1392_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredCapture_1392_1 = var1;
         final java.lang.String recoveredCapture_1392_2 = var2;
         final long recoveredCapture_1392_3 = var3;
         final float recoveredCapture_1392_4 = var5;
         final long recoveredCapture_1392_5 = var6;
         final androidx.compose.ui.text.font.FontWeight recoveredCapture_1392_6 = var8;
         final long recoveredCapture_1392_7 = var9;
         final int recoveredCapture_1392_8 = var12;
         final int recoveredCapture_1392_9 = var13;
         var77.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1392_0, recoveredArgument_1392_1) -> ChoicePillKt.ChoicePill_UdaoDFU$lambda$5(recoveredCapture_1392_0, recoveredCapture_1392_1, recoveredCapture_1392_2, recoveredCapture_1392_3, recoveredCapture_1392_4, recoveredCapture_1392_5, recoveredCapture_1392_6, recoveredCapture_1392_7, recoveredCapture_1392_8, recoveredCapture_1392_9, (androidx.compose.runtime.Composer)recoveredArgument_1392_0, ((java.lang.Integer)recoveredArgument_1392_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EmptyPill(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1274080153);
      ComposerKt.sourceInformation(var0, "C(EmptyPill)81@2862L112:ChoicePill.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1274080153, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.EmptyPill (ChoicePill.kt:80)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ChoicePillKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1387_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1387_0, recoveredArgument_1387_1) -> ChoicePillKt.EmptyPill$lambda$6(recoveredCapture_1387_0, (androidx.compose.runtime.Composer)recoveredArgument_1387_0, ((java.lang.Integer)recoveredArgument_1387_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void SelectedPill(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1118291055);
      ComposerKt.sourceInformation(var0, "C(SelectedPill)92@3032L190:ChoicePill.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1118291055, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.SelectedPill (ChoicePill.kt:91)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ChoicePillKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1388_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1388_0, recoveredArgument_1388_1) -> ChoicePillKt.SelectedPill$lambda$7(recoveredCapture_1388_0, (androidx.compose.runtime.Composer)recoveredArgument_1388_0, ((java.lang.Integer)recoveredArgument_1388_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void SelectedPillDark(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-774877671);
      ComposerKt.sourceInformation(var0, "C(SelectedPillDark)104@3284L153:ChoicePill.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-774877671, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.SelectedPillDark (ChoicePill.kt:103)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ChoicePillKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1389_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1389_0, recoveredArgument_1389_1) -> ChoicePillKt.SelectedPillDark$lambda$8(recoveredCapture_1389_0, (androidx.compose.runtime.Composer)recoveredArgument_1389_0, ((java.lang.Integer)recoveredArgument_1389_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void SelectedPillLongText(Composer var0, int var1) {
      var0 = var0.startRestartGroup(173524454);
      ComposerKt.sourceInformation(var0, "C(SelectedPillLongText)116@3503L756:ChoicePill.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(173524454, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.SelectedPillLongText (ChoicePill.kt:115)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ChoicePillKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1391_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1391_0, recoveredArgument_1391_1) -> ChoicePillKt.SelectedPillLongText$lambda$9(recoveredCapture_1391_0, (androidx.compose.runtime.Composer)recoveredArgument_1391_0, ((java.lang.Integer)recoveredArgument_1391_1).intValue())));
      }
   }

   @Preview(fontScale = 2.0F)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void SelectedPillLongBigText(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1982481602);
      ComposerKt.sourceInformation(var0, "C(SelectedPillLongBigText)128@4344L22:ChoicePill.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1982481602, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.SelectedPillLongBigText (ChoicePill.kt:127)"
            );
         }

         SelectedPillLongText(var0, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1390_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1390_0, recoveredArgument_1390_1) -> ChoicePillKt.SelectedPillLongBigText$lambda$10(recoveredCapture_1390_0, (androidx.compose.runtime.Composer)recoveredArgument_1390_0, ((java.lang.Integer)recoveredArgument_1390_1).intValue())));
      }
   }

   private static final Unit ChoicePill_UdaoDFU$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ChoicePill_UdaoDFU$lambda$3$lambda$2(Function1 var0, String var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ChoicePill_UdaoDFU$lambda$5(
      boolean var0, Function1 var1, String var2, long var3, float var5, long var6, FontWeight var8, long var9, int var11, int var12, Composer var13, int var14
   ) {
      recovered$b36a01a674e93ba3(var0, var1, var2, var3, var5, var6, var8, var9, var13, RecomposeScopeImplKt.updateChangedFlags(var11 | 1), var12);
      return Unit.INSTANCE;
   }

   private static final Unit EmptyPill$lambda$6(int var0, Composer var1, int var2) {
      EmptyPill(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SelectedPill$lambda$7(int var0, Composer var1, int var2) {
      SelectedPill(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SelectedPillDark$lambda$8(int var0, Composer var1, int var2) {
      SelectedPillDark(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SelectedPillLongText$lambda$9(int var0, Composer var1, int var2) {
      SelectedPillLongText(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SelectedPillLongBigText$lambda$10(int var0, Composer var1, int var2) {
      SelectedPillLongBigText(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

package io.intercom.android.sdk.survey.ui.questiontype.numericscale;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.CornerBasedShape;
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
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
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
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a]\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0001¢\u0006\u0004\b\u0010\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013¨\u0006\u0016",
   d2 = {
         "NumericRatingCell",
         "",
         "content",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "strokeColor",
         "Landroidx/compose/ui/graphics/Color;",
         "strokeWidth",
         "Landroidx/compose/ui/unit/Dp;",
         "backgroundColor",
         "fontWeight",
         "Landroidx/compose/ui/text/font/FontWeight;",
         "fontColor",
         "fontSize",
         "Landroidx/compose/ui/unit/TextUnit;",
         "NumericRatingCell-jWvj134",
         "(Ljava/lang/String;Landroidx/compose/ui/Modifier;JFJLandroidx/compose/ui/text/font/FontWeight;JJLandroidx/compose/runtime/Composer;II)V",
         "EmptyCell",
         "(Landroidx/compose/runtime/Composer;I)V",
         "FilledCell",
         "DarkFilledCell",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nNumericRatingCell.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NumericRatingCell.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/NumericRatingCellKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,81:1\n118#2:82\n118#2:83\n118#2:84\n70#3:85\n67#3,9:86\n77#3:120\n81#4,6:95\n88#4,6:110\n96#4:119\n402#5,9:101\n411#5,3:116\n*S KotlinDebug\n*F\n+ 1 NumericRatingCell.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/NumericRatingCellKt\n*L\n28#1:82\n43#1:83\n44#1:84\n35#1:85\n35#1:86,9\n35#1:120\n35#1:95,6\n35#1:110,6\n35#1:119\n35#1:101,9\n35#1:116,3\n*E\n"
)
public final class NumericRatingCellKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$7a9e16f91d684c97(
      @NotNull String var0,
      @Nullable Modifier var1,
      long var2,
      float var4,
      long var5,
      @Nullable FontWeight var7,
      long var8,
      long var10,
      @Nullable Composer var12,
      int var13,
      int var14
   ) {
      Intrinsics.checkNotNullParameter(var0, "content");
      var12 = var12.startRestartGroup(-881315304);
      ComposerKt.sourceInformation(
         var12,
         "C(NumericRatingCell)P(1,5,6:c#ui.graphics.Color,7:c#ui.unit.Dp,0:c#ui.graphics.Color,4,2:c#ui.graphics.Color,3:c#ui.unit.TextUnit)31@1261L10,33@1323L6,34@1340L507:NumericRatingCell.kt#hdztyp"
      );
      int var15 = var13;
      if ((var14 & 1) != 0) {
         var15 |= 6;
      } else if ((var13 & 6) == 0) {
         var15 |= var12.changed(var0) ? 4 : 2;
      }

      if ((var14 & 2) != 0) {
         var15 |= 48;
      } else if ((var13 & 48) == 0) {
         var15 |= var12.changed(var1) ? 32 : 16;
      }

      if ((var14 & 4) != 0) {
         var15 |= 384;
      } else if ((var13 & 384) == 0) {
         var15 |= var12.changed(var2) ? 256 : 128;
      }

      if ((var14 & 8) != 0) {
         var15 |= 3072;
      } else if ((var13 & 3072) == 0) {
         var15 |= var12.changed(var4) ? 2048 : 1024;
      }

      if ((var14 & 16) != 0) {
         var15 |= 24576;
      } else if ((var13 & 24576) == 0) {
         var15 |= var12.changed(var5) ? 16384 : 8192;
      }

      if ((var14 & 32) != 0) {
         var15 |= 196608;
      } else if ((var13 & 196608) == 0) {
         var15 |= var12.changed(var7) ? 131072 : 65536;
      }

      if ((var13 & 1572864) == 0) {
         var15 |= (var14 & 64) == 0 && var12.changed(var8) ? 1048576 : 524288;
      }

      if ((var13 & 12582912) == 0) {
         var15 |= (var14 & 128) == 0 && var12.changed(var10) ? 8388608 : 4194304;
      }

      if ((var15 & 4793491) == 4793490 && var12.getSkipping()) {
         var12.skipToGroupEnd();
      } else {
         var12.startDefaults();
         if ((var13 & 1) != 0 && !var12.getDefaultsInvalid()) {
            var12.skipToGroupEnd();
            if ((var14 & 64) != 0) {
               var15 &= -3670017;
            }

            if ((var14 & 128) != 0) {
               var15 &= -29360129;
            }
         } else {
            if ((var14 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var14 & 4) != 0) {
               var2 = Color.Companion.recovered$235b30c090a399c5();
            }

            if ((var14 & 8) != 0) {
               int var16 = 1;
               int var17 = 0;
               var4 = Dp.recovered$95b3d103663bf060(var16);
            }

            if ((var14 & 16) != 0) {
               var5 = Color.Companion.recovered$95989221dad41c4d();
            }

            if ((var14 & 32) != 0) {
               var7 = FontWeight.Companion.getNormal();
            }

            if ((var14 & 64) != 0) {
               var8 = ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var5);
               var15 &= -3670017;
            }

            if ((var14 & 128) != 0) {
               var10 = IntercomTheme.INSTANCE.getTypography(var12, IntercomTheme.$stable).getType04().recovered$8f8bc2178e9073c3();
               var15 &= -29360129;
            }
         }

         var12.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -881315304, var15, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingCell (NumericRatingCell.kt:32)"
            );
         }

         CornerBasedShape var46 = IntercomTheme.INSTANCE.getShapes(var12, IntercomTheme.$stable).getSmall();
         Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
            ClipKt.clip(BorderKt.border(var1, BorderStrokeKt.recovered$91879a1bef2fb536(var4, var2), var46), var46), var5, null, 2, null
         );
         int var47 = 44;
         int var18 = 0;
         var10000 = SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var47));
         var47 = 44;
         var18 = 0;
         Modifier var49 = SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var47));
         byte var20 = 0;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var12, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var51 = Alignment.Companion.getTopStart();
         boolean var19 = false;
         MeasurePolicy var22 = BoxKt.maybeCachedBoxMeasurePolicy(var51, var19);
         Modifier var23 = var49;
         MeasurePolicy var24 = var22;
         Composer var25 = var12;
         int var26 = 112 & var20 << 3;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var28 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var25, 0));
         CompositionLocalMap var29 = var25.getCurrentCompositionLocalMap();
         Modifier var30 = ComposedModifierKt.materializeModifier(var25, var23);
         Function0 var31 = ComposeUiNode.Companion.getConstructor();
         Composer var32 = var25;
         int var33 = 6 | 896 & var26 << 6;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var32.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var32.startReusableNode();
         if (var32.getInserting()) {
            var32.createNode(var31);
         } else {
            var32.useNode();
         }

         Composer var35 = Updater.recovered$676aa17b44fd6280(var32);
         int var36 = 0;
         Updater.recovered$89682b199a142880(var35, var24, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var35, var29, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var35, var28, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var35, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var35, var30, ComposeUiNode.Companion.getSetModifier());
         int var37 = 14 & var33 >> 6;
         Composer var38 = var32;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var40 = 6 | 112 & var20 >> 6;
         Composer var41 = var38;
         BoxScope var42 = BoxScopeInstance.INSTANCE;
         int var43 = 0;
         ComposerKt.sourceInformationMarkerStart(var41, -1769461972, "C45@1640L201:NumericRatingCell.kt#hdztyp");
         Modifier var44 = var42.align(Modifier.Companion, Alignment.Companion.getCenter());
         TextKt.recovered$5b114ab32a4f4190(
            var0,
            var44,
            var8,
            null,
            var10,
            null,
            var7,
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
            null,
            var41,
            14 & var15 | 896 & var15 >> 12 | 57344 & var15 >> 9 | 3670016 & var15 << 3,
            0,
            262056
         );
         ComposerKt.sourceInformationMarkerEnd(var41);
         ComposerKt.sourceInformationMarkerEnd(var38);
         var32.endNode();
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var12);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var53 = var12.endRestartGroup();
      if (var53 != null) {
         final java.lang.String recoveredCapture_1474_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_1474_1 = var1;
         final long recoveredCapture_1474_2 = var2;
         final float recoveredCapture_1474_3 = var4;
         final long recoveredCapture_1474_4 = var5;
         final androidx.compose.ui.text.font.FontWeight recoveredCapture_1474_5 = var7;
         final long recoveredCapture_1474_6 = var8;
         final long recoveredCapture_1474_7 = var10;
         final int recoveredCapture_1474_8 = var13;
         final int recoveredCapture_1474_9 = var14;
         var53.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1474_0, recoveredArgument_1474_1) -> NumericRatingCellKt.NumericRatingCell_jWvj134$lambda$1(recoveredCapture_1474_0, recoveredCapture_1474_1, recoveredCapture_1474_2, recoveredCapture_1474_3, recoveredCapture_1474_4, recoveredCapture_1474_5, recoveredCapture_1474_6, recoveredCapture_1474_7, recoveredCapture_1474_8, recoveredCapture_1474_9, (androidx.compose.runtime.Composer)recoveredArgument_1474_0, ((java.lang.Integer)recoveredArgument_1474_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EmptyCell(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1361614452);
      ComposerKt.sourceInformation(var0, "C(EmptyCell)58@1902L47:NumericRatingCell.kt#hdztyp");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1361614452, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.EmptyCell (NumericRatingCell.kt:57)");
         }

         recovered$7a9e16f91d684c97("1", null, 0L, 0.0F, 0L, null, 0L, 0L, var0, 6, 254);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1472_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1472_0, recoveredArgument_1472_1) -> NumericRatingCellKt.EmptyCell$lambda$2(recoveredCapture_1472_0, (androidx.compose.runtime.Composer)recoveredArgument_1472_0, ((java.lang.Integer)recoveredArgument_1472_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FilledCell(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1860651045);
      ComposerKt.sourceInformation(var0, "C(FilledCell)66@2005L87:NumericRatingCell.kt#hdztyp");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1860651045, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.FilledCell (NumericRatingCell.kt:65)");
         }

         recovered$7a9e16f91d684c97("1", null, 0L, 0.0F, Color.Companion.recovered$ba858dcfc5e04b9c(), null, 0L, 0L, var0, 24582, 238);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1473_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1473_0, recoveredArgument_1473_1) -> NumericRatingCellKt.FilledCell$lambda$3(recoveredCapture_1473_0, (androidx.compose.runtime.Composer)recoveredArgument_1473_0, ((java.lang.Integer)recoveredArgument_1473_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void DarkFilledCell(Composer var0, int var1) {
      var0 = var0.startRestartGroup(477358395);
      ComposerKt.sourceInformation(var0, "C(DarkFilledCell)75@2152L86:NumericRatingCell.kt#hdztyp");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               477358395, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.DarkFilledCell (NumericRatingCell.kt:74)"
            );
         }

         recovered$7a9e16f91d684c97("1", null, 0L, 0.0F, Color.Companion.recovered$235b30c090a399c5(), null, 0L, 0L, var0, 24582, 238);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1471_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1471_0, recoveredArgument_1471_1) -> NumericRatingCellKt.DarkFilledCell$lambda$4(recoveredCapture_1471_0, (androidx.compose.runtime.Composer)recoveredArgument_1471_0, ((java.lang.Integer)recoveredArgument_1471_1).intValue())));
      }
   }

   private static final Unit NumericRatingCell_jWvj134$lambda$1(
      String var0, Modifier var1, long var2, float var4, long var5, FontWeight var7, long var8, long var10, int var12, int var13, Composer var14, int var15
   ) {
      recovered$7a9e16f91d684c97(var0, var1, var2, var4, var5, var7, var8, var10, var14, RecomposeScopeImplKt.updateChangedFlags(var12 | 1), var13);
      return Unit.INSTANCE;
   }

   private static final Unit EmptyCell$lambda$2(int var0, Composer var1, int var2) {
      EmptyCell(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FilledCell$lambda$3(int var0, Composer var1, int var2) {
      FilledCell(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit DarkFilledCell$lambda$4(int var0, Composer var1, int var2) {
      DarkFilledCell(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

package io.intercom.android.sdk.survey.ui.questiontype.choice;

import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.SurfaceKt;
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
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.SurveyUiColors;
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
   d1 = "\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a{\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\r2\b\b\u0002\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0013\u001a\u00020\rH\u0001¢\u0006\u0004\b\u0014\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0017¨\u0006\u0018",
   d2 = {
         "OtherOption",
         "",
         "selected",
         "",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "text",
         "",
         "onClicked",
         "Lkotlin/Function0;",
         "onTextChanged",
         "Lkotlin/Function1;",
         "strokeColor",
         "Landroidx/compose/ui/graphics/Color;",
         "strokeWidth",
         "Landroidx/compose/ui/unit/Dp;",
         "backgroundColor",
         "fontWeight",
         "Landroidx/compose/ui/text/font/FontWeight;",
         "fontColor",
         "OtherOption-YCJL08c",
         "(ZLio/intercom/android/sdk/survey/SurveyUiColors;Ljava/lang/String;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;JFJLandroidx/compose/ui/text/font/FontWeight;JLandroidx/compose/runtime/Composer;II)V",
         "OtherOptionPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nOtherOption.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OtherOption.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/OtherOptionKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,86:1\n118#2:87\n118#2:127\n118#2:132\n87#3:88\n83#3,10:89\n94#3:131\n81#4,6:99\n88#4,6:114\n96#4:130\n402#5,9:105\n411#5:120\n412#5,2:128\n1047#6,6:121\n*S KotlinDebug\n*F\n+ 1 OtherOption.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/OtherOptionKt\n*L\n36#1:87\n52#1:127\n76#1:132\n41#1:88\n41#1:89,10\n41#1:131\n41#1:99,6\n41#1:114,6\n41#1:130\n41#1:105,9\n41#1:120\n41#1:128,2\n50#1:121,6\n*E\n"
)
public final class OtherOptionKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$ceeec0b30e6e12f1(
      boolean var0,
      @NotNull SurveyUiColors var1,
      @NotNull String var2,
      @NotNull Function0 var3,
      @NotNull Function1 var4,
      long var5,
      float var7,
      long var8,
      @Nullable FontWeight var10,
      long var11,
      @Nullable Composer var13,
      int var14,
      int var15
   ) {
      Intrinsics.checkNotNullParameter(var1, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var2, "text");
      Intrinsics.checkNotNullParameter(var3, "onClicked");
      Intrinsics.checkNotNullParameter(var4, "onTextChanged");
      var13 = var13.startRestartGroup(-933969743);
      ComposerKt.sourceInformation(
         var13,
         "C(OtherOption)P(5,8,9,3,4,6:c#ui.graphics.Color,7:c#ui.unit.Dp,0:c#ui.graphics.Color,2,1:c#ui.graphics.Color)40@1650L1277:OtherOption.kt#w8l19x"
      );
      int var16 = var14;
      if ((var15 & 1) != 0) {
         var16 |= 6;
      } else if ((var14 & 6) == 0) {
         var16 |= var13.changed(var0) ? 4 : 2;
      }

      if ((var15 & 2) != 0) {
         var16 |= 48;
      } else if ((var14 & 48) == 0) {
         var16 |= var13.changed(var1) ? 32 : 16;
      }

      if ((var15 & 4) != 0) {
         var16 |= 384;
      } else if ((var14 & 384) == 0) {
         var16 |= var13.changed(var2) ? 256 : 128;
      }

      if ((var15 & 8) != 0) {
         var16 |= 3072;
      } else if ((var14 & 3072) == 0) {
         var16 |= var13.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var15 & 16) != 0) {
         var16 |= 24576;
      } else if ((var14 & 24576) == 0) {
         var16 |= var13.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var15 & 32) != 0) {
         var16 |= 196608;
      } else if ((var14 & 196608) == 0) {
         var16 |= var13.changed(var5) ? 131072 : 65536;
      }

      if ((var15 & 64) != 0) {
         var16 |= 1572864;
      } else if ((var14 & 1572864) == 0) {
         var16 |= var13.changed(var7) ? 1048576 : 524288;
      }

      if ((var15 & 128) != 0) {
         var16 |= 12582912;
      } else if ((var14 & 12582912) == 0) {
         var16 |= var13.changed(var8) ? 8388608 : 4194304;
      }

      if ((var15 & 256) != 0) {
         var16 |= 100663296;
      } else if ((var14 & 100663296) == 0) {
         var16 |= var13.changed(var10) ? 67108864 : 33554432;
      }

      if ((var14 & 805306368) == 0) {
         var16 |= (var15 & 512) == 0 && var13.changed(var11) ? 536870912 : 268435456;
      }

      if ((var16 & 306783379) == 306783378 && var13.getSkipping()) {
         var13.skipToGroupEnd();
      } else {
         var13.startDefaults();
         if ((var14 & 1) != 0 && !var13.getDefaultsInvalid()) {
            var13.skipToGroupEnd();
            if ((var15 & 512) != 0) {
               var16 &= -1879048193;
            }
         } else {
            if ((var15 & 32) != 0) {
               var5 = Color.Companion.recovered$235b30c090a399c5();
            }

            if ((var15 & 64) != 0) {
               int var17 = 1;
               int var18 = 0;
               var7 = Dp.recovered$95b3d103663bf060(var17);
            }

            if ((var15 & 128) != 0) {
               var8 = Color.Companion.recovered$95989221dad41c4d();
            }

            if ((var15 & 256) != 0) {
               var10 = FontWeight.Companion.getNormal();
            }

            if ((var15 & 512) != 0) {
               var11 = ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var8);
               var16 &= -1879048193;
            }
         }

         var13.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-933969743, var16, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.OtherOption (OtherOption.kt:39)");
         }

         byte var20 = 0;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var13, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var56 = Modifier.Companion;
         Vertical var57 = Arrangement.INSTANCE.getTop();
         Horizontal var19 = Alignment.Companion.getStart();
         MeasurePolicy var22 = ColumnKt.columnMeasurePolicy(var57, var19, var13, 14 & var20 >> 3 | 112 & var20 >> 3);
         Modifier var23 = var56;
         MeasurePolicy var24 = var22;
         Composer var25 = var13;
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
         ComposerKt.sourceInformationMarkerStart(var38, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var40 = 6 | 112 & var20 >> 6;
         Composer var41 = var38;
         ColumnScope var42 = ColumnScopeInstance.INSTANCE;
         int var43 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var41, -1588642030, "C48@1928L77,49@2031L15,41@1667L390,51@2066L40,52@2154L767,52@2115L806:OtherOption.kt#w8l19x"
         );
         String var44 = StringResources_androidKt.stringResource(string.intercom_surveys_multiselect_other_option_value, var41, 0);
         boolean var10000 = var0;
         var41.startReplaceGroup(-1990898758);
         ComposerKt.sourceInformation(var41, "CC(remember):OtherOption.kt#9igjgp");
         Composer var45 = var41;
         boolean var46 = (var16 & 7168) == 2048;
         int var47 = 0;
         Object var48 = var45.rememberedValue();
         int var49 = 0;
         Object var10001;
         if (!var46 && var48 != Composer.Companion.getEmpty()) {
            var10001 = var48;
         } else {
            boolean var50 = var0;
            int var51 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_1406_0 = var3;
            Function1 var52 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1406_0) -> OtherOptionKt.OtherOption_YCJL08c$lambda$2$lambda$1$lambda$0(recoveredCapture_1406_0, (java.lang.String)recoveredArgument_1406_0));
            var10000 = var50;
            Object var53 = var52;
            var45.updateRememberedValue(var53);
            var10001 = var53;
         }

         Function1 var54 = (Function1)var10001;
         var41.endReplaceGroup();
         ChoicePillKt.recovered$b36a01a674e93ba3(
            var10000,
            var54,
            var44,
            var5,
            var7,
            var8,
            var10,
            var11,
            var41,
            14 & var16 | 7168 & var16 >> 6 | 57344 & var16 >> 6 | 458752 & var16 >> 6 | 3670016 & var16 >> 6 | 29360128 & var16 >> 6,
            0
         );
         Modifier var60 = Modifier.Companion;
         int var58 = 8;
         int var59 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var60, Dp.recovered$95b3d103663bf060(var58)), var41, 6);
         AnimatedVisibilityKt.AnimatedVisibility(
            var42,
            var0,
            null,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-252181085, true, new OtherOptionKt$OtherOption$1$2(var1, var2, var4), var41, 54),
            var41,
            1572864 | 14 & var40 | 112 & var16 << 3,
            30
         );
         ComposerKt.sourceInformationMarkerEnd(var41);
         ComposerKt.sourceInformationMarkerEnd(var38);
         var32.endNode();
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var13);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var61 = var13.endRestartGroup();
      if (var61 != null) {
         final boolean recoveredCapture_1407_0 = var0;
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredCapture_1407_1 = var1;
         final java.lang.String recoveredCapture_1407_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_1407_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredCapture_1407_4 = var4;
         final long recoveredCapture_1407_5 = var5;
         final float recoveredCapture_1407_6 = var7;
         final long recoveredCapture_1407_7 = var8;
         final androidx.compose.ui.text.font.FontWeight recoveredCapture_1407_8 = var10;
         final long recoveredCapture_1407_9 = var11;
         final int recoveredCapture_1407_10 = var14;
         final int recoveredCapture_1407_11 = var15;
         var61.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1407_0, recoveredArgument_1407_1) -> OtherOptionKt.OtherOption_YCJL08c$lambda$3(recoveredCapture_1407_0, recoveredCapture_1407_1, recoveredCapture_1407_2, recoveredCapture_1407_3, recoveredCapture_1407_4, recoveredCapture_1407_5, recoveredCapture_1407_6, recoveredCapture_1407_7, recoveredCapture_1407_8, recoveredCapture_1407_9, recoveredCapture_1407_10, recoveredCapture_1407_11, (androidx.compose.runtime.Composer)recoveredArgument_1407_0, ((java.lang.Integer)recoveredArgument_1407_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void OtherOptionPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-469899921);
      ComposerKt.sourceInformation(var0, "C(OtherOptionPreview)75@3015L269:OtherOption.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-469899921, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.OtherOptionPreview (OtherOption.kt:74)");
         }

         Modifier var10000 = Modifier.Companion;
         int var2 = 16;
         int var3 = 0;
         SurfaceKt.recovered$ecd79a99e04a9534(
            PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var2)),
            null,
            0L,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableSingletons$OtherOptionKt.INSTANCE.recovered$eac478aa65045e5d(),
            var0,
            12582918,
            126
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var5 = var0.endRestartGroup();
      if (var5 != null) {
         final int recoveredCapture_1405_0 = var1;
         var5.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1405_0, recoveredArgument_1405_1) -> OtherOptionKt.OtherOptionPreview$lambda$4(recoveredCapture_1405_0, (androidx.compose.runtime.Composer)recoveredArgument_1405_0, ((java.lang.Integer)recoveredArgument_1405_1).intValue())));
      }
   }

   private static final Unit OtherOption_YCJL08c$lambda$2$lambda$1$lambda$0(Function0 var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit OtherOption_YCJL08c$lambda$3(
      boolean var0,
      SurveyUiColors var1,
      String var2,
      Function0 var3,
      Function1 var4,
      long var5,
      float var7,
      long var8,
      FontWeight var10,
      long var11,
      int var13,
      int var14,
      Composer var15,
      int var16
   ) {
      recovered$ceeec0b30e6e12f1(var0, var1, var2, var3, var4, var5, var7, var8, var10, var11, var15, RecomposeScopeImplKt.updateChangedFlags(var13 | 1), var14);
      return Unit.INSTANCE;
   }

   private static final Unit OtherOptionPreview$lambda$4(int var0, Composer var1, int var2) {
      OtherOptionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

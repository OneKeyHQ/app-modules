package io.intercom.android.sdk.m5.components;

import androidx.annotation.DrawableRes;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
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
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001aN\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0003\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0015\b\u0002\u0010\t\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\n¢\u0006\u0002\b\u000bH\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e¨\u0006\u0010",
   d2 = {
         "EmptyState",
         "",
         "title",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "description",
         "iconId",
         "",
         "bottomContent",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Ljava/lang/String;Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/Integer;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "EmptyStatePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "EmptyStateWithOutlinedButtonPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nEmptyState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EmptyState.kt\nio/intercom/android/sdk/m5/components/EmptyStateKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,107:1\n118#2:108\n118#2:137\n118#2:138\n118#2:139\n118#2:140\n87#3,6:109\n94#3:144\n81#4,6:115\n88#4,6:130\n96#4:143\n402#5,9:121\n411#5:136\n412#5,2:141\n*S KotlinDebug\n*F\n+ 1 EmptyState.kt\nio/intercom/android/sdk/m5/components/EmptyStateKt\n*L\n34#1:108\n44#1:137\n46#1:138\n55#1:139\n64#1:140\n32#1:109,6\n32#1:144\n32#1:115,6\n32#1:130,6\n32#1:143\n32#1:121,9\n32#1:136\n32#1:141,2\n*E\n"
)
public final class EmptyStateKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void EmptyState(
      @NotNull String var0,
      @Nullable Modifier var1,
      @Nullable String var2,
      @DrawableRes @Nullable Integer var3,
      @Nullable Function2 var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "title");
      var5 = var5.startRestartGroup(728499887);
      ComposerKt.sourceInformation(var5, "C(EmptyState)P(4,3,1,2)31@1166L1163:EmptyState.kt#arqjag");
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
         if ((var7 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var7 & 4) != 0) {
            var2 = null;
         }

         if ((var7 & 8) != 0) {
            var3 = null;
         }

         if ((var7 & 16) != 0) {
            var4 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(728499887, var8, -1, "io.intercom.android.sdk.m5.components.EmptyState (EmptyState.kt:30)");
         }

         int var10 = 24;
         int var11 = 0;
         Modifier var9 = SizeKt.fillMaxSize$default(PaddingKt.recovered$eb6a744cd98cdbca(var1, Dp.recovered$95b3d103663bf060(var10)), 0.0F, 1, null);
         Horizontal var53 = Alignment.Companion.getCenterHorizontally();
         HorizontalOrVertical var54 = Arrangement.INSTANCE.getCenter();
         short var12 = 432;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var5, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         MeasurePolicy var14 = ColumnKt.columnMeasurePolicy(var54, var53, var5, 14 & var12 >> 3 | 112 & var12 >> 3);
         Modifier var15 = var9;
         MeasurePolicy var16 = var14;
         Composer var17 = var5;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         ColumnScope var34 = ColumnScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 234966895, "C49@1770L10,51@1865L6,47@1704L191:EmptyState.kt#arqjag");
         var33.startReplaceGroup(-1239346792);
         ComposerKt.sourceInformation(var33, "*40@1442L24,42@1548L6,39@1410L221,45@1644L41");
         if (var3 != null) {
            int var36 = var3.intValue();
            int var37 = 0;
            Painter var38 = PainterResources_androidKt.painterResource(var36, var33, 0);
            long var39 = IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).getIcon().recovered$e01a1d3b7b68e21a();
            Modifier var10000 = Modifier.Companion;
            int $this$dp$ivx = 32;
            int $i$f$getDpx = 0;
            Modifier var43 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            IconKt.recovered$e00adffb7fe45b02(var38, null, var43, var39, var33, 432 | Painter.$stable, 0);
            var10000 = Modifier.Companion;
            int $this$dp$ivxx = 16;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var33, 6);
         }

         var33.endReplaceGroup();
         TextStyle var45 = IntercomTheme.INSTANCE.getTypography(var33, IntercomTheme.$stable).getType03();
         int var46 = TextAlign.Companion.recovered$d707b9dd64876179();
         long var47 = IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$5b114ab32a4f4190(
            var0,
            null,
            var47,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var46),
            0L,
            0,
            false,
            0,
            0,
            null,
            var45,
            var33,
            14 & var8,
            0,
            130042
         );
         var33.startReplaceGroup(-1239330006);
         ComposerKt.sourceInformation(var33, "*54@1935L40,57@2059L10,59@2162L6,55@1988L208");
         if (var2 != null) {
            String var55 = var2;
            int var57 = 0;
            Modifier var66 = Modifier.Companion;
            int $this$dp$ivx = 6;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var66, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var33, 6);
            TextStyle var61 = IntercomTheme.INSTANCE.getTypography(var33, IntercomTheme.$stable).getType04();
            $i$f$getDpx = TextAlign.Companion.recovered$d707b9dd64876179();
            long var50 = IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
            TextKt.recovered$5b114ab32a4f4190(
               var55,
               null,
               var50,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               TextAlign.recovered$18ab2fc4c81b19cd($i$f$getDpx),
               0L,
               0,
               false,
               0,
               0,
               null,
               var61,
               var33,
               0,
               0,
               130042
            );
         }

         var33.endReplaceGroup();
         var33.startReplaceGroup(-1239320186);
         ComposerKt.sourceInformation(var33, "*63@2248L41,64@2305L8");
         if (var4 != null) {
            Function2 var56 = var4;
            int var58 = 0;
            Modifier var67 = Modifier.Companion;
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var67, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var33, 6);
            var56.invoke(var33, 0);
         }

         var33.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var5);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var68 = var5.endRestartGroup();
      if (var68 != null) {
         final java.lang.String recoveredCapture_196_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_196_1 = var1;
         final java.lang.String recoveredCapture_196_2 = var2;
         final java.lang.Integer recoveredCapture_196_3 = var3;
         final kotlin.jvm.functions.Function2 recoveredCapture_196_4 = var4;
         final int recoveredCapture_196_5 = var6;
         final int recoveredCapture_196_6 = var7;
         var68.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_196_0, recoveredArgument_196_1) -> EmptyStateKt.EmptyState$lambda$4(recoveredCapture_196_0, recoveredCapture_196_1, recoveredCapture_196_2, recoveredCapture_196_3, recoveredCapture_196_4, recoveredCapture_196_5, recoveredCapture_196_6, (androidx.compose.runtime.Composer)recoveredArgument_196_0, ((java.lang.Integer)recoveredArgument_196_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EmptyStatePreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1885515198);
      ComposerKt.sourceInformation(var0, "C(EmptyStatePreview)72@2401L506:EmptyState.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1885515198, var1, -1, "io.intercom.android.sdk.m5.components.EmptyStatePreview (EmptyState.kt:71)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$EmptyStateKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_197_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_197_0, recoveredArgument_197_1) -> EmptyStateKt.EmptyStatePreview$lambda$5(recoveredCapture_197_0, (androidx.compose.runtime.Composer)recoveredArgument_197_0, ((java.lang.Integer)recoveredArgument_197_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EmptyStateWithOutlinedButtonPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-484307592);
      ComposerKt.sourceInformation(var0, "C(EmptyStateWithOutlinedButtonPreview)91@2997L506:EmptyState.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-484307592, var1, -1, "io.intercom.android.sdk.m5.components.EmptyStateWithOutlinedButtonPreview (EmptyState.kt:90)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$EmptyStateKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_198_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_198_0, recoveredArgument_198_1) -> EmptyStateKt.EmptyStateWithOutlinedButtonPreview$lambda$6(recoveredCapture_198_0, (androidx.compose.runtime.Composer)recoveredArgument_198_0, ((java.lang.Integer)recoveredArgument_198_1).intValue())));
      }
   }

   private static final Unit EmptyState$lambda$4(
      String var0, Modifier var1, String var2, Integer var3, Function2 var4, int var5, int var6, Composer var7, int var8
   ) {
      EmptyState(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit EmptyStatePreview$lambda$5(int var0, Composer var1, int var2) {
      EmptyStatePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EmptyStateWithOutlinedButtonPreview$lambda$6(int var0, Composer var1, int var2) {
      EmptyStateWithOutlinedButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

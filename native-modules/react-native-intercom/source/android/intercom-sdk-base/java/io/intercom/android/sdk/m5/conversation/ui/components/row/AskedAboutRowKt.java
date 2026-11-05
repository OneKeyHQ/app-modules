package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import android.content.Intent;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$Companion;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u001f\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\t",
   d2 = {
         "AskedAboutRow",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/models/Part;Landroidx/compose/runtime/Composer;II)V",
         "AskedAboutRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nAskedAboutRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AskedAboutRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/AskedAboutRowKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,94:1\n75#2:95\n118#3:96\n118#3:129\n87#4:97\n84#4,9:98\n94#4:139\n81#5,6:107\n88#5,6:122\n96#5:138\n402#6,9:113\n411#6:128\n412#6,2:136\n1047#7,6:130\n*S KotlinDebug\n*F\n+ 1 AskedAboutRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/AskedAboutRowKt\n*L\n28#1:95\n30#1:96\n40#1:129\n29#1:97\n29#1:98,9\n29#1:139\n29#1:107,6\n29#1:122,6\n29#1:138\n29#1:113,9\n29#1:128\n29#1:136,2\n45#1:130,6\n*E\n"
)
public final class AskedAboutRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AskedAboutRow(@Nullable Modifier var0, @NotNull Part var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "part");
      var2 = var2.startRestartGroup(1277406973);
      ComposerKt.sourceInformation(var2, "C(AskedAboutRow)27@1251L7,28@1263L1611:AskedAboutRow.kt#hr3bi5");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1277406973, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.AskedAboutRow (AskedAboutRow.kt:26)");
         }

         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var2.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Context var6 = (Context)var10;
         int var52 = 16;
         var8 = 0;
         Modifier var53 = PaddingKt.recovered$f3b9f2e94c07c806(var0, Dp.recovered$95b3d103663bf060(var52), 0.0F, 2, null);
         byte var57 = 0;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var55 = Arrangement.INSTANCE.getTop();
         Horizontal var56 = Alignment.Companion.getStart();
         MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var55, var56, var2, 14 & var57 >> 3 | 112 & var57 >> 3);
         Modifier var13 = var53;
         MeasurePolicy var14 = var12;
         Composer var15 = var2;
         int var16 = 112 & var57 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var15;
         int var23 = 6 | 896 & var16 << 6;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var22.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var22.startReusableNode();
         if (var22.getInserting()) {
            var22.createNode(var21);
         } else {
            var22.useNode();
         }

         Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
         int var26 = 0;
         Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var30 = 6 | 112 & var57 >> 6;
         Composer var31 = var28;
         ColumnScope var32 = ColumnScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -821357066, "C32@1369L50,33@1455L10,35@1569L6,31@1344L394:AskedAboutRow.kt#hr3bi5");
         String var34 = StringResources_androidKt.stringResource(string.intercom_asked_about, var31, 0);
         TextStyle var35 = IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04Point5();
         int $this$dp$ivx = TextAlign.Companion.recovered$d707b9dd64876179();
         long var37 = IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         TextStyle var39 = TextStyle.recovered$cf01d8b71690b5c5(
            var35,
            var37,
            0L,
            null,
            null,
            null,
            null,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            null,
            $this$dp$ivx,
            0,
            0L,
            null,
            null,
            null,
            0,
            0,
            null,
            16744446,
            null
         );
         Modifier var10000 = var32.align(Modifier.Companion, Alignment.Companion.getCenterHorizontally());
         $this$dp$ivx = 8;
         int var40 = 0;
         Modifier var59 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 7, null);
         TextKt.recovered$5b114ab32a4f4190(var34, var59, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var39, var31, 0, 0, 131068);
         List var63 = var1.getBlocks();
         Intrinsics.checkNotNullExpressionValue(var63, "getBlocks(...)");
         Block var58 = (Block)CollectionsKt.firstOrNull(var63);
         var31.startReplaceGroup(-1827597377);
         ComposerKt.sourceInformation(var31, "*56@2465L23,44@1884L537,57@2504L354,42@1792L1066");
         if (var58 != null) {
            Block var61 = var58;
            var40 = 0;
            Modifier var41 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            Style var42 = IntercomCardStyle.INSTANCE.recovered$b8822edb5fda39fd(null, 0L, 0L, 0.0F, null, var31, IntercomCardStyle.$stable << 15, 31);
            var31.startReplaceGroup(1374196689);
            ComposerKt.sourceInformation(var31, "CC(remember):AskedAboutRow.kt#9igjgp");
            Composer var43 = var31;
            boolean var44 = var31.changedInstance(var6) | var31.changedInstance(var61);
            int var45 = 0;
            Object var46 = var43.rememberedValue();
            int var47 = 0;
            Object var64;
            if (!var44 && var46 != Composer.Companion.getEmpty()) {
               var64 = var46;
            } else {
               int var48 = 0;
               final android.content.Context recoveredCapture_646_0 = var6;
               final io.intercom.android.sdk.blocks.lib.models.Block recoveredCapture_646_1 = var61;
               Object var49 = ((kotlin.jvm.functions.Function0) () -> AskedAboutRowKt.AskedAboutRow$lambda$3$lambda$2$lambda$1$lambda$0(recoveredCapture_646_0, recoveredCapture_646_1));
               var43.updateRememberedValue(var49);
               var64 = var49;
            }

            Function0 var50 = (Function0)var64;
            var31.endReplaceGroup();
            IntercomCardKt.IntercomCard(
               var50,
               var41,
               false,
               var42,
               null,
               ComposableLambdaKt.rememberComposableLambda(-1866574392, true, new AskedAboutRowKt$AskedAboutRow$1$1$2(var61), var31, 54),
               var31,
               196656 | Style.$stable << 9,
               20
            );
         }

         var31.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var65 = var2.endRestartGroup();
      if (var65 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_647_0 = var0;
         final io.intercom.android.sdk.models.Part recoveredCapture_647_1 = var1;
         final int recoveredCapture_647_2 = var3;
         final int recoveredCapture_647_3 = var4;
         var65.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_647_0, recoveredArgument_647_1) -> AskedAboutRowKt.AskedAboutRow$lambda$4(recoveredCapture_647_0, recoveredCapture_647_1, recoveredCapture_647_2, recoveredCapture_647_3, (androidx.compose.runtime.Composer)recoveredArgument_647_0, ((java.lang.Integer)recoveredArgument_647_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AskedAboutRowPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(97963709);
      ComposerKt.sourceInformation(var0, "C(AskedAboutRowPreview)75@2950L604:AskedAboutRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               97963709, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.AskedAboutRowPreview (AskedAboutRow.kt:74)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AskedAboutRowKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_648_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_648_0, recoveredArgument_648_1) -> AskedAboutRowKt.AskedAboutRowPreview$lambda$5(recoveredCapture_648_0, (androidx.compose.runtime.Composer)recoveredArgument_648_0, ((java.lang.Integer)recoveredArgument_648_1).intValue())));
      }
   }

   private static final Unit AskedAboutRow$lambda$3$lambda$2$lambda$1$lambda$0(Context var0, Block var1) {
      IntercomArticleActivity$Companion var10000 = IntercomArticleActivity.Companion;
      String var10004 = var1.getArticleId();
      Intrinsics.checkNotNullExpressionValue(var10004, "getArticleId(...)");
      Intent var2 = var10000.buildIntent(var0, new IntercomArticleActivity$ArticleActivityArguments(var10004, "conversation", false, true));
      var0.startActivity(var2);
      return Unit.INSTANCE;
   }

   private static final Unit AskedAboutRow$lambda$4(Modifier var0, Part var1, int var2, int var3, Composer var4, int var5) {
      AskedAboutRow(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit AskedAboutRowPreview$lambda$5(int var0, Composer var1, int var2) {
      AskedAboutRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

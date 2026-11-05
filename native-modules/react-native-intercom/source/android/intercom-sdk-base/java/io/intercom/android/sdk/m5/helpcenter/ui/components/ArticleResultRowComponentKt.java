package io.intercom.android.sdk.m5.helpcenter.ui.components;

import android.content.Context;
import android.text.Spanned;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
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
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import androidx.core.text.HtmlCompat;
import io.intercom.android.sdk.helpcenter.search.ArticleSearchResultRow$ArticleResultRow;
import io.intercom.android.sdk.helpcenter.search.HighlightTagHandler;
import io.intercom.android.sdk.m5.components.IntercomChevronKt;
import io.intercom.android.sdk.survey.block.BlockExtensionsKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
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
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a3\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\u0010\t\u001a\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0006H\u0002\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e¨\u0006\u000f",
   d2 = {
         "ArticleResultRowComponent",
         "",
         "item",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchResultRow$ArticleResultRow;",
         "onClick",
         "Lkotlin/Function1;",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/helpcenter/search/ArticleSearchResultRow$ArticleResultRow;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "highlightedText",
         "Landroid/text/Spanned;",
         "rawText",
         "ArticleRowComponentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nArticleResultRowComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArticleResultRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ArticleResultRowComponentKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 8 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,100:1\n1047#2,6:101\n118#3:107\n118#3:108\n118#3:174\n118#3:179\n99#4:109\n96#4,9:110\n106#4:183\n81#5,6:119\n88#5,6:134\n81#5,6:151\n88#5,6:166\n96#5:177\n96#5:182\n402#6,9:125\n411#6:140\n402#6,9:157\n411#6:172\n412#6,2:175\n412#6,2:180\n87#7:141\n84#7,9:142\n94#7:178\n75#8:173\n*S KotlinDebug\n*F\n+ 1 ArticleResultRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ArticleResultRowComponentKt\n*L\n38#1:101,6\n39#1:107\n40#1:108\n56#1:174\n67#1:179\n35#1:109\n35#1:110,9\n35#1:183\n35#1:119,6\n35#1:134,6\n43#1:151,6\n43#1:166,6\n43#1:177\n35#1:182\n35#1:125,9\n35#1:140\n43#1:157,9\n43#1:172\n43#1:175,2\n35#1:180,2\n43#1:141\n43#1:142,9\n43#1:178\n46#1:173\n*E\n"
)
public final class ArticleResultRowComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ArticleResultRowComponent(
      @NotNull ArticleSearchResultRow$ArticleResultRow var0, @NotNull Function1 var1, @Nullable Modifier var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "item");
      Intrinsics.checkNotNullParameter(var1, "onClick");
      var3 = var3.startRestartGroup(-506618245);
      ComposerKt.sourceInformation(var3, "C(ArticleResultRowComponent)P(!1,2)37@1497L20,34@1413L1278:ArticleResultRowComponent.kt#ngglre");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -506618245, var6, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.ArticleResultRowComponent (ArticleResultRowComponent.kt:33)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var2, 0.0F, 1, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var3.startReplaceGroup(-866808730);
         ComposerKt.sourceInformation(var3, "CC(remember):ArticleResultRowComponent.kt#9igjgp");
         Composer var8 = var3;
         boolean var9 = (var6 & 112) == 32 | (var6 & 14) == 4;
         int var10 = 0;
         Object var11 = var8.rememberedValue();
         int var12 = 0;
         Object var10005;
         if (!var9 && var11 != Composer.Companion.getEmpty()) {
            var10005 = var11;
         } else {
            Object var72 = null;
            Object var71 = null;
            Object var70 = null;
            boolean var69 = false;
            Modifier var68 = var10000;
            int var13 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_853_0 = var1;
            final io.intercom.android.sdk.helpcenter.search.ArticleSearchResultRow$ArticleResultRow recoveredCapture_853_1 = var0;
            Function0 var73 = ((kotlin.jvm.functions.Function0) () -> ArticleResultRowComponentKt.ArticleResultRowComponent$lambda$1$lambda$0(recoveredCapture_853_0, recoveredCapture_853_1));
            var10000 = var68;
            var10001 = var69;
            var10002 = var70;
            var10003 = var71;
            var10004 = var72;
            Object var14 = var73;
            var8.updateRememberedValue(var14);
            var10005 = var14;
         }

         Function0 var7 = (Function0)var10005;
         var3.endReplaceGroup();
         var10000 = ClickableKt.recovered$1c9660c41fd850dc(
            var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var7, 15, null
         );
         int var75 = 16;
         int var78 = 0;
         var10000 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var75), 1, null);
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var77 = PaddingKt.recovered$03b481d31d62885a(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 0.0F, 0.0F, 14, null);
         Vertical var81 = Alignment.Companion.getCenterVertically();
         short var82 = 384;
         var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var80 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var84 = RowKt.rowMeasurePolicy(var80, var81, var3, 14 & var82 >> 3 | 112 & var82 >> 3);
         Modifier var85 = var77;
         MeasurePolicy var86 = var84;
         Composer var15 = var3;
         int var16 = 112 & var82 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var85);
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
         Updater.recovered$89682b199a142880(var25, var86, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var30 = 6 | 112 & var82 >> 6;
         Composer var31 = var28;
         RowScope var32 = RowScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1651564823, "C42@1666L946,66@2621L64:ArticleResultRowComponent.kt#ngglre");
         Modifier var34 = RowScope.weight$default(var32, Modifier.Companion, 1.0F, false, 2, null);
         byte var35 = 0;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Vertical var37 = Arrangement.INSTANCE.getTop();
         androidx.compose.ui.Alignment.Horizontal var38 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var37, var38, var31, 14 & var35 >> 3 | 112 & var35 >> 3);
         Modifier var40 = var34;
         MeasurePolicy var41 = measurePolicy$ivx;
         Composer var42 = var31;
         int var43 = 112 & var35 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var42, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var42, 0));
         CompositionLocalMap localMap$iv$ivx = var42.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var42, var40);
         Function0 var48 = ComposeUiNode.Companion.getConstructor();
         Composer var49 = var42;
         int var50 = 6 | 896 & var43 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var49, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var49.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var49.startReusableNode();
         if (var49.getInserting()) {
            var49.createNode(var48);
         } else {
            var49.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var49);
         int var53/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var41, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var54 = 14 & var50 >> 6;
         Composer $composer$ivx = var49;
         int var56 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var57 = 6 | 112 & var35 >> 6;
         Composer var58 = $composer$ivx;
         ColumnScope var59 = ColumnScopeInstance.INSTANCE;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart(var58, -1549237321, "C45@1769L7,50@1955L10,52@2062L6,46@1789L307:ArticleResultRowComponent.kt#ngglre");
         CompositionLocal var61 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var62 = 0;
         int var63 = 0;
         ComposerKt.sourceInformationMarkerStart(var58, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var64 = var58.consume(var61);
         ComposerKt.sourceInformationMarkerEnd(var58);
         Context var65 = (Context)var64;
         AnnotatedString var89 = BlockExtensionsKt.recovered$c71786fddfec5215(highlightedText(var0.getTitleText()), var65, null, 0L, 6, null);
         TextStyle var92 = IntercomTheme.INSTANCE.getTypography(var58, IntercomTheme.$stable).getType04();
         var63 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         long var66 = IntercomTheme.INSTANCE.getColors(var58, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$489343e57da77a35(
            var89, null, var66, null, 0L, null, null, null, 0L, null, null, 0L, var63, false, 0, 0, null, null, var92, var58, 0, 384, 258042
         );
         var58.startReplaceGroup(1612603965);
         ComposerKt.sourceInformation(var58, "55@2204L40,60@2445L10,56@2261L327");
         if (var0.getSummaryVisibility() == 0 && !StringsKt.isBlank(var0.getSummaryText())) {
            int $this$dp$ivxx = 4;
            var62 = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var2, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var58, 0);
            AnnotatedString var91 = BlockExtensionsKt.recovered$c71786fddfec5215(highlightedText(var0.getSummaryText()), var65, null, 0L, 6, null);
            TextStyle var94 = IntercomTheme.INSTANCE.getTypography(var58, IntercomTheme.$stable).getType04Point5();
            var63 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            var66 = ColorKt.Color(4286611584L);
            TextKt.recovered$489343e57da77a35(
               var91, null, var66, null, 0L, null, null, null, 0L, null, null, 0L, var63, false, 0, 0, null, null, var94, var58, 384, 384, 258042
            );
         }

         var58.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var58);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var49.endNode();
         ComposerKt.sourceInformationMarkerEnd(var49);
         ComposerKt.sourceInformationMarkerEnd(var42);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var10000 = Modifier.Companion;
         int $this$dp$ivxx = 22;
         int $i$f$getDpxx = 0;
         IntercomChevronKt.IntercomChevron(
            PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 2, null), var31, 6, 0
         );
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var101 = var3.endRestartGroup();
      if (var101 != null) {
         final io.intercom.android.sdk.helpcenter.search.ArticleSearchResultRow$ArticleResultRow recoveredCapture_852_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredCapture_852_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_852_2 = var2;
         final int recoveredCapture_852_3 = var4;
         final int recoveredCapture_852_4 = var5;
         var101.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_852_0, recoveredArgument_852_1) -> ArticleResultRowComponentKt.ArticleResultRowComponent$lambda$4(recoveredCapture_852_0, recoveredCapture_852_1, recoveredCapture_852_2, recoveredCapture_852_3, recoveredCapture_852_4, (androidx.compose.runtime.Composer)recoveredArgument_852_0, ((java.lang.Integer)recoveredArgument_852_1).intValue())));
      }
   }

   private static final Spanned highlightedText(String var0) {
      String var1 = "&zwj;";
      HighlightTagHandler var2 = new HighlightTagHandler();
      Spanned var10000 = HtmlCompat.fromHtml(var1 + var0, 0, null, var2);
      Intrinsics.checkNotNullExpressionValue(var10000, "fromHtml(...)");
      return var10000;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ArticleRowComponentPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(387759007);
      ComposerKt.sourceInformation(var0, "C(ArticleRowComponentPreview)86@3251L761:ArticleResultRowComponent.kt#ngglre");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               387759007, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.ArticleRowComponentPreview (ArticleResultRowComponent.kt:85)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ArticleResultRowComponentKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_854_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_854_0, recoveredArgument_854_1) -> ArticleResultRowComponentKt.ArticleRowComponentPreview$lambda$5(recoveredCapture_854_0, (androidx.compose.runtime.Composer)recoveredArgument_854_0, ((java.lang.Integer)recoveredArgument_854_1).intValue())));
      }
   }

   private static final Unit ArticleResultRowComponent$lambda$1$lambda$0(Function1 var0, ArticleSearchResultRow$ArticleResultRow var1) {
      var0.invoke(var1.getId());
      return Unit.INSTANCE;
   }

   private static final Unit ArticleResultRowComponent$lambda$4(
      ArticleSearchResultRow$ArticleResultRow var0, Function1 var1, Modifier var2, int var3, int var4, Composer var5, int var6
   ) {
      ArticleResultRowComponent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit ArticleRowComponentPreview$lambda$5(int var0, Composer var1, int var2) {
      ArticleRowComponentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

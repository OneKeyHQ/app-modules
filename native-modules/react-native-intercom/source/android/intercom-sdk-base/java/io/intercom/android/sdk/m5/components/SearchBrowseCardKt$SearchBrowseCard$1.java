package io.intercom.android.sdk.m5.components;

import android.content.Context;
import android.content.Intent;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.Modifier.Companion;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments;
import io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity;
import io.intercom.android.sdk.m5.home.data.SuggestedArticle;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeHelpCenterData;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSearchBrowseCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SearchBrowseCard.kt\nio/intercom/android/sdk/m5/components/SearchBrowseCardKt$SearchBrowseCard$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 7 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 8 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 9 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,270:1\n87#2:271\n83#2,10:272\n87#2:385\n83#2,10:386\n94#2:478\n94#2:483\n81#3,6:282\n88#3,6:297\n81#3,6:324\n88#3,6:339\n81#3,6:354\n88#3,6:369\n96#3:379\n96#3:383\n81#3,6:396\n88#3,6:411\n81#3,6:446\n88#3,6:461\n96#3:471\n96#3:477\n96#3:482\n402#4,9:288\n411#4:303\n402#4,9:330\n411#4:345\n402#4,9:360\n411#4:375\n412#4,2:377\n412#4,2:381\n402#4,9:402\n411#4:417\n402#4,9:452\n411#4:467\n412#4,2:469\n412#4,2:475\n412#4,2:480\n118#5:304\n118#5:305\n118#5:306\n118#5:307\n118#5:346\n118#5:347\n118#5:376\n118#5:426\n118#5:433\n118#5:434\n118#5:435\n118#5:468\n118#5:473\n118#5:479\n1047#6,6:308\n1047#6,6:418\n1047#6,6:427\n70#7:314\n67#7,9:315\n77#7:384\n99#8,6:348\n106#8:380\n99#8:436\n96#8,9:437\n106#8:472\n1878#9,2:424\n1880#9:474\n*S KotlinDebug\n*F\n+ 1 SearchBrowseCard.kt\nio/intercom/android/sdk/m5/components/SearchBrowseCardKt$SearchBrowseCard$1\n*L\n60#1:271\n60#1:272,10\n113#1:385\n113#1:386,10\n113#1:478\n60#1:483\n60#1:282,6\n60#1:297,6\n61#1:324,6\n61#1:339,6\n86#1:354,6\n86#1:369,6\n86#1:379\n61#1:383\n113#1:396,6\n113#1:411,6\n126#1:446,6\n126#1:461,6\n126#1:471\n113#1:477\n60#1:482\n60#1:288,9\n60#1:303\n61#1:330,9\n61#1:345\n86#1:360,9\n86#1:375\n86#1:377,2\n61#1:381,2\n113#1:402,9\n113#1:417\n126#1:452,9\n126#1:467\n126#1:469,2\n113#1:475,2\n60#1:480,2\n64#1:304\n65#1:305\n66#1:306\n67#1:307\n90#1:346\n91#1:347\n103#1:376\n124#1:426\n143#1:433\n144#1:434\n145#1:435\n157#1:468\n159#1:473\n166#1:479\n77#1:308,6\n117#1:418,6\n129#1:427,6\n61#1:314\n61#1:315,9\n61#1:384\n86#1:348,6\n86#1:380\n126#1:436\n126#1:437,9\n126#1:472\n122#1:424,2\n122#1:474\n*E\n"
)
final class SearchBrowseCardKt$SearchBrowseCard$1 implements Function3 {
   // $VF: synthetic field
   final boolean $isSearchFirstEnabled;
   // $VF: synthetic field
   final io.intercom.android.sdk.m5.home.data.HomeCards$HomeHelpCenterData $helpCenterData;
   // $VF: synthetic field
   final MetricTracker $metricTracker;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final boolean $accessToTeammateEnabled;
   // $VF: synthetic field
   final List $avatars;

   SearchBrowseCardKt$SearchBrowseCard$1(boolean var1, io.intercom.android.sdk.m5.home.data.HomeCards$HomeHelpCenterData var2, MetricTracker var3, Context var4, boolean var5, List var6) {
      this.$isSearchFirstEnabled = var1;
      this.$helpCenterData = var2;
      this.$metricTracker = var3;
      this.$context = var4;
      this.$accessToTeammateEnabled = var5;
      this.$avatars = var6;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C59@2601L5233:SearchBrowseCard.kt#arqjag");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1020132823, var3, -1, "io.intercom.android.sdk.m5.components.SearchBrowseCard.<anonymous> (SearchBrowseCard.kt:57)");
         }

         boolean var4 = this.$isSearchFirstEnabled || !this.$helpCenterData.getSuggestedArticles().isEmpty();
         MetricTracker var8 = this.$metricTracker;
         Context var9 = this.$context;
         io.intercom.android.sdk.m5.home.data.HomeCards$HomeHelpCenterData var10 = this.$helpCenterData;
         boolean var11 = this.$isSearchFirstEnabled;
         boolean var12 = this.$accessToTeammateEnabled;
         List var13 = this.$avatars;
         int var14 = 0;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var5 = Modifier.Companion;
         Vertical var6 = Arrangement.INSTANCE.getTop();
         Horizontal var7 = Alignment.Companion.getStart();
         MeasurePolicy var16 = ColumnKt.columnMeasurePolicy(var6, var7, var2, 14 & var14 >> 3 | 112 & var14 >> 3);
         Modifier var17 = var5;
         MeasurePolicy var18 = var16;
         Composer var19 = var2;
         int var20 = 112 & var14 << 3;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var19, 0));
         CompositionLocalMap var23 = var19.getCurrentCompositionLocalMap();
         Modifier var24 = ComposedModifierKt.materializeModifier(var19, var17);
         Function0 var25 = ComposeUiNode.Companion.getConstructor();
         Composer var26 = var19;
         int var27 = 6 | 896 & var20 << 6;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var26.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var26.startReusableNode();
         if (var26.getInserting()) {
            var26.createNode(var25);
         } else {
            var26.useNode();
         }

         Composer var29 = Updater.recovered$676aa17b44fd6280(var26);
         int var30 = 0;
         Updater.recovered$89682b199a142880(var29, var18, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var29, var23, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var29, var22, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var29, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var29, var24, ComposeUiNode.Companion.getSetModifier());
         int var31 = 14 & var27 >> 6;
         Composer var32 = var26;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var34 = 6 | 112 & var14 >> 6;
         Composer var35 = var32;
         ColumnScope var36 = ColumnScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 2061813963, "C68@3012L6,76@3352L349,60@2622L2278:SearchBrowseCard.kt#arqjag");
         Companion var38 = Modifier.Companion;
         float var10000;
         if (var4) {
            int var39 = 8;
            int var40 = 0;
            var10000 = Dp.recovered$95b3d103663bf060(var39);
         } else {
            int var126 = 0;
            int var132 = 0;
            var10000 = Dp.recovered$95b3d103663bf060(var126);
         }

         float var41 = var10000;
         int var133 = 0;
         int var42 = 0;
         float var127 = Dp.recovered$95b3d103663bf060(var133);
         float var247;
         if (var4) {
            int $this$dp$ivx = 8;
            int $i$f$getDpx = 0;
            var247 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 0;
            int $i$f$getDpx = 0;
            var247 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         float var134 = var247;
         float var248;
         if (var4) {
            int $this$dp$ivx = 8;
            int $i$f$getDpx = 0;
            var248 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 0;
            int $i$f$getDpx = 0;
            var248 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         float var144 = var248;
         Modifier var249 = ClipKt.clip(
            PaddingKt.recovered$f39cb96e94c47cf6(var38, var134, var41, var144, var127),
            IntercomTheme.INSTANCE.getShapes(var35, IntercomTheme.$stable).getSmall()
         );
         var35.startReplaceGroup(343614556);
         ComposerKt.sourceInformation(var35, "71@3153L6");
         long var45 = var4
            ? Color.recovered$41aa0ee03c2282fb(
               IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.05F, 0.0F, 0.0F, 0.0F, 14, null
            )
            : Color.Companion.recovered$562f21ab17d9b4c7();
         var35.endReplaceGroup();
         Modifier var250 = BackgroundKt.recovered$42a3174180b3fb7a(var249, var45, null, 2, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var35.startReplaceGroup(343623299);
         ComposerKt.sourceInformation(var35, "CC(remember):SearchBrowseCard.kt#9igjgp");
         Composer var138 = var35;
         boolean var128 = var35.changedInstance(var8) | var35.changedInstance(var9);
         int var135 = 0;
         Object var145 = var138.rememberedValue();
         int var151 = 0;
         Object var10005;
         if (!var128 && var145 != Composer.Companion.getEmpty()) {
            var10005 = var145;
         } else {
            Object var47 = null;
            Object var48 = null;
            Object var49 = null;
            boolean var50 = false;
            Modifier var51 = var250;
            int var155 = 0;
            final io.intercom.android.sdk.metrics.MetricTracker recoveredExpressionCapture_241_0 = var8;
            final android.content.Context recoveredExpressionCapture_241_1 = var9;
            Function0 var52 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.components.SearchBrowseCardKt$SearchBrowseCard$1.invoke$lambda$10$lambda$1$lambda$0(recoveredExpressionCapture_241_0, recoveredExpressionCapture_241_1));
            var250 = var51;
            var10001 = var50;
            var10002 = var49;
            var10003 = var48;
            var10004 = var47;
            Object var53 = var52;
            var138.updateRememberedValue(var53);
            var10005 = var53;
         }

         Function0 var123 = (Function0)var10005;
         var35.endReplaceGroup();
         Modifier var124 = ClickableKt.recovered$1c9660c41fd850dc(
            var250, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var123, 15, null
         );
         byte var136 = 0;
         int var146 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var35, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var139 = Alignment.Companion.getTopStart();
         boolean var129 = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var139, var129);
         Modifier var156 = var124;
         MeasurePolicy var158 = measurePolicy$ivx;
         Composer var54 = var35;
         int var55 = 112 & var136 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var54, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var54, 0));
         CompositionLocalMap localMap$iv$ivx = var54.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var54, var156);
         Function0 var60 = ComposeUiNode.Companion.getConstructor();
         Composer var61 = var54;
         int var62 = 6 | 896 & var55 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var61, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var61.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var61.startReusableNode();
         if (var61.getInserting()) {
            var61.createNode(var60);
         } else {
            var61.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var61);
         int var65/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var158, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var66 = 14 & var62 >> 6;
         Composer $composer$ivx = var61;
         int var68 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var69 = 6 | 112 & var136 >> 6;
         Composer var70 = $composer$ivx;
         BoxScope var71 = BoxScopeInstance.INSTANCE;
         int var72 = 0;
         ComposerKt.sourceInformationMarkerStart(var70, -266232321, "C85@3735L1151:SearchBrowseCard.kt#arqjag");
         Modifier var251 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         float var261;
         if (var4) {
            int $this$dp$ivx = 8;
            int $i$f$getDpx = 0;
            var261 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            var261 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         float var266;
         if (var4) {
            int $this$dp$ivx = 12;
            int $i$f$getDpx = 0;
            var266 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 20;
            int $i$f$getDpx = 0;
            var266 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         Modifier var75 = PaddingKt.recovered$904403cea4c49d95(var251, var261, var266);
         androidx.compose.ui.Alignment.Vertical var182 = Alignment.Companion.getCenterVertically();
         HorizontalOrVertical var187 = Arrangement.INSTANCE.getSpaceBetween();
         short var76 = 432;
         int var77 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var70, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var187, var182, var70, 14 & var76 >> 3 | 112 & var76 >> 3);
         Modifier var79 = var75;
         MeasurePolicy var80 = measurePolicy$ivxx;
         Composer var81 = var70;
         int var82 = 112 & var76 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var81, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var81, 0));
         CompositionLocalMap localMap$iv$ivxx = var81.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var81, var79);
         Function0 var87 = ComposeUiNode.Companion.getConstructor();
         Composer var88 = var81;
         int var89 = 6 | 896 & var82 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var88, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var88.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var88.startReusableNode();
         if (var88.getInserting()) {
            var88.createNode(var87);
         } else {
            var88.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var88);
         int var92/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var80, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var93 = 14 & var89 >> 6;
         Composer $composer$ivxx = var88;
         int var95 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var96 = 6 | 112 & var76 >> 6;
         Composer var97 = $composer$ivxx;
         RowScope var98 = RowScopeInstance.INSTANCE;
         int var99 = 0;
         ComposerKt.sourceInformationMarkerStart(var97, 1658208081, "C96@4251L54,95@4214L172,104@4671L28,105@4746L6,100@4489L379:SearchBrowseCard.kt#arqjag");
         TextKt.recovered$5b114ab32a4f4190(
            StringResources_androidKt.stringResource(string.intercom_search_for_help, var97, 0),
            null,
            0L,
            null,
            0L,
            null,
            FontWeight.Companion.getSemiBold(),
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
            var97,
            1572864,
            0,
            262078
         );
         int var100 = drawable.intercom_gif_search_icon;
         Modifier var252 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var103 = TestTagKt.testTag(SizeKt.recovered$111ef94c93e78c04(var252, Dp.recovered$95b3d103663bf060($this$dp$ivx)), String.valueOf(var100));
         Painter var241 = PainterResources_androidKt.painterResource(var100, var97, 0);
         long var104 = IntercomTheme.INSTANCE.getColors(var97, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
         IconKt.recovered$e00adffb7fe45b02(var241, null, var103, var104, var97, 48 | Painter.$stable, 0);
         ComposerKt.sourceInformationMarkerEnd(var97);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var88.endNode();
         ComposerKt.sourceInformationMarkerEnd(var88);
         ComposerKt.sourceInformationMarkerEnd(var81);
         ComposerKt.sourceInformationMarkerEnd(var70);
         ComposerKt.sourceInformationMarkerEnd(var70);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var61.endNode();
         ComposerKt.sourceInformationMarkerEnd(var61);
         ComposerKt.sourceInformationMarkerEnd(var54);
         ComposerKt.sourceInformationMarkerEnd(var35);
         List var125 = var10.getSuggestedArticles();
         var35.startReplaceGroup(343677646);
         ComposerKt.sourceInformation(var35, "112@5038L2435");
         if (!var125.isEmpty()) {
            byte var147 = 0;
            int $i$f$Columnx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var35, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            Modifier modifier$ivx = Modifier.Companion;
            Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
            Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
            MeasurePolicy measurePolicy$ivxxx = ColumnKt.columnMeasurePolicy(
               verticalArrangement$ivx, horizontalAlignment$ivx, var35, 14 & var147 >> 3 | 112 & var147 >> 3
            );
            Modifier var159 = modifier$ivx;
            MeasurePolicy var160 = measurePolicy$ivxxx;
            Composer var161 = var35;
            int var162 = 112 & var147 << 3;
            int $i$f$Layoutxxx = 0;
            ComposerKt.sourceInformationMarkerStart(var161, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var161, 0));
            CompositionLocalMap localMap$iv$ivxxx = var161.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxxx = ComposedModifierKt.materializeModifier(var161, var159);
            Function0 var167 = ComposeUiNode.Companion.getConstructor();
            Composer var168 = var161;
            int var169 = 6 | 896 & var162 << 6;
            int $i$f$ReusableComposeNodexxx = 0;
            ComposerKt.sourceInformationMarkerStart(var168, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var168.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var168.startReusableNode();
            if (var168.getInserting()) {
               var168.createNode(var167);
            } else {
               var168.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxxx = Updater.recovered$676aa17b44fd6280(var168);
            int var172/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, var160, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(
               $this$Layout_u24lambda_u240$iv$ivxxx, localMap$iv$ivxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
            );
            Updater.recovered$89682b199a142880(
               $this$Layout_u24lambda_u240$iv$ivxxx, compositeKeyHash$iv$ivxxx, ComposeUiNode.Companion.getSetCompositeKeyHash()
            );
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, materialized$iv$ivxxx, ComposeUiNode.Companion.getSetModifier());
            int var173 = 14 & var169 >> 6;
            Composer $composer$ivxxx = var168;
            int var175/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var176 = 6 | 112 & var147 >> 6;
            Composer var177 = $composer$ivxxx;
            ColumnScope var178 = ColumnScopeInstance.INSTANCE;
            int var183 = 0;
            ComposerKt.sourceInformationMarkerStart(var177, -264872630, "C:SearchBrowseCard.kt#arqjag");
            var177.startReplaceGroup(-562735656);
            ComposerKt.sourceInformation(var177, "116@5221L136,114@5130L254");
            if (!var125.isEmpty()) {
               String var253 = "";
               var177.startReplaceGroup(-562730931);
               ComposerKt.sourceInformation(var177, "CC(remember):SearchBrowseCard.kt#9igjgp");
               Composer var188 = var177;
               boolean var190 = var177.changedInstance(var8) | var177.changedInstance(var125);
               int $i$f$cachex = 0;
               Object it$ivx = var188.rememberedValue();
               int var196/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
               Object var263;
               if (!var190 && it$ivx != Composer.Companion.getEmpty()) {
                  var263 = it$ivx;
               } else {
                  String var198 = "";
                  int var199 = 0;
                  Function2 var262 = new SearchBrowseCardKt$SearchBrowseCard$1$1$3$1$1(var8, var125, null);
                  var253 = var198;
                  Object var201 = var262;
                  var188.updateRememberedValue(var201);
                  var263 = var201;
               }

               Function2 var203 = (Function2)var263;
               var177.endReplaceGroup();
               EffectsKt.LaunchedEffect(var253, var203, var177, 6);
            }

            var177.endReplaceGroup();
            var177.startReplaceGroup(-562721858);
            ComposerKt.sourceInformation(var177, "*128@5781L721,125@5637L1730,158@7392L41");
            Iterable var204 = var125;
            int var189 = 0;
            int var191 = 0;

            for (Object var195 : var204) {
               int var197 = var191++;
               if (var197 < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               SuggestedArticle var200 = (SuggestedArticle)var195;
               int var202 = var197;
               int var205 = 0;
               var177.startReplaceGroup(-562722027);
               ComposerKt.sourceInformation(var177, "123@5546L40");
               if (var202 == 0) {
                  Modifier var255 = Modifier.Companion;
                  int $this$dp$ivxx = 4;
                  int $i$f$getDpxx = 0;
                  SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var255, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var177, 6);
               }

               var177.endReplaceGroup();
               Modifier var256 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               boolean var264 = false;
               Object var267 = null;
               Object var269 = null;
               Object var270 = null;
               var177.startReplaceGroup(-1193385195);
               ComposerKt.sourceInformation(var177, "CC(remember):SearchBrowseCard.kt#9igjgp");
               Composer var214 = var177;
               boolean invalid$ivx = var177.changedInstance(var8) | var177.changed(var200) | var177.changedInstance(var9);
               int $i$f$cachex = 0;
               Object it$ivx = var214.rememberedValue();
               int var226/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
               Object var271;
               if (!invalid$ivx && it$ivx != Composer.Companion.getEmpty()) {
                  var271 = it$ivx;
               } else {
                  Object var228 = null;
                  Object var229 = null;
                  Object var230 = null;
                  boolean var231 = false;
                  Modifier var232 = var256;
                  int var233 = 0;
                  final io.intercom.android.sdk.metrics.MetricTracker recoveredExpressionCapture_240_0 = var8;
                  final io.intercom.android.sdk.m5.home.data.SuggestedArticle recoveredExpressionCapture_240_1 = var200;
                  final android.content.Context recoveredExpressionCapture_240_2 = var9;
                  Function0 var235 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.components.SearchBrowseCardKt$SearchBrowseCard$1.invoke$lambda$10$lambda$9$lambda$8$lambda$6$lambda$5(recoveredExpressionCapture_240_0, recoveredExpressionCapture_240_1, recoveredExpressionCapture_240_2));
                  var256 = var232;
                  var264 = var231;
                  var267 = var230;
                  var269 = var229;
                  var270 = var228;
                  Object var236 = var235;
                  var214.updateRememberedValue(var236);
                  var271 = var236;
               }

               Function0 var207 = (Function0)var271;
               var177.endReplaceGroup();
               Modifier var257 = ClickableKt.recovered$1c9660c41fd850dc(
                  var256, var264, (String)var267, (Role)var269, (MutableInteractionSource)var270, var207, 15, null
               );
               int $this$dp$ivxx = 16;
               int $i$f$getDpxx = 0;
               float var265 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
               int $this$dp$ivxxx = 4;
               int $i$f$getDpxxx = 0;
               float var268 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
               int $this$dp$ivxxxx = 4;
               int $i$f$getDpxxxx = 0;
               Modifier var211 = TestTagKt.testTag(
                  PaddingKt.recovered$03b481d31d62885a(var257, var265, var268, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 4, null),
                  "suggested article"
               );
               androidx.compose.ui.Alignment.Vertical var221 = Alignment.Companion.getCenterVertically();
               short var223 = 384;
               int $i$f$Rowx = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var177, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
               );
               androidx.compose.foundation.layout.Arrangement.Horizontal var218 = Arrangement.INSTANCE.getStart();
               MeasurePolicy measurePolicy$ivxxxx = RowKt.rowMeasurePolicy(var218, var221, var177, 14 & var223 >> 3 | 112 & var223 >> 3);
               Modifier var234 = var211;
               MeasurePolicy var237 = measurePolicy$ivxxxx;
               Composer var238 = var177;
               int var239 = 112 & var223 << 3;
               int $i$f$Layoutxxxx = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var238, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh"
               );
               int compositeKeyHash$iv$ivxxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var238, 0));
               CompositionLocalMap localMap$iv$ivxxxx = var238.getCurrentCompositionLocalMap();
               Modifier materialized$iv$ivxxxx = ComposedModifierKt.materializeModifier(var238, var234);
               Function0 var106 = ComposeUiNode.Companion.getConstructor();
               Composer var107 = var238;
               int var108 = 6 | 896 & var239 << 6;
               int $i$f$ReusableComposeNodexxxx = 0;
               ComposerKt.sourceInformationMarkerStart(var107, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
               if (!(var107.getApplier() instanceof Applier)) {
                  ComposablesKt.invalidApplier();
               }

               var107.startReusableNode();
               if (var107.getInserting()) {
                  var107.createNode(var106);
               } else {
                  var107.useNode();
               }

               Composer $this$Layout_u24lambda_u240$iv$ivxxxx = Updater.recovered$676aa17b44fd6280(var107);
               int var111/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxxx */ = 0;
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxxx, var237, ComposeUiNode.Companion.getSetMeasurePolicy());
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxxxx, localMap$iv$ivxxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
               );
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxxxx, compositeKeyHash$iv$ivxxxx, ComposeUiNode.Companion.getSetCompositeKeyHash()
               );
               Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxxx, materialized$iv$ivxxxx, ComposeUiNode.Companion.getSetModifier());
               int var112 = 14 & var108 >> 6;
               Composer $composer$ivxxxx = var107;
               int var114/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
               ComposerKt.sourceInformationMarkerStart($composer$ivxxxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
               int var115 = 6 | 112 & var223 >> 6;
               Composer var116 = $composer$ivxxxx;
               RowScope var117 = RowScopeInstance.INSTANCE;
               int var118 = 0;
               ComposerKt.sourceInformationMarkerStart(var116, -187778113, "C149@6924L324,156@7277L64:SearchBrowseCard.kt#arqjag");
               Modifier var119 = RowScope.weight$default(var117, Modifier.Companion, 1.0F, false, 2, null);
               String var120 = var200.getTitle();
               FontWeight var121 = FontWeight.Companion.getNormal();
               int var122 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
               TextKt.recovered$5b114ab32a4f4190(
                  var120, var119, 0L, null, 0L, null, var121, null, 0L, null, null, 0L, var122, false, 2, 0, null, null, var116, 1572864, 24960, 241596
               );
               Modifier var258 = Modifier.Companion;
               int $this$dp$ivxxxxx = 20;
               int $i$f$getDpxxxxx = 0;
               IntercomChevronKt.IntercomChevron(
                  PaddingKt.recovered$f3b9f2e94c07c806(var258, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx), 0.0F, 2, null), var116, 6, 0
               );
               ComposerKt.sourceInformationMarkerEnd(var116);
               ComposerKt.sourceInformationMarkerEnd($composer$ivxxxx);
               var107.endNode();
               ComposerKt.sourceInformationMarkerEnd(var107);
               ComposerKt.sourceInformationMarkerEnd(var238);
               ComposerKt.sourceInformationMarkerEnd(var177);
               Modifier var259 = Modifier.Companion;
               int $this$dp$ivxxxxxx = 16;
               int $i$f$getDpxxxxxx = 0;
               SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var259, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxx)), var177, 6);
            }

            var177.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var177);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxxx);
            var168.endNode();
            ComposerKt.sourceInformationMarkerEnd(var168);
            ComposerKt.sourceInformationMarkerEnd(var161);
            ComposerKt.sourceInformationMarkerEnd(var35);
         }

         var35.endReplaceGroup();
         var35.startReplaceGroup(343756010);
         ComposerKt.sourceInformation(var35, "168@7776L34");
         if (var11 && var12) {
            var35.startReplaceGroup(343758146);
            ComposerKt.sourceInformation(var35, "164@7630L111");
            if (!var125.isEmpty()) {
               Modifier var260 = Modifier.Companion;
               int $this$dp$ivxx = 16;
               int $i$f$getDpxx = 0;
               IntercomDividerKt.IntercomDivider(
                  PaddingKt.recovered$f3b9f2e94c07c806(var260, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 2, null), var35, 6, 0
               );
            }

            var35.endReplaceGroup();
            TeamPresenceRowKt.TeamPresenceRow(null, var13, var35, 0, 1);
         }

         var35.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$10$lambda$1$lambda$0(MetricTracker var0, Context var1) {
      var0.clickedSearchBrowseCard();
      Intent var2 = IntercomArticleSearchActivity.Companion.buildIntent(var1, true);
      var1.startActivity(var2);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$10$lambda$9$lambda$8$lambda$6$lambda$5(MetricTracker var0, SuggestedArticle var1, Context var2) {
      var0.clickedArticleSuggestion(var1.getId());
      var2.startActivity(
         IntercomArticleActivity.Companion.buildIntent(var2, new io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments(var1.getId(), "search_browse_card", true, false, 8, null))
      );
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

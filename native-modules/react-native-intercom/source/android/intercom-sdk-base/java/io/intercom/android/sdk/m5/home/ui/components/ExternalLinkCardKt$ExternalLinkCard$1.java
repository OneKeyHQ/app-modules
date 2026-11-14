package io.intercom.android.sdk.m5.home.ui.components;

import android.content.Context;
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
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeExternalLinkData;
import io.intercom.android.sdk.m5.home.data.Link;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.LinkOpener;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nExternalLinkCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExternalLinkCard.kt\nio/intercom/android/sdk/m5/home/ui/components/ExternalLinkCardKt$ExternalLinkCard$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 8 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,102:1\n87#2:103\n83#2,10:104\n94#2:190\n81#3,6:114\n88#3,6:129\n81#3,6:157\n88#3,6:172\n96#3:183\n96#3:189\n402#4,9:120\n411#4:135\n402#4,9:163\n411#4:178\n412#4,2:181\n412#4,2:187\n118#5:136\n118#5:137\n118#5:146\n118#5:179\n118#5:180\n118#5:185\n1878#6,2:138\n1880#6:186\n1047#7,6:140\n99#8:147\n96#8,9:148\n106#8:184\n*S KotlinDebug\n*F\n+ 1 ExternalLinkCard.kt\nio/intercom/android/sdk/m5/home/ui/components/ExternalLinkCardKt$ExternalLinkCard$1\n*L\n38#1:103\n38#1:104,10\n38#1:190\n38#1:114,6\n38#1:129,6\n49#1:157,6\n49#1:172,6\n49#1:183\n38#1:189\n38#1:120,9\n38#1:135\n49#1:163,9\n49#1:178\n49#1:181,2\n38#1:187,2\n42#1:136\n43#1:137\n56#1:146\n66#1:179\n68#1:180\n78#1:185\n48#1:138,2\n48#1:186\n52#1:140,6\n49#1:147\n49#1:148,9\n49#1:184\n*E\n"
)
final class ExternalLinkCardKt$ExternalLinkCard$1 implements Function3 {
   // $VF: synthetic field
   final HomeCards$HomeExternalLinkData $homeExternalLinkData;
   // $VF: synthetic field
   final Context $context;

   ExternalLinkCardKt$ExternalLinkCard$1(HomeCards$HomeExternalLinkData var1, Context var2) {
      this.$homeExternalLinkData = var1;
      this.$context = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C37@1547L2017:ExternalLinkCard.kt#utrtal");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1810723127, var3, -1, "io.intercom.android.sdk.m5.home.ui.components.ExternalLinkCard.<anonymous> (ExternalLinkCard.kt:37)"
            );
         }

         HomeCards$HomeExternalLinkData var7 = this.$homeExternalLinkData;
         Context var8 = this.$context;
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var4 = Modifier.Companion;
         Vertical var5 = Arrangement.INSTANCE.getTop();
         Horizontal var6 = Alignment.Companion.getStart();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var4;
         MeasurePolicy var13 = var11;
         Composer var14 = var2;
         int var15 = 112 & var9 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var12);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var14;
         int var22 = 6 | 896 & var15 << 6;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var21.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var21.startReusableNode();
         if (var21.getInserting()) {
            var21.createNode(var20);
         } else {
            var21.useNode();
         }

         Composer var24 = Updater.recovered$676aa17b44fd6280(var21);
         int var25 = 0;
         Updater.recovered$89682b199a142880(var24, var13, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         ColumnScope var31 = ColumnScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -541643021, "C:ExternalLinkCard.kt#utrtal");
         var30.startReplaceGroup(-433115947);
         ComposerKt.sourceInformation(var30, "44@1901L10,39@1639L306");
         CharSequence var33 = var7.getCardTitle();
         if (var33 != null && !StringsKt.isBlank(var33)) {
            Modifier var10000 = Modifier.Companion;
            int var34 = 16;
            int var35 = 0;
            var10000 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var34), 0.0F, 2, null);
            var34 = 12;
            var35 = 0;
            float var10002 = Dp.recovered$95b3d103663bf060(var34);
            var34 = 4;
            var35 = 0;
            Modifier var81 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, var10002, 0.0F, Dp.recovered$95b3d103663bf060(var34), 5, null);
            String var85 = var7.getCardTitle();
            TextStyle var89 = IntercomTheme.INSTANCE.getTypography(var30, IntercomTheme.$stable).getType04SemiBold();
            TextKt.recovered$5b114ab32a4f4190(
               var85, var81, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var89, var30, 48, 0, 131068
            );
         }

         var30.endReplaceGroup();
         var30.startReplaceGroup(-433100991);
         ComposerKt.sourceInformation(var30, "*51@2167L198,48@2047L1202");
         Iterable var82 = var7.getLinks();
         int var86 = 0;
         int var90 = 0;

         for (Object var37 : var82) {
            int var38 = var90++;
            if (var38 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            Link var39 = (Link)var37;
            int var40 = var38;
            int var41 = 0;
            Modifier var113 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            boolean var10001 = false;
            Object var119 = null;
            Object var10003 = null;
            Object var10004 = null;
            var30.startReplaceGroup(-626904077);
            ComposerKt.sourceInformation(var30, "CC(remember):ExternalLinkCard.kt#9igjgp");
            Composer var42 = var30;
            boolean var43 = var30.changed(var39) | var30.changedInstance(var8);
            int var44 = 0;
            Object var45 = var42.rememberedValue();
            int var46 = 0;
            Object var10005;
            if (!var43 && var45 != Composer.Companion.getEmpty()) {
               var10005 = var45;
            } else {
               Object var47 = null;
               Object var48 = null;
               Object var49 = null;
               boolean var50 = false;
               Modifier var51 = var113;
               int var52 = 0;
               final io.intercom.android.sdk.m5.home.data.Link recoveredCapture_915_0 = var39;
               final android.content.Context recoveredCapture_915_1 = var8;
               Function0 var53 = ((kotlin.jvm.functions.Function0) () -> ExternalLinkCardKt$ExternalLinkCard$1.invoke$lambda$4$lambda$3$lambda$1$lambda$0(recoveredCapture_915_0, recoveredCapture_915_1));
               var113 = var51;
               var10001 = var50;
               var119 = var49;
               var10003 = var48;
               var10004 = var47;
               Object var54 = var53;
               var42.updateRememberedValue(var54);
               var10005 = var54;
            }

            Function0 var55 = (Function0)var10005;
            var30.endReplaceGroup();
            var113 = ClickableKt.recovered$1c9660c41fd850dc(
               var113, var10001, (String)var119, (Role)var10003, (MutableInteractionSource)var10004, var55, 15, null
            );
            int var101 = 16;
            int var91 = 0;
            float var118 = Dp.recovered$95b3d103663bf060(var101);
            int $this$dp$ivx = 12;
            int $i$f$getDpx = 0;
            Modifier var103 = PaddingKt.recovered$904403cea4c49d95(var113, var118, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            androidx.compose.ui.Alignment.Vertical var95 = Alignment.Companion.getCenterVertically();
            short var96 = 384;
            var45 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var30, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            androidx.compose.foundation.layout.Arrangement.Horizontal var93 = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var93, var95, var30, 14 & var96 >> 3 | 112 & var96 >> 3);
            Modifier var99 = var103;
            MeasurePolicy var100 = measurePolicy$ivx;
            Composer var56 = var30;
            int var57 = 112 & var96 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var56, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var56, 0));
            CompositionLocalMap localMap$iv$ivx = var56.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var56, var99);
            Function0 var62 = ComposeUiNode.Companion.getConstructor();
            Composer var63 = var56;
            int var64 = 6 | 896 & var57 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var63, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var63.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var63.startReusableNode();
            if (var63.getInserting()) {
               var63.createNode(var62);
            } else {
               var63.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var63);
            int var67/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var100, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var68 = 14 & var64 >> 6;
            Composer $composer$ivx = var63;
            int var70 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var71 = 6 | 112 & var96 >> 6;
            Composer var72 = $composer$ivx;
            RowScope var73 = RowScopeInstance.INSTANCE;
            int var74 = 0;
            ComposerKt.sourceInformationMarkerStart(var72, -1435182230, "C58@2546L278,65@2845L40,68@3003L58,70@3182L6,66@2906L325:ExternalLinkCard.kt#utrtal");
            Modifier var75 = RowScope.weight$default(var73, Modifier.Companion, 1.0F, false, 2, null);
            String var76 = var39.getLabel();
            FontWeight var77 = FontWeight.Companion.getNormal();
            int var78 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextKt.recovered$5b114ab32a4f4190(
               var76, var75, 0L, null, 0L, null, var77, null, 0L, null, null, 0L, var78, false, 1, 0, null, null, var72, 1572864, 24960, 241596
            );
            var113 = Modifier.Companion;
            int $this$dp$ivxx = 16;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var113, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var72, 6);
            var113 = Modifier.Companion;
            int $this$dp$ivxxx = 16;
            int $i$f$getDpxxx = 0;
            var75 = SizeKt.recovered$111ef94c93e78c04(var113, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
            Painter var109 = PainterResources_androidKt.painterResource(drawable.intercom_open_help_center, var72, 0);
            long var79 = IntercomTheme.INSTANCE.getColors(var72, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
            IconKt.recovered$e00adffb7fe45b02(var109, null, var75, var79, var72, 432 | Painter.$stable, 0);
            ComposerKt.sourceInformationMarkerEnd(var72);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var63.endNode();
            ComposerKt.sourceInformationMarkerEnd(var63);
            ComposerKt.sourceInformationMarkerEnd(var56);
            ComposerKt.sourceInformationMarkerEnd(var30);
            var30.startReplaceGroup(-433061728);
            ComposerKt.sourceInformation(var30, "74@3338L184");
            if (var40 != var7.getLinks().size() - 1) {
               var113 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               int $this$dp$ivxxxx = 16;
               int $i$f$getDpxxxx = 0;
               IntercomDividerKt.IntercomDivider(
                  PaddingKt.recovered$f3b9f2e94c07c806(var113, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 0.0F, 2, null), var30, 6, 0
               );
            }

            var30.endReplaceGroup();
         }

         var30.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$4$lambda$3$lambda$1$lambda$0(Link var0, Context var1) {
      Injector.get().getMetricTracker().clickedExternalLinkCard();
      LinkOpener.handleUrl(var0.getUrl(), var1, Injector.get().getApi());
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

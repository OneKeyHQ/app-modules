package io.intercom.android.sdk.tickets.create.ui;

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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.draw.AlphaKt;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.conversation.ui.components.row.BubbleMessageRowKt;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCreateTicketCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketCard.kt\nio/intercom/android/sdk/tickets/create/ui/CreateTicketCardKt$CreateTicketCard$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 8 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n*L\n1#1,196:1\n1047#2,6:197\n70#3:203\n67#3,9:204\n77#3:310\n81#4,6:213\n88#4,6:228\n81#4,6:242\n88#4,6:257\n81#4,6:274\n88#4,6:289\n96#4:299\n96#4:305\n96#4:309\n402#5,9:219\n411#5:234\n402#5,9:248\n411#5:263\n402#5,9:280\n411#5:295\n412#5,2:297\n412#5,2:303\n412#5,2:307\n118#6:235\n118#6:296\n118#6:301\n118#6:302\n99#7,6:236\n106#7:306\n87#8:264\n84#8,9:265\n94#8:300\n*S KotlinDebug\n*F\n+ 1 CreateTicketCard.kt\nio/intercom/android/sdk/tickets/create/ui/CreateTicketCardKt$CreateTicketCard$1\n*L\n55#1:197,6\n53#1:203\n53#1:204,9\n53#1:310\n53#1:213,6\n53#1:228,6\n59#1:242,6\n59#1:257,6\n66#1:274,6\n66#1:289,6\n66#1:299\n59#1:305\n53#1:309\n53#1:219,9\n53#1:234\n59#1:248,9\n59#1:263\n66#1:280,9\n66#1:295\n66#1:297,2\n59#1:303,2\n53#1:307,2\n62#1:235\n75#1:296\n83#1:301\n88#1:302\n59#1:236,6\n59#1:306\n66#1:264\n66#1:265,9\n66#1:300\n*E\n"
)
final class CreateTicketCardKt$CreateTicketCard$1 implements Function3 {
   // $VF: synthetic field
   final boolean $enabled;
   // $VF: synthetic field
   final Function0 $onClick;
   // $VF: synthetic field
   final BlockRenderData $blockRenderData;

   CreateTicketCardKt$CreateTicketCard$1(boolean var1, Function0 var2, BlockRenderData var3) {
      this.$enabled = var1;
      this.$onClick = var2;
      this.$blockRenderData = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C54@2342L57,52@2259L1721:CreateTicketCard.kt#xh6pmz");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1721044843, var3, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketCard.<anonymous> (CreateTicketCard.kt:52)");
         }

         Modifier var10000 = Modifier.Companion;
         boolean var10001 = this.$enabled;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var2.startReplaceGroup(2105931123);
         ComposerKt.sourceInformation(var2, "CC(remember):CreateTicketCard.kt#9igjgp");
         Composer var5 = var2;
         boolean var6 = var2.changed(this.$onClick);
         Function0 var7 = this.$onClick;
         int var8 = 0;
         Object var9 = var5.rememberedValue();
         int var10 = 0;
         Object var10005;
         if (!var6 && var9 != Composer.Companion.getEmpty()) {
            var10005 = var9;
         } else {
            Object var99 = null;
            Object var98 = null;
            Object var97 = null;
            boolean var96 = var10001;
            Modifier var95 = var10000;
            int var11 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1591_0 = var7;
            Function0 var100 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.create.ui.CreateTicketCardKt$CreateTicketCard$1.invoke$lambda$1$lambda$0(recoveredExpressionCapture_1591_0));
            var10000 = var95;
            var10001 = var96;
            var10002 = var97;
            var10003 = var98;
            var10004 = var99;
            Object var12 = var100;
            var5.updateRememberedValue(var12);
            var10005 = var12;
         }

         Function0 var4 = (Function0)var10005;
         var2.endReplaceGroup();
         Modifier var101 = ClickableKt.recovered$1c9660c41fd850dc(
            var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var4, 14, null
         );
         boolean var104 = this.$enabled;
         BlockRenderData var105 = this.$blockRenderData;
         int var106 = 0;
         int var107 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var102 = Alignment.Companion.getTopStart();
         boolean var103 = false;
         MeasurePolicy var108 = BoxKt.maybeCachedBoxMeasurePolicy(var102, var103);
         Modifier var109 = var101;
         MeasurePolicy var13 = var108;
         Composer var14 = var2;
         int var15 = 112 & var106 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var109);
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
         ComposerKt.sourceInformationMarkerStart(var27, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var29 = 6 | 112 & var106 >> 6;
         Composer var30 = var27;
         BoxScope var31 = BoxScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -941378655, "C58@2425L1545:CreateTicketCard.kt#xh6pmz");
         Modifier var120 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var33 = 16;
         int var34 = 0;
         Modifier var35 = PaddingKt.recovered$eb6a744cd98cdbca(var120, Dp.recovered$95b3d103663bf060(var33));
         Vertical var110 = Alignment.Companion.getCenterVertically();
         HorizontalOrVertical var111 = Arrangement.INSTANCE.getSpaceBetween();
         short var36 = 438;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var30, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var111, var110, var30, 14 & var36 >> 3 | 112 & var36 >> 3);
         Modifier var39 = var35;
         MeasurePolicy var40 = measurePolicy$ivx;
         Composer var41 = var30;
         int var42 = 112 & var36 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var41, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var41, 0));
         CompositionLocalMap localMap$iv$ivx = var41.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var41, var39);
         Function0 var47 = ComposeUiNode.Companion.getConstructor();
         Composer var48 = var41;
         int var49 = 6 | 896 & var42 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var48, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var48.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var48.startReusableNode();
         if (var48.getInserting()) {
            var48.createNode(var47);
         } else {
            var48.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var48);
         int var52/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var40, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var53 = 14 & var49 >> 6;
         Composer $composer$ivx = var48;
         int var55 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var56 = 6 | 112 & var36 >> 6;
         Composer var57 = $composer$ivx;
         RowScope var58 = RowScopeInstance.INSTANCE;
         int var59 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var57, 1414756708, "C65@2701L808,82@3526L40,84@3619L60,88@3836L31,89@3911L6,83@3583L373:CreateTicketCard.kt#xh6pmz"
         );
         Modifier var60 = RowScope.weight$default(var58, Modifier.Companion, 1.0F, false, 2, null);
         byte var61 = 0;
         int var62 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var57, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Vertical var63 = Arrangement.INSTANCE.getTop();
         Horizontal var64 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(var63, var64, var57, 14 & var61 >> 3 | 112 & var61 >> 3);
         Modifier var66 = var60;
         MeasurePolicy var67 = measurePolicy$ivxx;
         Composer var68 = var57;
         int var69 = 112 & var61 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var68, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var68, 0));
         CompositionLocalMap localMap$iv$ivxx = var68.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var68, var66);
         Function0 var74 = ComposeUiNode.Companion.getConstructor();
         Composer var75 = var68;
         int var76 = 6 | 896 & var69 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var75, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var75.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var75.startReusableNode();
         if (var75.getInserting()) {
            var75.createNode(var74);
         } else {
            var75.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var75);
         int var79/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var67, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var80 = 14 & var76 >> 6;
         Composer $composer$ivxx = var75;
         int var82 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var83 = 6 | 112 & var61 >> 6;
         Composer var84 = $composer$ivxx;
         ColumnScope var85 = ColumnScopeInstance.INSTANCE;
         int var86 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var84, -1381668262, "C70@2913L6,71@2980L10,72@3057L31,68@2801L311,74@3133L40,77@3316L10,79@3436L31,75@3194L297:CreateTicketCard.kt#xh6pmz"
         );
         String var121 = var105.getBlock().getTitle();
         Intrinsics.checkNotNullExpressionValue(var121, "getTitle(...)");
         String var87 = var121;
         long var88 = IntercomTheme.INSTANCE.getColors(var84, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextStyle var90 = IntercomTheme.INSTANCE.getTypography(var84, IntercomTheme.$stable).getType04SemiBold();
         Modifier var91 = AlphaKt.alpha(Modifier.Companion, BubbleMessageRowKt.contentAlpha(var104, var84, 0));
         TextKt.recovered$5b114ab32a4f4190(
            var87, var91, var88, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var90, var84, 0, 0, 131064
         );
         Modifier var122 = Modifier.Companion;
         int $this$dp$ivx = 2;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var122, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var84, 6);
         String var117 = var105.getBlock().getTicketType().getName();
         TextStyle var119 = IntercomTheme.INSTANCE.getTypography(var84, IntercomTheme.$stable).getType04();
         long var93 = ColorKt.Color(4285887861L);
         Modifier var118 = AlphaKt.alpha(Modifier.Companion, BubbleMessageRowKt.contentAlpha(var104, var84, 0));
         TextKt.recovered$5b114ab32a4f4190(
            var117, var118, var93, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var119, var84, 384, 0, 131064
         );
         ComposerKt.sourceInformationMarkerEnd(var84);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var75.endNode();
         ComposerKt.sourceInformationMarkerEnd(var75);
         ComposerKt.sourceInformationMarkerEnd(var68);
         ComposerKt.sourceInformationMarkerEnd(var57);
         Modifier var123 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var123, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var57, 6);
         Painter var124 = PainterResources_androidKt.painterResource(drawable.intercom_ticket_detail_icon, var57, 0);
         Modifier var125 = Modifier.Companion;
         int $this$dp$ivxxx = 16;
         int $i$f$getDpxxx = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var124,
            null,
            AlphaKt.alpha(
               SizeKt.recovered$111ef94c93e78c04(var125, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), BubbleMessageRowKt.contentAlpha(var104, var57, 0)
            ),
            IntercomTheme.INSTANCE.getColors(var57, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d(),
            var57,
            48 | Painter.$stable,
            0
         );
         ComposerKt.sourceInformationMarkerEnd(var57);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var48.endNode();
         ComposerKt.sourceInformationMarkerEnd(var48);
         ComposerKt.sourceInformationMarkerEnd(var41);
         ComposerKt.sourceInformationMarkerEnd(var30);
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

   private static final Unit invoke$lambda$1$lambda$0(Function0 var0) {
      if (var0 != null) {
         var0.invoke();
      }

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

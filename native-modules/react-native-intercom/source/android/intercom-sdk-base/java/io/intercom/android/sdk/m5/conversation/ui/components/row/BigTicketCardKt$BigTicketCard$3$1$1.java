package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
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
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.TextWithSeparatorKt;
import io.intercom.android.sdk.tickets.TicketProgressIndicatorKt;
import io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.TimeFormatterExtKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBigTicketCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BigTicketCard.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BigTicketCardKt$BigTicketCard$3$1$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,198:1\n87#2:199\n83#2,10:200\n87#2:233\n84#2,9:234\n94#2:273\n94#2:316\n81#3,6:210\n88#3,6:225\n81#3,6:243\n88#3,6:258\n96#3:272\n81#3,6:286\n88#3,6:301\n96#3:311\n96#3:315\n402#4,9:216\n411#4:231\n402#4,9:249\n411#4:264\n412#4,2:270\n402#4,9:292\n411#4:307\n412#4,2:309\n412#4,2:313\n118#5:232\n118#5:265\n118#5:266\n118#5:267\n118#5:268\n118#5:269\n118#5:274\n118#5:275\n118#5:308\n99#6:276\n96#6,9:277\n106#6:312\n*S KotlinDebug\n*F\n+ 1 BigTicketCard.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BigTicketCardKt$BigTicketCard$3$1$1\n*L\n94#1:199\n94#1:200,10\n95#1:233\n95#1:234,9\n95#1:273\n94#1:316\n94#1:210,6\n94#1:225,6\n95#1:243,6\n95#1:258,6\n95#1:272\n131#1:286,6\n131#1:301,6\n131#1:311\n94#1:315\n94#1:216,9\n94#1:231\n95#1:249,9\n95#1:264\n95#1:270,2\n131#1:292,9\n131#1:307\n131#1:309,2\n94#1:313,2\n96#1:232\n99#1:265\n106#1:266\n116#1:267\n121#1:268\n126#1:269\n129#1:274\n134#1:275\n141#1:308\n131#1:276\n131#1:277,9\n131#1:312\n*E\n"
)
final class BigTicketCardKt$BigTicketCard$3$1$1 implements Function3 {
   // $VF: synthetic field
   final io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState $ticketDetailState;
   // $VF: synthetic field
   final Context $context;

   BigTicketCardKt$BigTicketCard$3$1$1(io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState var1, Context var2) {
      this.$ticketDetailState = var1;
      this.$context = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C93@3841L3098:BigTicketCard.kt#hr3bi5");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1554241908,
               var3,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCard.<anonymous>.<anonymous>.<anonymous> (BigTicketCard.kt:93)"
            );
         }

         io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState var7 = this.$ticketDetailState;
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
         ComposerKt.sourceInformationMarkerStart(var30, -61277685, "C94@3870L1908,127@5799L111,130@5931L990:BigTicketCard.kt#hr3bi5");
         Modifier var10000 = Modifier.Companion;
         int var33 = 12;
         int var34 = 0;
         Modifier var71 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var33));
         Horizontal var35 = Alignment.Companion.getCenterHorizontally();
         short var36 = 390;
         int $i$f$Columnx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var30, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(verticalArrangement$ivx, var35, var30, 14 & var36 >> 3 | 112 & var36 >> 3);
         Modifier var39 = var71;
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

         Composer $composer$ivx;
         Composer var57;
         String var117;
         String var124;
         label41: {
            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var48);
            int var52/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var40, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var53 = 14 & var49 >> 6;
            $composer$ivx = var48;
            int var55/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var56 = 6 | 112 & var36 >> 6;
            var57 = $composer$ivx;
            ColumnScope var58 = ColumnScopeInstance.INSTANCE;
            int var59 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var57,
               1078417177,
               "C98@4062L40,101@4248L10,99@4127L173,105@4435L40,111@4859L10,106@4500L562,115@5087L40,118@5298L10,116@5152L190,120@5367L41,121@5433L258,125@5716L40:BigTicketCard.kt#hr3bi5"
            );
            Modifier var115 = Modifier.Companion;
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var115, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var57, 6);
            TextKt.recovered$5b114ab32a4f4190(
               var7.getTicketName(),
               null,
               0L,
               null,
               0L,
               null,
               null,
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
               IntercomTheme.INSTANCE.getTypography(var57, IntercomTheme.$stable).getType04SemiBold(),
               var57,
               0,
               0,
               131070
            );
            String var110 = var7.getTicketTimelineCardState().getStatusLabel();
            int var62 = 0;
            Modifier var116 = Modifier.Companion;
            int $this$dp$ivxx = 8;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var116, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var57, 6);
            var117 = var7.getTicketTimelineCardState().getStatusLabel();
            Long var10001 = var7.getTicketTimelineCardState().getTimestamp();
            if (var10001 != null) {
               var124 = TimeFormatterExtKt.formattedDateFromLong(var10001, var8);
               if (var124 != null) {
                  break label41;
               }
            }

            var124 = "";
         }

         TextWithSeparatorKt.recovered$741c28fc37ac0ca8(
            var117,
            var124,
            null,
            null,
            IntercomTheme.INSTANCE.getTypography(var57, IntercomTheme.$stable).getType04SemiBold(),
            var7.getTicketTimelineCardState().recovered$4e526da7fcc96ce2(),
            0,
            0,
            TextAlign.recovered$18ab2fc4c81b19cd(TextAlign.Companion.recovered$d707b9dd64876179()),
            var57,
            0,
            204
         );
         Modifier var118 = Modifier.Companion;
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var118, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var57, 6);
         TextKt.recovered$5b114ab32a4f4190(
            var7.getTicketTimelineCardState().getStatusTitle(),
            null,
            0L,
            null,
            0L,
            null,
            null,
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
            IntercomTheme.INSTANCE.getTypography(var57, IntercomTheme.$stable).getType04(),
            var57,
            0,
            0,
            131070
         );
         Modifier var119 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var119, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var57, 6);
         TicketProgressIndicatorKt.recovered$8eea2a3ce3419e18(
            var7.getTicketTimelineCardState().getProgressSections(), var7.getTicketTimelineCardState().recovered$4e526da7fcc96ce2(), null, var57, 0, 4
         );
         Modifier var120 = Modifier.Companion;
         int $this$dp$ivxxx = 8;
         int $i$f$getDpxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var120, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var57, 6);
         ComposerKt.sourceInformationMarkerEnd(var57);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var48.endNode();
         ComposerKt.sourceInformationMarkerEnd(var48);
         ComposerKt.sourceInformationMarkerEnd(var41);
         ComposerKt.sourceInformationMarkerEnd(var30);
         Modifier var121 = Modifier.Companion;
         int $this$dp$ivxxxx = 12;
         int $i$f$getDpxxxx = 0;
         IntercomDividerKt.IntercomDivider(
            PaddingKt.recovered$f3b9f2e94c07c806(var121, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 0.0F, 2, null), var30, 6, 0
         );
         Modifier var122 = var31.align(Modifier.Companion, Alignment.Companion.getCenterHorizontally());
         int $this$dp$ivxxxxx = 14;
         int $i$f$getDpxxxxx = 0;
         Modifier var74 = PaddingKt.recovered$f3b9f2e94c07c806(var122, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx), 1, null);
         androidx.compose.ui.Alignment.Vertical var79 = Alignment.Companion.getCenterVertically();
         short var80 = 384;
         int var81 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var30, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var78 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var78, var79, var30, 14 & var80 >> 3 | 112 & var80 >> 3);
         Modifier var83 = var74;
         MeasurePolicy var84 = measurePolicy$ivxx;
         Composer var85 = var30;
         int var86 = 112 & var80 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var85, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var85, 0));
         CompositionLocalMap localMap$iv$ivxx = var85.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var85, var83);
         Function0 var91 = ComposeUiNode.Companion.getConstructor();
         Composer var92 = var85;
         int var93 = 6 | 896 & var86 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var92, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var92.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var92.startReusableNode();
         if (var92.getInserting()) {
            var92.createNode(var91);
         } else {
            var92.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var92);
         int var96/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var84, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var97 = 14 & var93 >> 6;
         Composer $composer$ivxx = var92;
         int var99 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var100 = 6 | 112 & var80 >> 6;
         Composer var101 = $composer$ivxx;
         RowScope var102 = RowScopeInstance.INSTANCE;
         int var103 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var101, 1080527409, "C137@6255L60,139@6421L6,136@6221L347,143@6634L58,144@6744L10,145@6821L6,142@6593L306:BigTicketCard.kt#hr3bi5"
         );
         Painter var108 = PainterResources_androidKt.painterResource(drawable.intercom_ticket_detail_icon, var101, 0);
         long var63 = ColorExtensionsKt.recovered$93702c38c1546c4a(IntercomTheme.INSTANCE.getColors(var101, IntercomTheme.$stable).recovered$202acdf3dbb5c020());
         Modifier var123 = Modifier.Companion;
         int $this$dp$ivxxxxxx = 8;
         int $i$f$getDpxxxxxx = 0;
         Modifier var67 = PaddingKt.recovered$03b481d31d62885a(var123, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxx), 0.0F, 11, null);
         IconKt.recovered$e00adffb7fe45b02(var108, null, var67, var63, var101, 432 | Painter.$stable, 0);
         String var109 = StringResources_androidKt.stringResource(string.intercom_tickets_view_ticket, var101, 0);
         TextStyle var68 = IntercomTheme.INSTANCE.getTypography(var101, IntercomTheme.$stable).getType04SemiBold();
         long var69 = ColorExtensionsKt.recovered$93702c38c1546c4a(IntercomTheme.INSTANCE.getColors(var101, IntercomTheme.$stable).recovered$202acdf3dbb5c020());
         TextKt.recovered$5b114ab32a4f4190(
            var109, null, var69, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var68, var101, 0, 0, 131066
         );
         ComposerKt.sourceInformationMarkerEnd(var101);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var92.endNode();
         ComposerKt.sourceInformationMarkerEnd(var92);
         ComposerKt.sourceInformationMarkerEnd(var85);
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

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

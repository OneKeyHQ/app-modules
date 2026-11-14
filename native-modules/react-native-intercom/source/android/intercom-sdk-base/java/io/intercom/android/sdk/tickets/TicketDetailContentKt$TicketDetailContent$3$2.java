package io.intercom.android.sdk.tickets;

import androidx.compose.foundation.BackgroundKt;
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
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
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
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.SpanStyle;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.AnnotatedString.Builder;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.models.Ticket$TicketAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$DateTimeAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$FilesAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$ListAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$PrimitiveAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$UnSupported;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.TimeFormatter;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketDetailContent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailContent.kt\nio/intercom/android/sdk/tickets/TicketDetailContentKt$TicketDetailContent$3$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 7 AnnotatedString.kt\nandroidx/compose/ui/text/AnnotatedStringKt\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,372:1\n118#2:373\n118#2:406\n118#2:407\n118#2:440\n118#2:441\n118#2:442\n118#2:454\n118#2:455\n118#2:489\n118#2:490\n87#3:374\n84#3,9:375\n87#3:456\n84#3,9:457\n94#3:495\n94#3:499\n81#4,6:384\n88#4,6:399\n81#4,6:418\n88#4,6:433\n96#4:452\n81#4,6:466\n88#4,6:481\n96#4:494\n96#4:498\n402#5,9:390\n411#5:405\n402#5,9:424\n411#5:439\n412#5,2:450\n402#5,9:472\n411#5:487\n412#5,2:492\n412#5,2:496\n99#6:408\n96#6,9:409\n106#6:453\n1580#7:443\n1398#7,6:444\n1869#8:488\n1870#8:491\n*S KotlinDebug\n*F\n+ 1 TicketDetailContent.kt\nio/intercom/android/sdk/tickets/TicketDetailContentKt$TicketDetailContent$3$2\n*L\n131#1:373\n136#1:406\n139#1:407\n143#1:440\n144#1:441\n149#1:442\n163#1:454\n167#1:455\n175#1:489\n218#1:490\n130#1:374\n130#1:375,9\n165#1:456\n165#1:457,9\n165#1:495\n130#1:499\n130#1:384,6\n130#1:399,6\n133#1:418,6\n133#1:433,6\n133#1:452\n165#1:466,6\n165#1:481,6\n165#1:494\n130#1:498\n130#1:390,9\n130#1:405\n133#1:424,9\n133#1:439\n133#1:450,2\n165#1:472,9\n165#1:487\n165#1:492,2\n130#1:496,2\n133#1:408\n133#1:409,9\n133#1:453\n152#1:443\n153#1:444,6\n170#1:488\n170#1:491\n*E\n"
)
final class TicketDetailContentKt$TicketDetailContent$3$2 implements Function2 {
   // $VF: synthetic field
   final TicketDetailState$TicketDetailContentState $ticketDetailContentState;

   TicketDetailContentKt$TicketDetailContent$3$2(TicketDetailState$TicketDetailContentState var1) {
      this.$ticketDetailContentState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C129@5769L4764:TicketDetailContent.kt#3ofz5f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               925724611, var2, -1, "io.intercom.android.sdk.tickets.TicketDetailContent.<anonymous>.<anonymous> (TicketDetailContent.kt:129)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 16;
         int var4 = 0;
         Modifier var79 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var3));
         TicketDetailState$TicketDetailContentState var6 = this.$ticketDetailContentState;
         int var7 = 6;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var80 = Arrangement.INSTANCE.getTop();
         Horizontal var5 = Alignment.Companion.getStart();
         MeasurePolicy var9 = ColumnKt.columnMeasurePolicy(var80, var5, var1, 14 & var7 >> 3 | 112 & var7 >> 3);
         Modifier var10 = var79;
         MeasurePolicy var11 = var9;
         Composer var12 = var1;
         int var13 = 112 & var7 << 3;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(var12, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var15 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var12, 0));
         CompositionLocalMap var16 = var12.getCurrentCompositionLocalMap();
         Modifier var17 = ComposedModifierKt.materializeModifier(var12, var10);
         Function0 var18 = ComposeUiNode.Companion.getConstructor();
         Composer var19 = var12;
         int var20 = 6 | 896 & var13 << 6;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var19.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var19.startReusableNode();
         if (var19.getInserting()) {
            var19.createNode(var18);
         } else {
            var19.useNode();
         }

         Composer var22 = Updater.recovered$676aa17b44fd6280(var19);
         int var23 = 0;
         Updater.recovered$89682b199a142880(var22, var11, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var22, var16, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var22, var15, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var22, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var22, var17, ComposeUiNode.Companion.getSetModifier());
         int var24 = 14 & var20 >> 6;
         Composer var25 = var19;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var27 = 6 | 112 & var7 >> 6;
         Composer var28 = var25;
         ColumnScope var29 = ColumnScopeInstance.INSTANCE;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, -578538221, "C136@6088L6,132@5861L1486,162@7364L41:TicketDetailContent.kt#3ofz5f");
         androidx.compose.ui.Alignment.Vertical var31 = Alignment.Companion.getCenterVertically();
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         var10000 = SizeKt.fillMaxWidth$default(
            BackgroundKt.recovered$42a3174180b3fb7a(
               ClipKt.clip(var10000, RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060($this$dp$ivx))),
               IntercomTheme.INSTANCE.getColors(var28, IntercomTheme.$stable).recovered$f6e7a825ec69c94c(),
               null,
               2,
               null
            ),
            0.0F,
            1,
            null
         );
         $this$dp$ivx = 12;
         $i$f$getDpx = 0;
         Modifier var34 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         short var88 = 384;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var28, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var85 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var85, var31, var28, 14 & var88 >> 3 | 112 & var88 >> 3);
         Modifier var37 = var34;
         MeasurePolicy var38 = measurePolicy$ivx;
         Composer var39 = var28;
         int var40 = 112 & var88 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var39, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var39, 0));
         CompositionLocalMap localMap$iv$ivx = var39.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var39, var37);
         Function0 var45 = ComposeUiNode.Companion.getConstructor();
         Composer var46 = var39;
         int var47 = 6 | 896 & var40 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var46.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var46.startReusableNode();
         if (var46.getInserting()) {
            var46.createNode(var45);
         } else {
            var46.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var46);
         int var50/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var38, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var51 = 14 & var47 >> 6;
         Composer $composer$ivx = var46;
         int var53 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var54 = 6 | 112 & var88 >> 6;
         Composer var55 = $composer$ivx;
         RowScope var56 = RowScopeInstance.INSTANCE;
         int var57 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var55, 185320927, "C144@6409L61,146@6568L6,140@6234L376,148@6631L40,150@6744L10,158@7243L6,149@6692L637:TicketDetailContent.kt#3ofz5f"
         );
         var10000 = Modifier.Companion;
         int $this$dp$ivxx = 4;
         int $i$f$getDpxx = 0;
         var10000 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 0.0F, 13, null);
         $this$dp$ivxx = 16;
         $i$f$getDpxx = 0;
         Modifier var60 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         Painter var117 = PainterResources_androidKt.painterResource(drawable.intercom_ticket_notification, var55, 0);
         long var61 = IntercomTheme.INSTANCE.getColors(var55, IntercomTheme.$stable).getIcon().recovered$e01a1d3b7b68e21a();
         IconKt.recovered$e00adffb7fe45b02(var117, null, var60, var61, var55, 432 | Painter.$stable, 0);
         var10000 = Modifier.Companion;
         int $this$dp$ivxxx = 16;
         $this$dp$ivxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var55, 6);
         TextStyle var125 = IntercomTheme.INSTANCE.getTypography(var55, IntercomTheme.$stable).getType04();
         var55.startReplaceGroup(1807110638);
         ComposerKt.sourceInformation(var55, "");
         int var63 = 0;
         Builder var64 = new Builder(0, 1, null);
         Builder var65 = var64;
         int var66 = 0;
         var55.startReplaceGroup(1807112122);
         ComposerKt.sourceInformation(var55, "*153@6949L74");
         Builder var67 = var65;
         SpanStyle var68 = new SpanStyle(
            0L, 0L, FontWeight.Companion.getSemiBold(), null, null, null, null, 0L, null, null, null, 0L, null, null, null, null, 65531, null
         );
         int var69 = 0;
         int var70 = var67.pushStyle(var68);

         try {
            Builder var71 = var67;
            int var72 = 0;
            var71.append(StringResources_androidKt.stringResource(string.intercom_youll_be_notified_here_and_by_email, var55, 0));
            var71.append(" ");
            Unit var135 = Unit.INSTANCE;
         } finally {
            var67.pop(var70);
         }

         var55.endReplaceGroup();
         var65.append(var6.getUserEmail());
         AnnotatedString var122 = var64.toAnnotatedString();
         var55.endReplaceGroup();
         AnnotatedString var119 = var122;
         var61 = IntercomTheme.INSTANCE.getColors(var55, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         long var73 = TextUnitKt.getSp(22);
         TextKt.recovered$489343e57da77a35(
            var119, null, var61, null, 0L, null, null, null, 0L, null, null, var73, 0, false, 0, 0, null, null, var125, var55, 0, 48, 260090
         );
         ComposerKt.sourceInformationMarkerEnd(var55);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var46.endNode();
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var39);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var10000 = Modifier.Companion;
         byte var81 = 24;
         boolean var90 = false;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var81)), var28, 6);
         var28.startReplaceGroup(-711350793);
         ComposerKt.sourceInformation(var28, "164@7504L2997");
         if (!var6.getTicketAttributes().isEmpty()) {
            var10000 = Modifier.Companion;
            int $this$dp$ivxxxx = 12;
            int $i$f$getDpxxx = 0;
            Modifier var83 = SizeKt.fillMaxSize$default(
               PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 0.0F, 2, null), 0.0F, 1, null
            );
            byte var89 = 6;
            var35 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var28, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
            Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
            measurePolicy$ivx = ColumnKt.columnMeasurePolicy(verticalArrangement$ivx, horizontalAlignment$ivx, var28, 14 & var89 >> 3 | 112 & var89 >> 3);
            var37 = var83;
            var38 = measurePolicy$ivx;
            var39 = var28;
            var40 = 112 & var89 << 3;
            $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var39, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var39, 0));
            localMap$iv$ivx = var39.getCurrentCompositionLocalMap();
            materialized$iv$ivx = ComposedModifierKt.materializeModifier(var39, var37);
            var45 = ComposeUiNode.Companion.getConstructor();
            var46 = var39;
            var47 = 6 | 896 & var40 << 6;
            $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var46, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var46.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var46.startReusableNode();
            if (var46.getInserting()) {
               var46.createNode(var45);
            } else {
               var46.useNode();
            }

            $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var46);
            var50/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var38, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            var51 = 14 & var47 >> 6;
            $composer$ivx = var46;
            var53 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            var54 = 6 | 112 & var89 >> 6;
            var55 = $composer$ivx;
            ColumnScope var114 = ColumnScopeInstance.INSTANCE;
            var57 = 0;
            ComposerKt.sourceInformationMarkerStart(var55, 186832208, "C:TicketDetailContent.kt#3ofz5f");
            var55.startReplaceGroup(1807143465);
            ComposerKt.sourceInformation(var55, "*172@7893L10,170@7785L164,174@7978L40,217@10412L41");
            Iterable var120 = var6.getTicketAttributes();
            $i$f$getDpxx = 0;

            for (Object var75 : var120) {
               Ticket$TicketAttribute var76 = (Ticket$TicketAttribute)var75;
               var63 = 0;
               TextKt.recovered$5b114ab32a4f4190(
                  var76.getName(),
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
                  IntercomTheme.INSTANCE.getTypography(var55, IntercomTheme.$stable).getType04SemiBold(),
                  var55,
                  0,
                  0,
                  131070
               );
               var10000 = Modifier.Companion;
               int $this$dp$ivxxxxx = 2;
               int $i$f$getDpxxxx = 0;
               SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx)), var55, 6);
               Ticket$TicketAttribute var130 = var76;
               if (var130 instanceof Ticket$TicketAttribute$PrimitiveAttribute) {
                  var55.startReplaceGroup(2134715913);
                  ComposerKt.sourceInformation(var55, "179@8279L10,177@8154L181");
                  TextKt.recovered$5b114ab32a4f4190(
                     ((Ticket$TicketAttribute$PrimitiveAttribute)var76).getValue(),
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
                     IntercomTheme.INSTANCE.getTypography(var55, IntercomTheme.$stable).getType04(),
                     var55,
                     0,
                     0,
                     131070
                  );
                  var55.endReplaceGroup();
               } else if (var130 instanceof Ticket$TicketAttribute$ListAttribute) {
                  var55.startReplaceGroup(2135020457);
                  ComposerKt.sourceInformation(var55, "186@8586L10,184@8461L181");
                  TextKt.recovered$5b114ab32a4f4190(
                     ((Ticket$TicketAttribute$ListAttribute)var76).getValue(),
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
                     IntercomTheme.INSTANCE.getTypography(var55, IntercomTheme.$stable).getType04(),
                     var55,
                     0,
                     0,
                     131070
                  );
                  var55.endReplaceGroup();
               } else if (var130 instanceof Ticket$TicketAttribute$DateTimeAttribute) {
                  var55.startReplaceGroup(2135335386);
                  ComposerKt.sourceInformation(var55, "196@9104L10,191@8772L388");
                  String var133 = TimeFormatter.formatTimeInMillisAsDate(
                     Long.parseLong(((Ticket$TicketAttribute$DateTimeAttribute)var76).getValue()) * 1000, "dd-MM-yyy, HH:mm"
                  );
                  Intrinsics.checkNotNullExpressionValue(var133, "formatTimeInMillisAsDate(...)");
                  TextKt.recovered$5b114ab32a4f4190(
                     var133,
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
                     IntercomTheme.INSTANCE.getTypography(var55, IntercomTheme.$stable).getType04(),
                     var55,
                     0,
                     0,
                     131070
                  );
                  var55.endReplaceGroup();
               } else if (var130 instanceof Ticket$TicketAttribute$FilesAttribute) {
                  var55.startReplaceGroup(2135835354);
                  ComposerKt.sourceInformation(var55, "201@9287L36");
                  FIleAttachmentListKt.FileAttachmentList(null, ((Ticket$TicketAttribute$FilesAttribute)var76).getValue(), var55, 0, 1);
                  var55.endReplaceGroup();
               } else {
                  if (!(var130 instanceof Ticket$TicketAttribute$UnSupported)) {
                     var55.startReplaceGroup(1592882332);
                     var55.endReplaceGroup();
                     throw new NoWhenBranchMatchedException();
                  }

                  var55.startReplaceGroup(2136019990);
                  ComposerKt.sourceInformation(var55, "213@10263L10,211@10127L192");
                  TextKt.recovered$5b114ab32a4f4190(
                     ((Ticket$TicketAttribute$UnSupported)var76).getValue().toString(),
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
                     IntercomTheme.INSTANCE.getTypography(var55, IntercomTheme.$stable).getType04(),
                     var55,
                     0,
                     0,
                     131070
                  );
                  var55.endReplaceGroup();
               }

               var10000 = Modifier.Companion;
               int $this$dp$ivxxxxxx = 16;
               int $i$f$getDpxxxxx = 0;
               SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxx)), var55, 6);
            }

            var55.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var55);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var46.endNode();
            ComposerKt.sourceInformationMarkerEnd(var46);
            ComposerKt.sourceInformationMarkerEnd(var39);
            ComposerKt.sourceInformationMarkerEnd(var28);
         }

         var28.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var25);
         var19.endNode();
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var12);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}

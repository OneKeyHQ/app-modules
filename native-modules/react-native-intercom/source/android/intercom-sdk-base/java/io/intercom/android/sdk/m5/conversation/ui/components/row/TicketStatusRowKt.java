package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import android.text.Spanned;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
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
import androidx.compose.runtime.CompositionLocal;
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
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import androidx.core.text.HtmlCompat;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.TextWithSeparatorKt;
import io.intercom.android.sdk.survey.block.BlockExtensionsKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.Phrase;
import java.util.Locale;
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
   d1 = "\u0000\u0018\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\u001aG\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003H\u0001¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f¨\u0006\r",
   d2 = {
         "TicketStatusRow",
         "",
         "eventStatus",
         "",
         "statusText",
         "time",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "customStateLabel",
         "customStatePrefix",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "SimplePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketStatusRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketStatusRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/TicketStatusRowKt\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,153:1\n87#2,6:154\n94#2:224\n81#3,6:160\n88#3,6:175\n81#3,6:193\n88#3,6:208\n96#3:218\n96#3:223\n81#3,6:234\n88#3,6:249\n96#3:259\n402#4,9:166\n411#4:181\n402#4,9:199\n411#4:214\n412#4,2:216\n412#4,2:221\n402#4,9:240\n411#4:255\n412#4,2:257\n99#5:182\n95#5,10:183\n106#5:219\n99#5,6:228\n106#5:260\n118#6:215\n118#6:220\n118#6:256\n75#7:225\n75#7:226\n75#7:227\n*S KotlinDebug\n*F\n+ 1 TicketStatusRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/TicketStatusRowKt\n*L\n41#1:154,6\n41#1:224\n41#1:160,6\n41#1:175,6\n48#1:193,6\n48#1:208,6\n48#1:218\n41#1:223\n92#1:234,6\n92#1:249,6\n92#1:259\n41#1:166,9\n41#1:181\n48#1:199,9\n48#1:214\n48#1:216,2\n41#1:221,2\n92#1:240,9\n92#1:255\n92#1:257,2\n48#1:182\n48#1:183,10\n48#1:219\n92#1:228,6\n92#1:260\n54#1:215\n65#1:220\n104#1:256\n79#1:225\n84#1:226\n90#1:227\n*E\n"
)
public final class TicketStatusRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketStatusRow(
      @NotNull String var0,
      @NotNull String var1,
      @NotNull String var2,
      @Nullable Modifier var3,
      @Nullable String var4,
      @Nullable String var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var0, "eventStatus");
      Intrinsics.checkNotNullParameter(var1, "statusText");
      Intrinsics.checkNotNullParameter(var2, "time");
      var6 = var6.startRestartGroup(-1382529243);
      ComposerKt.sourceInformation(var6, "C(TicketStatusRow)P(2,4,5,3):TicketStatusRow.kt#hr3bi5");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var6.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var6.changed(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var6.changed(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var6.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var6.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var6.changed(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         if ((var8 & 8) != 0) {
            var3 = Modifier.Companion;
         }

         if ((var8 & 16) != 0) {
            var4 = null;
         }

         if ((var8 & 32) != 0) {
            var5 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1382529243, var9, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.TicketStatusRow (TicketStatusRow.kt:38)"
            );
         }

         if (var4 != null && var5 != null) {
            var6.startReplaceGroup(1097246694);
            ComposerKt.sourceInformation(var6, "40@1638L1403");
            Modifier var74 = SizeKt.wrapContentHeight$default(SizeKt.fillMaxWidth$default(var3, 0.0F, 1, null), null, false, 3, null);
            Vertical var75 = Arrangement.INSTANCE.getCenter();
            Horizontal var78 = Alignment.Companion.getCenterHorizontally();
            short var81 = 432;
            int var85 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var6, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            MeasurePolicy var89 = ColumnKt.columnMeasurePolicy(var75, var78, var6, 14 & var81 >> 3 | 112 & var81 >> 3);
            Modifier var93 = var74;
            MeasurePolicy var95 = var89;
            Composer var96 = var6;
            int var97 = 112 & var81 << 3;
            int var98 = 0;
            ComposerKt.sourceInformationMarkerStart(var96, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int var99 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var96, 0));
            CompositionLocalMap var100 = var96.getCurrentCompositionLocalMap();
            Modifier var101 = ComposedModifierKt.materializeModifier(var96, var93);
            Function0 var102 = ComposeUiNode.Companion.getConstructor();
            Composer var103 = var96;
            int var104 = 6 | 896 & var97 << 6;
            int var105 = 0;
            ComposerKt.sourceInformationMarkerStart(var103, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var103.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var103.startReusableNode();
            if (var103.getInserting()) {
               var103.createNode(var102);
            } else {
               var103.useNode();
            }

            Composer var106 = Updater.recovered$676aa17b44fd6280(var103);
            int var107 = 0;
            Updater.recovered$89682b199a142880(var106, var95, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var106, var100, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var106, var99, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var106, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var106, var101, ComposeUiNode.Companion.getSetModifier());
            int var108 = 14 & var104 >> 6;
            Composer var109 = var103;
            int var110 = 0;
            ComposerKt.sourceInformationMarkerStart(var109, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var111 = 6 | 112 & var81 >> 6;
            Composer var112 = var109;
            ColumnScope var113 = ColumnScopeInstance.INSTANCE;
            int var114 = 0;
            ComposerKt.sourceInformationMarkerStart(var112, -812671778, "C47@1890L723,64@2626L40,67@2764L6,68@2821L10,65@2679L352:TicketStatusRow.kt#hr3bi5");
            int var115 = 0;
            int var117 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var112, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Modifier var119 = Modifier.Companion;
            androidx.compose.foundation.layout.Arrangement.Horizontal var121 = Arrangement.INSTANCE.getStart();
            androidx.compose.ui.Alignment.Vertical var123 = Alignment.Companion.getTop();
            MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var121, var123, var112, 14 & var115 >> 3 | 112 & var115 >> 3);
            Modifier var43 = var119;
            MeasurePolicy var44 = measurePolicy$ivx;
            Composer var45 = var112;
            int var46 = 112 & var115 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var45, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var45, 0));
            CompositionLocalMap localMap$iv$ivx = var45.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var45, var43);
            Function0 var51 = ComposeUiNode.Companion.getConstructor();
            Composer var52 = var45;
            int var53 = 6 | 896 & var46 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var52, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var52.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var52.startReusableNode();
            if (var52.getInserting()) {
               var52.createNode(var51);
            } else {
               var52.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var52);
            int var56/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var44, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var57 = 14 & var53 >> 6;
            Composer $composer$ivx = var52;
            int var59 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var60 = 6 | 112 & var115 >> 6;
            Composer var61 = $composer$ivx;
            RowScope var62 = RowScopeInstance.INSTANCE;
            int var63 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var61, -1952198395, "C49@1948L60,51@2098L6,48@1912L222,53@2151L39,57@2358L6,58@2419L10,54@2207L392:TicketStatusRow.kt#hr3bi5"
            );
            IconKt.recovered$e00adffb7fe45b02(
               PainterResources_androidKt.painterResource(drawable.intercom_ticket_detail_icon, var61, 0),
               null,
               null,
               IntercomTheme.INSTANCE.getColors(var61, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122(),
               var61,
               48 | Painter.$stable,
               4
            );
            Modifier var130 = Modifier.Companion;
            int var64 = 8;
            int var65 = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var130, Dp.recovered$95b3d103663bf060(var64)), var61, 6);
            long var66 = IntercomTheme.INSTANCE.getColors(var61, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
            TextStyle var68 = IntercomTheme.INSTANCE.getTypography(var61, IntercomTheme.$stable).getType04Point5();
            int var69 = TextAlign.Companion.recovered$d707b9dd64876179();
            int var70 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextWithSeparatorKt.recovered$741c28fc37ac0ca8(
               var5,
               var2,
               null,
               null,
               var68,
               var66,
               var70,
               1,
               TextAlign.recovered$18ab2fc4c81b19cd(var69),
               var61,
               14155776 | 14 & var9 >> 15 | 112 & var9 >> 3,
               12
            );
            ComposerKt.sourceInformationMarkerEnd(var61);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var52.endNode();
            ComposerKt.sourceInformationMarkerEnd(var52);
            ComposerKt.sourceInformationMarkerEnd(var45);
            ComposerKt.sourceInformationMarkerEnd(var112);
            var130 = Modifier.Companion;
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var130, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var112, 6);
            long var71 = IntercomTheme.INSTANCE.getColors(var112, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
            TextStyle var124 = IntercomTheme.INSTANCE.getTypography(var112, IntercomTheme.$stable).getType04Point5();
            var115 = TextAlign.Companion.recovered$d707b9dd64876179();
            var117 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            FontWeight var126 = FontWeight.Companion.getBold();
            TextKt.recovered$5b114ab32a4f4190(
               var4,
               null,
               var71,
               null,
               0L,
               null,
               var126,
               null,
               0L,
               null,
               TextAlign.recovered$18ab2fc4c81b19cd(var115),
               0L,
               var117,
               false,
               1,
               0,
               null,
               var124,
               var112,
               1572864 | 14 & var9 >> 12,
               24960,
               109498
            );
            ComposerKt.sourceInformationMarkerEnd(var112);
            ComposerKt.sourceInformationMarkerEnd(var109);
            var103.endNode();
            ComposerKt.sourceInformationMarkerEnd(var103);
            ComposerKt.sourceInformationMarkerEnd(var96);
            ComposerKt.sourceInformationMarkerEnd(var6);
            var6.endReplaceGroup();
         } else {
            var6.startReplaceGroup(1098662774);
            ComposerKt.sourceInformation(var6, "89@3824L7,91@3842L704");
            String recoveredLowerStatus = var1.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(recoveredLowerStatus, "toLowerCase(...)");
            String var10 = recoveredLowerStatus;
            CharSequence var10000;
            if (Intrinsics.areEqual(var0, "submitted")) {
               var6.startReplaceGroup(1098733020);
               ComposerKt.sourceInformation(var6, "78@3202L7");
               CompositionLocal var13 = AndroidCompositionLocals_androidKt.getLocalContext();
               int var14 = 0;
               int var15 = 0;
               ComposerKt.sourceInformationMarkerStart(var6, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
               Object var16 = var6.consume(var13);
               ComposerKt.sourceInformationMarkerEnd(var6);
               CharSequence var12 = Phrase.from((Context)var16, string.intercom_tickets_status_event_submitted)
                  .put("submitted_status", "<b>" + var10 + "</b>")
                  .put("timestamp", var2)
                  .format();
               var6.endReplaceGroup();
               var10000 = var12;
            } else {
               var6.startReplaceGroup(1098981609);
               ComposerKt.sourceInformation(var6, "83@3453L7");
               CompositionLocal var79 = AndroidCompositionLocals_androidKt.getLocalContext();
               int var82 = 0;
               int var86 = 0;
               ComposerKt.sourceInformationMarkerStart(var6, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
               Object var90 = var6.consume(var79);
               ComposerKt.sourceInformationMarkerEnd(var6);
               CharSequence var76 = Phrase.from((Context)var90, string.intercom_tickets_status_change_event)
                  .put("status", "<b>" + var10 + "</b>")
                  .put("timestamp", var2)
                  .format();
               var6.endReplaceGroup();
               var10000 = var76;
            }

            String var11 = var10000.toString();
            Spanned var77 = HtmlCompat.fromHtml(var11, 0);
            Intrinsics.checkNotNull(var77);
            var10000 = var77;
            CompositionLocal var83 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var87 = 0;
            int var91 = 0;
            ComposerKt.sourceInformationMarkerStart(var6, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var17 = var6.consume(var83);
            ComposerKt.sourceInformationMarkerEnd(var6);
            AnnotatedString var80 = BlockExtensionsKt.recovered$c71786fddfec5215(var10000, (Context)var17, null, 0L, 6, null);
            Modifier var84 = SizeKt.wrapContentHeight$default(SizeKt.fillMaxWidth$default(var3, 0.0F, 1, null), null, false, 3, null);
            androidx.compose.foundation.layout.Arrangement.Horizontal var88 = Arrangement.INSTANCE.getCenter();
            androidx.compose.ui.Alignment.Vertical var92 = Alignment.Companion.getCenterVertically();
            short var94 = 432;
            int var18 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var6, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            MeasurePolicy var19 = RowKt.rowMeasurePolicy(var88, var92, var6, 14 & var94 >> 3 | 112 & var94 >> 3);
            Modifier var20 = var84;
            MeasurePolicy var21 = var19;
            Composer var22 = var6;
            int var23 = 112 & var94 << 3;
            int var24 = 0;
            ComposerKt.sourceInformationMarkerStart(var22, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int var25 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var22, 0));
            CompositionLocalMap var26 = var22.getCurrentCompositionLocalMap();
            Modifier var27 = ComposedModifierKt.materializeModifier(var22, var20);
            Function0 var28 = ComposeUiNode.Companion.getConstructor();
            Composer var29 = var22;
            int var30 = 6 | 896 & var23 << 6;
            int var31 = 0;
            ComposerKt.sourceInformationMarkerStart(var29, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var29.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var29.startReusableNode();
            if (var29.getInserting()) {
               var29.createNode(var28);
            } else {
               var29.useNode();
            }

            Composer var32 = Updater.recovered$676aa17b44fd6280(var29);
            int var33 = 0;
            Updater.recovered$89682b199a142880(var32, var21, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var32, var26, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var32, var25, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var32, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var32, var27, ComposeUiNode.Companion.getSetModifier());
            int var34 = 14 & var30 >> 6;
            Composer var35 = var29;
            int var36 = 0;
            ComposerKt.sourceInformationMarkerStart(var35, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var37 = 6 | 112 & var94 >> 6;
            Composer var38 = var35;
            RowScope var39 = RowScopeInstance.INSTANCE;
            int var40 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var38, -810511884, "C99@4121L60,101@4263L6,98@4089L206,103@4308L39,106@4441L6,107@4498L10,104@4360L176:TicketStatusRow.kt#hr3bi5"
            );
            IconKt.recovered$e00adffb7fe45b02(
               PainterResources_androidKt.painterResource(drawable.intercom_ticket_detail_icon, var38, 0),
               null,
               null,
               IntercomTheme.INSTANCE.getColors(var38, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122(),
               var38,
               48 | Painter.$stable,
               4
            );
            Modifier var129 = Modifier.Companion;
            int var41 = 8;
            int var42 = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var129, Dp.recovered$95b3d103663bf060(var41)), var38, 6);
            TextKt.recovered$489343e57da77a35(
               var80,
               null,
               IntercomTheme.INSTANCE.getColors(var38, IntercomTheme.$stable).getText().recovered$0aef7413383f6122(),
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
               null,
               IntercomTheme.INSTANCE.getTypography(var38, IntercomTheme.$stable).getType04Point5(),
               var38,
               0,
               0,
               262138
            );
            ComposerKt.sourceInformationMarkerEnd(var38);
            ComposerKt.sourceInformationMarkerEnd(var35);
            var29.endNode();
            ComposerKt.sourceInformationMarkerEnd(var29);
            ComposerKt.sourceInformationMarkerEnd(var22);
            ComposerKt.sourceInformationMarkerEnd(var6);
            var6.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var132 = var6.endRestartGroup();
      if (var132 != null) {
         final java.lang.String recoveredCapture_797_0 = var0;
         final java.lang.String recoveredCapture_797_1 = var1;
         final java.lang.String recoveredCapture_797_2 = var2;
         final androidx.compose.ui.Modifier recoveredCapture_797_3 = var3;
         final java.lang.String recoveredCapture_797_4 = var4;
         final java.lang.String recoveredCapture_797_5 = var5;
         final int recoveredCapture_797_6 = var7;
         final int recoveredCapture_797_7 = var8;
         var132.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_797_0, recoveredArgument_797_1) -> TicketStatusRowKt.TicketStatusRow$lambda$3(recoveredCapture_797_0, recoveredCapture_797_1, recoveredCapture_797_2, recoveredCapture_797_3, recoveredCapture_797_4, recoveredCapture_797_5, recoveredCapture_797_6, recoveredCapture_797_7, (androidx.compose.runtime.Composer)recoveredArgument_797_0, ((java.lang.Integer)recoveredArgument_797_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SimplePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1725833053);
      ComposerKt.sourceInformation(var0, "C(SimplePreview)116@4621L1239:TicketStatusRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1725833053, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SimplePreview (TicketStatusRow.kt:115)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketStatusRowKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_796_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_796_0, recoveredArgument_796_1) -> TicketStatusRowKt.SimplePreview$lambda$4(recoveredCapture_796_0, (androidx.compose.runtime.Composer)recoveredArgument_796_0, ((java.lang.Integer)recoveredArgument_796_1).intValue())));
      }
   }

   private static final Unit TicketStatusRow$lambda$3(
      String var0, String var1, String var2, Modifier var3, String var4, String var5, int var6, int var7, Composer var8, int var9
   ) {
      TicketStatusRow(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit SimplePreview$lambda$4(int var0, Composer var1, int var2) {
      SimplePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

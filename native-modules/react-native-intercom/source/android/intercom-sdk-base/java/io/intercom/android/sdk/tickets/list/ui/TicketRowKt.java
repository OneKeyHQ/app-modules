package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.material3.TextKt;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.tooling.preview.Preview.Container;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.ConversationItemKt;
import io.intercom.android.sdk.m5.components.IntercomChevronKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
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
   d1 = "\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a3\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f¨\u0006\r",
   d2 = {
         "TicketRow",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "data",
         "Lio/intercom/android/sdk/tickets/list/ui/TicketRowData;",
         "contentPadding",
         "Landroidx/compose/foundation/layout/PaddingValues;",
         "showUnreadIndicator",
         "",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/tickets/list/ui/TicketRowData;Landroidx/compose/foundation/layout/PaddingValues;ZLandroidx/compose/runtime/Composer;II)V",
         "TicketRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketRow.kt\nio/intercom/android/sdk/tickets/list/ui/TicketRowKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n*L\n1#1,121:1\n118#2:122\n118#2:155\n118#2:156\n118#2:189\n118#2:194\n99#3:123\n96#3,9:124\n106#3:198\n81#4,6:133\n88#4,6:148\n81#4,6:167\n88#4,6:182\n96#4:192\n96#4:197\n402#5,9:139\n411#5:154\n402#5,9:173\n411#5:188\n412#5,2:190\n412#5,2:195\n87#6:157\n84#6,9:158\n94#6:193\n*S KotlinDebug\n*F\n+ 1 TicketRow.kt\nio/intercom/android/sdk/tickets/list/ui/TicketRowKt\n*L\n33#1:122\n41#1:155\n52#1:156\n61#1:189\n72#1:194\n36#1:123\n36#1:124,9\n36#1:198\n36#1:133,6\n36#1:148,6\n53#1:167,6\n53#1:182,6\n53#1:192\n36#1:197\n36#1:139,9\n36#1:154\n53#1:173,9\n53#1:188\n53#1:190,2\n36#1:195,2\n53#1:157\n53#1:158,9\n53#1:193\n*E\n"
)
public final class TicketRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketRow(
      @Nullable Modifier var0, @NotNull TicketRowData var1, @Nullable PaddingValues var2, boolean var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "data");
      var4 = var4.startRestartGroup(-1789130333);
      ComposerKt.sourceInformation(var4, "C(TicketRow)P(2,1)35@1495L1458:TicketRow.kt#yl74uv");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var7 |= (var6 & 4) == 0 && var4.changed(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var7 |= (var6 & 8) == 0 && var4.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         var4.startDefaults();
         if ((var5 & 1) != 0 && !var4.getDefaultsInvalid()) {
            var4.skipToGroupEnd();
            if ((var6 & 4) != 0) {
               var7 &= -897;
            }

            if ((var6 & 8) != 0) {
               var7 &= -7169;
            }
         } else {
            if ((var6 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var6 & 4) != 0) {
               int var9 = 16;
               int var10 = 0;
               float var8 = Dp.recovered$95b3d103663bf060(var9);
               var10 = 20;
               int $i$f$getDpx = 0;
               var2 = PaddingKt.recovered$4ebb4826aa57abc6(Dp.recovered$95b3d103663bf060(var10), var8);
               var7 &= -897;
            }

            if ((var6 & 8) != 0) {
               var3 = !var1.isRead();
               var7 &= -7169;
            }
         }

         var4.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1789130333, var7, -1, "io.intercom.android.sdk.tickets.list.ui.TicketRow (TicketRow.kt:34)");
         }

         Modifier var67 = PaddingKt.padding(var0, var2);
         Vertical var70 = Alignment.Companion.getCenterVertically();
         short var71 = 384;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var4, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var68 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var13 = RowKt.rowMeasurePolicy(var68, var70, var4, 14 & var71 >> 3 | 112 & var71 >> 3);
         Modifier var14 = var67;
         MeasurePolicy var15 = var13;
         Composer var16 = var4;
         int var17 = 112 & var71 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var14);
         Function0 var22 = ComposeUiNode.Companion.getConstructor();
         Composer var23 = var16;
         int var24 = 6 | 896 & var17 << 6;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var23.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var23.startReusableNode();
         if (var23.getInserting()) {
            var23.createNode(var22);
         } else {
            var23.useNode();
         }

         Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
         int var27 = 0;
         Updater.recovered$89682b199a142880(var26, var15, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
         int var28 = 14 & var24 >> 6;
         Composer var29 = var23;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var31 = 6 | 112 & var71 >> 6;
         Composer var32 = var29;
         RowScope var33 = RowScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -320014927, "C43@1780L248,39@1625L403,51@2037L40,52@2086L698:TicketRow.kt#yl74uv");
         Modifier var10000 = Modifier.Companion;
         int var35 = 32;
         int var36 = 0;
         SurfaceKt.recovered$ecd79a99e04a9534(
            SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var35)),
            RoundedCornerShapeKt.getCircleShape(),
            Color.recovered$41aa0ee03c2282fb(var1.getStatus().recovered$6f15fcf6c761ad06(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null),
            0L,
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(1329606754, true, new TicketRowKt$TicketRow$1$1(var1), var32, 54),
            var32,
            12582918,
            120
         );
         var10000 = Modifier.Companion;
         var35 = 10;
         var36 = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var35)), var32, 6);
         Modifier var73 = RowScope.weight$default(var33, Modifier.Companion, 1.0F, false, 2, null);
         int var37 = 0;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Vertical var76 = Arrangement.INSTANCE.getTop();
         androidx.compose.ui.Alignment.Horizontal var39 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var76, var39, var32, 14 & var37 >> 3 | 112 & var37 >> 3);
         Modifier var41 = var73;
         MeasurePolicy var42 = measurePolicy$ivx;
         Composer var43 = var32;
         int var44 = 112 & var37 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var43, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var43, 0));
         CompositionLocalMap localMap$iv$ivx = var43.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var43, var41);
         Function0 var49 = ComposeUiNode.Companion.getConstructor();
         Composer var50 = var43;
         int var51 = 6 | 896 & var44 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var50, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var50.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var50.startReusableNode();
         if (var50.getInserting()) {
            var50.createNode(var49);
         } else {
            var50.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var50);
         int var54/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var42, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var55 = 14 & var51 >> 6;
         Composer $composer$ivx = var50;
         int var57 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var58 = 6 | 112 & var37 >> 6;
         Composer var59 = $composer$ivx;
         ColumnScope var60 = ColumnScopeInstance.INSTANCE;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var59, 1185289249, "C56@2248L10,56@2293L6,53@2139L329,60@2481L40,65@2696L10,65@2741L6,61@2534L240:TicketRow.kt#yl74uv"
         );
         String var62 = var1.getTitle();
         TextStyle var63 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var59, IntercomTheme.$stable).getType04(),
            IntercomTheme.INSTANCE.getColors(var59, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
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
            0,
            0,
            0L,
            null,
            null,
            null,
            0,
            0,
            null,
            16777214,
            null
         );
         FontWeight var64 = var1.isRead() ? FontWeight.Companion.getNormal() : FontWeight.Companion.getSemiBold();
         int var65 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$5b114ab32a4f4190(
            var62, null, 0L, null, 0L, null, var64, null, 0L, null, null, 0L, var65, false, 1, 0, null, var63, var59, 0, 24960, 110526
         );
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 4;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var59, 6);
         var62 = var1.getSubTitle();
         int var81 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextStyle var82 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var59, IntercomTheme.$stable).getType04(),
            IntercomTheme.INSTANCE.getColors(var59, IntercomTheme.$stable).getText().recovered$0aef7413383f6122(),
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
            0,
            0,
            0L,
            null,
            null,
            null,
            0,
            0,
            null,
            16777214,
            null
         );
         TextKt.recovered$5b114ab32a4f4190(
            var62, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var81, false, 1, 0, null, var82, var59, 0, 24960, 110590
         );
         ComposerKt.sourceInformationMarkerEnd(var59);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var50.endNode();
         ComposerKt.sourceInformationMarkerEnd(var50);
         ComposerKt.sourceInformationMarkerEnd(var43);
         ComposerKt.sourceInformationMarkerEnd(var32);
         if (var3) {
            var32.startReplaceGroup(-318871183);
            ComposerKt.sourceInformation(var32, "69@2832L17");
            ConversationItemKt.UnreadIndicator(null, var32, 0, 1);
            var32.endReplaceGroup();
         } else {
            var32.startReplaceGroup(-318823288);
            ComposerKt.sourceInformation(var32, "71@2879L58");
            var10000 = Modifier.Companion;
            var35 = 6;
            var36 = 0;
            IntercomChevronKt.IntercomChevron(
               PaddingKt.recovered$03b481d31d62885a(var10000, Dp.recovered$95b3d103663bf060(var35), 0.0F, 0.0F, 0.0F, 14, null), var32, 6, 0
            );
            var32.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var86 = var4.endRestartGroup();
      if (var86 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1603_0 = var0;
         final io.intercom.android.sdk.tickets.list.ui.TicketRowData recoveredCapture_1603_1 = var1;
         final androidx.compose.foundation.layout.PaddingValues recoveredCapture_1603_2 = var2;
         final boolean recoveredCapture_1603_3 = var3;
         final int recoveredCapture_1603_4 = var5;
         final int recoveredCapture_1603_5 = var6;
         var86.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1603_0, recoveredArgument_1603_1) -> TicketRowKt.TicketRow$lambda$2(recoveredCapture_1603_0, recoveredCapture_1603_1, recoveredCapture_1603_2, recoveredCapture_1603_3, recoveredCapture_1603_4, recoveredCapture_1603_5, (androidx.compose.runtime.Composer)recoveredArgument_1603_0, ((java.lang.Integer)recoveredArgument_1603_1).intValue())));
      }
   }

   @Container({@Preview(name = "LTR", locale = "en"), @Preview(name = "RTL", locale = "ar")})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketRowPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1396185263);
      ComposerKt.sourceInformation(var0, "C(TicketRowPreview)80@3083L1610:TicketRow.kt#yl74uv");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1396185263, var1, -1, "io.intercom.android.sdk.tickets.list.ui.TicketRowPreview (TicketRow.kt:79)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketRowKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1604_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1604_0, recoveredArgument_1604_1) -> TicketRowKt.TicketRowPreview$lambda$3(recoveredCapture_1604_0, (androidx.compose.runtime.Composer)recoveredArgument_1604_0, ((java.lang.Integer)recoveredArgument_1604_1).intValue())));
      }
   }

   private static final Unit TicketRow$lambda$2(
      Modifier var0, TicketRowData var1, PaddingValues var2, boolean var3, int var4, int var5, Composer var6, int var7
   ) {
      TicketRow(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit TicketRowPreview$lambda$3(int var0, Composer var1, int var2) {
      TicketRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

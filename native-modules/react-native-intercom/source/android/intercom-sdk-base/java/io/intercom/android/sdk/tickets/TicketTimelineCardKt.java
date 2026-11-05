package io.intercom.android.sdk.tickets;

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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.tooling.preview.Preview.Container;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.m5.components.AvatarGroupKt;
import io.intercom.android.sdk.m5.components.TextWithSeparatorKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.TimeFormatterExtKt;
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
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\n\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b\"\u0014\u0010\f\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f",
   d2 = {
         "TicketTimelineCard",
         "",
         "ticketTimelineCardState",
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/tickets/TicketTimelineCardState;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "WaitingOnCustomerTicketTimelinePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "SubmittedTicketTimelineWithLabelPreview",
         "ResolvedTicketTimelineWithLabelPreview",
         "InProgressTicketTimelineWithLabelPreview",
         "sampleTicketTimelineCardState",
         "getSampleTicketTimelineCardState",
         "()Lio/intercom/android/sdk/tickets/TicketTimelineCardState;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketTimelineCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketTimelineCard.kt\nio/intercom/android/sdk/tickets/TicketTimelineCardKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,201:1\n75#2:202\n118#3:203\n118#3:268\n118#3:273\n118#3:274\n118#3:275\n118#3:276\n87#4:204\n84#4,9:205\n94#4:280\n81#5,6:214\n88#5,6:229\n81#5,6:246\n88#5,6:261\n96#5:271\n96#5:279\n402#6,9:220\n411#6:235\n402#6,9:252\n411#6:267\n412#6,2:269\n412#6,2:277\n99#7:236\n96#7,9:237\n106#7:272\n*S KotlinDebug\n*F\n+ 1 TicketTimelineCard.kt\nio/intercom/android/sdk/tickets/TicketTimelineCardKt\n*L\n32#1:202\n34#1:203\n40#1:268\n44#1:273\n52#1:274\n59#1:275\n66#1:276\n33#1:204\n33#1:205,9\n33#1:280\n33#1:214,6\n33#1:229,6\n37#1:246,6\n37#1:261,6\n37#1:271\n33#1:279\n33#1:220,9\n33#1:235\n37#1:252,9\n37#1:267\n37#1:269,2\n33#1:277,2\n37#1:236\n37#1:237,9\n37#1:272\n*E\n"
)
public final class TicketTimelineCardKt {
   @NotNull
   private static final TicketTimelineCardState sampleTicketTimelineCardState;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketTimelineCard(@NotNull TicketTimelineCardState var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "ticketTimelineCardState");
      var2 = var2.startRestartGroup(926572596);
      ComposerKt.sourceInformation(var2, "C(TicketTimelineCard)P(1)31@1321L7,32@1333L1592:TicketTimelineCard.kt#3ofz5f");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(926572596, var5, -1, "io.intercom.android.sdk.tickets.TicketTimelineCard (TicketTimelineCard.kt:30)");
         }

         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var2.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Context var6 = (Context)var10;
         int var66 = 24;
         var8 = 0;
         Modifier var67 = PaddingKt.recovered$eb6a744cd98cdbca(var1, Dp.recovered$95b3d103663bf060(var66));
         Horizontal var70 = Alignment.Companion.getCenterHorizontally();
         short var71 = 384;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var69 = Arrangement.INSTANCE.getTop();
         MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var69, var70, var2, 14 & var71 >> 3 | 112 & var71 >> 3);
         Modifier var13 = var67;
         MeasurePolicy var14 = var12;
         Composer var15 = var2;
         int var16 = 112 & var71 << 3;
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
         int var30 = 6 | 112 & var71 >> 6;
         Composer var31 = var28;
         ColumnScope var32 = ColumnScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31,
            524687994,
            "C36@1461L239,43@1709L41,47@1971L10,44@1759L349,51@2117L40,54@2262L10,55@2315L6,52@2166L179,65@2695L41,66@2745L174:TicketTimelineCard.kt#3ofz5f"
         );
         Modifier var34 = SizeKt.wrapContentWidth$default(Modifier.Companion, null, false, 3, null);
         int $changed$ivx = 6;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var37 = Arrangement.INSTANCE.getStart();
         androidx.compose.ui.Alignment.Vertical var38 = Alignment.Companion.getTop();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var37, var38, var31, 14 & $changed$ivx >> 3 | 112 & $changed$ivx >> 3);
         Modifier var40 = var34;
         MeasurePolicy var41 = measurePolicy$ivx;
         Composer var42 = var31;
         int var43 = 112 & $changed$ivx << 3;
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

         String var86;
         String var91;
         label71: {
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
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var57 = 6 | 112 & $changed$ivx >> 6;
            Composer var58 = $composer$ivx;
            RowScope var59 = RowScopeInstance.INSTANCE;
            int var60 = 0;
            ComposerKt.sourceInformationMarkerStart(var58, -1307453502, "C37@1519L171:TicketTimelineCard.kt#3ofz5f");
            List var10000 = var0.getAdminAvatars();
            int $this$dp$ivx = 64;
            int var62 = 0;
            AvatarGroupKt.recovered$813c81fac421be56(var10000, null, Dp.recovered$95b3d103663bf060($this$dp$ivx), TextUnitKt.getSp(24), var58, 3456, 2);
            ComposerKt.sourceInformationMarkerEnd(var58);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var49.endNode();
            ComposerKt.sourceInformationMarkerEnd(var49);
            ComposerKt.sourceInformationMarkerEnd(var42);
            ComposerKt.sourceInformationMarkerEnd(var31);
            Modifier var85 = Modifier.Companion;
            int $this$dp$ivxx = 12;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var85, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var31, 6);
            var86 = var0.getStatusLabel();
            Long var10001 = var0.getTimestamp();
            if (var10001 != null) {
               var91 = TimeFormatterExtKt.formattedDateFromLong(var10001, var6);
               if (var91 != null) {
                  break label71;
               }
            }

            var91 = "";
         }

         TextWithSeparatorKt.recovered$741c28fc37ac0ca8(
            var86,
            var91,
            null,
            null,
            IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04SemiBold(),
            var0.recovered$4e526da7fcc96ce2(),
            0,
            0,
            TextAlign.recovered$18ab2fc4c81b19cd(TextAlign.Companion.recovered$d707b9dd64876179()),
            var31,
            0,
            204
         );
         Modifier var87 = Modifier.Companion;
         int $this$dp$ivx = 8;
         int var79 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var87, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var31, 6);
         String var74 = var0.getStatusTitle();
         TextStyle var80 = IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04();
         long var63 = IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$5b114ab32a4f4190(var74, null, var63, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var80, var31, 0, 0, 131066);
         var31.startReplaceGroup(2095162851);
         ComposerKt.sourceInformation(var31, "58@2425L40,61@2585L10,62@2642L6,59@2478L198");
         if (var0.getStatusSubtitle().length() > 0) {
            Modifier var88 = Modifier.Companion;
            int $this$dp$ivxx = 8;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var88, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var31, 6);
            String var76 = var0.getStatusSubtitle();
            TextStyle var82 = IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04();
            var63 = IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
            TextKt.recovered$5b114ab32a4f4190(
               var76, null, var63, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var82, var31, 0, 0, 131066
            );
         }

         var31.endReplaceGroup();
         Modifier var89 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var89, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var31, 6);
         TicketProgressIndicatorKt.recovered$8eea2a3ce3419e18(var0.getProgressSections(), var0.recovered$4e526da7fcc96ce2(), null, var31, 0, 4);
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

      ScopeUpdateScope var90 = var2.endRestartGroup();
      if (var90 != null) {
         final io.intercom.android.sdk.tickets.TicketTimelineCardState recoveredCapture_1556_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_1556_1 = var1;
         final int recoveredCapture_1556_2 = var3;
         final int recoveredCapture_1556_3 = var4;
         var90.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1556_0, recoveredArgument_1556_1) -> TicketTimelineCardKt.TicketTimelineCard$lambda$2(recoveredCapture_1556_0, recoveredCapture_1556_1, recoveredCapture_1556_2, recoveredCapture_1556_3, (androidx.compose.runtime.Composer)recoveredArgument_1556_0, ((java.lang.Integer)recoveredArgument_1556_1).intValue())));
      }
   }

   @Container({@Preview(name = "Light Mode", showBackground = true, uiMode = 16), @Preview(name = "Dark Mode", showBackground = true, uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void WaitingOnCustomerTicketTimelinePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-670677167);
      ComposerKt.sourceInformation(var0, "C(WaitingOnCustomerTicketTimelinePreview)77@3189L902:TicketTimelineCard.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -670677167, var1, -1, "io.intercom.android.sdk.tickets.WaitingOnCustomerTicketTimelinePreview (TicketTimelineCard.kt:76)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketTimelineCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1557_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1557_0, recoveredArgument_1557_1) -> TicketTimelineCardKt.WaitingOnCustomerTicketTimelinePreview$lambda$3(recoveredCapture_1557_0, (androidx.compose.runtime.Composer)recoveredArgument_1557_0, ((java.lang.Integer)recoveredArgument_1557_1).intValue())));
      }
   }

   @Container({@Preview(name = "Light Mode", showBackground = true, uiMode = 16), @Preview(name = "Dark Mode", showBackground = true, uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SubmittedTicketTimelineWithLabelPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1972637636);
      ComposerKt.sourceInformation(var0, "C(SubmittedTicketTimelineWithLabelPreview)106@4356L204:TicketTimelineCard.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1972637636, var1, -1, "io.intercom.android.sdk.tickets.SubmittedTicketTimelineWithLabelPreview (TicketTimelineCard.kt:105)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketTimelineCardKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1555_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1555_0, recoveredArgument_1555_1) -> TicketTimelineCardKt.SubmittedTicketTimelineWithLabelPreview$lambda$4(recoveredCapture_1555_0, (androidx.compose.runtime.Composer)recoveredArgument_1555_0, ((java.lang.Integer)recoveredArgument_1555_1).intValue())));
      }
   }

   @Container({@Preview(name = "Light Mode", showBackground = true, uiMode = 16), @Preview(name = "Dark Mode", showBackground = true, uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ResolvedTicketTimelineWithLabelPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2040249091);
      ComposerKt.sourceInformation(var0, "C(ResolvedTicketTimelineWithLabelPreview)118@4824L887:TicketTimelineCard.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2040249091, var1, -1, "io.intercom.android.sdk.tickets.ResolvedTicketTimelineWithLabelPreview (TicketTimelineCard.kt:117)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketTimelineCardKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1554_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1554_0, recoveredArgument_1554_1) -> TicketTimelineCardKt.ResolvedTicketTimelineWithLabelPreview$lambda$5(recoveredCapture_1554_0, (androidx.compose.runtime.Composer)recoveredArgument_1554_0, ((java.lang.Integer)recoveredArgument_1554_1).intValue())));
      }
   }

   @Container({@Preview(name = "Light Mode", showBackground = true, uiMode = 16), @Preview(name = "Dark Mode", showBackground = true, uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InProgressTicketTimelineWithLabelPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-255211063);
      ComposerKt.sourceInformation(var0, "C(InProgressTicketTimelineWithLabelPreview)147@5977L847:TicketTimelineCard.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -255211063, var1, -1, "io.intercom.android.sdk.tickets.InProgressTicketTimelineWithLabelPreview (TicketTimelineCard.kt:146)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketTimelineCardKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1553_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1553_0, recoveredArgument_1553_1) -> TicketTimelineCardKt.InProgressTicketTimelineWithLabelPreview$lambda$6(recoveredCapture_1553_0, (androidx.compose.runtime.Composer)recoveredArgument_1553_0, ((java.lang.Integer)recoveredArgument_1553_1).intValue())));
      }
   }

   @NotNull
   public static final TicketTimelineCardState getSampleTicketTimelineCardState() {
      return sampleTicketTimelineCardState;
   }

   private static final Unit TicketTimelineCard$lambda$2(TicketTimelineCardState var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      TicketTimelineCard(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit WaitingOnCustomerTicketTimelinePreview$lambda$3(int var0, Composer var1, int var2) {
      WaitingOnCustomerTicketTimelinePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SubmittedTicketTimelineWithLabelPreview$lambda$4(int var0, Composer var1, int var2) {
      SubmittedTicketTimelineWithLabelPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ResolvedTicketTimelineWithLabelPreview$lambda$5(int var0, Composer var1, int var2) {
      ResolvedTicketTimelineWithLabelPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InProgressTicketTimelineWithLabelPreview$lambda$6(int var0, Composer var1, int var2) {
      InProgressTicketTimelineWithLabelPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   static {
      Avatar var10004 = Avatar.create("https://static.intercomassets.com/avatars/5355644/square_128/002Bizimply-Headshots-1641919551.jpeg", "");
      Intrinsics.checkNotNullExpressionValue(var10004, "create(...)");
      List var10002 = CollectionsKt.listOf(new AvatarWrapper(var10004, false, 2, null));
      long var10005 = TicketStatus.Submitted.recovered$6f15fcf6c761ad06();
      TicketTimelineCardState$ProgressSection[] var0 = new TicketTimelineCardState$ProgressSection[]{
         new TicketTimelineCardState$ProgressSection(true, true),
         new TicketTimelineCardState$ProgressSection(false, false),
         new TicketTimelineCardState$ProgressSection(false, false)
      };
      sampleTicketTimelineCardState = new TicketTimelineCardState(
         var10002,
         "Hannah will pick this up soon \ud83d\ude4c",
         "\ud83d\udd51  Estimated to be resolved today at 4pm",
         var10005,
         CollectionsKt.listOf(var0),
         "Submitted",
         System.currentTimeMillis(),
         null
      );
   }
}

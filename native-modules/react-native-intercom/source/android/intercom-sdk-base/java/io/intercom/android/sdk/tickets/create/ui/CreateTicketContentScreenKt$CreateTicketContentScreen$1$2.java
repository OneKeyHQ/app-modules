package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.ProgressIndicatorKt;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCreateTicketContentScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/CreateTicketContentScreenKt$CreateTicketContentScreen$1$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,374:1\n118#2:375\n118#2:376\n118#2:410\n118#2:411\n99#3:377\n95#3,10:378\n106#3:415\n81#4,6:388\n88#4,6:403\n96#4:414\n402#5,9:394\n411#5:409\n412#5,2:412\n*S KotlinDebug\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/CreateTicketContentScreenKt$CreateTicketContentScreen$1$2\n*L\n178#1:375\n179#1:376\n188#1:410\n193#1:411\n182#1:377\n182#1:378,10\n182#1:415\n182#1:388,6\n182#1:403,6\n182#1:414\n182#1:394,9\n182#1:409\n182#1:412,2\n*E\n"
)
final class CreateTicketContentScreenKt$CreateTicketContentScreen$1$2 implements Function3 {
   // $VF: synthetic field
   final CreateTicketViewModel$CreateTicketFormUiState$Content $state;

   CreateTicketContentScreenKt$CreateTicketContentScreen$1$2(CreateTicketViewModel$CreateTicketFormUiState$Content var1) {
      this.$state = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$LegacyIntercomPrimaryButton");
      ComposerKt.sourceInformation(var2, "C:CreateTicketContentScreen.kt#xh6pmz");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -964987781,
               var3,
               -1,
               "io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreen.<anonymous>.<anonymous> (CreateTicketContentScreen.kt:175)"
            );
         }

         if (this.$state.getShowCreatingTicketProgress()) {
            var2.startReplaceGroup(-265464223);
            ComposerKt.sourceInformation(var2, "176@7795L137");
            Modifier var10000 = Modifier.Companion;
            int var4 = 24;
            int var5 = 0;
            var10000 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var4));
            var4 = 2;
            var5 = 0;
            ProgressIndicatorKt.recovered$276fbc1f701bb35f(var10000, 0L, Dp.recovered$95b3d103663bf060(var4), 0L, 0, 0.0F, var2, 390, 58);
            var2.endReplaceGroup();
         } else {
            var2.startReplaceGroup(-265272736);
            ComposerKt.sourceInformation(var2, "181@7970L714");
            Vertical var6 = Alignment.Companion.getCenterVertically();
            int var7 = 384;
            int var8 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Modifier var41 = Modifier.Companion;
            Horizontal var43 = Arrangement.INSTANCE.getStart();
            MeasurePolicy var9 = RowKt.rowMeasurePolicy(var43, var6, var2, 14 & var7 >> 3 | 112 & var7 >> 3);
            Modifier var10 = var41;
            MeasurePolicy var11 = var9;
            Composer var12 = var2;
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
            ComposerKt.sourceInformationMarkerStart(var25, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var27 = 6 | 112 & var7 >> 6;
            Composer var28 = var25;
            RowScope var29 = RowScopeInstance.INSTANCE;
            int var30 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var28,
               -2104899138,
               "C183@8081L60,184@8189L10,185@8254L6,182@8044L248,187@8313L39,189@8413L60,191@8571L6,188@8373L293:CreateTicketContentScreen.kt#xh6pmz"
            );
            String var31 = StringResources_androidKt.stringResource(string.intercom_tickets_create_ticket, var28, 0);
            TextStyle var32 = IntercomTheme.INSTANCE.getTypography(var28, IntercomTheme.$stable).getType04();
            long var33 = IntercomTheme.INSTANCE.getColors(var28, IntercomTheme.$stable).recovered$8a5d9ad2993bccce();
            TextKt.recovered$5b114ab32a4f4190(
               var31, null, var33, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var32, var28, 0, 0, 131066
            );
            Modifier var48 = Modifier.Companion;
            int var44 = 6;
            int var46 = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var48, Dp.recovered$95b3d103663bf060(var44)), var28, 6);
            Painter var45 = PainterResources_androidKt.painterResource(drawable.intercom_ticket_detail_icon, var28, 0);
            long var35 = IntercomTheme.INSTANCE.getColors(var28, IntercomTheme.$stable).recovered$8a5d9ad2993bccce();
            var48 = Modifier.Companion;
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            Modifier var39 = SizeKt.recovered$111ef94c93e78c04(var48, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            IconKt.recovered$e00adffb7fe45b02(var45, null, var39, var35, var28, 432 | Painter.$stable, 0);
            ComposerKt.sourceInformationMarkerEnd(var28);
            ComposerKt.sourceInformationMarkerEnd(var25);
            var19.endNode();
            ComposerKt.sourceInformationMarkerEnd(var19);
            ComposerKt.sourceInformationMarkerEnd(var12);
            ComposerKt.sourceInformationMarkerEnd(var2);
            var2.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

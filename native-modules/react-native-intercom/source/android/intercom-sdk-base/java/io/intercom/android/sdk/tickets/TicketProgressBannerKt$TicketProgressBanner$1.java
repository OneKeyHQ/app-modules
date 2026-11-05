package io.intercom.android.sdk.tickets;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.InteractionSourceKt;
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
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketProgressBanner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketProgressBanner.kt\nio/intercom/android/sdk/tickets/TicketProgressBannerKt$TicketProgressBanner$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,113:1\n1047#2,6:114\n87#3:120\n84#3,9:121\n94#3:191\n81#4,6:130\n88#4,6:145\n81#4,6:160\n88#4,6:175\n96#4:185\n96#4:190\n402#5,9:136\n411#5:151\n402#5,9:166\n411#5:181\n412#5,2:183\n412#5,2:188\n118#6:152\n118#6:153\n118#6:182\n118#6:187\n99#7,6:154\n106#7:186\n*S KotlinDebug\n*F\n+ 1 TicketProgressBanner.kt\nio/intercom/android/sdk/tickets/TicketProgressBannerKt$TicketProgressBanner$1\n*L\n62#1:114,6\n56#1:120\n56#1:121,9\n56#1:191\n56#1:130,6\n56#1:145,6\n68#1:160,6\n68#1:175,6\n68#1:185\n56#1:190\n56#1:136,9\n56#1:151\n68#1:166,9\n68#1:181\n68#1:183,2\n56#1:188,2\n67#1:152\n72#1:153\n81#1:182\n92#1:187\n68#1:154,6\n68#1:186\n*E\n"
)
final class TicketProgressBannerKt$TicketProgressBanner$1 implements Function3 {
   // $VF: synthetic field
   final Function0 $onClick;
   // $VF: synthetic field
   final String $name;

   TicketProgressBannerKt$TicketProgressBanner$1(Function0 var1, String var2) {
      this.$onClick = var1;
      this.$name = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C59@2217L6,61@2303L39,55@2066L1560:TicketProgressBanner.kt#3ofz5f");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(417589981, var3, -1, "io.intercom.android.sdk.tickets.TicketProgressBanner.<anonymous> (TicketProgressBanner.kt:55)");
      }

      Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
         SizeKt.wrapContentHeight$default(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), null, false, 3, null),
         IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
         null,
         2,
         null
      );
      var2.startReplaceGroup(669306726);
      ComposerKt.sourceInformation(var2, "CC(remember):TicketProgressBanner.kt#9igjgp");
      Composer var5 = var2;
      boolean var6 = false;
      int var7 = 0;
      Object var8 = var5.rememberedValue();
      int var9 = 0;
      Object var86;
      if (var8 == Composer.Companion.getEmpty()) {
         Modifier var64 = var10000;
         int var10 = 0;
         MutableInteractionSource var10001 = InteractionSourceKt.MutableInteractionSource();
         var10000 = var64;
         Object var71 = var10001;
         var5.updateRememberedValue(var71);
         var86 = var71;
      } else {
         var86 = var8;
      }

      MutableInteractionSource var4 = (MutableInteractionSource)var86;
      var2.endReplaceGroup();
      Modifier var65 = ClickableKt.recovered$1f84abc9b53ba787(var10000, var4, null, false, null, null, this.$onClick, 28, null);
      String var68 = this.$name;
      int var69 = 0;
      int var70 = 0;
      ComposerKt.sourceInformationMarkerStart(
         var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
      );
      Vertical var66 = Arrangement.INSTANCE.getTop();
      Horizontal var67 = Alignment.Companion.getStart();
      MeasurePolicy var72 = ColumnKt.columnMeasurePolicy(var66, var67, var2, 14 & var69 >> 3 | 112 & var69 >> 3);
      Modifier var11 = var65;
      MeasurePolicy var12 = var72;
      Composer var13 = var2;
      int var14 = 112 & var69 << 3;
      int var15 = 0;
      ComposerKt.sourceInformationMarkerStart(var13, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
      int var16 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var13, 0));
      CompositionLocalMap var17 = var13.getCurrentCompositionLocalMap();
      Modifier var18 = ComposedModifierKt.materializeModifier(var13, var11);
      Function0 var19 = ComposeUiNode.Companion.getConstructor();
      Composer var20 = var13;
      int var21 = 6 | 896 & var14 << 6;
      int var22 = 0;
      ComposerKt.sourceInformationMarkerStart(var20, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
      if (!(var20.getApplier() instanceof Applier)) {
         ComposablesKt.invalidApplier();
      }

      var20.startReusableNode();
      if (var20.getInserting()) {
         var20.createNode(var19);
      } else {
         var20.useNode();
      }

      Composer var23 = Updater.recovered$676aa17b44fd6280(var20);
      int var24 = 0;
      Updater.recovered$89682b199a142880(var23, var12, ComposeUiNode.Companion.getSetMeasurePolicy());
      Updater.recovered$89682b199a142880(var23, var17, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
      Updater.recovered$89682b199a142880(var23, var16, ComposeUiNode.Companion.getSetCompositeKeyHash());
      Updater.recovered$3a70c9faca87a3d9(var23, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
      Updater.recovered$89682b199a142880(var23, var18, ComposeUiNode.Companion.getSetModifier());
      int var25 = 14 & var21 >> 6;
      Composer var26 = var20;
      int var27 = 0;
      ComposerKt.sourceInformationMarkerStart(var26, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
      int var28 = 6 | 112 & var69 >> 6;
      Composer var29 = var26;
      ColumnScope var30 = ColumnScopeInstance.INSTANCE;
      int var31 = 0;
      ComposerKt.sourceInformationMarkerStart(var29, -964469722, "C66@2465L41,67@2519L1013,91@3545L41,92@3599L17:TicketProgressBanner.kt#3ofz5f");
      Modifier var82 = Modifier.Companion;
      int var32 = 12;
      int var33 = 0;
      SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var82, Dp.recovered$95b3d103663bf060(var32)), var29, 6);
      Modifier var83 = SizeKt.wrapContentHeight$default(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), null, false, 3, null);
      int $this$dp$ivx = 16;
      int $i$f$getDpx = 0;
      Modifier var74 = PaddingKt.recovered$f3b9f2e94c07c806(var83, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 2, null);
      androidx.compose.foundation.layout.Arrangement.Horizontal var77 = Arrangement.INSTANCE.getCenter();
      androidx.compose.ui.Alignment.Vertical var34 = Alignment.Companion.getCenterVertically();
      short var35 = 438;
      int var36 = 0;
      ComposerKt.sourceInformationMarkerStart(
         var29, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
      );
      MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var77, var34, var29, 14 & var35 >> 3 | 112 & var35 >> 3);
      Modifier var38 = var74;
      MeasurePolicy var39 = measurePolicy$ivx;
      Composer var40 = var29;
      int var41 = 112 & var35 << 3;
      int $i$f$Layoutx = 0;
      ComposerKt.sourceInformationMarkerStart(var40, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
      int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var40, 0));
      CompositionLocalMap localMap$iv$ivx = var40.getCurrentCompositionLocalMap();
      Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var40, var38);
      Function0 var46 = ComposeUiNode.Companion.getConstructor();
      Composer var47 = var40;
      int var48 = 6 | 896 & var41 << 6;
      int $i$f$ReusableComposeNodex = 0;
      ComposerKt.sourceInformationMarkerStart(var47, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
      if (!(var47.getApplier() instanceof Applier)) {
         ComposablesKt.invalidApplier();
      }

      var47.startReusableNode();
      if (var47.getInserting()) {
         var47.createNode(var46);
      } else {
         var47.useNode();
      }

      Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var47);
      int var51/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
      Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var39, ComposeUiNode.Companion.getSetMeasurePolicy());
      Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
      Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
      Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
      Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
      int var52 = 14 & var48 >> 6;
      Composer $composer$ivx = var47;
      int var54 = 0;
      ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
      int var55 = 6 | 112 & var35 >> 6;
      Composer var56 = $composer$ivx;
      RowScope var57 = RowScopeInstance.INSTANCE;
      int var58 = 0;
      ComposerKt.sourceInformationMarkerStart(
         var56, -1925999069, "C76@2879L60,78@3029L6,75@2843L231,80@3091L39,83@3228L10,85@3362L6,81@3147L371:TicketProgressBanner.kt#3ofz5f"
      );
      IconKt.recovered$e00adffb7fe45b02(
         PainterResources_androidKt.painterResource(drawable.intercom_ticket_detail_icon, var56, 0),
         null,
         null,
         IntercomTheme.INSTANCE.getColors(var56, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d(),
         var56,
         48 | Painter.$stable,
         4
      );
      Modifier var84 = Modifier.Companion;
      int $this$dp$ivxx = 8;
      int $i$f$getDpxx = 0;
      SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var84, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var56, 6);
      TextStyle var80 = IntercomTheme.INSTANCE.getTypography(var56, IntercomTheme.$stable).getType04Point5();
      FontWeight var61 = FontWeight.Companion.getSemiBold();
      long var62 = IntercomTheme.INSTANCE.getColors(var56, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
      TextStyle var79 = TextStyle.recovered$cf01d8b71690b5c5(
         var80, var62, 0L, var61, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, 0L, null, null, null, 0, 0, null, 16777210, null
      );
      int var81 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
      TextKt.recovered$5b114ab32a4f4190(
         var68, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var81, false, 1, 0, null, var79, var56, 0, 24960, 110590
      );
      ComposerKt.sourceInformationMarkerEnd(var56);
      ComposerKt.sourceInformationMarkerEnd($composer$ivx);
      var47.endNode();
      ComposerKt.sourceInformationMarkerEnd(var47);
      ComposerKt.sourceInformationMarkerEnd(var40);
      ComposerKt.sourceInformationMarkerEnd(var29);
      Modifier var85 = Modifier.Companion;
      int $this$dp$ivxxx = 12;
      int $i$f$getDpxxx = 0;
      SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var85, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var29, 6);
      IntercomDividerKt.IntercomDivider(null, var29, 0, 1);
      ComposerKt.sourceInformationMarkerEnd(var29);
      ComposerKt.sourceInformationMarkerEnd(var26);
      var20.endNode();
      ComposerKt.sourceInformationMarkerEnd(var20);
      ComposerKt.sourceInformationMarkerEnd(var13);
      ComposerKt.sourceInformationMarkerEnd(var2);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((AnimatedVisibilityScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

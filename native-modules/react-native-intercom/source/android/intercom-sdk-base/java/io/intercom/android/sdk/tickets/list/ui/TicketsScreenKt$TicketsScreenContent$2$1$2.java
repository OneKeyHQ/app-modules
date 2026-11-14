package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketsScreen.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenKt$TicketsScreenContent$2$1$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n*L\n1#1,147:1\n118#2:148\n70#3:149\n68#3,8:150\n77#3:220\n81#4,6:158\n88#4,6:173\n81#4,6:191\n88#4,6:206\n96#4:215\n96#4:219\n402#5,9:164\n411#5:179\n402#5,9:197\n411#5,3:212\n412#5,2:217\n87#6:180\n83#6,10:181\n94#6:216\n*S KotlinDebug\n*F\n+ 1 TicketsScreen.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenKt$TicketsScreenContent$2$1$2\n*L\n110#1:148\n108#1:149\n108#1:150,8\n108#1:220\n108#1:158,6\n108#1:173,6\n114#1:191,6\n114#1:206,6\n114#1:215\n108#1:219\n108#1:164,9\n108#1:179\n114#1:197,9\n114#1:212,3\n108#1:217,2\n114#1:180\n114#1:181,10\n114#1:216\n*E\n"
)
final class TicketsScreenKt$TicketsScreenContent$2$1$2 implements Function3 {
   // $VF: synthetic field
   final ErrorState $errorState;

   TicketsScreenKt$TicketsScreenContent$2$1$2(ErrorState var1) {
      this.$errorState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C107@4147L891:TicketsScreen.kt#yl74uv");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1511938327, var3, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsScreenContent.<anonymous>.<anonymous>.<anonymous> (TicketsScreen.kt:107)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 16;
         int var5 = 0;
         Modifier var59 = SizeKt.fillMaxWidth$default(
            PaddingKt.recovered$f3b9f2e94c07c806(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var4), 1, null), 0.0F, 1, null
         );
         Alignment var60 = Alignment.Companion.getCenter();
         ErrorState var7 = this.$errorState;
         int var8 = 54;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var6 = false;
         MeasurePolicy var10 = BoxKt.maybeCachedBoxMeasurePolicy(var60, var6);
         Modifier var11 = var59;
         MeasurePolicy var12 = var10;
         Composer var13 = var2;
         int var14 = 112 & var8 << 3;
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
         ComposerKt.sourceInformationMarkerStart(var26, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var28 = 6 | 112 & var8 >> 6;
         Composer var29 = var26;
         BoxScope var30 = BoxScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -992631459, "C113@4381L639:TicketsScreen.kt#yl74uv");
         Horizontal var32 = Alignment.Companion.getCenterHorizontally();
         short var33 = 384;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var29, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var35 = Modifier.Companion;
         Vertical var36 = Arrangement.INSTANCE.getTop();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var36, var32, var29, 14 & var33 >> 3 | 112 & var33 >> 3);
         Modifier var38 = var35;
         MeasurePolicy var39 = measurePolicy$ivx;
         Composer var40 = var29;
         int var41 = 112 & var33 << 3;
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
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var55 = 6 | 112 & var33 >> 6;
         Composer var56 = $composer$ivx;
         ColumnScope var57 = ColumnScopeInstance.INSTANCE;
         int var58 = 0;
         ComposerKt.sourceInformationMarkerStart(var56, -116198760, "C116@4525L44,116@4513L57:TicketsScreen.kt#yl74uv");
         TextKt.recovered$5b114ab32a4f4190(
            StringResources_androidKt.stringResource(var7.getMessageResId(), var56, 0),
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
            null,
            var56,
            0,
            0,
            262142
         );
         var56.startReplaceGroup(-1943408455);
         ComposerKt.sourceInformation(var56, "118@4707L265,118@4663L309");
         if (var7 instanceof ErrorState$WithCTA) {
            ButtonKt.TextButton(
               ((ErrorState$WithCTA)var7).getOnCtaClick(),
               null,
               false,
               null,
               null,
               null,
               null,
               null,
               null,
               ComposableLambdaKt.rememberComposableLambda(-774229187, true, new TicketsScreenKt$TicketsScreenContent$2$1$2$1$1$1(var7), var56, 54),
               var56,
               805306368,
               510
            );
         }

         var56.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var56);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var47.endNode();
         ComposerKt.sourceInformationMarkerEnd(var47);
         ComposerKt.sourceInformationMarkerEnd(var40);
         ComposerKt.sourceInformationMarkerEnd(var29);
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
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

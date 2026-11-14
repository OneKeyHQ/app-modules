package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.foundation.BackgroundKt;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCreateTicketCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketCard.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketCardKt$lambda-3$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n*L\n1#1,196:1\n118#2:197\n118#2:198\n118#2:230\n87#3:199\n85#3,8:200\n94#3:269\n81#4,6:208\n88#4,6:223\n81#4,6:240\n88#4,6:255\n96#4:264\n96#4:268\n402#5,9:214\n411#5:229\n402#5,9:246\n411#5,3:261\n412#5,2:266\n70#6:231\n68#6,8:232\n77#6:265\n*S KotlinDebug\n*F\n+ 1 CreateTicketCard.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketCardKt$lambda-3$1\n*L\n155#1:197\n156#1:198\n167#1:230\n152#1:199\n152#1:200,8\n152#1:269\n152#1:208,6\n152#1:223,6\n164#1:240,6\n164#1:255,6\n164#1:264\n152#1:268\n152#1:214,9\n152#1:229\n164#1:246,9\n164#1:261,3\n152#1:266,2\n164#1:231\n164#1:232,8\n164#1:265\n*E\n"
)
final class ComposableSingletons$CreateTicketCardKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$CreateTicketCardKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$CreateTicketCardKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C153@6247L6,151@6165L1075:CreateTicketCard.kt#xh6pmz");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               192804812,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketCardKt.lambda-3.<anonymous> (CreateTicketCard.kt:151)"
            );
         }

         Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
            Modifier.Companion, IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
         );
         int var3 = 16;
         int var4 = 0;
         Modifier var57 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var3));
         var4 = 16;
         int $i$f$getDpx = 0;
         Vertical var59 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var4));
         byte var6 = 48;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var60 = Alignment.Companion.getStart();
         MeasurePolicy var8 = ColumnKt.columnMeasurePolicy(var59, var60, var1, 14 & var6 >> 3 | 112 & var6 >> 3);
         Modifier var9 = var57;
         MeasurePolicy var10 = var8;
         Composer var11 = var1;
         int var12 = 112 & var6 << 3;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(var11, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var14 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var11, 0));
         CompositionLocalMap var15 = var11.getCurrentCompositionLocalMap();
         Modifier var16 = ComposedModifierKt.materializeModifier(var11, var9);
         Function0 var17 = ComposeUiNode.Companion.getConstructor();
         Composer var18 = var11;
         int var19 = 6 | 896 & var12 << 6;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var18.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var18.startReusableNode();
         if (var18.getInserting()) {
            var18.createNode(var17);
         } else {
            var18.useNode();
         }

         Composer var21 = Updater.recovered$676aa17b44fd6280(var18);
         int var22 = 0;
         Updater.recovered$89682b199a142880(var21, var10, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var21, var15, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var21, var14, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var21, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var21, var16, ComposeUiNode.Companion.getSetModifier());
         int var23 = 14 & var19 >> 6;
         Composer var24 = var18;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var26 = 6 | 112 & var6 >> 6;
         Composer var27 = var24;
         ColumnScope var28 = ColumnScopeInstance.INSTANCE;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, -594387254, "C157@6385L135,167@6892L6,163@6734L496:CreateTicketCard.kt#xh6pmz");
         Block var10003 = CreateTicketCardKt.access$getSampleBlock$p();
         Intrinsics.checkNotNullExpressionValue(var10003, "access$getSampleBlock$p(...)");
         CreateTicketCardKt.CreateTicketCard(null, new BlockRenderData(var10003, null, null, null, null, 30, null), true, null, var27, 384, 9);
         var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int $this$dp$ivx = 96;
         int $i$f$getDpxx = 0;
         Modifier var61 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)),
            IntercomTheme.INSTANCE.getColors(var27, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d(),
            null,
            2,
            null
         );
         Alignment var62 = Alignment.Companion.getCenter();
         byte var32 = 48;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var27, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var34 = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var62, var34);
         Modifier var36 = var61;
         MeasurePolicy var37 = measurePolicy$ivx;
         Composer var38 = var27;
         int var39 = 112 & var32 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var38, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var38, 0));
         CompositionLocalMap localMap$iv$ivx = var38.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var38, var36);
         Function0 var44 = ComposeUiNode.Companion.getConstructor();
         Composer var45 = var38;
         int var46 = 6 | 896 & var39 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var45, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var45.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var45.startReusableNode();
         if (var45.getInserting()) {
            var45.createNode(var44);
         } else {
            var45.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var45);
         int var49/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var37, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var50 = 14 & var46 >> 6;
         Composer $composer$ivx = var45;
         int var52 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var53 = 6 | 112 & var32 >> 6;
         Composer var54 = $composer$ivx;
         BoxScope var55 = BoxScopeInstance.INSTANCE;
         int var56 = 0;
         ComposerKt.sourceInformationMarkerStart(var54, 1311717414, "C172@7100L6,173@7172L10,170@7006L210:CreateTicketCard.kt#xh6pmz");
         TextKt.recovered$5b114ab32a4f4190(
            "Action contrast",
            null,
            IntercomTheme.INSTANCE.getColors(var54, IntercomTheme.$stable).recovered$b32e6d5b37f98566(),
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
            IntercomTheme.INSTANCE.getTypography(var54, IntercomTheme.$stable).getType04SemiBold(),
            var54,
            6,
            0,
            131066
         );
         ComposerKt.sourceInformationMarkerEnd(var54);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var45.endNode();
         ComposerKt.sourceInformationMarkerEnd(var45);
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var24);
         var18.endNode();
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var11);
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

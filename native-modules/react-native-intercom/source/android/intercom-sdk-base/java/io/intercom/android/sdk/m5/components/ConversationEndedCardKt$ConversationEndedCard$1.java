package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
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
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationEndedCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationEndedCard.kt\nio/intercom/android/sdk/m5/components/ConversationEndedCardKt$ConversationEndedCard$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,124:1\n118#2:125\n118#2:154\n87#3,6:126\n94#3:164\n81#4,6:132\n88#4,6:147\n96#4:163\n402#5,9:138\n411#5:153\n412#5,2:161\n1047#6,6:155\n*S KotlinDebug\n*F\n+ 1 ConversationEndedCard.kt\nio/intercom/android/sdk/m5/components/ConversationEndedCardKt$ConversationEndedCard$1\n*L\n40#1:125\n49#1:154\n37#1:126,6\n37#1:164\n37#1:132,6\n37#1:147,6\n37#1:163\n37#1:138,9\n37#1:153\n37#1:161,2\n53#1:155,6\n*E\n"
)
final class ConversationEndedCardKt$ConversationEndedCard$1 implements Function3 {
   // $VF: synthetic field
   final ComposerState$ConversationEnded $conversationEndedUiState;
   // $VF: synthetic field
   final Function0 $onButtonClick;

   ConversationEndedCardKt$ConversationEndedCard$1(ComposerState$ConversationEnded var1, Function0 var2) {
      this.$conversationEndedUiState = var1;
      this.$onButtonClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$Card");
      ComposerKt.sourceInformation(var2, "C36@1394L877:ConversationEndedCard.kt#arqjag");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -676087732, var3, -1, "io.intercom.android.sdk.m5.components.ConversationEndedCard.<anonymous> (ConversationEndedCard.kt:36)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var4 = 20;
         int var5 = 0;
         Modifier var44 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var4));
         Vertical var45 = Arrangement.INSTANCE.getCenter();
         Horizontal var6 = Alignment.Companion.getCenterHorizontally();
         ComposerState$ConversationEnded var7 = this.$conversationEndedUiState;
         Function0 var8 = this.$onButtonClick;
         int var9 = 438;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var45, var6, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var44;
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
         ComposerKt.sourceInformationMarkerStart(var30, 1976952030, "C44@1709L9,45@1758L10,43@1647L143:ConversationEndedCard.kt#arqjag");
         TextKt.recovered$5b114ab32a4f4190(
            var7.getMessage().getText(var30, StringProvider.$stable),
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
            IntercomTheme.INSTANCE.getTypography(var30, IntercomTheme.$stable).getType04(),
            var30,
            0,
            0,
            131070
         );
         var30.startReplaceGroup(-1044601175);
         ComposerKt.sourceInformation(var30, "48@1863L41,51@2088L9,49@1921L326");
         if (var7.getCta() != null) {
            var10000 = Modifier.Companion;
            int $this$dp$ivx = 12;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var30, 6);
            Modifier var46 = TestTagKt.testTag(Modifier.Companion, "start-new-conversation-button");
            String var47 = var7.getCta().getTitle().getText(var30, StringProvider.$stable);
            var30.startReplaceGroup(-1044591182);
            ComposerKt.sourceInformation(var30, "52@2146L2");
            Function0 var51 = var8;
            if (var8 == null) {
               var30.startReplaceGroup(-1044590655);
               ComposerKt.sourceInformation(var30, "CC(remember):ConversationEndedCard.kt#9igjgp");
               Composer var35 = var30;
               boolean var36 = false;
               int var37 = 0;
               Object var38 = var35.rememberedValue();
               int var39 = 0;
               Object var52;
               if (var38 == Composer.Companion.getEmpty()) {
                  int var40 = 0;
                  Object var48 = ((kotlin.jvm.functions.Function0) () -> ConversationEndedCardKt$ConversationEndedCard$1.invoke$lambda$2$lambda$1$lambda$0());
                  var35.updateRememberedValue(var48);
                  var52 = var48;
               } else {
                  var52 = var38;
               }

               Function0 var41 = (Function0)var52;
               var30.endReplaceGroup();
               var51 = var41;
            }

            Function0 var42 = var51;
            var30.endReplaceGroup();
            Function0 var43 = var42;
            int var49 = var7.getCta().getTrailingIcon();
            IntercomPrimaryButtonKt.IntercomPrimaryButton(var47, var46, var49, var43, var30, 48, 0);
         }

         var30.endReplaceGroup();
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

   private static final Unit invoke$lambda$2$lambda$1$lambda$0() {
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

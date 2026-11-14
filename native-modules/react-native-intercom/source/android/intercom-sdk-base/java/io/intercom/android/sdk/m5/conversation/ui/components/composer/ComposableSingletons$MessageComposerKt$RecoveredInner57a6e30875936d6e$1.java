package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.errors.VoiceTranscriptionError$TranscriptionFailed;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Error;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Processing;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Recording;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlowKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMessageComposer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessageComposer.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MessageComposerKt$lambda-8$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,677:1\n118#2:678\n87#3:679\n83#3,10:680\n94#3:739\n81#4,6:690\n88#4,6:705\n96#4:738\n402#5,9:696\n411#5:711\n412#5,2:736\n1047#6,6:712\n1047#6,6:718\n1047#6,6:724\n1047#6,6:730\n*S KotlinDebug\n*F\n+ 1 MessageComposer.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MessageComposerKt$lambda-8$1\n*L\n646#1:678\n645#1:679\n645#1:680,10\n645#1:739\n645#1:690,6\n645#1:705,6\n645#1:738\n645#1:696,9\n645#1:711\n645#1:736,2\n648#1:712,6\n650#1:718,6\n659#1:724,6\n667#1:730,6\n*E\n"
)
final class ComposableSingletons$MessageComposerKt$RecoveredInner57a6e30875936d6e$1 implements Function2 {
   public static final ComposableSingletons$MessageComposerKt$RecoveredInner57a6e30875936d6e$1 INSTANCE = new ComposableSingletons$MessageComposerKt$RecoveredInner57a6e30875936d6e$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C644@26476L1375:MessageComposer.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -295862925,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.lambda-8.<anonymous> (MessageComposer.kt:644)"
            );
         }

         int var4 = 8;
         int var5 = 0;
         Vertical var39 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var4));
         byte var6 = 48;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var3 = Modifier.Companion;
         Horizontal var40 = Alignment.Companion.getStart();
         MeasurePolicy var8 = ColumnKt.columnMeasurePolicy(var39, var40, var1, 14 & var6 >> 3 | 112 & var6 >> 3);
         Modifier var9 = var3;
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
         ComposerKt.sourceInformationMarkerStart(
            var27, 80875558, "C647@26590L35,649@26687L11,648@26638L407,658@27107L11,657@27058L360,666@27480L11,665@27431L410:MessageComposer.kt#yb6g9"
         );
         var27.startReplaceGroup(141155625);
         ComposerKt.sourceInformation(var27, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var30 = var27;
         boolean var31 = false;
         int var32 = 0;
         Object var33 = var30.rememberedValue();
         int var34 = 0;
         Object var10000;
         if (var33 == Composer.Companion.getEmpty()) {
            int var35 = 0;
            Object var56 = StateFlowKt.MutableStateFlow(0.5F);
            var30.updateRememberedValue(var56);
            var10000 = var56;
         } else {
            var10000 = var33;
         }

         MutableStateFlow var36 = (MutableStateFlow)var10000;
         var27.endReplaceGroup();
         MutableStateFlow var37 = var36;
         var10000 = null;
         var27.startReplaceGroup(141158705);
         ComposerKt.sourceInformation(var27, "CC(remember):MessageComposer.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         kotlin.jvm.functions.Function2 var71;
         if (var33 == Composer.Companion.getEmpty()) {
            Object var38 = null;
            int var57 = 0;
            var71 = ((kotlin.jvm.functions.Function2) (recoveredArgument_554_0, recoveredArgument_554_1) -> ComposableSingletons$MessageComposerKt$RecoveredInner57a6e30875936d6e$1.invoke$lambda$7$lambda$2$lambda$1((java.lang.String)recoveredArgument_554_0, (java.util.List)recoveredArgument_554_1));
            var10000 = var38;
            Object var58 = var71;
            var30.updateRememberedValue(var58);
            var71 = (Function2)var58;
         } else {
            var71 = (Function2)var33;
         }

         Function2 var63 = var71;
         var27.endReplaceGroup();
         MessageComposerKt.MessageComposer(
            (Modifier)var10000,
            var63,
            new ComposerState$TextInput("", new StringRes(string.intercom_send_us_a_message, null, 2, null), false, null, null, null, 60, null),
            null,
            null,
            null,
            null,
            null,
            null,
            VoiceTranscriptionState$Recording.INSTANCE,
            var37,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var27,
            805306416,
            0,
            260601
         );
         var10000 = null;
         var27.startReplaceGroup(141172145);
         ComposerKt.sourceInformation(var27, "CC(remember):MessageComposer.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            Object var66 = null;
            int var59 = 0;
            var71 = ((kotlin.jvm.functions.Function2) (recoveredArgument_555_0, recoveredArgument_555_1) -> ComposableSingletons$MessageComposerKt$RecoveredInner57a6e30875936d6e$1.invoke$lambda$7$lambda$4$lambda$3((java.lang.String)recoveredArgument_555_0, (java.util.List)recoveredArgument_555_1));
            var10000 = var66;
            Object var60 = var71;
            var30.updateRememberedValue(var60);
            var71 = (Function2)var60;
         } else {
            var71 = (Function2)var33;
         }

         Function2 var64 = var71;
         var27.endReplaceGroup();
         MessageComposerKt.MessageComposer(
            (Modifier)var10000,
            var64,
            new ComposerState$TextInput("", new StringRes(string.intercom_send_us_a_message, null, 2, null), false, null, null, null, 60, null),
            null,
            null,
            null,
            null,
            null,
            null,
            VoiceTranscriptionState$Processing.INSTANCE,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var27,
            805306416,
            0,
            261625
         );
         var10000 = null;
         var27.startReplaceGroup(141184081);
         ComposerKt.sourceInformation(var27, "CC(remember):MessageComposer.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            Object var67 = null;
            int var61 = 0;
            var71 = ((kotlin.jvm.functions.Function2) (recoveredArgument_556_0, recoveredArgument_556_1) -> ComposableSingletons$MessageComposerKt$RecoveredInner57a6e30875936d6e$1.invoke$lambda$7$lambda$6$lambda$5((java.lang.String)recoveredArgument_556_0, (java.util.List)recoveredArgument_556_1));
            var10000 = var67;
            Object var62 = var71;
            var30.updateRememberedValue(var62);
            var71 = (Function2)var62;
         } else {
            var71 = (Function2)var33;
         }

         Function2 var65 = var71;
         var27.endReplaceGroup();
         MessageComposerKt.MessageComposer(
            (Modifier)var10000,
            var65,
            new ComposerState$TextInput("", new StringRes(string.intercom_send_us_a_message, null, 2, null), false, null, null, null, 60, null),
            null,
            null,
            null,
            null,
            null,
            null,
            new VoiceTranscriptionState$Error(new VoiceTranscriptionError$TranscriptionFailed(null, 1, null)),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var27,
            48,
            0,
            261625
         );
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

   private static final Unit invoke$lambda$7$lambda$2$lambda$1(String var0, List var1) {
      Intrinsics.checkNotNullParameter(var0, "<unused var>");
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$4$lambda$3(String var0, List var1) {
      Intrinsics.checkNotNullParameter(var0, "<unused var>");
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6$lambda$5(String var0, List var1) {
      Intrinsics.checkNotNullParameter(var0, "<unused var>");
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}

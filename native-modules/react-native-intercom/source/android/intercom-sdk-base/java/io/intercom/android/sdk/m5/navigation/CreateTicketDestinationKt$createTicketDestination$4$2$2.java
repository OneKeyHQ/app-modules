package io.intercom.android.sdk.m5.navigation;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import io.intercom.android.sdk.survey.ui.questiontype.files.FileActionSheetKt;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCreateTicketDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketDestination.kt\nio/intercom/android/sdk/m5/navigation/CreateTicketDestinationKt$createTicketDestination$4$2$2\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,174:1\n70#2:175\n67#2,9:176\n77#2:234\n81#3,6:185\n88#3,6:200\n96#3:233\n402#4,9:191\n411#4:206\n412#4,2:231\n1047#5,6:207\n1047#5,6:213\n1047#5,6:219\n1047#5,6:225\n*S KotlinDebug\n*F\n+ 1 CreateTicketDestination.kt\nio/intercom/android/sdk/m5/navigation/CreateTicketDestinationKt$createTicketDestination$4$2$2\n*L\n130#1:175\n130#1:176,9\n130#1:234\n130#1:185,6\n130#1:200,6\n130#1:233\n130#1:191,9\n130#1:206\n130#1:231,2\n135#1:207,6\n141#1:213,6\n147#1:219,6\n153#1:225,6\n*E\n"
)
final class CreateTicketDestinationKt$createTicketDestination$4$2$2 implements Function3 {
   // $VF: synthetic field
   final AnswerClickData $data;
   // $VF: synthetic field
   final CreateTicketViewModel $viewModel;
   // $VF: synthetic field
   final CoroutineScope $scope;
   // $VF: synthetic field
   final MutableState $answerClickedData;

   CreateTicketDestinationKt$createTicketDestination$4$2$2(AnswerClickData var1, CreateTicketViewModel var2, CoroutineScope var3, MutableState var4) {
      this.$data = var1;
      this.$viewModel = var2;
      this.$scope = var3;
      this.$answerClickedData = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$ModalBottomSheet");
      ComposerKt.sourceInformation(var2, "C129@5158L1034:CreateTicketDestination.kt#m1zs2i");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -693932488,
               var3,
               -1,
               "io.intercom.android.sdk.m5.navigation.createTicketDestination.<anonymous>.<anonymous>.<anonymous> (CreateTicketDestination.kt:129)"
            );
         }

         Modifier var4 = WindowInsetsPadding_androidKt.navigationBarsPadding(Modifier.Companion);
         AnswerClickData var7 = this.$data;
         CreateTicketViewModel var8 = this.$viewModel;
         CoroutineScope var9 = this.$scope;
         MutableState var10 = this.$answerClickedData;
         int var11 = 0;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var5 = Alignment.Companion.getTopStart();
         boolean var6 = false;
         MeasurePolicy var13 = BoxKt.maybeCachedBoxMeasurePolicy(var5, var6);
         Modifier var14 = var4;
         MeasurePolicy var15 = var13;
         Composer var16 = var2;
         int var17 = 112 & var11 << 3;
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
         ComposerKt.sourceInformationMarkerStart(var29, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var31 = 6 | 112 & var11 >> 6;
         Composer var32 = var29;
         BoxScope var33 = BoxScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32, -1526745857, "C134@5373L212,140@5627L213,146@5884L213,152@6133L18,132@5268L906:CreateTicketDestination.kt#m1zs2i"
         );
         Answer$MediaAnswer$MediaItem var10000 = var7.getClickedItem();
         var32.startReplaceGroup(-1711815186);
         ComposerKt.sourceInformation(var32, "CC(remember):CreateTicketDestination.kt#9igjgp");
         Composer var35 = var32;
         boolean var36 = var32.changedInstance(var8) | var32.changedInstance(var7) | var32.changedInstance(var9);
         int var37 = 0;
         Object var38 = var35.rememberedValue();
         int var39 = 0;
         Object var80;
         if (!var36 && var38 != Composer.Companion.getEmpty()) {
            var80 = (Function0)var38;
         } else {
            Answer$MediaAnswer$MediaItem var40 = var10000;
            int var41 = 0;
            final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredCapture_1020_0 = var8;
            final io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData recoveredCapture_1020_1 = var7;
            final kotlinx.coroutines.CoroutineScope recoveredCapture_1020_2 = var9;
            final androidx.compose.runtime.MutableState recoveredCapture_1020_3 = var10;
            var80 = ((kotlin.jvm.functions.Function0) () -> CreateTicketDestinationKt$createTicketDestination$4$2$2.invoke$lambda$8$lambda$1$lambda$0(recoveredCapture_1020_0, recoveredCapture_1020_1, recoveredCapture_1020_2, recoveredCapture_1020_3));
            var10000 = var40;
            Object var42 = var80;
            var35.updateRememberedValue(var42);
            var80 = (Function0)var42;
         }

         Function0 var43 = (kotlin.jvm.functions.Function0) var80;
         var32.endReplaceGroup();
         var80 = var43;
         var32.startReplaceGroup(-1711807057);
         ComposerKt.sourceInformation(var32, "CC(remember):CreateTicketDestination.kt#9igjgp");
         var35 = var32;
         var36 = var32.changedInstance(var8) | var32.changedInstance(var7) | var32.changedInstance(var9);
         var37 = 0;
         var38 = var35.rememberedValue();
         var39 = 0;
         Object var10002;
         if (!var36 && var38 != Composer.Companion.getEmpty()) {
            var10002 = (Function0)var38;
         } else {
            Function0 var44 = var43;
            Answer$MediaAnswer$MediaItem var63 = var10000;
            int var66 = 0;
            final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredCapture_1021_0 = var8;
            final io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData recoveredCapture_1021_1 = var7;
            final kotlinx.coroutines.CoroutineScope recoveredCapture_1021_2 = var9;
            final androidx.compose.runtime.MutableState recoveredCapture_1021_3 = var10;
            Function0 var45 = ((kotlin.jvm.functions.Function0) () -> CreateTicketDestinationKt$createTicketDestination$4$2$2.invoke$lambda$8$lambda$3$lambda$2(recoveredCapture_1021_0, recoveredCapture_1021_1, recoveredCapture_1021_2, recoveredCapture_1021_3));
            var10000 = var63;
            var80 = var44;
            Object var69 = var45;
            var35.updateRememberedValue(var69);
            var10002 = (Function0)var69;
         }

         var43 = (kotlin.jvm.functions.Function0) var10002;
         var32.endReplaceGroup();
         var10002 = var43;
         var32.startReplaceGroup(-1711798833);
         ComposerKt.sourceInformation(var32, "CC(remember):CreateTicketDestination.kt#9igjgp");
         var35 = var32;
         var36 = var32.changedInstance(var8) | var32.changedInstance(var7) | var32.changedInstance(var9);
         var37 = 0;
         var38 = var35.rememberedValue();
         var39 = 0;
         Object var10003;
         if (!var36 && var38 != Composer.Companion.getEmpty()) {
            var10003 = (Function0)var38;
         } else {
            Function0 var77 = var43;
            Function0 var75 = (kotlin.jvm.functions.Function0) var80;
            Answer$MediaAnswer$MediaItem var64 = var10000;
            int var67 = 0;
            final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredCapture_1022_0 = var8;
            final io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData recoveredCapture_1022_1 = var7;
            final kotlinx.coroutines.CoroutineScope recoveredCapture_1022_2 = var9;
            final androidx.compose.runtime.MutableState recoveredCapture_1022_3 = var10;
            Function0 var46 = ((kotlin.jvm.functions.Function0) () -> CreateTicketDestinationKt$createTicketDestination$4$2$2.invoke$lambda$8$lambda$5$lambda$4(recoveredCapture_1022_0, recoveredCapture_1022_1, recoveredCapture_1022_2, recoveredCapture_1022_3));
            var10000 = var64;
            var80 = var75;
            var10002 = var77;
            Object var70 = var46;
            var35.updateRememberedValue(var70);
            var10003 = (Function0)var70;
         }

         var43 = (kotlin.jvm.functions.Function0) var10003;
         var32.endReplaceGroup();
         var10003 = var43;
         var32.startReplaceGroup(-1711791060);
         ComposerKt.sourceInformation(var32, "CC(remember):CreateTicketDestination.kt#9igjgp");
         var35 = var32;
         var36 = var32.changedInstance(var9);
         var37 = 0;
         var38 = var35.rememberedValue();
         var39 = 0;
         Object var10004;
         if (!var36 && var38 != Composer.Companion.getEmpty()) {
            var10004 = var38;
         } else {
            Function0 var79 = var43;
            Function0 var78 = (kotlin.jvm.functions.Function0) var10002;
            Function0 var76 = (kotlin.jvm.functions.Function0) var80;
            Answer$MediaAnswer$MediaItem var65 = var10000;
            int var68 = 0;
            final kotlinx.coroutines.CoroutineScope recoveredCapture_1023_0 = var9;
            final androidx.compose.runtime.MutableState recoveredCapture_1023_1 = var10;
            Function0 var47 = ((kotlin.jvm.functions.Function0) () -> CreateTicketDestinationKt$createTicketDestination$4$2$2.invoke$lambda$8$lambda$7$lambda$6(recoveredCapture_1023_0, recoveredCapture_1023_1));
            var10000 = var65;
            var80 = var76;
            var10002 = var78;
            var10003 = var79;
            Object var71 = var47;
            var35.updateRememberedValue(var71);
            var10004 = var71;
         }

         var43 = (Function0)var10004;
         var32.endReplaceGroup();
         FileActionSheetKt.FileActionSheet(var10000, (kotlin.jvm.functions.Function0) var80, (kotlin.jvm.functions.Function0) var10002, (kotlin.jvm.functions.Function0) var10003, var43, var32, 0);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$8$lambda$1$lambda$0(CreateTicketViewModel var0, AnswerClickData var1, CoroutineScope var2, MutableState var3) {
      var0.onRetryFileClicked(var1);
      CreateTicketDestinationKt$createTicketDestination$4.access$invoke$dismissSheet(var2, var3);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$8$lambda$3$lambda$2(CreateTicketViewModel var0, AnswerClickData var1, CoroutineScope var2, MutableState var3) {
      var0.onDeleteFileClicked(var1);
      CreateTicketDestinationKt$createTicketDestination$4.access$invoke$dismissSheet(var2, var3);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$8$lambda$5$lambda$4(CreateTicketViewModel var0, AnswerClickData var1, CoroutineScope var2, MutableState var3) {
      var0.onDeleteFileClicked(var1);
      CreateTicketDestinationKt$createTicketDestination$4.access$invoke$dismissSheet(var2, var3);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$8$lambda$7$lambda$6(CoroutineScope var0, MutableState var1) {
      CreateTicketDestinationKt$createTicketDestination$4.access$invoke$dismissSheet(var0, var1);
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

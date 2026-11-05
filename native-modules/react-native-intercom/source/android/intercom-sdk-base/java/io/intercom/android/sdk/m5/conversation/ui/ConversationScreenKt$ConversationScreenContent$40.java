package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$Empty;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$GifSearch;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$InlineSources;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$MediaInput;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$TeammatePresence;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content;
import io.intercom.android.sdk.m5.conversation.ui.components.InlineSourcesSheetContentKt;
import io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt;
import io.intercom.android.sdk.m5.conversation.ui.components.TeammateSheetContentKt;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$40\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,1034:1\n87#2:1035\n84#2,9:1036\n94#2:1089\n81#3,6:1045\n88#3,6:1060\n96#3:1088\n402#4,9:1051\n411#4:1066\n412#4,2:1086\n1047#5,6:1067\n1047#5,6:1073\n1047#5,6:1080\n118#6:1079\n*S KotlinDebug\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$40\n*L\n584#1:1035\n584#1:1036,9\n584#1:1089\n584#1:1045,6\n584#1:1060,6\n584#1:1088\n584#1:1051,9\n584#1:1066\n584#1:1086,2\n592#1:1067,6\n596#1:1073,6\n618#1:1080,6\n608#1:1079\n*E\n"
)
final class ConversationScreenKt$ConversationScreenContent$40 implements Function3 {
   // $VF: synthetic field
   final ConversationUiState $uiState;
   // $VF: synthetic field
   final CoroutineScope $coroutineScope;
   // $VF: synthetic field
   final MutableState $openBottomSheet;
   // $VF: synthetic field
   final Function1 $onMediaItemsSelected;
   // $VF: synthetic field
   final Function1 $trackClickedInput;
   // $VF: synthetic field
   final Function1 $onGifClick;
   // $VF: synthetic field
   final Function1 $onGifSearchQueryChange;

   ConversationScreenKt$ConversationScreenContent$40(
      ConversationUiState var1, CoroutineScope var2, MutableState var3, Function1 var4, Function1 var5, Function1 var6, Function1 var7
   ) {
      this.$uiState = var1;
      this.$coroutineScope = var2;
      this.$openBottomSheet = var3;
      this.$onMediaItemsSelected = var4;
      this.$trackClickedInput = var5;
      this.$onGifClick = var6;
      this.$onGifSearchQueryChange = var7;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$ModalBottomSheet");
      ComposerKt.sourceInformation(var2, "C583@25505L2638:ConversationScreen.kt#fnibcj");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1670416802, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenContent.<anonymous> (ConversationScreen.kt:583)"
            );
         }

         Modifier var4 = WindowInsetsPadding_androidKt.navigationBarsPadding(Modifier.Companion);
         ConversationUiState var7 = this.$uiState;
         CoroutineScope var8 = this.$coroutineScope;
         MutableState var9 = this.$openBottomSheet;
         Function1 var10 = this.$onMediaItemsSelected;
         Function1 var11 = this.$trackClickedInput;
         Function1 var12 = this.$onGifClick;
         Function1 var13 = this.$onGifSearchQueryChange;
         int var14 = 0;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var5 = Arrangement.INSTANCE.getTop();
         Horizontal var6 = Alignment.Companion.getStart();
         MeasurePolicy var16 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var14 >> 3 | 112 & var14 >> 3);
         Modifier var17 = var4;
         MeasurePolicy var18 = var16;
         Composer var19 = var2;
         int var20 = 112 & var14 << 3;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var19, 0));
         CompositionLocalMap var23 = var19.getCurrentCompositionLocalMap();
         Modifier var24 = ComposedModifierKt.materializeModifier(var19, var17);
         Function0 var25 = ComposeUiNode.Companion.getConstructor();
         Composer var26 = var19;
         int var27 = 6 | 896 & var20 << 6;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var26.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var26.startReusableNode();
         if (var26.getInserting()) {
            var26.createNode(var25);
         } else {
            var26.useNode();
         }

         Composer var29 = Updater.recovered$676aa17b44fd6280(var26);
         int var30 = 0;
         Updater.recovered$89682b199a142880(var29, var18, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var29, var23, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var29, var22, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var29, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var29, var24, ComposeUiNode.Companion.getSetModifier());
         int var31 = 14 & var27 >> 6;
         Composer var32 = var26;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var34 = 6 | 112 & var14 >> 6;
         Composer var35 = var32;
         ColumnScope var36 = ColumnScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, -21714303, "C:ConversationScreen.kt#fnibcj");
         var35.startReplaceGroup(-554889790);
         ComposerKt.sourceInformation(var35, "");
         if (var7 instanceof ConversationUiState$Content) {
            BottomSheetState var38 = ((ConversationUiState$Content)var7).getBottomSheetState();
            if (var38 instanceof BottomSheetState$MediaInput) {
               var35.startReplaceGroup(-21614453);
               ComposerKt.sourceInformation(var35, "591@25946L150,595@26145L89,589@25794L632");
               Modifier var76 = SizeKt.wrapContentHeight$default(Modifier.Companion, null, false, 3, null);
               var35.startReplaceGroup(-554881283);
               ComposerKt.sourceInformation(var35, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var52 = var35;
               boolean var55 = var35.changedInstance(var8) | var35.changed(var9) | var35.changed(var10);
               int var57 = 0;
               Object var59 = var52.rememberedValue();
               int var61 = 0;
               Object var78;
               if (!var55 && var59 != Composer.Companion.getEmpty()) {
                  var78 = (Function1)var59;
               } else {
                  Modifier var63 = var76;
                  int var65 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_380_0 = var10;
                  final kotlinx.coroutines.CoroutineScope recoveredCapture_380_1 = var8;
                  final androidx.compose.runtime.MutableState recoveredCapture_380_2 = var9;
                  var78 = ((kotlin.jvm.functions.Function1) (recoveredArgument_380_0) -> ConversationScreenKt$ConversationScreenContent$40.invoke$lambda$6$lambda$1$lambda$0(recoveredCapture_380_0, recoveredCapture_380_1, recoveredCapture_380_2, (java.util.List)recoveredArgument_380_0));
                  var76 = var63;
                  Object var67 = var78;
                  var52.updateRememberedValue(var67);
                  var78 = (Function1)var67;
               }

               Function1 var71 = (kotlin.jvm.functions.Function1) var78;
               var35.endReplaceGroup();
               var78 = var71;
               var35.startReplaceGroup(-554874976);
               ComposerKt.sourceInformation(var35, "CC(remember):ConversationScreen.kt#9igjgp");
               var52 = var35;
               var55 = var35.changedInstance(var8) | var35.changed(var9);
               var57 = 0;
               var59 = var52.rememberedValue();
               var61 = 0;
               Object var80;
               if (!var55 && var59 != Composer.Companion.getEmpty()) {
                  var80 = var59;
               } else {
                  Function1 var73 = var71;
                  Modifier var64 = var76;
                  int var66 = 0;
                  final kotlinx.coroutines.CoroutineScope recoveredCapture_381_0 = var8;
                  final androidx.compose.runtime.MutableState recoveredCapture_381_1 = var9;
                  Function0 var74 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt$ConversationScreenContent$40.invoke$lambda$6$lambda$3$lambda$2(recoveredCapture_381_0, recoveredCapture_381_1));
                  var76 = var64;
                  var78 = var73;
                  Object var68 = var74;
                  var52.updateRememberedValue(var68);
                  var80 = var68;
               }

               Function0 var72 = (Function0)var80;
               var35.endReplaceGroup();
               MediaInputSheetContentKt.MediaInputSheetContent(
                  var76, (kotlin.jvm.functions.Function1) var78, var72, var11, ((ConversationUiState$Content)var7).getBottomBarUiState().getInputTypeState(), var35, 6, 0
               );
               var35.endReplaceGroup();
            } else if (var38 instanceof BottomSheetState$TeammatePresence) {
               var35.startReplaceGroup(-20873243);
               ComposerKt.sourceInformation(var35, "604@26548L414");
               Modifier var75 = SizeKt.wrapContentHeight$default(Modifier.Companion, null, false, 3, null);
               int var70 = 16;
               int var51 = 0;
               TeammateSheetContentKt.TeammateSheetContent(
                  PaddingKt.recovered$eb6a744cd98cdbca(var75, Dp.recovered$95b3d103663bf060(var70)),
                  ((ConversationUiState$Content)var7).getTeamPresenceState().getSpecialNotice(),
                  ((ConversationUiState$Content)var7).getTeamPresenceState().getExpandedTeamPresenceState(),
                  var35,
                  6,
                  0
               );
               var35.endReplaceGroup();
            } else if (!(var38 instanceof BottomSheetState$GifSearch)) {
               if (var38 instanceof BottomSheetState$InlineSources) {
                  var35.startReplaceGroup(-19792614);
                  ComposerKt.sourceInformation(var35, "626@27641L297");
                  Modifier var69 = SizeKt.wrapContentHeight$default(Modifier.Companion, null, false, 3, null);
                  List var50 = ((BottomSheetState$InlineSources)((ConversationUiState$Content)var7).getBottomSheetState()).getSelectedSources();
                  List var54 = ((BottomSheetState$InlineSources)((ConversationUiState$Content)var7).getBottomSheetState()).getSources();
                  InlineSourcesSheetContentKt.InlineSourcesSheetContent(var50, var54, var69, var35, 384, 0);
                  var35.endReplaceGroup();
               } else {
                  if (!Intrinsics.areEqual(var38, BottomSheetState$Empty.INSTANCE)) {
                     var35.startReplaceGroup(-554887762);
                     var35.endReplaceGroup();
                     throw new NoWhenBranchMatchedException();
                  }

                  var35.startReplaceGroup(-19399534);
                  var35.endReplaceGroup();
                  ConversationScreenKt.access$ConversationScreenContent$hideBottomSheet(var8, var9);
               }
            } else {
               var35.startReplaceGroup(-20347514);
               ComposerKt.sourceInformation(var35, "617@27270L140,614@27077L445");
               Modifier var10000 = SizeKt.fillMaxHeight$default(Modifier.Companion, 0.0F, 1, null);
               List var10001 = ((BottomSheetState$GifSearch)((ConversationUiState$Content)var7).getBottomSheetState()).getGifs();
               var35.startReplaceGroup(-554838925);
               ComposerKt.sourceInformation(var35, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var39 = var35;
               boolean var40 = var35.changedInstance(var8) | var35.changed(var9) | var35.changed(var12);
               int var41 = 0;
               Object var42 = var39.rememberedValue();
               int var43 = 0;
               Object var10002;
               if (!var40 && var42 != Composer.Companion.getEmpty()) {
                  var10002 = var42;
               } else {
                  List var48 = var10001;
                  Modifier var44 = var10000;
                  int var45 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_382_0 = var12;
                  final kotlinx.coroutines.CoroutineScope recoveredCapture_382_1 = var8;
                  final androidx.compose.runtime.MutableState recoveredCapture_382_2 = var9;
                  Function1 var49 = ((kotlin.jvm.functions.Function1) (recoveredArgument_382_0) -> ConversationScreenKt$ConversationScreenContent$40.invoke$lambda$6$lambda$5$lambda$4(recoveredCapture_382_0, recoveredCapture_382_1, recoveredCapture_382_2, (io.intercom.android.sdk.blocks.lib.models.Block)recoveredArgument_382_0));
                  var10000 = var44;
                  var10001 = var48;
                  Object var46 = var49;
                  var39.updateRememberedValue(var46);
                  var10002 = var46;
               }

               Function1 var47 = (Function1)var10002;
               var35.endReplaceGroup();
               GifGridKt.GifGrid(var10000, var10001, var47, var13, var35, 6, 0);
               var35.endReplaceGroup();
            }
         }

         var35.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$6$lambda$1$lambda$0(Function1 var0, CoroutineScope var1, MutableState var2, List var3) {
      Intrinsics.checkNotNullParameter(var3, "it");
      ConversationScreenKt.access$ConversationScreenContent$hideBottomSheet(var1, var2);
      var0.invoke(var3);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$3$lambda$2(CoroutineScope var0, MutableState var1) {
      ConversationScreenKt.access$ConversationScreenContent$hideBottomSheet(var0, var1);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$5$lambda$4(Function1 var0, CoroutineScope var1, MutableState var2, Block var3) {
      Intrinsics.checkNotNullParameter(var3, "it");
      ConversationScreenKt.access$ConversationScreenContent$hideBottomSheet(var1, var2);
      var0.invoke(var3);
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

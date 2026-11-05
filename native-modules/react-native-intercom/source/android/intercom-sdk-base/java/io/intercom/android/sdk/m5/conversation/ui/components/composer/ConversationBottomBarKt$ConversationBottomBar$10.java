package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import android.content.Context;
import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.EnterTransition;
import androidx.compose.animation.ExitTransition;
import androidx.compose.animation.core.FiniteAnimationSpec;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsKt;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.platform.SoftwareKeyboardController;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.activities.ConversationReactionListener;
import io.intercom.android.sdk.conversation.ReactionInputView;
import io.intercom.android.sdk.conversation.ReactionListener;
import io.intercom.android.sdk.m5.components.ConversationEndedCardKt;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$None;
import io.intercom.android.sdk.m5.conversation.states.ComposerState;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded$ConversationEndedCta;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$Hidden;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$Reactions;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState;
import io.intercom.android.sdk.m5.conversation.utils.KeyboardState;
import io.intercom.android.sdk.m5.conversation.utils.KeyboardStateKt;
import io.intercom.android.sdk.metrics.MetricTracker$ReactionLocation;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.StateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationBottomBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ConversationBottomBarKt$ConversationBottomBar$10\n+ 2 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 5 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 9 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 10 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 11 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,297:1\n52#2:298\n118#3:299\n157#3:300\n118#3:414\n118#3:415\n118#3:442\n75#4:301\n75#4:413\n75#4:428\n87#5:302\n84#5,9:303\n87#5:367\n84#5,9:368\n94#5:408\n94#5:438\n81#6,6:312\n88#6,6:327\n81#6,6:345\n88#6,6:360\n81#6,6:377\n88#6,6:392\n96#6:407\n96#6:411\n96#6:437\n402#7,9:318\n411#7:333\n402#7,9:351\n411#7:366\n402#7,9:383\n411#7:398\n412#7,2:405\n412#7,2:409\n412#7,2:435\n70#8:334\n66#8,10:335\n77#8:412\n1047#9,6:399\n1047#9,6:416\n1047#9,6:422\n1047#9,6:429\n85#10:439\n85#10:440\n1#11:441\n*S KotlinDebug\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ConversationBottomBarKt$ConversationBottomBar$10\n*L\n95#1:298\n95#1:299\n95#1:300\n133#1:414\n166#1:415\n191#1:442\n96#1:301\n127#1:413\n186#1:428\n98#1:302\n98#1:303,9\n107#1:367\n107#1:368,9\n107#1:408\n98#1:438\n98#1:312,6\n98#1:327,6\n106#1:345,6\n106#1:360,6\n107#1:377,6\n107#1:392,6\n107#1:407\n106#1:411\n98#1:437\n98#1:318,9\n98#1:333\n106#1:351,9\n106#1:366\n107#1:383,9\n107#1:398\n107#1:405,2\n106#1:409,2\n98#1:435,2\n106#1:334\n106#1:335,10\n106#1:412\n114#1:399,6\n167#1:416,6\n181#1:422,6\n190#1:429,6\n97#1:439\n181#1:440\n*E\n"
)
final class ConversationBottomBarKt$ConversationBottomBar$10 implements Function3 {
   // $VF: synthetic field
   final float $topSpacing;
   // $VF: synthetic field
   final BottomBarUiState $bottomBarUiState;
   // $VF: synthetic field
   final Function2 $onSendMessage;
   // $VF: synthetic field
   final Function0 $onGifInputSelected;
   // $VF: synthetic field
   final Function0 $onMediaInputSelected;
   // $VF: synthetic field
   final Function1 $onInputChange;
   // $VF: synthetic field
   final Function1 $trackMetric;
   // $VF: synthetic field
   final Function0 $onTyping;
   // $VF: synthetic field
   final SharedFlow $composerUiEffect;
   // $VF: synthetic field
   final VoiceTranscriptionState $voiceTranscriptionState;
   // $VF: synthetic field
   final StateFlow $amplitudeLevel;
   // $VF: synthetic field
   final Function0 $onStartVoiceRecording;
   // $VF: synthetic field
   final Function0 $onStopVoiceRecording;
   // $VF: synthetic field
   final Function0 $onCancelVoiceRecording;
   // $VF: synthetic field
   final Function0 $onTranscribedTextConsumed;
   // $VF: synthetic field
   final Function1 $onDeleteMediaUploadItem;
   // $VF: synthetic field
   final Function1 $onFailedMediaUploadItem;
   // $VF: synthetic field
   final Function0 $onClearMediaUploadItems;
   // $VF: synthetic field
   final Function0 $onNewConversationClicked;
   // $VF: synthetic field
   final Function1 $navigateToAnotherConversation;
   // $VF: synthetic field
   final Function0 $onPrivacyNoticeDismissed;

   ConversationBottomBarKt$ConversationBottomBar$10(
      float var1,
      BottomBarUiState var2,
      Function2 var3,
      Function0 var4,
      Function0 var5,
      Function1 var6,
      Function1 var7,
      Function0 var8,
      SharedFlow var9,
      VoiceTranscriptionState var10,
      StateFlow var11,
      Function0 var12,
      Function0 var13,
      Function0 var14,
      Function0 var15,
      Function1 var16,
      Function1 var17,
      Function0 var18,
      Function0 var19,
      Function1 var20,
      Function0 var21
   ) {
      this.$topSpacing = var1;
      this.$bottomBarUiState = var2;
      this.$onSendMessage = var3;
      this.$onGifInputSelected = var4;
      this.$onMediaInputSelected = var5;
      this.$onInputChange = var6;
      this.$trackMetric = var7;
      this.$onTyping = var8;
      this.$composerUiEffect = var9;
      this.$voiceTranscriptionState = var10;
      this.$amplitudeLevel = var11;
      this.$onStartVoiceRecording = var12;
      this.$onStopVoiceRecording = var13;
      this.$onCancelVoiceRecording = var14;
      this.$onTranscribedTextConsumed = var15;
      this.$onDeleteMediaUploadItem = var16;
      this.$onFailedMediaUploadItem = var17;
      this.$onClearMediaUploadItems = var18;
      this.$onNewConversationClicked = var19;
      this.$navigateToAnotherConversation = var20;
      this.$onPrivacyNoticeDismissed = var21;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxWithConstraintsScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BoxWithConstraints");
      ComposerKt.sourceInformation(var2, "C92@4565L10,92@4576L17,95@4834L7,96@4873L17,97@4899L6467:ConversationBottomBar.kt#yb6g9");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1308906311,
               var4,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ConversationBottomBar.<anonymous> (ConversationBottomBar.kt:92)"
            );
         }

         float var5 = WindowInsetsKt.asPaddingValues(WindowInsets_androidKt.getSystemBars(WindowInsets.Companion, var2, 6), var2, 0)
            .recovered$00be2fd8b9cc26a4();
         float var7 = var1.recovered$1760a9074021b1b8();
         float var8 = this.$topSpacing;
         int var9 = 0;
         var7 = Dp.recovered$95b3d103663bf060(var7 - var8);
         int var129/* $VF was: $i$f$minus-5rwHm24x */ = 0;
         var7 = Dp.recovered$95b3d103663bf060(var7 - var5);
         int var130 = 0;
         var9 = 0;
         var8 = Dp.recovered$95b3d103663bf060(var130);
         var9 = 0;
         float var6 = Dp.recovered$95b3d103663bf060(RangesKt.coerceAtLeast(var7, var8));
         CompositionLocal var132 = AndroidCompositionLocals_androidKt.getLocalContext();
         var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var11 = var2.consume(var132);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Context var128 = (Context)var11;
         State var133 = KeyboardStateKt.keyboardAsState(var2, 0);
         Modifier var137 = WindowInsetsPadding_androidKt.navigationBarsPadding(
            SizeKt.recovered$1c935d354e9159f0(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), 0.0F, var6, 1, null)
         );
         BottomBarUiState var12 = this.$bottomBarUiState;
         Function2 var13 = this.$onSendMessage;
         Function0 var14 = this.$onGifInputSelected;
         Function0 var15 = this.$onMediaInputSelected;
         Function1 var16 = this.$onInputChange;
         Function1 var17 = this.$trackMetric;
         Function0 var18 = this.$onTyping;
         SharedFlow var19 = this.$composerUiEffect;
         VoiceTranscriptionState var20 = this.$voiceTranscriptionState;
         StateFlow var21 = this.$amplitudeLevel;
         Function0 var22 = this.$onStartVoiceRecording;
         Function0 var23 = this.$onStopVoiceRecording;
         Function0 var24 = this.$onCancelVoiceRecording;
         Function0 var25 = this.$onTranscribedTextConsumed;
         Function1 var26 = this.$onDeleteMediaUploadItem;
         Function1 var27 = this.$onFailedMediaUploadItem;
         Function0 var28 = this.$onClearMediaUploadItems;
         Function0 var29 = this.$onNewConversationClicked;
         Function1 var30 = this.$navigateToAnotherConversation;
         Function0 var31 = this.$onPrivacyNoticeDismissed;
         int var32 = 0;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var138 = Arrangement.INSTANCE.getTop();
         Horizontal var139 = Alignment.Companion.getStart();
         MeasurePolicy var34 = ColumnKt.columnMeasurePolicy(var138, var139, var2, 14 & var32 >> 3 | 112 & var32 >> 3);
         Modifier var35 = var137;
         MeasurePolicy var36 = var34;
         Composer var37 = var2;
         int var38 = 112 & var32 << 3;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var40 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var37, 0));
         CompositionLocalMap var41 = var37.getCurrentCompositionLocalMap();
         Modifier var42 = ComposedModifierKt.materializeModifier(var37, var35);
         Function0 var43 = ComposeUiNode.Companion.getConstructor();
         Composer var44 = var37;
         int var45 = 6 | 896 & var38 << 6;
         int var46 = 0;
         ComposerKt.sourceInformationMarkerStart(var44, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var44.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var44.startReusableNode();
         if (var44.getInserting()) {
            var44.createNode(var43);
         } else {
            var44.useNode();
         }

         Composer var47 = Updater.recovered$676aa17b44fd6280(var44);
         int var48 = 0;
         Updater.recovered$89682b199a142880(var47, var36, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var47, var41, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var47, var40, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var47, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var47, var42, ComposeUiNode.Companion.getSetModifier());
         int var49 = 14 & var45 >> 6;
         Composer var50 = var44;
         int var51 = 0;
         ComposerKt.sourceInformationMarkerStart(var50, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var52 = 6 | 112 & var32 >> 6;
         Composer var53 = var50;
         ColumnScope var54 = ColumnScopeInstance.INSTANCE;
         int var55 = 0;
         ComposerKt.sourceInformationMarkerStart(var53, 518313499, "C:ConversationBottomBar.kt#yb6g9");
         ComposerState var56 = var12.getComposerState();
         if (var56 instanceof ComposerState$ConversationEnded) {
            var53.startReplaceGroup(518241981);
            ComposerKt.sourceInformation(var53, "105@5204L913");
            byte var143 = 0;
            int var148 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var53, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            Modifier var156 = Modifier.Companion;
            Alignment var163 = Alignment.Companion.getTopStart();
            boolean var169 = false;
            MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var163, var169);
            Modifier var176 = var156;
            MeasurePolicy var179 = measurePolicy$ivx;
            Composer var182 = var53;
            int var184 = 112 & var143 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var182, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var182, 0));
            CompositionLocalMap localMap$iv$ivx = var182.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var182, var176);
            Function0 var71 = ComposeUiNode.Companion.getConstructor();
            Composer var72 = var182;
            int var73 = 6 | 896 & var184 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var72, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var72.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var72.startReusableNode();
            if (var72.getInserting()) {
               var72.createNode(var71);
            } else {
               var72.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var72);
            int var76/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var179, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var77 = 14 & var73 >> 6;
            Composer $composer$ivx = var72;
            int var79 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var80 = 6 | 112 & var143 >> 6;
            Composer var81 = $composer$ivx;
            BoxScope var82 = BoxScopeInstance.INSTANCE;
            int var83 = 0;
            ComposerKt.sourceInformationMarkerStart(var81, -252740357, "C106@5234L861:ConversationBottomBar.kt#yb6g9");
            Modifier var84 = var82.align(Modifier.Companion, Alignment.Companion.getCenter());
            int $changed$ivx = 0;
            int $i$f$Columnx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var81, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
            Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
            MeasurePolicy measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(
               verticalArrangement$ivx, horizontalAlignment$ivx, var81, 14 & $changed$ivx >> 3 | 112 & $changed$ivx >> 3
            );
            Modifier var90 = var84;
            MeasurePolicy var91 = measurePolicy$ivxx;
            Composer var92 = var81;
            int var93 = 112 & $changed$ivx << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var92, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var92, 0));
            CompositionLocalMap localMap$iv$ivxx = var92.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var92, var90);
            Function0 var98 = ComposeUiNode.Companion.getConstructor();
            Composer var99 = var92;
            int var100 = 6 | 896 & var93 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var99, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var99.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var99.startReusableNode();
            if (var99.getInserting()) {
               var99.createNode(var98);
            } else {
               var99.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var99);
            int var103/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var91, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var104 = 14 & var100 >> 6;
            Composer $composer$ivxx = var99;
            int var106/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var107 = 6 | 112 & $changed$ivx >> 6;
            Composer var108 = $composer$ivxx;
            ColumnScope var109 = ColumnScopeInstance.INSTANCE;
            int var110 = 0;
            ComposerKt.sourceInformationMarkerStart(var108, 681436168, "C107@5316L17,116@5863L206:ConversationBottomBar.kt#yb6g9");
            IntercomDividerKt.IntercomDivider(null, var108, 0, 1);
            ComposerState$ConversationEnded$ConversationEndedCta var111 = ((ComposerState$ConversationEnded)var12.getComposerState()).getCta();
            var108.startReplaceGroup(-809297797);
            ComposerKt.sourceInformation(var108, "");
            Function0 var194;
            if (var111 == null) {
               var194 = null;
            } else {
               ComposerState$ConversationEnded$ConversationEndedCta var112 = var111;
               int var113 = 0;
               var108.startReplaceGroup(-809296529);
               ComposerKt.sourceInformation(var108, "113@5704L58");
               CharSequence var114 = var112.getLinkedConversationId();
               if (var114 == null || var114.length() == 0) {
                  var194 = var29;
               } else {
                  var108.startReplaceGroup(1562896419);
                  ComposerKt.sourceInformation(var108, "CC(remember):ConversationBottomBar.kt#9igjgp");
                  Composer var115 = var108;
                  boolean var116 = var108.changed(var30) | var108.changedInstance(var112);
                  int var117 = 0;
                  Object var118 = var115.rememberedValue();
                  int var119 = 0;
                  if (!var116 && var118 != Composer.Companion.getEmpty()) {
                     var194 = (Function0)var118;
                  } else {
                     int var120 = 0;
                     final kotlin.jvm.functions.Function1 recoveredCapture_570_0 = var30;
                     final io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded$ConversationEndedCta recoveredCapture_570_1 = var112;
                     Object var121 = ((kotlin.jvm.functions.Function0) () -> ConversationBottomBarKt$ConversationBottomBar$10.invoke$lambda$15$lambda$5$lambda$4$lambda$3$lambda$2$lambda$1(recoveredCapture_570_0, recoveredCapture_570_1));
                     var115.updateRememberedValue(var121);
                     var194 = (Function0)var121;
                  }

                  Function0 var189 = var194;
                  var108.endReplaceGroup();
                  var194 = var189;
               }

               Function0 var122 = var194;
               var108.endReplaceGroup();
               var194 = var122;
            }

            Function0 var123 = var194;
            var108.endReplaceGroup();
            Function0 var124 = var123;
            ComposerState$ConversationEnded var188 = (ComposerState$ConversationEnded)var12.getComposerState();
            ConversationEndedCardKt.ConversationEndedCard(null, var124, var188, var108, (StringProvider.$stable | StringProvider.$stable) << 6, 1);
            ComposerKt.sourceInformationMarkerEnd(var108);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var99.endNode();
            ComposerKt.sourceInformationMarkerEnd(var99);
            ComposerKt.sourceInformationMarkerEnd(var92);
            ComposerKt.sourceInformationMarkerEnd(var81);
            ComposerKt.sourceInformationMarkerEnd(var81);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var72.endNode();
            ComposerKt.sourceInformationMarkerEnd(var72);
            ComposerKt.sourceInformationMarkerEnd(var182);
            ComposerKt.sourceInformationMarkerEnd(var53);
            var53.endReplaceGroup();
         } else if (var56 instanceof ComposerState$Hidden) {
            var53.startReplaceGroup(519208158);
            ComposerKt.sourceInformation(var53, "126@6309L7");
            if (((ComposerState$Hidden)var12.getComposerState()).getHideKeyboard()) {
               CompositionLocal var162 = CompositionLocalsKt.getLocalSoftwareKeyboardController();
               int $changed$ivx = 0;
               int $i$f$getCurrentx = 0;
               ComposerKt.sourceInformationMarkerStart(var53, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
               Object var147 = var53.consume(var162);
               ComposerKt.sourceInformationMarkerEnd(var53);
               SoftwareKeyboardController var192 = (SoftwareKeyboardController)var147;
               if ((SoftwareKeyboardController)var147 != null) {
                  var192.hide();
               } else {
                  Object var193 = null;
               }
            }

            var53.endReplaceGroup();
         } else if (var56 instanceof ComposerState$TextInput) {
            var53.startReplaceGroup(519472650);
            ComposerKt.sourceInformation(var53, "131@6434L1252");
            Modifier var191 = Modifier.Companion;
            int $this$dp$ivx = 16;
            int var159 = 0;
            float var10001 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            $this$dp$ivx = 16;
            var159 = 0;
            float var10003 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            $this$dp$ivx = 8;
            var159 = 0;
            MessageComposerKt.MessageComposer(
               PaddingKt.recovered$03b481d31d62885a(var191, var10001, 0.0F, var10003, Dp.recovered$95b3d103663bf060($this$dp$ivx), 2, null),
               var13,
               (ComposerState$TextInput)var12.getComposerState(),
               var14,
               var15,
               var16,
               var17,
               var18,
               var19,
               var20,
               var21,
               var22,
               var23,
               var24,
               var25,
               var26,
               var27,
               var28,
               var53,
               6,
               0,
               0
            );
            var53.endReplaceGroup();
         } else {
            if (!(var56 instanceof ComposerState$Reactions)) {
               var53.startReplaceGroup(1956379925);
               var53.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var53.startReplaceGroup(520795203);
            ComposerKt.sourceInformation(var53, "166@8406L380,162@8226L583");
            ReactionListener var59 = new ConversationReactionListener(
               MetricTracker$ReactionLocation.CONVERSATION,
               ((ComposerState$Reactions)var12.getComposerState()).getLastPartId(),
               ((ComposerState$Reactions)var12.getComposerState()).getConversationId(),
               Injector.get().getApi(),
               Injector.get().getMetricTracker()
            );
            Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            int $this$dp$ivx = 24;
            int var57 = 0;
            Modifier var60 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            var53.startReplaceGroup(1956482728);
            ComposerKt.sourceInformation(var53, "CC(remember):ConversationBottomBar.kt#9igjgp");
            Composer var141 = var53;
            boolean var58 = var53.changedInstance(var12) | var53.changedInstance(var59);
            int var62 = 0;
            Object var63 = var141.rememberedValue();
            int var64 = 0;
            Object var190;
            if (!var58 && var63 != Composer.Companion.getEmpty()) {
               var190 = var63;
            } else {
               int var65 = 0;
               final io.intercom.android.sdk.m5.conversation.states.BottomBarUiState recoveredCapture_571_0 = var12;
               final io.intercom.android.sdk.conversation.ReactionListener recoveredCapture_571_1 = var59;
               Object var66 = ((kotlin.jvm.functions.Function1) (recoveredArgument_571_0) -> ConversationBottomBarKt$ConversationBottomBar$10.invoke$lambda$15$lambda$8$lambda$7(recoveredCapture_571_0, recoveredCapture_571_1, (android.content.Context)recoveredArgument_571_0));
               var141.updateRememberedValue(var66);
               var190 = var66;
            }

            Function1 var167 = (Function1)var190;
            var53.endReplaceGroup();
            AndroidView_androidKt.AndroidView(var167, var60, null, var53, 48, 4);
            var53.endReplaceGroup();
         }

         var53.startReplaceGroup(1956499217);
         ComposerKt.sourceInformation(var53, "180@8978L139,185@9161L7,189@9381L84,193@9644L1698,186@9185L2157");
         if (!Intrinsics.areEqual(var12.getBottomBadge(), BottomBarUiState$BottomBadgeState$None.INSTANCE)) {
            var53.startReplaceGroup(1956500791);
            ComposerKt.sourceInformation(var53, "CC(remember):ConversationBottomBar.kt#9igjgp");
            Composer var164 = var53;
            int $changed$ivx = 0;
            int var144 = 0;
            Object var149 = var164.rememberedValue();
            int var174 = 0;
            Object var197;
            if (var149 == Composer.Companion.getEmpty()) {
               int var177 = 0;
               final androidx.compose.runtime.State recoveredExpressionCapture_572_0 = var133;
               Object var180 = SnapshotStateKt.derivedStateOf(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.ConversationBottomBarKt$ConversationBottomBar$10.invoke$lambda$15$lambda$10$lambda$9(recoveredExpressionCapture_572_0)));
               var164.updateRememberedValue(var180);
               var197 = var180;
            } else {
               var197 = var149;
            }

            State var157 = (State)var197;
            var53.endReplaceGroup();
            State var140 = var157;
            CompositionLocal var165 = CompositionLocalsKt.getLocalDensity();
            $changed$ivx = 0;
            var144 = 0;
            ComposerKt.sourceInformationMarkerStart(var53, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            var149 = var53.consume(var165);
            ComposerKt.sourceInformationMarkerEnd(var53);
            Density var158 = (Density)var149;
            Modifier var166 = var54.align(Modifier.Companion, Alignment.Companion.getCenterHorizontally());
            boolean var172 = invoke$lambda$15$lambda$11(var140);
            var197 = null;
            var53.startReplaceGroup(1956513632);
            ComposerKt.sourceInformation(var53, "CC(remember):ConversationBottomBar.kt#9igjgp");
            Composer var175 = var53;
            boolean var178 = var53.changed(var158);
            int $i$f$cachex = 0;
            Object it$ivx = var175.rememberedValue();
            int var185/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            kotlin.jvm.functions.Function1 var200;
            if (!var178 && it$ivx != Composer.Companion.getEmpty()) {
               var200 = (Function1)it$ivx;
            } else {
               Object var125 = null;
               int var186 = 0;
               final androidx.compose.ui.unit.Density recoveredCapture_573_0 = var158;
               var200 = ((kotlin.jvm.functions.Function1) (recoveredArgument_573_0) -> ConversationBottomBarKt$ConversationBottomBar$10.invoke$lambda$15$lambda$14$lambda$13(recoveredCapture_573_0, ((java.lang.Integer)recoveredArgument_573_0).intValue()));
               var197 = var125;
               Object var187 = var200;
               var175.updateRememberedValue(var187);
               var200 = (Function1)var187;
            }

            var149 = var200;
            var53.endReplaceGroup();
            EnterTransition var146 = EnterExitTransitionKt.slideInVertically$default((FiniteAnimationSpec)var197, (Function1)var149, 1, null)
               .plus(EnterExitTransitionKt.expandVertically$default(null, Alignment.Companion.getTop(), false, null, 13, null))
               .plus(EnterExitTransitionKt.fadeIn$default(null, 0.3F, 1, null));
            ExitTransition var152 = EnterExitTransitionKt.slideOutVertically$default(null, null, 3, null)
               .plus(EnterExitTransitionKt.shrinkVertically$default(null, null, false, null, 15, null))
               .plus(EnterExitTransitionKt.fadeOut$default(null, 0.0F, 3, null));
            AnimatedVisibilityKt.AnimatedVisibility(
               var54,
               var172,
               var166,
               var146,
               var152,
               null,
               ComposableLambdaKt.rememberComposableLambda(
                  -2117134110, true, new ConversationBottomBarKt$ConversationBottomBar$10$1$4(var12, var31, var54, var17, var128), var53, 54
               ),
               var53,
               1597440 | 14 & var52,
               16
            );
         }

         var53.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var53);
         ComposerKt.sourceInformationMarkerEnd(var50);
         var44.endNode();
         ComposerKt.sourceInformationMarkerEnd(var44);
         ComposerKt.sourceInformationMarkerEnd(var37);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final KeyboardState invoke$lambda$0(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (KeyboardState)var1.getValue();
   }

   private static final Unit invoke$lambda$15$lambda$5$lambda$4$lambda$3$lambda$2$lambda$1(
      Function1 var0, ComposerState$ConversationEnded$ConversationEndedCta var1
   ) {
      var0.invoke(var1.getLinkedConversationId());
      return Unit.INSTANCE;
   }

   private static final ReactionInputView invoke$lambda$15$lambda$8$lambda$7(BottomBarUiState var0, ReactionListener var1, Context var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      ReactionInputView var3 = new ReactionInputView(var2);
      ReactionInputView var4 = var3;
      int var5 = 0;
      var4.setUpReactions(((ComposerState$Reactions)var0.getComposerState()).getReactionReply(), true, var1);
      return var3;
   }

   private static final boolean invoke$lambda$15$lambda$10$lambda$9(State var0) {
      return invoke$lambda$0(var0).isDismissed();
   }

   private static final boolean invoke$lambda$15$lambda$11(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final int invoke$lambda$15$lambda$14$lambda$13(Density var0, int var1) {
      Density var2 = var0;
      int var3 = 0;
      int var4 = 40;
      int var5 = 0;
      return -var2.recovered$308ab30a2dba1f24(Dp.recovered$95b3d103663bf060(var4));
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((BoxWithConstraintsScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

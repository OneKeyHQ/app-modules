package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Modifier.Companion;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.SoftwareKeyboardController;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.FooterNoticeKt;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState;
import io.intercom.android.sdk.m5.conversation.states.FooterNoticeState;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$PoweredByBadgeState;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Error;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Loading;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$FooterNoticeIndicator;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$JumpToBottomIndicator;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$None;
import io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt;
import io.intercom.android.sdk.ui.component.JumpToBottomKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.StateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$41$4\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1034:1\n70#2:1035\n67#2,9:1036\n77#2:1094\n81#3,6:1045\n88#3,6:1060\n96#3:1093\n402#4,9:1051\n411#4:1066\n412#4,2:1091\n1047#5,6:1067\n1047#5,6:1077\n1047#5,6:1085\n118#6:1073\n118#6:1074\n118#6:1075\n118#6:1083\n118#6:1084\n1#7:1076\n*S KotlinDebug\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$41$4\n*L\n759#1:1035\n759#1:1036,9\n759#1:1094\n759#1:1045,6\n759#1:1060,6\n759#1:1093\n759#1:1051,9\n759#1:1066\n759#1:1091,2\n788#1:1067,6\n801#1:1077,6\n848#1:1085,6\n821#1:1073\n822#1:1074\n823#1:1075\n841#1:1083\n842#1:1084\n*E\n"
)
final class ConversationScreenKt$ConversationScreenContent$41$4 implements Function3 {
   // $VF: synthetic field
   final ConversationUiState $uiState;
   // $VF: synthetic field
   final Function0 $onRetryClick;
   // $VF: synthetic field
   final StateFlow $streamingMessageState;
   // $VF: synthetic field
   final StateFlow $isFinStreamingActive;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final Function1 $onSuggestionClick;
   // $VF: synthetic field
   final Function1 $onReplyClicked;
   // $VF: synthetic field
   final Function1 $onRetryMessageClicked;
   // $VF: synthetic field
   final Function1 $onFailedMessageOptionsClicked;
   // $VF: synthetic field
   final Function1 $onRetryMediaClicked;
   // $VF: synthetic field
   final Function1 $onSubmitAttribute;
   // $VF: synthetic field
   final Function0 $navigateToTicketDetail;
   // $VF: synthetic field
   final Function1 $onCreateTicket;
   // $VF: synthetic field
   final Function1 $navigateToAnotherConversation;
   // $VF: synthetic field
   final Function2 $onInlineSourcesClick;
   // $VF: synthetic field
   final SoftwareKeyboardController $keyboardController;
   // $VF: synthetic field
   final CoroutineScope $coroutineScope;
   // $VF: synthetic field
   final State $keyboardAsState$delegate;
   // $VF: synthetic field
   final MutableState $openBottomSheet;
   // $VF: synthetic field
   final Function0 $onReportAiAnswer;
   // $VF: synthetic field
   final Density $density;
   // $VF: synthetic field
   final Function1 $onJumpToBottomButtonClicked;

   ConversationScreenKt$ConversationScreenContent$41$4(
      ConversationUiState var1,
      Function0 var2,
      StateFlow var3,
      StateFlow var4,
      LazyListState var5,
      Function1 var6,
      Function1 var7,
      Function1 var8,
      Function1 var9,
      Function1 var10,
      Function1 var11,
      Function0 var12,
      Function1 var13,
      Function1 var14,
      Function2 var15,
      SoftwareKeyboardController var16,
      CoroutineScope var17,
      State var18,
      MutableState var19,
      Function0 var20,
      Density var21,
      Function1 var22
   ) {
      this.$uiState = var1;
      this.$onRetryClick = var2;
      this.$streamingMessageState = var3;
      this.$isFinStreamingActive = var4;
      this.$lazyListState = var5;
      this.$onSuggestionClick = var6;
      this.$onReplyClicked = var7;
      this.$onRetryMessageClicked = var8;
      this.$onFailedMessageOptionsClicked = var9;
      this.$onRetryMediaClicked = var10;
      this.$onSubmitAttribute = var11;
      this.$navigateToTicketDetail = var12;
      this.$onCreateTicket = var13;
      this.$navigateToAnotherConversation = var14;
      this.$onInlineSourcesClick = var15;
      this.$keyboardController = var16;
      this.$coroutineScope = var17;
      this.$keyboardAsState$delegate = var18;
      this.$openBottomSheet = var19;
      this.$onReportAiAnswer = var20;
      this.$density = var21;
      this.$onJumpToBottomButtonClicked = var22;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "paddingValues");
      ComposerKt.sourceInformation(var2, "C:ConversationScreen.kt#fnibcj");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -617229178, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenContent.<anonymous>.<anonymous> (ConversationScreen.kt:742)"
            );
         }

         ConversationUiState var5 = this.$uiState;
         if (var5 instanceof io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Loading) {
            var2.startReplaceGroup(-13901777);
            ComposerKt.sourceInformation(var2, "744@33581L124");
            ConversationLoadingScreenKt.ConversationLoadingScreen(PaddingKt.padding(Modifier.Companion, var1), var2, 0, 0);
            var2.endReplaceGroup();
         } else if (var5 instanceof io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Error) {
            var2.startReplaceGroup(-13674485);
            ComposerKt.sourceInformation(var2, "750@33807L224");
            Modifier var99 = PaddingKt.padding(Modifier.Companion, var1);
            ConversationErrorScreenKt.ConversationErrorScreen((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Error)this.$uiState, this.$onRetryClick, var99, var2, 0, 0);
            var2.endReplaceGroup();
         } else {
            if (!(var5 instanceof io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)) {
               var2.startReplaceGroup(-554632679);
               var2.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var2.startReplaceGroup(-13143796);
            ComposerKt.sourceInformation(var2, "758@34135L6847");
            Modifier var6 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
            ConversationUiState var9 = this.$uiState;
            StateFlow var10 = this.$streamingMessageState;
            StateFlow var11 = this.$isFinStreamingActive;
            LazyListState var12 = this.$lazyListState;
            Function1 var13 = this.$onSuggestionClick;
            Function1 var14 = this.$onReplyClicked;
            Function1 var15 = this.$onRetryMessageClicked;
            Function1 var16 = this.$onFailedMessageOptionsClicked;
            Function1 var17 = this.$onRetryMediaClicked;
            Function1 var18 = this.$onSubmitAttribute;
            Function0 var19 = this.$navigateToTicketDetail;
            Function1 var20 = this.$onCreateTicket;
            Function1 var21 = this.$navigateToAnotherConversation;
            Function2 var22 = this.$onInlineSourcesClick;
            SoftwareKeyboardController var23 = this.$keyboardController;
            CoroutineScope var24 = this.$coroutineScope;
            State var25 = this.$keyboardAsState$delegate;
            MutableState var26 = this.$openBottomSheet;
            Function0 var27 = this.$onReportAiAnswer;
            Density var28 = this.$density;
            Function1 var29 = this.$onJumpToBottomButtonClicked;
            int var30 = 6;
            int var31 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            Alignment var7 = Alignment.Companion.getTopStart();
            boolean var8 = false;
            MeasurePolicy var32 = BoxKt.maybeCachedBoxMeasurePolicy(var7, var8);
            Modifier var33 = var6;
            MeasurePolicy var34 = var32;
            Composer var35 = var2;
            int var36 = 112 & var30 << 3;
            int var37 = 0;
            ComposerKt.sourceInformationMarkerStart(var35, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int var38 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var35, 0));
            CompositionLocalMap var39 = var35.getCurrentCompositionLocalMap();
            Modifier var40 = ComposedModifierKt.materializeModifier(var35, var33);
            Function0 var41 = ComposeUiNode.Companion.getConstructor();
            Composer var42 = var35;
            int var43 = 6 | 896 & var36 << 6;
            int var44 = 0;
            ComposerKt.sourceInformationMarkerStart(var42, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var42.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var42.startReusableNode();
            if (var42.getInserting()) {
               var42.createNode(var41);
            } else {
               var42.useNode();
            }

            Composer var45 = Updater.recovered$676aa17b44fd6280(var42);
            int var46 = 0;
            Updater.recovered$89682b199a142880(var45, var34, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var45, var39, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var45, var38, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var45, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var45, var40, ComposeUiNode.Companion.getSetModifier());
            int var47 = 14 & var43 >> 6;
            Composer var48 = var42;
            int var49 = 0;
            ComposerKt.sourceInformationMarkerStart(var48, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var50 = 6 | 112 & var30 >> 6;
            Composer var51 = var48;
            BoxScope var52 = BoxScopeInstance.INSTANCE;
            int var53 = 0;
            ComposerKt.sourceInformationMarkerStart(var51, -1923166540, "C767@34651L140,787@36201L314,763@34416L2284:ConversationScreen.kt#fnibcj");
            boolean var54 = ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var9).getBottomBarUiState().getComposerState() instanceof io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
            Modifier var55 = TestTagKt.testTag(
               PaddingKt.padding(
                  var52.align(Modifier.Companion, Alignment.Companion.getCenter()),
                  ConversationScreenKt.access$getPaddingValuesForComposer(var1, ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var9).getBottomBarUiState(), var51, 14 & var4)
               ),
               "message list"
            );
            List var56 = ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var9).getContentRows();
            boolean var57 = ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var9).getBottomBarUiState().getComposerState() instanceof io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
            boolean var58 = ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var9).getBottomBarUiState().getBottomBadge() instanceof io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$PoweredByBadgeState;
            List var10000 = var56;
            StateFlow var10001 = var10;
            StateFlow var10002 = var11;
            Modifier var10003 = var55;
            LazyListState var10004 = var12;
            Function1 var10005 = var13;
            Function1 var10006 = var14;
            Function1 var10007 = var15;
            Function1 var10008 = var16;
            Function1 var10009 = var17;
            Function1 var10010 = var18;
            Function0 var10011 = var19;
            Function1 var10012 = var20;
            boolean var10013 = var57;
            Function1 var10014 = var21;
            boolean var10015 = var58;
            var51.startReplaceGroup(1462038784);
            ComposerKt.sourceInformation(var51, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var59 = var51;
            boolean var60 = var51.changed(var22) | var51.changed(var23) | var51.changedInstance(var24) | var51.changed(var25) | var51.changed(var26);
            int var61 = 0;
            Object var62 = var59.rememberedValue();
            int var63 = 0;
            Object var10016;
            if (!var60 && var62 != Composer.Companion.getEmpty()) {
               var10016 = var62;
            } else {
               boolean var64 = var58;
               Function1 var65 = var21;
               boolean var66 = var57;
               Function1 var67 = var20;
               Function0 var68 = var19;
               Function1 var69 = var18;
               Function1 var70 = var17;
               Function1 var71 = var16;
               Function1 var72 = var15;
               Function1 var73 = var14;
               Function1 var74 = var13;
               LazyListState var75 = var12;
               Modifier var76 = var55;
               StateFlow var77 = var11;
               StateFlow var78 = var10;
               List var79 = var56;
               int var80 = 0;
               final kotlin.jvm.functions.Function2 recoveredExpressionCapture_390_0 = var22;
               final androidx.compose.ui.platform.SoftwareKeyboardController recoveredExpressionCapture_390_1 = var23;
               final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_390_2 = var24;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_390_3 = var26;
               final androidx.compose.runtime.State recoveredExpressionCapture_390_4 = var25;
               Function2 var81 = ((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_390_0, recoveredExpressionArgument_390_1) -> io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$41$4.invoke$lambda$8$lambda$2$lambda$1(recoveredExpressionCapture_390_0, recoveredExpressionCapture_390_1, recoveredExpressionCapture_390_2, recoveredExpressionCapture_390_3, recoveredExpressionCapture_390_4, (java.util.List) ((java.util.List)recoveredExpressionArgument_390_0), (java.util.List) ((java.util.List)recoveredExpressionArgument_390_1)));
               var10000 = var79;
               var10001 = var78;
               var10002 = var77;
               var10003 = var76;
               var10004 = var75;
               var10005 = var74;
               var10006 = var73;
               var10007 = var72;
               var10008 = var71;
               var10009 = var70;
               var10010 = var69;
               var10011 = var68;
               var10012 = var67;
               var10013 = var66;
               var10014 = var65;
               var10015 = var64;
               Object var82 = var81;
               var59.updateRememberedValue(var82);
               var10016 = var82;
            }

            Function2 var83 = (Function2)var10016;
            var51.endReplaceGroup();
            StreamingMessageListKt.StreamingMessageList(
               var10000,
               var10001,
               var10002,
               var10003,
               var10004,
               var10005,
               var10006,
               var10007,
               var10008,
               var10009,
               var10010,
               var10011,
               var10012,
               var10013,
               var10014,
               var10015,
               var83,
               var54,
               var27,
               var51,
               0,
               0,
               0
            );
            FloatingIndicatorState var100 = ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var9).getFloatingIndicatorState();
            if (var100 instanceof io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$JumpToBottomIndicator) {
               var51.startReplaceGroup(-1920717696);
               ComposerKt.sourceInformation(var51, "830@39344L6,831@39432L6,800@36955L1515,799@36891L2612");
               Companion var103 = Modifier.Companion;
               int var139 = 24;
               int var113 = 0;
               float var108 = Dp.recovered$95b3d103663bf060(var139);
               int $this$dp$ivx = 24;
               int $i$f$getDpx = 0;
               float var140 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
               int $this$dp$ivxx = 8;
               int $i$f$getDpxx = 0;
               float var115 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
               float var120 = var1.recovered$7be19aa93c4abc66();
               Modifier var101 = var52.align(
                  PaddingKt.recovered$f39cb96e94c47cf6(var103, var108, var115, var140, var120), Alignment.Companion.getBottomCenter()
               );
               Integer var141 = ((io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$JumpToBottomIndicator)((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var9).getFloatingIndicatorState()).getJumpToBottomButtonState().getUnreadMessages();
               int var116 = var141.intValue();
               int var121 = 0;
               Integer var109 = var116 > 0 ? var141 : null;
               String var104 = var109 != null ? var109.toString() : null;
               long var84 = IntercomTheme.INSTANCE.getColors(var51, IntercomTheme.$stable).recovered$ff4dc34320f68c71();
               long var86 = ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(
                  IntercomTheme.INSTANCE.getColors(var51, IntercomTheme.$stable).recovered$ff4dc34320f68c71()
               );
               Modifier var143 = var101;
               long var145 = 0L;
               long var148 = 0L;
               String var151 = var104;
               long var153 = var84;
               long var154 = var86;
               var51.startReplaceGroup(1462064113);
               ComposerKt.sourceInformation(var51, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var126 = var51;
               boolean var128 = var51.changedInstance(var24) | var51.changedInstance(var9) | var51.changed(var12) | var51.changed(var28) | var51.changed(var29);
               int $i$f$cachex = 0;
               Object it$ivx = var126.rememberedValue();
               int var88/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
               Object var155;
               if (!var128 && it$ivx != Composer.Companion.getEmpty()) {
                  var155 = it$ivx;
               } else {
                  long var89 = var86;
                  long var91 = var84;
                  String var131 = var104;
                  long var93 = 0L;
                  long var95 = 0L;
                  Modifier var135 = var101;
                  int var97 = 0;
                  final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_391_0 = var24;
                  final io.intercom.android.sdk.m5.conversation.states.ConversationUiState recoveredExpressionCapture_391_1 = var9;
                  final androidx.compose.foundation.lazy.LazyListState recoveredExpressionCapture_391_2 = var12;
                  final androidx.compose.ui.unit.Density recoveredExpressionCapture_391_3 = var28;
                  final kotlin.jvm.functions.Function1 recoveredExpressionCapture_391_4 = var29;
                  Function0 var130 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$41$4.invoke$lambda$8$lambda$5$lambda$4(recoveredExpressionCapture_391_0, recoveredExpressionCapture_391_1, recoveredExpressionCapture_391_2, recoveredExpressionCapture_391_3, recoveredExpressionCapture_391_4));
                  var143 = var135;
                  var145 = var95;
                  var148 = var93;
                  var151 = var131;
                  var153 = var91;
                  var154 = var89;
                  Object var98 = var130;
                  var126.updateRememberedValue(var98);
                  var155 = var98;
               }

               Function0 var124 = (Function0)var155;
               var51.endReplaceGroup();
               JumpToBottomKt.recovered$611c8eb088dfc7a6(var143, var145, var148, var151, var153, var154, var124, var51, 0, 6);
               var51.endReplaceGroup();
            } else if (var100 instanceof io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$FooterNoticeIndicator) {
               var51.startReplaceGroup(-1918018433);
               ComposerKt.sourceInformation(var51, "847@40444L254,837@39769L968");
               FooterNoticeState var102 = ((io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$FooterNoticeIndicator)((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var9).getFloatingIndicatorState()).getFooterNoticeState();
               Modifier var146 = Modifier.Companion;
               int var105 = 24;
               int var110 = 0;
               float var149 = Dp.recovered$95b3d103663bf060(var105);
               int $this$dp$ivx = 24;
               int $i$f$getDpx = 0;
               Modifier var144 = var52.align(
                  PaddingKt.recovered$03b481d31d62885a(
                     var146, var149, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), var1.recovered$7be19aa93c4abc66(), 2, null
                  ),
                  Alignment.Companion.getBottomCenter()
               );
               String var147 = var102.getTitle();
               List var150 = var102.getAvatars();
               var51.startReplaceGroup(1462174500);
               ComposerKt.sourceInformation(var51, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var112 = var51;
               boolean var142 = var51.changedInstance(var24) | var51.changed(var12);
               int $i$f$cachex = 0;
               Object it$ivx = var112.rememberedValue();
               int var125/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
               Object var152;
               if (!var142 && it$ivx != Composer.Companion.getEmpty()) {
                  var152 = it$ivx;
               } else {
                  List var133 = var150;
                  String var134 = var147;
                  Modifier var136 = var144;
                  int var127 = 0;
                  final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_389_0 = var24;
                  final androidx.compose.foundation.lazy.LazyListState recoveredExpressionCapture_389_1 = var12;
                  Function0 var132 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$41$4.invoke$lambda$8$lambda$7$lambda$6(recoveredExpressionCapture_389_0, recoveredExpressionCapture_389_1));
                  var144 = var136;
                  var147 = var134;
                  var150 = var133;
                  Object var129 = var132;
                  var112.updateRememberedValue(var129);
                  var152 = var129;
               }

               Function0 var107 = (Function0)var152;
               var51.endReplaceGroup();
               FooterNoticeKt.FooterNoticePill(var144, var147, var150, var107, var51, 0, 0);
               var51.endReplaceGroup();
            } else {
               if (!(var100 instanceof io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$None)) {
                  var51.startReplaceGroup(1462059624);
                  var51.endReplaceGroup();
                  throw new NoWhenBranchMatchedException();
               }

               var51.startReplaceGroup(-1916844091);
               var51.endReplaceGroup();
            }

            ComposerKt.sourceInformationMarkerEnd(var51);
            ComposerKt.sourceInformationMarkerEnd(var48);
            var42.endNode();
            ComposerKt.sourceInformationMarkerEnd(var42);
            ComposerKt.sourceInformationMarkerEnd(var35);
            ComposerKt.sourceInformationMarkerEnd(var2);
            var2.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$8$lambda$2$lambda$1(
      Function2 var0, SoftwareKeyboardController var1, CoroutineScope var2, MutableState var3, State var4, List var5, List var6
   ) {
      Intrinsics.checkNotNullParameter(var5, "selectedSources");
      Intrinsics.checkNotNullParameter(var6, "sources");
      if (var0 != null) {
         Function2 var7 = var0;
         int var8 = 0;
         var7.invoke(var5, var6);
         ConversationScreenKt.access$ConversationScreenContent$hideKeyboardAndShowBottomSheet(var1, var2, var3, var4);
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$8$lambda$5$lambda$4(CoroutineScope var0, ConversationUiState var1, LazyListState var2, Density var3, Function1 var4) {
      BuildersKt.launch$default(var0, null, null, new ConversationScreenKt$ConversationScreenContent$41$4$1$3$1$1(var1, var2, var3, var4, null), 3, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$8$lambda$7$lambda$6(CoroutineScope var0, LazyListState var1) {
      BuildersKt.launch$default(var0, null, null, new ConversationScreenKt$ConversationScreenContent$41$4$1$4$1$1(var1, null), 3, null);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((PaddingValues)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

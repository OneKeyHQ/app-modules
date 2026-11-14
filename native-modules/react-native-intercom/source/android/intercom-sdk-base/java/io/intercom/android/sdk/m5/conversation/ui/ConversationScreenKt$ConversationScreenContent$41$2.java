package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.State;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.focus.FocusManager;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.platform.SoftwareKeyboardController;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.ConversationBottomBarKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.StateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$41$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,1034:1\n118#2:1035\n1047#3,6:1036\n1047#3,6:1042\n1047#3,6:1048\n*S KotlinDebug\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$41$2\n*L\n712#1:1035\n699#1:1036,6\n722#1:1042,6\n713#1:1048,6\n*E\n"
)
final class ConversationScreenKt$ConversationScreenContent$41$2 implements Function2 {
   // $VF: synthetic field
   final ConversationUiState $uiState;
   // $VF: synthetic field
   final FocusManager $focusManager;
   // $VF: synthetic field
   final SoftwareKeyboardController $keyboardController;
   // $VF: synthetic field
   final Function2 $onSendMessage;
   // $VF: synthetic field
   final CoroutineScope $coroutineScope;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final Function1 $onInputChange;
   // $VF: synthetic field
   final Function0 $onGifInputSelected;
   // $VF: synthetic field
   final MutableState $openBottomSheet;
   // $VF: synthetic field
   final Function0 $onNewConversationClicked;
   // $VF: synthetic field
   final Function0 $onMediaItemsLimitReached;
   // $VF: synthetic field
   final Function0 $onMediaInputSelected;
   // $VF: synthetic field
   final State $keyboardAsState$delegate;
   // $VF: synthetic field
   final Function1 $trackMetric;
   // $VF: synthetic field
   final Function1 $navigateToAnotherConversation;
   // $VF: synthetic field
   final Function0 $onPrivacyNoticeDismissed;
   // $VF: synthetic field
   final Function0 $onTyping;
   // $VF: synthetic field
   final SharedFlow $composerUiEffect;
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

   ConversationScreenKt$ConversationScreenContent$41$2(
      ConversationUiState var1,
      FocusManager var2,
      SoftwareKeyboardController var3,
      Function2 var4,
      CoroutineScope var5,
      LazyListState var6,
      Function1 var7,
      Function0 var8,
      MutableState var9,
      Function0 var10,
      Function0 var11,
      Function0 var12,
      State var13,
      Function1 var14,
      Function1 var15,
      Function0 var16,
      Function0 var17,
      SharedFlow var18,
      StateFlow var19,
      Function0 var20,
      Function0 var21,
      Function0 var22,
      Function0 var23,
      Function1 var24,
      Function1 var25,
      Function0 var26
   ) {
      this.$uiState = var1;
      this.$focusManager = var2;
      this.$keyboardController = var3;
      this.$onSendMessage = var4;
      this.$coroutineScope = var5;
      this.$lazyListState = var6;
      this.$onInputChange = var7;
      this.$onGifInputSelected = var8;
      this.$openBottomSheet = var9;
      this.$onNewConversationClicked = var10;
      this.$onMediaItemsLimitReached = var11;
      this.$onMediaInputSelected = var12;
      this.$keyboardAsState$delegate = var13;
      this.$trackMetric = var14;
      this.$navigateToAnotherConversation = var15;
      this.$onPrivacyNoticeDismissed = var16;
      this.$onTyping = var17;
      this.$composerUiEffect = var18;
      this.$amplitudeLevel = var19;
      this.$onStartVoiceRecording = var20;
      this.$onStopVoiceRecording = var21;
      this.$onCancelVoiceRecording = var22;
      this.$onTranscribedTextConsumed = var23;
      this.$onDeleteMediaUploadItem = var24;
      this.$onFailedMediaUploadItem = var25;
      this.$onClearMediaUploadItems = var26;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C698@30940L680,721@32321L122,712@31867L356,693@30662L2731:ConversationScreen.kt#fnibcj");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1816921840,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenContent.<anonymous>.<anonymous> (ConversationScreen.kt:692)"
            );
         }

         if (this.$uiState instanceof io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content) {
            Modifier var3 = BackgroundKt.recovered$42a3174180b3fb7a(
               WindowInsetsPadding_androidKt.imePadding(Modifier.Companion), Color.Companion.recovered$562f21ab17d9b4c7(), null, 2, null
            );
            BottomBarUiState var4 = ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)this.$uiState).getBottomBarUiState();
            int var6 = 56;
            int var7 = 0;
            float var5 = Dp.recovered$95b3d103663bf060(var6);
            VoiceTranscriptionState var29 = ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)this.$uiState).getVoiceTranscriptionState();
            Modifier var10000 = var3;
            BottomBarUiState var10001 = var4;
            var1.startReplaceGroup(-554720945);
            ComposerKt.sourceInformation(var1, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var8 = var1;
            boolean var9 = var1.changedInstance(this.$focusManager)
               | var1.changed(this.$keyboardController)
               | var1.changed(this.$onSendMessage)
               | var1.changedInstance(this.$coroutineScope)
               | var1.changed(this.$lazyListState);
            FocusManager var10 = this.$focusManager;
            SoftwareKeyboardController var11 = this.$keyboardController;
            Function2 var12 = this.$onSendMessage;
            CoroutineScope var13 = this.$coroutineScope;
            LazyListState var14 = this.$lazyListState;
            int var15 = 0;
            Object var16 = var8.rememberedValue();
            int var17 = 0;
            Object var10002;
            if (!var9 && var16 != Composer.Companion.getEmpty()) {
               var10002 = var16;
            } else {
               BottomBarUiState var23 = var4;
               Modifier var22 = var3;
               int var18 = 0;
               final androidx.compose.ui.focus.FocusManager recoveredExpressionCapture_386_0 = var10;
               final androidx.compose.ui.platform.SoftwareKeyboardController recoveredExpressionCapture_386_1 = var11;
               final kotlin.jvm.functions.Function2 recoveredExpressionCapture_386_2 = var12;
               final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_386_3 = var13;
               final androidx.compose.foundation.lazy.LazyListState recoveredExpressionCapture_386_4 = var14;
               Function2 var24 = ((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_386_0, recoveredExpressionArgument_386_1) -> io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$41$2.invoke$lambda$1$lambda$0(recoveredExpressionCapture_386_0, recoveredExpressionCapture_386_1, recoveredExpressionCapture_386_2, recoveredExpressionCapture_386_3, recoveredExpressionCapture_386_4, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_386_0), (java.util.List) ((java.util.List)recoveredExpressionArgument_386_1)));
               var10000 = var22;
               var10001 = var23;
               Object var19 = var24;
               var8.updateRememberedValue(var19);
               var10002 = var19;
            }

            Function2 var30 = (Function2)var10002;
            var1.endReplaceGroup();
            Function2 var63 = var30;
            Function1 var10003 = this.$onInputChange;
            var1.startReplaceGroup(-554677311);
            ComposerKt.sourceInformation(var1, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var33 = var1;
            boolean var35 = var1.changed(this.$onGifInputSelected) | var1.changedInstance(this.$coroutineScope) | var1.changed(this.$openBottomSheet);
            Function0 var37 = this.$onGifInputSelected;
            CoroutineScope var39 = this.$coroutineScope;
            MutableState var41 = this.$openBottomSheet;
            int $i$f$cachex = 0;
            Object it$ivx = var33.rememberedValue();
            int var47/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10004;
            if (!var35 && it$ivx != Composer.Companion.getEmpty()) {
               var10004 = it$ivx;
            } else {
               Function1 var25 = var10003;
               Function2 var59 = var30;
               BottomBarUiState var57 = var10001;
               Modifier var55 = var10000;
               int var49 = 0;
               final kotlin.jvm.functions.Function0 recoveredExpressionCapture_387_0 = var37;
               final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_387_1 = var39;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_387_2 = var41;
               Function0 var26 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$41$2.invoke$lambda$3$lambda$2(recoveredExpressionCapture_387_0, recoveredExpressionCapture_387_1, recoveredExpressionCapture_387_2));
               var10000 = var55;
               var10001 = var57;
               var63 = var59;
               var10003 = var25;
               Object var51 = var26;
               var33.updateRememberedValue(var51);
               var10004 = var51;
            }

            Function0 var31 = (Function0)var10004;
            var1.endReplaceGroup();
            Function0 var64 = var31;
            Function0 var10005 = this.$onNewConversationClicked;
            var1.startReplaceGroup(-554691605);
            ComposerKt.sourceInformation(var1, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var34 = var1;
            boolean var36 = var1.changedInstance(this.$uiState)
               | var1.changed(this.$onMediaItemsLimitReached)
               | var1.changed(this.$onMediaInputSelected)
               | var1.changed(this.$keyboardController)
               | var1.changedInstance(this.$coroutineScope)
               | var1.changed(this.$keyboardAsState$delegate)
               | var1.changed(this.$openBottomSheet);
            ConversationUiState var38 = this.$uiState;
            Function0 var40 = this.$onMediaItemsLimitReached;
            Function0 var42 = this.$onMediaInputSelected;
            SoftwareKeyboardController var44 = this.$keyboardController;
            CoroutineScope var46 = this.$coroutineScope;
            MutableState var48 = this.$openBottomSheet;
            State var50 = this.$keyboardAsState$delegate;
            int $i$f$cachexx = 0;
            Object it$ivxx = var34.rememberedValue();
            int var54/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var10006;
            if (!var36 && it$ivxx != Composer.Companion.getEmpty()) {
               var10006 = it$ivxx;
            } else {
               Function0 var27 = var10005;
               Function0 var62 = var31;
               Function1 var61 = var10003;
               Function2 var60 = var63;
               BottomBarUiState var58 = var10001;
               Modifier var56 = var10000;
               int var20 = 0;
               final io.intercom.android.sdk.m5.conversation.states.ConversationUiState recoveredExpressionCapture_388_0 = var38;
               final kotlin.jvm.functions.Function0 recoveredExpressionCapture_388_1 = var40;
               final kotlin.jvm.functions.Function0 recoveredExpressionCapture_388_2 = var42;
               final androidx.compose.ui.platform.SoftwareKeyboardController recoveredExpressionCapture_388_3 = var44;
               final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_388_4 = var46;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_388_5 = var48;
               final androidx.compose.runtime.State recoveredExpressionCapture_388_6 = var50;
               Function0 var28 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$41$2.invoke$lambda$5$lambda$4(recoveredExpressionCapture_388_0, recoveredExpressionCapture_388_1, recoveredExpressionCapture_388_2, recoveredExpressionCapture_388_3, recoveredExpressionCapture_388_4, recoveredExpressionCapture_388_5, recoveredExpressionCapture_388_6));
               var10000 = var56;
               var10001 = var58;
               var63 = var60;
               var10003 = var61;
               var64 = var62;
               var10005 = var27;
               Object var21 = var28;
               var34.updateRememberedValue(var21);
               var10006 = var21;
            }

            Function0 var32 = (Function0)var10006;
            var1.endReplaceGroup();
            ConversationBottomBarKt.recovered$3e50179ac1c3f351(
               var10000,
               var10001,
               var63,
               var10003,
               var64,
               var10005,
               var32,
               this.$trackMetric,
               var5,
               this.$navigateToAnotherConversation,
               this.$onPrivacyNoticeDismissed,
               this.$onTyping,
               this.$composerUiEffect,
               var29,
               this.$amplitudeLevel,
               this.$onStartVoiceRecording,
               this.$onStopVoiceRecording,
               this.$onCancelVoiceRecording,
               this.$onTranscribedTextConsumed,
               this.$onDeleteMediaUploadItem,
               this.$onFailedMediaUploadItem,
               this.$onClearMediaUploadItems,
               var1,
               100663296,
               0,
               0,
               0
            );
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(
      FocusManager var0, SoftwareKeyboardController var1, Function2 var2, CoroutineScope var3, LazyListState var4, String var5, List var6
   ) {
      Intrinsics.checkNotNullParameter(var5, "message");
      Intrinsics.checkNotNullParameter(var6, "mediaUploadItems");
      FocusManager.clearFocus$default(var0, false, 1, null);
      if (var1 != null) {
         var1.hide();
      }

      var2.invoke(var5, var6);
      BuildersKt.launch$default(var3, null, null, new ConversationScreenKt$ConversationScreenContent$41$2$1$1$1(var4, null), 3, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(Function0 var0, CoroutineScope var1, MutableState var2) {
      var0.invoke();
      ConversationScreenKt.access$ConversationScreenContent$showBottomSheet(var1, var2);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4(
      ConversationUiState var0, Function0 var1, Function0 var2, SoftwareKeyboardController var3, CoroutineScope var4, MutableState var5, State var6
   ) {
      if (((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var0).getBottomBarUiState().isMaxMediaItemsReached()) {
         var1.invoke();
      } else {
         var2.invoke();
         ConversationScreenKt.access$ConversationScreenContent$hideKeyboardAndShowBottomSheet(var3, var4, var5, var6);
      }

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

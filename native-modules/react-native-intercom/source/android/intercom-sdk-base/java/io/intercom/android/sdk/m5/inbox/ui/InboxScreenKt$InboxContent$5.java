package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.inbox.InboxViewModel;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Content;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Empty;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Error;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Initial;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Loading;
import io.intercom.android.sdk.models.Conversation;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nInboxScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxScreenKt$InboxContent$5\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,295:1\n1047#2,6:296\n*S KotlinDebug\n*F\n+ 1 InboxScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxScreenKt$InboxContent$5\n*L\n190#1:296,6\n*E\n"
)
final class InboxScreenKt$InboxContent$5 implements Function3 {
   // $VF: synthetic field
   final InboxUiState $uiState;
   // $VF: synthetic field
   final InboxViewModel $viewModel;
   // $VF: synthetic field
   final Function0 $onSendMessageButtonClick;
   // $VF: synthetic field
   final Function0 $onBrowseHelpCenterButtonClick;

   InboxScreenKt$InboxContent$5(InboxUiState var1, InboxViewModel var2, Function0 var3, Function0 var4) {
      this.$uiState = var1;
      this.$viewModel = var2;
      this.$onSendMessageButtonClick = var3;
      this.$onBrowseHelpCenterButtonClick = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "paddingValues");
      ComposerKt.sourceInformation(var2, "C189@7393L2037,183@7123L2307:InboxScreen.kt#po70xe");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1330134806, var4, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxContent.<anonymous> (InboxScreen.kt:183)");
         }

         Modifier var5 = WindowInsetsPaddingKt.consumeWindowInsets(SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null), var1);
         Horizontal var6 = Alignment.Companion.getCenterHorizontally();
         Modifier var10000 = var5;
         Object var10001 = null;
         PaddingValues var10002 = var1;
         boolean var10003 = false;
         Object var10004 = null;
         Horizontal var10005 = var6;
         Object var10006 = null;
         boolean var10007 = false;
         Object var10008 = null;
         var2.startReplaceGroup(1124491879);
         ComposerKt.sourceInformation(var2, "CC(remember):InboxScreen.kt#9igjgp");
         Composer var8 = var2;
         boolean var9 = var2.changedInstance(this.$uiState)
            | var2.changedInstance(this.$viewModel)
            | var2.changed(this.$onSendMessageButtonClick)
            | var2.changed(this.$onBrowseHelpCenterButtonClick);
         InboxUiState var10 = this.$uiState;
         InboxViewModel var11 = this.$viewModel;
         Function0 var12 = this.$onSendMessageButtonClick;
         Function0 var13 = this.$onBrowseHelpCenterButtonClick;
         int var14 = 0;
         Object var15 = var8.rememberedValue();
         int var16 = 0;
         Object var10009;
         if (!var9 && var15 != Composer.Companion.getEmpty()) {
            var10009 = var15;
         } else {
            Object var27 = null;
            boolean var26 = false;
            Object var25 = null;
            Horizontal var24 = var6;
            Object var23 = null;
            boolean var22 = false;
            PaddingValues var21 = var1;
            Object var20 = null;
            Modifier var19 = var5;
            int var17 = 0;
            final io.intercom.android.sdk.m5.inbox.states.InboxUiState recoveredExpressionCapture_990_0 = var10;
            final io.intercom.android.sdk.m5.inbox.InboxViewModel recoveredExpressionCapture_990_1 = var11;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_990_2 = var12;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_990_3 = var13;
            Function1 var28 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_990_0) -> io.intercom.android.sdk.m5.inbox.ui.InboxScreenKt$InboxContent$5.invoke$lambda$3$lambda$2(recoveredExpressionCapture_990_0, recoveredExpressionCapture_990_1, recoveredExpressionCapture_990_2, recoveredExpressionCapture_990_3, (androidx.compose.foundation.lazy.LazyListScope) ((androidx.compose.foundation.lazy.LazyListScope)recoveredExpressionArgument_990_0)));
            var10000 = var19;
            var10001 = var20;
            var10002 = var21;
            var10003 = var22;
            var10004 = var23;
            var10005 = var24;
            var10006 = var25;
            var10007 = var26;
            var10008 = var27;
            Object var18 = var28;
            var8.updateRememberedValue(var18);
            var10009 = var18;
         }

         Function1 var7 = (Function1)var10009;
         var2.endReplaceGroup();
         LazyDslKt.LazyColumn(
            var10000,
            (LazyListState)var10001,
            var10002,
            var10003,
            (Vertical)var10004,
            var10005,
            (FlingBehavior)var10006,
            var10007,
            (OverscrollEffect)var10008,
            var7,
            var2,
            196608 | 896 & var4 << 6,
            474
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$3$lambda$2$lambda$0(InboxViewModel var0, Conversation var1) {
      Intrinsics.checkNotNullParameter(var1, "conversation");
      var0.onConversationClick(var1);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(InboxUiState var0, InboxViewModel var1, Function0 var2, Function0 var3, LazyListScope var4) {
      Intrinsics.checkNotNullParameter(var4, "$this$LazyColumn");
      InboxUiState var5 = var0;
      if (var5 instanceof InboxUiState$Content) {
         final io.intercom.android.sdk.m5.inbox.InboxViewModel recoveredExpressionCapture_991_0 = var1;
         InboxContentScreenItemsKt.inboxContentScreenItems(
            var4,
            ((InboxUiState$Content)var0).getInboxConversations(),
            ((InboxUiState$Content)var0).getTypingConversations(),
            ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_991_0) -> io.intercom.android.sdk.m5.inbox.ui.InboxScreenKt$InboxContent$5.invoke$lambda$3$lambda$2$lambda$0(recoveredExpressionCapture_991_0, (io.intercom.android.sdk.models.Conversation) ((io.intercom.android.sdk.models.Conversation)recoveredExpressionArgument_991_0)))
         );
         ErrorState var10000 = ((InboxUiState$Content)var0).getErrorState();
         if (var10000 != null) {
            ErrorState var6 = var10000;
            int var7 = 0;
            LazyListScope.item$default(
               var4, null, null, ComposableLambdaKt.composableLambdaInstance(1670986031, true, new InboxScreenKt$InboxContent$5$1$1$2$1(var6)), 3, null
            );
         }

         if (((InboxUiState$Content)var0).isLoadingMore()) {
            LazyListScope.item$default(var4, null, null, ComposableSingletons$InboxScreenKt.INSTANCE.recovered$030ea852b673257f(), 3, null);
         }
      } else if (var5 instanceof InboxUiState$Empty) {
         LazyListScope.item$default(
            var4, null, null, ComposableLambdaKt.composableLambdaInstance(-1427381856, true, new InboxScreenKt$InboxContent$5$1$1$3(var0, var2, var3)), 3, null
         );
      } else if (var5 instanceof InboxUiState$Error) {
         LazyListScope.item$default(
            var4, null, null, ComposableLambdaKt.composableLambdaInstance(-81627649, true, new InboxScreenKt$InboxContent$5$1$1$4(var0)), 3, null
         );
      } else if (var5 instanceof InboxUiState$Initial || var5 instanceof InboxUiState$Loading) {
         LazyListScope.item$default(var4, null, null, ComposableSingletons$InboxScreenKt.INSTANCE.recovered$600dea7ea70c63b7(), 3, null);
      }

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

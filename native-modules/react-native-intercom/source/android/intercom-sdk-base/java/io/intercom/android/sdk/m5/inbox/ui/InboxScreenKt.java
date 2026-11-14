package io.intercom.android.sdk.m5.inbox.ui;

import androidx.annotation.DrawableRes;
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
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.DisposableEffectResult;
import androidx.compose.runtime.DisposableEffectScope;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
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
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.compose.LocalLifecycleOwnerKt;
import androidx.paging.LoadState.NotLoading;
import androidx.paging.compose.LazyPagingItems;
import androidx.paging.compose.LazyPagingItemsKt;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.inbox.InboxViewModel;
import io.intercom.android.sdk.m5.inbox.reducers.InboxPagingItemsReducerKt;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000N\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\u001a]\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\t2\b\b\u0001\u0010\u000b\u001a\u00020\fH\u0001¢\u0006\u0002\u0010\r\u001a8\u0010\u000e\u001a\u00020\u00012\b\b\u0001\u0010\u000b\u001a\u00020\f2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0011\u0010\u000f\u001a\r\u0012\u0004\u0012\u00020\u00010\u0005¢\u0006\u0002\b\u0010H\u0003¢\u0006\u0002\u0010\u0011\u001a]\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\t2\b\b\u0001\u0010\u000b\u001a\u00020\fH\u0003¢\u0006\u0002\u0010\r\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0014\u001a\u0015\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0016\u001a\u00020\u0017H\u0003¢\u0006\u0002\u0010\u0018¨\u0006\u0019²\u0006\u0012\u0010\u001a\u001a\n\u0012\u0004\u0012\u00020\u001c\u0018\u00010\u001bX\u008a\u0084\u0002²\u0006\u0016\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020 0\u001eX\u008a\u0084\u0002",
   d2 = {
         "InboxScreen",
         "",
         "viewModel",
         "Lio/intercom/android/sdk/m5/inbox/InboxViewModel;",
         "onSendMessageButtonClick",
         "Lkotlin/Function0;",
         "onBrowseHelpCenterButtonClick",
         "onBackButtonClick",
         "onConversationClicked",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiEffects$NavigateToConversation;",
         "navIcon",
         "",
         "(Lio/intercom/android/sdk/m5/inbox/InboxViewModel;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;ILandroidx/compose/runtime/Composer;I)V",
         "InboxOpenGateScaffold",
         "content",
         "Landroidx/compose/runtime/Composable;",
         "(ILkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;I)V",
         "InboxContent",
         "InboxLoadingRow",
         "(Landroidx/compose/runtime/Composer;I)V",
         "InboxErrorRow",
         "errorState",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "(Lio/intercom/android/sdk/m5/components/ErrorState;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release",
         "openState",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "typingConversations",
         "",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;"
   }
)
@SourceDebugExtension(
   "SMAP\nInboxScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxScreenKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 5 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 9 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 10 Effects.kt\nandroidx/compose/runtime/DisposableEffectScope\n*L\n1#1,295:1\n75#2:296\n1047#3,6:297\n1047#3,6:303\n118#4:309\n118#4:345\n70#5:310\n68#5,8:311\n77#5:344\n70#5:346\n68#5,8:347\n77#5:417\n81#6,6:319\n88#6,6:334\n96#6:343\n81#6,6:355\n88#6,6:370\n81#6,6:388\n88#6,6:403\n96#6:412\n96#6:416\n402#7,9:325\n411#7,3:340\n402#7,9:361\n411#7:376\n402#7,9:394\n411#7,3:409\n412#7,2:414\n87#8:377\n83#8,10:378\n94#8:413\n85#9:418\n85#9:419\n68#10,5:420\n*S KotlinDebug\n*F\n+ 1 InboxScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxScreenKt\n*L\n144#1:296\n145#1:297,6\n161#1:303,6\n262#1:309\n276#1:345\n260#1:310\n260#1:311,8\n260#1:344\n274#1:346\n274#1:347,8\n274#1:417\n260#1:319,6\n260#1:334,6\n260#1:343\n274#1:355,6\n274#1:370,6\n280#1:388,6\n280#1:403,6\n280#1:412\n274#1:416\n260#1:325,9\n260#1:340,3\n274#1:361,9\n274#1:376\n280#1:394,9\n280#1:409,3\n274#1:414,2\n280#1:377\n280#1:378,10\n280#1:413\n59#1:418\n137#1:419\n155#1:420,5\n*E\n"
)
public final class InboxScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InboxScreen(
      @NotNull InboxViewModel var0,
      @NotNull Function0 var1,
      @NotNull Function0 var2,
      @NotNull Function0 var3,
      @NotNull Function1 var4,
      @DrawableRes int var5,
      @Nullable Composer var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "viewModel");
      Intrinsics.checkNotNullParameter(var1, "onSendMessageButtonClick");
      Intrinsics.checkNotNullParameter(var2, "onBrowseHelpCenterButtonClick");
      Intrinsics.checkNotNullParameter(var3, "onBackButtonClick");
      Intrinsics.checkNotNullParameter(var4, "onConversationClicked");
      var6 = var6.startRestartGroup(988563388);
      ComposerKt.sourceInformation(var6, "C(InboxScreen)P(5,4,2,1,3)58@2694L16:InboxScreen.kt#po70xe");
      int var8 = var7;
      if ((var7 & 6) == 0) {
         var8 |= var6.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 48) == 0) {
         var8 |= var6.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 384) == 0) {
         var8 |= var6.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 3072) == 0) {
         var8 |= var6.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 24576) == 0) {
         var8 |= var6.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var7 & 196608) == 0) {
         var8 |= var6.changed(var5) ? 131072 : 65536;
      }

      if ((var8 & 74899) == 74898 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(988563388, var8, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxScreen (InboxScreen.kt:57)");
         }

         State var9 = SnapshotStateKt.collectAsState(var0.getOpenState(), null, var6, 0, 1);
         NetworkResponse var10 = InboxScreen$lambda$0(var9);
         if (var10 == null) {
            var6.startReplaceGroup(-559116588);
            ComposerKt.sourceInformation(var6, "62@2788L132");
            InboxOpenGateScaffold(
               var5, var3, ComposableSingletons$InboxScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var6, 384 | 14 & var8 >> 15 | 112 & var8 >> 6
            );
            var6.endReplaceGroup();
         } else if (!(var10 instanceof NetworkResponse$NetworkError)
            && !(var10 instanceof NetworkResponse$ClientError)
            && !(var10 instanceof NetworkResponse$ServerError)) {
            if (!(var10 instanceof NetworkResponse$Success)) {
               var6.startReplaceGroup(1090341883);
               var6.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var6.startReplaceGroup(-558313719);
            ComposerKt.sourceInformation(var6, "84@3590L367");
            InboxContent(var0, var1, var2, var3, var4, var5, var6, 14 & var8 | 112 & var8 | 896 & var8 | 7168 & var8 | 57344 & var8 | 458752 & var8);
            var6.endReplaceGroup();
         } else {
            var6.startReplaceGroup(-558819112);
            ComposerKt.sourceInformation(var6, "71@3158L368,71@3078L448");
            InboxOpenGateScaffold(
               var5,
               var3,
               ComposableLambdaKt.rememberComposableLambda(-157026591, true, new InboxScreenKt$InboxScreen$1(var10, var0), var6, 54),
               var6,
               384 | 14 & var8 >> 15 | 112 & var8 >> 6
            );
            var6.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var6.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.inbox.InboxViewModel recoveredCapture_989_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_989_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_989_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_989_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredCapture_989_4 = var4;
         final int recoveredCapture_989_5 = var5;
         final int recoveredCapture_989_6 = var7;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_989_0, recoveredArgument_989_1) -> InboxScreenKt.InboxScreen$lambda$1(recoveredCapture_989_0, recoveredCapture_989_1, recoveredCapture_989_2, recoveredCapture_989_3, recoveredCapture_989_4, recoveredCapture_989_5, recoveredCapture_989_6, (androidx.compose.runtime.Composer)recoveredArgument_989_0, ((java.lang.Integer)recoveredArgument_989_1).intValue())));
      }
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   private static final void InboxOpenGateScaffold(@DrawableRes int var0, Function0 var1, Function2 var2, Composer var3, int var4) {
      var3 = var3.startRestartGroup(-246524547);
      ComposerKt.sourceInformation(var3, "C(InboxOpenGateScaffold)P(1,2)104@4211L6,105@4275L13,106@4308L344,115@4660L185,102@4135L710:InboxScreen.kt#po70xe");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-246524547, var5, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxOpenGateScaffold (InboxScreen.kt:101)");
         }

         ScaffoldKt.recovered$857cf99fc8161460(
            WindowInsetsPaddingKt.windowInsetsPadding(
               BackgroundKt.recovered$42a3174180b3fb7a(
                  Modifier.Companion, IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
               ),
               WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var3, 6)
            ),
            ComposableLambdaKt.rememberComposableLambda(-152365511, true, new InboxScreenKt$InboxOpenGateScaffold$1(var1, var0), var3, 54),
            null,
            null,
            null,
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(1214047374, true, new InboxScreenKt$InboxOpenGateScaffold$2(var2), var3, 54),
            var3,
            805306416,
            508
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_988_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_988_1 = var1;
         final kotlin.jvm.functions.Function2 recoveredCapture_988_2 = var2;
         final int recoveredCapture_988_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_988_0, recoveredArgument_988_1) -> InboxScreenKt.InboxOpenGateScaffold$lambda$2(recoveredCapture_988_0, recoveredCapture_988_1, recoveredCapture_988_2, recoveredCapture_988_3, (androidx.compose.runtime.Composer)recoveredArgument_988_0, ((java.lang.Integer)recoveredArgument_988_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void InboxContent(
      InboxViewModel var0, Function0 var1, Function0 var2, Function0 var3, Function1 var4, @DrawableRes int var5, Composer var6, int var7
   ) {
      var6 = var6.startRestartGroup(-144739367);
      ComposerKt.sourceInformation(
         var6,
         "C(InboxContent)P(5,4,2,1,3)135@5205L26,136@5293L16,137@5344L148,143@5539L7,144@5584L521,144@5551L554,160@6163L280,158@6110L340,171@6531L6,172@6595L13,173@6628L444,238@9473L660,182@7092L2348,169@6455L3685:InboxScreen.kt#po70xe"
      );
      int var8 = var7;
      if ((var7 & 6) == 0) {
         var8 |= var6.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 48) == 0) {
         var8 |= var6.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 384) == 0) {
         var8 |= var6.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 3072) == 0) {
         var8 |= var6.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 24576) == 0) {
         var8 |= var6.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var7 & 196608) == 0) {
         var8 |= var6.changed(var5) ? 131072 : 65536;
      }

      if ((var8 & 74899) == 74898 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-144739367, var8, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxContent (InboxScreen.kt:134)");
         }

         LazyPagingItems var9 = LazyPagingItemsKt.collectAsLazyPagingItems(var0.getInboxPagingData(), null, var6, 0, 1);
         State var10 = SnapshotStateKt.collectAsState(var0.getTypingConversations(), null, var6, 0, 1);
         InboxUiState var11 = InboxPagingItemsReducerKt.reduceToInboxUiState(
            var9, var0.getEmptyState(), null, var5, InboxContent$lambda$3(var10), var6, LazyPagingItems.$stable | 7168 & var8 >> 6, 2
         );
         CompositionLocal var13 = LocalLifecycleOwnerKt.getLocalLifecycleOwner();
         int var14 = 0;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(var6, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var16 = var6.consume(var13);
         ComposerKt.sourceInformationMarkerEnd(var6);
         LifecycleOwner var12 = (LifecycleOwner)var16;
         Object var10000 = var12;
         var6.startReplaceGroup(-1093804836);
         ComposerKt.sourceInformation(var6, "CC(remember):InboxScreen.kt#9igjgp");
         Composer var25 = var6;
         boolean var27 = var6.changedInstance(var9) | var6.changedInstance(var12);
         int var29 = 0;
         Object var17 = var25.rememberedValue();
         int var18 = 0;
         Object var38;
         if (!var27 && var17 != Composer.Companion.getEmpty()) {
            var38 = (Function1)var17;
         } else {
            LifecycleOwner var21 = var12;
            int var19 = 0;
            final androidx.lifecycle.LifecycleOwner recoveredExpressionCapture_983_0 = var12;
            final androidx.paging.compose.LazyPagingItems recoveredExpressionCapture_983_1 = var9;
            var38 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_983_0) -> io.intercom.android.sdk.m5.inbox.ui.InboxScreenKt.InboxContent$lambda$7$lambda$6(recoveredExpressionCapture_983_0, recoveredExpressionCapture_983_1, (androidx.compose.runtime.DisposableEffectScope) ((androidx.compose.runtime.DisposableEffectScope)recoveredExpressionArgument_983_0)));
            var10000 = var21;
            Object var20 = var38;
            var25.updateRememberedValue(var20);
            var38 = (Function1)var20;
         }

         Function1 var23 = (kotlin.jvm.functions.Function1) var38;
         var6.endReplaceGroup();
         EffectsKt.DisposableEffect(var10000, var23, var6, 0);
         var10000 = null;
         var6.startReplaceGroup(-1093786549);
         ComposerKt.sourceInformation(var6, "CC(remember):InboxScreen.kt#9igjgp");
         Composer var26 = var6;
         var27 = var6.changedInstance(var0) | (var8 & 57344) == 16384 | var6.changedInstance(var9);
         var29 = 0;
         var17 = var26.rememberedValue();
         var18 = 0;
         Object var40;
         if (!var27 && var17 != Composer.Companion.getEmpty()) {
            var40 = (Function2)var17;
         } else {
            Object var35 = null;
            int var33 = 0;
            var40 = new InboxScreenKt$InboxContent$2$1(var0, var4, var9, null);
            var10000 = var35;
            Object var34 = var40;
            var26.updateRememberedValue(var34);
            var40 = (Function2)var34;
         }

         Function2 var24 = (kotlin.jvm.functions.Function2) var40;
         var6.endReplaceGroup();
         EffectsKt.LaunchedEffect(var10000, var24, var6, 6);
         ScaffoldKt.recovered$857cf99fc8161460(
            WindowInsetsPaddingKt.windowInsetsPadding(
               BackgroundKt.recovered$42a3174180b3fb7a(
                  Modifier.Companion, IntercomTheme.INSTANCE.getColors(var6, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
               ),
               WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var6, 6)
            ),
            ComposableLambdaKt.rememberComposableLambda(-398771051, true, new InboxScreenKt$InboxContent$3(var11, var3), var6, 54),
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1074822478, true, new InboxScreenKt$InboxContent$4(var11, var1), var6, 54),
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1330134806, true, new InboxScreenKt$InboxContent$5(var11, var0, var1, var2), var6, 54),
            var6,
            805330992,
            492
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var37 = var6.endRestartGroup();
      if (var37 != null) {
         final io.intercom.android.sdk.m5.inbox.InboxViewModel recoveredCapture_984_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_984_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_984_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_984_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredCapture_984_4 = var4;
         final int recoveredCapture_984_5 = var5;
         final int recoveredCapture_984_6 = var7;
         var37.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_984_0, recoveredArgument_984_1) -> InboxScreenKt.InboxContent$lambda$9(recoveredCapture_984_0, recoveredCapture_984_1, recoveredCapture_984_2, recoveredCapture_984_3, recoveredCapture_984_4, recoveredCapture_984_5, recoveredCapture_984_6, (androidx.compose.runtime.Composer)recoveredArgument_984_0, ((java.lang.Integer)recoveredArgument_984_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void InboxLoadingRow(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1843849504);
      ComposerKt.sourceInformation(var0, "C(InboxLoadingRow)259@10192L266:InboxScreen.kt#po70xe");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1843849504, var1, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxLoadingRow (InboxScreen.kt:258)");
         }

         Modifier var10000 = Modifier.Companion;
         int var2 = 16;
         int var3 = 0;
         Modifier var30 = SizeKt.fillMaxWidth$default(
            PaddingKt.recovered$f3b9f2e94c07c806(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var2), 1, null), 0.0F, 1, null
         );
         Alignment var31 = Alignment.Companion.getCenter();
         byte var5 = 54;
         int var6 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var0, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var4 = false;
         MeasurePolicy var7 = BoxKt.maybeCachedBoxMeasurePolicy(var31, var4);
         Modifier var8 = var30;
         MeasurePolicy var9 = var7;
         Composer var10 = var0;
         int var11 = 112 & var5 << 3;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var10, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var13 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var10, 0));
         CompositionLocalMap var14 = var10.getCurrentCompositionLocalMap();
         Modifier var15 = ComposedModifierKt.materializeModifier(var10, var8);
         Function0 var16 = ComposeUiNode.Companion.getConstructor();
         Composer var17 = var10;
         int var18 = 6 | 896 & var11 << 6;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var17.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var17.startReusableNode();
         if (var17.getInserting()) {
            var17.createNode(var16);
         } else {
            var17.useNode();
         }

         Composer var20 = Updater.recovered$676aa17b44fd6280(var17);
         int var21 = 0;
         Updater.recovered$89682b199a142880(var20, var9, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var20, var14, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var20, var13, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var20, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var20, var15, ComposeUiNode.Companion.getSetModifier());
         int var22 = 14 & var18 >> 6;
         Composer var23 = var17;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var25 = 6 | 112 & var5 >> 6;
         Composer var26 = var23;
         BoxScope var27 = BoxScopeInstance.INSTANCE;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -584592546, "C266@10415L6,265@10354L98:InboxScreen.kt#po70xe");
         ProgressIndicatorKt.recovered$276fbc1f701bb35f(
            null, IntercomTheme.INSTANCE.getColors(var26, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d(), 0.0F, 0L, 0, 0.0F, var26, 0, 61
         );
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var23);
         var17.endNode();
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var10);
         ComposerKt.sourceInformationMarkerEnd(var0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var32 = var0.endRestartGroup();
      if (var32 != null) {
         final int recoveredCapture_987_0 = var1;
         var32.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_987_0, recoveredArgument_987_1) -> InboxScreenKt.InboxLoadingRow$lambda$11(recoveredCapture_987_0, (androidx.compose.runtime.Composer)recoveredArgument_987_0, ((java.lang.Integer)recoveredArgument_987_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void InboxErrorRow(ErrorState var0, Composer var1, int var2) {
      var1 = var1.startRestartGroup(-126725909);
      ComposerKt.sourceInformation(var1, "C(InboxErrorRow)273@10530L663:InboxScreen.kt#po70xe");
      int var3 = var2;
      if ((var2 & 6) == 0) {
         var3 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var3 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-126725909, var3, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxErrorRow (InboxScreen.kt:272)");
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 16;
         int var5 = 0;
         Modifier var59 = SizeKt.fillMaxWidth$default(
            PaddingKt.recovered$f3b9f2e94c07c806(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var4), 1, null), 0.0F, 1, null
         );
         Alignment var60 = Alignment.Companion.getCenter();
         byte var7 = 54;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var6 = false;
         MeasurePolicy var9 = BoxKt.maybeCachedBoxMeasurePolicy(var60, var6);
         Modifier var10 = var59;
         MeasurePolicy var11 = var9;
         Composer var12 = var1;
         int var13 = 112 & var7 << 3;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(var12, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var15 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var12, 0));
         CompositionLocalMap var16 = var12.getCurrentCompositionLocalMap();
         Modifier var17 = ComposedModifierKt.materializeModifier(var12, var10);
         Function0 var18 = ComposeUiNode.Companion.getConstructor();
         Composer var19 = var12;
         int var20 = 6 | 896 & var13 << 6;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var19.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var19.startReusableNode();
         if (var19.getInserting()) {
            var19.createNode(var18);
         } else {
            var19.useNode();
         }

         Composer var22 = Updater.recovered$676aa17b44fd6280(var19);
         int var23 = 0;
         Updater.recovered$89682b199a142880(var22, var11, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var22, var16, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var22, var15, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var22, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var22, var17, ComposeUiNode.Companion.getSetModifier());
         int var24 = 14 & var20 >> 6;
         Composer var25 = var19;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var27 = 6 | 112 & var7 >> 6;
         Composer var28 = var25;
         BoxScope var29 = BoxScopeInstance.INSTANCE;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 1642780029, "C279@10692L495:InboxScreen.kt#po70xe");
         Horizontal var31 = Alignment.Companion.getCenterHorizontally();
         short var32 = 384;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var28, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var34 = Modifier.Companion;
         Vertical var35 = Arrangement.INSTANCE.getTop();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var35, var31, var28, 14 & var32 >> 3 | 112 & var32 >> 3);
         Modifier var37 = var34;
         MeasurePolicy var38 = measurePolicy$ivx;
         Composer var39 = var28;
         int var40 = 112 & var32 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var39, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var39, 0));
         CompositionLocalMap localMap$iv$ivx = var39.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var39, var37);
         Function0 var45 = ComposeUiNode.Companion.getConstructor();
         Composer var46 = var39;
         int var47 = 6 | 896 & var40 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var46.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var46.startReusableNode();
         if (var46.getInserting()) {
            var46.createNode(var45);
         } else {
            var46.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var46);
         int var50/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var38, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var51 = 14 & var47 >> 6;
         Composer $composer$ivx = var46;
         int var53 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var54 = 6 | 112 & var32 >> 6;
         Composer var55 = $composer$ivx;
         ColumnScope var56 = ColumnScopeInstance.INSTANCE;
         int var57 = 0;
         ComposerKt.sourceInformationMarkerStart(var55, -1162644216, "C282@10800L44,282@10788L57:InboxScreen.kt#po70xe");
         TextKt.recovered$5b114ab32a4f4190(
            StringResources_androidKt.stringResource(var0.getMessageResId(), var55, 0),
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
            var55,
            0,
            0,
            262142
         );
         var55.startReplaceGroup(-868786475);
         ComposerKt.sourceInformation(var55, "284@10958L205,284@10914L249");
         if (var0 instanceof ErrorState$WithCTA) {
            ButtonKt.TextButton(
               ((ErrorState$WithCTA)var0).getOnCtaClick(),
               null,
               false,
               null,
               null,
               null,
               null,
               null,
               null,
               ComposableLambdaKt.rememberComposableLambda(-282010049, true, new InboxScreenKt$InboxErrorRow$1$1$1(var0), var55, 54),
               var55,
               805306368,
               510
            );
         }

         var55.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var55);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var46.endNode();
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var39);
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var25);
         var19.endNode();
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var12);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var61 = var1.endRestartGroup();
      if (var61 != null) {
         final io.intercom.android.sdk.m5.components.ErrorState recoveredCapture_986_0 = var0;
         final int recoveredCapture_986_1 = var2;
         var61.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_986_0, recoveredArgument_986_1) -> InboxScreenKt.InboxErrorRow$lambda$14(recoveredCapture_986_0, recoveredCapture_986_1, (androidx.compose.runtime.Composer)recoveredArgument_986_0, ((java.lang.Integer)recoveredArgument_986_1).intValue())));
      }
   }

   private static final NetworkResponse InboxScreen$lambda$0(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (NetworkResponse)var1.getValue();
   }

   private static final Unit InboxScreen$lambda$1(
      InboxViewModel var0, Function0 var1, Function0 var2, Function0 var3, Function1 var4, int var5, int var6, Composer var7, int var8
   ) {
      InboxScreen(var0, var1, var2, var3, var4, var5, var7, RecomposeScopeImplKt.updateChangedFlags(var6 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InboxOpenGateScaffold$lambda$2(int var0, Function0 var1, Function2 var2, int var3, Composer var4, int var5) {
      InboxOpenGateScaffold(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   private static final Map InboxContent$lambda$3(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Map)var1.getValue();
   }

   private static final void InboxContent$lambda$7$lambda$6$lambda$4(LazyPagingItems var0, LifecycleOwner var1, Event var2) {
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      Intrinsics.checkNotNullParameter(var2, "event");
      if (var2 == Event.ON_RESUME && var0.getLoadState().getRefresh() instanceof NotLoading) {
         var0.refresh();
      }
   }

   private static final DisposableEffectResult InboxContent$lambda$7$lambda$6(LifecycleOwner var0, LazyPagingItems var1, DisposableEffectScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$DisposableEffect");
      final androidx.paging.compose.LazyPagingItems recoveredCapture_985_0 = var1;
      LifecycleEventObserver var3 = ((androidx.lifecycle.LifecycleEventObserver) (recoveredArgument_985_0, recoveredArgument_985_1) -> InboxScreenKt.InboxContent$lambda$7$lambda$6$lambda$4(recoveredCapture_985_0, (androidx.lifecycle.LifecycleOwner)recoveredArgument_985_0, (androidx.lifecycle.Lifecycle.Event)recoveredArgument_985_1));
      var0.getLifecycle().addObserver(var3);
      int var5 = 0;
      return new InboxScreenKt$InboxContent$lambda$7$lambda$6$RecoveredInnere3b0c44298fc1c14$inlined$onDispose$1(var0, var3);
   }

   private static final Unit InboxContent$lambda$9(
      InboxViewModel var0, Function0 var1, Function0 var2, Function0 var3, Function1 var4, int var5, int var6, Composer var7, int var8
   ) {
      InboxContent(var0, var1, var2, var3, var4, var5, var7, RecomposeScopeImplKt.updateChangedFlags(var6 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InboxLoadingRow$lambda$11(int var0, Composer var1, int var2) {
      InboxLoadingRow(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InboxErrorRow$lambda$14(ErrorState var0, int var1, Composer var2, int var3) {
      InboxErrorRow(var0, var2, RecomposeScopeImplKt.updateChangedFlags(var1 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$InboxLoadingRow(Composer var0, int var1) {
      InboxLoadingRow(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$InboxErrorRow(ErrorState var0, Composer var1, int var2) {
      InboxErrorRow(var0, var1, var2);
   }
}

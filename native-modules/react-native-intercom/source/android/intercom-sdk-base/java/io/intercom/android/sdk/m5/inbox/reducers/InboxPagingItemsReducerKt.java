package io.intercom.android.sdk.m5.inbox.reducers;

import androidx.annotation.DrawableRes;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.paging.LoadState;
import androidx.paging.LoadState.Error;
import androidx.paging.LoadState.Loading;
import androidx.paging.compose.LazyPagingItems;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Content;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Empty;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Initial;
import io.intercom.android.sdk.models.EmptyState;
import io.intercom.android.sdk.models.Space$Type;
import io.intercom.android.sdk.ui.common.IntercomTopBarState;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.extensions.AppConfigExtensionsKt;
import java.io.IOException;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00008\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\u001aI\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u00022\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0001\u0010\b\u001a\u00020\t2\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u000bH\u0001¢\u0006\u0002\u0010\u000e\u001a\f\u0010\u000f\u001a\u00020\u0010*\u00020\u0007H\u0002¨\u0006\u0011",
   d2 = {
         "reduceToInboxUiState",
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState;",
         "Landroidx/paging/compose/LazyPagingItems;",
         "Lio/intercom/android/sdk/models/Conversation;",
         "emptyState",
         "Lio/intercom/android/sdk/models/EmptyState;",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "navIcon",
         "",
         "typingConversations",
         "",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "(Landroidx/paging/compose/LazyPagingItems;Lio/intercom/android/sdk/models/EmptyState;Lio/intercom/android/sdk/identity/AppConfig;ILjava/util/Map;Landroidx/compose/runtime/Composer;II)Lio/intercom/android/sdk/m5/inbox/states/InboxUiState;",
         "shouldShowSendMessageButton",
         "",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nInboxPagingItemsReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxPagingItemsReducer.kt\nio/intercom/android/sdk/m5/inbox/reducers/InboxPagingItemsReducerKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,81:1\n1047#2,6:82\n1047#2,6:88\n*S KotlinDebug\n*F\n+ 1 InboxPagingItemsReducer.kt\nio/intercom/android/sdk/m5/inbox/reducers/InboxPagingItemsReducerKt\n*L\n45#1:82,6\n56#1:88,6\n*E\n"
)
public final class InboxPagingItemsReducerKt {
   @Composable
   @NotNull
   public static final InboxUiState reduceToInboxUiState(
      @NotNull LazyPagingItems var0,
      @NotNull EmptyState var1,
      @Nullable AppConfig var2,
      @DrawableRes int var3,
      @Nullable Map var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "emptyState");
      var5.startReplaceGroup(-628473750);
      ComposerKt.sourceInformation(var5, "C(reduceToInboxUiState)P(1)32@1488L6,33@1540L6:InboxPagingItemsReducer.kt#x0nqzj");
      if ((var7 & 2) != 0) {
         var2 = (AppConfig)Injector.get().getAppConfigProvider().get();
      }

      if ((var7 & 8) != 0) {
         var4 = MapsKt.emptyMap();
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-628473750, var6, -1, "io.intercom.android.sdk.m5.inbox.reducers.reduceToInboxUiState (InboxPagingItemsReducer.kt:27)");
      }

      String var10 = var2.getSpaceLabelIfExists(io.intercom.android.sdk.models.Space$Type.MESSAGES);
      var5.startReplaceGroup(-261432417);
      ComposerKt.sourceInformation(var5, "30@1365L54");
      String var11 = var10 == null ? StringResources_androidKt.stringResource(string.intercom_messages_space_title, var5, 0) : var10;
      var5.endReplaceGroup();
      String var9 = var11;
      long var63 = IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).recovered$a4be33b4cee7fcc4();
      long var12 = IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5();
      IntercomTopBarState var8 = new IntercomTopBarState(
         var3, var9, null, Color.recovered$f46ef195ad4a2363(var63), Color.recovered$f46ef195ad4a2363(var12), null, 36, null
      );
      InboxUiState var79;
      if (var0.getItemSnapshotList().size() != 0) {
         var5.startReplaceGroup(-261419678);
         ComposerKt.sourceInformation(var5, "");
         IntercomTopBarState var10000 = var8;
         LazyPagingItems var10001 = var0;
         boolean var10002 = shouldShowSendMessageButton(var2);
         boolean var10003 = var0.getLoadState().getAppend() instanceof Loading;
         LoadState var67 = var0.getLoadState().getAppend();
         Error var10004 = var67 instanceof Error ? (Error)var67 : null;
         ErrorState var90;
         if ((var67 instanceof Error ? (Error)var67 : null) == null) {
            var90 = null;
         } else {
            Error var15 = var10004;
            boolean var35 = var10003;
            boolean var34 = var10002;
            LazyPagingItems var33 = var0;
            IntercomTopBarState var32 = var8;
            int var16 = 0;
            boolean var17 = var15.getError() instanceof IOException;
            var5.startReplaceGroup(-261407761);
            ComposerKt.sourceInformation(var5, "44@2103L11");
            ErrorState var78;
            if (var17) {
               byte var77 = 0;
               byte var83 = 0;
               Object var85 = null;
               byte var87 = 0;
               var5.startReplaceGroup(-598334865);
               ComposerKt.sourceInformation(var5, "CC(remember):InboxPagingItemsReducer.kt#9igjgp");
               Composer var18 = var5;
               boolean var19 = (var6 & 14 ^ 6) > 4 && var5.changedInstance(var0) || (var6 & 6) == 4;
               int var20 = 0;
               Object var21 = var18.rememberedValue();
               int var22 = 0;
               Object var89;
               if (!var19 && var21 != Composer.Companion.getEmpty()) {
                  var89 = var21;
               } else {
                  byte var23 = 0;
                  Object var24 = null;
                  byte var25 = 0;
                  byte var26 = 0;
                  int var27 = 0;
                  final androidx.paging.compose.LazyPagingItems recoveredExpressionCapture_961_0 = var0;
                  Function0 var28 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.inbox.reducers.InboxPagingItemsReducerKt.reduceToInboxUiState$lambda$2$lambda$1$lambda$0(recoveredExpressionCapture_961_0));
                  var77 = var26;
                  var83 = var25;
                  var85 = var24;
                  var87 = var23;
                  Object var29 = var28;
                  var18.updateRememberedValue(var29);
                  var89 = var29;
               }

               Function0 var30 = (Function0)var89;
               var5.endReplaceGroup();
               Object var40 = null;
               byte var41 = 15;
               Function0 var42 = var30;
               byte var43 = var87;
               Object var44 = var85;
               byte var45 = var83;
               byte var46 = var77;
               var78 = new io.intercom.android.sdk.m5.components.ErrorState$WithCTA(var46, var45, (Integer)var44, var43, var42, var41, (DefaultConstructorMarker)var40);
            } else {
               var78 = new io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA(0, 0, null, 7, null);
            }

            ErrorState var31 = var78;
            var5.endReplaceGroup();
            ErrorState var36 = var31;
            var10000 = var32;
            var10001 = var33;
            var10002 = var34;
            var10003 = var35;
            var90 = var36;
         }

         Map var47 = var4;
         ErrorState var48 = var90;
         boolean var49 = var10003;
         boolean var50 = var10002;
         LazyPagingItems var51 = var10001;
         IntercomTopBarState var52 = var10000;
         io.intercom.android.sdk.m5.inbox.states.InboxUiState$Content var64 = new io.intercom.android.sdk.m5.inbox.states.InboxUiState$Content(var52, var51, var50, var49, var48, var47);
         var5.endReplaceGroup();
         var79 = var64;
      } else if (var0.getLoadState().getRefresh() instanceof Error) {
         var5.startReplaceGroup(486543987);
         ComposerKt.sourceInformation(var5, "55@2577L13");
         LoadState var80 = var0.getLoadState().getRefresh();
         Intrinsics.checkNotNull(var80, "null cannot be cast to non-null type androidx.paging.LoadState.Error");
         boolean var66 = ((Error)var80).getError() instanceof IOException;
         io.intercom.android.sdk.m5.inbox.states.InboxUiState$Error var82;
         if (!var66) {
            var82 = new io.intercom.android.sdk.m5.inbox.states.InboxUiState$Error(var8, new io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA(0, 0, null, 7, null));
         } else {
            IntercomTopBarState var81 = var8;
            byte var84 = 0;
            byte var86 = 0;
            Object var88 = null;
            byte var91 = 0;
            var5.startReplaceGroup(-261391566);
            ComposerKt.sourceInformation(var5, "CC(remember):InboxPagingItemsReducer.kt#9igjgp");
            Composer var14 = var5;
            boolean var68 = (var6 & 14 ^ 6) > 4 && var5.changedInstance(var0) || (var6 & 6) == 4;
            int var69 = 0;
            Object var70 = var14.rememberedValue();
            int var71 = 0;
            Object var10005;
            if (!var68 && var70 != Composer.Companion.getEmpty()) {
               var10005 = var70;
            } else {
               byte var38 = 0;
               Object var37 = null;
               byte var76 = 0;
               byte var75 = 0;
               IntercomTopBarState var74 = var8;
               int var72 = 0;
               final androidx.paging.compose.LazyPagingItems recoveredExpressionCapture_962_0 = var0;
               Function0 var39 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.inbox.reducers.InboxPagingItemsReducerKt.reduceToInboxUiState$lambda$4$lambda$3(recoveredExpressionCapture_962_0));
               var81 = var74;
               var84 = var75;
               var86 = var76;
               var88 = var37;
               var91 = var38;
               Object var73 = var39;
               var14.updateRememberedValue(var73);
               var10005 = var73;
            }

            Function0 var13 = (Function0)var10005;
            var5.endReplaceGroup();
            Object var53 = null;
            byte var54 = 15;
            Function0 var55 = var13;
            byte var56 = var91;
            Object var57 = var88;
            byte var58 = var86;
            byte var59 = var84;
            ErrorState var60 = new io.intercom.android.sdk.m5.components.ErrorState$WithCTA(var59, var58, (Integer)var57, var56, var55, var54, (DefaultConstructorMarker)var53);
            IntercomTopBarState var61 = var81;
            var82 = new io.intercom.android.sdk.m5.inbox.states.InboxUiState$Error(var61, var60);
         }

         io.intercom.android.sdk.m5.inbox.states.InboxUiState$Error var65 = var82;
         var5.endReplaceGroup();
         var79 = var65;
      } else if (!Intrinsics.areEqual(var1, EmptyState.Companion.getNULL())) {
         var5.startReplaceGroup(-261380615);
         var5.endReplaceGroup();
         var79 = new io.intercom.android.sdk.m5.inbox.states.InboxUiState$Empty(var8, var1, shouldShowSendMessageButton(var2));
      } else if (var0.getLoadState().getRefresh() instanceof Loading) {
         var5.startReplaceGroup(-261372105);
         var5.endReplaceGroup();
         var79 = new io.intercom.android.sdk.m5.inbox.states.InboxUiState$Loading(var8);
      } else {
         var5.startReplaceGroup(-261369513);
         var5.endReplaceGroup();
         var79 = new io.intercom.android.sdk.m5.inbox.states.InboxUiState$Initial(var8);
      }

      InboxUiState var62 = var79;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var5.endReplaceGroup();
      return var62;
   }

   private static final boolean shouldShowSendMessageButton(AppConfig var0) {
      return AppConfigExtensionsKt.canStartNewConversation(var0) && !var0.isHelpCenterRequireSearchEnabled();
   }

   private static final Unit reduceToInboxUiState$lambda$2$lambda$1$lambda$0(LazyPagingItems var0) {
      var0.retry();
      return Unit.INSTANCE;
   }

   private static final Unit reduceToInboxUiState$lambda$4$lambda$3(LazyPagingItems var0) {
      var0.refresh();
      return Unit.INSTANCE;
   }
}

package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState;
import io.intercom.android.sdk.m5.conversation.states.ComposerState;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.FinStreamingData;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$FooterNoticeIndicator;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$JumpToBottomIndicator;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$None;
import io.intercom.android.sdk.m5.conversation.states.FooterNoticeState;
import io.intercom.android.sdk.m5.conversation.states.JumpToBottomButtonState;
import io.intercom.android.sdk.m5.conversation.states.LaunchMode;
import io.intercom.android.sdk.m5.conversation.states.NetworkState;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.AvatarDetails;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.FooterNotice;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\nH\u0086\u0002¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/UpdateFloatingIndicatorUseCase;",
         "",
         "<init>",
         "()V",
         "invoke",
         "",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "conversationScrolledState",
         "Lio/intercom/android/sdk/m5/conversation/usecase/ConversationScrolledState;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nUpdateFloatingIndicatorUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UpdateFloatingIndicatorUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/UpdateFloatingIndicatorUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,66:1\n230#2,3:67\n233#2,2:74\n230#2,5:76\n230#2,5:81\n1563#3:70\n1634#3,3:71\n*S KotlinDebug\n*F\n+ 1 UpdateFloatingIndicatorUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/UpdateFloatingIndicatorUseCase\n*L\n23#1:67,3\n23#1:74,2\n41#1:76,5\n53#1:81,5\n29#1:70\n29#1:71,3\n*E\n"
)
public final class UpdateFloatingIndicatorUseCase {
   public static int $stable;

   static {}

   public final void invoke(@NotNull MutableStateFlow var1, @NotNull ConversationScrolledState var2) {
      Intrinsics.checkNotNullParameter(var1, "clientState");
      Intrinsics.checkNotNullParameter(var2, "conversationScrolledState");
      boolean var3 = var2.getScrolled();
      Conversation var10000 = ((ConversationClientState)var1.getValue()).getConversation();
      FooterNotice var4 = var10000 != null ? var10000.getFooterNotice() : null;
      if (var4 != null && !var2.isLandscape() && !var2.isLargeFont()) {
         if (var3 && !(((ConversationClientState)var1.getValue()).getFloatingIndicatorState() instanceof FloatingIndicatorState$FooterNoticeIndicator)) {
            MutableStateFlow var43 = var1;
            int var45 = 0;

            Object var47;
            ConversationClientState var51;
            do {
               var47 = var43.getValue();
               var51 = (ConversationClientState)var47;
               int var54 = 0;
               ConversationClientState var57 = (ConversationClientState)var51;
               Object var10001 = null;
               Object var10002 = null;
               Object var10003 = null;
               Object var10004 = null;
               Object var10005 = null;
               Object var10006 = null;
               Object var10007 = null;
               Object var10008 = null;
               Object var10009 = null;
               Object var10010 = null;
               Object var10011 = null;
               Object var10012 = null;
               Object var10013 = null;
               Object var10014 = null;
               byte var10015 = 0;
               byte var10016 = 0;
               String var10017 = var4.getTitle();
               String var10018 = var4.getSubtitle();
               if (var10018 == null) {
                  var10018 = "";
               }

               List var60;
               label63: {
                  AvatarDetails var10019 = var4.getAvatarDetails();
                  if (var10019 != null) {
                     var60 = var10019.getAvatars();
                     if (var60 != null) {
                        Iterable var10 = var60;
                        String var11 = var10018;
                        String var12 = var10017;
                        byte var13 = 0;
                        byte var14 = 0;
                        Object var15 = null;
                        Object var16 = null;
                        Object var17 = null;
                        Object var18 = null;
                        Object var19 = null;
                        Object var20 = null;
                        Object var21 = null;
                        Object var22 = null;
                        Object var23 = null;
                        Object var24 = null;
                        Object var25 = null;
                        Object var26 = null;
                        Object var27 = null;
                        Object var28 = null;
                        ConversationClientState var29 = (ConversationClientState)var51;
                        int var30 = 0;
                        Iterable var31 = var10;
                        ArrayList var32 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10));
                        int var33 = 0;

                        for (Object var35 : var31) {
                           Avatar$Builder var36 = (Avatar$Builder)var35;
                           Collection var37 = var32;
                           int var38 = 0;
                           Avatar var58 = var36.build();
                           Intrinsics.checkNotNullExpressionValue(var58, "build(...)");
                           var37.add(new AvatarWrapper(var58, false, 2, null));
                        }

                        List var56 = (List)var32;
                        var57 = var29;
                        var10001 = var28;
                        var10002 = var27;
                        var10003 = var26;
                        var10004 = var25;
                        var10005 = var24;
                        var10006 = var23;
                        var10007 = var22;
                        var10008 = var21;
                        var10009 = var20;
                        var10010 = var19;
                        var10011 = var18;
                        var10012 = var17;
                        var10013 = var16;
                        var10014 = var15;
                        var10015 = var14;
                        var10016 = var13;
                        var10017 = var12;
                        var10018 = var11;
                        var60 = var56;
                        break label63;
                     }
                  }

                  var60 = CollectionsKt.emptyList();
               }

               List var39 = var60;
               String var40 = var10018;
               String var41 = var10017;
               FooterNoticeState var42 = new FooterNoticeState(var41, var40, var39);
               var51 = ConversationClientState.copy$default(
                  var57,
                  (Map)var10001,
                  (Conversation)var10002,
                  (String)var10003,
                  (CurrentlyTypingState)var10004,
                  (ComposerState)var10005,
                  (BottomSheetState)var10006,
                  (LaunchMode)var10007,
                  (NetworkResponse)var10008,
                  (ArticleMetadata)var10009,
                  (NetworkState)var10010,
                  (List)var10011,
                  (List)var10012,
                  (FinStreamingData)var10013,
                  (OpenMessengerResponse)var10014,
                  var10015,
                  var10016,
                  new FloatingIndicatorState$FooterNoticeIndicator(var42),
                  null,
                  false,
                  false,
                  null,
                  null,
                  null,
                  8323071,
                  null
               );
            } while (!var43.compareAndSet(var47, var51));
         }
      } else if (var3 && !(((ConversationClientState)var1.getValue()).getFloatingIndicatorState() instanceof FloatingIndicatorState$JumpToBottomIndicator)) {
         MutableStateFlow var5 = var1;
         int var6 = 0;

         Object var7;
         ConversationClientState var49;
         do {
            var7 = var5.getValue();
            var49 = (ConversationClientState)var7;
            int var9 = 0;
            var49 = ConversationClientState.copy$default(
               (ConversationClientState)var49,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               0,
               0,
               new FloatingIndicatorState$JumpToBottomIndicator(new JumpToBottomButtonState(0, var2.getLastSeenItemIndex(), 1, null)),
               null,
               false,
               false,
               null,
               null,
               null,
               8323071,
               null
            );
         } while (!var5.compareAndSet(var7, var49));
      }

      if (!var3 && !(((ConversationClientState)var1.getValue()).getFloatingIndicatorState() instanceof FloatingIndicatorState$None)) {
         MutableStateFlow var44 = var1;
         int var46 = 0;

         Object var48;
         ConversationClientState var53;
         do {
            var48 = var44.getValue();
            var53 = (ConversationClientState)var48;
            int var55 = 0;
            var53 = ConversationClientState.copy$default(
               (ConversationClientState)var53,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               0,
               0,
               FloatingIndicatorState$None.INSTANCE,
               null,
               false,
               false,
               null,
               null,
               null,
               8323071,
               null
            );
         } while (!var44.compareAndSet(var48, var53));
      }
   }
}

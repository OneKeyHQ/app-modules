package io.intercom.android.sdk.m5.conversation.reducers;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState;
import io.intercom.android.sdk.m5.conversation.states.ContentRow;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState;
import io.intercom.android.sdk.m5.conversation.states.FooterNoticeState;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.states.NetworkState;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.m5.conversation.states.TeamPresenceState;
import io.intercom.android.sdk.m5.conversation.states.TopAppBarUiState;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$AskedAboutRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$FooterNoticeRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$SpecialNoticeRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$TeamIntroRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$TypingIndicatorRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow$PartWrapper;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.AvatarDetails;
import io.intercom.android.sdk.models.ComposerSuggestions;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.FooterNotice;
import io.intercom.android.sdk.models.Header;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B'\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003¢\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0000¢\u0006\u0002\b\rJ\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\f\u0010\u0010\u001a\u00020\u0011*\u00020\u0012H\u0002R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/reducers/ConversationReducer;",
         "",
         "config",
         "Lkotlin/Function0;",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "<init>",
         "(Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)V",
         "computeUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState;",
         "clientState",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "computeUiState$intercom_sdk_base_release",
         "isBotIntroEmpty",
         "",
         "toArticlePart",
         "Lio/intercom/android/sdk/models/Part;",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nConversationReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/ConversationReducer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,197:1\n1563#2:198\n1634#2,3:199\n1878#2,3:202\n1563#2:205\n1634#2,3:206\n*S KotlinDebug\n*F\n+ 1 ConversationReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/ConversationReducer\n*L\n125#1:198\n125#1:199,3\n129#1:202,3\n171#1:205\n171#1:206,3\n*E\n"
)
public final class ConversationReducer {
   @NotNull
   private final Function0 config;
   @NotNull
   private final Function0 userIdentity;
   public static int $stable;

   static {}

   public ConversationReducer(@NotNull Function0 var1, @NotNull Function0 var2) {
      Intrinsics.checkNotNullParameter(var1, "config");
      Intrinsics.checkNotNullParameter(var2, "userIdentity");
      super();
      this.config = var1;
      this.userIdentity = var2;
   }

   // $VF: synthetic method
   public ConversationReducer(Function0 var1, Function0 var2, int var3, DefaultConstructorMarker var4) {
      if ((var3 & 1) != 0) {
         var1 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.reducers.ConversationReducer._init_$lambda$0());
      }

      if ((var3 & 2) != 0) {
         var2 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.reducers.ConversationReducer._init_$lambda$1());
      }

      this(var1, var2);
   }

   @NotNull
   public final ConversationUiState computeUiState$intercom_sdk_base_release(@NotNull ConversationClientState var1) {
      Conversation var2;
      NetworkResponse var4;
      boolean var5;
      boolean var6;
      boolean var9;
      boolean var10;
      TeamPresenceState var11;
      Header var97;
      boolean var10001;
      Conversation var10002;
      label185: {
         Intrinsics.checkNotNullParameter(var1, "clientState");
         var2 = var1.getConversation();
         String var3 = var1.getConversationId();
         var4 = var1.getLastNetworkCall();
         var5 = var2 == null && var3 != null;
         var6 = var2 == null && var3 == null;
         boolean var7 = var1.getArticleMetadata() != null;
         boolean var8 = this.isBotIntroEmpty(var1);
         var9 = var6 && var7 && var8;
         var10 = var6 && var1.getOpenMessengerResponse() == null;
         var11 = TeamPresenceReducerKt.reduceTeamPresenceState(var1.getConversation(), var1.getOpenMessengerResponse());
         var10001 = !var1.getPendingMessages().isEmpty();
         var10002 = var1.getConversation();
         OpenMessengerResponse var10003 = var1.getOpenMessengerResponse();
         if (var10003 != null) {
            io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData var96 = var10003.getNewConversationData();
            if (var96 != null) {
               var97 = var96.getHeader();
               break label185;
            }
         }

         var97 = null;
      }

      TopAppBarUiState var12 = HeaderReducerKt.reduceTopAppBarUiState(
         var6,
         var10001,
         var10002,
         var97,
         (AppConfig)this.config.invoke(),
         var1.getUnreadConversationsCount(),
         var1.getUnreadTicketsCount(),
         var1.getLaunchMode(),
         var1.getPushNotificationsBannerState()
      );
      ConversationUiState var10000;
      if (var4 != null && !(var4 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)) {
         var10000 = ErrorReducerKt.reduceError(var4, var12);
      } else if (!var5 && !var10) {
         NetworkState var13 = var1.getNetworkState();
         BottomSheetState var14 = var1.getBottomSheetState();
         BottomBarUiState var15 = ComposerStateReducerKt.reduceComposerState(var1, (AppConfig)this.config.invoke(), var6);
         List var17 = CollectionsKt.createListBuilder();
         List var18 = var17;
         int var19 = 0;
         if (!StringsKt.isBlank(var11.getTeamIntro())) {
            var18.add(new io.intercom.android.sdk.m5.conversation.states.ContentRow$TeamIntroRow(var11.getTeamIntro()));
         }

         if (!StringsKt.isBlank(var11.getSpecialNotice())) {
            var18.add(new io.intercom.android.sdk.m5.conversation.states.ContentRow$SpecialNoticeRow(var11.getSpecialNotice()));
         }

         if (var9 && var1.getArticleMetadata() != null) {
            var18.add(new io.intercom.android.sdk.m5.conversation.states.ContentRow$AskedAboutRow(this.toArticlePart(var1.getArticleMetadata())));
         }

         List var20 = CollectionsKt.createListBuilder();
         List var21 = var20;
         List var22 = var18;
         int var23 = 0;
         if (var6) {
            var21.addAll(IntroPartsReducerKt.reduceIntroParts(var1));
         }

         var21.addAll(ConversationPartsReducerKt.reduceMessages(var1, (UserIdentity)this.userIdentity.invoke(), (AppConfig)this.config.invoke()));
         var22.addAll(ConversationPartsReducerKt.markLastNonSelfPart(CollectionsKt.build(var20)));
         if (var6 && var1.getPendingMessages().isEmpty()) {
            OpenMessengerResponse var82 = var1.getOpenMessengerResponse();
            if (var82 != null) {
               ComposerSuggestions var83 = var82.getComposerSuggestions();
               if (var83 != null) {
                  ComposerSuggestions var24 = var83;
                  int var25 = 0;
                  ContentRow var26 = ComposerSuggestionsReducerKt.reduceComposerSuggestions(var24);
                  if (var26 != null) {
                     ContentRow var27 = var26;
                     int var28 = 0;
                     var18.add(var27);
                  }
               }
            }
         }

         List var85;
         label155: {
            Conversation var84 = var1.getConversation();
            if (var84 != null) {
               var85 = var84.parts();
               if (var85 != null) {
                  break label155;
               }
            }

            var85 = CollectionsKt.emptyList();
         }

         Collection var86 = var85;
         Iterable var52 = var1.getPendingMessages().values();
         Collection var54 = var86;
         int var55 = 0;
         Iterable var57 = var52;
         ArrayList var59 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var52, 10));
         int var62 = 0;

         for (Object var68 : var57) {
            PendingMessage var29 = (PendingMessage)var68;
            Collection var30 = var59;
            int var31 = 0;
            var30.add(var29.getPart());
         }

         List var51;
         int var89;
         label144: {
            var51 = CollectionsKt.plus(var54, (Iterable & List)var59);
            Conversation var87 = var1.getConversation();
            if (var87 != null) {
               List var88 = var87.parts();
               if (var88 != null) {
                  var89 = var88.size();
                  break label144;
               }
            }

            var89 = 0;
         }

         int var53 = var89;
         Iterable var56 = var1.getPendingMessages().values();
         int var58 = 0;
         int var60 = 0;

         for (Object var66 : var56) {
            int var69 = var60++;
            if (var69 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            PendingMessage var71 = (PendingMessage)var66;
            int var73 = var69;
            int var32 = 0;
            int var33 = var53 + var73;
            boolean var34 = ConversationPartsReducerKt.isAdminOrAltParticipant(var71.getPart(), (UserIdentity)this.userIdentity.invoke());
            io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow$PartWrapper var35 = new io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow$PartWrapper(var71.getPart(), var73 == var1.getPendingMessages().size() - 1, var34, null, null, 24, null);
            GroupingPosition var36 = ConversationPartsReducerKt.getGroupingPosition(var51, var33);
            boolean var37 = var71.isFailed();
            boolean var38 = var71.isRetryable();
            io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData var39 = var71.getFailedMediaUploadData();
            SenderMetadata var40 = var34 ? new SenderMetadata(ConversationPartsReducerKt.buildSenderLabel(var71.getPart()), false, 2, null) : null;
            io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow var41 = new io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow(var35, var36, var39, var37, var38, true, var40);
            var18.add(var41);
         }

         if (var1.getCurrentlyTypingState().getUserType() != TypingIndicatorType.NONE) {
            var18.add(new io.intercom.android.sdk.m5.conversation.states.ContentRow$TypingIndicatorRow(var1.getCurrentlyTypingState()));
         }

         if (var2 != null) {
            FooterNotice var91 = var2.getFooterNotice();
            if (var91 != null) {
               FooterNotice var61 = var91;
               int var64 = 0;
               List var92 = var18;
               String var93 = var61.getTitle();
               String var94 = var61.getSubtitle();
               if (var94 == null) {
                  var94 = "";
               }

               List var100;
               label127: {
                  AvatarDetails var98 = var61.getAvatarDetails();
                  if (var98 != null) {
                     List var99 = var98.getAvatars();
                     if (var99 != null) {
                        Iterable var67 = var99;
                        String var70 = var94;
                        String var72 = var93;
                        List var74 = var18;
                        int $i$f$mapx = 0;
                        Iterable var76 = var67;
                        var destination$iv$ivx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var67, 10));
                        int $i$f$mapTox = 0;

                        for (Object var80 : var76) {
                           io.intercom.android.sdk.models.Avatar$Builder var42 = (io.intercom.android.sdk.models.Avatar$Builder)var80;
                           Collection var43 = destination$iv$ivx;
                           int var44 = 0;
                           Avatar var95 = var42.build();
                           Intrinsics.checkNotNullExpressionValue(var95, "build(...)");
                           var43.add(new AvatarWrapper(var95, false, 2, null));
                        }

                        List var81 = (List)destination$iv$ivx;
                        var92 = var74;
                        var93 = var72;
                        var94 = var70;
                        var100 = var81;
                        break label127;
                     }
                  }

                  var100 = CollectionsKt.emptyList();
               }

               List var45 = var100;
               String var46 = var94;
               String var47 = var93;
               FooterNoticeState var48 = new FooterNoticeState(var47, var46, var45);
               var92.add(new io.intercom.android.sdk.m5.conversation.states.ContentRow$FooterNoticeRow(var48));
            }
         }

         List var16 = CollectionsKt.build(var17);
         FloatingIndicatorState var49 = var1.getFloatingIndicatorState();
         VoiceTranscriptionState var50 = var1.getVoiceTranscriptionState();
         var10000 = new io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content(var12, var16, var15, var13, var14, var49, var11, var50);
      } else {
         var10000 = LoadingStateReducerKt.reduceLoadingState(var12);
      }

      return var10000;
   }

   private final boolean isBotIntroEmpty(ConversationClientState var1) {
      List var5;
      label48: {
         OpenMessengerResponse var10000 = var1.getOpenMessengerResponse();
         if (var10000 != null) {
            io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData var4 = var10000.getNewConversationData();
            if (var4 != null) {
               var5 = var4.getParts();
               break label48;
            }
         }

         var5 = null;
      }

      Collection var2 = var5;
      if (var2 == null || var2.isEmpty()) {
         List var8;
         label35: {
            OpenMessengerResponse var6 = var1.getOpenMessengerResponse();
            if (var6 != null) {
               ComposerSuggestions var7 = var6.getComposerSuggestions();
               if (var7 != null) {
                  var8 = var7.getSuggestions();
                  break label35;
               }
            }

            var8 = null;
         }

         Collection var3 = var8;
         if (var3 == null || var3.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private final Part toArticlePart(ArticleMetadata var1) {
      Part var10000 = new io.intercom.android.sdk.models.Part$Builder()
         .withIsInitialMessage(true)
         .withBlocks(CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withArticleId(var1.getId()).withTitle(var1.getTitle())))
         .build();
      Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
      return var10000;
   }

   private static final AppConfig _init_$lambda$0() {
      return (AppConfig)Injector.get().getAppConfigProvider().get();
   }

   private static final UserIdentity _init_$lambda$1() {
      return Injector.get().getUserIdentity();
   }

   public ConversationReducer() {
      this(null, null, 3, null);
   }
}

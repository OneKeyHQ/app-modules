package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0092\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b-\n\u0002\u0010\"\n\u0002\b\u001e\b\u0081\b\u0018\u00002\u00020\u0001B\u009f\u0002\u0012\u0014\b\u0002\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0004\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\u0010\b\u0002\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0012\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0014\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0016\u0012\u000e\b\u0002\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u0018\u0012\u000e\b\u0002\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00040\u0018\u0012\b\b\u0002\u0010\u001a\u001a\u00020\u001b\u0012\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001d\u0012\b\b\u0002\u0010\u001e\u001a\u00020\u001f\u0012\b\b\u0002\u0010 \u001a\u00020\u001f\u0012\b\b\u0002\u0010!\u001a\u00020\"\u0012\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u0004\u0012\b\b\u0002\u0010$\u001a\u00020%\u0012\b\b\u0002\u0010&\u001a\u00020%\u0012\n\b\u0002\u0010'\u001a\u0004\u0018\u00010(\u0012\b\b\u0002\u0010)\u001a\u00020*\u0012\u000e\b\u0002\u0010+\u001a\b\u0012\u0004\u0012\u00020,0\u0018¢\u0006\u0004\b-\u0010.J\r\u0010W\u001a\u00020%H\u0000¢\u0006\u0002\bXJ\u0013\u0010Y\u001a\b\u0012\u0004\u0012\u00020\u00040ZH\u0000¢\u0006\u0002\b[J\u0015\u0010\\\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003HÆ\u0003J\u000b\u0010]\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\u000b\u0010^\u001a\u0004\u0018\u00010\u0004HÆ\u0003J\t\u0010_\u001a\u00020\nHÆ\u0003J\t\u0010`\u001a\u00020\fHÆ\u0003J\t\u0010a\u001a\u00020\u000eHÆ\u0003J\t\u0010b\u001a\u00020\u0010HÆ\u0003J\u0011\u0010c\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0012HÆ\u0003J\u000b\u0010d\u001a\u0004\u0018\u00010\u0014HÆ\u0003J\t\u0010e\u001a\u00020\u0016HÆ\u0003J\u000f\u0010f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0018HÆ\u0003J\u000f\u0010g\u001a\b\u0012\u0004\u0012\u00020\u00040\u0018HÆ\u0003J\t\u0010h\u001a\u00020\u001bHÆ\u0003J\u000b\u0010i\u001a\u0004\u0018\u00010\u001dHÆ\u0003J\t\u0010j\u001a\u00020\u001fHÆ\u0003J\t\u0010k\u001a\u00020\u001fHÆ\u0003J\t\u0010l\u001a\u00020\"HÆ\u0003J\u000b\u0010m\u001a\u0004\u0018\u00010\u0004HÆ\u0003J\t\u0010n\u001a\u00020%HÆ\u0003J\t\u0010o\u001a\u00020%HÆ\u0003J\u000b\u0010p\u001a\u0004\u0018\u00010(HÆ\u0003J\t\u0010q\u001a\u00020*HÆ\u0003J\u000f\u0010r\u001a\b\u0012\u0004\u0012\u00020,0\u0018HÆ\u0003J¡\u0002\u0010s\u001a\u00020\u00002\u0014\b\u0002\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00042\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\u0010\b\u0002\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00122\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00162\u000e\b\u0002\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u00182\u000e\b\u0002\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00040\u00182\b\b\u0002\u0010\u001a\u001a\u00020\u001b2\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\b\b\u0002\u0010\u001e\u001a\u00020\u001f2\b\b\u0002\u0010 \u001a\u00020\u001f2\b\b\u0002\u0010!\u001a\u00020\"2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u00042\b\b\u0002\u0010$\u001a\u00020%2\b\b\u0002\u0010&\u001a\u00020%2\n\b\u0002\u0010'\u001a\u0004\u0018\u00010(2\b\b\u0002\u0010)\u001a\u00020*2\u000e\b\u0002\u0010+\u001a\b\u0012\u0004\u0012\u00020,0\u0018HÇ\u0001J\u0013\u0010t\u001a\u00020%2\b\u0010u\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010v\u001a\u00020\u001fH×\u0001J\t\u0010w\u001a\u00020\u0004H×\u0001R\u001d\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003¢\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b1\u00102R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0004¢\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b5\u00106R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b7\u00108R\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b9\u0010:R\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n\u0000\u001a\u0004\b;\u0010<R\u0019\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b=\u0010>R\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u0014¢\u0006\b\n\u0000\u001a\u0004\b?\u0010@R\u0011\u0010\u0015\u001a\u00020\u0016¢\u0006\b\n\u0000\u001a\u0004\bA\u0010BR\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u0018¢\u0006\b\n\u0000\u001a\u0004\bC\u0010DR\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00040\u0018¢\u0006\b\n\u0000\u001a\u0004\bE\u0010DR\u0011\u0010\u001a\u001a\u00020\u001b¢\u0006\b\n\u0000\u001a\u0004\bF\u0010GR\u0013\u0010\u001c\u001a\u0004\u0018\u00010\u001d¢\u0006\b\n\u0000\u001a\u0004\bH\u0010IR\u0011\u0010\u001e\u001a\u00020\u001f¢\u0006\b\n\u0000\u001a\u0004\bJ\u0010KR\u0011\u0010 \u001a\u00020\u001f¢\u0006\b\n\u0000\u001a\u0004\bL\u0010KR\u0011\u0010!\u001a\u00020\"¢\u0006\b\n\u0000\u001a\u0004\bM\u0010NR\u0013\u0010#\u001a\u0004\u0018\u00010\u0004¢\u0006\b\n\u0000\u001a\u0004\bO\u00104R\u0011\u0010$\u001a\u00020%¢\u0006\b\n\u0000\u001a\u0004\b$\u0010PR\u0011\u0010&\u001a\u00020%¢\u0006\b\n\u0000\u001a\u0004\bQ\u0010PR\u0013\u0010'\u001a\u0004\u0018\u00010(¢\u0006\b\n\u0000\u001a\u0004\bR\u0010SR\u0011\u0010)\u001a\u00020*¢\u0006\b\n\u0000\u001a\u0004\bT\u0010UR\u0017\u0010+\u001a\b\u0012\u0004\u0012\u00020,0\u0018¢\u0006\b\n\u0000\u001a\u0004\bV\u0010D¨\u0006x",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "",
         "pendingMessages",
         "",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage;",
         "conversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "conversationId",
         "currentlyTypingState",
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "composerState",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState;",
         "bottomSheetState",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState;",
         "launchMode",
         "Lio/intercom/android/sdk/m5/conversation/states/LaunchMode;",
         "lastNetworkCall",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "articleMetadata",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "networkState",
         "Lio/intercom/android/sdk/m5/conversation/states/NetworkState;",
         "failedAttributeIdentifiers",
         "",
         "loadingAttributeIdentifiers",
         "finStreamingData",
         "Lio/intercom/android/sdk/m5/conversation/states/FinStreamingData;",
         "openMessengerResponse",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "unreadConversationsCount",
         "",
         "unreadTicketsCount",
         "floatingIndicatorState",
         "Lio/intercom/android/sdk/m5/conversation/states/FloatingIndicatorState;",
         "newMessageId",
         "isConversationScrolled",
         "",
         "dismissedPrivacyNotice",
         "pushNotificationsBannerState",
         "Lio/intercom/android/sdk/m5/conversation/states/PushNotificationsBannerState;",
         "voiceTranscriptionState",
         "Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;",
         "mediaUploadItems",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "<init>",
         "(Ljava/util/Map;Lio/intercom/android/sdk/models/Conversation;Ljava/lang/String;Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;Lio/intercom/android/sdk/m5/conversation/states/ComposerState;Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState;Lio/intercom/android/sdk/m5/conversation/states/LaunchMode;Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;Lio/intercom/android/sdk/m5/conversation/states/NetworkState;Ljava/util/List;Ljava/util/List;Lio/intercom/android/sdk/m5/conversation/states/FinStreamingData;Lio/intercom/android/sdk/models/OpenMessengerResponse;IILio/intercom/android/sdk/m5/conversation/states/FloatingIndicatorState;Ljava/lang/String;ZZLio/intercom/android/sdk/m5/conversation/states/PushNotificationsBannerState;Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;Ljava/util/List;)V",
         "getPendingMessages",
         "()Ljava/util/Map;",
         "getConversation",
         "()Lio/intercom/android/sdk/models/Conversation;",
         "getConversationId",
         "()Ljava/lang/String;",
         "getCurrentlyTypingState",
         "()Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "getComposerState",
         "()Lio/intercom/android/sdk/m5/conversation/states/ComposerState;",
         "getBottomSheetState",
         "()Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState;",
         "getLaunchMode",
         "()Lio/intercom/android/sdk/m5/conversation/states/LaunchMode;",
         "getLastNetworkCall",
         "()Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "getArticleMetadata",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "getNetworkState",
         "()Lio/intercom/android/sdk/m5/conversation/states/NetworkState;",
         "getFailedAttributeIdentifiers",
         "()Ljava/util/List;",
         "getLoadingAttributeIdentifiers",
         "getFinStreamingData",
         "()Lio/intercom/android/sdk/m5/conversation/states/FinStreamingData;",
         "getOpenMessengerResponse",
         "()Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "getUnreadConversationsCount",
         "()I",
         "getUnreadTicketsCount",
         "getFloatingIndicatorState",
         "()Lio/intercom/android/sdk/m5/conversation/states/FloatingIndicatorState;",
         "getNewMessageId",
         "()Z",
         "getDismissedPrivacyNotice",
         "getPushNotificationsBannerState",
         "()Lio/intercom/android/sdk/m5/conversation/states/PushNotificationsBannerState;",
         "getVoiceTranscriptionState",
         "()Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;",
         "getMediaUploadItems",
         "hasUserSentAMessage",
         "hasUserSentAMessage$intercom_sdk_base_release",
         "committedPartUuids",
         "",
         "committedPartUuids$intercom_sdk_base_release",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "component10",
         "component11",
         "component12",
         "component13",
         "component14",
         "component15",
         "component16",
         "component17",
         "component18",
         "component19",
         "component20",
         "component21",
         "component22",
         "component23",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nConversationClientState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationClientState.kt\nio/intercom/android/sdk/m5/conversation/states/ConversationClientState\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,113:1\n1761#2,3:114\n1617#2,9:117\n1869#2:126\n1870#2:129\n1626#2:130\n1#3:127\n1#3:128\n*S KotlinDebug\n*F\n+ 1 ConversationClientState.kt\nio/intercom/android/sdk/m5/conversation/states/ConversationClientState\n*L\n81#1:114,3\n87#1:117,9\n87#1:126\n87#1:129\n87#1:130\n87#1:128\n*E\n"
)
public final class ConversationClientState {
   @NotNull
   private final Map pendingMessages;
   @Nullable
   private final Conversation conversation;
   @Nullable
   private final String conversationId;
   @NotNull
   private final CurrentlyTypingState currentlyTypingState;
   @NotNull
   private final ComposerState composerState;
   @NotNull
   private final BottomSheetState bottomSheetState;
   @NotNull
   private final LaunchMode launchMode;
   @Nullable
   private final NetworkResponse lastNetworkCall;
   @Nullable
   private final ArticleMetadata articleMetadata;
   @NotNull
   private final NetworkState networkState;
   @NotNull
   private final List failedAttributeIdentifiers;
   @NotNull
   private final List loadingAttributeIdentifiers;
   @NotNull
   private final FinStreamingData finStreamingData;
   @Nullable
   private final OpenMessengerResponse openMessengerResponse;
   private final int unreadConversationsCount;
   private final int unreadTicketsCount;
   @NotNull
   private final FloatingIndicatorState floatingIndicatorState;
   @Nullable
   private final String newMessageId;
   private final boolean isConversationScrolled;
   private final boolean dismissedPrivacyNotice;
   @Nullable
   private final PushNotificationsBannerState pushNotificationsBannerState;
   @NotNull
   private final VoiceTranscriptionState voiceTranscriptionState;
   @NotNull
   private final List mediaUploadItems;
   public static final int $stable;

   static { $stable = 8; }

   public ConversationClientState(
      @NotNull Map var1,
      @Nullable Conversation var2,
      @Nullable String var3,
      @NotNull CurrentlyTypingState var4,
      @NotNull ComposerState var5,
      @NotNull BottomSheetState var6,
      @NotNull LaunchMode var7,
      @Nullable NetworkResponse var8,
      @Nullable ArticleMetadata var9,
      @NotNull NetworkState var10,
      @NotNull List var11,
      @NotNull List var12,
      @NotNull FinStreamingData var13,
      @Nullable OpenMessengerResponse var14,
      int var15,
      int var16,
      @NotNull FloatingIndicatorState var17,
      @Nullable String var18,
      boolean var19,
      boolean var20,
      @Nullable PushNotificationsBannerState var21,
      @NotNull VoiceTranscriptionState var22,
      @NotNull List var23
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "pendingMessages");
      Intrinsics.checkNotNullParameter(var4, "currentlyTypingState");
      Intrinsics.checkNotNullParameter(var5, "composerState");
      Intrinsics.checkNotNullParameter(var6, "bottomSheetState");
      Intrinsics.checkNotNullParameter(var7, "launchMode");
      Intrinsics.checkNotNullParameter(var10, "networkState");
      Intrinsics.checkNotNullParameter(var11, "failedAttributeIdentifiers");
      Intrinsics.checkNotNullParameter(var12, "loadingAttributeIdentifiers");
      Intrinsics.checkNotNullParameter(var13, "finStreamingData");
      Intrinsics.checkNotNullParameter(var17, "floatingIndicatorState");
      Intrinsics.checkNotNullParameter(var22, "voiceTranscriptionState");
      Intrinsics.checkNotNullParameter(var23, "mediaUploadItems");
      this.pendingMessages = var1;
      this.conversation = var2;
      this.conversationId = var3;
      this.currentlyTypingState = var4;
      this.composerState = var5;
      this.bottomSheetState = var6;
      this.launchMode = var7;
      this.lastNetworkCall = var8;
      this.articleMetadata = var9;
      this.networkState = var10;
      this.failedAttributeIdentifiers = var11;
      this.loadingAttributeIdentifiers = var12;
      this.finStreamingData = var13;
      this.openMessengerResponse = var14;
      this.unreadConversationsCount = var15;
      this.unreadTicketsCount = var16;
      this.floatingIndicatorState = var17;
      this.newMessageId = var18;
      this.isConversationScrolled = var19;
      this.dismissedPrivacyNotice = var20;
      this.pushNotificationsBannerState = var21;
      this.voiceTranscriptionState = var22;
      this.mediaUploadItems = var23;
   }

   // $VF: synthetic method
   public ConversationClientState(
      Map var1,
      Conversation var2,
      String var3,
      CurrentlyTypingState var4,
      ComposerState var5,
      BottomSheetState var6,
      LaunchMode var7,
      NetworkResponse var8,
      ArticleMetadata var9,
      NetworkState var10,
      List var11,
      List var12,
      FinStreamingData var13,
      OpenMessengerResponse var14,
      int var15,
      int var16,
      FloatingIndicatorState var17,
      String var18,
      boolean var19,
      boolean var20,
      PushNotificationsBannerState var21,
      VoiceTranscriptionState var22,
      List var23,
      int var24,
      DefaultConstructorMarker var25
   ) {
      this((((var24 & 1) != 0) ? (Map) (MapsKt.emptyMap()) : var1), (((var24 & 2) != 0) ? (Conversation) (null) : var2), (((var24 & 4) != 0) ? (String) (null) : var3), (((var24 & 8) != 0) ? (CurrentlyTypingState) (new CurrentlyTypingState(null, false, null, TypingIndicatorType.NONE, 7, null)) : var4), (((var24 & 16) != 0) ? (ComposerState) (new ComposerState$TextInput("", new StringRes(string.intercom_reply_to_conversation, null, 2, null), false, null, null, null, 60, null)) : var5), (((var24 & 32) != 0) ? (BottomSheetState) (BottomSheetState$Empty.INSTANCE) : var6), (((var24 & 64) != 0) ? (LaunchMode) (LaunchMode.CLASSIC) : var7), (((var24 & 128) != 0) ? (NetworkResponse) (null) : var8), (((var24 & 256) != 0) ? (ArticleMetadata) (null) : var9), (((var24 & 512) != 0) ? (NetworkState) (NetworkState$Connected.INSTANCE) : var10), (((var24 & 1024) != 0) ? (List) (CollectionsKt.emptyList()) : var11), (((var24 & 2048) != 0) ? (List) (CollectionsKt.emptyList()) : var12), (((var24 & 4096) != 0) ? (FinStreamingData) (new FinStreamingData(false, null, 3, null)) : var13), (((var24 & 8192) != 0) ? (OpenMessengerResponse) (null) : var14), (((var24 & 16384) != 0) ? (int) (0) : var15), (((var24 & 32768) != 0) ? (int) (0) : var16), (((var24 & 65536) != 0) ? (FloatingIndicatorState) (FloatingIndicatorState$None.INSTANCE) : var17), (((var24 & 131072) != 0) ? (String) (null) : var18), (((var24 & 262144) != 0) ? (boolean) (false) : var19), (((var24 & 524288) != 0) ? (boolean) (false) : var20), (((var24 & 1048576) != 0) ? (PushNotificationsBannerState) (null) : var21), (((var24 & 2097152) != 0) ? (VoiceTranscriptionState) (VoiceTranscriptionState$Idle.INSTANCE) : var22), (((var24 & 4194304) != 0) ? (List) (CollectionsKt.emptyList()) : var23));
   }

   @NotNull
   public final Map getPendingMessages() {
      return this.pendingMessages;
   }

   @Nullable
   public final Conversation getConversation() {
      return this.conversation;
   }

   @Nullable
   public final String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final CurrentlyTypingState getCurrentlyTypingState() {
      return this.currentlyTypingState;
   }

   @NotNull
   public final ComposerState getComposerState() {
      return this.composerState;
   }

   @NotNull
   public final BottomSheetState getBottomSheetState() {
      return this.bottomSheetState;
   }

   @NotNull
   public final LaunchMode getLaunchMode() {
      return this.launchMode;
   }

   @Nullable
   public final NetworkResponse getLastNetworkCall() {
      return this.lastNetworkCall;
   }

   @Nullable
   public final ArticleMetadata getArticleMetadata() {
      return this.articleMetadata;
   }

   @NotNull
   public final NetworkState getNetworkState() {
      return this.networkState;
   }

   @NotNull
   public final List getFailedAttributeIdentifiers() {
      return this.failedAttributeIdentifiers;
   }

   @NotNull
   public final List getLoadingAttributeIdentifiers() {
      return this.loadingAttributeIdentifiers;
   }

   @NotNull
   public final FinStreamingData getFinStreamingData() {
      return this.finStreamingData;
   }

   @Nullable
   public final OpenMessengerResponse getOpenMessengerResponse() {
      return this.openMessengerResponse;
   }

   public final int getUnreadConversationsCount() {
      return this.unreadConversationsCount;
   }

   public final int getUnreadTicketsCount() {
      return this.unreadTicketsCount;
   }

   @NotNull
   public final FloatingIndicatorState getFloatingIndicatorState() {
      return this.floatingIndicatorState;
   }

   @Nullable
   public final String getNewMessageId() {
      return this.newMessageId;
   }

   public final boolean isConversationScrolled() {
      return this.isConversationScrolled;
   }

   public final boolean getDismissedPrivacyNotice() {
      return this.dismissedPrivacyNotice;
   }

   @Nullable
   public final PushNotificationsBannerState getPushNotificationsBannerState() {
      return this.pushNotificationsBannerState;
   }

   @NotNull
   public final VoiceTranscriptionState getVoiceTranscriptionState() {
      return this.voiceTranscriptionState;
   }

   @NotNull
   public final List getMediaUploadItems() {
      return this.mediaUploadItems;
   }

   public final boolean hasUserSentAMessage$intercom_sdk_base_release() {
      if (!this.pendingMessages.isEmpty()) {
         return true;
      }

      if (this.conversation != null) {
         List var10000 = this.conversation.parts();
         if (var10000 != null) {
            var var1 = var10000;
            int var2 = 0;
            if (!(var1 instanceof Collection) || !((Collection)var1).isEmpty()) {
               for (Object var4 : var1) {
                  Part var5 = (Part)var4;
                  int var6 = 0;
                  if (var5.isUser()) {
                     return true;
                  }
               }

               return false;
            }

            return false;
         }
      }

      return false;
   }

   @NotNull
   public final Set committedPartUuids$intercom_sdk_base_release() {
      Set var10000;
      label43: {
         Conversation var1 = this.conversation;
         if (var1 != null) {
            List var2 = var1.parts();
            if (var2 != null) {
               Iterable var3 = var2;
               int var4 = 0;
               Iterable var5 = var3;
               ArrayList var6 = new ArrayList();
               int var7 = 0;
               Iterable var8 = var5;
               int var9 = 0;

               for (Object var11 : var8) {
                  Object var12 = var11;
                  int var13 = 0;
                  Part var14 = (Part)var12;
                  int var15 = 0;
                  String var21 = var14.getUuid();
                  String var22;
                  if (var21 != null) {
                     String var16 = var21;
                     String var17 = var16;
                     int var18 = 0;
                     var22 = !StringsKt.isBlank(var17) ? var16 : null;
                  } else {
                     var22 = null;
                  }

                  if (var22 != null) {
                     Object var19 = var22;
                     int var20 = 0;
                     var6.add(var19);
                  }
               }

               var10000 = CollectionsKt.toSet((Iterable & List)var6);
               break label43;
            }
         }

         var10000 = null;
      }

      if (var10000 == null) {
         var10000 = SetsKt.emptySet();
      }

      return var10000;
   }

   @NotNull
   public final Map component1() {
      return this.pendingMessages;
   }

   @Nullable
   public final Conversation component2() {
      return this.conversation;
   }

   @Nullable
   public final String component3() {
      return this.conversationId;
   }

   @NotNull
   public final CurrentlyTypingState component4() {
      return this.currentlyTypingState;
   }

   @NotNull
   public final ComposerState component5() {
      return this.composerState;
   }

   @NotNull
   public final BottomSheetState component6() {
      return this.bottomSheetState;
   }

   @NotNull
   public final LaunchMode component7() {
      return this.launchMode;
   }

   @Nullable
   public final NetworkResponse component8() {
      return this.lastNetworkCall;
   }

   @Nullable
   public final ArticleMetadata component9() {
      return this.articleMetadata;
   }

   @NotNull
   public final NetworkState component10() {
      return this.networkState;
   }

   @NotNull
   public final List component11() {
      return this.failedAttributeIdentifiers;
   }

   @NotNull
   public final List component12() {
      return this.loadingAttributeIdentifiers;
   }

   @NotNull
   public final FinStreamingData component13() {
      return this.finStreamingData;
   }

   @Nullable
   public final OpenMessengerResponse component14() {
      return this.openMessengerResponse;
   }

   public final int component15() {
      return this.unreadConversationsCount;
   }

   public final int component16() {
      return this.unreadTicketsCount;
   }

   @NotNull
   public final FloatingIndicatorState component17() {
      return this.floatingIndicatorState;
   }

   @Nullable
   public final String component18() {
      return this.newMessageId;
   }

   public final boolean component19() {
      return this.isConversationScrolled;
   }

   public final boolean component20() {
      return this.dismissedPrivacyNotice;
   }

   @Nullable
   public final PushNotificationsBannerState component21() {
      return this.pushNotificationsBannerState;
   }

   @NotNull
   public final VoiceTranscriptionState component22() {
      return this.voiceTranscriptionState;
   }

   @NotNull
   public final List component23() {
      return this.mediaUploadItems;
   }

   @NotNull
   public final ConversationClientState copy(
      @NotNull Map var1,
      @Nullable Conversation var2,
      @Nullable String var3,
      @NotNull CurrentlyTypingState var4,
      @NotNull ComposerState var5,
      @NotNull BottomSheetState var6,
      @NotNull LaunchMode var7,
      @Nullable NetworkResponse var8,
      @Nullable ArticleMetadata var9,
      @NotNull NetworkState var10,
      @NotNull List var11,
      @NotNull List var12,
      @NotNull FinStreamingData var13,
      @Nullable OpenMessengerResponse var14,
      int var15,
      int var16,
      @NotNull FloatingIndicatorState var17,
      @Nullable String var18,
      boolean var19,
      boolean var20,
      @Nullable PushNotificationsBannerState var21,
      @NotNull VoiceTranscriptionState var22,
      @NotNull List var23
   ) {
      Intrinsics.checkNotNullParameter(var1, "pendingMessages");
      Intrinsics.checkNotNullParameter(var4, "currentlyTypingState");
      Intrinsics.checkNotNullParameter(var5, "composerState");
      Intrinsics.checkNotNullParameter(var6, "bottomSheetState");
      Intrinsics.checkNotNullParameter(var7, "launchMode");
      Intrinsics.checkNotNullParameter(var10, "networkState");
      Intrinsics.checkNotNullParameter(var11, "failedAttributeIdentifiers");
      Intrinsics.checkNotNullParameter(var12, "loadingAttributeIdentifiers");
      Intrinsics.checkNotNullParameter(var13, "finStreamingData");
      Intrinsics.checkNotNullParameter(var17, "floatingIndicatorState");
      Intrinsics.checkNotNullParameter(var22, "voiceTranscriptionState");
      Intrinsics.checkNotNullParameter(var23, "mediaUploadItems");
      return new ConversationClientState(
         var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var22, var23
      );
   }

   // $VF: synthetic method
   public static ConversationClientState copy$default(
      ConversationClientState var0,
      Map var1,
      Conversation var2,
      String var3,
      CurrentlyTypingState var4,
      ComposerState var5,
      BottomSheetState var6,
      LaunchMode var7,
      NetworkResponse var8,
      ArticleMetadata var9,
      NetworkState var10,
      List var11,
      List var12,
      FinStreamingData var13,
      OpenMessengerResponse var14,
      int var15,
      int var16,
      FloatingIndicatorState var17,
      String var18,
      boolean var19,
      boolean var20,
      PushNotificationsBannerState var21,
      VoiceTranscriptionState var22,
      List var23,
      int var24,
      Object var25
   ) {
      if ((var24 & 1) != 0) {
         var1 = var0.pendingMessages;
      }

      if ((var24 & 2) != 0) {
         var2 = var0.conversation;
      }

      if ((var24 & 4) != 0) {
         var3 = var0.conversationId;
      }

      if ((var24 & 8) != 0) {
         var4 = var0.currentlyTypingState;
      }

      if ((var24 & 16) != 0) {
         var5 = var0.composerState;
      }

      if ((var24 & 32) != 0) {
         var6 = var0.bottomSheetState;
      }

      if ((var24 & 64) != 0) {
         var7 = var0.launchMode;
      }

      if ((var24 & 128) != 0) {
         var8 = var0.lastNetworkCall;
      }

      if ((var24 & 256) != 0) {
         var9 = var0.articleMetadata;
      }

      if ((var24 & 512) != 0) {
         var10 = var0.networkState;
      }

      if ((var24 & 1024) != 0) {
         var11 = var0.failedAttributeIdentifiers;
      }

      if ((var24 & 2048) != 0) {
         var12 = var0.loadingAttributeIdentifiers;
      }

      if ((var24 & 4096) != 0) {
         var13 = var0.finStreamingData;
      }

      if ((var24 & 8192) != 0) {
         var14 = var0.openMessengerResponse;
      }

      if ((var24 & 16384) != 0) {
         var15 = var0.unreadConversationsCount;
      }

      if ((var24 & 32768) != 0) {
         var16 = var0.unreadTicketsCount;
      }

      if ((var24 & 65536) != 0) {
         var17 = var0.floatingIndicatorState;
      }

      if ((var24 & 131072) != 0) {
         var18 = var0.newMessageId;
      }

      if ((var24 & 262144) != 0) {
         var19 = var0.isConversationScrolled;
      }

      if ((var24 & 524288) != 0) {
         var20 = var0.dismissedPrivacyNotice;
      }

      if ((var24 & 1048576) != 0) {
         var21 = var0.pushNotificationsBannerState;
      }

      if ((var24 & 2097152) != 0) {
         var22 = var0.voiceTranscriptionState;
      }

      if ((var24 & 4194304) != 0) {
         var23 = var0.mediaUploadItems;
      }

      return var0.copy(
         var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var22, var23
      );
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("ConversationClientState(pendingMessages=")
         .append(this.pendingMessages)
         .append(", conversation=")
         .append(this.conversation)
         .append(", conversationId=")
         .append(this.conversationId)
         .append(", currentlyTypingState=")
         .append(this.currentlyTypingState)
         .append(", composerState=")
         .append(this.composerState)
         .append(", bottomSheetState=")
         .append(this.bottomSheetState)
         .append(", launchMode=")
         .append(this.launchMode)
         .append(", lastNetworkCall=")
         .append(this.lastNetworkCall)
         .append(", articleMetadata=")
         .append(this.articleMetadata)
         .append(", networkState=")
         .append(this.networkState)
         .append(", failedAttributeIdentifiers=")
         .append(this.failedAttributeIdentifiers)
         .append(", loadingAttributeIdentifiers=");
      var1.append(this.loadingAttributeIdentifiers)
         .append(", finStreamingData=")
         .append(this.finStreamingData)
         .append(", openMessengerResponse=")
         .append(this.openMessengerResponse)
         .append(", unreadConversationsCount=")
         .append(this.unreadConversationsCount)
         .append(", unreadTicketsCount=")
         .append(this.unreadTicketsCount)
         .append(", floatingIndicatorState=")
         .append(this.floatingIndicatorState)
         .append(", newMessageId=")
         .append(this.newMessageId)
         .append(", isConversationScrolled=")
         .append(this.isConversationScrolled)
         .append(", dismissedPrivacyNotice=")
         .append(this.dismissedPrivacyNotice)
         .append(", pushNotificationsBannerState=")
         .append(this.pushNotificationsBannerState)
         .append(", voiceTranscriptionState=")
         .append(this.voiceTranscriptionState)
         .append(", mediaUploadItems=")
         .append(this.mediaUploadItems);
      var1.append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.pendingMessages.hashCode();
      var1 = var1 * 31 + (this.conversation == null ? 0 : this.conversation.hashCode());
      var1 = var1 * 31 + (this.conversationId == null ? 0 : this.conversationId.hashCode());
      var1 = var1 * 31 + this.currentlyTypingState.hashCode();
      var1 = var1 * 31 + this.composerState.hashCode();
      var1 = var1 * 31 + this.bottomSheetState.hashCode();
      var1 = var1 * 31 + this.launchMode.hashCode();
      var1 = var1 * 31 + (this.lastNetworkCall == null ? 0 : this.lastNetworkCall.hashCode());
      var1 = var1 * 31 + (this.articleMetadata == null ? 0 : this.articleMetadata.hashCode());
      var1 = var1 * 31 + this.networkState.hashCode();
      var1 = var1 * 31 + this.failedAttributeIdentifiers.hashCode();
      var1 = var1 * 31 + this.loadingAttributeIdentifiers.hashCode();
      var1 = var1 * 31 + this.finStreamingData.hashCode();
      var1 = var1 * 31 + (this.openMessengerResponse == null ? 0 : this.openMessengerResponse.hashCode());
      var1 = var1 * 31 + Integer.hashCode(this.unreadConversationsCount);
      var1 = var1 * 31 + Integer.hashCode(this.unreadTicketsCount);
      var1 = var1 * 31 + this.floatingIndicatorState.hashCode();
      var1 = var1 * 31 + (this.newMessageId == null ? 0 : this.newMessageId.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.isConversationScrolled);
      var1 = var1 * 31 + Boolean.hashCode(this.dismissedPrivacyNotice);
      var1 = var1 * 31 + (this.pushNotificationsBannerState == null ? 0 : this.pushNotificationsBannerState.hashCode());
      var1 = var1 * 31 + this.voiceTranscriptionState.hashCode();
      return var1 * 31 + this.mediaUploadItems.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ConversationClientState)) {
         return false;
      } else {
         ConversationClientState var2 = (ConversationClientState)var1;
         if (!Intrinsics.areEqual(this.pendingMessages, var2.pendingMessages)) {
            return false;
         } else if (!Intrinsics.areEqual(this.conversation, var2.conversation)) {
            return false;
         } else if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.currentlyTypingState, var2.currentlyTypingState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.composerState, var2.composerState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.bottomSheetState, var2.bottomSheetState)) {
            return false;
         } else if (this.launchMode != var2.launchMode) {
            return false;
         } else if (!Intrinsics.areEqual(this.lastNetworkCall, var2.lastNetworkCall)) {
            return false;
         } else if (!Intrinsics.areEqual(this.articleMetadata, var2.articleMetadata)) {
            return false;
         } else if (!Intrinsics.areEqual(this.networkState, var2.networkState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.failedAttributeIdentifiers, var2.failedAttributeIdentifiers)) {
            return false;
         } else if (!Intrinsics.areEqual(this.loadingAttributeIdentifiers, var2.loadingAttributeIdentifiers)) {
            return false;
         } else if (!Intrinsics.areEqual(this.finStreamingData, var2.finStreamingData)) {
            return false;
         } else if (!Intrinsics.areEqual(this.openMessengerResponse, var2.openMessengerResponse)) {
            return false;
         } else if (this.unreadConversationsCount != var2.unreadConversationsCount) {
            return false;
         } else if (this.unreadTicketsCount != var2.unreadTicketsCount) {
            return false;
         } else if (!Intrinsics.areEqual(this.floatingIndicatorState, var2.floatingIndicatorState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.newMessageId, var2.newMessageId)) {
            return false;
         } else if (this.isConversationScrolled != var2.isConversationScrolled) {
            return false;
         } else if (this.dismissedPrivacyNotice != var2.dismissedPrivacyNotice) {
            return false;
         } else if (!Intrinsics.areEqual(this.pushNotificationsBannerState, var2.pushNotificationsBannerState)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.voiceTranscriptionState, var2.voiceTranscriptionState)
               ? false
               : Intrinsics.areEqual(this.mediaUploadItems, var2.mediaUploadItems);
         }
      }
   }

   public ConversationClientState() {
      this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 0, null, null, false, false, null, null, null, 8388607, null);
   }
}

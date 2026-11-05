package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.Gson;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.sdk.AblyManager;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.api.MessengerApiHelper;
import io.intercom.android.sdk.api.UserUpdateRequest;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRequestBody;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.m5.upload.data.UploadRepository;
import io.intercom.android.sdk.utilities.TimeFormatterExtKt;
import java.io.File;
import java.util.List;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import okhttp3.RequestBody;
import okhttp3.MultipartBody.Part;
import okhttp3.MultipartBody.Part.Companion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000À\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001BK\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\u0010\u0010\u0011J\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\"0!J<\u0010$\u001a\b\u0012\u0004\u0012\u00020&0%2\u0006\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020(2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020,0+2\b\u0010-\u001a\u0004\u0018\u00010.H\u0086@¢\u0006\u0002\u0010/Jl\u00100\u001a\b\u0012\u0004\u0012\u0002010%2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020,0+2\b\u00102\u001a\u0004\u0018\u00010(2\b\u00103\u001a\u0004\u0018\u00010(2\b\u00104\u001a\u0004\u0018\u00010(2\u000e\u00105\u001a\n\u0012\u0004\u0012\u000206\u0018\u00010+2\b\u00107\u001a\u0004\u0018\u00010.2\b\u00108\u001a\u0004\u0018\u00010(2\u0006\u0010)\u001a\u00020(H\u0086@¢\u0006\u0002\u00109J$\u0010:\u001a\b\u0012\u0004\u0012\u00020;0%2\u0006\u0010'\u001a\u00020(2\u0006\u0010<\u001a\u00020=H\u0086@¢\u0006\u0002\u0010>J&\u0010?\u001a\b\u0012\u0004\u0012\u00020;0%2\u0006\u0010@\u001a\u00020(2\b\u00108\u001a\u0004\u0018\u00010(H\u0086@¢\u0006\u0002\u0010AJ4\u0010B\u001a\b\u0012\u0004\u0012\u00020&0%2\u0006\u0010C\u001a\u00020(2\u0006\u0010'\u001a\u00020(2\u0006\u0010D\u001a\u00020(2\u0006\u0010)\u001a\u00020(H\u0086@¢\u0006\u0002\u0010EJ\u0016\u0010F\u001a\u00020G2\u0006\u0010'\u001a\u00020(H\u0086@¢\u0006\u0002\u0010HJ\u001c\u0010I\u001a\b\u0012\u0004\u0012\u00020J0%2\u0006\u0010K\u001a\u00020(H\u0086@¢\u0006\u0002\u0010HJ\u001c\u0010L\u001a\b\u0012\u0004\u0012\u00020M0%2\u0006\u0010N\u001a\u00020OH\u0086@¢\u0006\u0002\u0010PJ<\u0010Q\u001a\b\u0012\u0004\u0012\u00020;0%2\u0006\u0010'\u001a\u00020(2\u0006\u0010R\u001a\u00020(2\u0006\u0010S\u001a\u00020(2\u0006\u0010T\u001a\u00020(2\u0006\u0010U\u001a\u00020(H\u0086@¢\u0006\u0002\u0010VJ\u001c\u0010W\u001a\b\u0012\u0004\u0012\u00020X0%2\u0006\u0010Y\u001a\u00020ZH\u0086@¢\u0006\u0002\u0010[J\u0006\u0010\u001a\u001a\u00020\u001bJ\u0006\u0010\\\u001a\u00020GJ!\u0010]\u001a\u00020G2\u0006\u0010'\u001a\u00020(2\f\u0010^\u001a\b\u0012\u0004\u0012\u00020(0_¢\u0006\u0002\u0010`R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010\u001a\u001a\u00020\u001b8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\u001f\u001a\u0004\b\u001c\u0010\u001dR\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020\"0!X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006a",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "",
         "messengerApi",
         "Lio/intercom/android/sdk/api/MessengerApi;",
         "uploadRepository",
         "Lio/intercom/android/sdk/m5/upload/data/UploadRepository;",
         "api",
         "Lio/intercom/android/sdk/api/Api;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "ablyManager",
         "Lio/intercom/android/sdk/AblyManager;",
         "nexusClient",
         "Lio/intercom/android/nexus/NexusClient;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "<init>",
         "(Lio/intercom/android/sdk/api/MessengerApi;Lio/intercom/android/sdk/m5/upload/data/UploadRepository;Lio/intercom/android/sdk/api/Api;Lio/intercom/android/sdk/identity/UserIdentity;Lio/intercom/android/sdk/AblyManager;Lio/intercom/android/nexus/NexusClient;Lkotlinx/coroutines/CoroutineScope;)V",
         "getMessengerApi",
         "()Lio/intercom/android/sdk/api/MessengerApi;",
         "getApi",
         "()Lio/intercom/android/sdk/api/Api;",
         "getUserIdentity",
         "()Lio/intercom/android/sdk/identity/UserIdentity;",
         "getAblyManager",
         "()Lio/intercom/android/sdk/AblyManager;",
         "nexusEventsRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/NexusEventsRepository;",
         "getNexusEventsRepository",
         "()Lio/intercom/android/sdk/m5/conversation/data/NexusEventsRepository;",
         "nexusEventsRepository$delegate",
         "Lkotlin/Lazy;",
         "nexusEventFlow",
         "Lkotlinx/coroutines/flow/Flow;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent;",
         "realTimeEvents",
         "replyToConversation",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/Part$Builder;",
         "conversationId",
         "",
         "clientUUID",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "lastAdminPartCreatedAt",
         "",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/Long;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "createNewConversation",
         "Lio/intercom/android/sdk/models/ConversationResponse$Builder;",
         "botIntroId",
         "articleId",
         "botBehaviourId",
         "suggestions",
         "Lio/intercom/android/sdk/models/ComposerSuggestions$Suggestion;",
         "snapshotId",
         "openRequestId",
         "(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "getConversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "reason",
         "Lio/intercom/android/sdk/m5/conversation/data/GetConversationReason;",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/conversation/data/GetConversationReason;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "createConversationFromSuggestion",
         "suggestionId",
         "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "addQuickReplyToConversation",
         "quickReplyId",
         "quickReplyPartId",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "markAsRead",
         "",
         "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "loadGifs",
         "Lio/intercom/android/sdk/models/GifResponse;",
         "searchQuery",
         "uploadMedia",
         "Lio/intercom/android/sdk/models/Upload$Builder;",
         "imageData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "(Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "submitForm",
         "partId",
         "identifier",
         "formValue",
         "formType",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "transcribe",
         "Lio/intercom/android/sdk/models/TranscribedText;",
         "audioFile",
         "Ljava/io/File;",
         "(Ljava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "dismissPrivacyPolicy",
         "recordInteractions",
         "interactions",
         "",
         "(Ljava/lang/String;[Ljava/lang/String;)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ConversationRepository {
   @NotNull
   private final MessengerApi messengerApi;
   @NotNull
   private final UploadRepository uploadRepository;
   @NotNull
   private final Api api;
   @NotNull
   private final UserIdentity userIdentity;
   @NotNull
   private final AblyManager ablyManager;
   @NotNull
   private final NexusClient nexusClient;
   @NotNull
   private final Lazy nexusEventsRepository$delegate;
   @NotNull
   private final Flow nexusEventFlow;
   public static final int $stable;

   static { $stable = 8; }

   public ConversationRepository(
      @NotNull MessengerApi var1,
      @NotNull UploadRepository var2,
      @NotNull Api var3,
      @NotNull UserIdentity var4,
      @NotNull AblyManager var5,
      @NotNull NexusClient var6,
      @NotNull CoroutineScope var7
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "messengerApi");
      Intrinsics.checkNotNullParameter(var2, "uploadRepository");
      Intrinsics.checkNotNullParameter(var3, "api");
      Intrinsics.checkNotNullParameter(var4, "userIdentity");
      Intrinsics.checkNotNullParameter(var5, "ablyManager");
      Intrinsics.checkNotNullParameter(var6, "nexusClient");
      Intrinsics.checkNotNullParameter(var7, "scope");
      this.messengerApi = var1;
      this.uploadRepository = var2;
      this.api = var3;
      this.userIdentity = var4;
      this.ablyManager = var5;
      this.nexusClient = var6;
      final io.intercom.android.sdk.m5.conversation.data.ConversationRepository recoveredExpressionCapture_282_0 = this;
      final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_282_1 = var7;
      this.nexusEventsRepository$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.data.ConversationRepository.nexusEventsRepository_delegate$lambda$0(recoveredExpressionCapture_282_0, recoveredExpressionCapture_282_1)));
      this.nexusEventFlow = CombinedEventAsFlowKt.combinedEventAsFlow(this.nexusClient, this.ablyManager);
   }

   // $VF: synthetic method
   public ConversationRepository(
      MessengerApi var1,
      UploadRepository var2,
      Api var3,
      UserIdentity var4,
      AblyManager var5,
      NexusClient var6,
      CoroutineScope var7,
      int var8,
      DefaultConstructorMarker var9
   ) {
      this((((var8 & 1) != 0) ? (MessengerApi) (Injector.get().getMessengerApi()) : var1), (((var8 & 2) != 0) ? (UploadRepository) (new UploadRepository(null, null, null, null, 15, null)) : var2), (((var8 & 4) != 0) ? (Api) (Injector.get().getApi()) : var3), (((var8 & 8) != 0) ? (UserIdentity) (Injector.get().getUserIdentity()) : var4), (((var8 & 16) != 0) ? (AblyManager) (Injector.get().getAblyManager()) : var5), (((var8 & 32) != 0) ? (NexusClient) (Injector.get().getNexusClient()) : var6), var7);
   }

   @NotNull
   public final MessengerApi getMessengerApi() {
      return this.messengerApi;
   }

   @NotNull
   public final Api getApi() {
      return this.api;
   }

   @NotNull
   public final UserIdentity getUserIdentity() {
      return this.userIdentity;
   }

   @NotNull
   public final AblyManager getAblyManager() {
      return this.ablyManager;
   }

   private final NexusEventsRepository getNexusEventsRepository() {
      Lazy var1 = this.nexusEventsRepository$delegate;
      return (NexusEventsRepository)var1.getValue();
   }

   @NotNull
   public final Flow realTimeEvents() {
      return this.nexusEventFlow;
   }

   @Nullable
   public final Object replyToConversation(@NotNull String var1, @NotNull String var2, @NotNull List var3, @Nullable Long var4, @NotNull Continuation var5) {
      Map var7 = this.api.createBaseReplyParams();
      Map var8 = var7;
      int var9 = 0;
      var8.put("blocks", var3);
      var8.put("client_assigned_uuid", var2);
      if (var4 != null) {
         long var10 = var4.longValue();
         int var12 = 0;
         var8.put("last_admin_part_created_at", TimeFormatterExtKt.toISOFormat(var4));
      }

      Map var6 = var7;
      MessengerApiHelper var10000 = MessengerApiHelper.INSTANCE;
      Intrinsics.checkNotNull(var6);
      RequestBody var13 = var10000.getDefaultRequestBody$intercom_sdk_base_release(var6);
      return this.messengerApi.replyToConversationSuspend(var1, var13, var5);
   }

   @Nullable
   public final Object createNewConversation(
      @NotNull List var1,
      @Nullable String var2,
      @Nullable String var3,
      @Nullable String var4,
      @Nullable List var5,
      @Nullable Long var6,
      @Nullable String var7,
      @NotNull String var8,
      @NotNull Continuation var9
   ) {
      Map var11 = this.api.baseNewConversationParams();
      Map var12 = var11;
      int var13 = 0;
      var12.put("blocks", var1);
      var12.put("client_assigned_uuid", var8);
      if (var2 != null) {
         var12.put("bot_intro", var2);
      }

      if (var3 != null) {
         var12.put("article_id", var3);
      }

      if (var4 != null) {
         var12.put("resolution_bot_behavior_version_id", var4);
      }

      if (var5 != null) {
         var12.put("composer_suggestions", new Gson().toJson(var5));
      }

      if (var6 != null) {
         var12.put("snapshot_id", var6);
      }

      if (var7 != null) {
         var12.put("messenger_open_request_id", var7);
      }

      Map var10 = var11;
      MessengerApiHelper var10000 = MessengerApiHelper.INSTANCE;
      Intrinsics.checkNotNull(var10);
      RequestBody var14 = var10000.getDefaultRequestBody$intercom_sdk_base_release(var10);
      return this.messengerApi.startNewConversationSuspend(var14, var9);
   }

   @Nullable
   public final Object getConversation(@NotNull String var1, @NotNull GetConversationReason var2, @NotNull Continuation var3) {
      String var10000;
      switch (var2) {
         case NEW_COMMENT:
            var10000 = "Nexus New Comment";
            break;
         case NEXUS_CONNECTED:
            var10000 = "Nexus Reconnected";
            break;
         case NETWORK_CONNECTED:
            var10000 = "Internet Reconnected";
            break;
         case OPEN_CONVERSATION:
            var10000 = "Click Conversation";
            break;
         case POLLING:
            var10000 = "State Sync";
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      String var4 = var10000;
      Pair[] var6 = new Pair[]{TuplesKt.to("request_origin", var4)};
      Map var5 = MapsKt.mutableMapOf(var6);
      if (var2 == GetConversationReason.POLLING) {
         var5.put("sync", "true");
      }

      RequestBody var7 = MessengerApiHelper.INSTANCE.getDefaultRequestBody$intercom_sdk_base_release(var5);
      return this.messengerApi.getConversationSuspend(var1, var7, var3);
   }

   @Nullable
   public final Object createConversationFromSuggestion(@NotNull String var1, @Nullable String var2, @NotNull Continuation var3) {
      MessengerApiHelper var10000 = MessengerApiHelper.INSTANCE;
      Map var5 = MapsKt.createMapBuilder();
      Map var6 = var5;
      MessengerApiHelper var8 = var10000;
      int var7 = 0;
      var6.put("id", var1);
      if (var2 != null) {
         var6.put("messenger_open_request_id", var2);
      }

      RequestBody var4 = var8.getDefaultRequestBody$intercom_sdk_base_release(MapsKt.build(var5));
      return this.messengerApi.triggerInboundConversationSuspend(var4, var3);
   }

   @Nullable
   public final Object addQuickReplyToConversation(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull Continuation var5
   ) {
      Pair[] var7 = new Pair[]{TuplesKt.to("reply_option_uuid", var1), TuplesKt.to("client_assigned_uuid", var4)};
      Map var6 = MapsKt.mutableMapOf(var7);
      if (var3.length() > 0) {
         var6.put("quick_reply_part_id", var3);
      }

      RequestBody var8 = MessengerApiHelper.INSTANCE.getDefaultRequestBody$intercom_sdk_base_release(var6);
      return this.messengerApi.addConversationQuickReplySuspend(var2, var8, var5);
   }

   @Nullable
   public final Object markAsRead(@NotNull String var1, @NotNull Continuation var2) {
      RequestBody var3 = MessengerApiHelper.INSTANCE
         .getDefaultRequestBody$intercom_sdk_base_release(MapsKt.mapOf(TuplesKt.to("app_id", Injector.get().getAppIdentity().appId())));
      Object var10000 = this.messengerApi.markAsReadSuspend(var1, var3, var2);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   @Nullable
   public final Object loadGifs(@NotNull String var1, @NotNull Continuation var2) {
      Map var3 = !StringsKt.isBlank(var1) ? MapsKt.mapOf(TuplesKt.to("query", var1)) : MapsKt.emptyMap();
      RequestBody var4 = MessengerApiHelper.INSTANCE.getDefaultRequestBody$intercom_sdk_base_release(var3);
      return this.messengerApi.getGifsSuspended(var4, var2);
   }

   @Nullable
   public final Object uploadMedia(@NotNull MediaData$Media var1, @NotNull Continuation var2) {
      return this.uploadRepository.uploadFile(var1, var2);
   }

   @Nullable
   public final Object submitForm(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull String var5, @NotNull Continuation var6
   ) {
      Pair[] var8 = new Pair[]{
         TuplesKt.to("conversation_part_id", var2), TuplesKt.to("identifier", var3), TuplesKt.to("value", var4), TuplesKt.to("type", var5)
      };
      Map var7 = MapsKt.mapOf(var8);
      RequestBody var9 = MessengerApiHelper.INSTANCE.getDefaultRequestBody$intercom_sdk_base_release(MapsKt.mapOf(TuplesKt.to("form_params", var7)));
      return this.messengerApi.submitFormSuspend(var1, var9, var6);
   }

   @Nullable
   public final Object transcribe(@NotNull File var1, @NotNull Continuation var2) {
      String var10000 = this.userIdentity.getEmail();
      Intrinsics.checkNotNullExpressionValue(var10000, "getEmail(...)");
      if (var10000.length() != 0) {
         Map var5 = MapsKt.createMapBuilder();
         Map var6 = var5;
         int var7 = 0;
         String var10002 = this.userIdentity.getEmail();
         Intrinsics.checkNotNullExpressionValue(var10002, "getEmail(...)");
         var6.put("email", var10002);
         var10000 = this.userIdentity.getUserId();
         Intrinsics.checkNotNullExpressionValue(var10000, "getUserId(...)");
         if (var10000.length() != 0) {
            var10002 = this.userIdentity.getUserId();
            Intrinsics.checkNotNullExpressionValue(var10002, "getUserId(...)");
            var6.put("user_id", var10002);
         }

         Map var4 = MapsKt.build(var5);
         var10000 = new Gson().toJson(var4);
      } else {
         Map var10 = MapsKt.mapOf(TuplesKt.to("anonymous_id", this.userIdentity.getAnonymousId()));
         var10000 = new Gson().toJson(var10);
      }

      String var3 = var10000;
      AudioRequestBody var11 = new AudioRequestBody(var1);
      Part var12 = Part.Companion.createFormData("audio", var1.getName(), var11);
      Companion var17 = Part.Companion;
      Intrinsics.checkNotNull(var3);
      Part var13 = var17.createFormData("user_data", var3);
      String var14 = Injector.get().getAppIdentity().appId();
      Companion var18 = Part.Companion;
      Intrinsics.checkNotNull(var14);
      Part var8 = var18.createFormData("app_id", var14);
      List var9 = MessengerApiHelper.INSTANCE.getDefaultMultipartFields$intercom_sdk_base_release();
      return this.messengerApi.transcribeSuspend(var12, var13, var8, var9, var2);
   }

   @NotNull
   public final NexusEventsRepository nexusEventsRepository() {
      return this.getNexusEventsRepository();
   }

   public final void dismissPrivacyPolicy() {
      Map var1 = MapsKt.mapOf(TuplesKt.to("dismissed_privacy_policy_notice_at", System.currentTimeMillis() / 1000));
      this.api.updateUser(UserUpdateRequest.create(false, false, var1, true), new ConversationRepository$dismissPrivacyPolicy$1());
   }

   public final void recordInteractions(@NotNull String var1, @NotNull String[] var2) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "interactions");
      this.api.recordInteractions(var1, var2);
   }

   private static final NexusEventsRepository nexusEventsRepository_delegate$lambda$0(ConversationRepository var0, CoroutineScope var1) {
      return new NexusEventsRepository(var0.nexusClient, var0.userIdentity, var1);
   }
}

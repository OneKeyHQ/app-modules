package io.intercom.android.sdk.api;

import java.util.List;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000¶\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b`\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\"\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J(\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00032\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u000fJ(\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u000fJ\"\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J(\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\n0\u00032\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u000fJ\u0018\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\"\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u001e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\u0018\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u001e\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00190\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J(\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00032\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0003\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u000fJB\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u00032\b\b\u0001\u0010\u001e\u001a\u00020\u001f2\b\b\u0001\u0010 \u001a\u00020\u001f2\b\b\u0001\u0010!\u001a\u00020\u001f2\u000e\b\u0001\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u001f0#H§@¢\u0006\u0002\u0010$J\u001e\u0010%\u001a\b\u0012\u0004\u0012\u00020&0\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\"\u0010'\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\"\u0010(\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J(\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u000fJ\u0018\u0010*\u001a\b\u0012\u0004\u0012\u00020+0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010,\u001a\b\u0012\u0004\u0012\u00020-0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u001e\u0010.\u001a\b\u0012\u0004\u0012\u00020/0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\u0018\u00100\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u00101\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u00102\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u00103\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\"\u00104\u001a\b\u0012\u0004\u0012\u0002050\t2\b\b\u0001\u00106\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\"\u00107\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u00106\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u001e\u00108\u001a\b\u0012\u0004\u0012\u0002090\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\u0018\u0010:\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010;\u001a\b\u0012\u0004\u0012\u00020<0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010=\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u001e\u0010>\u001a\b\u0012\u0004\u0012\u00020?0\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\u001e\u0010@\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\u0018\u0010A\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010B\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010C\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010D\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010E\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010F\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\"\u0010G\u001a\b\u0012\u0004\u0012\u00020H0\t2\b\b\u0001\u0010I\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010J\u001a\b\u0012\u0004\u0012\u00020H0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u0018\u0010K\u001a\b\u0012\u0004\u0012\u00020L0\t2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H'J\u001e\u0010M\u001a\b\u0012\u0004\u0012\u00020N0\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\u001e\u0010O\u001a\b\u0012\u0004\u0012\u00020P0\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007¨\u0006Q",
   d2 = {
         "Lio/intercom/android/sdk/api/MessengerApi;",
         "",
         "startNewConversationSuspend",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/ConversationResponse$Builder;",
         "options",
         "Lokhttp3/RequestBody;",
         "(Lokhttp3/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "reactToConversation",
         "Lretrofit2/Call;",
         "Ljava/lang/Void;",
         "conversationId",
         "",
         "submitFormSuspend",
         "Lio/intercom/android/sdk/models/Conversation;",
         "(Ljava/lang/String;Lokhttp3/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "replyToConversationSuspend",
         "Lio/intercom/android/sdk/models/Part$Builder;",
         "markAsRead",
         "markAsReadSuspend",
         "markAsDismissed",
         "recordInteractions",
         "getConversationsSuspend",
         "Lio/intercom/android/sdk/models/ConversationsResponse$Builder;",
         "getUnreadConversations",
         "Lio/intercom/android/sdk/models/UsersResponse$Builder;",
         "getUnreadConversationsSuspended",
         "getConversationSuspend",
         "transcribeSuspend",
         "Lio/intercom/android/sdk/models/TranscribedText;",
         "audio",
         "Lokhttp3/MultipartBody$Part;",
         "userData",
         "appId",
         "defaultFields",
         "",
         "(Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "openMessengerSuspended",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "rateConversation",
         "addConversationRatingRemark",
         "addConversationQuickReplySuspend",
         "updateUser",
         "Lio/intercom/android/sdk/models/UpdateUserResponse$Builder;",
         "logEvent",
         "Lio/intercom/android/sdk/models/LogEventResponse$Builder;",
         "getUploadFileUrlSuspended",
         "Lio/intercom/android/sdk/models/Upload$Builder;",
         "setDeviceToken",
         "deleteDeviceToken",
         "setAuthToken",
         "sendMetrics",
         "getLink",
         "Lio/intercom/android/sdk/models/LinkResponse$Builder;",
         "articleId",
         "reactToLink",
         "getGifsSuspended",
         "Lio/intercom/android/sdk/models/GifResponse;",
         "reportError",
         "getSheet",
         "Lio/intercom/android/sdk/models/Sheet$Builder;",
         "submitSheet",
         "getHomeCardsV2Suspend",
         "Lio/intercom/android/sdk/m5/home/data/HomeV2Response;",
         "triggerInboundConversationSuspend",
         "markPushAsOpened",
         "markCarouselAsDismissed",
         "markCarouselAsCompleted",
         "markCarouselScreenViewed",
         "markPermissionGranted",
         "markCarouselActionButtonTapped",
         "getProgrammaticCarousel",
         "Lio/intercom/android/sdk/models/carousel/CarouselResponse$Builder;",
         "carouselId",
         "getCarousel",
         "getSurvey",
         "Lio/intercom/android/sdk/survey/model/FetchSurveyRequest;",
         "getAblyConnectConfigSuspend",
         "Lio/intercom/android/sdk/models/AblyConnectResponse;",
         "getAblyAuthTokenSuspend",
         "Lio/intercom/android/sdk/models/AblyAuthResponse;",
         "intercom-sdk-base_release"
   }
)
public interface MessengerApi {
   @POST("conversations")
   @Nullable
   Object startNewConversationSuspend(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("conversations/{conversationId}/react")
   @NotNull
   Call reactToConversation(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2);

   @POST("conversations/{conversationId}/form")
   @Nullable
   Object submitFormSuspend(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("conversations/{conversationId}/reply")
   @Nullable
   Object replyToConversationSuspend(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("conversations/{conversationId}/read")
   @NotNull
   Call markAsRead(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2);

   @POST("conversations/{conversationId}/read")
   @Nullable
   Object markAsReadSuspend(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("conversations/dismiss")
   @NotNull
   Call markAsDismissed(@Body @NotNull RequestBody var1);

   @POST("conversations/{conversationId}/record_interactions")
   @NotNull
   Call recordInteractions(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2);

   @POST("conversations/inbox")
   @Nullable
   Object getConversationsSuspend(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("conversations/unread")
   @NotNull
   Call getUnreadConversations(@Body @NotNull RequestBody var1);

   @POST("conversations/unread")
   @Nullable
   Object getUnreadConversationsSuspended(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("conversations/{conversationId}")
   @Nullable
   Object getConversationSuspend(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @Multipart
   @POST("conversations/transcribe")
   @Nullable
   Object transcribeSuspend(
      @Part @NotNull okhttp3.MultipartBody.Part var1,
      @Part @NotNull okhttp3.MultipartBody.Part var2,
      @Part @NotNull okhttp3.MultipartBody.Part var3,
      @Part @NotNull List var4,
      @NotNull Continuation var5
   );

   @POST("open")
   @Nullable
   Object openMessengerSuspended(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("conversations/{conversationId}/rate")
   @NotNull
   Call rateConversation(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2);

   @POST("conversations/{conversationId}/remark")
   @NotNull
   Call addConversationRatingRemark(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2);

   @POST("conversations/{conversationId}/quick_reply")
   @Nullable
   Object addConversationQuickReplySuspend(@Path("conversationId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("users")
   @NotNull
   Call updateUser(@Body @NotNull RequestBody var1);

   @POST("events")
   @NotNull
   Call logEvent(@Body @NotNull RequestBody var1);

   @POST("uploads")
   @Nullable
   Object getUploadFileUrlSuspended(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("device_tokens")
   @NotNull
   Call setDeviceToken(@Body @NotNull RequestBody var1);

   @PUT("device_tokens")
   @NotNull
   Call deleteDeviceToken(@Body @NotNull RequestBody var1);

   @POST("user_auth_tokens")
   @NotNull
   Call setAuthToken(@Body @NotNull RequestBody var1);

   @POST("metrics")
   @NotNull
   Call sendMetrics(@Body @NotNull RequestBody var1);

   @POST("articles/{articleId}")
   @NotNull
   Call getLink(@Path("articleId") @NotNull String var1, @Body @NotNull RequestBody var2);

   @POST("articles/{articleId}/react")
   @NotNull
   Call reactToLink(@Path("articleId") @NotNull String var1, @Body @NotNull RequestBody var2);

   @POST("gifs")
   @Nullable
   Object getGifsSuspended(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("error_reports")
   @NotNull
   Call reportError(@Body @NotNull RequestBody var1);

   @POST("sheets/open")
   @NotNull
   Call getSheet(@Body @NotNull RequestBody var1);

   @POST("sheets/submit")
   @NotNull
   Call submitSheet(@Body @NotNull RequestBody var1);

   @POST("home")
   @Nullable
   Object getHomeCardsV2Suspend(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("custom_bots/trigger_inbound_conversation")
   @Nullable
   Object triggerInboundConversationSuspend(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("stats_system/push_opened")
   @NotNull
   Call markPushAsOpened(@Body @NotNull RequestBody var1);

   @POST("stats_system/carousel_dismissed")
   @NotNull
   Call markCarouselAsDismissed(@Body @NotNull RequestBody var1);

   @POST("stats_system/carousel_completed")
   @NotNull
   Call markCarouselAsCompleted(@Body @NotNull RequestBody var1);

   @POST("stats_system/carousel_screen_viewed")
   @NotNull
   Call markCarouselScreenViewed(@Body @NotNull RequestBody var1);

   @POST("stats_system/carousel_permission_granted")
   @NotNull
   Call markPermissionGranted(@Body @NotNull RequestBody var1);

   @POST("stats_system/carousel_button_action_tapped")
   @NotNull
   Call markCarouselActionButtonTapped(@Body @NotNull RequestBody var1);

   @POST("carousels/{carouselId}/fetch")
   @NotNull
   Call getProgrammaticCarousel(@Path("carouselId") @NotNull String var1, @Body @NotNull RequestBody var2);

   @POST("content/fetch_carousel")
   @NotNull
   Call getCarousel(@Body @NotNull RequestBody var1);

   @POST("content/fetch_survey")
   @NotNull
   Call getSurvey(@Body @NotNull RequestBody var1);

   @POST("realtime/connect")
   @Nullable
   Object getAblyConnectConfigSuspend(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("realtime/authorize")
   @Nullable
   Object getAblyAuthTokenSuspend(@Body @NotNull RequestBody var1, @NotNull Continuation var2);
}

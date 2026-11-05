package io.intercom.android.sdk.api;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.RestrictTo.Scope;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.AuthToken;
import io.intercom.android.sdk.AuthTokenKt;
import io.intercom.android.sdk.IntercomError;
import io.intercom.android.sdk.IntercomStatusCallback;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.errorreporting.ErrorReport;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.AppIdentity;
import io.intercom.android.sdk.identity.DeviceIdentityStore;
import io.intercom.android.sdk.identity.PushTokenStore;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.MetricObject;
import io.intercom.android.sdk.metrics.ops.OpsMetricObject;
import io.intercom.android.sdk.metrics.ops.OpsMetricTracker;
import io.intercom.android.sdk.models.HostAppState;
import io.intercom.android.sdk.models.LinkResponse$Builder;
import io.intercom.android.sdk.models.OverlayState;
import io.intercom.android.sdk.models.Sheet$Builder;
import io.intercom.android.sdk.models.carousel.Carousel;
import io.intercom.android.sdk.models.carousel.CarouselResponse$Builder;
import io.intercom.android.sdk.user.DeviceData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import org.json.JSONObject;
import retrofit2.Callback;

public class Api {
   private static final String USER = "user";
   private static final String DEVICE_TOKEN = "device_token";
   private static final String AUTH_TOKENS = "auth_tokens";
   private static final String USER_ATTRIBUTES = "user_attributes";
   private static final String DEVICE_DATA = "device_data";
   private static final String NEW_SESSION = "new_session";
   private static final String HMAC = "hmac";
   private static final String JWT = "intercom_user_jwt";
   private static final String MESSENGER_ENVIRONMENT = "messenger_environment";
   private static final String DEVICE_IDENTIFIER = "device_identifier";
   private static final String SENT_FROM_BACKGROUND = "sent_from_background";
   private static final String CAROUSEL_VISIBLE = "carousel_visible";
   private static final String CAROUSELS_ENABLED = "carousels_enabled";
   private static final String SURVEYS_ENABLED = "surveys_enabled";
   private static final String INSTANCE_ID = "instance_id";
   private static final String SCREEN_ID = "screen_id";
   private static final String CAROUSEL_CREATED_VIA = "created_via";
   private static final String CAROUSEL_PROGRAMMATIC = "triggered_from_code";
   private static final String CAROUSEL_AUTOMATIC = "shown_automatically";
   private static final String SESSION_ID = "session_id";
   private static final Twig TWIG = LumberMill.getLogger();
   final CallbackHolder callbacks;
   private final Context context;
   private final AppIdentity appIdentity;
   private final Provider appConfigProvider;
   final UserIdentity userIdentity;
   final OkHttpClient httpClient = new OkHttpClient();
   private final MessengerApi messengerApi;
   private final OkHttpClient apiHttpClient;
   private final int defaultOkHttpMaxRequests;
   private final RateLimiter rateLimiter;
   private OpsMetricTracker opsMetricTracker;
   private final IntercomDataLayer dataLayer;
   private final DeviceIdentityStore deviceIdentityStore;
   private final PushTokenStore pushTokenStore;
   private final Callback emptyCallback = new Api$8(this);

   public Api(
      Context var1,
      AppIdentity var2,
      UserIdentity var3,
      OkHttpClient var4,
      MessengerApi var5,
      CallbackHolder var6,
      RateLimiter var7,
      Provider var8,
      OpsMetricTracker var9,
      IntercomDataLayer var10,
      DeviceIdentityStore var11,
      PushTokenStore var12
   ) {
      this.context = var1;
      this.appIdentity = var2;
      this.userIdentity = var3;
      this.messengerApi = var5;
      this.callbacks = var6;
      this.rateLimiter = var7;
      this.appConfigProvider = var8;
      this.opsMetricTracker = var9;
      this.dataLayer = var10;
      this.deviceIdentityStore = var11;
      this.pushTokenStore = var12;
      this.apiHttpClient = var4;
      this.defaultOkHttpMaxRequests = var4.dispatcher().getMaxRequests();
      this.updateMaxRequests();
   }

   public void updateMaxRequests() {
      int var1 = this.userIdentity.hasIntercomId() ? this.defaultOkHttpMaxRequests : 1;
      Dispatcher var2 = this.apiHttpClient.dispatcher();
      if (var2.getMaxRequests() != var1) {
         var2.setMaxRequests(var1);
      }
   }

   public void setDeviceToken(String var1) {
      Map var2 = DeviceData.generateDeviceData(this.context, this.pushTokenStore);
      var2.put("device_token", var1);
      Map var3 = new HashMap();
      var3.put("user", this.userIdentity.toMap());
      var3.put("device_data", var2);
      this.addSecureHash(var3);
      this.messengerApi.setDeviceToken(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var3)).enqueue(new Api$1(this));
   }

   public void removeDeviceToken(String var1, UserIdentity var2) {
      String var3 = var2.getFingerprint();
      Map var4 = new HashMap();
      if (var2.isSoftReset()) {
         var4.put("user", var2.softUserIdentityToMap());
      } else {
         var4.put("user", var2.toMap());
      }

      var4.put("device_token", var1);
      this.addSecureHash(var4);
      this.messengerApi.deleteDeviceToken(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var4)).enqueue(new Api$2(this, var3));
   }

   public void updateUser(UserUpdateRequest var1, IntercomStatusCallback var2) {
      boolean var3 = !var1.isInternalUpdate();
      if (var3) {
         if (this.rateLimiter.isLimited()) {
            this.rateLimiter.logError();
            var2.onFailure(new IntercomError(1005, "rate limit reached"));
            return;
         }

         if (this.shouldStopBackgroundRequest(var1.isSentFromBackground())) {
            this.logBackgroundDisabledError();
            var2.onFailure(new IntercomError(1006, "Background requests are disabled"));
            return;
         }

         this.rateLimiter.recordRequest();
      }

      this.retriableUpdateUser(this.generateUpdateUserParams(var1), var2);
   }

   @VisibleForTesting
   void retriableUpdateUser(Map var1, IntercomStatusCallback var2) {
      this.retriableUpdateUser(var1, this.userIdentity.getFingerprint(), var2);
   }

   private void retriableUpdateUser(Map var1, String var2, IntercomStatusCallback var3) {
      this.messengerApi.updateUser(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var1)).enqueue(new Api$3(this, var3, var2, var1));
   }

   public void setAuthTokens(List var1, IntercomStatusCallback var2) {
      Map var3 = new HashMap();
      var3.put("user", this.userIdentity.toMap());
      this.addSecureHash(var3);
      if (!var1.isEmpty()) {
         var3.put("auth_tokens", AuthTokenKt.toMap(var1));
      }

      this.messengerApi.setAuthToken(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var3)).enqueue(new Api$4(this, var2));
   }

   public void logEvent(String var1, Map var2) {
      if (this.rateLimiter.isLimited()) {
         this.rateLimiter.logError();
      } else {
         boolean var3 = ((HostAppState)this.dataLayer.getHostAppState().getValue()).isBackgrounded();
         if (this.shouldStopBackgroundRequest(var3)) {
            this.logBackgroundDisabledError();
         } else {
            this.rateLimiter.recordRequest();
            Map var4 = new HashMap();
            var4.put("event_name", var1);
            if (!var2.isEmpty()) {
               var4.put("metadata", var2);
            }

            Map var5 = new HashMap();
            var5.put("event", var4);
            var5.put("sent_from_background", var3);
            var5.put("user", this.userIdentity.toMap());
            var5.put("carousel_visible", this.isCarouselVisible());
            this.addSecureHash(var5);
            this.messengerApi.logEvent(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var5)).enqueue(this.callbacks.logEventCallback());
         }
      }
   }

   public void markConversationAsRead(String var1) {
      Map var2 = new HashMap();
      var2.put("app_id", this.appIdentity.appId());
      var2.put("user", this.userIdentity.toMap());
      this.addSecureHash(var2);
      this.messengerApi.markAsRead(var1, MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var2)).enqueue(this.emptyCallback);
   }

   public void markConversationAsDismissed(String var1) {
      Map var2 = new HashMap();
      var2.put("user", this.userIdentity.toMap());
      var2.put("conversation_ids", new String[]{var1});
      this.addSecureHash(var2);
      this.messengerApi.markAsDismissed(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var2)).enqueue(this.emptyCallback);
   }

   public void recordInteractions(String var1, String[] var2) {
      Map var3 = new HashMap();
      var3.put("user", this.userIdentity.toMap());
      var3.put("interactions", var2);
      this.addSecureHash(var3);
      this.messengerApi.recordInteractions(var1, MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var3)).enqueue(this.emptyCallback);
   }

   public void getUnreadConversations() {
      Map var1 = this.userIdentity.toMap();
      var1.put("per_page", "20");
      this.addSecureHash(var1);
      this.messengerApi.getUnreadConversations(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var1)).enqueue(this.callbacks.unreadCallback());
   }

   public void rateConversation(String var1, int var2) {
      this.rateConversation(var1, var2, null);
   }

   @RestrictTo(Scope.LIBRARY)
   public void rateConversation(String var1, int var2, @Nullable RatingCallback var3) {
      Map var4 = new HashMap();
      var4.put("user", this.userIdentity.toMap());
      var4.put("rating_index", var2);
      this.addSecureHash(var4);
      this.messengerApi
         .rateConversation(var1, MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var4))
         .enqueue(this.callbacks.ratingCallback("conversation rating", var3));
   }

   public void addConversationRatingRemark(String var1, String var2) {
      this.addConversationRatingRemark(var1, var2, null);
   }

   @RestrictTo(Scope.LIBRARY)
   public void addConversationRatingRemark(String var1, String var2, @Nullable RatingCallback var3) {
      Map var4 = new HashMap();
      var4.put("user", this.userIdentity.toMap());
      var4.put("remark", var2);
      this.addSecureHash(var4);
      this.messengerApi
         .addConversationRatingRemark(var1, MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var4))
         .enqueue(this.callbacks.ratingCallback("adding remark to conversation", var3));
   }

   public Map createBaseReplyParams() {
      Map var1 = new HashMap();
      var1.put("app_id", this.appIdentity.appId());
      var1.put("type", "user");
      var1.put("message_type", "comment");
      var1.put("user", this.userIdentity.toMap());
      this.addSecureHash(var1);
      return var1;
   }

   public void reactToConversation(String var1, int var2) {
      Map var3 = new HashMap();
      var3.put("user", this.userIdentity.toMap());
      var3.put("reaction_index", var2);
      this.addSecureHash(var3);
      this.messengerApi
         .reactToConversation(var1, MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var3))
         .enqueue(this.callbacks.loggingCallback("add reaction to conversation"));
   }

   public Map baseNewConversationParams() {
      Map var1 = new HashMap();
      var1.put("app_id", this.appIdentity.appId());
      var1.put("user", this.userIdentity.toMap());
      this.addSecureHash(var1);
      return var1;
   }

   public void getLink(String var1, Callback var2) {
      Map var3 = this.userIdentity.toMap();
      this.addSecureHash(var3);
      this.messengerApi.getLink(var1, MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var3)).enqueue(var2);
   }

   public void reactToLink(String var1, @Nullable String var2, int var3, boolean var4) {
      Map var5 = new HashMap();
      var5.put("user", this.userIdentity.toMap());
      var5.put("article_content_id", var2);
      var5.put("reaction_index", var3);
      var5.put("allow_auto_responses", var4);
      this.addSecureHash(var5);
      this.messengerApi
         .reactToLink(var1, MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var5))
         .enqueue(this.callbacks.loggingCallback("add reaction to link"));
   }

   public void sendMetrics(List var1, List var2, Callback var3) {
      Map var4 = new HashMap();
      var4.put("metrics", var1);
      var4.put("op_metrics", var2);
      this.messengerApi.sendMetrics(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var4)).enqueue(var3);
   }

   public void sendErrorReport(ErrorReport var1) {
      Map var2 = new HashMap();
      var2.put("user", this.userIdentity.toMap());
      var2.put("device_data", DeviceData.generateDeviceData(this.context, this.pushTokenStore));
      var2.put("error_report", var1);
      String var3 = var1.getSessionId();
      if (var3 != null) {
         var2.put("session_id", var3);
      }

      this.addSecureHash(var2);
      this.messengerApi.reportError(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var2)).enqueue(this.callbacks.loggingCallback("report error"));
   }

   public void getVideo(String var1, okhttp3.Callback var2) {
      this.httpClient.newCall(new Builder().url(var1).build()).enqueue(var2);
   }

   public void hitTrackingUrl(String var1) {
      this.httpClient.newCall(new Builder().url(var1).build()).enqueue(new Api$5(this));
   }

   public void fetchSheet(HashMap var1, Callback var2) {
      Map var3 = new HashMap();
      var3.put("user", this.userIdentity.toMap());
      this.addSecureHash(var3);
      var3.putAll(var1);
      this.messengerApi.getSheet(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var3)).enqueue(var2);
   }

   public void submitSheet(String var1, Map var2, Callback var3) {
      Map var4 = new HashMap();
      var4.put("user", this.userIdentity.toMap());
      this.addSecureHash(var4);
      var4.put("uri", var1);
      var4.put("sheet_values", var2);
      this.messengerApi.submitSheet(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var4)).enqueue(var3);
   }

   public void markPushAsOpened(String var1) {
      Map var2 = this.statsSystemParams(var1);
      this.messengerApi.markPushAsOpened(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var2)).enqueue(this.emptyCallback);
   }

   public void markCarouselAsDismissed(String var1, boolean var2) {
      Map var3 = this.statsSystemParams(var1);
      this.addCarouselCreatedViaParam(var3, var2);
      this.messengerApi.markCarouselAsDismissed(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var3)).enqueue(this.emptyCallback);
   }

   public void markCarouselAsCompleted(String var1, boolean var2) {
      Map var3 = this.statsSystemParams(var1);
      this.addCarouselCreatedViaParam(var3, var2);
      this.messengerApi.markCarouselAsCompleted(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var3)).enqueue(this.emptyCallback);
   }

   public void markCarouselScreenViewed(String var1, String var2, boolean var3) {
      Map var4 = this.statsSystemParams(var1, var2);
      this.addCarouselCreatedViaParam(var4, var3);
      this.messengerApi.markCarouselScreenViewed(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var4)).enqueue(this.emptyCallback);
   }

   public void markPermissionGranted(String var1, String var2, boolean var3) {
      Map var4 = this.statsSystemParams(var1, var2);
      this.addCarouselCreatedViaParam(var4, var3);
      this.messengerApi.markPermissionGranted(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var4)).enqueue(this.emptyCallback);
   }

   public void markCarouselActionButtonTapped(String var1, String var2, boolean var3) {
      Map var4 = this.statsSystemParams(var1, var2);
      this.addCarouselCreatedViaParam(var4, var3);
      this.messengerApi.markCarouselActionButtonTapped(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var4)).enqueue(this.emptyCallback);
   }

   public void fetchProgrammaticCarousel(String var1, Callback var2) {
      Map var3 = this.userIdentity.toMap();
      this.addSecureHash(var3);
      this.messengerApi.getProgrammaticCarousel(var1, MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var3)).enqueue(var2);
   }

   public void fetchCarouselByEntityId(String var1) {
      Map var2 = this.userIdentity.toMap();
      var2.put("entity_id", var1);
      var2.put("entity_type", 44);
      this.addSecureHash(var2);
      this.messengerApi.getCarousel(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var2)).enqueue(new Api$6(this));
   }

   public void fetchSurveyByEntityId(String var1) {
      Map var2 = this.userIdentity.toMap();
      var2.put("entity_id", var1);
      var2.put("entity_type", 85);
      this.addSecureHash(var2);
      this.messengerApi.getSurvey(MessengerApiHelper.INSTANCE.optionsMapToRequestBody(var2)).enqueue(new Api$7(this));
   }

   protected static boolean isUserNotFound(ErrorObject var0, Map var1) {
      if (var0.hasErrorBody() && var0.getStatusCode() == 404 && var1 != null && var1.get("intercom_id") != null && var1.size() > 1) {
         try {
            JSONObject var2 = new JSONObject(var0.getErrorBody());
            if (var2.getJSONArray("errors").getJSONObject(0).getString("code").equals("not_found")) {
               return true;
            }
         } catch (Exception var3) {
            TWIG.internal("Could not parse error response");
         }
      }

      return false;
   }

   public void addSecureHash(Map var1) {
      var1.put("device_identifier", this.deviceIdentityStore.getDeviceIdentifier());
      String var2 = this.userIdentity.getJwt();
      if (!TextUtils.isEmpty(var2)) {
         var1.put("intercom_user_jwt", var2);
      } else {
         String var3 = this.userIdentity.getHmac();
         if (!TextUtils.isEmpty(var3)) {
            var1.put("hmac", var3);
         }
      }

      String var4 = this.userIdentity.getMessengerEnvironment();
      if (!TextUtils.isEmpty(var4)) {
         var1.put("messenger_environment", var4);
      }
   }

   private Map generateUpdateUserParams(UserUpdateRequest var1) {
      Map var2 = new HashMap();
      var2.put("user", this.userIdentity.toMap());
      var2.put("device_data", DeviceData.generateDeviceData(this.context, this.pushTokenStore));
      var2.put("new_session", var1.isNewSession());
      var2.put("sent_from_background", var1.isSentFromBackground());
      var2.put("user_attributes", var1.getAttributes());
      var2.put("carousel_visible", this.isCarouselVisible());
      var2.put("carousels_enabled", this.isCarouselContentEnabled());
      var2.put("surveys_enabled", this.isSurveyContentEnabled());
      String var3 = this.dataLayer.getSessionId();
      if (var1.isNewSession() && var3 != null) {
         var2.put("session_id", var3);
      }

      this.addSecureHash(var2);
      return var2;
   }

   private void addCarouselCreatedViaParam(Map var1, boolean var2) {
      var1.put("created_via", var2 ? "triggered_from_code" : "shown_automatically");
   }

   private boolean isCarouselVisible() {
      Carousel var1 = ((OverlayState)this.dataLayer.getOverlayState().getValue()).getCarousel();
      return !Carousel.NULL.equals(var1);
   }

   private boolean isCarouselContentEnabled() {
      return ((OverlayState)this.dataLayer.getOverlayState().getValue()).getCarouselEnabled();
   }

   private boolean isSurveyContentEnabled() {
      return ((OverlayState)this.dataLayer.getOverlayState().getValue()).getSurveyEnabled();
   }

   private Map statsSystemParams(String var1, String var2) {
      Map var3 = this.statsSystemParams(var1);
      var3.put("screen_id", var2);
      return var3;
   }

   private Map statsSystemParams(String var1) {
      Map var2 = new HashMap();
      var2.put("instance_id", var1);
      var2.put("user", this.userIdentity.toMap());
      this.addSecureHash(var2);
      return var2;
   }

   private boolean shouldStopBackgroundRequest(boolean var1) {
      return var1 && ((AppConfig)this.appConfigProvider.get()).isBackgroundRequestsDisabled();
   }

   private void logBackgroundDisabledError() {
      TWIG.e("Your request was not sent because the app is in the background. Please contact Intercom to enable background requests.");
   }

   @VisibleForTesting
   boolean isSynchronous() {
      return this.apiHttpClient.dispatcher().getMaxRequests() == 1;
   }

   @VisibleForTesting
   public void setIdleCallback(Runnable var1) {
      this.apiHttpClient.dispatcher().setIdleCallback(var1);
   }

   @VisibleForTesting
   public boolean isIdle() {
      return this.apiHttpClient.dispatcher().runningCallsCount() == 0;
   }

   // $VF: synthetic method
   static PushTokenStore access$000(Api var0) {
      return var0.pushTokenStore;
   }

   // $VF: synthetic method
   static Twig access$100() {
      return TWIG;
   }

   // $VF: synthetic method
   static IntercomDataLayer access$200(Api var0) {
      return var0.dataLayer;
   }

   // $VF: synthetic method
   static OpsMetricTracker access$300(Api var0) {
      return var0.opsMetricTracker;
   }

   // $VF: synthetic method
   static void access$400(Api var0, Map var1, String var2, IntercomStatusCallback var3) {
      var0.retriableUpdateUser(var1, var2, var3);
   }
}

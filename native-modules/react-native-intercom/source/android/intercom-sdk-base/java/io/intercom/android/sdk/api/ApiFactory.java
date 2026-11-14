package io.intercom.android.sdk.api;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.integer;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponseAdapterFactory;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.AppIdentity;
import io.intercom.android.sdk.identity.DeviceIdentityStore;
import io.intercom.android.sdk.identity.PushTokenStore;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.identity.UserJwtRefresher;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.home.data.HomeCards;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeExternalLinkData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeHelpCenterData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeMessengerAppData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeRecentConversationData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeRecentTicketsData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeSpacesData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeTicketLinksData;
import io.intercom.android.sdk.m5.home.data.HomeCards$UnSupported;
import io.intercom.android.sdk.metrics.UnreadConversationsTracker;
import io.intercom.android.sdk.metrics.ops.OpsMetricTracker;
import io.intercom.android.sdk.models.Ticket$TicketAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$DateTimeAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$FilesAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$ListAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$PrimitiveAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$UnSupported;
import io.intercom.android.sdk.tickets.create.data.TicketApi;
import io.intercom.android.sdk.utilities.UtilsKt;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import io.intercom.android.sdk.utilities.gson.RuntimeTypeAdapterFactory;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000À\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003Jd\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u00182\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'J\u000e\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+J\u000e\u0010,\u001a\u00020-2\u0006\u0010*\u001a\u00020+J\u000e\u0010.\u001a\u00020/2\u0006\u0010*\u001a\u00020+J\u000e\u00100\u001a\u00020#2\u0006\u0010*\u001a\u00020+J\u000e\u00101\u001a\u0002022\u0006\u0010*\u001a\u00020+J.\u00103\u001a\u00020!2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u00104\u001a\u0002052\u0006\u00106\u001a\u000207J\u000e\u00108\u001a\u00020!2\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u00109\u001a\u00020:J4\u0010;\u001a\u00020+2\u0006\u0010<\u001a\u00020\u00052\u0006\u0010 \u001a\u00020!2\u0006\u0010=\u001a\u00020:2\u0014\b\u0002\u0010>\u001a\u000e\u0012\u0004\u0012\u00020@\u0012\u0004\u0012\u00020A0?J,\u0010B\u001a\u00020+2\u0006\u0010<\u001a\u00020\u00052\u0006\u0010 \u001a\u00020!2\u0014\b\u0002\u0010>\u001a\u000e\u0012\u0004\u0012\u00020@\u0012\u0004\u0012\u00020A0?J\u0016\u0010C\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010D\u001a\u00020\u00052\u0006\u0010<\u001a\u00020\u0005J\u0016\u0010E\u001a\u00020\u00052\u0006\u0010F\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0012J\u0018\u0010G\u001a\u00020\u00052\u0006\u0010H\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010I\u001a\u00020\u00052\u0006\u0010F\u001a\u00020\u0005H\u0007J\u000e\u0010J\u001a\u00020\u00052\u0006\u0010F\u001a\u00020\u0005J\u000e\u0010K\u001a\b\u0012\u0004\u0012\u00020M0LH\u0002J\u000e\u0010N\u001a\b\u0012\u0004\u0012\u00020O0LH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\fX\u0082T¢\u0006\u0002\n\u0000¨\u0006P",
   d2 = {
         "Lio/intercom/android/sdk/api/ApiFactory;",
         "",
         "<init>",
         "()V",
         "CACHE_NAME",
         "",
         "PROTOCOL",
         "PARTIAL_HOSTNAME_US",
         "PARTIAL_HOSTNAME_AUS",
         "PARTIAL_HOSTNAME_EU",
         "ENDPOINT",
         "CACHE_SIZE",
         "",
         "MAX_DNS_SEGMENT_SIZE",
         "INTERCOM_TRAFFIC_TAG",
         "createApi",
         "Lio/intercom/android/sdk/api/Api;",
         "context",
         "Landroid/content/Context;",
         "appIdentity",
         "Lio/intercom/android/sdk/identity/AppIdentity;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "appConfigProvider",
         "Lio/intercom/android/sdk/Provider;",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "opsMetricTracker",
         "Lio/intercom/android/sdk/metrics/ops/OpsMetricTracker;",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "unreadConversationsTracker",
         "Lio/intercom/android/sdk/metrics/UnreadConversationsTracker;",
         "httpClient",
         "Lokhttp3/OkHttpClient;",
         "messengerApi",
         "Lio/intercom/android/sdk/api/MessengerApi;",
         "deviceIdentityStore",
         "Lio/intercom/android/sdk/identity/DeviceIdentityStore;",
         "pushTokenStore",
         "Lio/intercom/android/sdk/identity/PushTokenStore;",
         "createHelpCenterApi",
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "retrofit",
         "Lretrofit2/Retrofit;",
         "createSurveyApi",
         "Lio/intercom/android/sdk/api/SurveyApi;",
         "createTicketApi",
         "Lio/intercom/android/sdk/tickets/create/data/TicketApi;",
         "createMessengerApi",
         "getUploadApi",
         "Lio/intercom/android/sdk/api/ExternalUploadApi;",
         "createConfigurableHttpClient",
         "shutdownStore",
         "Lio/intercom/android/sdk/api/ShutdownStore;",
         "userJwtRefresher",
         "Lio/intercom/android/sdk/identity/UserJwtRefresher;",
         "createHttpClientWithoutHeaders",
         "createGsonWithAdapters",
         "Lcom/google/gson/Gson;",
         "createLegacyRetrofit",
         "hostname",
         "gson",
         "onClientError",
         "Lkotlin/Function1;",
         "",
         "",
         "createKotlinXRetrofit",
         "getServerUrl",
         "convertHostnameToUrl",
         "getFullHostname",
         "appId",
         "getCorrectServerHostname",
         "region",
         "createUniqueIdentifier",
         "removeInvalidCharacters",
         "getHomeCardsAdapterFactory",
         "Lio/intercom/android/sdk/utilities/gson/RuntimeTypeAdapterFactory;",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "getTicketTypeAdapterFactory",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ApiFactory {
   @NotNull
   public static final ApiFactory INSTANCE = new ApiFactory();
   @NotNull
   private static final String CACHE_NAME = "Intercom_SDK/HttpCache";
   @NotNull
   private static final String PROTOCOL = "https://";
   @NotNull
   private static final String PARTIAL_HOSTNAME_US = ".mobile-messenger.intercom.com";
   @NotNull
   private static final String PARTIAL_HOSTNAME_AUS = ".mobile-messenger.au.intercom.io";
   @NotNull
   private static final String PARTIAL_HOSTNAME_EU = ".mobile-messenger.eu.intercom.io";
   @NotNull
   private static final String ENDPOINT = "/messenger/mobile/";
   private static final int CACHE_SIZE = 10485760;
   private static final int MAX_DNS_SEGMENT_SIZE = 63;
   private static final int INTERCOM_TRAFFIC_TAG = 46837266;
   public static int $stable;

   static {}

   private ApiFactory() {
   }

   @NotNull
   public final Api createApi(
      @NotNull Context var1,
      @NotNull AppIdentity var2,
      @NotNull UserIdentity var3,
      @NotNull Provider var4,
      @NotNull OpsMetricTracker var5,
      @NotNull IntercomDataLayer var6,
      @NotNull UnreadConversationsTracker var7,
      @NotNull OkHttpClient var8,
      @NotNull MessengerApi var9,
      @NotNull DeviceIdentityStore var10,
      @NotNull PushTokenStore var11
   ) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "appIdentity");
      Intrinsics.checkNotNullParameter(var3, "userIdentity");
      Intrinsics.checkNotNullParameter(var4, "appConfigProvider");
      Intrinsics.checkNotNullParameter(var5, "opsMetricTracker");
      Intrinsics.checkNotNullParameter(var6, "dataLayer");
      Intrinsics.checkNotNullParameter(var7, "unreadConversationsTracker");
      Intrinsics.checkNotNullParameter(var8, "httpClient");
      Intrinsics.checkNotNullParameter(var9, "messengerApi");
      Intrinsics.checkNotNullParameter(var10, "deviceIdentityStore");
      Intrinsics.checkNotNullParameter(var11, "pushTokenStore");
      CallbackHolder var12 = new CallbackHolder(var6, var3, var7);
      RateLimiter var13 = new RateLimiter((AppConfig)var4.get());
      return new Api(var1, var2, var3, var8, var9, var12, var13, var4, var5, var6, var10, var11);
   }

   @NotNull
   public final HelpCenterApi createHelpCenterApi(@NotNull Retrofit var1) {
      Intrinsics.checkNotNullParameter(var1, "retrofit");
      Object var10000 = var1.create(HelpCenterApi.class);
      Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
      return (HelpCenterApi)var10000;
   }

   @NotNull
   public final SurveyApi createSurveyApi(@NotNull Retrofit var1) {
      Intrinsics.checkNotNullParameter(var1, "retrofit");
      Object var10000 = var1.create(SurveyApi.class);
      Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
      return (SurveyApi)var10000;
   }

   @NotNull
   public final TicketApi createTicketApi(@NotNull Retrofit var1) {
      Intrinsics.checkNotNullParameter(var1, "retrofit");
      Object var10000 = var1.create(TicketApi.class);
      Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
      return (TicketApi)var10000;
   }

   @NotNull
   public final MessengerApi createMessengerApi(@NotNull Retrofit var1) {
      Intrinsics.checkNotNullParameter(var1, "retrofit");
      Object var10000 = var1.create(MessengerApi.class);
      Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
      return (MessengerApi)var10000;
   }

   @NotNull
   public final ExternalUploadApi getUploadApi(@NotNull Retrofit var1) {
      Intrinsics.checkNotNullParameter(var1, "retrofit");
      Object var10000 = var1.create(ExternalUploadApi.class);
      Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
      return (ExternalUploadApi)var10000;
   }

   @NotNull
   public final OkHttpClient createConfigurableHttpClient(
      @NotNull Context var1, @NotNull AppIdentity var2, @NotNull UserIdentity var3, @NotNull ShutdownStore var4, @NotNull UserJwtRefresher var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "appIdentity");
      Intrinsics.checkNotNullParameter(var3, "userIdentity");
      Intrinsics.checkNotNullParameter(var4, "shutdownStore");
      Intrinsics.checkNotNullParameter(var5, "userJwtRefresher");
      Builder var10000 = new Builder()
         .readTimeout(2L, TimeUnit.MINUTES)
         .connectTimeout(2L, TimeUnit.MINUTES)
         .writeTimeout(2L, TimeUnit.MINUTES)
         .socketFactory(new TaggingSocketFactory(SocketFactory.getDefault(), 46837266))
         .addInterceptor(new UserIdentityInterceptor(var3))
         .addInterceptor(new UserJwtExpiryInterceptor(var5, null, 2, null))
         .addInterceptor(new RetryInterceptor(new RetryInterceptor$Sleeper()))
         .addInterceptor(new ShutdownInterceptor(new ShutdownState(var4, var1, var2, TimeProvider.SYSTEM)));
      HeaderInterceptor var10001 = HeaderInterceptor.create(var1, var2);
      Intrinsics.checkNotNullExpressionValue(var10001, "create(...)");
      Builder var6 = var10000.addNetworkInterceptor(var10001);
      File var7 = var1.getCacheDir();
      if (var7 != null) {
         File var8 = new File(var7.getAbsolutePath(), "Intercom_SDK/HttpCache");
         var6.cache(new Cache(var8, 10485760L));
      }

      return var6.build();
   }

   @NotNull
   public final OkHttpClient createHttpClientWithoutHeaders(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Builder var2 = new Builder();
      var2.addInterceptor(new S3HostRedirectInterceptor());
      return var2.build();
   }

   @NotNull
   public final Gson createGsonWithAdapters() {
      Gson var10000 = new GsonBuilder()
         .registerTypeAdapterFactory(this.getHomeCardsAdapterFactory())
         .registerTypeAdapterFactory(this.getTicketTypeAdapterFactory())
         .create();
      Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
      return var10000;
   }

   @NotNull
   public final Retrofit createLegacyRetrofit(@NotNull String var1, @NotNull OkHttpClient var2, @NotNull Gson var3, @NotNull Function1 var4) {
      Intrinsics.checkNotNullParameter(var1, "hostname");
      Intrinsics.checkNotNullParameter(var2, "httpClient");
      Intrinsics.checkNotNullParameter(var3, "gson");
      Intrinsics.checkNotNullParameter(var4, "onClientError");
      Retrofit var10000 = new retrofit2.Retrofit.Builder()
         .baseUrl(var1)
         .addCallAdapterFactory(new NetworkResponseAdapterFactory(var4))
         .addConverterFactory(GsonConverterFactory.create(var3))
         .client(var2)
         .build();
      Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
      return var10000;
   }

   // $VF: synthetic method
   public static Retrofit createLegacyRetrofit$default(ApiFactory var0, String var1, OkHttpClient var2, Gson var3, Function1 var4, int var5, Object var6) {
      if ((var5 & 8) != 0) {
         var4 = ((kotlin.jvm.functions.Function1) (recoveredArgument_46_0) -> ApiFactory.createLegacyRetrofit$lambda$0((java.lang.Throwable)recoveredArgument_46_0));
      }

      return var0.createLegacyRetrofit(var1, var2, var3, var4);
   }

   @NotNull
   public final Retrofit createKotlinXRetrofit(@NotNull String var1, @NotNull OkHttpClient var2, @NotNull Function1 var3) {
      Intrinsics.checkNotNullParameter(var1, "hostname");
      Intrinsics.checkNotNullParameter(var2, "httpClient");
      Intrinsics.checkNotNullParameter(var3, "onClientError");
      Retrofit var10000 = new retrofit2.Retrofit.Builder()
         .baseUrl(var1)
         .addCallAdapterFactory(new NetworkResponseAdapterFactory(var3))
         .addConverterFactory(KotlinXConvertorFactory.INSTANCE.getConvertorFactory())
         .client(var2)
         .build();
      Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
      return var10000;
   }

   // $VF: synthetic method
   public static Retrofit createKotlinXRetrofit$default(ApiFactory var0, String var1, OkHttpClient var2, Function1 var3, int var4, Object var5) {
      if ((var4 & 4) != 0) {
         var3 = ((kotlin.jvm.functions.Function1) (recoveredArgument_45_0) -> ApiFactory.createKotlinXRetrofit$lambda$1((java.lang.Throwable)recoveredArgument_45_0));
      }

      return var0.createKotlinXRetrofit(var1, var2, var3);
   }

   @NotNull
   public final String getServerUrl(@NotNull AppIdentity var1, @NotNull Context var2) {
      Intrinsics.checkNotNullParameter(var1, "appIdentity");
      Intrinsics.checkNotNullParameter(var2, "context");
      String var3 = UtilsKt.readHostFromManifest(var2);
      String var10000;
      if (TextUtils.isEmpty(var3)) {
         String var10001 = var1.appId();
         Intrinsics.checkNotNullExpressionValue(var10001, "appId(...)");
         var10000 = this.getFullHostname(var10001, var2);
      } else {
         StringBuilder var5 = new StringBuilder();
         String var10002 = var1.appId();
         Intrinsics.checkNotNullExpressionValue(var10002, "appId(...)");
         var10000 = var5.append(this.createUniqueIdentifier(var10002)).append(var3).toString();
      }

      String var4 = var10000;
      return this.convertHostnameToUrl(var4);
   }

   @NotNull
   public final String convertHostnameToUrl(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "hostname");
      return "https://" + var1 + "/messenger/mobile/";
   }

   @NotNull
   public final String getFullHostname(@NotNull String var1, @NotNull Context var2) {
      Intrinsics.checkNotNullParameter(var1, "appId");
      Intrinsics.checkNotNullParameter(var2, "context");
      return this.createUniqueIdentifier(var1) + this.getCorrectServerHostname(UtilsKt.getServerRegionFromManifest(var2), var2);
   }

   private final String getCorrectServerHostname(int var1, Context var2) {
      int var3 = var1;
      String var10000;
      if (var3 == UtilsKt.getRegionCode(var2, integer.intercom_server_region_aus)) {
         var10000 = ".mobile-messenger.au.intercom.io";
      } else if (var3 == UtilsKt.getRegionCode(var2, integer.intercom_server_region_eu)) {
         var10000 = ".mobile-messenger.eu.intercom.io";
      } else if (var3 != UtilsKt.getRegionCode(var2, integer.intercom_server_region_us) && var3 != 0) {
         LumberMill.getLogger()
            .e(
               "Incorrect value for region is provided in AndroidManifest.xml file. Please use one of the available regions values from provided list. For more info on regions, please visit Intercom Android SDK documentation"
            );
         var10000 = "";
      } else {
         LumberMill.getLogger()
            .i("Defaulting to US region, since no explicit region was mentioned. For more info on regions, please visit Intercom Android SDK documentation");
         var10000 = ".mobile-messenger.intercom.com";
      }

      return var10000;
   }

   @VisibleForTesting
   @NotNull
   public final String createUniqueIdentifier(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "appId");
      String var2 = this.removeInvalidCharacters(var1) + "-android";
      if (var2.length() > 63) {
         int var3 = 63;
         if (var2.charAt(62) == '-') {
            var3--;
         }

         String var10000 = var2.substring(0, var3);
         Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
         var2 = var10000;
      }

      return var2;
   }

   @NotNull
   public final String removeInvalidCharacters(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "appId");
      CharSequence var2 = var1;
      Regex var3 = new Regex("[^A-Za-z0-9\\-$]");
      String var4 = "";
      return var3.replace(var2, var4);
   }

   private final RuntimeTypeAdapterFactory getHomeCardsAdapterFactory() {
      RuntimeTypeAdapterFactory var10000 = RuntimeTypeAdapterFactory.of(HomeCards.class, "type");
      String var10002 = "SPACES".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(HomeCards$HomeSpacesData.class, var10002);
      var10002 = "NEW_CONVERSATION".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(HomeCards$HomeNewConversationData.class, var10002);
      var10002 = "RECENT_CONVERSATION".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(HomeCards$HomeRecentConversationData.class, var10002);
      var10002 = "HELP_CENTER".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(HomeCards$HomeHelpCenterData.class, var10002);
      var10002 = "EXTERNAL_LINKS".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(HomeCards$HomeExternalLinkData.class, var10002);
      var10002 = "MESSENGER_APP".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(HomeCards$HomeMessengerAppData.class, var10002);
      var10002 = "RECENT_TICKETS".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(HomeCards$HomeRecentTicketsData.class, var10002);
      var10002 = "TICKET_LINKS".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(HomeCards$HomeTicketLinksData.class, var10002).registerIgnoredSubtype(HomeCards$UnSupported.class, "UnSupported");
      Intrinsics.checkNotNullExpressionValue(var10000, "registerIgnoredSubtype(...)");
      return var10000;
   }

   private final RuntimeTypeAdapterFactory getTicketTypeAdapterFactory() {
      RuntimeTypeAdapterFactory var10000 = RuntimeTypeAdapterFactory.of(Ticket$TicketAttribute.class, "type");
      String var10002 = "STRING".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(Ticket$TicketAttribute$PrimitiveAttribute.class, var10002);
      var10002 = "INTEGER".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(Ticket$TicketAttribute$PrimitiveAttribute.class, var10002);
      var10002 = "FLOAT".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(Ticket$TicketAttribute$PrimitiveAttribute.class, var10002);
      var10002 = "BOOLEAN".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(Ticket$TicketAttribute$PrimitiveAttribute.class, var10002);
      var10002 = "LIST".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(Ticket$TicketAttribute$ListAttribute.class, var10002);
      var10002 = "DATETIME".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(Ticket$TicketAttribute$DateTimeAttribute.class, var10002);
      var10002 = "FILES".toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10002, "toLowerCase(...)");
      var10000 = var10000.registerSubtype(Ticket$TicketAttribute$FilesAttribute.class, var10002)
         .registerIgnoredSubtype(Ticket$TicketAttribute$UnSupported.class, "UnSupported");
      Intrinsics.checkNotNullExpressionValue(var10000, "registerIgnoredSubtype(...)");
      return var10000;
   }

   private static final Unit createLegacyRetrofit$lambda$0(Throwable var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit createKotlinXRetrofit$lambda$1(Throwable var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }
}

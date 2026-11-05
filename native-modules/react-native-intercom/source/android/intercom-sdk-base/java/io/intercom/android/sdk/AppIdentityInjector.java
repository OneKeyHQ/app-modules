package io.intercom.android.sdk;

import android.app.Application;
import android.content.Context;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.Gson;
import com.intercom.twig.Twig;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.api.ApiFactory;
import io.intercom.android.sdk.api.ExternalUploadApi;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.api.ShutdownStore;
import io.intercom.android.sdk.api.SurveyApi;
import io.intercom.android.sdk.api.TaggingSocketFactory;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.AppIdentity;
import io.intercom.android.sdk.identity.AppIdentityStore;
import io.intercom.android.sdk.identity.DeviceIdentityStore;
import io.intercom.android.sdk.identity.PushTokenStore;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.identity.UserJwtRefresher;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.UnreadConversationsTracker;
import io.intercom.android.sdk.metrics.ops.OpsMetricTracker;
import io.intercom.android.sdk.tickets.create.data.TicketApi;
import io.intercom.android.sdk.utilities.CoroutineExceptionHandlersKt;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.SupervisorKt;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Retrofit;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000¨\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\b\u0001\u0018\u0000 d2\u00020\u0001:\u0001dB3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u000fR\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\u00020\u00128BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u001b\u0010\u0015\u001a\u00020\u00168FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u0017\u0010\u0018R\u001b\u0010\u001b\u001a\u00020\u001c8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001f\u0010\u001a\u001a\u0004\b\u001d\u0010\u001eR\u001b\u0010 \u001a\u00020\u001c8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\"\u0010\u001a\u001a\u0004\b!\u0010\u001eR\u001b\u0010#\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b&\u0010\u001a\u001a\u0004\b$\u0010%R\u001b\u0010'\u001a\u00020(8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b+\u0010\u001a\u001a\u0004\b)\u0010*R\u001a\u0010,\u001a\u000e\u0012\u0004\u0012\u00020.\u0012\u0004\u0012\u00020\u000f0-X\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010/\u001a\u0002008FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b3\u0010\u001a\u001a\u0004\b1\u00102R\u001b\u00104\u001a\u0002008FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b6\u0010\u001a\u001a\u0004\b5\u00102R\u001b\u00107\u001a\u0002008FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b9\u0010\u001a\u001a\u0004\b8\u00102R\u001b\u0010:\u001a\u00020;8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b>\u0010\u001a\u001a\u0004\b<\u0010=R\u001b\u0010?\u001a\u00020@8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bC\u0010\u001a\u001a\u0004\bA\u0010BR\u001b\u0010D\u001a\u00020E8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bH\u0010\u001a\u001a\u0004\bF\u0010GR\u001b\u0010I\u001a\u00020J8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bM\u0010\u001a\u001a\u0004\bK\u0010LR\u001b\u0010N\u001a\u00020O8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bR\u0010\u001a\u001a\u0004\bP\u0010QR\u001b\u0010S\u001a\u00020T8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bW\u0010\u001a\u001a\u0004\bU\u0010VR\u001b\u0010X\u001a\u00020Y8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\\\u0010\u001a\u001a\u0004\bZ\u0010[R\u001b\u0010]\u001a\u00020^8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\ba\u0010\u001a\u001a\u0004\b_\u0010`R\u000e\u0010b\u001a\u00020cX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006e",
   d2 = {
         "Lio/intercom/android/sdk/AppIdentityInjector;",
         "",
         "application",
         "Landroid/app/Application;",
         "apiKey",
         "",
         "appId",
         "customHostname",
         "appIdentityStore",
         "Lio/intercom/android/sdk/identity/AppIdentityStore;",
         "<init>",
         "(Landroid/app/Application;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/identity/AppIdentityStore;)V",
         "appIdentityScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "initializeEvents",
         "",
         "destroy",
         "injector",
         "Lio/intercom/android/sdk/Injector;",
         "getInjector",
         "()Lio/intercom/android/sdk/Injector;",
         "appIdentity",
         "Lio/intercom/android/sdk/identity/AppIdentity;",
         "getAppIdentity",
         "()Lio/intercom/android/sdk/identity/AppIdentity;",
         "appIdentity$delegate",
         "Lkotlin/Lazy;",
         "configurableHttpClient",
         "Lokhttp3/OkHttpClient;",
         "getConfigurableHttpClient",
         "()Lokhttp3/OkHttpClient;",
         "configurableHttpClient$delegate",
         "httpClientWithoutHeaders",
         "getHttpClientWithoutHeaders",
         "httpClientWithoutHeaders$delegate",
         "hostname",
         "getHostname",
         "()Ljava/lang/String;",
         "hostname$delegate",
         "gsonWithAdapters",
         "Lcom/google/gson/Gson;",
         "getGsonWithAdapters",
         "()Lcom/google/gson/Gson;",
         "gsonWithAdapters$delegate",
         "onClientError",
         "Lkotlin/Function1;",
         "",
         "kotlinXRetrofit",
         "Lretrofit2/Retrofit;",
         "getKotlinXRetrofit",
         "()Lretrofit2/Retrofit;",
         "kotlinXRetrofit$delegate",
         "legacyRetrofit",
         "getLegacyRetrofit",
         "legacyRetrofit$delegate",
         "retrofitWithoutHeaders",
         "getRetrofitWithoutHeaders",
         "retrofitWithoutHeaders$delegate",
         "api",
         "Lio/intercom/android/sdk/api/Api;",
         "getApi",
         "()Lio/intercom/android/sdk/api/Api;",
         "api$delegate",
         "helpCenterApi",
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "getHelpCenterApi",
         "()Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "helpCenterApi$delegate",
         "surveyApi",
         "Lio/intercom/android/sdk/api/SurveyApi;",
         "getSurveyApi",
         "()Lio/intercom/android/sdk/api/SurveyApi;",
         "surveyApi$delegate",
         "ticketApi",
         "Lio/intercom/android/sdk/tickets/create/data/TicketApi;",
         "getTicketApi",
         "()Lio/intercom/android/sdk/tickets/create/data/TicketApi;",
         "ticketApi$delegate",
         "externalUploadApi",
         "Lio/intercom/android/sdk/api/ExternalUploadApi;",
         "getExternalUploadApi",
         "()Lio/intercom/android/sdk/api/ExternalUploadApi;",
         "externalUploadApi$delegate",
         "messengerApi",
         "Lio/intercom/android/sdk/api/MessengerApi;",
         "getMessengerApi",
         "()Lio/intercom/android/sdk/api/MessengerApi;",
         "messengerApi$delegate",
         "nexusClient",
         "Lio/intercom/android/nexus/NexusClient;",
         "getNexusClient",
         "()Lio/intercom/android/nexus/NexusClient;",
         "nexusClient$delegate",
         "ablyManager",
         "Lio/intercom/android/sdk/AblyManager;",
         "getAblyManager",
         "()Lio/intercom/android/sdk/AblyManager;",
         "ablyManager$delegate",
         "nexusDebouncePeriod",
         "",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AppIdentityInjector {
   @NotNull
   public static final AppIdentityInjector$Companion Companion = new AppIdentityInjector$Companion(null);
   @NotNull
   private final CoroutineScope appIdentityScope;
   @NotNull
   private final Lazy appIdentity$delegate;
   @NotNull
   private final Lazy configurableHttpClient$delegate;
   @NotNull
   private final Lazy httpClientWithoutHeaders$delegate;
   @NotNull
   private final Lazy hostname$delegate;
   @NotNull
   private final Lazy gsonWithAdapters$delegate;
   @NotNull
   private final Function1 onClientError;
   @NotNull
   private final Lazy kotlinXRetrofit$delegate;
   @NotNull
   private final Lazy legacyRetrofit$delegate;
   @NotNull
   private final Lazy retrofitWithoutHeaders$delegate;
   @NotNull
   private final Lazy api$delegate;
   @NotNull
   private final Lazy helpCenterApi$delegate;
   @NotNull
   private final Lazy surveyApi$delegate;
   @NotNull
   private final Lazy ticketApi$delegate;
   @NotNull
   private final Lazy externalUploadApi$delegate;
   @NotNull
   private final Lazy messengerApi$delegate;
   @NotNull
   private final Lazy nexusClient$delegate;
   @NotNull
   private final Lazy ablyManager$delegate;
   private final long nexusDebouncePeriod;
   public static final int $stable;

   static { $stable = 8; }
   private static final int NEXUS_TRAFFIC_TAG = 63987;

   public AppIdentityInjector(@NotNull Application var1, @NotNull String var2, @NotNull String var3, @Nullable String var4, @NotNull AppIdentityStore var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "application");
      Intrinsics.checkNotNullParameter(var2, "apiKey");
      Intrinsics.checkNotNullParameter(var3, "appId");
      Intrinsics.checkNotNullParameter(var5, "appIdentityStore");
      var5.saveAppCredentials(var2, var3);
      this.appIdentityScope = CoroutineScopeKt.CoroutineScope(
         SupervisorKt.SupervisorJob$default(null, 1, null)
            .plus(CoroutineExceptionHandlersKt.loggingCoroutineExceptionHandler("AppIdentityInjector.appIdentityScope"))
      );
      final java.lang.String recoveredExpressionCapture_18_0 = var2;
      final java.lang.String recoveredExpressionCapture_18_1 = var3;
      this.appIdentity$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.appIdentity_delegate$lambda$0(recoveredExpressionCapture_18_0, recoveredExpressionCapture_18_1)));
      final android.app.Application recoveredExpressionCapture_19_0 = var1;
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_19_1 = this;
      this.configurableHttpClient$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.configurableHttpClient_delegate$lambda$1(recoveredExpressionCapture_19_0, recoveredExpressionCapture_19_1)));
      final android.app.Application recoveredExpressionCapture_20_0 = var1;
      this.httpClientWithoutHeaders$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.httpClientWithoutHeaders_delegate$lambda$2(recoveredExpressionCapture_20_0)));
      final java.lang.String recoveredExpressionCapture_21_0 = var4;
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_21_1 = this;
      final android.app.Application recoveredExpressionCapture_21_2 = var1;
      this.hostname$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.hostname_delegate$lambda$3(recoveredExpressionCapture_21_0, recoveredExpressionCapture_21_1, recoveredExpressionCapture_21_2)));
      this.gsonWithAdapters$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> AppIdentityInjector.gsonWithAdapters_delegate$lambda$4()));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_23_0 = this;
      this.onClientError = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_23_0) -> io.intercom.android.sdk.AppIdentityInjector.onClientError$lambda$5(recoveredExpressionCapture_23_0, (java.lang.Throwable) ((java.lang.Throwable)recoveredExpressionArgument_23_0)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_24_0 = this;
      this.kotlinXRetrofit$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.kotlinXRetrofit_delegate$lambda$6(recoveredExpressionCapture_24_0)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_8_0 = this;
      this.legacyRetrofit$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.legacyRetrofit_delegate$lambda$7(recoveredExpressionCapture_8_0)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_9_0 = this;
      this.retrofitWithoutHeaders$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.retrofitWithoutHeaders_delegate$lambda$8(recoveredExpressionCapture_9_0)));
      final android.app.Application recoveredExpressionCapture_10_0 = var1;
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_10_1 = this;
      this.api$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.api_delegate$lambda$9(recoveredExpressionCapture_10_0, recoveredExpressionCapture_10_1)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_11_0 = this;
      this.helpCenterApi$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.helpCenterApi_delegate$lambda$10(recoveredExpressionCapture_11_0)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_12_0 = this;
      this.surveyApi$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.surveyApi_delegate$lambda$11(recoveredExpressionCapture_12_0)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_13_0 = this;
      this.ticketApi$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.ticketApi_delegate$lambda$12(recoveredExpressionCapture_13_0)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_14_0 = this;
      this.externalUploadApi$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.externalUploadApi_delegate$lambda$13(recoveredExpressionCapture_14_0)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_15_0 = this;
      this.messengerApi$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.messengerApi_delegate$lambda$14(recoveredExpressionCapture_15_0)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_16_0 = this;
      this.nexusClient$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.nexusClient_delegate$lambda$15(recoveredExpressionCapture_16_0)));
      final io.intercom.android.sdk.AppIdentityInjector recoveredExpressionCapture_17_0 = this;
      this.ablyManager$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AppIdentityInjector.ablyManager_delegate$lambda$16(recoveredExpressionCapture_17_0)));
      this.nexusDebouncePeriod = TimeUnit.SECONDS.toMillis(1L);
   }

   // $VF: synthetic method
   public AppIdentityInjector(Application var1, String var2, String var3, String var4, AppIdentityStore var5, int var6, DefaultConstructorMarker var7) {
      this(var1, var2, var3, (((var6 & 8) != 0) ? (String) (null) : var4), var5);
   }

   public final void initializeEvents() {
      BuildersKt.launch$default(this.appIdentityScope, Dispatchers.getIO(), null, new AppIdentityInjector$initializeEvents$1(this, null), 2, null);
      BuildersKt.launch$default(this.appIdentityScope, null, null, new AppIdentityInjector$initializeEvents$2(this, null), 3, null);
      BuildersKt.launch$default(this.appIdentityScope, null, null, new AppIdentityInjector$initializeEvents$3(this, null), 3, null);
   }

   public final void destroy() {
      CoroutineScopeKt.cancel$default(this.appIdentityScope, null, 1, null);
   }

   private final Injector getInjector() {
      Injector var10000 = Injector.get();
      Intrinsics.checkNotNullExpressionValue(var10000, "get(...)");
      return var10000;
   }

   @NotNull
   public final AppIdentity getAppIdentity() {
      Lazy var1 = this.appIdentity$delegate;
      Object var10000 = var1.getValue();
      Intrinsics.checkNotNullExpressionValue(var10000, "getValue(...)");
      return (AppIdentity)var10000;
   }

   @NotNull
   public final OkHttpClient getConfigurableHttpClient() {
      Lazy var1 = this.configurableHttpClient$delegate;
      return (OkHttpClient)var1.getValue();
   }

   @NotNull
   public final OkHttpClient getHttpClientWithoutHeaders() {
      Lazy var1 = this.httpClientWithoutHeaders$delegate;
      return (OkHttpClient)var1.getValue();
   }

   @NotNull
   public final String getHostname() {
      Lazy var1 = this.hostname$delegate;
      return (String)var1.getValue();
   }

   @NotNull
   public final Gson getGsonWithAdapters() {
      Lazy var1 = this.gsonWithAdapters$delegate;
      return (Gson)var1.getValue();
   }

   @NotNull
   public final Retrofit getKotlinXRetrofit() {
      Lazy var1 = this.kotlinXRetrofit$delegate;
      return (Retrofit)var1.getValue();
   }

   @NotNull
   public final Retrofit getLegacyRetrofit() {
      Lazy var1 = this.legacyRetrofit$delegate;
      return (Retrofit)var1.getValue();
   }

   @NotNull
   public final Retrofit getRetrofitWithoutHeaders() {
      Lazy var1 = this.retrofitWithoutHeaders$delegate;
      return (Retrofit)var1.getValue();
   }

   @NotNull
   public final Api getApi() {
      Lazy var1 = this.api$delegate;
      return (Api)var1.getValue();
   }

   @NotNull
   public final HelpCenterApi getHelpCenterApi() {
      Lazy var1 = this.helpCenterApi$delegate;
      return (HelpCenterApi)var1.getValue();
   }

   @NotNull
   public final SurveyApi getSurveyApi() {
      Lazy var1 = this.surveyApi$delegate;
      return (SurveyApi)var1.getValue();
   }

   @NotNull
   public final TicketApi getTicketApi() {
      Lazy var1 = this.ticketApi$delegate;
      return (TicketApi)var1.getValue();
   }

   @NotNull
   public final ExternalUploadApi getExternalUploadApi() {
      Lazy var1 = this.externalUploadApi$delegate;
      return (ExternalUploadApi)var1.getValue();
   }

   @NotNull
   public final MessengerApi getMessengerApi() {
      Lazy var1 = this.messengerApi$delegate;
      return (MessengerApi)var1.getValue();
   }

   @NotNull
   public final NexusClient getNexusClient() {
      Lazy var1 = this.nexusClient$delegate;
      return (NexusClient)var1.getValue();
   }

   @NotNull
   public final AblyManager getAblyManager() {
      Lazy var1 = this.ablyManager$delegate;
      return (AblyManager)var1.getValue();
   }

   private static final AppIdentity appIdentity_delegate$lambda$0(String var0, String var1) {
      return AppIdentity.create(var0, var1);
   }

   private static final OkHttpClient configurableHttpClient_delegate$lambda$1(Application var0, AppIdentityInjector var1) {
      ApiFactory var10000 = ApiFactory.INSTANCE;
      Context var10001 = var0;
      AppIdentity var10002 = var1.getAppIdentity();
      UserIdentity var10003 = var1.getInjector().getUserIdentity();
      Intrinsics.checkNotNullExpressionValue(var10003, "getUserIdentity(...)");
      ShutdownStore var10004 = var1.getInjector().getShutdownStore();
      Intrinsics.checkNotNullExpressionValue(var10004, "getShutdownStore(...)");
      UserJwtRefresher var10005 = var1.getInjector().getUserJwtRefresher();
      Intrinsics.checkNotNullExpressionValue(var10005, "getUserJwtRefresher(...)");
      return var10000.createConfigurableHttpClient(var10001, var10002, var10003, var10004, var10005);
   }

   private static final OkHttpClient httpClientWithoutHeaders_delegate$lambda$2(Application var0) {
      return ApiFactory.INSTANCE.createHttpClientWithoutHeaders(var0);
   }

   private static final String hostname_delegate$lambda$3(String var0, AppIdentityInjector var1, Application var2) {
      String var10000 = var0;
      if (var0 == null) {
         var10000 = ApiFactory.INSTANCE.getServerUrl(var1.getAppIdentity(), var2);
      }

      return var10000;
   }

   private static final Gson gsonWithAdapters_delegate$lambda$4() {
      return ApiFactory.INSTANCE.createGsonWithAdapters();
   }

   private static final Unit onClientError$lambda$5(AppIdentityInjector var0, Throwable var1) {
      Intrinsics.checkNotNullParameter(var1, "throwable");
      if (((AppConfig)var0.getInjector().getAppConfigProvider().get()).isCrashReportingEnabled()) {
         var0.getInjector().getErrorReporter().reportHandledException(var1);
      }

      return Unit.INSTANCE;
   }

   private static final Retrofit kotlinXRetrofit_delegate$lambda$6(AppIdentityInjector var0) {
      return ApiFactory.INSTANCE.createKotlinXRetrofit(var0.getHostname(), var0.getConfigurableHttpClient(), var0.onClientError);
   }

   private static final Retrofit legacyRetrofit_delegate$lambda$7(AppIdentityInjector var0) {
      return ApiFactory.INSTANCE.createLegacyRetrofit(var0.getHostname(), var0.getConfigurableHttpClient(), var0.getGsonWithAdapters(), var0.onClientError);
   }

   private static final Retrofit retrofitWithoutHeaders_delegate$lambda$8(AppIdentityInjector var0) {
      ApiFactory var10000 = ApiFactory.INSTANCE;
      String var10001 = var0.getHostname();
      OkHttpClient var10002 = var0.getHttpClientWithoutHeaders();
      Gson var10003 = var0.getInjector().getGson();
      Intrinsics.checkNotNullExpressionValue(var10003, "getGson(...)");
      return var10000.createLegacyRetrofit(var10001, var10002, var10003, var0.onClientError);
   }

   private static final Api api_delegate$lambda$9(Application var0, AppIdentityInjector var1) {
      ApiFactory var10000 = ApiFactory.INSTANCE;
      Context var10001 = var0;
      AppIdentity var10002 = var1.getAppIdentity();
      UserIdentity var10003 = var1.getInjector().getUserIdentity();
      Intrinsics.checkNotNullExpressionValue(var10003, "getUserIdentity(...)");
      Provider var10004 = var1.getInjector().getAppConfigProvider();
      Intrinsics.checkNotNullExpressionValue(var10004, "getAppConfigProvider(...)");
      OpsMetricTracker var10005 = var1.getInjector().getOpsMetricTracker();
      Intrinsics.checkNotNullExpressionValue(var10005, "getOpsMetricTracker(...)");
      IntercomDataLayer var10006 = var1.getInjector().getDataLayer();
      Intrinsics.checkNotNullExpressionValue(var10006, "getDataLayer(...)");
      UnreadConversationsTracker var10007 = var1.getInjector().getUnreadConversationsTracker();
      Intrinsics.checkNotNullExpressionValue(var10007, "getUnreadConversationsTracker(...)");
      OkHttpClient var10008 = var1.getConfigurableHttpClient();
      MessengerApi var10009 = var1.getMessengerApi();
      DeviceIdentityStore var10010 = var1.getInjector().getDeviceIdentityStore();
      Intrinsics.checkNotNullExpressionValue(var10010, "getDeviceIdentityStore(...)");
      PushTokenStore var10011 = var1.getInjector().getPushTokenStore();
      Intrinsics.checkNotNullExpressionValue(var10011, "getPushTokenStore(...)");
      Api var2 = var10000.createApi(var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008, var10009, var10010, var10011);
      var2.updateMaxRequests();
      return var2;
   }

   private static final HelpCenterApi helpCenterApi_delegate$lambda$10(AppIdentityInjector var0) {
      return ApiFactory.INSTANCE.createHelpCenterApi(var0.getKotlinXRetrofit());
   }

   private static final SurveyApi surveyApi_delegate$lambda$11(AppIdentityInjector var0) {
      return ApiFactory.INSTANCE.createSurveyApi(var0.getLegacyRetrofit());
   }

   private static final TicketApi ticketApi_delegate$lambda$12(AppIdentityInjector var0) {
      return ApiFactory.INSTANCE.createTicketApi(var0.getLegacyRetrofit());
   }

   private static final ExternalUploadApi externalUploadApi_delegate$lambda$13(AppIdentityInjector var0) {
      return ApiFactory.INSTANCE.getUploadApi(var0.getRetrofitWithoutHeaders());
   }

   private static final MessengerApi messengerApi_delegate$lambda$14(AppIdentityInjector var0) {
      return ApiFactory.INSTANCE.createMessengerApi(var0.getLegacyRetrofit());
   }

   private static final NexusWrapper nexusClient_delegate$lambda$15(AppIdentityInjector var0) {
      OkHttpClient var1 = NexusClient.defaultOkHttpClientBuilder().socketFactory(new TaggingSocketFactory(SocketFactory.getDefault(), 63987)).build();
      Twig var10002 = LumberMill.getNexusTwig();
      Intrinsics.checkNotNullExpressionValue(var10002, "getNexusTwig(...)");
      Api var10004 = var0.getApi();
      long var10005 = var0.nexusDebouncePeriod;
      IntercomDataLayer var10006 = var0.getInjector().getDataLayer();
      Intrinsics.checkNotNullExpressionValue(var10006, "getDataLayer(...)");
      return new NexusWrapper(var10002, var1, var10004, var10005, var10006);
   }

   private static final AblyManager ablyManager_delegate$lambda$16(AppIdentityInjector var0) {
      Twig var10002 = LumberMill.getAblyTwig();
      Intrinsics.checkNotNullExpressionValue(var10002, "getAblyTwig(...)");
      return new AblyManager(var10002, var0.getMessengerApi());
   }

   // $VF: synthetic method
   public static final Injector access$getInjector(AppIdentityInjector var0) {
      return var0.getInjector();
   }
}

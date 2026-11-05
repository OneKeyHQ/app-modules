package io.intercom.android.sdk;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.google.gson.Gson;
import com.intercom.twig.Twig;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.api.DeDuper;
import io.intercom.android.sdk.api.DeDuperStore;
import io.intercom.android.sdk.api.ExternalUploadApi;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.api.ShutdownStore;
import io.intercom.android.sdk.api.SurveyApi;
import io.intercom.android.sdk.api.UserUpdater;
import io.intercom.android.sdk.api.WrapperPrefsStore;
import io.intercom.android.sdk.errorreporting.ErrorReporter;
import io.intercom.android.sdk.exceptions.IntercomIntegrationException;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.identity.AppConfigStore;
import io.intercom.android.sdk.identity.AppIdentity;
import io.intercom.android.sdk.identity.AppIdentityStore;
import io.intercom.android.sdk.identity.AppIdentityStore$AppCredentials;
import io.intercom.android.sdk.identity.DeviceIdentityStore;
import io.intercom.android.sdk.identity.PushTokenStore;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.identity.UserIdentityStore;
import io.intercom.android.sdk.identity.UserJwtRefresher;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.metrics.MetricsStore;
import io.intercom.android.sdk.metrics.UnreadConversationsTracker;
import io.intercom.android.sdk.metrics.ops.OpsMetricTracker;
import io.intercom.android.sdk.overlay.OverlayPresenter;
import io.intercom.android.sdk.tickets.create.data.TicketApi;
import io.intercom.android.sdk.user.UserUpdaterHelper;
import io.intercom.android.sdk.utilities.ActivityFinisher;
import io.intercom.android.sdk.utilities.CoroutineExceptionHandlersKt;
import io.intercom.android.sdk.utilities.FirstMessageUtilsKt;
import io.intercom.android.sdk.utilities.SystemSettings;
import io.intercom.android.sdk.utilities.UuidStringProvider;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.SupervisorKt;

public class Injector {
   @SuppressLint("StaticFieldLeak")
   @Nullable
   private static Injector instance;
   private static final Twig TWIG = LumberMill.getLogger();
   private final Application application;
   private final UserIdentity userIdentity;
   private final ActivityFinisher activityFinisher = new ActivityFinisher();
   private final CoroutineScope applicationScope;
   private final AppIdentityStore appIdentityStore;
   private final DeviceIdentityStore deviceIdentityStore;
   private final AppConfigStore appConfigStore;
   private final PushTokenStore pushTokenStore;
   private final ShutdownStore shutdownStore;
   private final DeDuperStore deDuperStore;
   private AppIdentityInjector appIdentityInjector;
   @Nullable
   private DeDuper superDeDuper;
   @Nullable
   private MetricsStore metricsStore;
   @Nullable
   private MetricTracker metricTracker;
   @Nullable
   private OpsMetricTracker opsMetricTracker;
   @Nullable
   private OverlayPresenter overlayPresenter;
   @Nullable
   private LifecycleTracker lifecycleTracker;
   @Nullable
   private UserUpdater userUpdater;
   @Nullable
   private ResetManager resetManager;
   @Nullable
   private Gson gson;
   @Nullable
   private ErrorReporter errorReporter;
   @Nullable
   private IntercomDataLayer dataLayer;
   @Nullable
   private UnreadConversationsTracker unreadConversationsTracker;
   @Nullable
   private UserUpdaterHelper userUpdaterHelper;
   @Nullable
   private UserJwtRefresher userJwtRefresher;
   private final Provider appConfigProvider = new Injector$1(this);
   private final Provider nexusClientProvider = this::getNexusClient;
   private final Provider userUpdaterProvider = this::getUserUpdater;
   private final Provider apiProvider = this::getApi;
   private final Provider userIdentityProvider = this::getUserIdentity;
   private final Provider metricTrackerProvider = this::getMetricTracker;

   public static synchronized void initIfCachedCredentials(Application var0) {
      if (instance == null) {
         AppIdentityStore$AppCredentials var1 = AppIdentityStore.loadCachedCredentials(var0);
         String var2 = var1.getApiKey();
         String var3 = var1.getAppId();
         if (!var3.isEmpty() && !var2.isEmpty()) {
            initWithAppCredentials(var0, var2, var3);
         }
      }
   }

   public static synchronized void initWithAppCredentials(Application var0, String var1, String var2) {
      if (instance == null) {
         TWIG.internal("Injector", "Initializing");
         instance = new Injector(var0, var1, var2, null);
         var0.registerActivityLifecycleCallbacks(instance.getLifecycleTracker());
      }
   }

   public synchronized void changeWorkspace(String var1, String var2) {
      TWIG.internal("Injector", "Switching workspace");
      this.getResetManager().hardReset();
      this.appIdentityInjector.destroy();
      this.appIdentityInjector = new AppIdentityInjector(this.getApplication(), var1, var2, null, this.appIdentityStore);
      this.appIdentityInjector.initializeEvents();
   }

   protected Injector(Application var1, String var2, String var3, @Nullable String var4) {
      this.application = var1;
      this.applicationScope = CoroutineScopeKt.CoroutineScope(
         SupervisorKt.SupervisorJob(null).plus(CoroutineExceptionHandlersKt.loggingCoroutineExceptionHandler("Injector.applicationScope"))
      );
      UserIdentityStore var5 = UserIdentityStore.create(var1, this.applicationScope);
      this.userIdentity = new UserIdentity(var5);
      this.appIdentityStore = AppIdentityStore.create(var1, this.applicationScope);
      this.appConfigStore = AppConfigStore.create(var1, this.applicationScope);
      this.deviceIdentityStore = DeviceIdentityStore.create(var1, this.applicationScope);
      this.pushTokenStore = PushTokenStore.create(var1, this.applicationScope);
      this.shutdownStore = ShutdownStore.create(var1, this.applicationScope);
      this.deDuperStore = DeDuperStore.create(var1, this.applicationScope);
      WrapperPrefsStore.create(var1);
      this.appIdentityInjector = new AppIdentityInjector(var1, var2, var3, var4, this.appIdentityStore);
   }

   public static synchronized boolean isNotInitialised() {
      return instance == null;
   }

   public static synchronized Injector get() {
      if (instance == null) {
         String var0 = "Intercom was not initialized correctly, Intercom.initialize() needs to be called in onCreate() in your Application class.";
         throw new IntercomIntegrationException(var0);
      } else {
         return instance;
      }
   }

   public void initializeIntercomEventsListeners() {
      FirstMessageUtilsKt.handleFirstMessageToast(this.applicationScope, LumberMill.getLogger(), this.application, this.getDataLayer());
      this.appIdentityInjector.initializeEvents();
   }

   public Application getApplication() {
      return this.application;
   }

   public Provider getAppConfigProvider() {
      return this.appConfigProvider;
   }

   public AppIdentity getAppIdentity() {
      return this.appIdentityInjector.getAppIdentity();
   }

   public UserIdentity getUserIdentity() {
      return this.userIdentity;
   }

   public DeviceIdentityStore getDeviceIdentityStore() {
      return this.deviceIdentityStore;
   }

   public PushTokenStore getPushTokenStore() {
      return this.pushTokenStore;
   }

   public ShutdownStore getShutdownStore() {
      return this.shutdownStore;
   }

   public synchronized IntercomDataLayer getDataLayer() {
      if (this.dataLayer == null) {
         this.dataLayer = new IntercomDataLayer(this.appConfigStore, this.applicationScope);
      }

      return this.dataLayer;
   }

   public synchronized UnreadConversationsTracker getUnreadConversationsTracker() {
      if (this.unreadConversationsTracker == null) {
         this.unreadConversationsTracker = new UnreadConversationsTracker(this.metricTrackerProvider);
      }

      return this.unreadConversationsTracker;
   }

   public synchronized UserUpdaterHelper getUserUpdaterHelper() {
      if (this.userUpdaterHelper == null) {
         this.userUpdaterHelper = new UserUpdaterHelper(this.userUpdaterProvider, this.getDataLayer(), this.userIdentityProvider);
      }

      return this.userUpdaterHelper;
   }

   public synchronized UserJwtRefresher getUserJwtRefresher() {
      if (this.userJwtRefresher == null) {
         this.userJwtRefresher = UserJwtRefresher.create(this.userIdentity, this.getDataLayer());
      }

      return this.userJwtRefresher;
   }

   public NexusClient getNexusClient() {
      return this.appIdentityInjector.getNexusClient();
   }

   public AblyManager getAblyManager() {
      return this.appIdentityInjector.getAblyManager();
   }

   public synchronized DeDuper getDeDuper() {
      if (this.superDeDuper == null) {
         this.superDeDuper = new DeDuper(this.appConfigProvider, this.deDuperStore);
      }

      return this.superDeDuper;
   }

   public Api getApi() {
      return this.appIdentityInjector.getApi();
   }

   public HelpCenterApi getHelpCenterApi() {
      return this.appIdentityInjector.getHelpCenterApi();
   }

   public SurveyApi getSurveyApi() {
      return this.appIdentityInjector.getSurveyApi();
   }

   public TicketApi getTicketApi() {
      return this.appIdentityInjector.getTicketApi();
   }

   public MessengerApi getMessengerApi() {
      return this.appIdentityInjector.getMessengerApi();
   }

   public ExternalUploadApi getExternalUploadApi() {
      return this.appIdentityInjector.getExternalUploadApi();
   }

   public synchronized Gson getGson() {
      if (this.gson == null) {
         this.gson = new Gson();
      }

      return this.gson;
   }

   public synchronized MetricTracker getMetricTracker() {
      if (this.metricTracker == null) {
         this.metricTracker = new MetricTracker(this.userIdentity, this.getMetricsStore(), this.getApplication());
      }

      return this.metricTracker;
   }

   public synchronized MetricsStore getMetricsStore() {
      if (this.metricsStore == null) {
         this.metricsStore = new MetricsStore(this.getApplication(), this.getApiProvider(), this.appConfigProvider);
      }

      return this.metricsStore;
   }

   public synchronized OpsMetricTracker getOpsMetricTracker() {
      if (this.opsMetricTracker == null) {
         this.opsMetricTracker = new OpsMetricTracker(this.getMetricsStore(), this.getTimeProvider(), UuidStringProvider.SYSTEM);
      }

      return this.opsMetricTracker;
   }

   public synchronized OverlayPresenter getOverlayPresenter() {
      if (this.overlayPresenter == null) {
         this.overlayPresenter = new OverlayPresenter(
            this.getApplication(), this.appConfigProvider, this.getMetricTracker(), this.userIdentity, this.getDataLayer(), this.getApiProvider()
         );
      }

      return this.overlayPresenter;
   }

   public TimeProvider getTimeProvider() {
      return TimeProvider.SYSTEM;
   }

   public synchronized LifecycleTracker getLifecycleTracker() {
      if (this.lifecycleTracker == null) {
         this.lifecycleTracker = LifecycleTracker.create(
            this.getMetricsStore(),
            this.getErrorReporter(),
            this.getDeDuper(),
            this.getTimeProvider(),
            this.getResetManager(),
            SystemSettings.getTransitionScale(this.application),
            this.getApiProvider(),
            this.getDataLayer(),
            this.getUserUpdaterHelper()
         );
      }

      return this.lifecycleTracker;
   }

   public synchronized ErrorReporter getErrorReporter() {
      if (this.errorReporter == null) {
         this.errorReporter = ErrorReporter.create(this.application, this.getGson(), this.apiProvider, this.getDataLayer());
      }

      return this.errorReporter;
   }

   public synchronized UserUpdater getUserUpdater() {
      if (this.userUpdater == null) {
         this.userUpdater = new UserUpdater(this.getApiProvider(), this.getOpsMetricTracker(), this.getDataLayer());
      }

      return this.userUpdater;
   }

   public synchronized ResetManager getResetManager() {
      if (this.resetManager == null) {
         this.resetManager = new ResetManager(
            this.getApiProvider(),
            this.getUserIdentity(),
            this.getOverlayPresenter(),
            this.appConfigProvider,
            this.application,
            this.activityFinisher,
            this.getDataLayer(),
            this.getUnreadConversationsTracker(),
            this.pushTokenStore
         );
      }

      return this.resetManager;
   }

   public Provider getUserUpdaterProvider() {
      return this.userUpdaterProvider;
   }

   public Provider getApiProvider() {
      return this.apiProvider;
   }

   public Provider getNexusClientProvider() {
      return this.nexusClientProvider;
   }

   public ActivityFinisher getActivityFinisher() {
      return this.activityFinisher;
   }

   @VisibleForTesting
   static void setSharedInstance(Injector var0) {
      instance = var0;
   }

   // $VF: synthetic method
   static IntercomDataLayer access$000(Injector var0) {
      return var0.dataLayer;
   }
}

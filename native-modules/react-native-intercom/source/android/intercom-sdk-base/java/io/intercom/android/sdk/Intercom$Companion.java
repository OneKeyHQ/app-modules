package io.intercom.android.sdk;

import android.app.Application;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.api.DeDuper;
import io.intercom.android.sdk.errorreporting.ErrorReporter;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.identity.UserJwtRefresher;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.UnreadCountTracker;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.overlay.OverlayPresenter;
import io.intercom.android.sdk.ui.theme.ThemeManager;
import io.intercom.android.sdk.user.UserUpdaterHelper;
import io.intercom.android.sdk.utilities.ActivityFinisher;
import io.intercom.android.sdk.utilities.ValidatorUtil;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\b2\b\u0010\u0014\u001a\u0004\u0018\u00010\bH\u0007J\u0012\u0010\u0015\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0007J\u0012\u0010\u0016\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0007J\b\u0010\u0017\u001a\u00020\u000eH\u0007J\u0012\u0010\u0018\u001a\u00020\u00102\b\b\u0001\u0010\u0019\u001a\u00020\u001aH\u0007J \u0010\u001b\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\bH\u0002R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0086T¢\u0006\u0002\n\u0000R\u0018\u0010\t\u001a\n \u000b*\u0004\u0018\u00010\n0\nX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\fR\u0014\u0010\r\u001a\u0004\u0018\u00010\u000e8\u0002@\u0002X\u0083\u000e¢\u0006\u0002\n\u0000¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/Intercom$Companion;",
         "",
         "<init>",
         "()V",
         "VISIBLE",
         "Lio/intercom/android/sdk/Intercom$Visibility;",
         "GONE",
         "PUSH_RECEIVER",
         "",
         "TWIG",
         "Lcom/intercom/twig/Twig;",
         "kotlin.jvm.PlatformType",
         "Lcom/intercom/twig/Twig;",
         "instance",
         "Lio/intercom/android/sdk/Intercom;",
         "initialize",
         "",
         "application",
         "Landroid/app/Application;",
         "apiKey",
         "appId",
         "registerForLaterInitialisation",
         "unregisterForLateInitialisation",
         "client",
         "setLogLevel",
         "logLevel",
         "",
         "create",
         "intercom-sdk-base_release"
   }
)
public final class Intercom$Companion {
   private Intercom$Companion() {
   }

   @JvmStatic
   public final synchronized void initialize(@Nullable Application var1, @Nullable String var2, @Nullable String var3) {
      if (Intercom.access$getInstance$cp() != null) {
         Intercom.access$getTWIG$cp().i("Intercom has already been initialized");
      } else {
         if (var1 != null && var2 != null && var3 != null && ValidatorUtil.isValidConstructorParams(var1, var2, var3)) {
            Intercom.access$setInstance$cp(this.create(var1, var2, var3));
            LateInitializationPreparer.getInstance().handlePastLifecycleEvents(var1, Injector.get());
            Injector.get().initializeIntercomEventsListeners();
         } else {
            Intercom.access$getTWIG$cp().e("Intercom has been initialized incorrectly. Please make sure that you're passing in the correct app ID and API key");
         }
      }
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(message = "Use initialize() instead. SDK will not communicate with Intercom until a user registration is made.")
   public final synchronized void registerForLaterInitialisation(@Nullable Application var1) {
      if (Intercom.access$getInstance$cp() != null) {
         Intercom.access$getTWIG$cp().i("Intercom has already been initialized");
      } else {
         if (var1 == null) {
            throw new NullPointerException("Cannot call registerForLaterInitialisation() with a null Application");
         }

         LateInitializationPreparer.getInstance().register(var1);
      }
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(message = "Use initialize() instead. SDK will not communicate with Intercom until a user registration is made.")
   public final void unregisterForLateInitialisation(@Nullable Application var1) {
      if (var1 == null) {
         throw new NullPointerException("Cannot call unregisterForLateInitialisation() with a null Application");
      }

      LateInitializationPreparer.getInstance().unregister(var1);
   }

   @JvmStatic
   @NotNull
   public final synchronized Intercom client() {
      Intercom var10000 = Intercom.access$getInstance$cp();
      if (var10000 == null) {
         int var1 = 0;
         String var2 = "Intercom has been initialized incorrectly. Please make sure the first Intercom method you call is initialize() and that you're passing in the correct app ID and API key";
         throw new IllegalStateException(var2.toString());
      } else {
         return var10000;
      }
   }

   @JvmStatic
   public final void setLogLevel(@Intercom$LogLevel int var1) {
      LumberMill.setLogLevel(var1);
   }

   private final Intercom create(Application var1, String var2, String var3) {
      Injector.initWithAppCredentials(var1, var2, var3);
      Injector var4 = Injector.get();
      IntercomDataLayer var5 = var4.getDataLayer();
      ThemeManager.INSTANCE.initialize();
      DeDuper var10002 = var4.getDeDuper();
      Intrinsics.checkNotNullExpressionValue(var10002, "getDeDuper(...)");
      Provider var10003 = var4.getApiProvider();
      Intrinsics.checkNotNullExpressionValue(var10003, "getApiProvider(...)");
      Provider var10004 = var4.getNexusClientProvider();
      Intrinsics.checkNotNullExpressionValue(var10004, "getNexusClientProvider(...)");
      Intrinsics.checkNotNull(var5);
      UnreadCountTracker var10005 = new UnreadCountTracker(var5);
      MetricTracker var10006 = var4.getMetricTracker();
      Intrinsics.checkNotNullExpressionValue(var10006, "getMetricTracker(...)");
      ErrorReporter var10007 = var4.getErrorReporter();
      Intrinsics.checkNotNullExpressionValue(var10007, "getErrorReporter(...)");
      OverlayPresenter var10009 = var4.getOverlayPresenter();
      Intrinsics.checkNotNullExpressionValue(var10009, "getOverlayPresenter(...)");
      Provider var10010 = var4.getAppConfigProvider();
      Intrinsics.checkNotNullExpressionValue(var10010, "getAppConfigProvider(...)");
      UserIdentity var10011 = var4.getUserIdentity();
      Intrinsics.checkNotNullExpressionValue(var10011, "getUserIdentity(...)");
      Provider var10012 = var4.getUserUpdaterProvider();
      Intrinsics.checkNotNullExpressionValue(var10012, "getUserUpdaterProvider(...)");
      ResetManager var10013 = var4.getResetManager();
      Intrinsics.checkNotNullExpressionValue(var10013, "getResetManager(...)");
      Twig var10014 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10014, "getLogger(...)");
      ActivityFinisher var10015 = var4.getActivityFinisher();
      Intrinsics.checkNotNullExpressionValue(var10015, "getActivityFinisher(...)");
      UserUpdaterHelper var10017 = var4.getUserUpdaterHelper();
      Intrinsics.checkNotNullExpressionValue(var10017, "getUserUpdaterHelper(...)");
      UserJwtRefresher var10018 = var4.getUserJwtRefresher();
      Intrinsics.checkNotNullExpressionValue(var10018, "getUserJwtRefresher(...)");
      return new Intercom(
         var10002,
         var10003,
         var10004,
         var10005,
         var10006,
         var10007,
         var1,
         var10009,
         var10010,
         var10011,
         var10012,
         var10013,
         var10014,
         var10015,
         var5,
         var10017,
         var10018
      );
   }

   // $VF: synthetic method
   public Intercom$Companion(DefaultConstructorMarker var1) {
      this();
   }
}

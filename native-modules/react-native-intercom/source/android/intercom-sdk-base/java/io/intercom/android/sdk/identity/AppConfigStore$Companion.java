package io.intercom.android.sdk.identity;

import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.annotation.VisibleForTesting;
import androidx.datastore.core.DataMigration;
import androidx.datastore.core.DataStore;
import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.sdk.models.AttachmentSettings;
import io.intercom.android.sdk.models.ConversationStateSyncSettings;
import io.intercom.android.sdk.utilities.ColorUtils;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference2Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0002J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0007J-\u0010\u0015\u001a\u00020\u00102\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u00172\u0006\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0019\u001a\u00020\u001aH\u0001¢\u0006\u0002\b\u001bJ\u001b\u0010N\u001a\b\u0012\u0004\u0012\u00020\u00180O2\u0006\u0010\u0011\u001a\u00020\u0012H\u0001¢\u0006\u0002\bPR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\u00020\t8\u0000X\u0081T¢\u0006\b\n\u0000\u0012\u0004\b\n\u0010\u0003R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001d0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010#\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010%\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010&\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010'\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010(\u001a\b\u0012\u0004\u0012\u00020\u000e0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010)\u001a\b\u0012\u0004\u0012\u00020\u000e0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010*\u001a\b\u0012\u0004\u0012\u00020\u000e0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010+\u001a\b\u0012\u0004\u0012\u00020\t0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010,\u001a\b\u0012\u0004\u0012\u00020\t0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010-\u001a\b\u0012\u0004\u0012\u00020\t0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010.\u001a\b\u0012\u0004\u0012\u00020\t0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010/\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00101\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00102\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00103\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00104\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00105\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00106\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00107\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00108\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00109\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010:\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010;\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010<\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010=\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010>\u001a\b\u0012\u0004\u0012\u0002000\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010?\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0@0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010A\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0@0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R \u0010B\u001a\u0014\u0012\u0004\u0012\u00020\u001d\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u001f0CX\u0082\u0004¢\u0006\u0002\n\u0000R \u0010D\u001a\u0014\u0012\u0004\u0012\u00020\u001d\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u001f0CX\u0082\u0004¢\u0006\u0002\n\u0000R \u0010E\u001a\u0014\u0012\u0004\u0012\u00020\u001d\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u001f0CX\u0082\u0004¢\u0006\u0002\n\u0000R \u0010F\u001a\u0014\u0012\u0004\u0012\u00020\u001d\u0012\n\u0012\b\u0012\u0004\u0012\u0002000\u001f0CX\u0082\u0004¢\u0006\u0002\n\u0000R&\u0010G\u001a\u001a\u0012\u0004\u0012\u00020\u001d\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0@0\u001f0CX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010H\u001a\b\u0012\u0004\u0012\u00020\u001d0@X\u0082\u0004¢\u0006\u0002\n\u0000R%\u0010I\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017*\u00020\u00128BX\u0082\u0084\u0002¢\u0006\f\n\u0004\bL\u0010M\u001a\u0004\bJ\u0010K¨\u0006Q",
   d2 = {
         "Lio/intercom/android/sdk/identity/AppConfigStore$Companion;",
         "",
         "<init>",
         "()V",
         "GSON",
         "Lcom/google/gson/Gson;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "RETRY_DELAY_MS",
         "",
         "getRETRY_DELAY_MS$intercom_sdk_base_release$annotations",
         "defaultAppConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "defaultColor",
         "",
         "create",
         "Lio/intercom/android/sdk/identity/AppConfigStore;",
         "context",
         "Landroid/content/Context;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "createForTesting",
         "dataStore",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "ioDispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "createForTesting$intercom_sdk_base_release",
         "OLD_PREFS_NAME",
         "",
         "KEY_APP_NAME",
         "Landroidx/datastore/preferences/core/Preferences$Key;",
         "KEY_APP_LOCALE",
         "KEY_HELP_CENTER_LOCALE",
         "KEY_LAUNCHER_LOGO_URL",
         "KEY_TEAM_GREETING",
         "KEY_HELP_CENTER_URL",
         "KEY_HUB_CONFIG",
         "KEY_ATTACHMENT_SETTINGS",
         "KEY_SESSION_ID",
         "KEY_PRIMARY_COLOR",
         "KEY_SECONDARY_COLOR",
         "KEY_RATE_LIMIT_COUNT",
         "KEY_USER_UPDATE_CACHE_MAX_AGE_MS",
         "KEY_RATE_LIMIT_PERIOD_MS",
         "KEY_SOFT_RESET_TIMEOUT_MS",
         "KEY_NEW_SESSION_THRESHOLD_MS",
         "KEY_INBOUND_MESSAGES",
         "",
         "KEY_PRIMARY_COLOR_RENDER_DARK_TEXT",
         "KEY_SECONDARY_COLOR_RENDER_DARK_TEXT",
         "KEY_AUDIO_ENABLED",
         "KEY_METRICS_ENABLED",
         "KEY_RECEIVED_FROM_SERVER",
         "KEY_BACKGROUND_REQUESTS_ENABLED",
         "KEY_ACCESS_TEAMMATE_ENABLED",
         "KEY_HELP_CENTER_REQUIRE_SEARCH",
         "KEY_PREVENT_MULTIPLE_INBOUND_CONVERSATIONS",
         "KEY_HAS_OPEN_CONVERSATIONS",
         "KEY_ARTICLE_AUTO_REACTION_ENABLED",
         "KEY_IDENTITY_VERIFICATION_ENABLED",
         "KEY_PUSH_NOTIFICATIONS_BANNER_DISMISSED",
         "KEY_CRASH_REPORTING_ENABLED",
         "KEY_HELP_CENTER_URLS",
         "",
         "KEY_FEATURES",
         "STRING_MIGRATION_KEYS",
         "",
         "INT_MIGRATION_KEYS",
         "LONG_MIGRATION_KEYS",
         "BOOLEAN_MIGRATION_KEYS",
         "STRING_SET_MIGRATION_KEYS",
         "ALL_MIGRATION_KEY_NAMES",
         "appConfigDataStore",
         "getAppConfigDataStore",
         "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;",
         "appConfigDataStore$delegate",
         "Lkotlin/properties/ReadOnlyProperty;",
         "createSharedPrefsMigration",
         "Landroidx/datastore/core/DataMigration;",
         "createSharedPrefsMigration$intercom_sdk_base_release",
         "intercom-sdk-base_release"
   }
)
public final class AppConfigStore$Companion {
   // $VF: synthetic field
   static final KProperty[] $$delegatedProperties;

   private AppConfigStore$Companion() {
   }

   /** @deprecated */
   // $VF: synthetic method
   @VisibleForTesting
   public static void getRETRY_DELAY_MS$intercom_sdk_base_release$annotations() {
   }

   private final AppConfig defaultAppConfig(@ColorInt int var1) {
      return new AppConfig(
         "",
         var1,
         var1,
         ColorUtils.darkenColor(var1),
         false,
         false,
         false,
         100,
         TimeUnit.SECONDS.toMillis(60L),
         TimeUnit.SECONDS.toMillis(300L),
         TimeUnit.SECONDS.toMillis(20L),
         TimeUnit.SECONDS.toMillis(1L),
         true,
         true,
         "",
         "",
         false,
         true,
         "",
         SetsKt.emptySet(),
         SetsKt.emptySet(),
         "",
         "",
         false,
         true,
         false,
         false,
         false,
         null,
         new NexusConfig(),
         AttachmentSettings.Companion.getDEFAULT(),
         true,
         ConversationStateSyncSettings.Companion.getDEFAULT(),
         true
      );
   }

   @JvmStatic
   @NotNull
   public final AppConfigStore create(@NotNull Context var1, @NotNull CoroutineScope var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "scope");
      return new AppConfigStore(this.getAppConfigDataStore(var1), var2, Dispatchers.getIO(), null);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public final AppConfigStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var1, @NotNull CoroutineScope var2, @NotNull CoroutineDispatcher var3
   ) {
      Intrinsics.checkNotNullParameter(var1, "dataStore");
      Intrinsics.checkNotNullParameter(var2, "scope");
      Intrinsics.checkNotNullParameter(var3, "ioDispatcher");
      return new AppConfigStore(var1, var2, var3, null);
   }

   // $VF: synthetic method
   public static AppConfigStore createForTesting$intercom_sdk_base_release$default(
      AppConfigStore$Companion var0, DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, int var4, Object var5
   ) {
      if ((var4 & 4) != 0) {
         var3 = Dispatchers.getIO();
      }

      return var0.createForTesting$intercom_sdk_base_release(var1, var2, var3);
   }

   private final DataStore getAppConfigDataStore(Context var1) {
      return (DataStore)AppConfigStore.access$getAppConfigDataStore$delegate$cp().getValue(var1, $$delegatedProperties[0]);
   }

   @VisibleForTesting
   @NotNull
   public final DataMigration createSharedPrefsMigration$intercom_sdk_base_release(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      return new AppConfigStore$Companion$createSharedPrefsMigration$1(var1);
   }

   // $VF: synthetic method
   public static final AppConfig access$defaultAppConfig(AppConfigStore$Companion var0, int var1) {
      return var0.defaultAppConfig(var1);
   }

   // $VF: synthetic method
   public AppConfigStore$Companion(DefaultConstructorMarker var1) {
      this();
   }

   static {
      KProperty[] var0 = new KProperty[]{
         Reflection.property2(
            new PropertyReference2Impl(
               AppConfigStore$Companion.class, "appConfigDataStore", "getAppConfigDataStore(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", 0
            )
         )
      };
      $$delegatedProperties = var0;
   }
}

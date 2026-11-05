package io.intercom.android.sdk.identity;

import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.core.Preferences.Key;
import com.google.gson.Gson;
import com.intercom.twig.Twig;
import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.models.AttachmentSettings;
import io.intercom.android.sdk.models.AttachmentSettingsKt;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.ConfigModulesKt;
import io.intercom.android.sdk.models.ConversationStateSyncSettings;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.MultiProcessDataStoreDelegateKt;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.properties.ReadOnlyProperty;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CompletableDeferred;
import kotlinx.coroutines.CompletableDeferredKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0001\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB'\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\n\u0010\u0011\u001a\u0004\u0018\u00010\u0004H\u0002J\u0010\u0010\u0012\u001a\u00020\u00132\b\b\u0001\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u0013J\b\u0010\u0018\u001a\u0004\u0018\u00010\rJ\u0006\u0010\u0019\u001a\u00020\rJ\u0006\u0010\u001a\u001a\u00020\u001bJ\u000e\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u001bJ\u0006\u0010\u001e\u001a\u00020\u0010R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/identity/AppConfigStore;",
         "",
         "dataStore",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "ioDispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "<init>",
         "(Landroidx/datastore/core/DataStore;Lkotlinx/coroutines/CoroutineScope;Lkotlinx/coroutines/CoroutineDispatcher;)V",
         "cachedPrefs",
         "generatedSessionId",
         "",
         "firstLoad",
         "Lkotlinx/coroutines/CompletableDeferred;",
         "",
         "prefsSnapshot",
         "getAppConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "defaultColor",
         "",
         "setAppConfig",
         "config",
         "getSessionId",
         "generateSessionId",
         "getPushNotificationsBannerDismissed",
         "",
         "setPushNotificationsBannerDismissed",
         "dismissed",
         "clear",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nAppConfigStore.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AppConfigStore.kt\nio/intercom/android/sdk/identity/AppConfigStore\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,508:1\n1#2:509\n*E\n"
)
public final class AppConfigStore {
   @NotNull
   public static final AppConfigStore$Companion Companion = new AppConfigStore$Companion(null);
   @NotNull
   private final DataStore dataStore;
   @NotNull
   private final CoroutineScope scope;
   @Nullable
   private volatile Preferences cachedPrefs;
   @Nullable
   private volatile String generatedSessionId;
   @NotNull
   private final CompletableDeferred firstLoad;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final Gson GSON = new Gson();
   @NotNull
   private static final Twig twig;
   public static final long RETRY_DELAY_MS = 1000L;
   @NotNull
   private static final String OLD_PREFS_NAME = "INTERCOM_SDK_PREFS";
   @NotNull
   private static final Key KEY_APP_NAME = PreferencesKeys.stringKey("app_name");
   @NotNull
   private static final Key KEY_APP_LOCALE = PreferencesKeys.stringKey("app_locale");
   @NotNull
   private static final Key KEY_HELP_CENTER_LOCALE = PreferencesKeys.stringKey("help_center_locale");
   @NotNull
   private static final Key KEY_LAUNCHER_LOGO_URL = PreferencesKeys.stringKey("app_launcher_logo_url");
   @NotNull
   private static final Key KEY_TEAM_GREETING = PreferencesKeys.stringKey("app_team_greeting");
   @NotNull
   private static final Key KEY_HELP_CENTER_URL = PreferencesKeys.stringKey("help_center_url");
   @NotNull
   private static final Key KEY_HUB_CONFIG = PreferencesKeys.stringKey("hub_config");
   @NotNull
   private static final Key KEY_ATTACHMENT_SETTINGS = PreferencesKeys.stringKey("app_attachment_settings");
   @NotNull
   private static final Key KEY_SESSION_ID = PreferencesKeys.stringKey("session_id");
   @NotNull
   private static final Key KEY_PRIMARY_COLOR = PreferencesKeys.intKey("app_primary_color");
   @NotNull
   private static final Key KEY_SECONDARY_COLOR = PreferencesKeys.intKey("app_secondary_color");
   @NotNull
   private static final Key KEY_RATE_LIMIT_COUNT = PreferencesKeys.intKey("app_rate_limit_count");
   @NotNull
   private static final Key KEY_USER_UPDATE_CACHE_MAX_AGE_MS = PreferencesKeys.longKey("app_user_update_cache_max_age_ms");
   @NotNull
   private static final Key KEY_RATE_LIMIT_PERIOD_MS = PreferencesKeys.longKey("app_rate_limit_period_ms");
   @NotNull
   private static final Key KEY_SOFT_RESET_TIMEOUT_MS = PreferencesKeys.longKey("app_soft_reset_timeout_ms");
   @NotNull
   private static final Key KEY_NEW_SESSION_THRESHOLD_MS = PreferencesKeys.longKey("new_session_threshold_ms");
   @NotNull
   private static final Key KEY_INBOUND_MESSAGES = PreferencesKeys.booleanKey("app_inbound_messages");
   @NotNull
   private static final Key KEY_PRIMARY_COLOR_RENDER_DARK_TEXT = PreferencesKeys.booleanKey("app_primary_color_render_dark_text");
   @NotNull
   private static final Key KEY_SECONDARY_COLOR_RENDER_DARK_TEXT = PreferencesKeys.booleanKey("app_secondary_color_render_dark_text");
   @NotNull
   private static final Key KEY_AUDIO_ENABLED = PreferencesKeys.booleanKey("app_audio_enabled");
   @NotNull
   private static final Key KEY_METRICS_ENABLED = PreferencesKeys.booleanKey("app_metrics_enabled");
   @NotNull
   private static final Key KEY_RECEIVED_FROM_SERVER = PreferencesKeys.booleanKey("app_received_from_server");
   @NotNull
   private static final Key KEY_BACKGROUND_REQUESTS_ENABLED = PreferencesKeys.booleanKey("app_background_requests_enabled");
   @NotNull
   private static final Key KEY_ACCESS_TEAMMATE_ENABLED = PreferencesKeys.booleanKey("access_to_teammate_enabled");
   @NotNull
   private static final Key KEY_HELP_CENTER_REQUIRE_SEARCH = PreferencesKeys.booleanKey("help_center_require_search");
   @NotNull
   private static final Key KEY_PREVENT_MULTIPLE_INBOUND_CONVERSATIONS = PreferencesKeys.booleanKey("prevent_multiple_inbound_conversations_enabled");
   @NotNull
   private static final Key KEY_HAS_OPEN_CONVERSATIONS = PreferencesKeys.booleanKey("has_open_conversations");
   @NotNull
   private static final Key KEY_ARTICLE_AUTO_REACTION_ENABLED = PreferencesKeys.booleanKey("article_auto_reaction_enabled");
   @NotNull
   private static final Key KEY_IDENTITY_VERIFICATION_ENABLED = PreferencesKeys.booleanKey("app_identity_verification_enabled");
   @NotNull
   private static final Key KEY_PUSH_NOTIFICATIONS_BANNER_DISMISSED = PreferencesKeys.booleanKey("push_notifications_banner_dismissed");
   @NotNull
   private static final Key KEY_CRASH_REPORTING_ENABLED = PreferencesKeys.booleanKey("crash_reporting_enabled");
   @NotNull
   private static final Key KEY_HELP_CENTER_URLS = PreferencesKeys.stringSetKey("help_center_urls");
   @NotNull
   private static final Key KEY_FEATURES = PreferencesKeys.stringSetKey("features");
   @NotNull
   private static final Map STRING_MIGRATION_KEYS;
   @NotNull
   private static final Map INT_MIGRATION_KEYS;
   @NotNull
   private static final Map LONG_MIGRATION_KEYS;
   @NotNull
   private static final Map BOOLEAN_MIGRATION_KEYS;
   @NotNull
   private static final Map STRING_SET_MIGRATION_KEYS;
   @NotNull
   private static final Set ALL_MIGRATION_KEY_NAMES;
   @NotNull
   private static final ReadOnlyProperty appConfigDataStore$delegate = MultiProcessDataStoreDelegateKt.multiProcessPreferencesDataStore$default(
      "intercom_app_config_datastore", null, ((kotlin.jvm.functions.Function1) (recoveredArgument_119_0) -> AppConfigStore.appConfigDataStore_delegate$lambda$5((android.content.Context)recoveredArgument_119_0)), 2, null
   );

   private AppConfigStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3) {
      this.dataStore = var1;
      this.scope = var2;
      this.firstLoad = CompletableDeferredKt.CompletableDeferred$default(null, 1, null);
      Job var4 = BuildersKt.launch$default(this.scope, var3, null, new AppConfigStore$job$1(this, null), 2, null);
      final io.intercom.android.sdk.identity.AppConfigStore recoveredExpressionCapture_120_0 = this;
      var4.invokeOnCompletion(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_120_0) -> io.intercom.android.sdk.identity.AppConfigStore._init_$lambda$0(recoveredExpressionCapture_120_0, (java.lang.Throwable) ((java.lang.Throwable)recoveredExpressionArgument_120_0))));
   }

   private final Preferences prefsSnapshot() {
      if (this.cachedPrefs != null) {
         Preferences var3 = this.cachedPrefs;
         int var4 = 0;
         return var3;
      }

      Preferences var1;
      try {
         BuildersKt.runBlocking$default(null, new AppConfigStore$prefsSnapshot$2(this, null), 1, null);
         var1 = this.cachedPrefs;
      } catch (InterruptedException var5) {
         Thread.currentThread().interrupt();
         var1 = this.cachedPrefs;
      } catch (Exception var6) {
         twig.w("Failed to load AppConfig prefs: " + var6.getMessage());
         var1 = null;
      }

      return var1;
   }

   @NotNull
   public final AppConfig getAppConfig(@ColorInt int var1) {
      Preferences var10000 = this.prefsSnapshot();
      if (var10000 == null) {
         return AppConfigStore$Companion.access$defaultAppConfig(Companion, var1);
      }

      Preferences var2 = var10000;

      AppConfig var3;
      try {
         Integer var148 = (Integer)var2.get(KEY_PRIMARY_COLOR);
         int var85 = var148 != null ? var148 : var1;
         Integer var149 = (Integer)var2.get(KEY_SECONDARY_COLOR);
         int var4 = var149 != null ? var149 : var85;
         String var150 = (String)var2.get(KEY_APP_NAME);
         if (var150 == null) {
            var150 = "";
         }

         int var10003 = ColorUtils.darkenColor(var4);
         Boolean var10004 = (Boolean)var2.get(KEY_PRIMARY_COLOR_RENDER_DARK_TEXT);
         boolean var154 = var10004 != null ? var10004 : false;
         Boolean var10005 = (Boolean)var2.get(KEY_SECONDARY_COLOR_RENDER_DARK_TEXT);
         boolean var156 = var10005 != null ? var10005 : false;
         Boolean var10006 = (Boolean)var2.get(KEY_INBOUND_MESSAGES);
         boolean var158 = var10006 != null ? var10006 : false;
         Integer var10007 = (Integer)var2.get(KEY_RATE_LIMIT_COUNT);
         int var160 = var10007 != null ? var10007 : 100;
         Long var10008 = (Long)var2.get(KEY_RATE_LIMIT_PERIOD_MS);
         long var162 = var10008 != null ? var10008 : TimeUnit.SECONDS.toMillis(60L);
         Long var10009 = (Long)var2.get(KEY_USER_UPDATE_CACHE_MAX_AGE_MS);
         long var164 = var10009 != null ? var10009 : TimeUnit.SECONDS.toMillis(300L);
         Long var10010 = (Long)var2.get(KEY_NEW_SESSION_THRESHOLD_MS);
         long var166 = var10010 != null ? var10010 : TimeUnit.SECONDS.toMillis(20L);
         Long var10011 = (Long)var2.get(KEY_SOFT_RESET_TIMEOUT_MS);
         long var168 = var10011 != null ? var10011 : TimeUnit.SECONDS.toMillis(1L);
         Boolean var10012 = (Boolean)var2.get(KEY_METRICS_ENABLED);
         boolean var170 = var10012 != null ? var10012 : true;
         Boolean var10013 = (Boolean)var2.get(KEY_AUDIO_ENABLED);
         boolean var172 = var10013 != null ? var10013 : true;
         String var10014 = (String)var2.get(KEY_APP_LOCALE);
         if (var10014 == null) {
            var10014 = "";
         }

         String var10015 = (String)var2.get(KEY_HELP_CENTER_LOCALE);
         if (var10015 == null) {
            var10015 = "";
         }

         Boolean var10016 = (Boolean)var2.get(KEY_RECEIVED_FROM_SERVER);
         boolean var176 = var10016 != null ? var10016 : false;
         Boolean var10017 = (Boolean)var2.get(KEY_BACKGROUND_REQUESTS_ENABLED);
         boolean var178 = var10017 != null ? var10017 : true;
         String var10018 = (String)var2.get(KEY_HELP_CENTER_URL);
         if (var10018 == null) {
            var10018 = "";
         }

         Set var10019 = (Set)var2.get(KEY_HELP_CENTER_URLS);
         if (var10019 == null) {
            var10019 = SetsKt.emptySet();
         }

         Set var10020 = (Set)var2.get(KEY_FEATURES);
         if (var10020 == null) {
            var10020 = SetsKt.emptySet();
         }

         String var10021 = (String)var2.get(KEY_LAUNCHER_LOGO_URL);
         if (var10021 == null) {
            var10021 = "";
         }

         String var10022 = (String)var2.get(KEY_TEAM_GREETING);
         if (var10022 == null) {
            var10022 = "";
         }

         Boolean var10023 = (Boolean)var2.get(KEY_IDENTITY_VERIFICATION_ENABLED);
         boolean var185 = var10023 != null ? var10023 : false;
         Boolean var10024 = (Boolean)var2.get(KEY_ACCESS_TEAMMATE_ENABLED);
         boolean var187 = var10024 != null ? var10024 : true;
         Boolean var10025 = (Boolean)var2.get(KEY_HELP_CENTER_REQUIRE_SEARCH);
         boolean var189 = var10025 != null ? var10025 : false;
         Boolean var10026 = (Boolean)var2.get(KEY_PREVENT_MULTIPLE_INBOUND_CONVERSATIONS);
         boolean var191 = var10026 != null ? var10026 : false;
         Boolean var10027 = (Boolean)var2.get(KEY_HAS_OPEN_CONVERSATIONS);
         boolean var193 = var10027 != null ? var10027 : false;
         AppConfigStore var5 = this;
         boolean var41 = var193;
         boolean var40 = var191;
         boolean var39 = var189;
         boolean var38 = var187;
         boolean var37 = var185;
         String var36 = var10022;
         String var35 = var10021;
         Set var34 = var10020;
         Set var33 = var10019;
         String var32 = var10018;
         boolean var31 = var178;
         boolean var30 = var176;
         String var29 = var10015;
         String var28 = var10014;
         boolean var27 = var172;
         boolean var26 = var170;
         long var24 = var168;
         long var22 = var166;
         long var20 = var164;
         long var18 = var162;
         int var17 = var160;
         boolean var16 = var158;
         boolean var15 = var156;
         boolean var14 = var154;
         int var13 = var10003;
         int var12 = var4;
         int var11 = var85;
         String var10 = var150;

         Object var6;
         try {
            var6 = var5;
            int var7 = 0;
            ConfigModules var151 = (ConfigModules)GSON.fromJson((String)var2.get(KEY_HUB_CONFIG), ConfigModules.class);
            var6 = Result.recovered$52f8e2dc6aafd1c0(var151 != null ? ConfigModulesKt.validated(var151) : null);
         } catch (Throwable var83) {
            var6 = Result.recovered$52f8e2dc6aafd1c0(ResultKt.createFailure(var83));
         }

         Object var42 = var6;
         String var152 = var10;
         int var10001 = var11;
         int var10002 = var12;
         var10003 = var13;
         var154 = var14;
         var156 = var15;
         var158 = var16;
         var160 = var17;
         var162 = var18;
         var164 = var20;
         var166 = var22;
         var168 = var24;
         var170 = var26;
         var172 = var27;
         var10014 = var28;
         var10015 = var29;
         var176 = var30;
         var178 = var31;
         var10018 = var32;
         var10019 = var33;
         var10020 = var34;
         var10021 = var35;
         var10022 = var36;
         var185 = var37;
         var187 = var38;
         var189 = var39;
         var191 = var40;
         var193 = var41;
         Object var86 = var42;
         Throwable var10028 = Result.recovered$a1b169544506bc0c(var86);
         ConfigModules var195;
         if (var10028 == null) {
            var195 = (ConfigModules)var86;
         } else {
            Throwable recoveredHubConfigFailure = var10028;
            var41 = var41;
            var40 = var40;
            var39 = var39;
            var38 = var38;
            var37 = var37;
            var36 = var36;
            var35 = var35;
            var34 = var34;
            var33 = var33;
            var32 = var32;
            var31 = var31;
            var30 = var30;
            var29 = var29;
            var28 = var28;
            var27 = var27;
            var26 = var26;
            var24 = var24;
            var22 = var22;
            var20 = var20;
            var18 = var18;
            var17 = var17;
            var16 = var16;
            var15 = var15;
            var14 = var14;
            var13 = var13;
            var12 = var12;
            var11 = var11;
            var10 = var10;
            int var89 = 0;
            twig.w("Discarding invalid persisted hub config: " + recoveredHubConfigFailure.getMessage());
            var42 = null;
            var152 = var10;
            var10001 = var11;
            var10002 = var12;
            var10003 = var13;
            var154 = var14;
            var156 = var15;
            var158 = var16;
            var160 = var17;
            var162 = var18;
            var164 = var20;
            var166 = var22;
            var168 = var24;
            var170 = var26;
            var172 = var27;
            var10014 = var28;
            var10015 = var29;
            var176 = var30;
            var178 = var31;
            var10018 = var32;
            var10019 = var33;
            var10020 = var34;
            var10021 = var35;
            var10022 = var36;
            var185 = var37;
            var187 = var38;
            var189 = var39;
            var191 = var40;
            var193 = var41;
            var195 = (ConfigModules)var42;
         }

         AttachmentSettings var197;
         NexusConfig var10029;
         label158: {
            var195 = var195;
            var10029 = new NexusConfig();
            String var10030 = (String)var2.get(KEY_ATTACHMENT_SETTINGS);
            if (var10030 != null) {
               String var8 = var10030;
               NexusConfig var43 = var10029;
               var42 = var195;
               var41 = var193;
               var40 = var191;
               var39 = var189;
               var38 = var187;
               var37 = var185;
               var36 = var10022;
               var35 = var10021;
               var34 = var10020;
               var33 = var10019;
               var32 = var10018;
               var31 = var178;
               var30 = var176;
               var29 = var10015;
               var28 = var10014;
               var27 = var172;
               var26 = var170;
               var24 = var168;
               var22 = var166;
               var20 = var164;
               var18 = var162;
               var17 = var160;
               var16 = var158;
               var15 = var156;
               var14 = var154;
               var13 = var10003;
               var12 = var10002;
               var11 = var10001;
               var10 = var152;
               int var9 = 0;
               AttachmentSettings var44 = AttachmentSettingsKt.sanitized((AttachmentSettings)GSON.fromJson(var8, AttachmentSettings.class));
               var152 = var10;
               var10001 = var11;
               var10002 = var12;
               var10003 = var13;
               var154 = var14;
               var156 = var15;
               var158 = var16;
               var160 = var17;
               var162 = var18;
               var164 = var20;
               var166 = var22;
               var168 = var24;
               var170 = var26;
               var172 = var27;
               var10014 = var28;
               var10015 = var29;
               var176 = var30;
               var178 = var31;
               var10018 = var32;
               var10019 = var33;
               var10020 = var34;
               var10021 = var35;
               var10022 = var36;
               var185 = var37;
               var187 = var38;
               var189 = var39;
               var191 = var40;
               var193 = var41;
               var195 = (ConfigModules)var42;
               var10029 = var43;
               var197 = var44;
               if (var44 != null) {
                  break label158;
               }
            }

            var197 = AttachmentSettings.Companion.getDEFAULT();
         }

         Boolean var10031 = (Boolean)var2.get(KEY_ARTICLE_AUTO_REACTION_ENABLED);
         boolean var198 = var10031 != null ? var10031 : true;
         ConversationStateSyncSettings var10032 = ConversationStateSyncSettings.Companion.getDEFAULT();
         Boolean var10033 = (Boolean)var2.get(KEY_CRASH_REPORTING_ENABLED);
         boolean var45 = var10033 != null ? var10033 : true;
         ConversationStateSyncSettings var46 = var10032;
         boolean var47 = var198;
         AttachmentSettings var48 = var197;
         NexusConfig var49 = var10029;
         ConfigModules var50 = var195;
         boolean var51 = var193;
         boolean var52 = var191;
         boolean var53 = var189;
         boolean var54 = var187;
         boolean var55 = var185;
         String var56 = var10022;
         String var57 = var10021;
         Set var58 = var10020;
         Set var59 = var10019;
         String var60 = var10018;
         boolean var61 = var178;
         boolean var62 = var176;
         String var63 = var10015;
         String var64 = var10014;
         boolean var65 = var172;
         boolean var66 = var170;
         long var67 = var168;
         long var69 = var166;
         long var71 = var164;
         long var73 = var162;
         int var75 = var160;
         boolean var76 = var158;
         boolean var77 = var156;
         boolean var78 = var154;
         int var79 = var10003;
         int var80 = var10002;
         int var81 = var10001;
         String var82 = var152;
         var3 = new AppConfig(
            var82,
            var81,
            var80,
            var79,
            var78,
            var77,
            var76,
            var75,
            var73,
            var71,
            var69,
            var67,
            var66,
            var65,
            var64,
            var63,
            var62,
            var61,
            var60,
            var59,
            var58,
            var57,
            var56,
            var55,
            var54,
            var53,
            var52,
            var51,
            var50,
            var49,
            var48,
            var47,
            var46,
            var45
         );
      } catch (Exception var84) {
         twig.w("Failed to load AppConfig from DataStore, returning defaults: " + var84.getMessage());
         var3 = AppConfigStore$Companion.access$defaultAppConfig(Companion, var1);
      }

      return var3;
   }

   public final void setAppConfig(@NotNull AppConfig var1) {
      Intrinsics.checkNotNullParameter(var1, "config");
      BuildersKt.launch$default(this.scope, null, null, new AppConfigStore$setAppConfig$1(this, var1, null), 3, null);
   }

   @Nullable
   public final String getSessionId() {
      String var10000 = this.generatedSessionId;
      if (this.generatedSessionId == null) {
         Preferences var1 = this.prefsSnapshot();
         var10000 = var1 != null ? (String)var1.get(KEY_SESSION_ID) : null;
      }

      return var10000;
   }

   @NotNull
   public final String generateSessionId() {
      String var10000 = UUID.randomUUID().toString();
      Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
      String var1 = var10000;
      this.generatedSessionId = var1;
      BuildersKt.launch$default(this.scope, null, null, new AppConfigStore$generateSessionId$1(this, var1, null), 3, null);
      return var1;
   }

   public final boolean getPushNotificationsBannerDismissed() {
      Preferences var10000 = this.prefsSnapshot();
      if (var10000 != null) {
         Boolean var1 = (Boolean)var10000.get(KEY_PUSH_NOTIFICATIONS_BANNER_DISMISSED);
         if (var1 != null) {
            return var1;
         }
      }

      return false;
   }

   public final void setPushNotificationsBannerDismissed(boolean var1) {
      BuildersKt.launch$default(this.scope, null, null, new AppConfigStore$setPushNotificationsBannerDismissed$1(this, var1, null), 3, null);
   }

   public final void clear() {
      this.generatedSessionId = null;
      BuildersKt.launch$default(this.scope, null, null, new AppConfigStore$clear$1(this, null), 3, null);
   }

   private static final Unit _init_$lambda$0(AppConfigStore var0, Throwable var1) {
      var0.firstLoad.complete(Unit.INSTANCE);
      return Unit.INSTANCE;
   }

   private static final List appConfigDataStore_delegate$lambda$5(Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return CollectionsKt.listOf(Companion.createSharedPrefsMigration$intercom_sdk_base_release(var0));
   }

   @JvmStatic
   @NotNull
   public static final AppConfigStore create(@NotNull Context var0, @NotNull CoroutineScope var1) {
      return Companion.create(var0, var1);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public static final AppConfigStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var0, @NotNull CoroutineScope var1, @NotNull CoroutineDispatcher var2
   ) {
      return Companion.createForTesting$intercom_sdk_base_release(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final CompletableDeferred access$getFirstLoad$p(AppConfigStore var0) {
      return var0.firstLoad;
   }

   // $VF: synthetic method
   public static final DataStore access$getDataStore$p(AppConfigStore var0) {
      return var0.dataStore;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_APP_NAME$cp() {
      return KEY_APP_NAME;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_APP_LOCALE$cp() {
      return KEY_APP_LOCALE;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_HELP_CENTER_LOCALE$cp() {
      return KEY_HELP_CENTER_LOCALE;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_LAUNCHER_LOGO_URL$cp() {
      return KEY_LAUNCHER_LOGO_URL;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_TEAM_GREETING$cp() {
      return KEY_TEAM_GREETING;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_PRIMARY_COLOR$cp() {
      return KEY_PRIMARY_COLOR;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_SECONDARY_COLOR$cp() {
      return KEY_SECONDARY_COLOR;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_RATE_LIMIT_COUNT$cp() {
      return KEY_RATE_LIMIT_COUNT;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_USER_UPDATE_CACHE_MAX_AGE_MS$cp() {
      return KEY_USER_UPDATE_CACHE_MAX_AGE_MS;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_RATE_LIMIT_PERIOD_MS$cp() {
      return KEY_RATE_LIMIT_PERIOD_MS;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_NEW_SESSION_THRESHOLD_MS$cp() {
      return KEY_NEW_SESSION_THRESHOLD_MS;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_SOFT_RESET_TIMEOUT_MS$cp() {
      return KEY_SOFT_RESET_TIMEOUT_MS;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_INBOUND_MESSAGES$cp() {
      return KEY_INBOUND_MESSAGES;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_AUDIO_ENABLED$cp() {
      return KEY_AUDIO_ENABLED;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_METRICS_ENABLED$cp() {
      return KEY_METRICS_ENABLED;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_RECEIVED_FROM_SERVER$cp() {
      return KEY_RECEIVED_FROM_SERVER;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_BACKGROUND_REQUESTS_ENABLED$cp() {
      return KEY_BACKGROUND_REQUESTS_ENABLED;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_PRIMARY_COLOR_RENDER_DARK_TEXT$cp() {
      return KEY_PRIMARY_COLOR_RENDER_DARK_TEXT;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_SECONDARY_COLOR_RENDER_DARK_TEXT$cp() {
      return KEY_SECONDARY_COLOR_RENDER_DARK_TEXT;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_HELP_CENTER_URL$cp() {
      return KEY_HELP_CENTER_URL;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_HELP_CENTER_URLS$cp() {
      return KEY_HELP_CENTER_URLS;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_FEATURES$cp() {
      return KEY_FEATURES;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_ACCESS_TEAMMATE_ENABLED$cp() {
      return KEY_ACCESS_TEAMMATE_ENABLED;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_HELP_CENTER_REQUIRE_SEARCH$cp() {
      return KEY_HELP_CENTER_REQUIRE_SEARCH;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_PREVENT_MULTIPLE_INBOUND_CONVERSATIONS$cp() {
      return KEY_PREVENT_MULTIPLE_INBOUND_CONVERSATIONS;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_HAS_OPEN_CONVERSATIONS$cp() {
      return KEY_HAS_OPEN_CONVERSATIONS;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_HUB_CONFIG$cp() {
      return KEY_HUB_CONFIG;
   }

   // $VF: synthetic method
   public static final Gson access$getGSON$cp() {
      return GSON;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_ATTACHMENT_SETTINGS$cp() {
      return KEY_ATTACHMENT_SETTINGS;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_ARTICLE_AUTO_REACTION_ENABLED$cp() {
      return KEY_ARTICLE_AUTO_REACTION_ENABLED;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_IDENTITY_VERIFICATION_ENABLED$cp() {
      return KEY_IDENTITY_VERIFICATION_ENABLED;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_CRASH_REPORTING_ENABLED$cp() {
      return KEY_CRASH_REPORTING_ENABLED;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_SESSION_ID$cp() {
      return KEY_SESSION_ID;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_PUSH_NOTIFICATIONS_BANNER_DISMISSED$cp() {
      return KEY_PUSH_NOTIFICATIONS_BANNER_DISMISSED;
   }

   // $VF: synthetic method
   public AppConfigStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, DefaultConstructorMarker var4) {
      this(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final ReadOnlyProperty access$getAppConfigDataStore$delegate$cp() {
      return appConfigDataStore$delegate;
   }

   // $VF: synthetic method
   public static final Set access$getALL_MIGRATION_KEY_NAMES$cp() {
      return ALL_MIGRATION_KEY_NAMES;
   }

   // $VF: synthetic method
   public static final Map access$getSTRING_MIGRATION_KEYS$cp() {
      return STRING_MIGRATION_KEYS;
   }

   // $VF: synthetic method
   public static final Map access$getINT_MIGRATION_KEYS$cp() {
      return INT_MIGRATION_KEYS;
   }

   // $VF: synthetic method
   public static final Map access$getLONG_MIGRATION_KEYS$cp() {
      return LONG_MIGRATION_KEYS;
   }

   // $VF: synthetic method
   public static final Map access$getBOOLEAN_MIGRATION_KEYS$cp() {
      return BOOLEAN_MIGRATION_KEYS;
   }

   // $VF: synthetic method
   public static final Map access$getSTRING_SET_MIGRATION_KEYS$cp() {
      return STRING_SET_MIGRATION_KEYS;
   }

   // $VF: synthetic method
   public static final void access$setCachedPrefs$p(AppConfigStore var0, Preferences var1) {
      var0.cachedPrefs = var1;
   }

   // $VF: synthetic method
   public static final Twig access$getTwig$cp() {
      return twig;
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
      Pair[] var0 = new Pair[]{
         TuplesKt.to("app_name", KEY_APP_NAME),
         TuplesKt.to("app_locale", KEY_APP_LOCALE),
         TuplesKt.to("help_center_locale", KEY_HELP_CENTER_LOCALE),
         TuplesKt.to("app_launcher_logo_url", KEY_LAUNCHER_LOGO_URL),
         TuplesKt.to("app_team_greeting", KEY_TEAM_GREETING),
         TuplesKt.to("help_center_url", KEY_HELP_CENTER_URL),
         TuplesKt.to("hub_config", KEY_HUB_CONFIG),
         TuplesKt.to("app_attachment_settings", KEY_ATTACHMENT_SETTINGS),
         TuplesKt.to("session_id", KEY_SESSION_ID)
      };
      STRING_MIGRATION_KEYS = MapsKt.mapOf(var0);
      var0 = new Pair[]{
         TuplesKt.to("app_primary_color", KEY_PRIMARY_COLOR),
         TuplesKt.to("app_secondary_color", KEY_SECONDARY_COLOR),
         TuplesKt.to("app_rate_limit_count", KEY_RATE_LIMIT_COUNT)
      };
      INT_MIGRATION_KEYS = MapsKt.mapOf(var0);
      var0 = new Pair[]{
         TuplesKt.to("app_user_update_cache_max_age_ms", KEY_USER_UPDATE_CACHE_MAX_AGE_MS),
         TuplesKt.to("app_rate_limit_period_ms", KEY_RATE_LIMIT_PERIOD_MS),
         TuplesKt.to("app_soft_reset_timeout_ms", KEY_SOFT_RESET_TIMEOUT_MS),
         TuplesKt.to("new_session_threshold_ms", KEY_NEW_SESSION_THRESHOLD_MS)
      };
      LONG_MIGRATION_KEYS = MapsKt.mapOf(var0);
      var0 = new Pair[]{
         TuplesKt.to("app_inbound_messages", KEY_INBOUND_MESSAGES),
         TuplesKt.to("app_primary_color_render_dark_text", KEY_PRIMARY_COLOR_RENDER_DARK_TEXT),
         TuplesKt.to("app_secondary_color_render_dark_text", KEY_SECONDARY_COLOR_RENDER_DARK_TEXT),
         TuplesKt.to("app_audio_enabled", KEY_AUDIO_ENABLED),
         TuplesKt.to("app_metrics_enabled", KEY_METRICS_ENABLED),
         TuplesKt.to("app_received_from_server", KEY_RECEIVED_FROM_SERVER),
         TuplesKt.to("app_background_requests_enabled", KEY_BACKGROUND_REQUESTS_ENABLED),
         TuplesKt.to("access_to_teammate_enabled", KEY_ACCESS_TEAMMATE_ENABLED),
         TuplesKt.to("help_center_require_search", KEY_HELP_CENTER_REQUIRE_SEARCH),
         TuplesKt.to("prevent_multiple_inbound_conversations_enabled", KEY_PREVENT_MULTIPLE_INBOUND_CONVERSATIONS),
         TuplesKt.to("has_open_conversations", KEY_HAS_OPEN_CONVERSATIONS),
         TuplesKt.to("article_auto_reaction_enabled", KEY_ARTICLE_AUTO_REACTION_ENABLED),
         TuplesKt.to("app_identity_verification_enabled", KEY_IDENTITY_VERIFICATION_ENABLED),
         TuplesKt.to("push_notifications_banner_dismissed", KEY_PUSH_NOTIFICATIONS_BANNER_DISMISSED)
      };
      BOOLEAN_MIGRATION_KEYS = MapsKt.mapOf(var0);
      var0 = new Pair[]{TuplesKt.to("help_center_urls", KEY_HELP_CENTER_URLS), TuplesKt.to("features", KEY_FEATURES)};
      STRING_SET_MIGRATION_KEYS = MapsKt.mapOf(var0);
      ALL_MIGRATION_KEY_NAMES = SetsKt.plus(
         SetsKt.plus(
            SetsKt.plus(SetsKt.plus(STRING_MIGRATION_KEYS.keySet(), INT_MIGRATION_KEYS.keySet()), LONG_MIGRATION_KEYS.keySet()),
            BOOLEAN_MIGRATION_KEYS.keySet()
         ),
         STRING_SET_MIGRATION_KEYS.keySet()
      );
   }
}

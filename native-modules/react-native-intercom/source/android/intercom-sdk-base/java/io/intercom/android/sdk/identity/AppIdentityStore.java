package io.intercom.android.sdk.identity;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.core.Preferences.Key;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.MultiProcessDataStoreDelegateKt;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.properties.ReadOnlyProperty;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0001\u0018\u0000 \u00102\u00020\u0001:\u0002\u000f\u0010B\u001f\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fJ\u0006\u0010\u000e\u001a\u00020\nR\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/identity/AppIdentityStore;",
         "",
         "dataStore",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "<init>",
         "(Landroidx/datastore/core/DataStore;Lkotlinx/coroutines/CoroutineScope;)V",
         "saveAppCredentials",
         "",
         "apiKey",
         "",
         "appId",
         "clear",
         "AppCredentials",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AppIdentityStore {
   @NotNull
   public static final AppIdentityStore$Companion Companion = new AppIdentityStore$Companion(null);
   @NotNull
   private final DataStore dataStore;
   @NotNull
   private final CoroutineScope scope;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String OLD_PREFS_NAME = "INTERCOM_SDK_PREFS";
   @NotNull
   private static final Key KEY_API_KEY = PreferencesKeys.stringKey("api_key");
   @NotNull
   private static final Key KEY_APP_ID = PreferencesKeys.stringKey("app_id");
   @NotNull
   private static final Map MIGRATION_KEY_MAP;
   @NotNull
   private static final ReadOnlyProperty appDataStore$delegate = MultiProcessDataStoreDelegateKt.multiProcessPreferencesDataStore$default(
      "intercom_app_prefs_datastore", null, ((kotlin.jvm.functions.Function1) (recoveredArgument_122_0) -> AppIdentityStore.appDataStore_delegate$lambda$0((android.content.Context)recoveredArgument_122_0)), 2, null
   );
   @NotNull
   private static final Twig twig;

   private AppIdentityStore(DataStore var1, CoroutineScope var2) {
      this.dataStore = var1;
      this.scope = var2;
   }

   public final void saveAppCredentials(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "apiKey");
      Intrinsics.checkNotNullParameter(var2, "appId");
      BuildersKt.launch$default(this.scope, null, null, new AppIdentityStore$saveAppCredentials$1(this, var1, var2, null), 3, null);
   }

   public final void clear() {
      BuildersKt.launch$default(this.scope, null, null, new AppIdentityStore$clear$1(this, null), 3, null);
   }

   private static final List appDataStore_delegate$lambda$0(Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return CollectionsKt.listOf(Companion.createSharedPrefsMigration$intercom_sdk_base_release(var0));
   }

   @JvmStatic
   @NotNull
   public static final AppIdentityStore create(@NotNull Context var0, @NotNull CoroutineScope var1) {
      return Companion.create(var0, var1);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public static final AppIdentityStore createForTesting$intercom_sdk_base_release(@NotNull DataStore var0, @NotNull CoroutineScope var1) {
      return Companion.createForTesting$intercom_sdk_base_release(var0, var1);
   }

   @JvmStatic
   @NotNull
   public static final AppIdentityStore$AppCredentials loadCachedCredentials(@NotNull Context var0) {
      return Companion.loadCachedCredentials(var0);
   }

   // $VF: synthetic method
   public static final DataStore access$getDataStore$p(AppIdentityStore var0) {
      return var0.dataStore;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_API_KEY$cp() {
      return KEY_API_KEY;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_APP_ID$cp() {
      return KEY_APP_ID;
   }

   // $VF: synthetic method
   public AppIdentityStore(DataStore var1, CoroutineScope var2, DefaultConstructorMarker var3) {
      this(var1, var2);
   }

   // $VF: synthetic method
   public static final ReadOnlyProperty access$getAppDataStore$delegate$cp() {
      return appDataStore$delegate;
   }

   // $VF: synthetic method
   public static final Twig access$getTwig$cp() {
      return twig;
   }

   // $VF: synthetic method
   public static final Map access$getMIGRATION_KEY_MAP$cp() {
      return MIGRATION_KEY_MAP;
   }

   static {
      Pair[] var0 = new Pair[]{TuplesKt.to("ApiKey", KEY_API_KEY), TuplesKt.to("AppId", KEY_APP_ID)};
      MIGRATION_KEY_MAP = MapsKt.mapOf(var0);
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
   }
}

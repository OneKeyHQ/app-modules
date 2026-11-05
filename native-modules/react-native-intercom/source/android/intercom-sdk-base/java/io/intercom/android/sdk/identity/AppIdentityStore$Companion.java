package io.intercom.android.sdk.identity;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.datastore.core.DataMigration;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.Preferences.Key;
import io.intercom.android.sdk.utilities.CryptoHelper;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference2Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0007J#\u0010\n\u001a\u00020\u00052\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0002\b\u000eJ\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u001b\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\r0 2\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\b!J\u001e\u0010\"\u001a\u00020\u00102\u0006\u0010#\u001a\u00020\r2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00100\u0012H\u0002R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00100\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00100\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0014\u001a\u0014\u0012\u0004\u0012\u00020\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00120\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R%\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\r0\f*\u00020\u00078BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u0017\u0010\u0018R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006%",
   d2 = {
         "Lio/intercom/android/sdk/identity/AppIdentityStore$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/identity/AppIdentityStore;",
         "context",
         "Landroid/content/Context;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "createForTesting",
         "dataStore",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "createForTesting$intercom_sdk_base_release",
         "OLD_PREFS_NAME",
         "",
         "KEY_API_KEY",
         "Landroidx/datastore/preferences/core/Preferences$Key;",
         "KEY_APP_ID",
         "MIGRATION_KEY_MAP",
         "",
         "appDataStore",
         "getAppDataStore",
         "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;",
         "appDataStore$delegate",
         "Lkotlin/properties/ReadOnlyProperty;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "loadCachedCredentials",
         "Lio/intercom/android/sdk/identity/AppIdentityStore$AppCredentials;",
         "createSharedPrefsMigration",
         "Landroidx/datastore/core/DataMigration;",
         "createSharedPrefsMigration$intercom_sdk_base_release",
         "decryptField",
         "prefs",
         "key",
         "intercom-sdk-base_release"
   }
)
public final class AppIdentityStore$Companion {
   // $VF: synthetic field
   static final KProperty[] $$delegatedProperties;

   private AppIdentityStore$Companion() {
   }

   @JvmStatic
   @NotNull
   public final AppIdentityStore create(@NotNull Context var1, @NotNull CoroutineScope var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "scope");
      return new AppIdentityStore(this.getAppDataStore(var1), var2, null);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public final AppIdentityStore createForTesting$intercom_sdk_base_release(@NotNull DataStore var1, @NotNull CoroutineScope var2) {
      Intrinsics.checkNotNullParameter(var1, "dataStore");
      Intrinsics.checkNotNullParameter(var2, "scope");
      return new AppIdentityStore(var1, var2, null);
   }

   private final DataStore getAppDataStore(Context var1) {
      return (DataStore)AppIdentityStore.access$getAppDataStore$delegate$cp().getValue(var1, $$delegatedProperties[0]);
   }

   @JvmStatic
   @NotNull
   public final AppIdentityStore$AppCredentials loadCachedCredentials(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");

      AppIdentityStore$AppCredentials var2;
      try {
         var2 = (AppIdentityStore$AppCredentials)BuildersKt.runBlocking$default(
            null, new AppIdentityStore$Companion$loadCachedCredentials$1(var1, null), 1, null
         );
      } catch (Exception var4) {
         AppIdentityStore.access$getTwig$cp().w("Failed to load cached credentials from DataStore, returning defaults: " + var4.getMessage());
         var2 = new AppIdentityStore$AppCredentials(null, null, 3, null);
      }

      return var2;
   }

   @VisibleForTesting
   @NotNull
   public final DataMigration createSharedPrefsMigration$intercom_sdk_base_release(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      return new AppIdentityStore$Companion$createSharedPrefsMigration$1(var1);
   }

   private final String decryptField(Preferences var1, Key var2) {
      String var10000 = (String)var1.get(var2);
      if (var10000 == null) {
         return "";
      }

      String var3 = var10000;
      return CryptoHelper.INSTANCE.decrypt$intercom_sdk_base_release(var3);
   }

   // $VF: synthetic method
   public static final DataStore access$getAppDataStore(AppIdentityStore$Companion var0, Context var1) {
      return var0.getAppDataStore(var1);
   }

   // $VF: synthetic method
   public static final String access$decryptField(AppIdentityStore$Companion var0, Preferences var1, Key var2) {
      return var0.decryptField(var1, var2);
   }

   // $VF: synthetic method
   public AppIdentityStore$Companion(DefaultConstructorMarker var1) {
      this();
   }

   static {
      KProperty[] var0 = new KProperty[]{
         Reflection.property2(
            new PropertyReference2Impl(
               AppIdentityStore$Companion.class, "appDataStore", "getAppDataStore(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", 0
            )
         )
      };
      $$delegatedProperties = var0;
   }
}

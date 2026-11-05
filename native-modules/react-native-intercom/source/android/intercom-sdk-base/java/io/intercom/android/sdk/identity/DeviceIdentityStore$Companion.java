package io.intercom.android.sdk.identity;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.datastore.core.DataMigration;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences.Key;
import kotlin.Metadata;
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
   d1 = "\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007J5\u0010\f\u001a\u00020\u00072\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0001¢\u0006\u0002\b\u0012J\u001b\u0010 \u001a\b\u0012\u0004\u0012\u00020\u000f0!2\u0006\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0002\b\"R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0014X\u0082T¢\u0006\u0002\n\u0000R\"\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00140\u00178\u0000X\u0081\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0018\u0010\u0003\u001a\u0004\b\u0019\u0010\u001aR%\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e*\u00020\t8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\u001f\u001a\u0004\b\u001c\u0010\u001d¨\u0006#",
   d2 = {
         "Lio/intercom/android/sdk/identity/DeviceIdentityStore$Companion;",
         "",
         "<init>",
         "()V",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "create",
         "Lio/intercom/android/sdk/identity/DeviceIdentityStore;",
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
         "LEGACY_PREFS_NAME",
         "",
         "KEY_NAME",
         "KEY_DEVICE_IDENTIFIER",
         "Landroidx/datastore/preferences/core/Preferences$Key;",
         "getKEY_DEVICE_IDENTIFIER$intercom_sdk_base_release$annotations",
         "getKEY_DEVICE_IDENTIFIER$intercom_sdk_base_release",
         "()Landroidx/datastore/preferences/core/Preferences$Key;",
         "deviceDataStore",
         "getDeviceDataStore",
         "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;",
         "deviceDataStore$delegate",
         "Lkotlin/properties/ReadOnlyProperty;",
         "createSharedPrefsMigration",
         "Landroidx/datastore/core/DataMigration;",
         "createSharedPrefsMigration$intercom_sdk_base_release",
         "intercom-sdk-base_release"
   }
)
public final class DeviceIdentityStore$Companion {
   // $VF: synthetic field
   static final KProperty[] $$delegatedProperties;

   private DeviceIdentityStore$Companion() {
   }

   @JvmStatic
   @NotNull
   public final DeviceIdentityStore create(@NotNull Context var1, @NotNull CoroutineScope var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "scope");
      return new DeviceIdentityStore(this.getDeviceDataStore(var1), var1, var2, Dispatchers.getIO(), null);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public final DeviceIdentityStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var1, @NotNull Context var2, @NotNull CoroutineScope var3, @NotNull CoroutineDispatcher var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "dataStore");
      Intrinsics.checkNotNullParameter(var2, "context");
      Intrinsics.checkNotNullParameter(var3, "scope");
      Intrinsics.checkNotNullParameter(var4, "ioDispatcher");
      return new DeviceIdentityStore(var1, var2, var3, var4, null);
   }

   // $VF: synthetic method
   public static DeviceIdentityStore createForTesting$intercom_sdk_base_release$default(
      DeviceIdentityStore$Companion var0, DataStore var1, Context var2, CoroutineScope var3, CoroutineDispatcher var4, int var5, Object var6
   ) {
      if ((var5 & 8) != 0) {
         var4 = Dispatchers.getIO();
      }

      return var0.createForTesting$intercom_sdk_base_release(var1, var2, var3, var4);
   }

   @NotNull
   public final Key getKEY_DEVICE_IDENTIFIER$intercom_sdk_base_release() {
      return DeviceIdentityStore.access$getKEY_DEVICE_IDENTIFIER$cp();
   }

   /** @deprecated */
   // $VF: synthetic method
   @VisibleForTesting
   public static void getKEY_DEVICE_IDENTIFIER$intercom_sdk_base_release$annotations() {
   }

   private final DataStore getDeviceDataStore(Context var1) {
      return (DataStore)DeviceIdentityStore.access$getDeviceDataStore$delegate$cp().getValue(var1, $$delegatedProperties[0]);
   }

   @VisibleForTesting
   @NotNull
   public final DataMigration createSharedPrefsMigration$intercom_sdk_base_release(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      return new DeviceIdentityStore$Companion$createSharedPrefsMigration$1(var1);
   }

   // $VF: synthetic method
   public DeviceIdentityStore$Companion(DefaultConstructorMarker var1) {
      this();
   }

   static {
      KProperty[] var0 = new KProperty[]{
         Reflection.property2(
            new PropertyReference2Impl(
               DeviceIdentityStore$Companion.class, "deviceDataStore", "getDeviceDataStore(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", 0
            )
         )
      };
      $$delegatedProperties = var0;
   }
}

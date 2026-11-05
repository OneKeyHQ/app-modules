package io.intercom.android.sdk.api;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.datastore.core.DataMigration;
import androidx.datastore.core.DataStore;
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
   d1 = "\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u001b\u001a\u00020\u001cH\u0007J-\u0010\u001d\u001a\u00020\u00192\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u001b\u001a\u00020\u001c2\b\b\u0002\u0010\u001f\u001a\u00020 H\u0001¢\u0006\u0002\b!J\u001b\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00100#2\u0006\u0010\u001a\u001a\u00020\u0011H\u0001¢\u0006\u0002\b$R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\nX\u0082\u0004¢\u0006\u0002\n\u0000R%\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f*\u00020\u00118BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0014\u0010\u0015\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006%",
   d2 = {
         "Lio/intercom/android/sdk/api/ShutdownStore$Companion;",
         "",
         "<init>",
         "()V",
         "OLD_PREFS_NAME",
         "",
         "OLD_FINGERPRINT_KEY",
         "OLD_EXPIRY_KEY",
         "OLD_REASON_KEY",
         "KEY_SHUTDOWN_FINGERPRINT",
         "Landroidx/datastore/preferences/core/Preferences$Key;",
         "KEY_SHUTDOWN_EXPIRY",
         "",
         "KEY_SHUTDOWN_REASON",
         "shutdownDataStore",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "Landroid/content/Context;",
         "getShutdownDataStore",
         "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;",
         "shutdownDataStore$delegate",
         "Lkotlin/properties/ReadOnlyProperty;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "create",
         "Lio/intercom/android/sdk/api/ShutdownStore;",
         "context",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "createForTesting",
         "dataStore",
         "ioDispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "createForTesting$intercom_sdk_base_release",
         "createSharedPrefsMigration",
         "Landroidx/datastore/core/DataMigration;",
         "createSharedPrefsMigration$intercom_sdk_base_release",
         "intercom-sdk-base_release"
   }
)
public final class ShutdownStore$Companion {
   // $VF: synthetic field
   static final KProperty[] $$delegatedProperties;

   private ShutdownStore$Companion() {
   }

   private final DataStore getShutdownDataStore(Context var1) {
      return (DataStore)ShutdownStore.access$getShutdownDataStore$delegate$cp().getValue(var1, $$delegatedProperties[0]);
   }

   @JvmStatic
   @NotNull
   public final ShutdownStore create(@NotNull Context var1, @NotNull CoroutineScope var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "scope");
      return new ShutdownStore(this.getShutdownDataStore(var1), var2, Dispatchers.getIO(), null);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public final ShutdownStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var1, @NotNull CoroutineScope var2, @NotNull CoroutineDispatcher var3
   ) {
      Intrinsics.checkNotNullParameter(var1, "dataStore");
      Intrinsics.checkNotNullParameter(var2, "scope");
      Intrinsics.checkNotNullParameter(var3, "ioDispatcher");
      return new ShutdownStore(var1, var2, var3, null);
   }

   // $VF: synthetic method
   public static ShutdownStore createForTesting$intercom_sdk_base_release$default(
      ShutdownStore$Companion var0, DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, int var4, Object var5
   ) {
      if ((var4 & 4) != 0) {
         var3 = Dispatchers.getIO();
      }

      return var0.createForTesting$intercom_sdk_base_release(var1, var2, var3);
   }

   @VisibleForTesting
   @NotNull
   public final DataMigration createSharedPrefsMigration$intercom_sdk_base_release(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      return new ShutdownStore$Companion$createSharedPrefsMigration$1(var1);
   }

   // $VF: synthetic method
   public ShutdownStore$Companion(DefaultConstructorMarker var1) {
      this();
   }

   static {
      KProperty[] var0 = new KProperty[]{
         Reflection.property2(
            new PropertyReference2Impl(
               ShutdownStore$Companion.class, "shutdownDataStore", "getShutdownDataStore(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", 0
            )
         )
      };
      $$delegatedProperties = var0;
   }
}

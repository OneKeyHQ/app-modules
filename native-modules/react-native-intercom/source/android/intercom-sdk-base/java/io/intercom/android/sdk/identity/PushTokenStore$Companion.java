package io.intercom.android.sdk.identity;

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
   d1 = "\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u0019H\u0007J-\u0010\u001a\u001a\u00020\u00162\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001c\u001a\u00020\u001dH\u0001¢\u0006\u0002\b\u001eJ\u001b\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\r0 2\u0006\u0010\u0017\u001a\u00020\u000eH\u0001¢\u0006\u0002\b!R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\bX\u0082\u0004¢\u0006\u0002\n\u0000R%\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f*\u00020\u000e8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/identity/PushTokenStore$Companion;",
         "",
         "<init>",
         "()V",
         "OLD_PREFS_NAME",
         "",
         "OLD_PUSH_TOKEN_KEY",
         "KEY_PUSH_TOKEN",
         "Landroidx/datastore/preferences/core/Preferences$Key;",
         "KEY_DEVICE_TOKEN_SUBMITTED",
         "",
         "pushTokenDataStore",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "Landroid/content/Context;",
         "getPushTokenDataStore",
         "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;",
         "pushTokenDataStore$delegate",
         "Lkotlin/properties/ReadOnlyProperty;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "create",
         "Lio/intercom/android/sdk/identity/PushTokenStore;",
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
public final class PushTokenStore$Companion {
   // $VF: synthetic field
   static final KProperty[] $$delegatedProperties;

   private PushTokenStore$Companion() {
   }

   private final DataStore getPushTokenDataStore(Context var1) {
      return (DataStore)PushTokenStore.access$getPushTokenDataStore$delegate$cp().getValue(var1, $$delegatedProperties[0]);
   }

   @JvmStatic
   @NotNull
   public final PushTokenStore create(@NotNull Context var1, @NotNull CoroutineScope var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "scope");
      return new PushTokenStore(this.getPushTokenDataStore(var1), var2, Dispatchers.getIO(), null);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public final PushTokenStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var1, @NotNull CoroutineScope var2, @NotNull CoroutineDispatcher var3
   ) {
      Intrinsics.checkNotNullParameter(var1, "dataStore");
      Intrinsics.checkNotNullParameter(var2, "scope");
      Intrinsics.checkNotNullParameter(var3, "ioDispatcher");
      return new PushTokenStore(var1, var2, var3, null);
   }

   // $VF: synthetic method
   public static PushTokenStore createForTesting$intercom_sdk_base_release$default(
      PushTokenStore$Companion var0, DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, int var4, Object var5
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
      return new PushTokenStore$Companion$createSharedPrefsMigration$1(var1);
   }

   // $VF: synthetic method
   public PushTokenStore$Companion(DefaultConstructorMarker var1) {
      this();
   }

   static {
      KProperty[] var0 = new KProperty[]{
         Reflection.property2(
            new PropertyReference2Impl(
               PushTokenStore$Companion.class, "pushTokenDataStore", "getPushTokenDataStore(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", 0
            )
         )
      };
      $$delegatedProperties = var0;
   }
}

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
   d1 = "\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010$\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0007J-\u0010\n\u001a\u00020\u00052\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0001¢\u0006\u0002\b\u0010J\u001b\u0010#\u001a\b\u0012\u0004\u0012\u00020\r0$2\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\b%R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0012X\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00120\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00120\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00120\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00120\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00120\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00120\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00120\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u001c\u001a\u0014\u0012\u0004\u0012\u00020\u0012\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u00150\u001dX\u0082\u0004¢\u0006\u0002\n\u0000R%\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\r0\f*\u00020\u00078BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b!\u0010\"\u001a\u0004\b\u001f\u0010 ¨\u0006&",
   d2 = {
         "Lio/intercom/android/sdk/identity/UserIdentityStore$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/identity/UserIdentityStore;",
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
         "OLD_KEY_PREFIX",
         "KEY_ANONYMOUS_ID",
         "Landroidx/datastore/preferences/core/Preferences$Key;",
         "KEY_INTERCOM_ID",
         "KEY_USER_ID",
         "KEY_EMAIL",
         "KEY_HMAC",
         "KEY_JWT",
         "KEY_MESSENGER_ENVIRONMENT",
         "MIGRATION_KEY_MAP",
         "",
         "userDataStore",
         "getUserDataStore",
         "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;",
         "userDataStore$delegate",
         "Lkotlin/properties/ReadOnlyProperty;",
         "createSharedPrefsMigration",
         "Landroidx/datastore/core/DataMigration;",
         "createSharedPrefsMigration$intercom_sdk_base_release",
         "intercom-sdk-base_release"
   }
)
public final class UserIdentityStore$Companion {
   // $VF: synthetic field
   static final KProperty[] $$delegatedProperties;

   private UserIdentityStore$Companion() {
   }

   @JvmStatic
   @NotNull
   public final UserIdentityStore create(@NotNull Context var1, @NotNull CoroutineScope var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "scope");
      return new UserIdentityStore(this.getUserDataStore(var1), var2, Dispatchers.getIO(), null);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public final UserIdentityStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var1, @NotNull CoroutineScope var2, @NotNull CoroutineDispatcher var3
   ) {
      Intrinsics.checkNotNullParameter(var1, "dataStore");
      Intrinsics.checkNotNullParameter(var2, "scope");
      Intrinsics.checkNotNullParameter(var3, "ioDispatcher");
      return new UserIdentityStore(var1, var2, var3, null);
   }

   // $VF: synthetic method
   public static UserIdentityStore createForTesting$intercom_sdk_base_release$default(
      UserIdentityStore$Companion var0, DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, int var4, Object var5
   ) {
      if ((var4 & 4) != 0) {
         var3 = Dispatchers.getIO();
      }

      return var0.createForTesting$intercom_sdk_base_release(var1, var2, var3);
   }

   private final DataStore getUserDataStore(Context var1) {
      return (DataStore)UserIdentityStore.access$getUserDataStore$delegate$cp().getValue(var1, $$delegatedProperties[0]);
   }

   @VisibleForTesting
   @NotNull
   public final DataMigration createSharedPrefsMigration$intercom_sdk_base_release(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      return new UserIdentityStore$Companion$createSharedPrefsMigration$1(var1);
   }

   // $VF: synthetic method
   public UserIdentityStore$Companion(DefaultConstructorMarker var1) {
      this();
   }

   static {
      KProperty[] var0 = new KProperty[]{
         Reflection.property2(
            new PropertyReference2Impl(
               UserIdentityStore$Companion.class, "userDataStore", "getUserDataStore(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", 0
            )
         )
      };
      $$delegatedProperties = var0;
   }
}

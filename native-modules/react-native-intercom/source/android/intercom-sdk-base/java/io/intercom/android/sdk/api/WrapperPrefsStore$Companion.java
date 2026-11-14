package io.intercom.android.sdk.api;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.datastore.core.DataMigration;
import androidx.datastore.core.DataStore;
import io.intercom.android.sdk.utilities.CoroutineExceptionHandlersKt;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference2Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.SupervisorKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u001b\u001a\u00020\u000fH\u0007J-\u0010\u001c\u001a\u00020\u00172\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u001e\u001a\u00020\u00192\b\b\u0002\u0010\u001f\u001a\u00020 H\u0001¢\u0006\u0002\b!J\r\u0010\"\u001a\u00020#H\u0001¢\u0006\u0002\b$J\u001b\u0010%\u001a\b\u0012\u0004\u0012\u00020\u000e0&2\u0006\u0010\u001b\u001a\u00020\u000fH\u0001¢\u0006\u0002\b'R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\tX\u0082\u0004¢\u0006\u0002\n\u0000R%\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r*\u00020\u000f8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006(",
   d2 = {
         "Lio/intercom/android/sdk/api/WrapperPrefsStore$Companion;",
         "",
         "<init>",
         "()V",
         "OLD_PREFS_NAME",
         "",
         "OLD_CORDOVA_VERSION_KEY",
         "OLD_REACT_NATIVE_VERSION_KEY",
         "KEY_CORDOVA_VERSION",
         "Landroidx/datastore/preferences/core/Preferences$Key;",
         "KEY_REACT_NATIVE_VERSION",
         "KEY_UNITY_VERSION",
         "wrapperPrefsDataStore",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "Landroid/content/Context;",
         "getWrapperPrefsDataStore",
         "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;",
         "wrapperPrefsDataStore$delegate",
         "Lkotlin/properties/ReadOnlyProperty;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "instance",
         "Lio/intercom/android/sdk/api/WrapperPrefsStore;",
         "ownedScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "create",
         "context",
         "createForTesting",
         "dataStore",
         "scope",
         "ioDispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "createForTesting$intercom_sdk_base_release",
         "resetForTesting",
         "",
         "resetForTesting$intercom_sdk_base_release",
         "createSharedPrefsMigration",
         "Landroidx/datastore/core/DataMigration;",
         "createSharedPrefsMigration$intercom_sdk_base_release",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nWrapperPrefsStore.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WrapperPrefsStore.kt\nio/intercom/android/sdk/api/WrapperPrefsStore$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,265:1\n1#2:266\n*E\n"
)
public final class WrapperPrefsStore$Companion {
   // $VF: synthetic field
   static final KProperty[] $$delegatedProperties;

   private WrapperPrefsStore$Companion() {
   }

   private final DataStore getWrapperPrefsDataStore(Context var1) {
      return (DataStore)WrapperPrefsStore.access$getWrapperPrefsDataStore$delegate$cp().getValue(var1, $$delegatedProperties[0]);
   }

   // $VF: Extended synchronized range to monitorexit
   @JvmStatic
   @NotNull
   public final WrapperPrefsStore create(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      WrapperPrefsStore var10000 = WrapperPrefsStore.access$getInstance$cp();
      if (var10000 == null) {
         synchronized (this) {
            int var3 = 0;
            var10000 = WrapperPrefsStore.access$getInstance$cp();
            if (var10000 == null) {
               WrapperPrefsStore$Companion var4 = WrapperPrefsStore.Companion;
               int var5 = 0;
               CoroutineScope var6 = CoroutineScopeKt.CoroutineScope(
                  SupervisorKt.SupervisorJob$default(null, 1, null)
                     .plus(CoroutineExceptionHandlersKt.loggingCoroutineExceptionHandler("WrapperPrefsStore.scope"))
               );
               WrapperPrefsStore.access$setOwnedScope$cp(var6);
               WrapperPrefsStore var7 = new WrapperPrefsStore(var4.getWrapperPrefsDataStore(var1), var6, Dispatchers.getIO(), null);
               WrapperPrefsStore var8 = var7;
               int var9 = 0;
               WrapperPrefsStore.access$setInstance$cp(var8);
               var10000 = var7;
            }

            WrapperPrefsStore var11 = var10000;
            var10000 = var11;
         }
      }

      return var10000;
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public final WrapperPrefsStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var1, @NotNull CoroutineScope var2, @NotNull CoroutineDispatcher var3
   ) {
      Intrinsics.checkNotNullParameter(var1, "dataStore");
      Intrinsics.checkNotNullParameter(var2, "scope");
      Intrinsics.checkNotNullParameter(var3, "ioDispatcher");
      return new WrapperPrefsStore(var1, var2, var3, null);
   }

   // $VF: synthetic method
   public static WrapperPrefsStore createForTesting$intercom_sdk_base_release$default(
      WrapperPrefsStore$Companion var0, DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, int var4, Object var5
   ) {
      if ((var4 & 4) != 0) {
         var3 = Dispatchers.getIO();
      }

      return var0.createForTesting$intercom_sdk_base_release(var1, var2, var3);
   }

   @VisibleForTesting
   public final void resetForTesting$intercom_sdk_base_release() {
      WrapperPrefsStore.access$setInstance$cp(null);
      CoroutineScope var10000 = WrapperPrefsStore.access$getOwnedScope$cp();
      if (var10000 != null) {
         CoroutineScopeKt.cancel$default(var10000, null, 1, null);
      }

      WrapperPrefsStore.access$setOwnedScope$cp(null);
   }

   @VisibleForTesting
   @NotNull
   public final DataMigration createSharedPrefsMigration$intercom_sdk_base_release(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      return new WrapperPrefsStore$Companion$createSharedPrefsMigration$1(var1);
   }

   // $VF: synthetic method
   public WrapperPrefsStore$Companion(DefaultConstructorMarker var1) {
      this();
   }

   static {
      KProperty[] var0 = new KProperty[]{
         Reflection.property2(
            new PropertyReference2Impl(
               WrapperPrefsStore$Companion.class,
               "wrapperPrefsDataStore",
               "getWrapperPrefsDataStore(Landroid/content/Context;)Landroidx/datastore/core/DataStore;",
               0
            )
         )
      };
      $$delegatedProperties = var0;
   }
}

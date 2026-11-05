package io.intercom.android.sdk.api;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.core.Preferences.Key;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.MultiProcessDataStoreDelegateKt;
import io.intercom.android.sdk.utilities.PreloadedValue;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.properties.ReadOnlyProperty;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0001\u0018\u0000 \u001b2\u00020\u0001:\u0002\u001a\u001bB'\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fJ\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0010\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\u000fJ\u001e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u000fJ\u0006\u0010\u0019\u001a\u00020\u0015R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/api/ShutdownStore;",
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
         "fields",
         "Lio/intercom/android/sdk/utilities/PreloadedValue;",
         "Lio/intercom/android/sdk/api/ShutdownStore$ShutdownFields;",
         "getShutdownFingerprint",
         "",
         "default",
         "getShutdownExpiry",
         "",
         "getShutdownReason",
         "save",
         "",
         "fingerprint",
         "expiry",
         "reason",
         "clear",
         "ShutdownFields",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ShutdownStore {
   @NotNull
   public static final ShutdownStore$Companion Companion = new ShutdownStore$Companion(null);
   @NotNull
   private final DataStore dataStore;
   @NotNull
   private final CoroutineScope scope;
   @NotNull
   private final PreloadedValue fields;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String OLD_PREFS_NAME = "INTERCOM_SHUTDOWN_PREFS";
   @NotNull
   private static final String OLD_FINGERPRINT_KEY = "ShutdownFingerprint";
   @NotNull
   private static final String OLD_EXPIRY_KEY = "ShutdownExpiry";
   @NotNull
   private static final String OLD_REASON_KEY = "ShutdownReason";
   @NotNull
   private static final Key KEY_SHUTDOWN_FINGERPRINT = PreferencesKeys.stringKey("shutdown_fingerprint");
   @NotNull
   private static final Key KEY_SHUTDOWN_EXPIRY = PreferencesKeys.longKey("shutdown_expiry");
   @NotNull
   private static final Key KEY_SHUTDOWN_REASON = PreferencesKeys.stringKey("shutdown_reason");
   @NotNull
   private static final ReadOnlyProperty shutdownDataStore$delegate = MultiProcessDataStoreDelegateKt.multiProcessPreferencesDataStore$default(
      "intercom_shutdown_datastore", null, ((kotlin.jvm.functions.Function1) (recoveredArgument_52_0) -> ShutdownStore.shutdownDataStore_delegate$lambda$1((android.content.Context)recoveredArgument_52_0)), 2, null
   );
   @NotNull
   private static final Twig twig;

   private ShutdownStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3) {
      this.dataStore = var1;
      this.scope = var2;
      this.fields = new PreloadedValue(this.scope, var3, ((kotlin.jvm.functions.Function0) () -> ShutdownStore.fields$lambda$0()), new ShutdownStore$fields$2(this, null));
   }

   @NotNull
   public final String getShutdownFingerprint(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "default");
      String var10000 = ((ShutdownStore$ShutdownFields)this.fields.get$intercom_sdk_base_release()).getFingerprint();
      if (var10000 == null) {
         var10000 = var1;
      }

      return var10000;
   }

   public final long getShutdownExpiry(long var1) {
      Long var10000 = ((ShutdownStore$ShutdownFields)this.fields.get$intercom_sdk_base_release()).getExpiry();
      return var10000 != null ? var10000 : var1;
   }

   @NotNull
   public final String getShutdownReason() {
      String var10000 = ((ShutdownStore$ShutdownFields)this.fields.get$intercom_sdk_base_release()).getReason();
      if (var10000 == null) {
         var10000 = "";
      }

      return var10000;
   }

   public final void save(@NotNull String var1, long var2, @NotNull String var4) {
      Intrinsics.checkNotNullParameter(var1, "fingerprint");
      Intrinsics.checkNotNullParameter(var4, "reason");
      this.fields.set$intercom_sdk_base_release(new ShutdownStore$ShutdownFields(var1, var2, var4));
      BuildersKt.launch$default(this.scope, null, null, new ShutdownStore$save$1(this, var1, var2, var4, null), 3, null);
   }

   public final void clear() {
      this.fields.set$intercom_sdk_base_release(new ShutdownStore$ShutdownFields(null, null, null, 7, null));
      BuildersKt.launch$default(this.scope, null, null, new ShutdownStore$clear$1(this, null), 3, null);
   }

   private static final ShutdownStore$ShutdownFields fields$lambda$0() {
      return new ShutdownStore$ShutdownFields(null, null, null, 7, null);
   }

   private static final List shutdownDataStore_delegate$lambda$1(Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return CollectionsKt.listOf(Companion.createSharedPrefsMigration$intercom_sdk_base_release(var0));
   }

   @JvmStatic
   @NotNull
   public static final ShutdownStore create(@NotNull Context var0, @NotNull CoroutineScope var1) {
      return Companion.create(var0, var1);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public static final ShutdownStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var0, @NotNull CoroutineScope var1, @NotNull CoroutineDispatcher var2
   ) {
      return Companion.createForTesting$intercom_sdk_base_release(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final DataStore access$getDataStore$p(ShutdownStore var0) {
      return var0.dataStore;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_SHUTDOWN_FINGERPRINT$cp() {
      return KEY_SHUTDOWN_FINGERPRINT;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_SHUTDOWN_EXPIRY$cp() {
      return KEY_SHUTDOWN_EXPIRY;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_SHUTDOWN_REASON$cp() {
      return KEY_SHUTDOWN_REASON;
   }

   // $VF: synthetic method
   public static final ReadOnlyProperty access$getShutdownDataStore$delegate$cp() {
      return shutdownDataStore$delegate;
   }

   // $VF: synthetic method
   public ShutdownStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, DefaultConstructorMarker var4) {
      this(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final Twig access$getTwig$cp() {
      return twig;
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
   }
}

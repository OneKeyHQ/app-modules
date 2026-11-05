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
   d1 = "\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0001\u0018\u0000 \u00182\u00020\u0001:\u0002\u0017\u0018B'\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0011J\u0006\u0010\u0016\u001a\u00020\u0013R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/api/DeDuperStore;",
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
         "Lio/intercom/android/sdk/api/DeDuperStore$DeDuperFields;",
         "getCachedAttributes",
         "",
         "getEarliestUpdateAt",
         "",
         "save",
         "",
         "cachedAttributesJson",
         "earliestUpdateAt",
         "clear",
         "DeDuperFields",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class DeDuperStore {
   @NotNull
   public static final DeDuperStore$Companion Companion = new DeDuperStore$Companion(null);
   @NotNull
   private final DataStore dataStore;
   @NotNull
   private final CoroutineScope scope;
   @NotNull
   private final PreloadedValue fields;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String OLD_PREFS_NAME = "INTERCOM_DEDUPER_PREFS";
   @NotNull
   private static final String OLD_CACHED_ATTRIBUTES_KEY = "CachedAttributes";
   @NotNull
   private static final String OLD_EARLIEST_UPDATE_AT_KEY = "EarliestUpdateAt";
   @NotNull
   private static final String EMPTY_JSON = "{}";
   @NotNull
   private static final Key KEY_CACHED_ATTRIBUTES = PreferencesKeys.stringKey("cached_attributes");
   @NotNull
   private static final Key KEY_EARLIEST_UPDATE_AT = PreferencesKeys.stringKey("earliest_update_at");
   @NotNull
   private static final ReadOnlyProperty deDuperDataStore$delegate = MultiProcessDataStoreDelegateKt.multiProcessPreferencesDataStore$default(
      "intercom_deduper_datastore", null, ((kotlin.jvm.functions.Function1) (recoveredArgument_47_0) -> DeDuperStore.deDuperDataStore_delegate$lambda$1((android.content.Context)recoveredArgument_47_0)), 2, null
   );
   @NotNull
   private static final Twig twig;

   private DeDuperStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3) {
      this.dataStore = var1;
      this.scope = var2;
      this.fields = new PreloadedValue(this.scope, var3, ((kotlin.jvm.functions.Function0) () -> DeDuperStore.fields$lambda$0()), new DeDuperStore$fields$2(this, null));
   }

   @NotNull
   public final String getCachedAttributes() {
      return ((DeDuperStore$DeDuperFields)this.fields.get$intercom_sdk_base_release()).getCachedAttributes();
   }

   public final long getEarliestUpdateAt() {
      return ((DeDuperStore$DeDuperFields)this.fields.get$intercom_sdk_base_release()).getEarliestUpdateAt();
   }

   public final void save(@NotNull String var1, long var2) {
      Intrinsics.checkNotNullParameter(var1, "cachedAttributesJson");
      this.fields.set$intercom_sdk_base_release(new DeDuperStore$DeDuperFields(var1, var2));
      BuildersKt.launch$default(this.scope, null, null, new DeDuperStore$save$1(this, var1, var2, null), 3, null);
   }

   public final void clear() {
      this.fields.set$intercom_sdk_base_release(new DeDuperStore$DeDuperFields(null, 0L, 3, null));
      BuildersKt.launch$default(this.scope, null, null, new DeDuperStore$clear$1(this, null), 3, null);
   }

   private static final DeDuperStore$DeDuperFields fields$lambda$0() {
      return new DeDuperStore$DeDuperFields(null, 0L, 3, null);
   }

   private static final List deDuperDataStore_delegate$lambda$1(Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return CollectionsKt.listOf(Companion.createSharedPrefsMigration$intercom_sdk_base_release(var0));
   }

   @JvmStatic
   @NotNull
   public static final DeDuperStore create(@NotNull Context var0, @NotNull CoroutineScope var1) {
      return Companion.create(var0, var1);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public static final DeDuperStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var0, @NotNull CoroutineScope var1, @NotNull CoroutineDispatcher var2
   ) {
      return Companion.createForTesting$intercom_sdk_base_release(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final DataStore access$getDataStore$p(DeDuperStore var0) {
      return var0.dataStore;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_CACHED_ATTRIBUTES$cp() {
      return KEY_CACHED_ATTRIBUTES;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_EARLIEST_UPDATE_AT$cp() {
      return KEY_EARLIEST_UPDATE_AT;
   }

   // $VF: synthetic method
   public static final ReadOnlyProperty access$getDeDuperDataStore$delegate$cp() {
      return deDuperDataStore$delegate;
   }

   // $VF: synthetic method
   public DeDuperStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, DefaultConstructorMarker var4) {
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

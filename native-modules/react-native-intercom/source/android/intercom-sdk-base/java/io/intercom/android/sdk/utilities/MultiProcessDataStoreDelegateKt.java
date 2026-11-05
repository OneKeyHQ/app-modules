package io.intercom.android.sdk.utilities;

import android.content.Context;
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.properties.ReadOnlyProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u001aV\u0010\u0000\u001a\u0014\u0012\u0004\u0012\u00020\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u00012\u0006\u0010\u0005\u001a\u00020\u00062\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\b2 \b\u0002\u0010\t\u001a\u001a\u0012\u0004\u0012\u00020\u0002\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\f0\u000b0\nH\u0000\"\u000e\u0010\r\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u000e",
   d2 = {
         "multiProcessPreferencesDataStore",
         "Lkotlin/properties/ReadOnlyProperty;",
         "Landroid/content/Context;",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "name",
         "",
         "corruptionHandler",
         "Landroidx/datastore/core/handlers/ReplaceFileCorruptionHandler;",
         "produceMigrations",
         "Lkotlin/Function1;",
         "",
         "Landroidx/datastore/core/DataMigration;",
         "FALLBACK_VERSION_SUFFIX",
         "intercom-sdk-base_release"
   }
)
public final class MultiProcessDataStoreDelegateKt {
   @NotNull
   private static final String FALLBACK_VERSION_SUFFIX = ".intercom_version";

   @NotNull
   public static final ReadOnlyProperty multiProcessPreferencesDataStore(
      @NotNull String var0, @Nullable ReplaceFileCorruptionHandler var1, @NotNull Function1 var2
   ) {
      Intrinsics.checkNotNullParameter(var0, "name");
      Intrinsics.checkNotNullParameter(var2, "produceMigrations");
      return new MultiProcessPreferenceDataStoreSingletonDelegate(var0, var1, var2);
   }

   // $VF: synthetic method
   public static ReadOnlyProperty multiProcessPreferencesDataStore$default(
      String var0, ReplaceFileCorruptionHandler var1, Function1 var2, int var3, Object var4
   ) {
      if ((var3 & 2) != 0) {
         var1 = null;
      }

      if ((var3 & 4) != 0) {
         var2 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1620_0) -> MultiProcessDataStoreDelegateKt.multiProcessPreferencesDataStore$lambda$0((android.content.Context)recoveredArgument_1620_0));
      }

      return multiProcessPreferencesDataStore(var0, var1, var2);
   }

   private static final List multiProcessPreferencesDataStore$lambda$0(Context var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return CollectionsKt.emptyList();
   }
}

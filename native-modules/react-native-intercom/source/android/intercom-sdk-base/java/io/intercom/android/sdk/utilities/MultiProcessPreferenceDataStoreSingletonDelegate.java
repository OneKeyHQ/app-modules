package io.intercom.android.sdk.utilities;

import android.content.Context;
import androidx.datastore.core.DataStore;
import androidx.datastore.core.InterProcessCoordinator;
import androidx.datastore.core.MultiProcessCoordinatorKt;
import androidx.datastore.core.MultiProcessDataStoreFactory;
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler;
import androidx.datastore.core.okio.OkioStorage;
import androidx.datastore.preferences.PreferenceDataStoreFile;
import androidx.datastore.preferences.core.PreferencesSerializer;
import java.io.File;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.properties.ReadOnlyProperty;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.Dispatchers;
import okio.FileSystem;
import okio.Path;
import okio.Path.Companion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0001B?\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u000e\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\b\u0012\u001e\u0010\t\u001a\u001a\u0012\u0004\u0012\u00020\u0002\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\f0\u000b0\n¢\u0006\u0004\b\r\u0010\u000eJ#\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0013\u001a\u00020\u00022\n\u0010\u0014\u001a\u0006\u0012\u0002\b\u00030\u0015H\u0096\u0002J\u0016\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0017\u001a\u00020\u0002H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\bX\u0082\u0004¢\u0006\u0002\n\u0000R&\u0010\t\u001a\u001a\u0012\u0004\u0012\u00020\u0002\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\f0\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/utilities/MultiProcessPreferenceDataStoreSingletonDelegate;",
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
         "<init>",
         "(Ljava/lang/String;Landroidx/datastore/core/handlers/ReplaceFileCorruptionHandler;Lkotlin/jvm/functions/Function1;)V",
         "lock",
         "",
         "instance",
         "getValue",
         "thisRef",
         "property",
         "Lkotlin/reflect/KProperty;",
         "create",
         "context",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMultiProcessDataStoreDelegate.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultiProcessDataStoreDelegate.kt\nio/intercom/android/sdk/utilities/MultiProcessPreferenceDataStoreSingletonDelegate\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,63:1\n1#2:64\n*E\n"
)
final class MultiProcessPreferenceDataStoreSingletonDelegate implements ReadOnlyProperty {
   @NotNull
   private final String name;
   @Nullable
   private final ReplaceFileCorruptionHandler corruptionHandler;
   @NotNull
   private final Function1 produceMigrations;
   @NotNull
   private final Object lock;
   @Nullable
   private volatile DataStore instance;

   public MultiProcessPreferenceDataStoreSingletonDelegate(@NotNull String var1, @Nullable ReplaceFileCorruptionHandler var2, @NotNull Function1 var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var3, "produceMigrations");
      this.name = var1;
      this.corruptionHandler = var2;
      this.produceMigrations = var3;
      this.lock = new Object();
   }

   // $VF: Extended synchronized range to monitorexit
   @NotNull
   public DataStore getValue(@NotNull Context var1, @NotNull KProperty var2) {
      Intrinsics.checkNotNullParameter(var1, "thisRef");
      Intrinsics.checkNotNullParameter(var2, "property");
      DataStore var10000 = this.instance;
      if (this.instance == null) {
         synchronized (this.lock) {
            int var4 = 0;
            var10000 = this.instance;
            if (this.instance == null) {
               Context var10001 = var1.getApplicationContext();
               Intrinsics.checkNotNullExpressionValue(var10001, "getApplicationContext(...)");
               DataStore var5 = this.create(var10001);
               DataStore var6 = var5;
               int var7 = 0;
               this.instance = var6;
               var10000 = var5;
            }

            DataStore var9 = var10000;
            var10000 = var9;
         }
      }

      return var10000;
   }

   private final DataStore create(Context var1) {
      final android.content.Context recoveredExpressionCapture_1622_0 = var1;
      final io.intercom.android.sdk.utilities.MultiProcessPreferenceDataStoreSingletonDelegate recoveredExpressionCapture_1622_1 = this;
      return MultiProcessDataStoreFactory.create$default(
         MultiProcessDataStoreFactory.INSTANCE,
         new OkioStorage(
            FileSystem.SYSTEM,
            PreferencesSerializer.INSTANCE,
            ((kotlin.jvm.functions.Function2) (recoveredArgument_1621_0, recoveredArgument_1621_1) -> MultiProcessPreferenceDataStoreSingletonDelegate.create$lambda$2((okio.Path)recoveredArgument_1621_0, (okio.FileSystem)recoveredArgument_1621_1)),
            ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.utilities.MultiProcessPreferenceDataStoreSingletonDelegate.create$lambda$3(recoveredExpressionCapture_1622_0, recoveredExpressionCapture_1622_1))
         ),
         this.corruptionHandler,
         (List)this.produceMigrations.invoke(var1),
         null,
         8,
         null
      );
   }

   private static final InterProcessCoordinator create$lambda$2(Path var0, FileSystem var1) {
      Intrinsics.checkNotNullParameter(var0, "path");
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      File var2 = var0.toFile();
      return new NativeCounterFallbackCoordinator(
         MultiProcessCoordinatorKt.createMultiProcessCoordinator(Dispatchers.getIO(), var2),
         new File(var2.getParentFile(), var2.getName() + ".intercom_version")
      );
   }

   private static final Path create$lambda$3(Context var0, MultiProcessPreferenceDataStoreSingletonDelegate var1) {
      Companion var10000 = Path.Companion;
      String var10001 = PreferenceDataStoreFile.preferencesDataStoreFile(var0, var1.name).getAbsolutePath();
      Intrinsics.checkNotNullExpressionValue(var10001, "getAbsolutePath(...)");
      return Companion.get$default(var10000, var10001, false, 1, null);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object getValue(Object var1, KProperty var2) {
      return this.getValue((Context)var1, var2);
   }
}

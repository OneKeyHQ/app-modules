package io.intercom.android.sdk.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import androidx.datastore.core.DataMigration;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import io.intercom.android.sdk.utilities.SafeSharedPreferencesKt;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.Boxing;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000'\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0002H\u0096@¢\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0002H\u0096@¢\u0006\u0002\u0010\fJ\u000e\u0010\u000e\u001a\u00020\u000fH\u0096@¢\u0006\u0002\u0010\u0010R\u001d\u0010\u0003\u001a\u0004\u0018\u00010\u00048BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0011",
   d2 = {
         "io/intercom/android/sdk/api/ShutdownStore$Companion$createSharedPrefsMigration$1",
         "Landroidx/datastore/core/DataMigration;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "oldPrefs",
         "Landroid/content/SharedPreferences;",
         "getOldPrefs",
         "()Landroid/content/SharedPreferences;",
         "oldPrefs$delegate",
         "Lkotlin/Lazy;",
         "shouldMigrate",
         "",
         "currentData",
         "(Landroidx/datastore/preferences/core/Preferences;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "migrate",
         "cleanUp",
         "",
         "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
public final class ShutdownStore$Companion$createSharedPrefsMigration$1 implements DataMigration {
   private final Lazy oldPrefs$delegate;

   ShutdownStore$Companion$createSharedPrefsMigration$1(Context var1) {
      final android.content.Context recoveredExpressionCapture_54_0 = var1;
      this.oldPrefs$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.api.ShutdownStore$Companion$createSharedPrefsMigration$1.oldPrefs_delegate$lambda$0(recoveredExpressionCapture_54_0)));
   }

   private final SharedPreferences getOldPrefs() {
      Lazy var1 = this.oldPrefs$delegate;
      return (SharedPreferences)var1.getValue();
   }

   public Object shouldMigrate(Preferences var1, Continuation var2) {
      SharedPreferences var10000 = this.getOldPrefs();
      if (var10000 == null) {
         return Boxing.boxBoolean(false);
      }

      SharedPreferences var3 = var10000;
      boolean var4 = var1.get(ShutdownStore.access$getKEY_SHUTDOWN_FINGERPRINT$cp()) == null
         && var1.get(ShutdownStore.access$getKEY_SHUTDOWN_EXPIRY$cp()) == null
         && var1.get(ShutdownStore.access$getKEY_SHUTDOWN_REASON$cp()) == null;
      boolean var5 = var3.contains("ShutdownFingerprint") || var3.contains("ShutdownExpiry") || var3.contains("ShutdownReason");
      return Boxing.boxBoolean(var4 && var5);
   }

   public Object migrate(Preferences var1, Continuation var2) {
      SharedPreferences var10000 = this.getOldPrefs();
      if (var10000 == null) {
         return var1;
      }

      SharedPreferences var3 = var10000;
      MutablePreferences var4 = var1.toMutablePreferences();
      String var9 = var3.getString("ShutdownFingerprint", "");
      if (var9 == null) {
         var9 = "";
      }

      String var5 = var9;
      if (var5.length() > 0) {
         var4.set(ShutdownStore.access$getKEY_SHUTDOWN_FINGERPRINT$cp(), var5);
      }

      long var6 = var3.getLong("ShutdownExpiry", 0L);
      if (var6 != 0L) {
         var4.set(ShutdownStore.access$getKEY_SHUTDOWN_EXPIRY$cp(), Boxing.boxLong(var6));
      }

      String var10 = var3.getString("ShutdownReason", "");
      if (var10 == null) {
         var10 = "";
      }

      String var8 = var10;
      if (var8.length() > 0) {
         var4.set(ShutdownStore.access$getKEY_SHUTDOWN_REASON$cp(), var8);
      }

      return var4.toPreferences();
   }

   @Override
   public Object cleanUp(Continuation var1) {
      SharedPreferences var10000 = this.getOldPrefs();
      if (var10000 != null) {
         Editor var2 = var10000.edit();
         if (var2 != null) {
            Editor var3 = var2.clear();
            if (var3 != null) {
               var3.apply();
            }
         }
      }

      return Unit.INSTANCE;
   }

   private static final SharedPreferences oldPrefs_delegate$lambda$0(Context var0) {
      return SafeSharedPreferencesKt.getSharedPreferencesOrNull(var0, "INTERCOM_SHUTDOWN_PREFS");
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object shouldMigrate(Object var1, Continuation var2) {
      return this.shouldMigrate((Preferences)var1, var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object migrate(Object var1, Continuation var2) {
      return this.migrate((Preferences)var1, var2);
   }
}

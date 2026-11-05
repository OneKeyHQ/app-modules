package io.intercom.android.sdk.identity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import androidx.datastore.core.DataMigration;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.Preferences.Key;
import io.intercom.android.sdk.utilities.SafeSharedPreferencesKt;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000'\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0002H\u0096@¢\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0002H\u0096@¢\u0006\u0002\u0010\fJ\u000e\u0010\u000e\u001a\u00020\u000fH\u0096@¢\u0006\u0002\u0010\u0010R\u001d\u0010\u0003\u001a\u0004\u0018\u00010\u00048BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0011",
   d2 = {
         "io/intercom/android/sdk/identity/AppConfigStore$Companion$createSharedPrefsMigration$1",
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
@SourceDebugExtension(
   "SMAP\nAppConfigStore.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AppConfigStore.kt\nio/intercom/android/sdk/identity/AppConfigStore$Companion$createSharedPrefsMigration$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,508:1\n1761#2,3:509\n*S KotlinDebug\n*F\n+ 1 AppConfigStore.kt\nio/intercom/android/sdk/identity/AppConfigStore$Companion$createSharedPrefsMigration$1\n*L\n456#1:509,3\n*E\n"
)
public final class AppConfigStore$Companion$createSharedPrefsMigration$1 implements DataMigration {
   private final Lazy oldPrefs$delegate;

   AppConfigStore$Companion$createSharedPrefsMigration$1(Context var1) {
      final android.content.Context recoveredExpressionCapture_121_0 = var1;
      this.oldPrefs$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.identity.AppConfigStore$Companion$createSharedPrefsMigration$1.oldPrefs_delegate$lambda$0(recoveredExpressionCapture_121_0)));
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
      if (var1.asMap().isEmpty()) {
         var var4 = AppConfigStore.access$getALL_MIGRATION_KEY_NAMES$cp();
         int var5 = 0;
         boolean var10;
         if (var4 instanceof Collection && ((Collection)var4).isEmpty()) {
            var10 = false;
         } else {
            Iterator var6 = var4.iterator();

            while (true) {
               if (!var6.hasNext()) {
                  var10 = false;
                  break;
               }

               Object var7 = var6.next();
               String var8 = (String)var7;
               int var9 = 0;
               if (var3.contains(var8)) {
                  var10 = true;
                  break;
               }
            }
         }

         if (var10) {
            return Boxing.boxBoolean(true);
         }
      }

      return Boxing.boxBoolean(false);
   }

   public Object migrate(Preferences var1, Continuation var2) {
      SharedPreferences var10000 = this.getOldPrefs();
      if (var10000 == null) {
         return var1;
      }

      SharedPreferences var3 = var10000;
      MutablePreferences var4 = var1.toMutablePreferences();

      for (java.lang.Object recoveredEntryElement_0_114 : AppConfigStore.access$getSTRING_MIGRATION_KEYS$cp().entrySet()) {
         Entry var6 = (Entry) recoveredEntryElement_0_114;
         String var7 = (String)var6.getKey();
         Key var8 = (Key)var6.getValue();
         if (var3.contains(var7)) {
            String var9 = var3.getString(var7, null);
            if (var9 != null) {
               var4.set(var8, var9);
            }
         }
      }

      for (java.lang.Object recoveredEntryElement_1_125 : AppConfigStore.access$getINT_MIGRATION_KEYS$cp().entrySet()) {
         Entry var14 = (Entry) recoveredEntryElement_1_125;
         String var18 = (String)var14.getKey();
         Key var22 = (Key)var14.getValue();
         if (var3.contains(var18)) {
            var4.set(var22, Boxing.boxInt(var3.getInt(var18, 0)));
         }
      }

      for (java.lang.Object recoveredEntryElement_2_133 : AppConfigStore.access$getLONG_MIGRATION_KEYS$cp().entrySet()) {
         Entry var15 = (Entry) recoveredEntryElement_2_133;
         String var19 = (String)var15.getKey();
         Key var23 = (Key)var15.getValue();
         if (var3.contains(var19)) {
            var4.set(var23, Boxing.boxLong(var3.getLong(var19, 0L)));
         }
      }

      for (java.lang.Object recoveredEntryElement_3_141 : AppConfigStore.access$getBOOLEAN_MIGRATION_KEYS$cp().entrySet()) {
         Entry var16 = (Entry) recoveredEntryElement_3_141;
         String var20 = (String)var16.getKey();
         Key var24 = (Key)var16.getValue();
         if (var3.contains(var20)) {
            var4.set(var24, Boxing.boxBoolean(var3.getBoolean(var20, false)));
         }
      }

      for (java.lang.Object recoveredEntryElement_4_149 : AppConfigStore.access$getSTRING_SET_MIGRATION_KEYS$cp().entrySet()) {
         Entry var17 = (Entry) recoveredEntryElement_4_149;
         String var21 = (String)var17.getKey();
         Key var25 = (Key)var17.getValue();
         if (var3.contains(var21)) {
            Set var26 = var3.getStringSet(var21, null);
            if (var26 != null) {
               var4.set(var25, var26);
            }
         }
      }

      return var4.toPreferences();
   }

   @Override
   public Object cleanUp(Continuation var1) {
      SharedPreferences var10000 = this.getOldPrefs();
      if (var10000 != null) {
         Editor var5 = var10000.edit();
         if (var5 != null) {
            Editor var2 = var5;

            for (java.lang.Object recoveredEntryElement_0_176 : AppConfigStore.access$getALL_MIGRATION_KEY_NAMES$cp()) {
               String var4 = (String) recoveredEntryElement_0_176;
               var2.remove(var4);
            }

            var2.apply();
            return Unit.INSTANCE;
         }
      }

      return Unit.INSTANCE;
   }

   private static final SharedPreferences oldPrefs_delegate$lambda$0(Context var0) {
      return SafeSharedPreferencesKt.getSharedPreferencesOrNull(var0, "INTERCOM_SDK_PREFS");
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

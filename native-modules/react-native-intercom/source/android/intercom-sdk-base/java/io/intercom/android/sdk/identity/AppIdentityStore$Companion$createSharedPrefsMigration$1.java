package io.intercom.android.sdk.identity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import androidx.datastore.core.DataMigration;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.Preferences.Key;
import io.intercom.android.sdk.utilities.CryptoHelper;
import io.intercom.android.sdk.utilities.SafeSharedPreferencesKt;
import java.util.Collection;
import java.util.Iterator;
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
         "io/intercom/android/sdk/identity/AppIdentityStore$Companion$createSharedPrefsMigration$1",
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
   "SMAP\nAppIdentityStore.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AppIdentityStore.kt\nio/intercom/android/sdk/identity/AppIdentityStore$Companion$createSharedPrefsMigration$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,159:1\n1740#2,3:160\n1761#2,3:163\n*S KotlinDebug\n*F\n+ 1 AppIdentityStore.kt\nio/intercom/android/sdk/identity/AppIdentityStore$Companion$createSharedPrefsMigration$1\n*L\n128#1:160,3\n129#1:163,3\n*E\n"
)
public final class AppIdentityStore$Companion$createSharedPrefsMigration$1 implements DataMigration {
   private final Lazy oldPrefs$delegate;

   AppIdentityStore$Companion$createSharedPrefsMigration$1(Context var1) {
      final android.content.Context recoveredExpressionCapture_123_0 = var1;
      this.oldPrefs$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.identity.AppIdentityStore$Companion$createSharedPrefsMigration$1.oldPrefs_delegate$lambda$0(recoveredExpressionCapture_123_0)));
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
      var var4 = AppIdentityStore.access$getMIGRATION_KEY_MAP$cp().values();
      int var5 = 0;
      boolean var16;
      if (var4 instanceof Collection && ((Collection)var4).isEmpty()) {
         var16 = true;
      } else {
         label68: {
            for (Object var7 : var4) {
               Key var8 = (Key)var7;
               int var9 = 0;
               if (var1.get(var8) != null) {
                  var16 = false;
                  break label68;
               }
            }

            var16 = true;
         }
      }

      if (var16) {
         var4 = AppIdentityStore.access$getMIGRATION_KEY_MAP$cp().keySet();
         var5 = 0;
         boolean var17;
         if (var4 instanceof Collection && ((Collection)var4).isEmpty()) {
            var17 = false;
         } else {
            Iterator var12 = var4.iterator();

            while (true) {
               if (!var12.hasNext()) {
                  var17 = false;
                  break;
               }

               Object var13 = var12.next();
               String var14 = (String)var13;
               int var15 = 0;
               if (var3.contains(var14)) {
                  var17 = true;
                  break;
               }
            }
         }

         if (var17) {
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

      for (java.lang.Object recoveredEntryElement_0_134 : AppIdentityStore.access$getMIGRATION_KEY_MAP$cp().entrySet()) {
         Entry var6 = (Entry) recoveredEntryElement_0_134;
         String var7 = (String)var6.getKey();
         Key var8 = (Key)var6.getValue();
         String var10 = var3.getString(var7, "");
         if (var10 == null) {
            var10 = "";
         }

         String var9 = var10;
         if (var9.length() > 0) {
            var4.set(var8, CryptoHelper.INSTANCE.encrypt$intercom_sdk_base_release(var9));
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

            for (java.lang.Object recoveredEntryElement_0_160 : AppIdentityStore.access$getMIGRATION_KEY_MAP$cp().keySet()) {
               String var4 = (String) recoveredEntryElement_0_160;
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

package io.intercom.android.sdk.identity;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.core.PreferencesKt;
import androidx.datastore.preferences.core.Preferences.Key;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.CryptoHelper;
import io.intercom.android.sdk.utilities.MultiProcessDataStoreDelegateKt;
import io.intercom.android.sdk.utilities.PreloadedValue;
import io.intercom.android.sdk.utilities.SafeSharedPreferencesKt;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.properties.ReadOnlyProperty;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0001\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B/\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fJ\u0006\u0010\u0011\u001a\u00020\u000fJ\u0016\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u0006H\u0082@¢\u0006\u0002\u0010\u0013R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u0010\r\u001a\r\u0012\t\u0012\u00070\u000f¢\u0006\u0002\b\u00100\u000eX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/identity/DeviceIdentityStore;",
         "",
         "dataStore",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "context",
         "Landroid/content/Context;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "ioDispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "<init>",
         "(Landroidx/datastore/core/DataStore;Landroid/content/Context;Lkotlinx/coroutines/CoroutineScope;Lkotlinx/coroutines/CoroutineDispatcher;)V",
         "deviceId",
         "Lio/intercom/android/sdk/utilities/PreloadedValue;",
         "",
         "Lkotlin/jvm/internal/EnhancedNullability;",
         "getDeviceIdentifier",
         "loadDeviceIdentifier",
         "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class DeviceIdentityStore {
   @NotNull
   public static final DeviceIdentityStore$Companion Companion = new DeviceIdentityStore$Companion(null);
   @NotNull
   private final DataStore dataStore;
   @NotNull
   private final PreloadedValue deviceId;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final Twig twig;
   @NotNull
   private static final String LEGACY_PREFS_NAME = "INTERCOM_SDK_PREFS";
   @NotNull
   private static final String KEY_NAME = "device_identifier";
   @NotNull
   private static final Key KEY_DEVICE_IDENTIFIER = PreferencesKeys.stringKey("device_identifier");
   @NotNull
   private static final ReadOnlyProperty deviceDataStore$delegate = MultiProcessDataStoreDelegateKt.multiProcessPreferencesDataStore$default(
      "intercom_device_prefs_datastore", null, ((kotlin.jvm.functions.Function1) (recoveredArgument_124_0) -> DeviceIdentityStore.deviceDataStore_delegate$lambda$1((android.content.Context)recoveredArgument_124_0)), 2, null
   );

   private DeviceIdentityStore(DataStore var1, Context var2, CoroutineScope var3, CoroutineDispatcher var4) {
      this.dataStore = var1;
      this.deviceId = new PreloadedValue(var3, var4, ((kotlin.jvm.functions.Function0) () -> DeviceIdentityStore.deviceId$lambda$0()), new DeviceIdentityStore$deviceId$2(this, var2, null));
   }

   @NotNull
   public final String getDeviceIdentifier() {
      return (String)this.deviceId.get$intercom_sdk_base_release();
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   private final Object loadDeviceIdentifier(Context var1, Continuation var2) {
      DeviceIdentityStore recoveredThis = this;
      DeviceIdentityStore$loadDeviceIdentifier$1 var8;
      label115: {
         if (var2 instanceof DeviceIdentityStore$loadDeviceIdentifier$1) {
            var8 = (DeviceIdentityStore$loadDeviceIdentifier$1)var2;
            if ((var8.label & -2147483648) != 0) {
               var8.label -= Integer.MIN_VALUE;
               break label115;
            }
         }

         var8 = new DeviceIdentityStore$loadDeviceIdentifier$1(recoveredThis, var2);
      }

      String var5;
      label118: {
         Object var9;
         label106: {
            label119: {
               Object var7 = var8.result;
               var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               Object var30;
               switch (var8.label) {
                  case 0:
                     ResultKt.throwOnFailure(var7);

                     try {
                        Flow var32 = recoveredThis.dataStore.getData();
                        var8.L$0 = recoveredThis;
                        var8.L$1 = var1;
                        var8.label = 1;
                        var30 = FlowKt.first(var32, var8);
                     } catch (CancellationException var18) {
                        throw var18;
                     } catch (Exception var19) {
                        twig.w("Failed to load device identifier from DataStore, generating fallback: " + var19.getMessage());
                        String var31 = UUID.randomUUID().toString();
                        Intrinsics.checkNotNullExpressionValue(var31, "toString(...)");
                        var5 = var31;
                        break label106;
                     }

                     if (var30 == var9) {
                        return var9;
                     }
                     break;
                  case 1:
                     var1 = (Context)var8.L$1;
                     recoveredThis = (DeviceIdentityStore)var8.L$0;

                     try {
                        ResultKt.throwOnFailure(var7);
                        var30 = var7;
                        break;
                     } catch (CancellationException var20) {
                        throw var20;
                     } catch (Exception var21) {
                        twig.w("Failed to load device identifier from DataStore, generating fallback: " + var21.getMessage());
                        String var29 = UUID.randomUUID().toString();
                        Intrinsics.checkNotNullExpressionValue(var29, "toString(...)");
                        var5 = var29;
                        break label106;
                     }
                  case 2:
                     var5 = (String)var8.L$1;
                     recoveredThis = (DeviceIdentityStore)var8.L$0;

                     try {
                        ResultKt.throwOnFailure(var7);
                        break label119;
                     } catch (CancellationException var22) {
                        throw var22;
                     } catch (Exception var23) {
                        twig.w("Failed to load device identifier from DataStore, generating fallback: " + var23.getMessage());
                        String var10000 = UUID.randomUUID().toString();
                        Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
                        var5 = var10000;
                        break label106;
                     }
                  case 3:
                     var5 = (String)var8.L$0;

                     try {
                        ResultKt.throwOnFailure(var7);
                        break label118;
                     } catch (CancellationException var16) {
                        throw var16;
                     } catch (Exception var17) {
                        twig.w("Failed to persist fallback device identifier: " + var17.getMessage());
                        return var5;
                     }
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               try {
                  Preferences var3 = (Preferences)var30;
                  String var4 = (String)var3.get(KEY_DEVICE_IDENTIFIER);
                  if (var4 != null) {
                     var5 = CryptoHelper.INSTANCE.decrypt$intercom_sdk_base_release(var4);
                     if (var5.length() > 0) {
                        return var5;
                     }
                  }

                  if (SafeSharedPreferencesKt.getSharedPreferencesOrNull(var1, "INTERCOM_SDK_PREFS") == null) {
                     twig.w("Legacy SharedPreferences unavailable; using unpersisted fallback device identifier");
                     String var40 = UUID.randomUUID().toString();
                     Intrinsics.checkNotNullExpressionValue(var40, "toString(...)");
                     return var40;
                  }

                  String var34 = UUID.randomUUID().toString();
                  Intrinsics.checkNotNullExpressionValue(var34, "toString(...)");
                  var5 = var34;
                  DataStore var35 = recoveredThis.dataStore;
                  Function2 var10001 = new DeviceIdentityStore$loadDeviceIdentifier$2(var5, null);
                  var8.L$0 = recoveredThis;
                  var8.L$1 = var5;
                  var8.label = 2;
                  var30 = PreferencesKt.edit(var35, var10001, var8);
               } catch (CancellationException var24) {
                  throw var24;
               } catch (Exception var25) {
                  twig.w("Failed to load device identifier from DataStore, generating fallback: " + var25.getMessage());
                  String var33 = UUID.randomUUID().toString();
                  Intrinsics.checkNotNullExpressionValue(var33, "toString(...)");
                  var5 = var33;
                  break label106;
               }

               if (var30 == var9) {
                  return var9;
               }
            }

            try {
               return var5;
            } catch (CancellationException var12) {
               throw var12;
            } catch (Exception var13) {
               twig.w("Failed to load device identifier from DataStore, generating fallback: " + var13.getMessage());
               String var37 = UUID.randomUUID().toString();
               Intrinsics.checkNotNullExpressionValue(var37, "toString(...)");
               var5 = var37;
            }
         }

         Object var39;
         try {
            DataStore recoveredFallbackStore = recoveredThis.dataStore;
            Function2 var41 = new DeviceIdentityStore$loadDeviceIdentifier$3(var5, null);
            var8.L$0 = var5;
            var8.L$1 = null;
            var8.label = 3;
            var39 = PreferencesKt.edit(recoveredFallbackStore, var41, var8);
         } catch (CancellationException var14) {
            throw var14;
         } catch (Exception var15) {
            twig.w("Failed to persist fallback device identifier: " + var15.getMessage());
            return var5;
         }

         if (var39 == var9) {
            return var9;
         }
      }

      try {
         ;
      } catch (CancellationException var10) {
         throw var10;
      } catch (Exception var11) {
         twig.w("Failed to persist fallback device identifier: " + var11.getMessage());
      }

      return var5;
   }

   private static final String deviceId$lambda$0() {
      return UUID.randomUUID().toString();
   }

   private static final List deviceDataStore_delegate$lambda$1(Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return CollectionsKt.listOf(Companion.createSharedPrefsMigration$intercom_sdk_base_release(var0));
   }

   @JvmStatic
   @NotNull
   public static final DeviceIdentityStore create(@NotNull Context var0, @NotNull CoroutineScope var1) {
      return Companion.create(var0, var1);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public static final DeviceIdentityStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var0, @NotNull Context var1, @NotNull CoroutineScope var2, @NotNull CoroutineDispatcher var3
   ) {
      return Companion.createForTesting$intercom_sdk_base_release(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   public static final Object access$loadDeviceIdentifier(DeviceIdentityStore var0, Context var1, Continuation var2) {
      return var0.loadDeviceIdentifier(var1, var2);
   }

   // $VF: synthetic method
   public DeviceIdentityStore(DataStore var1, Context var2, CoroutineScope var3, CoroutineDispatcher var4, DefaultConstructorMarker var5) {
      this(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static final Key access$getKEY_DEVICE_IDENTIFIER$cp() {
      return KEY_DEVICE_IDENTIFIER;
   }

   // $VF: synthetic method
   public static final ReadOnlyProperty access$getDeviceDataStore$delegate$cp() {
      return deviceDataStore$delegate;
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
   }
}

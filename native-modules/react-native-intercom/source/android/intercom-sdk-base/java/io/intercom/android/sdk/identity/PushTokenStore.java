package io.intercom.android.sdk.identity;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.core.Preferences.Key;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.CryptoHelper;
import io.intercom.android.sdk.utilities.MultiProcessDataStoreDelegateKt;
import io.intercom.android.sdk.utilities.PreloadedValue;
import java.util.List;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.properties.ReadOnlyProperty;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\n\b\u0001\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB'\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u0006\u0010\u0010\u001a\u00020\rJ\u0006\u0010\u0011\u001a\u00020\u000fJ\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000fJ\u000e\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\rJ\u000e\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\rJ\u0006\u0010\u0018\u001a\u00020\u0013J\u000e\u0010\u0019\u001a\u00020\rH\u0082@¢\u0006\u0002\u0010\u001aJ\u000e\u0010\u001b\u001a\u00020\u000fH\u0082@¢\u0006\u0002\u0010\u001aR\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/identity/PushTokenStore;",
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
         "token",
         "Lio/intercom/android/sdk/utilities/PreloadedValue;",
         "",
         "submitted",
         "",
         "getPushToken",
         "isDeviceTokenSubmitted",
         "setDeviceTokenSubmitted",
         "",
         "deviceTokenSubmitted",
         "savePushToken",
         "pushToken",
         "hasCachedToken",
         "clear",
         "loadPushToken",
         "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "loadSubmitted",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class PushTokenStore {
   @NotNull
   public static final PushTokenStore$Companion Companion = new PushTokenStore$Companion(null);
   @NotNull
   private final DataStore dataStore;
   @NotNull
   private final CoroutineScope scope;
   @NotNull
   private final PreloadedValue token;
   @NotNull
   private final PreloadedValue submitted;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String OLD_PREFS_NAME = "INTERCOM_SDK_DATA";
   @NotNull
   private static final String OLD_PUSH_TOKEN_KEY = "push_token";
   @NotNull
   private static final Key KEY_PUSH_TOKEN = PreferencesKeys.stringKey("push_token");
   @NotNull
   private static final Key KEY_DEVICE_TOKEN_SUBMITTED = PreferencesKeys.booleanKey("device_token_submitted");
   @NotNull
   private static final ReadOnlyProperty pushTokenDataStore$delegate = MultiProcessDataStoreDelegateKt.multiProcessPreferencesDataStore$default(
      "intercom_push_token_datastore", null, ((kotlin.jvm.functions.Function1) (recoveredArgument_127_0) -> PushTokenStore.pushTokenDataStore_delegate$lambda$2((android.content.Context)recoveredArgument_127_0)), 2, null
   );
   @NotNull
   private static final Twig twig;

   private PushTokenStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3) {
      this.dataStore = var1;
      this.scope = var2;
      this.token = new PreloadedValue(this.scope, var3, ((kotlin.jvm.functions.Function0) () -> PushTokenStore.token$lambda$0()), new PushTokenStore$token$2(this, null));
      this.submitted = new PreloadedValue(this.scope, var3, ((kotlin.jvm.functions.Function0) () -> PushTokenStore.submitted$lambda$1()), new PushTokenStore$submitted$2(this, null));
   }

   @NotNull
   public final String getPushToken() {
      return (String)this.token.get$intercom_sdk_base_release();
   }

   public final boolean isDeviceTokenSubmitted() {
      return (Boolean)this.submitted.get$intercom_sdk_base_release();
   }

   public final void setDeviceTokenSubmitted(boolean var1) {
      this.submitted.set$intercom_sdk_base_release(var1);
      BuildersKt.launch$default(this.scope, null, null, new PushTokenStore$setDeviceTokenSubmitted$1(this, var1, null), 3, null);
   }

   public final void savePushToken(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "pushToken");
      this.token.set$intercom_sdk_base_release(var1);
      this.submitted.set$intercom_sdk_base_release(false);
      BuildersKt.launch$default(this.scope, null, null, new PushTokenStore$savePushToken$1(this, var1, null), 3, null);
   }

   public final boolean hasCachedToken(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "pushToken");
      return Intrinsics.areEqual(var1, this.getPushToken());
   }

   public final void clear() {
      this.token.set$intercom_sdk_base_release("");
      this.submitted.set$intercom_sdk_base_release(false);
      BuildersKt.launch$default(this.scope, null, null, new PushTokenStore$clear$1(this, null), 3, null);
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   private final Object loadPushToken(Continuation var1) {
      PushTokenStore$loadPushToken$1 var5;
      label47: {
         if (var1 instanceof PushTokenStore$loadPushToken$1) {
            var5 = (PushTokenStore$loadPushToken$1)var1;
            if ((var5.label & -2147483648) != 0) {
               var5.label -= Integer.MIN_VALUE;
               break label47;
            }
         }

         var5 = new PushTokenStore$loadPushToken$1(this, var1);
      }

      Object var4 = var5.result;
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var5.label) {
         case 0:
            ResultKt.throwOnFailure(var4);

            try {
               Flow var14 = this.dataStore.getData();
               var5.label = 1;
               var10000 = FlowKt.first(var14, var5);
            } catch (CancellationException var9) {
               throw var9;
            } catch (Exception var10) {
               twig.w("Failed to read push token from DataStore: " + var10.getMessage());
               return "";
            }

            if (var10000 == var6) {
               return var6;
            }
            break;
         case 1:
            try {
               ResultKt.throwOnFailure(var4);
               var10000 = var4;
               break;
            } catch (CancellationException var11) {
               throw var11;
            } catch (Exception var12) {
               twig.w("Failed to read push token from DataStore: " + var12.getMessage());
               return "";
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      String var2;
      try {
         Preferences var13 = (Preferences)var10000;
         String var15 = (String)var13.get(KEY_PUSH_TOKEN);
         if (var15 == null) {
            return "";
         }

         String var3 = var15;
         var2 = CryptoHelper.INSTANCE.decrypt$intercom_sdk_base_release(var3);
      } catch (CancellationException var7) {
         throw var7;
      } catch (Exception var8) {
         twig.w("Failed to read push token from DataStore: " + var8.getMessage());
         var2 = "";
      }

      return var2;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   private final Object loadSubmitted(Continuation var1) {
      PushTokenStore$loadSubmitted$1 var5;
      label56: {
         if (var1 instanceof PushTokenStore$loadSubmitted$1) {
            var5 = (PushTokenStore$loadSubmitted$1)var1;
            if ((var5.label & -2147483648) != 0) {
               var5.label -= Integer.MIN_VALUE;
               break label56;
            }
         }

         var5 = new PushTokenStore$loadSubmitted$1(this, var1);
      }

      Object var4 = var5.result;
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var5.label) {
         case 0:
            ResultKt.throwOnFailure(var4);

            try {
               Flow var14 = this.dataStore.getData();
               var5.label = 1;
               var10000 = FlowKt.first(var14, var5);
            } catch (CancellationException var9) {
               throw var9;
            } catch (Exception var10) {
               twig.w("Failed to read device token submitted flag from DataStore: " + var10.getMessage());
               boolean var16 = false;
               return Boxing.boxBoolean(var16);
            }

            if (var10000 == var6) {
               return var6;
            }
            break;
         case 1:
            try {
               ResultKt.throwOnFailure(var4);
               var10000 = var4;
               break;
            } catch (CancellationException var11) {
               throw var11;
            } catch (Exception var12) {
               twig.w("Failed to read device token submitted flag from DataStore: " + var12.getMessage());
               boolean var2 = false;
               return Boxing.boxBoolean(var2);
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      boolean var17;
      try {
         Preferences var13 = (Preferences)var10000;
         Boolean var15 = (Boolean)var13.get(KEY_DEVICE_TOKEN_SUBMITTED);
         var17 = var15 != null ? var15 : false;
      } catch (CancellationException var7) {
         throw var7;
      } catch (Exception var8) {
         twig.w("Failed to read device token submitted flag from DataStore: " + var8.getMessage());
         var17 = false;
      }

      return Boxing.boxBoolean(var17);
   }

   private static final String token$lambda$0() {
      return "";
   }

   private static final boolean submitted$lambda$1() {
      return false;
   }

   private static final List pushTokenDataStore_delegate$lambda$2(Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return CollectionsKt.listOf(Companion.createSharedPrefsMigration$intercom_sdk_base_release(var0));
   }

   @JvmStatic
   @NotNull
   public static final PushTokenStore create(@NotNull Context var0, @NotNull CoroutineScope var1) {
      return Companion.create(var0, var1);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public static final PushTokenStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var0, @NotNull CoroutineScope var1, @NotNull CoroutineDispatcher var2
   ) {
      return Companion.createForTesting$intercom_sdk_base_release(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final DataStore access$getDataStore$p(PushTokenStore var0) {
      return var0.dataStore;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_DEVICE_TOKEN_SUBMITTED$cp() {
      return KEY_DEVICE_TOKEN_SUBMITTED;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_PUSH_TOKEN$cp() {
      return KEY_PUSH_TOKEN;
   }

   // $VF: synthetic method
   public static final Object access$loadPushToken(PushTokenStore var0, Continuation var1) {
      return var0.loadPushToken(var1);
   }

   // $VF: synthetic method
   public static final Object access$loadSubmitted(PushTokenStore var0, Continuation var1) {
      return var0.loadSubmitted(var1);
   }

   // $VF: synthetic method
   public static final ReadOnlyProperty access$getPushTokenDataStore$delegate$cp() {
      return pushTokenDataStore$delegate;
   }

   // $VF: synthetic method
   public PushTokenStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, DefaultConstructorMarker var4) {
      this(var1, var2, var3);
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
   }
}

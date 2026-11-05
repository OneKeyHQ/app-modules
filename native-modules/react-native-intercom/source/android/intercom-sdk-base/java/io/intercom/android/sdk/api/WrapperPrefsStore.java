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
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.properties.ReadOnlyProperty;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0001\u0018\u0000 &2\u00020\u0001:\u0001&B'\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u0006\u0010\u0010\u001a\u00020\rJ\u0006\u0010\u0014\u001a\u00020\rJ\u0006\u0010\u0015\u001a\u00020\rJ\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\rJ\u000e\u0010\u0019\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\rJ\u000e\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\rJ\u0006\u0010\u001b\u001a\u00020\u0017JA\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\r2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00170\u001f2\u001c\u0010 \u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\"\u0012\u0006\u0012\u0004\u0018\u00010\u00010!H\u0002¢\u0006\u0002\u0010#J4\u0010$\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\r2\u001c\u0010 \u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\"\u0012\u0006\u0012\u0004\u0018\u00010\u00010!H\u0082@¢\u0006\u0002\u0010%R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006'",
   d2 = {
         "Lio/intercom/android/sdk/api/WrapperPrefsStore;",
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
         "unityVersion",
         "Lio/intercom/android/sdk/utilities/PreloadedValue;",
         "",
         "cordovaVersion",
         "reactNativeVersion",
         "getUnityVersion",
         "writeChainLock",
         "lastWrite",
         "Lkotlinx/coroutines/Job;",
         "getCordovaVersion",
         "getReactNativeVersion",
         "setCordovaVersion",
         "",
         "version",
         "setReactNativeVersion",
         "setUnityVersion",
         "clear",
         "enqueueWrite",
         "action",
         "cacheUpdate",
         "Lkotlin/Function0;",
         "block",
         "Lkotlin/Function1;",
         "Lkotlin/coroutines/Continuation;",
         "(Ljava/lang/String;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;)V",
         "writeSafely",
         "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class WrapperPrefsStore {
   @NotNull
   public static final WrapperPrefsStore$Companion Companion = new WrapperPrefsStore$Companion(null);
   @NotNull
   private final DataStore dataStore;
   @NotNull
   private final CoroutineScope scope;
   @NotNull
   private final PreloadedValue unityVersion;
   @NotNull
   private final PreloadedValue cordovaVersion;
   @NotNull
   private final PreloadedValue reactNativeVersion;
   @NotNull
   private final Object writeChainLock;
   @Nullable
   private Job lastWrite;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String OLD_PREFS_NAME = "intercomsdk_wrapper_prefs";
   @NotNull
   private static final String OLD_CORDOVA_VERSION_KEY = "cordova_version";
   @NotNull
   private static final String OLD_REACT_NATIVE_VERSION_KEY = "react_native_version";
   @NotNull
   private static final Key KEY_CORDOVA_VERSION = PreferencesKeys.stringKey("cordova_version");
   @NotNull
   private static final Key KEY_REACT_NATIVE_VERSION = PreferencesKeys.stringKey("react_native_version");
   @NotNull
   private static final Key KEY_UNITY_VERSION = PreferencesKeys.stringKey("unity_version");
   @NotNull
   private static final ReadOnlyProperty wrapperPrefsDataStore$delegate = MultiProcessDataStoreDelegateKt.multiProcessPreferencesDataStore$default(
      "intercom_wrapper_prefs_datastore", null, ((kotlin.jvm.functions.Function1) (recoveredArgument_56_0) -> WrapperPrefsStore.wrapperPrefsDataStore_delegate$lambda$8((android.content.Context)recoveredArgument_56_0)), 2, null
   );
   @NotNull
   private static final Twig twig;
   @Nullable
   private static volatile WrapperPrefsStore instance;
   @Nullable
   private static volatile CoroutineScope ownedScope;

   private WrapperPrefsStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3) {
      this.dataStore = var1;
      this.scope = var2;
      this.unityVersion = new PreloadedValue(this.scope, var3, ((kotlin.jvm.functions.Function0) () -> WrapperPrefsStore.unityVersion$lambda$0()), new WrapperPrefsStore$unityVersion$2(this, null));
      this.cordovaVersion = new PreloadedValue(this.scope, var3, ((kotlin.jvm.functions.Function0) () -> WrapperPrefsStore.cordovaVersion$lambda$1()), new WrapperPrefsStore$cordovaVersion$2(this, null));
      this.reactNativeVersion = new PreloadedValue(
         this.scope, var3, ((kotlin.jvm.functions.Function0) () -> WrapperPrefsStore.reactNativeVersion$lambda$2()), new WrapperPrefsStore$reactNativeVersion$2(this, null)
      );
      this.writeChainLock = new Object();
   }

   @NotNull
   public final String getUnityVersion() {
      return (String)this.unityVersion.get$intercom_sdk_base_release();
   }

   @NotNull
   public final String getCordovaVersion() {
      return (String)this.cordovaVersion.get$intercom_sdk_base_release();
   }

   @NotNull
   public final String getReactNativeVersion() {
      return (String)this.reactNativeVersion.get$intercom_sdk_base_release();
   }

   public final void setCordovaVersion(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "version");
      final io.intercom.android.sdk.api.WrapperPrefsStore recoveredExpressionCapture_61_0 = this;
      final java.lang.String recoveredExpressionCapture_61_1 = var1;
      this.enqueueWrite("write cordova version", ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.api.WrapperPrefsStore.setCordovaVersion$lambda$3(recoveredExpressionCapture_61_0, recoveredExpressionCapture_61_1)), new WrapperPrefsStore$setCordovaVersion$2(this, var1, null));
   }

   public final void setReactNativeVersion(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "version");
      final io.intercom.android.sdk.api.WrapperPrefsStore recoveredExpressionCapture_62_0 = this;
      final java.lang.String recoveredExpressionCapture_62_1 = var1;
      this.enqueueWrite(
         "write react native version", ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.api.WrapperPrefsStore.setReactNativeVersion$lambda$4(recoveredExpressionCapture_62_0, recoveredExpressionCapture_62_1)), new WrapperPrefsStore$setReactNativeVersion$2(this, var1, null)
      );
   }

   public final void setUnityVersion(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "version");
      final io.intercom.android.sdk.api.WrapperPrefsStore recoveredExpressionCapture_63_0 = this;
      final java.lang.String recoveredExpressionCapture_63_1 = var1;
      this.enqueueWrite("write unity version", ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.api.WrapperPrefsStore.setUnityVersion$lambda$5(recoveredExpressionCapture_63_0, recoveredExpressionCapture_63_1)), new WrapperPrefsStore$setUnityVersion$2(this, var1, null));
   }

   public final void clear() {
      final io.intercom.android.sdk.api.WrapperPrefsStore recoveredExpressionCapture_60_0 = this;
      this.enqueueWrite("clear DataStore", ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.api.WrapperPrefsStore.clear$lambda$6(recoveredExpressionCapture_60_0)), new WrapperPrefsStore$clear$2(this, null));
   }

   // $VF: Extended synchronized range to monitorexit
   private final void enqueueWrite(String var1, Function0 var2, Function1 var3) {
      synchronized (this.writeChainLock) {
         int var5 = 0;
         var2.invoke();
         Job var6 = this.lastWrite;
         this.lastWrite = BuildersKt.launch$default(this.scope, null, null, new WrapperPrefsStore$enqueueWrite$1$1(var6, this, var1, var3, null), 3, null);
         Unit var8 = Unit.INSTANCE;
      }
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   private final Object writeSafely(String var1, Function1 var2, Continuation var3) {
      WrapperPrefsStore$writeSafely$1 var6;
      label45: {
         if (var3 instanceof WrapperPrefsStore$writeSafely$1) {
            var6 = (WrapperPrefsStore$writeSafely$1)var3;
            if ((var6.label & -2147483648) != 0) {
               var6.label -= Integer.MIN_VALUE;
               break label45;
            }
         }

         var6 = new WrapperPrefsStore$writeSafely$1(this, var3);
      }

      Object var5 = var6.result;
      Object var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var6.label) {
         case 0:
            ResultKt.throwOnFailure(var5);

            Object var10000;
            try {
               var6.L$0 = var1;
               var6.label = 1;
               var10000 = var2.invoke(var6);
            } catch (CancellationException var10) {
               throw var10;
            } catch (Exception var11) {
               twig.w("Failed to " + var1 + " to DataStore: " + var11.getMessage());
               return Unit.INSTANCE;
            }

            if (var10000 == var7) {
               return var7;
            }
            break;
         case 1:
            var1 = (String)var6.L$0;

            try {
               ResultKt.throwOnFailure(var5);
               break;
            } catch (CancellationException var12) {
               throw var12;
            } catch (Exception var13) {
               twig.w("Failed to " + var1 + " to DataStore: " + var13.getMessage());
               return Unit.INSTANCE;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      try {
         ;
      } catch (CancellationException var8) {
         throw var8;
      } catch (Exception var9) {
         twig.w("Failed to " + var1 + " to DataStore: " + var9.getMessage());
      }

      return Unit.INSTANCE;
   }

   private static final String unityVersion$lambda$0() {
      return "";
   }

   private static final String cordovaVersion$lambda$1() {
      return "";
   }

   private static final String reactNativeVersion$lambda$2() {
      return "";
   }

   private static final Unit setCordovaVersion$lambda$3(WrapperPrefsStore var0, String var1) {
      var0.cordovaVersion.set$intercom_sdk_base_release(var1);
      return Unit.INSTANCE;
   }

   private static final Unit setReactNativeVersion$lambda$4(WrapperPrefsStore var0, String var1) {
      var0.reactNativeVersion.set$intercom_sdk_base_release(var1);
      return Unit.INSTANCE;
   }

   private static final Unit setUnityVersion$lambda$5(WrapperPrefsStore var0, String var1) {
      var0.unityVersion.set$intercom_sdk_base_release(var1);
      return Unit.INSTANCE;
   }

   private static final Unit clear$lambda$6(WrapperPrefsStore var0) {
      var0.cordovaVersion.set$intercom_sdk_base_release("");
      var0.reactNativeVersion.set$intercom_sdk_base_release("");
      var0.unityVersion.set$intercom_sdk_base_release("");
      return Unit.INSTANCE;
   }

   private static final List wrapperPrefsDataStore_delegate$lambda$8(Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return CollectionsKt.listOf(Companion.createSharedPrefsMigration$intercom_sdk_base_release(var0));
   }

   @JvmStatic
   @NotNull
   public static final WrapperPrefsStore create(@NotNull Context var0) {
      return Companion.create(var0);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public static final WrapperPrefsStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var0, @NotNull CoroutineScope var1, @NotNull CoroutineDispatcher var2
   ) {
      return Companion.createForTesting$intercom_sdk_base_release(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final DataStore access$getDataStore$p(WrapperPrefsStore var0) {
      return var0.dataStore;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_CORDOVA_VERSION$cp() {
      return KEY_CORDOVA_VERSION;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_REACT_NATIVE_VERSION$cp() {
      return KEY_REACT_NATIVE_VERSION;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_UNITY_VERSION$cp() {
      return KEY_UNITY_VERSION;
   }

   // $VF: synthetic method
   public static final Object access$writeSafely(WrapperPrefsStore var0, String var1, Function1 var2, Continuation var3) {
      return var0.writeSafely(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final ReadOnlyProperty access$getWrapperPrefsDataStore$delegate$cp() {
      return wrapperPrefsDataStore$delegate;
   }

   // $VF: synthetic method
   public static final WrapperPrefsStore access$getInstance$cp() {
      return instance;
   }

   // $VF: synthetic method
   public WrapperPrefsStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, DefaultConstructorMarker var4) {
      this(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final void access$setInstance$cp(WrapperPrefsStore var0) {
      instance = var0;
   }

   // $VF: synthetic method
   public static final CoroutineScope access$getOwnedScope$cp() {
      return ownedScope;
   }

   // $VF: synthetic method
   public static final void access$setOwnedScope$cp(CoroutineScope var0) {
      ownedScope = var0;
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

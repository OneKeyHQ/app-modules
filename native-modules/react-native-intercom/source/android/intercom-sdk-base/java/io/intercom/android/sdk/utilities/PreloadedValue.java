package io.intercom.android.sdk.utilities;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Deferred;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0001\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0002BC\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\b\u0012\u001c\u0010\t\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u000b\u0012\u0006\u0012\u0004\u0018\u00010\u00020\n¢\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u0014\u001a\u00028\u0000H\u0000¢\u0006\u0004\b\u0015\u0010\u0016J\u0017\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0010\u001a\u00028\u0000H\u0000¢\u0006\u0004\b\u0019\u0010\u001aR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\bX\u0082\u0004¢\u0006\u0002\n\u0000R&\u0010\t\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u000b\u0012\u0006\u0012\u0004\u0018\u00010\u00020\nX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u0002X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0010\u001a\u0004\u0018\u00018\u0000X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0011R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00028\u00000\u0013X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/utilities/PreloadedValue;",
         "T",
         "",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "defaultOnError",
         "Lkotlin/Function0;",
         "loader",
         "Lkotlin/Function1;",
         "Lkotlin/coroutines/Continuation;",
         "<init>",
         "(Lkotlinx/coroutines/CoroutineScope;Lkotlinx/coroutines/CoroutineDispatcher;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;)V",
         "Lkotlin/jvm/functions/Function1;",
         "lock",
         "value",
         "Ljava/lang/Object;",
         "preload",
         "Lkotlinx/coroutines/Deferred;",
         "get",
         "get$intercom_sdk_base_release",
         "()Ljava/lang/Object;",
         "set",
         "",
         "set$intercom_sdk_base_release",
         "(Ljava/lang/Object;)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nPreloadedValue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreloadedValue.kt\nio/intercom/android/sdk/utilities/PreloadedValue\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,80:1\n1#2:81\n*E\n"
)
public final class PreloadedValue {
   @NotNull
   private final Function0 defaultOnError;
   @NotNull
   private final Function1 loader;
   @NotNull
   private final Object lock;
   @Nullable
   private volatile Object value;
   @NotNull
   private final Deferred preload;
   public static final int $stable;

   static { $stable = 8; }

   public PreloadedValue(@NotNull CoroutineScope var1, @NotNull CoroutineDispatcher var2, @NotNull Function0 var3, @NotNull Function1 var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "scope");
      Intrinsics.checkNotNullParameter(var2, "dispatcher");
      Intrinsics.checkNotNullParameter(var3, "defaultOnError");
      Intrinsics.checkNotNullParameter(var4, "loader");
      this.defaultOnError = var3;
      this.loader = var4;
      this.lock = new Object();
      this.preload = BuildersKt.async$default(var1, var2, null, new PreloadedValue$preload$1(this, null), 2, null);
      BuildersKt.launch$default(var1, var2, null, new PreloadedValue$1(this, null), 2, null);
   }

   @NotNull
   public final Object get$intercom_sdk_base_release() {
      if (this.value != null) {
         Object var23 = this.value;
         int var25 = 0;
         return var23;
      }

      try {
         Object var1 = BuildersKt.runBlocking$default(null, new PreloadedValue$get$loaded$1(this, null), 1, null);
         synchronized (this.lock) {
            int var24 = 0;
            if (this.value != null) {
               Object var27 = this.value;
               int var29 = 0;
               return var27;
            } else {
               Object var30 = var1;
               Object var31 = var30;
               int var26 = 0;
               this.value = var31;
               return var30;
            }
         }
      } catch (InterruptedException var21) {
         Thread.currentThread().interrupt();
         return this.defaultOnError.invoke();
      } catch (Exception var22) {
         synchronized (this.lock) {
            int var5 = 0;
            if (this.value != null) {
               Object var28 = this.value;
               int var7 = 0;
               return var28;
            } else {
               Object var8 = this.defaultOnError.invoke();
               Object var9 = var8;
               int var6 = 0;
               this.value = var9;
               return var8;
            }
         }
      }
   }

   // $VF: Extended synchronized range to monitorexit
   public final void set$intercom_sdk_base_release(@NotNull Object var1) {
      Intrinsics.checkNotNullParameter(var1, "value");
      synchronized (this.lock) {
         int var3 = 0;
         this.value = var1;
         Unit var6 = Unit.INSTANCE;
      }
   }

   // $VF: synthetic method
   public static final Deferred access$getPreload$p(PreloadedValue var0) {
      return var0.preload;
   }

   // $VF: synthetic method
   public static final Function1 access$getLoader$p(PreloadedValue var0) {
      return var0.loader;
   }

   // $VF: synthetic method
   public static final Function0 access$getDefaultOnError$p(PreloadedValue var0) {
      return var0.defaultOnError;
   }

   // $VF: synthetic method
   public static final Object access$getLock$p(PreloadedValue var0) {
      return var0.lock;
   }

   // $VF: synthetic method
   public static final Object access$getValue$p(PreloadedValue var0) {
      return var0.value;
   }

   // $VF: synthetic method
   public static final void access$setValue$p(PreloadedValue var0, Object var1) {
      var0.value = var1;
   }
}

package io.intercom.android.sdk.utilities;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.datastore.core.InterProcessCoordinator;
import io.intercom.android.sdk.logger.LumberMill;
import java.io.File;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J2\u0010\u0010\u001a\u0002H\u0011\"\u0004\b\u0000\u0010\u00112\u001c\u0010\u0012\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00110\u0014\u0012\u0006\u0012\u0004\u0018\u00010\u00150\u0013H\u0096@¢\u0006\u0002\u0010\u0016J8\u0010\u0017\u001a\u0002H\u0011\"\u0004\b\u0000\u0010\u00112\"\u0010\u0012\u001a\u001e\b\u0001\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00110\u0014\u0012\u0006\u0012\u0004\u0018\u00010\u00150\u0018H\u0096@¢\u0006\u0002\u0010\u0019J\u000e\u0010\u001a\u001a\u00020\u001bH\u0096@¢\u0006\u0002\u0010\u001cJ\u000e\u0010\u001d\u001a\u00020\u001bH\u0096@¢\u0006\u0002\u0010\u001cJ\u0010\u0010\u001e\u001a\u00020\r2\u0006\u0010\u001f\u001a\u00020 H\u0002R\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u000f¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/utilities/NativeCounterFallbackCoordinator;",
         "Landroidx/datastore/core/InterProcessCoordinator;",
         "delegate",
         "versionFile",
         "Ljava/io/File;",
         "<init>",
         "(Landroidx/datastore/core/InterProcessCoordinator;Ljava/io/File;)V",
         "fallbackVersion",
         "Lio/intercom/android/sdk/utilities/MappedVersion;",
         "nativeCounterBroken",
         "",
         "updateNotifications",
         "Lkotlinx/coroutines/flow/Flow;",
         "",
         "getUpdateNotifications",
         "()Lkotlinx/coroutines/flow/Flow;",
         "lock",
         "T",
         "block",
         "Lkotlin/Function1;",
         "Lkotlin/coroutines/Continuation;",
         "",
         "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "tryLock",
         "Lkotlin/Function2;",
         "(Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "getVersion",
         "",
         "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "incrementAndGetVersion",
         "degrade",
         "cause",
         "Ljava/lang/LinkageError;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class NativeCounterFallbackCoordinator implements InterProcessCoordinator {
   @NotNull
   private final InterProcessCoordinator delegate;
   @NotNull
   private final MappedVersion fallbackVersion;
   private volatile boolean nativeCounterBroken;
   public static final int $stable;

   static { $stable = 8; }

   public NativeCounterFallbackCoordinator(@NotNull InterProcessCoordinator var1, @NotNull File var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "delegate");
      Intrinsics.checkNotNullParameter(var2, "versionFile");
      this.delegate = var1;
      this.fallbackVersion = new MappedVersion(var2);
   }

   @NotNull
   @Override
   public Flow getUpdateNotifications() {
      return this.delegate.getUpdateNotifications();
   }

   @Nullable
   @Override
   public Object lock(@NotNull Function1 var1, @NotNull Continuation var2) {
      return this.delegate.lock(var1, var2);
   }

   @Nullable
   @Override
   public Object tryLock(@NotNull Function2 var1, @NotNull Continuation var2) {
      return this.delegate.tryLock(var1, var2);
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Nullable
   @Override
   public Object getVersion(@NotNull Continuation var1) {
      NativeCounterFallbackCoordinator recoveredThis = this;
      NativeCounterFallbackCoordinator$getVersion$1 var5;
      label50: {
         if (var1 instanceof NativeCounterFallbackCoordinator$getVersion$1) {
            var5 = (NativeCounterFallbackCoordinator$getVersion$1)var1;
            if ((var5.label & -2147483648) != 0) {
               var5.label -= Integer.MIN_VALUE;
               break label50;
            }
         }

         var5 = new NativeCounterFallbackCoordinator$getVersion$1(recoveredThis, var1);
      }

      Object var4 = var5.result;
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var5.label) {
         case 0:
            ResultKt.throwOnFailure(var4);
            if (recoveredThis.nativeCounterBroken) {
               return Boxing.boxInt(recoveredThis.fallbackVersion.get());
            }

            try {
               InterProcessCoordinator var10 = recoveredThis.delegate;
               var5.L$0 = recoveredThis;
               var5.label = 1;
               var10000 = var10.getVersion(var5);
            } catch (LinkageError var8) {
               recoveredThis.degrade(var8);
               int var12 = recoveredThis.fallbackVersion.get();
               return Boxing.boxInt(var12);
            }

            if (var10000 == var6) {
               return var6;
            }
            break;
         case 1:
            recoveredThis = (NativeCounterFallbackCoordinator)var5.L$0;

            try {
               ResultKt.throwOnFailure(var4);
               var10000 = var4;
               break;
            } catch (LinkageError var9) {
               recoveredThis.degrade(var9);
               int var2 = recoveredThis.fallbackVersion.get();
               return Boxing.boxInt(var2);
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      int var13;
      try {
         var13 = ((Number)var10000).intValue();
      } catch (LinkageError var7) {
         recoveredThis.degrade(var7);
         var13 = recoveredThis.fallbackVersion.get();
      }

      return Boxing.boxInt(var13);
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Nullable
   @Override
   public Object incrementAndGetVersion(@NotNull Continuation var1) {
      NativeCounterFallbackCoordinator recoveredThis = this;
      NativeCounterFallbackCoordinator$incrementAndGetVersion$1 var5;
      label50: {
         if (var1 instanceof NativeCounterFallbackCoordinator$incrementAndGetVersion$1) {
            var5 = (NativeCounterFallbackCoordinator$incrementAndGetVersion$1)var1;
            if ((var5.label & -2147483648) != 0) {
               var5.label -= Integer.MIN_VALUE;
               break label50;
            }
         }

         var5 = new NativeCounterFallbackCoordinator$incrementAndGetVersion$1(recoveredThis, var1);
      }

      Object var4 = var5.result;
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var5.label) {
         case 0:
            ResultKt.throwOnFailure(var4);
            if (recoveredThis.nativeCounterBroken) {
               return Boxing.boxInt(recoveredThis.fallbackVersion.incrementAndGet());
            }

            try {
               InterProcessCoordinator var10 = recoveredThis.delegate;
               var5.L$0 = recoveredThis;
               var5.label = 1;
               var10000 = var10.incrementAndGetVersion(var5);
            } catch (LinkageError var8) {
               recoveredThis.degrade(var8);
               int var12 = recoveredThis.fallbackVersion.incrementAndGet();
               return Boxing.boxInt(var12);
            }

            if (var10000 == var6) {
               return var6;
            }
            break;
         case 1:
            recoveredThis = (NativeCounterFallbackCoordinator)var5.L$0;

            try {
               ResultKt.throwOnFailure(var4);
               var10000 = var4;
               break;
            } catch (LinkageError var9) {
               recoveredThis.degrade(var9);
               int var2 = recoveredThis.fallbackVersion.incrementAndGet();
               return Boxing.boxInt(var2);
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      int var13;
      try {
         var13 = ((Number)var10000).intValue();
      } catch (LinkageError var7) {
         recoveredThis.degrade(var7);
         var13 = recoveredThis.fallbackVersion.incrementAndGet();
      }

      return Boxing.boxInt(var13);
   }

   private final void degrade(LinkageError var1) {
      if (!this.nativeCounterBroken) {
         this.nativeCounterBroken = true;
         LumberMill.getLogger()
            .w(
               "DataStore shared-counter native library unavailable ("
                  + var1.getMessage()
                  + "); using a memory-mapped version counter. Cross-process locking is unaffected."
            );
      }
   }
}

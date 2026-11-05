package io.intercom.android.sdk.utilities;

import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(f = "PreloadedValue.kt", l = 33, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.utilities.PreloadedValue$preload$1")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u0002H\u0001\"\b\b\u0000\u0010\u0001*\u00020\u0002*\u00020\u0003H\n",
   d2 = {"<anonymous>", "T", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class PreloadedValue$preload$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final PreloadedValue this$0;

   PreloadedValue$preload$1(PreloadedValue var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);

            try {
               Function1 recoveredLoader = PreloadedValue.access$getLoader$p(this.this$0);
               this.label = 1;
               var10000 = recoveredLoader.invoke(this);
            } catch (CancellationException var7) {
               throw var7;
            } catch (Exception var8) {
               return PreloadedValue.access$getDefaultOnError$p(this.this$0).invoke();
            }

            if (var10000 == var4) {
               return var4;
            }
            break;
         case 1:
            try {
               ResultKt.throwOnFailure(var1);
               var10000 = var1;
               break;
            } catch (CancellationException var9) {
               throw var9;
            } catch (Exception var10) {
               return PreloadedValue.access$getDefaultOnError$p(this.this$0).invoke();
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Object var2;
      try {
         var2 = var10000;
      } catch (CancellationException var5) {
         throw var5;
      } catch (Exception var6) {
         var2 = PreloadedValue.access$getDefaultOnError$p(this.this$0).invoke();
      }

      return var2;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new PreloadedValue$preload$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((PreloadedValue$preload$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

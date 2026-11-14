package io.intercom.android.sdk.utilities;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Deferred;

@DebugMetadata(f = "PreloadedValue.kt", l = 43, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.utilities.PreloadedValue$1")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class PreloadedValue$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final PreloadedValue this$0;

   PreloadedValue$1(PreloadedValue var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   // $VF: Extended synchronized range to monitorexit
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var8 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Deferred recoveredPreload = PreloadedValue.access$getPreload$p(this.this$0);
            Continuation var10001 = this;
            this.label = 1;
            var10000 = recoveredPreload.await(var10001);
            if (var10000 == var8) {
               return var8;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Object var2 = var10000;
      Object var3 = PreloadedValue.access$getLock$p(this.this$0);
      PreloadedValue var4 = this.this$0;
      synchronized (var3) {
         int var6 = 0;
         if (PreloadedValue.access$getValue$p(var4) == null) {
            PreloadedValue.access$setValue$p(var4, var2);
         }

         Unit var10 = Unit.INSTANCE;
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new PreloadedValue$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((PreloadedValue$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

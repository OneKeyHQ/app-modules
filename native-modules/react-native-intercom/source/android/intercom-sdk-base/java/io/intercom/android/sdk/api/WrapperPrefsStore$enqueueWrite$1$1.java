package io.intercom.android.sdk.api;

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
import kotlinx.coroutines.Job;

@DebugMetadata(
   f = "WrapperPrefsStore.kt",
   l = {142, 143},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.api.WrapperPrefsStore$enqueueWrite$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class WrapperPrefsStore$enqueueWrite$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Job $previous;
   // $VF: synthetic field
   final WrapperPrefsStore this$0;
   // $VF: synthetic field
   final String $action;
   // $VF: synthetic field
   final Function1 $block;

   WrapperPrefsStore$enqueueWrite$1$1(Job var1, WrapperPrefsStore var2, String var3, Function1 var4, Continuation var5) {
      super(2, var5);
      this.$previous = var1;
      this.this$0 = var2;
      this.$action = var3;
      this.$block = var4;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Job var10000 = this.$previous;
            if (this.$previous != null) {
               Continuation var10001 = this;
               this.label = 1;
               if (var10000.join(var10001) == var2) {
                  return var2;
               }
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         case 2:
            ResultKt.throwOnFailure(var1);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      WrapperPrefsStore var3 = this.this$0;
      String var4 = this.$action;
      Function1 var10002 = this.$block;
      Continuation var10003 = this;
      this.label = 2;
      return WrapperPrefsStore.access$writeSafely(var3, var4, var10002, var10003) == var2 ? var2 : Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new WrapperPrefsStore$enqueueWrite$1$1(this.$previous, this.this$0, this.$action, this.$block, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((WrapperPrefsStore$enqueueWrite$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

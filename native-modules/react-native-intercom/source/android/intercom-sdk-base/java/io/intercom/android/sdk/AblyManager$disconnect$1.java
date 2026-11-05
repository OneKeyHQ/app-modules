package io.intercom.android.sdk;

import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.realtime.ChannelStateListener;
import io.ably.lib.realtime.ConnectionStateListener;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(f = "AblyManager.kt", l = {}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.AblyManager$disconnect$1")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class AblyManager$disconnect$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final AblyManager this$0;

   AblyManager$disconnect$1(AblyManager var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);

            try {
               ChannelStateListener var10000 = AblyManager.access$getRegisteredChannelStateListener$p(this.this$0);
               if (var10000 != null) {
                  ChannelStateListener var3 = var10000;
                  AblyManager var4 = this.this$0;
                  ChannelStateListener var5 = var3;
                  int var6 = 0;
                  Channel var12 = AblyManager.access$getCurrentChannel$p(var4);
                  if (var12 != null) {
                     var12.off(var5);
                  }
               }

               ConnectionStateListener var13 = AblyManager.access$getRegisteredConnectionListener$p(this.this$0);
               if (var13 != null) {
                  ConnectionStateListener var8 = var13;
                  AblyManager var9 = this.this$0;
                  ConnectionStateListener var10 = var8;
                  int var11 = 0;
                  AblyRealtime var14 = AblyManager.access$getAblyRealtime$p(var9);
                  if (var14 != null && var14.connection != null) {
                     var14.connection.off(var10);
                  }
               }

               Channel var16 = AblyManager.access$getCurrentChannel$p(this.this$0);
               if (var16 != null) {
                  var16.unsubscribe();
               }

               AblyRealtime var17 = AblyManager.access$getAblyRealtime$p(this.this$0);
               if (var17 != null && var17.connection != null) {
                  var17.connection.close();
               }

               AblyManager.access$setAblyRealtime$p(this.this$0, null);
               AblyManager.access$setCurrentChannel$p(this.this$0, null);
               AblyManager.access$setMessageListener$p(this.this$0, null);
               AblyManager.access$setConnectionListener$p(this.this$0, null);
               AblyManager.access$setChannelStateListener$p(this.this$0, null);
               AblyManager.access$setRegisteredConnectionListener$p(this.this$0, null);
               AblyManager.access$setRegisteredChannelStateListener$p(this.this$0, null);
               AblyManager.access$setHasChannelBeenAttached$p(this.this$0, false);
               AblyManager.access$set_sharedEventFlow$p(this.this$0, null);
               AblyManager.access$getTwig$p(this.this$0).d("AblyManager: Disconnected from Ably");
            } catch (Exception var7) {
               AblyManager.access$getTwig$p(this.this$0).e("AblyManager: Disconnect error: " + var7.getMessage());
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new AblyManager$disconnect$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((AblyManager$disconnect$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

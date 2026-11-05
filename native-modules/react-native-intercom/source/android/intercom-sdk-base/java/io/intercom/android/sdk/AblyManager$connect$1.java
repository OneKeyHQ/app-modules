package io.intercom.android.sdk;

import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.realtime.ChannelStateListener;
import io.ably.lib.realtime.ConnectionStateListener;
import io.ably.lib.realtime.Channel.MessageListener;
import io.ably.lib.realtime.ChannelStateListener.ChannelStateChange;
import io.ably.lib.realtime.ConnectionStateListener.ConnectionStateChange;
import io.ably.lib.types.ClientOptions;
import io.ably.lib.types.Message;
import io.intercom.android.sdk.models.AblyEndpoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(f = "AblyManager.kt", l = 155, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.AblyManager$connect$1")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nAblyManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AblyManager.kt\nio/intercom/android/sdk/AblyManager$connect$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,329:1\n1374#2:330\n1460#2,5:331\n37#3:336\n36#3,3:337\n*S KotlinDebug\n*F\n+ 1 AblyManager.kt\nio/intercom/android/sdk/AblyManager$connect$1\n*L\n163#1:330\n163#1:331,5\n167#1:336\n167#1:337,3\n*E\n"
)
final class AblyManager$connect$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final AblyManager this$0;

   AblyManager$connect$1(AblyManager var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var21 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);

            try {
               AblyManager.access$getTwig$p(this.this$0).d("AblyManager: Connecting...");
               AblyManager var31 = this.this$0;
               Continuation var10001 = this;
               this.label = 1;
               var10000 = AblyManager.access$getAblyConfiguration(var31, var10001);
            } catch (Exception var23) {
               AblyManager.access$getTwig$p(this.this$0).e("AblyManager: Connection error: " + var23.getMessage());
               return Unit.INSTANCE;
            }

            if (var10000 == var21) {
               return var21;
            }
            break;
         case 1:
            try {
               ResultKt.throwOnFailure(var1);
               var10000 = var1;
               break;
            } catch (Exception var24) {
               AblyManager.access$getTwig$p(this.this$0).e("AblyManager: Connection error: " + var24.getMessage());
               return Unit.INSTANCE;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      try {
         Triple recoveredConfiguration = (Triple)var10000;
         if (recoveredConfiguration == null) {
            return Unit.INSTANCE;
         }

         Triple var2 = recoveredConfiguration;
         String var3 = (String)var2.component1();
         String var4 = (String)var2.component2();
         List var5 = (List)var2.component3();
         ClientOptions var7 = new ClientOptions();
         AblyManager var8 = this.this$0;
         ClientOptions var9 = var7;
         int var10 = 0;
         var9.token = var3;
         var9.authCallback = AblyManager.access$createAuthCallback(var8);
         Iterable var11 = var5;
         int var12 = 0;
         Iterable var13 = var11;
         ArrayList var14 = new ArrayList();
         int var15 = 0;

         for (Object var17 : var13) {
            AblyEndpoint var18 = (AblyEndpoint)var17;
            int var19 = 0;
            Collection var33 = CollectionsKt.listOf(var18.getEndpoint());
            List var39 = var18.getFallback();
            Iterable var30 = CollectionsKt.plus(var33, var39 != null ? var39 : CollectionsKt.emptyList());
            CollectionsKt.addAll(var14, var30);
         }

         List var20 = (List)var14;
         if (!var20.isEmpty()) {
            Collection recoveredFallbackHosts = (Collection)var20;
            var12 = 0;
            var9.fallbackHosts = (String[])recoveredFallbackHosts.toArray(new String[0]);
         }

         ClientOptions var6 = var7;
         AblyManager.access$setAblyRealtime$p(this.this$0, new AblyRealtime(var6));
         final io.intercom.android.sdk.AblyManager recoveredExpressionCapture_1_0 = ((io.intercom.android.sdk.AblyManager$connect$1) this).this$0;
         ConnectionStateListener var25 = ((io.ably.lib.realtime.ConnectionStateListener) (recoveredExpressionArgument_1_0) -> io.intercom.android.sdk.AblyManager$connect$1.invokeSuspend$lambda$2(recoveredExpressionCapture_1_0, (io.ably.lib.realtime.ConnectionStateListener.ConnectionStateChange) (recoveredExpressionArgument_1_0)));
         AblyManager.access$setRegisteredConnectionListener$p(this.this$0, var25);
         AblyRealtime var34 = AblyManager.access$getAblyRealtime$p(this.this$0);
         if (var34 != null && var34.connection != null) {
            var34.connection.on(var25);
         }

         AblyManager var36 = this.this$0;
         AblyRealtime var40 = AblyManager.access$getAblyRealtime$p(this.this$0);
         AblyManager.access$setCurrentChannel$p(var36, var40 != null && var40.channels != null ? var40.channels.get(var4) : null);
         final io.intercom.android.sdk.AblyManager recoveredExpressionCapture_2_0 = ((io.intercom.android.sdk.AblyManager$connect$1) this).this$0;
         ChannelStateListener var26 = ((io.ably.lib.realtime.ChannelStateListener) (recoveredExpressionArgument_2_0) -> io.intercom.android.sdk.AblyManager$connect$1.invokeSuspend$lambda$3(recoveredExpressionCapture_2_0, (io.ably.lib.realtime.ChannelStateListener.ChannelStateChange) (recoveredExpressionArgument_2_0)));
         AblyManager.access$setRegisteredChannelStateListener$p(this.this$0, var26);
         Channel var37 = AblyManager.access$getCurrentChannel$p(this.this$0);
         if (var37 != null) {
            var37.on(var26);
         }

         Channel var38 = AblyManager.access$getCurrentChannel$p(this.this$0);
         if (var38 != null) {
            final io.intercom.android.sdk.AblyManager recoveredExpressionCapture_3_0 = ((io.intercom.android.sdk.AblyManager$connect$1) this).this$0;
            var38.subscribe(((io.ably.lib.realtime.ChannelBase.MessageListener) (recoveredExpressionArgument_3_0) -> io.intercom.android.sdk.AblyManager$connect$1.invokeSuspend$lambda$4(recoveredExpressionCapture_3_0, (io.ably.lib.types.Message) (recoveredExpressionArgument_3_0))));
         }
      } catch (Exception var22) {
         AblyManager.access$getTwig$p(this.this$0).e("AblyManager: Connection error: " + var22.getMessage());
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new AblyManager$connect$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((AblyManager$connect$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final void invokeSuspend$lambda$2(AblyManager var0, ConnectionStateChange var1) {
      String var2 = var1.current.name();
      String var3 = var2;
      if (Intrinsics.areEqual(var3, "connected")) {
         AblyManager.access$getTwig$p(var0).d("AblyManager: Connected");
      } else if (Intrinsics.areEqual(var3, "failed")) {
         AblyManager.access$getTwig$p(var0).e("AblyManager: Connection failed: " + var1.reason);
      }

      ConnectionStateListener var10000 = AblyManager.access$getConnectionListener$p(var0);
      if (var10000 != null) {
         var10000.onConnectionStateChanged(var1);
      }
   }

   private static final void invokeSuspend$lambda$3(AblyManager var0, ChannelStateChange var1) {
      ChannelStateListener var10000 = AblyManager.access$getChannelStateListener$p(var0);
      if (var10000 != null) {
         var10000.onChannelStateChanged(var1);
      }
   }

   private static final void invokeSuspend$lambda$4(AblyManager var0, Message var1) {
      MessageListener var10000 = AblyManager.access$getMessageListener$p(var0);
      if (var10000 != null) {
         var10000.onMessage(var1);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}

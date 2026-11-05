package io.intercom.android.sdk;

import io.ably.lib.realtime.ChannelEvent;
import io.ably.lib.realtime.ChannelStateListener;
import io.ably.lib.realtime.ConnectionStateListener;
import io.ably.lib.realtime.Channel.MessageListener;
import io.ably.lib.realtime.ChannelStateListener.ChannelStateChange;
import io.ably.lib.realtime.ConnectionStateListener.ConnectionStateChange;
import io.ably.lib.types.Message;
import io.intercom.android.sdk.m5.conversation.data.ParsedAblyEvent;
import io.intercom.android.sdk.m5.conversation.data.ParsedAblyEvent$AblyClosed;
import io.intercom.android.sdk.m5.conversation.data.ParsedAblyEvent$AblyConnected;
import io.intercom.android.sdk.m5.conversation.data.ParsedAblyEvent$AblyFailed;
import io.intercom.android.sdk.m5.conversation.data.ParsedAblyEvent$AblyReconnectedWithoutResume;
import io.intercom.android.sdk.m5.conversation.data.ParsedAblyEventKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ProducerScope;

@DebugMetadata(f = "AblyManager.kt", l = 134, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.AblyManager$createAblyEventFlow$1")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/channels/ProducerScope;", "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent;"}
)
final class AblyManager$createAblyEventFlow$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final AblyManager this$0;

   AblyManager$createAblyEventFlow$1(AblyManager var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            ProducerScope var2 = (ProducerScope)this.L$0;
            final io.intercom.android.sdk.AblyManager recoveredExpressionCapture_4_0 = ((io.intercom.android.sdk.AblyManager$createAblyEventFlow$1) this).this$0;
            final kotlinx.coroutines.channels.ProducerScope recoveredExpressionCapture_4_1 = var2;
            MessageListener var3 = ((io.ably.lib.realtime.Channel.MessageListener) (recoveredExpressionArgument_4_0) -> io.intercom.android.sdk.AblyManager$createAblyEventFlow$1.invokeSuspend$lambda$0(recoveredExpressionCapture_4_0, recoveredExpressionCapture_4_1, (io.ably.lib.types.Message) (recoveredExpressionArgument_4_0)));
            final kotlinx.coroutines.channels.ProducerScope recoveredExpressionCapture_5_0 = var2;
            final io.intercom.android.sdk.AblyManager recoveredExpressionCapture_5_1 = ((io.intercom.android.sdk.AblyManager$createAblyEventFlow$1) this).this$0;
            ConnectionStateListener var4 = ((io.ably.lib.realtime.ConnectionStateListener) (recoveredExpressionArgument_5_0) -> io.intercom.android.sdk.AblyManager$createAblyEventFlow$1.invokeSuspend$lambda$1(recoveredExpressionCapture_5_0, recoveredExpressionCapture_5_1, (io.ably.lib.realtime.ConnectionStateListener.ConnectionStateChange) (recoveredExpressionArgument_5_0)));
            final io.intercom.android.sdk.AblyManager recoveredExpressionCapture_6_0 = ((io.intercom.android.sdk.AblyManager$createAblyEventFlow$1) this).this$0;
            final kotlinx.coroutines.channels.ProducerScope recoveredExpressionCapture_6_1 = var2;
            ChannelStateListener var5 = ((io.ably.lib.realtime.ChannelStateListener) (recoveredExpressionArgument_6_0) -> io.intercom.android.sdk.AblyManager$createAblyEventFlow$1.invokeSuspend$lambda$2(recoveredExpressionCapture_6_0, recoveredExpressionCapture_6_1, (io.ably.lib.realtime.ChannelStateListener.ChannelStateChange) (recoveredExpressionArgument_6_0)));
            AblyManager.access$setMessageListener$p(this.this$0, var3);
            AblyManager.access$setConnectionListener$p(this.this$0, var4);
            AblyManager.access$setChannelStateListener$p(this.this$0, var5);
            if (AblyManager.access$getAblyRealtime$p(this.this$0) == null) {
               this.this$0.connect();
            }

            final io.intercom.android.sdk.AblyManager recoveredExpressionCapture_7_0 = ((io.intercom.android.sdk.AblyManager$createAblyEventFlow$1) this).this$0;
            Function0 var10001 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.AblyManager$createAblyEventFlow$1.invokeSuspend$lambda$3(recoveredExpressionCapture_7_0));
            Continuation var10002 = this;
            this.label = 1;
            if (ProduceKt.awaitClose(var2, var10001, var10002) == var6) {
               return var6;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      AblyManager$createAblyEventFlow$1 var3 = new AblyManager$createAblyEventFlow$1(this.this$0, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(ProducerScope var1, Continuation var2) {
      return ((AblyManager$createAblyEventFlow$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final void invokeSuspend$lambda$0(AblyManager var0, ProducerScope var1, Message var2) {
      AblyManager.access$getTwig$p(var0).d("AblyManager: Received message: " + var2.name);
      Intrinsics.checkNotNull(var2);
      ParsedAblyEvent var3 = ParsedAblyEventKt.asAblyData(var2);
      AblyManager.access$getTwig$p(var0).d("AblyManager: Parsed event: " + var3);
      var1.recovered$1b09debba82c0756(var3);
   }

   private static final void invokeSuspend$lambda$1(ProducerScope var0, AblyManager var1, ConnectionStateChange var2) {
      String var3 = var2.current.name();
      String var4 = var3;
      switch (var4.hashCode()) {
         case -1357520532:
            if (var4.equals("closed")) {
               var0.recovered$1b09debba82c0756(ParsedAblyEvent$AblyClosed.INSTANCE);
               AblyManager.access$getTwig$p(var1).d("AblyManager: Connection closed");
            }
            break;
         case -1281977283:
            if (var4.equals("failed")) {
               var0.recovered$1b09debba82c0756(ParsedAblyEvent$AblyFailed.INSTANCE);
               AblyManager.access$getTwig$p(var1).e("AblyManager: Connection failed");
            }
            break;
         case -579210487:
            if (var4.equals("connected")) {
               var0.recovered$1b09debba82c0756(ParsedAblyEvent$AblyConnected.INSTANCE);
            }
      }
   }

   private static final void invokeSuspend$lambda$2(AblyManager var0, ProducerScope var1, ChannelStateChange var2) {
      String var3 = var2.current.name();
      boolean var4 = var2.resumed;
      AblyManager.access$getTwig$p(var0)
         .d("AblyManager: Channel state changed to " + var3 + ", resumed=" + var4 + ", hasBeenAttached=" + AblyManager.access$getHasChannelBeenAttached$p(var0));
      if (!var4 && AblyManager.access$getHasChannelBeenAttached$p(var0) && (Intrinsics.areEqual(var3, "attached") || var2.event == ChannelEvent.update)) {
         AblyManager.access$getTwig$p(var0).d("AblyManager: Channel reconnected without resume - message continuity lost");
         var1.recovered$1b09debba82c0756(ParsedAblyEvent$AblyReconnectedWithoutResume.INSTANCE);
      }

      if (Intrinsics.areEqual(var3, "attached")) {
         AblyManager.access$setHasChannelBeenAttached$p(var0, true);
      }
   }

   private static final Unit invokeSuspend$lambda$3(AblyManager var0) {
      var0.removeListeners();
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((ProducerScope)var1, (Continuation)var2);
   }
}

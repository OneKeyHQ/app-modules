package io.intercom.android.sdk.m5.conversation.data;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nEmitters.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 CombinedEventAsFlow.kt\nio/intercom/android/sdk/m5/conversation/data/CombinedEventAsFlowKt\n*L\n1#1,49:1\n50#2:50\n38#3,14:51\n*E\n"
)
public final class CombinedEventAsFlowKt$combinedEventAsFlow$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2 implements FlowCollector {
   // $VF: synthetic field
   final FlowCollector $this_unsafeFlow;

   public CombinedEventAsFlowKt$combinedEventAsFlow$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2(FlowCollector var1) {
      this.$this_unsafeFlow = var1;
   }

   @Override
   public final Object emit(Object var1, Continuation var2) {
      CombinedEventAsFlowKt$combinedEventAsFlow$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1 var3;
      label28: {
         if (var2 instanceof CombinedEventAsFlowKt$combinedEventAsFlow$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1) {
            var3 = (CombinedEventAsFlowKt$combinedEventAsFlow$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1)var2;
            if ((var3.label & -2147483648) != 0) {
               var3.label -= Integer.MIN_VALUE;
               break label28;
            }
         }

         var3 = new CombinedEventAsFlowKt$combinedEventAsFlow$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1(this, var2);
      }

      Object var4 = var3.result;
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var3.label) {
         case 0: {
            ResultKt.throwOnFailure(var4);
            Object var6 = var1;
            FlowCollector var7 = this.$this_unsafeFlow;
            int var13 = 0;
            FlowCollector var9 = var7;
            ParsedAblyEvent var10 = (ParsedAblyEvent)var6;
            int var11 = 0;
            ParsedAblyEvent var12 = var10;
            ParsedNexusEvent var10000 = var12 instanceof ParsedAblyEvent$ConversationAblyEvent$NewComment
               ? new ParsedNexusEvent$ConversationNexusEvent$NewComment(
                  ((ParsedAblyEvent$ConversationAblyEvent$NewComment)var10).getConversationId(),
                  ((ParsedAblyEvent$ConversationAblyEvent$NewComment)var10).getCreatedByUser(),
                  ((ParsedAblyEvent$ConversationAblyEvent$NewComment)var10).getTicketId(),
                  ((ParsedAblyEvent$ConversationAblyEvent$NewComment)var10).getPartBuilder()
               )
               : (
                  var12 instanceof ParsedAblyEvent$AblyReconnectedWithoutResume
                     ? ParsedNexusEvent$AblyReconnectedWithoutResume.INSTANCE
                     : ParsedNexusEvent$UnSupportedEvent.INSTANCE
               );
            var3.label = 1;
            if (var9.emit(var10000, var3) == var5) {
               return var5;
            }
            break;
         }
         case 1: {
            int var8 = 0;
            ResultKt.throwOnFailure(var4);
            break;
         }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }
}

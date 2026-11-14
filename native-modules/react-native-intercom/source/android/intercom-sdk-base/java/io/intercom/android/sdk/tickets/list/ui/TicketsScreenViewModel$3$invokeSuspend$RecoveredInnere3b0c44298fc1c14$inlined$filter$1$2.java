package io.intercom.android.sdk.tickets.list.ui;

import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$NewComment;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nEmitters.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 TicketsScreenViewModel.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel$3\n*L\n1#1,49:1\n18#2:50\n19#2:52\n71#3:51\n*E\n"
)
public final class TicketsScreenViewModel$3$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1$2 implements FlowCollector {
   // $VF: synthetic field
   final FlowCollector $this_unsafeFlow;

   public TicketsScreenViewModel$3$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1$2(FlowCollector var1) {
      this.$this_unsafeFlow = var1;
   }

   @Override
   public final Object emit(Object var1, Continuation var2) {
      TicketsScreenViewModel$3$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1$2$1 var3;
      label45: {
         if (var2 instanceof TicketsScreenViewModel$3$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1$2$1) {
            var3 = (TicketsScreenViewModel$3$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1$2$1)var2;
            if ((var3.label & -2147483648) != 0) {
               var3.label -= Integer.MIN_VALUE;
               break label45;
            }
         }

         var3 = new TicketsScreenViewModel$3$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1$2$1(this, var2);
      }

      Object var4 = var3.result;
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var3.label) {
         case 0: {
            ResultKt.throwOnFailure(var4);
            Object var6 = var1;
            FlowCollector var7 = this.$this_unsafeFlow;
            int var12 = 0;
            ParsedNexusEvent$ConversationNexusEvent$NewComment var9 = (ParsedNexusEvent$ConversationNexusEvent$NewComment)var6;
            int var10 = 0;
            CharSequence var11 = var9.getTicketId();
            if (var11 != null && var11.length() != 0) {
               var3.label = 1;
               if (var7.emit(var6, var3) == var5) {
                  return var5;
               }
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

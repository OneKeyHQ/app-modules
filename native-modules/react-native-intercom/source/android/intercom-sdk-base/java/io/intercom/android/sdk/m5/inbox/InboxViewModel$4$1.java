package io.intercom.android.sdk.m5.inbox;

import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import java.util.Map;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.Job.DefaultImpls;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.MutableStateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nInboxViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxViewModel.kt\nio/intercom/android/sdk/m5/inbox/InboxViewModel$4$1\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,177:1\n230#2,5:178\n*S KotlinDebug\n*F\n+ 1 InboxViewModel.kt\nio/intercom/android/sdk/m5/inbox/InboxViewModel$4$1\n*L\n121#1:178,5\n*E\n"
)
final class InboxViewModel$4$1 implements FlowCollector {
   // $VF: synthetic field
   final InboxViewModel this$0;

   InboxViewModel$4$1(InboxViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping var1, Continuation var2) {
      String var3 = var1.getConversationId();
      Job var10000 = (Job)InboxViewModel.access$getTypingJobs$p(this.this$0).get(var3);
      if (var10000 != null) {
         DefaultImpls.cancel$default(var10000, (CancellationException)null, 1, null);
      }

      MutableStateFlow var4 = InboxViewModel.access$get_typingConversations$p(this.this$0);
      int var5 = 0;

      Object var6;
      Object var12;
      do {
         var6 = var4.getValue();
         Map var7 = (Map)var6;
         int var8 = 0;
         AvatarWrapper var9 = new AvatarWrapper(var1.getAvatar(), var1.isBot());
         TypingIndicatorType var10 = TypingIndicatorType.ADMIN;
         boolean var11 = var1.getShowAvatar() && !var1.isBot();
         var12 = MapsKt.plus(var7, TuplesKt.to(var3, new CurrentlyTypingState(var9, var11, null, var10, 4, null)));
      } while (!var4.compareAndSet(var6, var12));

      InboxViewModel.access$getTypingJobs$p(this.this$0)
         .put(
            var3, BuildersKt.launch$default(ViewModelKt.getViewModelScope(this.this$0), null, null, new InboxViewModel$4$1$2(this.this$0, var3, null), 3, null)
         );
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping)var1, var2);
   }
}

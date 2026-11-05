package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent;
import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping;
import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$FinStreaming;
import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$NewComment;
import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin;
import io.intercom.android.sdk.m5.conversation.usecase.Action;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.Job.DefaultImpls;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationViewModel$2$2 implements FlowCollector {
   // $VF: synthetic field
   final ConversationViewModel this$0;
   // $VF: synthetic field
   final CoroutineScope $$this$launch;

   ConversationViewModel$2$2(ConversationViewModel var1, CoroutineScope var2) {
      this.this$0 = var1;
      this.$$this$launch = var2;
   }

   public final Object emit(ParsedNexusEvent$ConversationNexusEvent var1, Continuation var2) {
      ParsedNexusEvent$ConversationNexusEvent var3 = var1;
      if (var3 instanceof ParsedNexusEvent$ConversationNexusEvent$NewComment) {
         Action var4 = StringsKt.isBlank(((ParsedNexusEvent$ConversationNexusEvent$NewComment)var1).getCreatedByUser())
            ? Action.OPERATOR_MESSAGE_RECEIVED
            : Action.ADMIN_MESSAGE_RECEIVED;
         if (((ParsedNexusEvent$ConversationNexusEvent$NewComment)var1).getPartBuilder() != null) {
            ConversationViewModel.access$getAppendPartToConversationUseCase$p(this.this$0)
               .invoke(ConversationViewModel.access$getClientState$p(this.this$0), ((ParsedNexusEvent$ConversationNexusEvent$NewComment)var1).getPartBuilder());
            ConversationViewModel.access$getSoundEffectsUseCase$p(this.this$0).invoke$intercom_sdk_base_release(var4);
         } else {
            BuildersKt.launch$default(this.$$this$launch, null, null, new ConversationViewModel$2$2$1(this.this$0, var4, null), 3, null);
         }
      } else if (var3 instanceof ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping) {
         Job var10000 = ConversationViewModel.access$getAdminIsTypingJob$p(this.this$0);
         if (var10000 != null) {
            DefaultImpls.cancel$default(var10000, (CancellationException)null, 1, null);
         }

         ConversationViewModel.access$setAdminIsTypingJob$p(
            this.this$0, BuildersKt.launch$default(this.$$this$launch, null, null, new ConversationViewModel$2$2$2(this.this$0, var1, null), 3, null)
         );
      } else if (var3 instanceof ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin) {
         ConversationViewModel.access$getMarkUserContentAsSeenByAdmin$p(this.this$0).invoke(ConversationViewModel.access$getClientState$p(this.this$0));
      } else if (var3 instanceof ParsedNexusEvent$ConversationNexusEvent$FinStreaming) {
         ConversationViewModel.access$getHandleFinStreamingDeltaUseCase$p(this.this$0)
            .invoke(
               ConversationViewModel.access$getClientState$p(this.this$0),
               ConversationViewModel.access$getFinStreamingStateHolder$p(this.this$0),
               (ParsedNexusEvent$ConversationNexusEvent$FinStreaming)var1
            );
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ParsedNexusEvent$ConversationNexusEvent)var1, var2);
   }
}

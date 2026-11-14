package io.intercom.android.sdk.m5.conversation.ui;

import io.intercom.android.sdk.m5.conversation.ConversationViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$ConversationScreen$16$1 extends FunctionReferenceImpl implements Function0 {
   ConversationScreenKt$ConversationScreen$16$1(Object var1) {
      super(0, var1, ConversationViewModel.class, "cancelVoiceRecording", "cancelVoiceRecording()V", 0);
   }

   public final void recoveredVoidInvoke$d21699c85077d512() {
      ((ConversationViewModel)this.receiver).cancelVoiceRecording();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$d21699c85077d512();
      return Unit.INSTANCE;
   }
}

package io.intercom.android.sdk.m5.conversation.ui;

import io.intercom.android.sdk.m5.conversation.ConversationViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$ConversationScreen$14$1 extends FunctionReferenceImpl implements Function0 {
   ConversationScreenKt$ConversationScreen$14$1(Object var1) {
      super(0, var1, ConversationViewModel.class, "startVoiceRecording", "startVoiceRecording()V", 0);
   }

   public final void recoveredVoidInvoke$ce76f631b9e841a0() {
      ((ConversationViewModel)this.receiver).startVoiceRecording();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$ce76f631b9e841a0();
      return Unit.INSTANCE;
   }
}

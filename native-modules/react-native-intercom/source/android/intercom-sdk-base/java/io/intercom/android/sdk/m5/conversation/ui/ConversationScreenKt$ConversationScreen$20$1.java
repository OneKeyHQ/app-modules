package io.intercom.android.sdk.m5.conversation.ui;

import io.intercom.android.sdk.m5.conversation.ConversationViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$ConversationScreen$20$1 extends FunctionReferenceImpl implements Function0 {
   ConversationScreenKt$ConversationScreen$20$1(Object var1) {
      super(0, var1, ConversationViewModel.class, "clearMediaUploadItems", "clearMediaUploadItems()V", 0);
   }

   public final void recoveredVoidInvoke$a26be75a3897bca1() {
      ((ConversationViewModel)this.receiver).clearMediaUploadItems();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$a26be75a3897bca1();
      return Unit.INSTANCE;
   }
}

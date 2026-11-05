package io.intercom.android.sdk.m5.inbox.ui;

import io.intercom.android.sdk.m5.inbox.InboxViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxScreenKt$InboxScreen$1$1$1 extends FunctionReferenceImpl implements Function0 {
   InboxScreenKt$InboxScreen$1$1$1(Object var1) {
      super(0, var1, InboxViewModel.class, "onRetryOpen", "onRetryOpen()V", 0);
   }

   public final void recoveredVoidInvoke$bc18312e4563a769() {
      ((InboxViewModel)this.receiver).onRetryOpen();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$bc18312e4563a769();
      return Unit.INSTANCE;
   }
}

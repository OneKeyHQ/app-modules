package io.intercom.android.sdk.m5.helpcenter.ui;

import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterScreenKt$HelpCenterScreen$1$1$1 extends FunctionReferenceImpl implements Function0 {
   HelpCenterScreenKt$HelpCenterScreen$1$1$1(Object var1) {
      super(0, var1, HelpCenterViewModel.class, "onRetryOpen", "onRetryOpen()V", 0);
   }

   public final void recoveredVoidInvoke$4f812aabd4e134b2() {
      ((HelpCenterViewModel)this.receiver).onRetryOpen();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$4f812aabd4e134b2();
      return Unit.INSTANCE;
   }
}

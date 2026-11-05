package io.intercom.android.sdk.m5.home;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HomeViewModel$uiState$1$1 extends FunctionReferenceImpl implements Function0 {
   HomeViewModel$uiState$1$1(Object var1) {
      super(0, var1, HomeViewModel.class, "onRetryClicked", "onRetryClicked()V", 0);
   }

   public final void recoveredVoidInvoke$82df0f9d0739bfbc() {
      ((HomeViewModel)this.receiver).onRetryClicked();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$82df0f9d0739bfbc();
      return Unit.INSTANCE;
   }
}

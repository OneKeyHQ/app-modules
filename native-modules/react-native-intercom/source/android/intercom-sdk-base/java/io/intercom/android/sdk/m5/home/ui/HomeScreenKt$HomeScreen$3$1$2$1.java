package io.intercom.android.sdk.m5.home.ui;

import io.intercom.android.sdk.m5.home.HomeViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HomeScreenKt$HomeScreen$3$1$2$1 extends FunctionReferenceImpl implements Function0 {
   HomeScreenKt$HomeScreen$3$1$2$1(Object var1) {
      super(0, var1, HomeViewModel.class, "onHeaderImageLoaded", "onHeaderImageLoaded()V", 0);
   }

   public final void recoveredVoidInvoke$c837018a8b1b607f() {
      ((HomeViewModel)this.receiver).onHeaderImageLoaded();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$c837018a8b1b607f();
      return Unit.INSTANCE;
   }
}

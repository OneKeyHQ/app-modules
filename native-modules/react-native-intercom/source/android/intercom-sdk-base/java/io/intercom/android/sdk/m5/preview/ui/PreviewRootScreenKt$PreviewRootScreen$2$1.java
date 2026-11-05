package io.intercom.android.sdk.m5.preview.ui;

import io.intercom.android.sdk.m5.preview.viewmodel.PreviewViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PreviewRootScreenKt$PreviewRootScreen$2$1 extends FunctionReferenceImpl implements Function1 {
   PreviewRootScreenKt$PreviewRootScreen$2$1(Object var1) {
      super(1, var1, PreviewViewModel.class, "onPageNavigated", "onPageNavigated$intercom_sdk_base_release(I)V", 0);
   }

   public final void invoke(int var1) {
      ((PreviewViewModel)this.receiver).onPageNavigated$intercom_sdk_base_release(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      this.invoke(((Number)var1).intValue());
      return Unit.INSTANCE;
   }
}

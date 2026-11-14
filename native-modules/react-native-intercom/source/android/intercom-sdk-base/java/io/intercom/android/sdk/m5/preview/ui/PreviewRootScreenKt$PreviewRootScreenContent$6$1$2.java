package io.intercom.android.sdk.m5.preview.ui;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PreviewRootScreenKt$PreviewRootScreenContent$6$1$2 implements FlowCollector {
   // $VF: synthetic field
   final Function1 $onPageNavigated;

   PreviewRootScreenKt$PreviewRootScreenContent$6$1$2(Function1 var1) {
      this.$onPageNavigated = var1;
   }

   public final Object emit(int var1, Continuation var2) {
      this.$onPageNavigated.invoke(Boxing.boxInt(var1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit(((Number)var1).intValue(), var2);
   }
}

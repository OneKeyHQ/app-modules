package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class MediaItemsKt$MediaItems$1$1$2$1$1 implements Function0 {
   // $VF: synthetic field
   final Function1 $onItemClick;
   // $VF: synthetic field
   final int $index;

   MediaItemsKt$MediaItems$1$1$2$1$1(Function1 var1, int var2) {
      this.$onItemClick = var1;
      this.$index = var2;
   }

   public final void recoveredVoidInvoke$2b3d92a81d3c32f5() {
      this.$onItemClick.invoke(this.$index);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$2b3d92a81d3c32f5();
      return Unit.INSTANCE;
   }
}

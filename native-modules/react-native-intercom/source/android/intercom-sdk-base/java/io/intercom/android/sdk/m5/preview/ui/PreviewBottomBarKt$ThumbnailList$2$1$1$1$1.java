package io.intercom.android.sdk.m5.preview.ui;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PreviewBottomBarKt$ThumbnailList$2$1$1$1$1 implements Function0 {
   // $VF: synthetic field
   final Function1 $onThumbnailClick;
   // $VF: synthetic field
   final int $index;

   PreviewBottomBarKt$ThumbnailList$2$1$1$1$1(Function1 var1, int var2) {
      this.$onThumbnailClick = var1;
      this.$index = var2;
   }

   public final void recoveredVoidInvoke$9fb4daaa74b15f46() {
      this.$onThumbnailClick.invoke(this.$index);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$9fb4daaa74b15f46();
      return Unit.INSTANCE;
   }
}

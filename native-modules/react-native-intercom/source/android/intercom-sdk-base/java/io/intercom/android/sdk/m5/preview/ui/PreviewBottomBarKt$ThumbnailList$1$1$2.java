package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.runtime.MutableState;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PreviewBottomBarKt$ThumbnailList$1$1$2 implements FlowCollector {
   // $VF: synthetic field
   final MutableState $visibleItems;

   PreviewBottomBarKt$ThumbnailList$1$1$2(MutableState var1) {
      this.$visibleItems = var1;
   }

   public final Object emit(List var1, Continuation var2) {
      this.$visibleItems.setValue(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((List)var1, var2);
   }
}

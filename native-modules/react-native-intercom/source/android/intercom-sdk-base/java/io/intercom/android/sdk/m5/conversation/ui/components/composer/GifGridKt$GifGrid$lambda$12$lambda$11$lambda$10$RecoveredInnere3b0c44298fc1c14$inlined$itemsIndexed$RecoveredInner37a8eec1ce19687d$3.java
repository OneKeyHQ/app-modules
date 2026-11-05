package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class GifGridKt$GifGrid$lambda$12$lambda$11$lambda$10$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3
   implements Function1 {
   // $VF: synthetic field
   final Function2 $span;
   // $VF: synthetic field
   final List $items;

   public GifGridKt$GifGrid$lambda$12$lambda$11$lambda$10$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3(
      Function2 var1, List var2
   ) {
      this.$span = var1;
      this.$items = var2;
   }

   public final StaggeredGridItemSpan invoke(int var1) {
      return (StaggeredGridItemSpan)this.$span.invoke(var1, this.$items.get(var1));
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      return this.invoke(((Number)var1).intValue());
   }
}

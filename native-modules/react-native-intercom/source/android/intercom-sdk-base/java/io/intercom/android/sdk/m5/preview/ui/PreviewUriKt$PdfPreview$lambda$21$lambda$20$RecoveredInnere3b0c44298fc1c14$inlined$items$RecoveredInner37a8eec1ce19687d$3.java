package io.intercom.android.sdk.m5.preview.ui;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class PreviewUriKt$PdfPreview$lambda$21$lambda$20$RecoveredInnere3b0c44298fc1c14$inlined$items$RecoveredInner37a8eec1ce19687d$3
   implements Function1 {
   // $VF: synthetic field
   final Function1 $contentType;
   // $VF: synthetic field
   final List $items;

   public PreviewUriKt$PdfPreview$lambda$21$lambda$20$RecoveredInnere3b0c44298fc1c14$inlined$items$RecoveredInner37a8eec1ce19687d$3(Function1 var1, List var2) {
      this.$contentType = var1;
      this.$items = var2;
   }

   public final Object invoke(int var1) {
      return this.$contentType.invoke(this.$items.get(var1));
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      return this.invoke(((Number)var1).intValue());
   }
}

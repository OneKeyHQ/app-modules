package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.ui.graphics.drawscope.DrawScope;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InlineSourcesSheetContentKt$InlineSourcesSheetContent$1$1$1$2$1$2$2$1$1$1 implements Function1 {
   // $VF: synthetic field
   final long $iconBackgroundColor;

   InlineSourcesSheetContentKt$InlineSourcesSheetContent$1$1$1$2$1$2$2$1$1$1(long var1) {
      this.$iconBackgroundColor = var1;
   }

   public final void invoke(DrawScope var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$drawBehind");
      DrawScope.recovered$34dce0a89fdcb963(var1, this.$iconBackgroundColor, 0.0F, 0L, 0.0F, null, null, 0, 126, null);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      this.invoke((DrawScope)var1);
      return Unit.INSTANCE;
   }
}

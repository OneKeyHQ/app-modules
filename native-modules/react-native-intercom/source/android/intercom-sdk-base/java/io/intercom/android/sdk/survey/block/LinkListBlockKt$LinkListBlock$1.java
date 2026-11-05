package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class LinkListBlockKt$LinkListBlock$1 implements Function3 {
   // $VF: synthetic field
   final Block $block;
   // $VF: synthetic field
   final long $textColor;
   // $VF: synthetic field
   final String $conversationId;

   LinkListBlockKt$LinkListBlock$1(Block var1, long var2, String var4) {
      this.$block = var1;
      this.$textColor = var2;
      this.$conversationId = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C20@611L136:LinkListBlock.kt#jxhu3x");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-46303019, var3, -1, "io.intercom.android.sdk.survey.block.LinkListBlock.<anonymous> (LinkListBlock.kt:20)");
         }

         BlockViewKt.recovered$3dc71cc6f7fc8f68(this.$block, this.$textColor, null, this.$conversationId, var2, 0, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

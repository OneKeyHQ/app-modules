package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.FunctionKeyMeta;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.survey.block.ImageBlockKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nLazyStaggeredGridDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LazyStaggeredGridDsl.kt\nandroidx/compose/foundation/lazy/staggeredgrid/LazyStaggeredGridDslKt$itemsIndexed$5\n+ 2 GifGrid.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/GifGridKt\n*L\n1#1,512:1\n133#2,2:513\n*E\n"
)
public final class GifGridKt$GifGrid$lambda$12$lambda$11$lambda$10$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$4
   implements Function4 {
   // $VF: synthetic field
   final List $items;
   // $VF: synthetic field
   final Function1 $onGifClick$inlined;

   public GifGridKt$GifGrid$lambda$12$lambda$11$lambda$10$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$4(
      List var1, Function1 var2
   ) {
      this.$items = var1;
      this.$onGifClick$inlined = var2;
   }

   @Composable
   @FunctionKeyMeta(key = -1466459515, startOffset = 20470, endOffset = 20515)
   public final void invoke(LazyStaggeredGridItemScope var1, int var2, Composer var3, int var4) {
      ComposerKt.sourceInformation(var3, "CN(index)436@20481L32:LazyStaggeredGridDsl.kt#fzvcnm");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var1) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changed(var2) ? 32 : 16;
      }

      if (var3.shouldExecute((var5 & 147) != 146, var5 & 1)) {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1466459515, var5, -1, "androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed.<anonymous> (LazyStaggeredGridDsl.kt:436)"
            );
         }

         Object var10001 = this.$items.get(var2);
         int var6 = 14 & var5 | 112 & var5;
         Composer var7 = var3;
         Block var8 = (Block)var10001;
         int var10 = 0;
         var7.startReplaceGroup(-700831960);
         ComposerKt.sourceInformation(var7, "C*132@5848L76:GifGrid.kt#yb6g9");
         ImageBlockKt.recovered$5b15bdef32cfadf4(var8, null, this.$onGifClick$inlined, true, null, 0L, var7, 3072 | 14 & var6 >> 6, 50);
         var7.endReplaceGroup();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      } else {
         var3.skipToGroupEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((LazyStaggeredGridItemScope)var1, ((Number)var2).intValue(), (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}

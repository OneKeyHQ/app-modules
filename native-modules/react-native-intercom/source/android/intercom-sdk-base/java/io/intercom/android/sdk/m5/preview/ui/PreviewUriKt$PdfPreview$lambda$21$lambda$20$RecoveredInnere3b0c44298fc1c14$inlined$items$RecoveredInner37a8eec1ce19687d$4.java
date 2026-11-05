package io.intercom.android.sdk.m5.preview.ui;

import android.graphics.Bitmap;
import androidx.compose.foundation.ImageKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.FunctionKeyMeta;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.AndroidImageBitmap_androidKt;
import androidx.compose.ui.graphics.ImageBitmap;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.unit.Dp;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nLazyDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt$items$4\n+ 2 PreviewUri.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewUriKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,523:1\n268#2,4:524\n272#2,5:529\n118#3:528\n*S KotlinDebug\n*F\n+ 1 PreviewUri.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewUriKt\n*L\n271#1:528\n*E\n"
)
public final class PreviewUriKt$PdfPreview$lambda$21$lambda$20$RecoveredInnere3b0c44298fc1c14$inlined$items$RecoveredInner37a8eec1ce19687d$4
   implements Function4 {
   // $VF: synthetic field
   final List $items;

   public PreviewUriKt$PdfPreview$lambda$21$lambda$20$RecoveredInnere3b0c44298fc1c14$inlined$items$RecoveredInner37a8eec1ce19687d$4(List var1) {
      this.$items = var1;
   }

   @Composable
   @FunctionKeyMeta(key = 802480018, startOffset = 8824, endOffset = 8862)
   public final void invoke(LazyItemScope var1, int var2, Composer var3, int var4) {
      ComposerKt.sourceInformation(var3, "CN(it)178@8834L22:LazyDsl.kt#428nma");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var1) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changed(var2) ? 32 : 16;
      }

      if (var3.shouldExecute((var5 & 147) != 146, var5 & 1)) {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(802480018, var5, -1, "androidx.compose.foundation.lazy.items.<anonymous> (LazyDsl.kt:178)");
         }

         Object var10001 = this.$items.get(var2);
         int var6 = 14 & var5;
         Composer var7 = var3;
         Bitmap var8 = (Bitmap)var10001;
         int var10 = 0;
         var7.startReplaceGroup(2098873078);
         ComposerKt.sourceInformation(var7, "C*267@9325L344:PreviewUri.kt#hhbc0c");
         Modifier var10000 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         int var11 = 8;
         int var12 = 0;
         Modifier var13 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060(var11), 7, null);
         ImageBitmap var14 = AndroidImageBitmap_androidKt.asImageBitmap(var8);
         ContentScale var15 = ContentScale.Companion.getFillWidth();
         ImageKt.recovered$53ab2526b008be7a(var14, "Pdf Preview", var13, null, var15, 0.0F, null, 0, var7, 25008, 232);
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
      this.invoke((LazyItemScope)var1, ((Number)var2).intValue(), (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}

package io.intercom.android.sdk.tickets;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.models.FileType;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nFIleAttachmentList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FIleAttachmentList.kt\nio/intercom/android/sdk/tickets/FIleAttachmentListKt$FileAttachment$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,146:1\n118#2:147\n*S KotlinDebug\n*F\n+ 1 FIleAttachmentList.kt\nio/intercom/android/sdk/tickets/FIleAttachmentListKt$FileAttachment$1\n*L\n84#1:147\n*E\n"
)
final class FIleAttachmentListKt$FileAttachment$1 implements Function3 {
   // $VF: synthetic field
   final Function3 $trialingIcon;
   // $VF: synthetic field
   final FileType $fileType;

   FIleAttachmentListKt$FileAttachment$1(Function3 var1, FileType var2) {
      this.$trialingIcon = var1;
      this.$fileType = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BoxedTextLayout");
      ComposerKt.sourceInformation(var2, "C:FIleAttachmentList.kt#3ofz5f");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-344274505, var4, -1, "io.intercom.android.sdk.tickets.FileAttachment.<anonymous> (FIleAttachmentList.kt:72)");
         }

         if (this.$trialingIcon != null) {
            var2.startReplaceGroup(1377521019);
            ComposerKt.sourceInformation(var2, "73@2508L14");
            this.$trialingIcon.invoke(var1, var2, 14 & var4);
            var2.endReplaceGroup();
         } else {
            var2.startReplaceGroup(1377586987);
            ComposerKt.sourceInformation(var2, "84@2936L28,81@2822L216");
            int var10000;
            switch (this.$fileType) {
               case IMAGE:
                  var10000 = drawable.intercom_ic_image;
                  break;
               case VIDEO:
                  var10000 = drawable.intercom_ic_video;
                  break;
               default:
                  var10000 = drawable.intercom_ic_attachment;
            }

            int var5 = var10000;
            Modifier var9 = Modifier.Companion;
            int var7 = 16;
            int var8 = 0;
            Modifier var6 = SizeKt.recovered$111ef94c93e78c04(var9, Dp.recovered$95b3d103663bf060(var7));
            IconKt.recovered$e00adffb7fe45b02(PainterResources_androidKt.painterResource(var5, var2, 0), "Image Icon", var6, 0L, var2, 432 | Painter.$stable, 8);
            var2.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

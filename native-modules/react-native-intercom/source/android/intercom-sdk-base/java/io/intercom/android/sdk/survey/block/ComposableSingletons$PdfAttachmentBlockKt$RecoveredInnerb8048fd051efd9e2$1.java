package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment$Companion;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$PdfAttachmentBlockKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$PdfAttachmentBlockKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$PdfAttachmentBlockKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C194@7403L670:PdfAttachmentBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2127457534,
               var2,
               -1,
               "io.intercom.android.sdk.survey.block.ComposableSingletons$PdfAttachmentBlockKt.lambda-3.<anonymous> (PdfAttachmentBlock.kt:194)"
            );
         }

         Block var10003 = new Block$Builder()
            .withAttachments(
               CollectionsKt.listOf(
                  BlockAttachment$Companion.create$default(
                     BlockAttachment.Companion, "Attachment Name name name name     .pdf", null, "application/pdf", "100 KB", 0L, 0L, 50, null
                  )
               )
            )
            .build();
         Intrinsics.checkNotNullExpressionValue(var10003, "build(...)");
         AttachmentBlockKt.AttachmentBlock(null, new BlockRenderData(var10003, null, null, null, null, 30, null), false, var1, 384, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}

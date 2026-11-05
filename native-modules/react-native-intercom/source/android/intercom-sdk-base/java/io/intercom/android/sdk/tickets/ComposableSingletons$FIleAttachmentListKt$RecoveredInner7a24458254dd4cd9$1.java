package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.models.FileType;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$FilesAttribute$File;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$FIleAttachmentListKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$FIleAttachmentListKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$FIleAttachmentListKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C121@3871L709:FIleAttachmentList.kt#3ofz5f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               597838424,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.ComposableSingletons$FIleAttachmentListKt.lambda-2.<anonymous> (FIleAttachmentList.kt:121)"
            );
         }

         Ticket$TicketAttribute$FilesAttribute$File[] var3 = new Ticket$TicketAttribute$FilesAttribute$File[]{
            new Ticket$TicketAttribute$FilesAttribute$File("", "image.png", "https://www.google.com", FileType.IMAGE),
            new Ticket$TicketAttribute$FilesAttribute$File("", "video.mp4", "https://www.google.com", FileType.VIDEO),
            new Ticket$TicketAttribute$FilesAttribute$File("", "attachment.pdf", "https://www.google.com", FileType.ATTACHMENT)
         };
         FIleAttachmentListKt.FileAttachmentList(null, CollectionsKt.listOf(var3), var1, 0, 1);
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

package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$None;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Queued;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Success;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Uploading;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nFileAttachmentList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileAttachmentList.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileAttachmentListKt$FileAttachmentList$1$1$3\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,124:1\n118#2:125\n*S KotlinDebug\n*F\n+ 1 FileAttachmentList.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileAttachmentListKt$FileAttachmentList$1$1$3\n*L\n56#1:125\n*E\n"
)
final class FileAttachmentListKt$FileAttachmentList$1$1$3 implements Function3 {
   // $VF: synthetic field
   final Answer$MediaAnswer$MediaItem $it;

   FileAttachmentListKt$FileAttachmentList$1$1$3(Answer$MediaAnswer$MediaItem var1) {
      this.$it = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$FileAttachment");
      ComposerKt.sourceInformation(var2, "C55@2489L67:FileAttachmentList.kt#lphomb");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2007803062,
               var3,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.FileAttachmentList.<anonymous>.<anonymous>.<anonymous> (FileAttachmentList.kt:53)"
            );
         }

         Answer$MediaAnswer$FileUploadStatus var4 = this.$it.getUploadStatus();
         if (Intrinsics.areEqual(var4, Answer$MediaAnswer$FileUploadStatus$Uploading.INSTANCE)) {
            Modifier var10000 = Modifier.Companion;
            int var5 = 16;
            int var6 = 0;
            var10000 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var5));
            var5 = 1;
            var6 = 0;
            ProgressIndicatorKt.recovered$276fbc1f701bb35f(var10000, 0L, Dp.recovered$95b3d103663bf060(var5), 0L, 0, 0.0F, var2, 390, 58);
         } else if (!Intrinsics.areEqual(var4, Answer$MediaAnswer$FileUploadStatus$None.INSTANCE)
            && !(var4 instanceof Answer$MediaAnswer$FileUploadStatus$Success)
            && !(var4 instanceof Answer$MediaAnswer$FileUploadStatus$Error)
            && !(var4 instanceof Answer$MediaAnswer$FileUploadStatus$Queued)) {
            throw new NoWhenBranchMatchedException();
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

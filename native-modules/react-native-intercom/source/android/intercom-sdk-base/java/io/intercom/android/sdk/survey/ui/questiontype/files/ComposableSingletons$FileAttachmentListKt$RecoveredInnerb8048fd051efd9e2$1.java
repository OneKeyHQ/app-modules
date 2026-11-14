package io.intercom.android.sdk.survey.ui.questiontype.files;

import android.net.Uri;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileLimitExceeded;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nFileAttachmentList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileAttachmentList.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/ComposableSingletons$FileAttachmentListKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,124:1\n1047#2,6:125\n*S KotlinDebug\n*F\n+ 1 FileAttachmentList.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/ComposableSingletons$FileAttachmentListKt$lambda-3$1\n*L\n119#1:125,6\n*E\n"
)
final class ComposableSingletons$FileAttachmentListKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$FileAttachmentListKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$FileAttachmentListKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C118@4536L2,95@3548L1005:FileAttachmentList.kt#lphomb");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1435954886,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.ComposableSingletons$FileAttachmentListKt.lambda-3.<anonymous> (FileAttachmentList.kt:95)"
            );
         }

         Uri var10002 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         Uri var3 = var10002;
         Answer$MediaAnswer$MediaItem var11 = new Answer$MediaAnswer$MediaItem(
            new MediaData$Media$Image("image/png", 0, 0, 0L, "google.png", var3, null, 64, null)
         );
         Answer$MediaAnswer$MediaItem var4 = var11;
         int var5 = 0;
         var4.setUploadStatus(
            new Answer$MediaAnswer$FileUploadStatus$Error(
               new Answer$MediaAnswer$FileUploadError$FileLimitExceeded(CollectionsKt.listOf(new StringRes(string.intercom_file_max_limit, null, 2, null)))
            )
         );
         List var10000 = CollectionsKt.listOf(var11);
         var1.startReplaceGroup(1128558290);
         ComposerKt.sourceInformation(var1, "CC(remember):FileAttachmentList.kt#9igjgp");
         Composer var13 = var1;
         boolean var14 = false;
         int var6 = 0;
         Object var7 = var13.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function1 var16;
         if (var7 == Composer.Companion.getEmpty()) {
            List var10 = var10000;
            int var9 = 0;
            var16 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1429_0) -> ComposableSingletons$FileAttachmentListKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$2$lambda$1((io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem)recoveredArgument_1429_0));
            var10000 = var10;
            Object var15 = var16;
            var13.updateRememberedValue(var15);
            var16 = (Function1)var15;
         } else {
            var16 = (Function1)var7;
         }

         Function1 var12 = var16;
         var1.endReplaceGroup();
         FileAttachmentListKt.FileAttachmentList(var10000, var12, var1, 48);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1(Answer$MediaAnswer$MediaItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}

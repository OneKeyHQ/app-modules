package io.intercom.android.sdk.survey.ui.questiontype.files;

import android.net.Uri;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
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
   "SMAP\nFileAttachmentList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileAttachmentList.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/ComposableSingletons$FileAttachmentListKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,124:1\n1047#2,6:125\n*S KotlinDebug\n*F\n+ 1 FileAttachmentList.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/ComposableSingletons$FileAttachmentListKt$lambda-1$1\n*L\n85#1:125,6\n*E\n"
)
final class ComposableSingletons$FileAttachmentListKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$FileAttachmentListKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$FileAttachmentListKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C84@3384L2,71@2881L520:FileAttachmentList.kt#lphomb");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1462071998,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.ComposableSingletons$FileAttachmentListKt.lambda-1.<anonymous> (FileAttachmentList.kt:71)"
            );
         }

         Uri var10002 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         Uri var3 = var10002;
         List var10000 = CollectionsKt.listOf(
            new Answer$MediaAnswer$MediaItem(new MediaData$Media$Image("image/png", 0, 0, 0L, "google.png", var3, null, 64, null))
         );
         var1.startReplaceGroup(-1886582192);
         ComposerKt.sourceInformation(var1, "CC(remember):FileAttachmentList.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function1 var13;
         if (var7 == Composer.Companion.getEmpty()) {
            List var10 = var10000;
            int var9 = 0;
            var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1428_0) -> ComposableSingletons$FileAttachmentListKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem)recoveredArgument_1428_0));
            var10000 = var10;
            Object var12 = var13;
            var4.updateRememberedValue(var12);
            var13 = (Function1)var12;
         } else {
            var13 = (Function1)var7;
         }

         Function1 var11 = var13;
         var1.endReplaceGroup();
         FileAttachmentListKt.FileAttachmentList(var10000, var11, var1, 48);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Answer$MediaAnswer$MediaItem var0) {
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

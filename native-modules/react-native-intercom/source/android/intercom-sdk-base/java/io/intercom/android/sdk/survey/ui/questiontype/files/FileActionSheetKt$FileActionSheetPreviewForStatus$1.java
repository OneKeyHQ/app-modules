package io.intercom.android.sdk.survey.ui.questiontype.files;

import android.net.Uri;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nFileActionSheet.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileActionSheet.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileActionSheetKt$FileActionSheetPreviewForStatus$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,133:1\n1047#2,6:134\n1047#2,6:140\n1047#2,6:146\n1047#2,6:152\n*S KotlinDebug\n*F\n+ 1 FileActionSheet.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileActionSheetKt$FileActionSheetPreviewForStatus$1\n*L\n126#1:134,6\n127#1:140,6\n128#1:146,6\n129#1:152,6\n*E\n"
)
final class FileActionSheetKt$FileActionSheetPreviewForStatus$1 implements Function2 {
   // $VF: synthetic field
   final Answer$MediaAnswer$FileUploadStatus $uploadStatus;

   FileActionSheetKt$FileActionSheetPreviewForStatus$1(Answer$MediaAnswer$FileUploadStatus var1) {
      this.$uploadStatus = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C125@4624L2,126@4656L2,127@4690L2,128@4716L2,112@4164L565:FileActionSheet.kt#lphomb");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               193945980, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileActionSheetPreviewForStatus.<anonymous> (FileActionSheet.kt:112)"
            );
         }

         Uri var10002 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         Uri var3 = var10002;
         Answer$MediaAnswer$MediaItem var15 = new Answer$MediaAnswer$MediaItem(
            new MediaData$Media$Image("image/png", 0, 0, 0L, "google.png", var3, null, 64, null)
         );
         Answer$MediaAnswer$FileUploadStatus var4 = this.$uploadStatus;
         Answer$MediaAnswer$MediaItem var5 = var15;
         int var6 = 0;
         var5.setUploadStatus(var4);
         Answer$MediaAnswer$MediaItem var10000 = var15;
         var1.startReplaceGroup(-1862993898);
         ComposerKt.sourceInformation(var1, "CC(remember):FileActionSheet.kt#9igjgp");
         Composer var20 = var1;
         boolean var24 = false;
         var6 = 0;
         Object var7 = var20.rememberedValue();
         int var8 = 0;
         Object var53;
         if (var7 == Composer.Companion.getEmpty()) {
            Answer$MediaAnswer$MediaItem var10 = var15;
            int var9 = 0;
            var53 = ((kotlin.jvm.functions.Function0) () -> FileActionSheetKt$FileActionSheetPreviewForStatus$1.invoke$lambda$2$lambda$1());
            var10000 = var10;
            Object var38 = var53;
            var20.updateRememberedValue(var38);
            var53 = (Function0)var38;
         } else {
            var53 = (Function0)var7;
         }

         Function0 var16 = (kotlin.jvm.functions.Function0) var53;
         var1.endReplaceGroup();
         var53 = var16;
         var1.startReplaceGroup(-1862992874);
         ComposerKt.sourceInformation(var1, "CC(remember):FileActionSheet.kt#9igjgp");
         Composer var21 = var1;
         boolean var25 = false;
         var6 = 0;
         var7 = var21.rememberedValue();
         var8 = 0;
         Object var55;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var11 = var16;
            Answer$MediaAnswer$MediaItem var45 = var10000;
            int var39 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> FileActionSheetKt$FileActionSheetPreviewForStatus$1.invoke$lambda$4$lambda$3());
            var10000 = var45;
            var53 = var11;
            Object var40 = var12;
            var21.updateRememberedValue(var40);
            var55 = (Function0)var40;
         } else {
            var55 = (Function0)var7;
         }

         Function0 var17 = (kotlin.jvm.functions.Function0) var55;
         var1.endReplaceGroup();
         var55 = var17;
         var1.startReplaceGroup(-1862991786);
         ComposerKt.sourceInformation(var1, "CC(remember):FileActionSheet.kt#9igjgp");
         Composer var22 = var1;
         boolean var26 = false;
         var6 = 0;
         var7 = var22.rememberedValue();
         var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var50 = var17;
            Function0 var48 = (kotlin.jvm.functions.Function0) var53;
            Answer$MediaAnswer$MediaItem var46 = var10000;
            int var41 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> FileActionSheetKt$FileActionSheetPreviewForStatus$1.invoke$lambda$6$lambda$5());
            var10000 = var46;
            var53 = var48;
            var55 = var50;
            Object var42 = var13;
            var22.updateRememberedValue(var42);
            var10003 = (Function0)var42;
         } else {
            var10003 = (Function0)var7;
         }

         Function0 var18 = (kotlin.jvm.functions.Function0) var10003;
         var1.endReplaceGroup();
         var10003 = var18;
         var1.startReplaceGroup(-1862990954);
         ComposerKt.sourceInformation(var1, "CC(remember):FileActionSheet.kt#9igjgp");
         Composer var23 = var1;
         boolean var27 = false;
         var6 = 0;
         var7 = var23.rememberedValue();
         var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var52 = var18;
            Function0 var51 = (kotlin.jvm.functions.Function0) var55;
            Function0 var49 = (kotlin.jvm.functions.Function0) var53;
            Answer$MediaAnswer$MediaItem var47 = var10000;
            int var43 = 0;
            Function0 var14 = ((kotlin.jvm.functions.Function0) () -> FileActionSheetKt$FileActionSheetPreviewForStatus$1.invoke$lambda$8$lambda$7());
            var10000 = var47;
            var53 = var49;
            var55 = var51;
            var10003 = var52;
            Object var44 = var14;
            var23.updateRememberedValue(var44);
            var10004 = var44;
         } else {
            var10004 = var7;
         }

         Function0 var19 = (Function0)var10004;
         var1.endReplaceGroup();
         FileActionSheetKt.FileActionSheet(var10000, (kotlin.jvm.functions.Function0) var53, (kotlin.jvm.functions.Function0) var55, (kotlin.jvm.functions.Function0) var10003, var19, var1, 28080);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$4$lambda$3() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$5() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$8$lambda$7() {
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

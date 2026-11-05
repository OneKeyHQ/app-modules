package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nUploadFileQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UploadFileQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/ComposableSingletons$UploadFileQuestionKt$lambda-2$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,115:1\n1047#2,6:116\n*S KotlinDebug\n*F\n+ 1 UploadFileQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/ComposableSingletons$UploadFileQuestionKt$lambda-2$1\n*L\n110#1:116,6\n*E\n"
)
final class ComposableSingletons$UploadFileQuestionKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$UploadFileQuestionKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$UploadFileQuestionKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C109@4868L15,97@4385L509:UploadFileQuestion.kt#lphomb");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -112786639,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.ComposableSingletons$UploadFileQuestionKt.lambda-2.<anonymous> (UploadFileQuestion.kt:97)"
            );
         }

         Object var10000 = null;
         List var10004 = CollectionsKt.listOf(new Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText("Attachments"));
         String[] var3 = new String[]{"image/jpeg", "image/png"};
         SurveyData$Step$Question$UploadFileQuestionModel var10001 = new SurveyData$Step$Question$UploadFileQuestionModel(
            "", var10004, null, false, 1, SetsKt.setOf(var3), 4, null
         );
         Object var10002 = null;
         var1.startReplaceGroup(-2065428552);
         ComposerKt.sourceInformation(var1, "CC(remember):UploadFileQuestion.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var12 = null;
            SurveyData$Step$Question$UploadFileQuestionModel var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1435_0) -> ComposableSingletons$UploadFileQuestionKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.models.Answer)recoveredArgument_1435_0));
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            Object var15 = var13;
            var4.updateRememberedValue(var15);
            var10003 = var15;
         } else {
            var10003 = var7;
         }

         Function1 var14 = (Function1)var10003;
         var1.endReplaceGroup();
         UploadFileQuestionKt.UploadFileQuestion((Modifier)var10000, var10001, (Answer)var10002, var14, null, null, var1, 3072, 53);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Answer var0) {
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

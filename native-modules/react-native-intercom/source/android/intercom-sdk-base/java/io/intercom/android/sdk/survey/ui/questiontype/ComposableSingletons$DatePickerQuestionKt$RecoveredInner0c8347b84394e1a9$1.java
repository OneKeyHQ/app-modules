package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nDatePickerQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/ComposableSingletons$DatePickerQuestionKt$lambda-6$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,329:1\n1047#2,6:330\n*S KotlinDebug\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/ComposableSingletons$DatePickerQuestionKt$lambda-6$1\n*L\n324#1:330,6\n*E\n"
)
final class ComposableSingletons$DatePickerQuestionKt$RecoveredInner0c8347b84394e1a9$1 implements Function2 {
   public static final ComposableSingletons$DatePickerQuestionKt$RecoveredInner0c8347b84394e1a9$1 INSTANCE = new ComposableSingletons$DatePickerQuestionKt$RecoveredInner0c8347b84394e1a9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C323@11964L2,314@11547L434:DatePickerQuestion.kt#uw1bh6");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1513878239,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.ComposableSingletons$DatePickerQuestionKt.lambda-6.<anonymous> (DatePickerQuestion.kt:314)"
            );
         }

         Object var10000 = null;
         SurveyData$Step$Question$DatePickerQuestionModel var10001 = new SurveyData$Step$Question$DatePickerQuestionModel(
            "123", CollectionsKt.listOf(new Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText("Choose date")), true
         );
         Object var10002 = null;
         var1.startReplaceGroup(1421060921);
         ComposerKt.sourceInformation(var1, "CC(remember):DatePickerQuestion.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var12 = null;
            SurveyData$Step$Question$DatePickerQuestionModel var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1372_0) -> ComposableSingletons$DatePickerQuestionKt$RecoveredInner0c8347b84394e1a9$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.models.Answer)recoveredArgument_1372_0));
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            Object var14 = var13;
            var4.updateRememberedValue(var14);
            var10003 = var14;
         } else {
            var10003 = var7;
         }

         Function1 var3 = (Function1)var10003;
         var1.endReplaceGroup();
         DatePickerQuestionKt.DatePickerQuestion((Modifier)var10000, var10001, (Answer)var10002, var3, null, var1, 3072, 21);
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

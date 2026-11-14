package io.intercom.android.sdk.survey.ui.questiontype.files;

import android.content.Context;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.tooling.preview.Preview.Container;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001ah\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\t2\u0013\b\u0002\u0010\f\u001a\r\u0012\u0004\u0012\u00020\u00010\r¢\u0006\u0002\b\u000eH\u0001¢\u0006\u0002\u0010\u000f\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0011¨\u0006\u0012",
   d2 = {
         "UploadFileQuestion",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "questionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$UploadFileQuestionModel;",
         "answer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "onAnswer",
         "Lkotlin/Function1;",
         "onAnswerClick",
         "Lio/intercom/android/sdk/survey/ui/questiontype/AnswerClickData;",
         "questionHeader",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$UploadFileQuestionModel;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "UploadFileQuestionPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nUploadFileQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UploadFileQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/UploadFileQuestionKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,115:1\n1047#2,6:116\n75#3:122\n*S KotlinDebug\n*F\n+ 1 UploadFileQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/UploadFileQuestionKt\n*L\n35#1:116,6\n38#1:122\n*E\n"
)
public final class UploadFileQuestionKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void UploadFileQuestion(
      @Nullable Modifier var0,
      @NotNull SurveyData$Step$Question$UploadFileQuestionModel var1,
      @Nullable Answer var2,
      @NotNull Function1 var3,
      @Nullable Function1 var4,
      @Nullable Function2 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "questionModel");
      Intrinsics.checkNotNullParameter(var3, "onAnswer");
      var6 = var6.startRestartGroup(1688907441);
      ComposerKt.sourceInformation(var6, "C(UploadFileQuestion)P(1,5)34@1685L2,37@1773L7,38@1793L2364,38@1785L2372:UploadFileQuestion.kt#lphomb");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var6.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var6.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var6.changed(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var6.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var6.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var6.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var8 & 4) != 0) {
            var2 = Answer$NoAnswer$InitialNoAnswer.INSTANCE;
         }

         if ((var8 & 16) != 0) {
            var6.startReplaceGroup(573866320);
            ComposerKt.sourceInformation(var6, "CC(remember):UploadFileQuestion.kt#9igjgp");
            Composer var11 = var6;
            boolean var12 = false;
            int var13 = 0;
            Object var14 = var11.rememberedValue();
            int var15 = 0;
            Object var10000;
            if (var14 == Composer.Companion.getEmpty()) {
               int var16 = 0;
               Object var23 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1461_0) -> UploadFileQuestionKt.UploadFileQuestion$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData)recoveredArgument_1461_0));
               var11.updateRememberedValue(var23);
               var10000 = var23;
            } else {
               var10000 = var14;
            }

            Function1 var10 = (Function1)var10000;
            var6.endReplaceGroup();
            var4 = var10;
         }

         if ((var8 & 32) != 0) {
            var5 = ComposableSingletons$UploadFileQuestionKt.INSTANCE.recovered$eac478aa65045e5d();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1688907441, var9, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestion (UploadFileQuestion.kt:36)"
            );
         }

         CompositionLocal var19 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var20 = 0;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var6, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var22 = var6.consume(var19);
         ComposerKt.sourceInformationMarkerEnd(var6);
         Context var18 = (Context)var22;
         SurfaceKt.recovered$ecd79a99e04a9534(
            null,
            null,
            0L,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               1739158412, true, new UploadFileQuestionKt$UploadFileQuestion$2(var0, var5, var2, var4, var1, var18, var3), var6, 54
            ),
            var6,
            12582912,
            127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var24 = var6.endRestartGroup();
      if (var24 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1462_0 = var0;
         final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel recoveredCapture_1462_1 = var1;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1462_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_1462_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredCapture_1462_4 = var4;
         final kotlin.jvm.functions.Function2 recoveredCapture_1462_5 = var5;
         final int recoveredCapture_1462_6 = var7;
         final int recoveredCapture_1462_7 = var8;
         var24.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1462_0, recoveredArgument_1462_1) -> UploadFileQuestionKt.UploadFileQuestion$lambda$2(recoveredCapture_1462_0, recoveredCapture_1462_1, recoveredCapture_1462_2, recoveredCapture_1462_3, recoveredCapture_1462_4, recoveredCapture_1462_5, recoveredCapture_1462_6, recoveredCapture_1462_7, (androidx.compose.runtime.Composer)recoveredArgument_1462_0, ((java.lang.Integer)recoveredArgument_1462_1).intValue())));
      }
   }

   @Container({@Preview(name = "Light Mode", uiMode = 16), @Preview(name = "Dark Mode", uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void UploadFileQuestionPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(21672603);
      ComposerKt.sourceInformation(var0, "C(UploadFileQuestionPreview)96@4361L539:UploadFileQuestion.kt#lphomb");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               21672603, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestionPreview (UploadFileQuestion.kt:95)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$UploadFileQuestionKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1463_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1463_0, recoveredArgument_1463_1) -> UploadFileQuestionKt.UploadFileQuestionPreview$lambda$3(recoveredCapture_1463_0, (androidx.compose.runtime.Composer)recoveredArgument_1463_0, ((java.lang.Integer)recoveredArgument_1463_1).intValue())));
      }
   }

   private static final Unit UploadFileQuestion$lambda$1$lambda$0(AnswerClickData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit UploadFileQuestion$lambda$2(
      Modifier var0,
      SurveyData$Step$Question$UploadFileQuestionModel var1,
      Answer var2,
      Function1 var3,
      Function1 var4,
      Function2 var5,
      int var6,
      int var7,
      Composer var8,
      int var9
   ) {
      UploadFileQuestion(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit UploadFileQuestionPreview$lambda$3(int var0, Composer var1, int var2) {
      UploadFileQuestionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

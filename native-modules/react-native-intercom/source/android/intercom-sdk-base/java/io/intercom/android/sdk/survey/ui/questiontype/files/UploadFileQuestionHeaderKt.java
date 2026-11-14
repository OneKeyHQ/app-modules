package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.text.font.FontWeight;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.ui.components.QuestionHeaderComponentKt;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error;
import io.intercom.android.sdk.ui.common.StringProvider;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0004\b\b\u0010\t¨\u0006\n",
   d2 = {
         "UploadFileQuestionHeader",
         "",
         "questionState",
         "Lio/intercom/android/sdk/survey/QuestionState;",
         "questionFontWeight",
         "Landroidx/compose/ui/text/font/FontWeight;",
         "questionFontSize",
         "Landroidx/compose/ui/unit/TextUnit;",
         "UploadFileQuestionHeader-INMd_9Y",
         "(Lio/intercom/android/sdk/survey/QuestionState;Landroidx/compose/ui/text/font/FontWeight;JLandroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nUploadFileQuestionHeader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UploadFileQuestionHeader.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/UploadFileQuestionHeaderKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,38:1\n1#2:39\n*E\n"
)
public final class UploadFileQuestionHeaderKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$31647a00fc08b5cc(@NotNull QuestionState var0, @NotNull FontWeight var1, long var2, @Nullable Composer var4, int var5) {
      Intrinsics.checkNotNullParameter(var0, "questionState");
      Intrinsics.checkNotNullParameter(var1, "questionFontWeight");
      Composer var17 = var4.startRestartGroup(43160084);
      ComposerKt.sourceInformation(var17, "C(UploadFileQuestionHeader)P(2,1,0:c#ui.unit.TextUnit)32@1269L73,25@927L422:UploadFileQuestionHeader.kt#lphomb");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= var17.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var17.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var17.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var17.getSkipping()) {
         var17.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               43160084, var6, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestionHeader (UploadFileQuestionHeader.kt:16)"
            );
         }

         List var22;
         label68: {
            Answer var9 = var0.getAnswer();
            io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer var8 = var9 instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer ? (io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer)var9 : null;
            if (var8 != null) {
               List var18 = var8.getMediaItems();
               if (var18 != null) {
                  Iterable var12 = var18;
                  Iterator var13 = var12.iterator();

                  Object var10000;
                  while (true) {
                     if (!var13.hasNext()) {
                        var10000 = null;
                        break;
                     }

                     Object var14 = var13.next();
                     io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem var15 = (io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem)var14;
                     int var16 = 0;
                     if (var15.getUploadStatus() instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error) {
                        var10000 = var14;
                        break;
                     }
                  }

                  io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem var10 = (io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem)var10000;
                  if (var10 != null) {
                     io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem var19 = var10;
                     int var20 = 0;
                     io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus var21 = var19.getUploadStatus();
                     Intrinsics.checkNotNull(
                        var21, "null cannot be cast to non-null type io.intercom.android.sdk.survey.ui.models.Answer.MediaAnswer.FileUploadStatus.Error"
                     );
                     List var11 = ((io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error)var21).getError().getErrorMessages();
                     if (var11 != null) {
                        var22 = var11;
                        break label68;
                     }
                  }
               }
            }

            var22 = CollectionsKt.emptyList();
         }

         List var7 = var22;
         QuestionHeaderComponentKt.recovered$5a429c657b5a276d(
            var0.getQuestionModel().getTitle(),
            var0.getQuestionModel().getDescription(),
            var0.getQuestionModel().isRequired(),
            var0.getValidationError(),
            var1,
            var2,
            ComposableLambdaKt.rememberComposableLambda(1052404026, true, new UploadFileQuestionHeaderKt$UploadFileQuestionHeader$1(var7), var17, 54),
            null,
            var17,
            1572864 | StringProvider.$stable << 3 | 57344 & var6 << 9 | 458752 & var6 << 9,
            128
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var23 = var17.endRestartGroup();
      if (var23 != null) {
         final io.intercom.android.sdk.survey.QuestionState recoveredExpressionCapture_1460_0 = var0;
         final androidx.compose.ui.text.font.FontWeight recoveredExpressionCapture_1460_1 = var1;
         final long recoveredExpressionCapture_1460_2 = var2;
         final int recoveredExpressionCapture_1460_3 = var5;
         var23.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1460_0, recoveredExpressionArgument_1460_1) -> io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestionHeaderKt.UploadFileQuestionHeader_INMd_9Y$lambda$2(recoveredExpressionCapture_1460_0, recoveredExpressionCapture_1460_1, recoveredExpressionCapture_1460_2, recoveredExpressionCapture_1460_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1460_0), ((java.lang.Integer)recoveredExpressionArgument_1460_1).intValue())));
      }
   }

   private static final Unit UploadFileQuestionHeader_INMd_9Y$lambda$2(QuestionState var0, FontWeight var1, long var2, int var4, Composer var5, int var6) {
      recovered$31647a00fc08b5cc(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }
}

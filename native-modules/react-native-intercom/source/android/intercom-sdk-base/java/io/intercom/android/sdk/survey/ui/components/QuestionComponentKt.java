package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.relocation.BringIntoViewRequesterKt;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.foundation.text.KeyboardActionScope;
import androidx.compose.material3.CardColors;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.CardElevation;
import androidx.compose.material3.CardKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.focus.FocusManager;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.platform.SoftwareKeyboardController;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.ValidationError$NoValidationError;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
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
   d1 = "\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0087\u0001\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00132\u0014\b\u0002\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0004\b\u0016\u0010\u0017¨\u0006\u0018",
   d2 = {
         "QuestionComponent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "contentModifier",
         "questionState",
         "Lio/intercom/android/sdk/survey/QuestionState;",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "onAnswerUpdated",
         "Lkotlin/Function1;",
         "",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "elevation",
         "Landroidx/compose/ui/unit/Dp;",
         "questionFontWeight",
         "Landroidx/compose/ui/text/font/FontWeight;",
         "questionFontSize",
         "Landroidx/compose/ui/unit/TextUnit;",
         "onAnswerClick",
         "Lio/intercom/android/sdk/survey/ui/questiontype/AnswerClickData;",
         "QuestionComponent-lzVJ5Jw",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/QuestionState;Lio/intercom/android/sdk/survey/SurveyUiColors;Lkotlin/jvm/functions/Function1;JFLandroidx/compose/ui/text/font/FontWeight;JLkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nQuestionComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QuestionComponent.kt\nio/intercom/android/sdk/survey/ui/components/QuestionComponentKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,202:1\n118#2:203\n118#2:204\n1047#3,6:205\n1047#3,6:211\n1047#3,6:219\n75#4:217\n75#4:218\n*S KotlinDebug\n*F\n+ 1 QuestionComponent.kt\nio/intercom/android/sdk/survey/ui/components/QuestionComponentKt\n*L\n47#1:203\n52#1:204\n55#1:205,6\n57#1:211,6\n68#1:219,6\n66#1:217\n67#1:218\n*E\n"
)
public final class QuestionComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$3f1b8998319da4be(
      @Nullable Modifier var0,
      @Nullable Modifier var1,
      @NotNull QuestionState var2,
      @Nullable SurveyUiColors var3,
      @NotNull Function1 var4,
      long var5,
      float var7,
      @Nullable FontWeight var8,
      long var9,
      @Nullable Function1 var11,
      @Nullable Composer var12,
      int var13,
      int var14
   ) {
      Intrinsics.checkNotNullParameter(var2, "questionState");
      Intrinsics.checkNotNullParameter(var4, "onAnswerUpdated");
      Composer var28 = var12.startRestartGroup(435304450);
      ComposerKt.sourceInformation(
         var28,
         "C(QuestionComponent)P(3,1,8,9,5,0:c#ui.graphics.Color,2:c#ui.unit.Dp,7,6:c#ui.unit.TextUnit)50@3025L6,54@3212L2,56@3257L294,65@3613L7,66@3662L7,67@3728L265,75@4031L478,90@4654L67,93@4756L43,94@4831L6,95@4851L4132,87@4515L4468:QuestionComponent.kt#bmpoeo"
      );
      int var15 = var13;
      if ((var14 & 1) != 0) {
         var15 |= 6;
      } else if ((var13 & 6) == 0) {
         var15 |= var28.changed(var0) ? 4 : 2;
      }

      if ((var14 & 2) != 0) {
         var15 |= 48;
      } else if ((var13 & 48) == 0) {
         var15 |= var28.changed(var1) ? 32 : 16;
      }

      if ((var14 & 4) != 0) {
         var15 |= 384;
      } else if ((var13 & 384) == 0) {
         var15 |= var28.changedInstance(var2) ? 256 : 128;
      }

      if ((var13 & 3072) == 0) {
         var15 |= (var14 & 8) == 0 && var28.changed(var3) ? 2048 : 1024;
      }

      if ((var14 & 16) != 0) {
         var15 |= 24576;
      } else if ((var13 & 24576) == 0) {
         var15 |= var28.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var13 & 196608) == 0) {
         var15 |= (var14 & 32) == 0 && var28.changed(var5) ? 131072 : 65536;
      }

      if ((var14 & 64) != 0) {
         var15 |= 1572864;
      } else if ((var13 & 1572864) == 0) {
         var15 |= var28.changed(var7) ? 1048576 : 524288;
      }

      if ((var14 & 128) != 0) {
         var15 |= 12582912;
      } else if ((var13 & 12582912) == 0) {
         var15 |= var28.changed(var8) ? 8388608 : 4194304;
      }

      if ((var14 & 256) != 0) {
         var15 |= 100663296;
      } else if ((var13 & 100663296) == 0) {
         var15 |= var28.changed(var9) ? 67108864 : 33554432;
      }

      if ((var14 & 512) != 0) {
         var15 |= 805306368;
      } else if ((var13 & 805306368) == 0) {
         var15 |= var28.changedInstance(var11) ? 536870912 : 268435456;
      }

      if ((var15 & 306783379) == 306783378 && var28.getSkipping()) {
         var28.skipToGroupEnd();
      } else {
         var28.startDefaults();
         if ((var13 & 1) != 0 && !var28.getDefaultsInvalid()) {
            var28.skipToGroupEnd();
            if ((var14 & 8) != 0) {
               var15 &= -7169;
            }

            if ((var14 & 32) != 0) {
               var15 &= -458753;
            }
         } else {
            if ((var14 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var14 & 2) != 0) {
               Modifier var10000 = Modifier.Companion;
               int var16 = 16;
               int var17 = 0;
               var1 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var16));
            }

            if ((var14 & 8) != 0) {
               var3 = var2.getSurveyUiColors();
               var15 &= -7169;
            }

            if ((var14 & 32) != 0) {
               var5 = IntercomTheme.INSTANCE.getColors(var28, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
               var15 &= -458753;
            }

            if ((var14 & 64) != 0) {
               int var29 = 1;
               int var32 = 0;
               var7 = Dp.recovered$95b3d103663bf060(var29);
            }

            if ((var14 & 128) != 0) {
               var8 = FontWeight.Companion.getNormal();
            }

            if ((var14 & 256) != 0) {
               var9 = TextUnitKt.getSp(16);
            }

            if ((var14 & 512) != 0) {
               var28.startReplaceGroup(-1352076741);
               ComposerKt.sourceInformation(var28, "CC(remember):QuestionComponent.kt#9igjgp");
               Composer var33 = var28;
               boolean var18 = false;
               int var19 = 0;
               Object var20 = var33.rememberedValue();
               int var21 = 0;
               Object var62;
               if (var20 == Composer.Companion.getEmpty()) {
                  int var22 = 0;
                  Object var53 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1332_0) -> io.intercom.android.sdk.survey.ui.components.QuestionComponentKt.QuestionComponent_lzVJ5Jw$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData) ((io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData)recoveredExpressionArgument_1332_0)));
                  var33.updateRememberedValue(var53);
                  var62 = var53;
               } else {
                  var62 = var20;
               }

               Function1 var30 = (Function1)var62;
               var28.endReplaceGroup();
               var11 = var30;
            }
         }

         var28.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(435304450, var15, -1, "io.intercom.android.sdk.survey.ui.components.QuestionComponent (QuestionComponent.kt:55)");
         }

         var28.startReplaceGroup(-1352075009);
         ComposerKt.sourceInformation(var28, "CC(remember):QuestionComponent.kt#9igjgp");
         Composer var36 = var28;
         boolean var39 = var28.changedInstance(var2) | (var15 & 57344) == 16384;
         int var43 = 0;
         Object var48 = var36.rememberedValue();
         int var54 = 0;
         Object var63;
         if (!var39 && var48 != Composer.Companion.getEmpty()) {
            var63 = var48;
         } else {
            int var23 = 0;
            final io.intercom.android.sdk.survey.QuestionState recoveredExpressionCapture_1333_0 = var2;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1333_1 = var4;
            Object var24 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1333_0) -> io.intercom.android.sdk.survey.ui.components.QuestionComponentKt.QuestionComponent_lzVJ5Jw$lambda$3$lambda$2(recoveredExpressionCapture_1333_0, recoveredExpressionCapture_1333_1, (io.intercom.android.sdk.survey.ui.models.Answer) ((io.intercom.android.sdk.survey.ui.models.Answer)recoveredExpressionArgument_1333_0)));
            var36.updateRememberedValue(var24);
            var63 = var24;
         }

         Function1 var34 = (Function1)var63;
         var28.endReplaceGroup();
         Function1 var31 = var34;
         CompositionLocal var37 = CompositionLocalsKt.getLocalSoftwareKeyboardController();
         int var40 = 0;
         int var44 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var49 = var28.consume(var37);
         ComposerKt.sourceInformationMarkerEnd(var28);
         SoftwareKeyboardController var35 = (SoftwareKeyboardController)var49;
         CompositionLocal var41 = CompositionLocalsKt.getLocalFocusManager();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var55 = var28.consume(var41);
         ComposerKt.sourceInformationMarkerEnd(var28);
         FocusManager var38 = (FocusManager)var55;
         var28.startReplaceGroup(-1352059966);
         ComposerKt.sourceInformation(var28, "CC(remember):QuestionComponent.kt#9igjgp");
         Composer var51 = var28;
         boolean var56 = var28.changedInstance(var2) | (var15 & 57344) == 16384 | var28.changed(var35) | var28.changedInstance(var38);
         int $i$f$cachex = 0;
         Object it$ivx = var51.rememberedValue();
         int var25/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var64;
         if (!var56 && it$ivx != Composer.Companion.getEmpty()) {
            var64 = it$ivx;
         } else {
            int var26 = 0;
            final io.intercom.android.sdk.survey.QuestionState recoveredExpressionCapture_1334_0 = var2;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1334_1 = var4;
            final androidx.compose.ui.platform.SoftwareKeyboardController recoveredExpressionCapture_1334_2 = var35;
            final androidx.compose.ui.focus.FocusManager recoveredExpressionCapture_1334_3 = var38;
            Object var27 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1334_0) -> io.intercom.android.sdk.survey.ui.components.QuestionComponentKt.QuestionComponent_lzVJ5Jw$lambda$5$lambda$4(recoveredExpressionCapture_1334_0, recoveredExpressionCapture_1334_1, recoveredExpressionCapture_1334_2, recoveredExpressionCapture_1334_3, (androidx.compose.foundation.text.KeyboardActionScope) ((androidx.compose.foundation.text.KeyboardActionScope)recoveredExpressionArgument_1334_0)));
            var51.updateRememberedValue(var27);
            var64 = var27;
         }

         Function1 var46 = (Function1)var64;
         var28.endReplaceGroup();
         Function1 var42 = var46;
         Function2 var47 = ComposableLambdaKt.rememberComposableLambda(
            1322549775, true, new QuestionComponentKt$QuestionComponent$questionHeader$1(var2, var8, var9), var28, 54
         );
         Modifier var52 = BringIntoViewRequesterKt.bringIntoViewRequester(var0, var2.getBringIntoViewRequester());
         CardColors var57 = CardDefaults.INSTANCE.recovered$da184985090a28d4(var5, 0L, 0L, 0L, var28, 14 & var15 >> 15 | CardDefaults.$stable << 12, 14);
         CardElevation var59 = CardDefaults.INSTANCE
            .recovered$9b4df60457ff5e1b(var7, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var28, 14 & var15 >> 18 | CardDefaults.$stable << 18, 62);
         CornerBasedShape var61 = IntercomTheme.INSTANCE.getShapes(var28, IntercomTheme.$stable).getSmall();
         CardKt.Card(
            var52,
            var61,
            var57,
            var59,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               2001737844, true, new QuestionComponentKt$QuestionComponent$2(var2, var1, var31, var3, var47, var42, var11, var8, var9), var28, 54
            ),
            var28,
            196608,
            16
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var65 = var28.endRestartGroup();
      if (var65 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1335_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1335_1 = var1;
         final io.intercom.android.sdk.survey.QuestionState recoveredExpressionCapture_1335_2 = var2;
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredExpressionCapture_1335_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1335_4 = var4;
         final long recoveredExpressionCapture_1335_5 = var5;
         final float recoveredExpressionCapture_1335_6 = var7;
         final androidx.compose.ui.text.font.FontWeight recoveredExpressionCapture_1335_7 = var8;
         final long recoveredExpressionCapture_1335_8 = var9;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1335_9 = var11;
         final int recoveredExpressionCapture_1335_10 = var13;
         final int recoveredExpressionCapture_1335_11 = var14;
         var65.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1335_0, recoveredExpressionArgument_1335_1) -> io.intercom.android.sdk.survey.ui.components.QuestionComponentKt.QuestionComponent_lzVJ5Jw$lambda$6(recoveredExpressionCapture_1335_0, recoveredExpressionCapture_1335_1, recoveredExpressionCapture_1335_2, recoveredExpressionCapture_1335_3, recoveredExpressionCapture_1335_4, recoveredExpressionCapture_1335_5, recoveredExpressionCapture_1335_6, recoveredExpressionCapture_1335_7, recoveredExpressionCapture_1335_8, recoveredExpressionCapture_1335_9, recoveredExpressionCapture_1335_10, recoveredExpressionCapture_1335_11, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1335_0), ((java.lang.Integer)recoveredExpressionArgument_1335_1).intValue())));
      }
   }

   private static final Unit QuestionComponent_lzVJ5Jw$lambda$1$lambda$0(AnswerClickData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit QuestionComponent_lzVJ5Jw$lambda$3$lambda$2(QuestionState var0, Function1 var1, Answer var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      var0.setAnswer(var2);
      if (!(var0.getQuestionModel() instanceof io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel) && !(var0.getQuestionModel() instanceof io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel)) {
         var0.validate();
      }

      var1.invoke(var0.getQuestionModel().getId());
      return Unit.INSTANCE;
   }

   private static final Unit QuestionComponent_lzVJ5Jw$lambda$5$lambda$4(
      QuestionState var0, Function1 var1, SoftwareKeyboardController var2, FocusManager var3, KeyboardActionScope var4
   ) {
      Intrinsics.checkNotNullParameter(var4, "<this>");
      var0.validate();
      var1.invoke(var0.getQuestionModel().getId());
      if (var0.getValidationError() instanceof io.intercom.android.sdk.survey.ValidationError$NoValidationError) {
         if (var2 != null) {
            var2.hide();
         }

         FocusManager.clearFocus$default(var3, false, 1, null);
      }

      return Unit.INSTANCE;
   }

   private static final Unit QuestionComponent_lzVJ5Jw$lambda$6(
      Modifier var0,
      Modifier var1,
      QuestionState var2,
      SurveyUiColors var3,
      Function1 var4,
      long var5,
      float var7,
      FontWeight var8,
      long var9,
      Function1 var11,
      int var12,
      int var13,
      Composer var14,
      int var15
   ) {
      recovered$3f1b8998319da4be(var0, var1, var2, var3, var4, var5, var7, var8, var9, var11, var14, RecomposeScopeImplKt.updateChangedFlags(var12 | 1), var13);
      return Unit.INSTANCE;
   }
}

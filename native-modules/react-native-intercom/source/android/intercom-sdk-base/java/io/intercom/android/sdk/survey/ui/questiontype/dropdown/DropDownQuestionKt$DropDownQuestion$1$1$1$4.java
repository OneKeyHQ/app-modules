package io.intercom.android.sdk.survey.ui.questiontype.dropdown;

import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.material3.AndroidMenu_androidKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.focus.FocusManager;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nDropDownQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DropDownQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/dropdown/DropDownQuestionKt$DropDownQuestion$1$1$1$4\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,187:1\n1047#2,6:188\n1047#2,6:196\n1878#3,2:194\n1880#3:202\n*S KotlinDebug\n*F\n+ 1 DropDownQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/dropdown/DropDownQuestionKt$DropDownQuestion$1$1$1$4\n*L\n116#1:188,6\n121#1:196,6\n119#1:194,2\n119#1:202\n*E\n"
)
final class DropDownQuestionKt$DropDownQuestion$1$1$1$4 implements Function3 {
   // $VF: synthetic field
   final FocusManager $focusManager;
   // $VF: synthetic field
   final SurveyData$Step$Question$DropDownQuestionModel $dropDownQuestionModel;
   // $VF: synthetic field
   final Function1 $onAnswer;
   // $VF: synthetic field
   final MutableState $expanded$delegate;

   DropDownQuestionKt$DropDownQuestion$1$1$1$4(FocusManager var1, SurveyData$Step$Question$DropDownQuestionModel var2, Function1 var3, MutableState var4) {
      this.$focusManager = var1;
      this.$dropDownQuestionModel = var2;
      this.$onAnswer = var3;
      this.$expanded$delegate = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$DropdownMenu");
      ComposerKt.sourceInformation(var2, "C115@5358L73,115@5332L99,*124@5780L128,120@5596L147,119@5540L395:DropDownQuestion.kt#ppg8nv");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               17506981,
               var3,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestion.<anonymous>.<anonymous>.<anonymous>.<anonymous> (DropDownQuestion.kt:115)"
            );
         }

         String var10000 = "";
         var2.startReplaceGroup(602855941);
         ComposerKt.sourceInformation(var2, "CC(remember):DropDownQuestion.kt#9igjgp");
         Composer var5 = var2;
         boolean var6 = var2.changedInstance(this.$focusManager);
         FocusManager var7 = this.$focusManager;
         int var8 = 0;
         Object var9 = var5.rememberedValue();
         int var10 = 0;
         Object var35;
         if (!var6 && var9 != Composer.Companion.getEmpty()) {
            var35 = (Function2)var9;
         } else {
            String var23 = "";
            int var11 = 0;
            var35 = new DropDownQuestionKt$DropDownQuestion$1$1$1$4$1$1(var7, null);
            var10000 = var23;
            Object var12 = var35;
            var5.updateRememberedValue(var12);
            var35 = (Function2)var12;
         }

         Function2 var4 = (kotlin.jvm.functions.Function2) var35;
         var2.endReplaceGroup();
         EffectsKt.LaunchedEffect(var10000, var4, var2, 6);
         Iterable var24 = this.$dropDownQuestionModel.getOptions();
         Function1 var25 = this.$onAnswer;
         MutableState var26 = this.$expanded$delegate;
         int var27 = 0;
         var8 = 0;

         for (Object var30 : var24) {
            int var31 = var8++;
            if (var31 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            String var32 = (String)var30;
            int var13 = 0;
            Function2 var34 = ComposableLambdaKt.rememberComposableLambda(
               -864903633, true, new DropDownQuestionKt$DropDownQuestion$1$1$1$4$2$1(var32), var2, 54
            );
            var2.startReplaceGroup(55713806);
            ComposerKt.sourceInformation(var2, "CC(remember):DropDownQuestion.kt#9igjgp");
            Composer var14 = var2;
            boolean var15 = var2.changed(var25) | var2.changed(var32);
            int $i$f$cachex = 0;
            Object it$ivx = var14.rememberedValue();
            int var18/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            kotlin.jvm.functions.Function0 var37;
            if (!var15 && it$ivx != Composer.Companion.getEmpty()) {
               var37 = (Function0)it$ivx;
            } else {
               Function2 var19 = var34;
               int var20 = 0;
               final kotlin.jvm.functions.Function1 recoveredCapture_1425_0 = var25;
               final java.lang.String recoveredCapture_1425_1 = var32;
               final androidx.compose.runtime.MutableState recoveredCapture_1425_2 = var26;
               var37 = ((kotlin.jvm.functions.Function0) () -> DropDownQuestionKt$DropDownQuestion$1$1$1$4.invoke$lambda$3$lambda$2$lambda$1(recoveredCapture_1425_0, recoveredCapture_1425_1, recoveredCapture_1425_2));
               var34 = var19;
               Object var21 = var37;
               var14.updateRememberedValue(var21);
               var37 = (Function0)var21;
            }

            Function0 var22 = var37;
            var2.endReplaceGroup();
            AndroidMenu_androidKt.DropdownMenuItem(var34, var22, null, null, null, false, null, null, null, var2, 6, 508);
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$3$lambda$2$lambda$1(Function1 var0, String var1, MutableState var2) {
      var0.invoke(new Answer$SingleAnswer(var1));
      DropDownQuestionKt.access$DropDownQuestion$lambda$2(var2, false);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

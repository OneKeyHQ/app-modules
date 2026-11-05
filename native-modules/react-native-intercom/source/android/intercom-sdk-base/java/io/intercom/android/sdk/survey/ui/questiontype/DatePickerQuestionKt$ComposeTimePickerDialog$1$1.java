package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.material3.TextKt;
import androidx.compose.material3.TimePickerKt;
import androidx.compose.material3.TimePickerState;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nDatePickerQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt$ComposeTimePickerDialog$1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,329:1\n118#2:330\n118#2:363\n118#2:364\n118#2:396\n87#3:331\n84#3,9:332\n94#3:410\n81#4,6:341\n88#4,6:356\n81#4,6:374\n88#4,6:389\n96#4:405\n96#4:409\n402#5,9:347\n411#5:362\n402#5,9:380\n411#5:395\n412#5,2:403\n412#5,2:407\n99#6:365\n97#6,8:366\n106#6:406\n1047#7,6:397\n*S KotlinDebug\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt$ComposeTimePickerDialog$1$1\n*L\n252#1:330\n259#1:363\n261#1:364\n269#1:396\n251#1:331\n251#1:332,9\n251#1:410\n251#1:341,6\n251#1:356,6\n262#1:374,6\n262#1:389,6\n262#1:405\n251#1:409\n251#1:347,9\n251#1:362\n262#1:380,9\n262#1:395\n262#1:403,2\n251#1:407,2\n262#1:365\n262#1:366,8\n262#1:406\n271#1:397,6\n*E\n"
)
final class DatePickerQuestionKt$ComposeTimePickerDialog$1$1 implements Function2 {
   // $VF: synthetic field
   final TimePickerState $timePickerState;
   // $VF: synthetic field
   final Function0 $onDismiss;
   // $VF: synthetic field
   final Answer $answer;
   // $VF: synthetic field
   final Function1 $onAnswer;

   DatePickerQuestionKt$ComposeTimePickerDialog$1$1(TimePickerState var1, Function0 var2, Answer var3, Function1 var4) {
      this.$timePickerState = var1;
      this.$onDismiss = var2;
      this.$answer = var3;
      this.$onAnswer = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C250@9213L1833:DatePickerQuestion.kt#uw1bh6");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1066676752,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.ComposeTimePickerDialog.<anonymous>.<anonymous> (DatePickerQuestion.kt:250)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 24;
         int var4 = 0;
         Modifier var69 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var3));
         Horizontal var5 = Alignment.Companion.getCenterHorizontally();
         TimePickerState var6 = this.$timePickerState;
         Function0 var7 = this.$onDismiss;
         Answer var8 = this.$answer;
         Function1 var9 = this.$onAnswer;
         int var10 = 390;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var70 = Arrangement.INSTANCE.getTop();
         MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var70, var5, var1, 14 & var10 >> 3 | 112 & var10 >> 3);
         Modifier var13 = var69;
         MeasurePolicy var14 = var12;
         Composer var15 = var1;
         int var16 = 112 & var10 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var15;
         int var23 = 6 | 896 & var16 << 6;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var22.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var22.startReusableNode();
         if (var22.getInserting()) {
            var22.createNode(var21);
         } else {
            var22.useNode();
         }

         Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
         int var26 = 0;
         Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var30 = 6 | 112 & var10 >> 6;
         Composer var31 = var28;
         ColumnScope var32 = ColumnScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, -549379990, "C256@9463L10,254@9373L131,258@9521L41,259@9579L35,260@9631L41,261@9689L1343:DatePickerQuestion.kt#uw1bh6"
         );
         TextKt.recovered$5b114ab32a4f4190(
            "Select time",
            null,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            MaterialTheme.INSTANCE.getTypography(var31, MaterialTheme.$stable).getLabelMedium(),
            var31,
            6,
            0,
            131070
         );
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 24;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var31, 6);
         TimePickerKt.recovered$c5b7084de08aac9b(var6, null, null, 0, var31, 0, 14);
         var10000 = Modifier.Companion;
         $this$dp$ivx = 24;
         $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var31, 6);
         Modifier var72 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         androidx.compose.foundation.layout.Arrangement.Horizontal var74 = Arrangement.INSTANCE.getEnd();
         byte var36 = 54;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.ui.Alignment.Vertical var38 = Alignment.Companion.getTop();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var74, var38, var31, 14 & var36 >> 3 | 112 & var36 >> 3);
         Modifier var40 = var72;
         MeasurePolicy var41 = measurePolicy$ivx;
         Composer var42 = var31;
         int var43 = 112 & var36 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var42, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var42, 0));
         CompositionLocalMap localMap$iv$ivx = var42.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var42, var40);
         Function0 var48 = ComposeUiNode.Companion.getConstructor();
         Composer var49 = var42;
         int var50 = 6 | 896 & var43 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var49, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var49.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var49.startReusableNode();
         if (var49.getInserting()) {
            var49.createNode(var48);
         } else {
            var49.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var49);
         int var53/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var41, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var54 = 14 & var50 >> 6;
         Composer $composer$ivx = var49;
         int var56 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var57 = 6 | 112 & var36 >> 6;
         Composer var58 = $composer$ivx;
         RowScope var59 = RowScopeInstance.INSTANCE;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart(var58, 1358134139, "C265@9851L131,268@10003L39,270@10109L786,269@10063L951:DatePickerQuestion.kt#uw1bh6");
         ButtonKt.TextButton(
            var7,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            ComposableSingletons$DatePickerQuestionKt.INSTANCE.recovered$600dea7ea70c63b7(),
            var58,
            805306368,
            510
         );
         var10000 = Modifier.Companion;
         int $this$dp$ivxx = 8;
         int $i$f$getDpxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var58, 6);
         var58.startReplaceGroup(1567839311);
         ComposerKt.sourceInformation(var58, "CC(remember):DatePickerQuestion.kt#9igjgp");
         Composer var76 = var58;
         boolean var63 = var58.changedInstance(var6) | var58.changed(var8) | var58.changed(var9);
         int var64 = 0;
         Object var65 = var76.rememberedValue();
         int var66 = 0;
         Object var80;
         if (!var63 && var65 != Composer.Companion.getEmpty()) {
            var80 = var65;
         } else {
            int var67 = 0;
            final androidx.compose.material3.TimePickerState recoveredCapture_1386_0 = var6;
            final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1386_1 = var8;
            final kotlin.jvm.functions.Function1 recoveredCapture_1386_2 = var9;
            Object var68 = ((kotlin.jvm.functions.Function0) () -> DatePickerQuestionKt$ComposeTimePickerDialog$1$1.invoke$lambda$3$lambda$2$lambda$1$lambda$0(recoveredCapture_1386_0, recoveredCapture_1386_1, recoveredCapture_1386_2));
            var76.updateRememberedValue(var68);
            var80 = var68;
         }

         Function0 var75 = (Function0)var80;
         var58.endReplaceGroup();
         ButtonKt.TextButton(
            var75,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            ComposableSingletons$DatePickerQuestionKt.INSTANCE.recovered$ce8ad24344d60afb(),
            var58,
            805306368,
            510
         );
         ComposerKt.sourceInformationMarkerEnd(var58);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var49.endNode();
         ComposerKt.sourceInformationMarkerEnd(var49);
         ComposerKt.sourceInformationMarkerEnd(var42);
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$3$lambda$2$lambda$1$lambda$0(TimePickerState var0, Answer var1, Function1 var2) {
      List var3 = DatePickerQuestionKt.access$getUtcTime(var0.getHour(), var0.getMinute());
      Answer$DateTimeAnswer var4 = var1 instanceof Answer$DateTimeAnswer
         ? Answer$DateTimeAnswer.copy$default(
            (Answer$DateTimeAnswer)var1, 0L, Integer.parseInt((String)var3.get(0)), Integer.parseInt((String)var3.get(1)), 1, null
         )
         : new Answer$DateTimeAnswer(-1L, Integer.parseInt((String)var3.get(0)), Integer.parseInt((String)var3.get(1)));
      var2.invoke(var4);
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

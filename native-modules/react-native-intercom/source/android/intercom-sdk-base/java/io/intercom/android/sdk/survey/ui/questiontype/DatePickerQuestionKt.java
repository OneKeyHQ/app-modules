package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
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
import androidx.compose.material3.DatePickerDialog_androidKt;
import androidx.compose.material3.DatePickerKt;
import androidx.compose.material3.DatePickerState;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.material3.TimePickerKt;
import androidx.compose.material3.TimePickerState;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.focus.FocusManager;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.window.AndroidDialog_androidKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.TimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000L\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\u001aR\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\u0013\b\u0002\u0010\n\u001a\r\u0012\u0004\u0012\u00020\u00010\u000b¢\u0006\u0002\b\fH\u0001¢\u0006\u0002\u0010\r\u001a1\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\tH\u0003¢\u0006\u0002\u0010\u000f\u001a7\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0003¢\u0006\u0002\u0010\u0012\u001a1\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\tH\u0003¢\u0006\u0002\u0010\u000f\u001a7\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0003¢\u0006\u0002\u0010\u0012\u001a\u0015\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0001¢\u0006\u0002\u0010\u0019\u001a\u001e\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001eH\u0002\u001a\u0016\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\u0006\u0010\u0006\u001a\u00020\u0007H\u0002\u001a\r\u0010!\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\"¨\u0006#²\u0006\n\u0010$\u001a\u00020\u0018X\u008a\u008e\u0002²\u0006\n\u0010%\u001a\u00020\u0018X\u008a\u008e\u0002",
   d2 = {
         "DatePickerQuestion",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "questionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$DatePickerQuestionModel;",
         "answer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "onAnswer",
         "Lkotlin/Function1;",
         "questionHeader",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$DatePickerQuestionModel;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "DatePicker",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "ComposeDatePickerDialog",
         "onDismiss",
         "(Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "TimePicker",
         "ComposeTimePickerDialog",
         "contentAlpha",
         "",
         "enabled",
         "",
         "(ZLandroidx/compose/runtime/Composer;I)F",
         "getUtcTime",
         "",
         "",
         "hour",
         "",
         "minute",
         "getLocalTime",
         "DatePickerQuestionPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release",
         "showDatePicker",
         "showTimePicker"
   }
)
@SourceDebugExtension(
   "SMAP\nDatePickerQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 9 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 10 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 11 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,329:1\n87#2:330\n84#2,9:331\n94#2:402\n81#3,6:340\n88#3,6:355\n81#3,6:372\n88#3,6:387\n96#3:397\n96#3:401\n81#3,6:423\n88#3,6:438\n96#3:449\n81#3,6:483\n88#3,6:498\n96#3:509\n402#4,9:346\n411#4:361\n402#4,9:378\n411#4:393\n412#4,2:395\n412#4,2:399\n402#4,9:429\n411#4:444\n412#4,2:447\n402#4,9:489\n411#4:504\n412#4,2:507\n118#5:362\n118#5:394\n118#5:410\n118#5:445\n118#5:446\n118#5:470\n118#5:505\n118#5:506\n99#6:363\n97#6,8:364\n106#6:398\n99#6,6:417\n106#6:450\n99#6,6:477\n106#6:510\n75#7:403\n75#7:463\n1047#8,6:404\n1047#8,6:411\n1047#8,6:451\n1047#8,6:457\n1047#8,6:464\n1047#8,6:471\n1047#8,6:511\n1047#8,6:517\n1617#9,9:523\n1869#9:532\n1870#9:534\n1626#9:535\n1#10:533\n1#10:536\n85#11:537\n117#11,2:538\n85#11:540\n117#11,2:541\n*S KotlinDebug\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt\n*L\n58#1:330\n58#1:331,9\n58#1:402\n58#1:340,6\n58#1:355,6\n61#1:372,6\n61#1:387,6\n61#1:397\n58#1:401\n79#1:423,6\n79#1:438,6\n79#1:449\n184#1:483,6\n184#1:498,6\n184#1:509\n58#1:346,9\n58#1:361\n61#1:378,9\n61#1:393\n61#1:395,2\n58#1:399,2\n79#1:429,9\n79#1:444\n79#1:447,2\n184#1:489,9\n184#1:504\n184#1:507,2\n60#1:362\n63#1:394\n82#1:410\n98#1:445\n106#1:446\n187#1:470\n203#1:505\n211#1:506\n61#1:363\n61#1:364,8\n61#1:398\n79#1:417,6\n79#1:450\n184#1:477,6\n184#1:510\n71#1:403\n176#1:463\n72#1:404,6\n88#1:411,6\n114#1:451,6\n118#1:457,6\n177#1:464,6\n193#1:471,6\n219#1:511,6\n223#1:517,6\n235#1:523,9\n235#1:532\n235#1:534\n235#1:535\n235#1:533\n72#1:537\n72#1:538,2\n177#1:540\n177#1:541,2\n*E\n"
)
public final class DatePickerQuestionKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void DatePickerQuestion(
      @Nullable Modifier var0,
      @NotNull io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel var1,
      @Nullable Answer var2,
      @NotNull Function1 var3,
      @Nullable Function2 var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "questionModel");
      Intrinsics.checkNotNullParameter(var3, "onAnswer");
      Composer var64 = var5.startRestartGroup(1725209962);
      ComposerKt.sourceInformation(var64, "C(DatePickerQuestion)P(1,4)57@2535L459:DatePickerQuestion.kt#uw1bh6");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var64.changed(var0) ? 4 : 2;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var64.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var64.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var64.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9347) == 9346 && var64.getSkipping()) {
         var64.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var7 & 4) != 0) {
            var2 = io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer.INSTANCE;
         }

         if ((var7 & 16) != 0) {
            var4 = io.intercom.android.sdk.survey.ui.questiontype.ComposableSingletons$DatePickerQuestionKt.INSTANCE.recovered$eac478aa65045e5d();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1725209962, var8, -1, "io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestion (DatePickerQuestion.kt:56)");
         }

         int var11 = 14 & var8;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var64, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var9 = Arrangement.INSTANCE.getTop();
         Horizontal var10 = Alignment.Companion.getStart();
         MeasurePolicy var13 = ColumnKt.columnMeasurePolicy(var9, var10, var64, 14 & var11 >> 3 | 112 & var11 >> 3);
         Modifier var14 = var0;
         MeasurePolicy var15 = var13;
         Composer var16 = var64;
         int var17 = 112 & var11 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var14);
         Function0 var22 = ComposeUiNode.Companion.getConstructor();
         Composer var23 = var16;
         int var24 = 6 | 896 & var17 << 6;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var23.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var23.startReusableNode();
         if (var23.getInserting()) {
            var23.createNode(var22);
         } else {
            var23.useNode();
         }

         Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
         int var27 = 0;
         Updater.recovered$89682b199a142880(var26, var15, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
         int var28 = 14 & var24 >> 6;
         Composer var29 = var23;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var31 = 6 | 112 & var11 >> 6;
         Composer var32 = var29;
         ColumnScope var33 = ColumnScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -2120293457, "C58@2588L8,59@2605L40,60@2654L334:DatePickerQuestion.kt#uw1bh6");
         var4.invoke(var32, 14 & var8 >> 12);
         Modifier var10000 = Modifier.Companion;
         int var35 = 8;
         int var36 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var35)), var32, 6);
         Modifier var65 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         androidx.compose.foundation.layout.Arrangement.Horizontal var66 = Arrangement.INSTANCE.getSpaceBetween();
         int var37 = 54;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.ui.Alignment.Vertical var39 = Alignment.Companion.getTop();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var66, var39, var32, 14 & var37 >> 3 | 112 & var37 >> 3);
         Modifier var41 = var65;
         MeasurePolicy var42 = measurePolicy$ivx;
         Composer var43 = var32;
         int var44 = 112 & var37 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var43, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var43, 0));
         CompositionLocalMap localMap$iv$ivx = var43.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var43, var41);
         Function0 var49 = ComposeUiNode.Companion.getConstructor();
         Composer var50 = var43;
         int var51 = 6 | 896 & var44 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var50, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var50.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var50.startReusableNode();
         if (var50.getInserting()) {
            var50.createNode(var49);
         } else {
            var50.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var50);
         int var54/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var42, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var55 = 14 & var51 >> 6;
         Composer $composer$ivx = var50;
         int var57 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var58 = 6 | 112 & var37 >> 6;
         Composer var59 = $composer$ivx;
         RowScope var60 = RowScopeInstance.INSTANCE;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(var59, 1608212051, "C61@2758L68,62@2839L40,63@2892L86:DatePickerQuestion.kt#uw1bh6");
         DatePicker(SizeKt.fillMaxWidth(Modifier.Companion, 0.5F), var2, var3, var59, 6 | 112 & var8 >> 3 | 896 & var8 >> 3);
         Modifier var67 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var67, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var59, 6);
         TimePicker(SizeKt.fillMaxWidth(Modifier.Companion, 1.0F), var2, var3, var59, 6 | 112 & var8 >> 3 | 896 & var8 >> 3);
         ComposerKt.sourceInformationMarkerEnd(var59);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var50.endNode();
         ComposerKt.sourceInformationMarkerEnd(var50);
         ComposerKt.sourceInformationMarkerEnd(var43);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var64);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var68 = var64.endRestartGroup();
      if (var68 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1379_0 = var0;
         final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel recoveredExpressionCapture_1379_1 = var1;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1379_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1379_3 = var3;
         final kotlin.jvm.functions.Function2 recoveredExpressionCapture_1379_4 = var4;
         final int recoveredExpressionCapture_1379_5 = var6;
         final int recoveredExpressionCapture_1379_6 = var7;
         var68.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1379_0, recoveredExpressionArgument_1379_1) -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.DatePickerQuestion$lambda$2(recoveredExpressionCapture_1379_0, recoveredExpressionCapture_1379_1, recoveredExpressionCapture_1379_2, recoveredExpressionCapture_1379_3, recoveredExpressionCapture_1379_4, recoveredExpressionCapture_1379_5, recoveredExpressionCapture_1379_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1379_0), ((java.lang.Integer)recoveredExpressionArgument_1379_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void DatePicker(Modifier var0, Answer var1, Function1 var2, Composer var3, int var4) {
      Composer var49 = var3.startRestartGroup(2133326452);
      ComposerKt.sourceInformation(
         var49,
         "C(DatePicker)P(1)70@3140L7,71@3174L34,82@3560L6,83@3638L6,85@3698L6,86@3750L6,87@3791L95,78@3438L1179,113@4730L103,117@4859L26,111@4653L243:DatePickerQuestion.kt#uw1bh6"
      );
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var49.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var49.changed(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var49.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var49.getSkipping()) {
         var49.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2133326452, var5, -1, "io.intercom.android.sdk.survey.ui.questiontype.DatePicker (DatePickerQuestion.kt:69)");
         }

         CompositionLocal var7 = CompositionLocalsKt.getLocalFocusManager();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var49, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var49.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var49);
         FocusManager var6 = (FocusManager)var10;
         var49.startReplaceGroup(995395826);
         ComposerKt.sourceInformation(var49, "CC(remember):DatePickerQuestion.kt#9igjgp");
         Composer var53 = var49;
         boolean var55 = false;
         int var11 = 0;
         Object var12 = var53.rememberedValue();
         int var13 = 0;
         Object var10000;
         if (var12 == Composer.Companion.getEmpty()) {
            int var14 = 0;
            Object var74 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var53.updateRememberedValue(var74);
            var10000 = var74;
         } else {
            var10000 = var12;
         }

         MutableState var51 = (MutableState)var10000;
         var49.endReplaceGroup();
         MutableState var50 = var51;
         String var52 = "DD - MM - YYYY";
         String var54 = var1 instanceof io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer && ((io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer)var1).getDate() != -1L
            ? TimeFormatter.formatTimeInMillisAsDate(((io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer)var1).getDate(), "dd - MM - YYYY")
            : var52;
         int var56 = 1;
         int var61 = 0;
         Modifier var95 = BackgroundKt.recovered$42a3174180b3fb7a(
            ClipKt.clip(
               BorderKt.recovered$1227359600bc50fd(
                  var0,
                  Dp.recovered$95b3d103663bf060(var56),
                  Color.recovered$41aa0ee03c2282fb(
                     IntercomTheme.INSTANCE.getColors(var49, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null
                  ),
                  IntercomTheme.INSTANCE.getShapes(var49, IntercomTheme.$stable).getSmall()
               ),
               IntercomTheme.INSTANCE.getShapes(var49, IntercomTheme.$stable).getSmall()
            ),
            IntercomTheme.INSTANCE.getColors(var49, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var49.startReplaceGroup(995415631);
         ComposerKt.sourceInformation(var49, "CC(remember):DatePickerQuestion.kt#9igjgp");
         Composer var62 = var49;
         boolean var66 = var49.changedInstance(var6);
         int $i$f$cachex = 0;
         Object it$ivx = var62.rememberedValue();
         int var15/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10005;
         if (!var66 && it$ivx != Composer.Companion.getEmpty()) {
            var10005 = it$ivx;
         } else {
            Object var47 = null;
            Object var46 = null;
            Object var45 = null;
            boolean var44 = false;
            Modifier var43 = var95;
            int var16 = 0;
            final androidx.compose.ui.focus.FocusManager recoveredExpressionCapture_1376_0 = var6;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1376_1 = var50;
            Function0 var48 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.DatePicker$lambda$7$lambda$6(recoveredExpressionCapture_1376_0, recoveredExpressionCapture_1376_1));
            var95 = var43;
            var10001 = var44;
            var10002 = var45;
            var10003 = var46;
            var10004 = var47;
            Object var17 = var48;
            var62.updateRememberedValue(var17);
            var10005 = var17;
         }

         Function0 var57 = (Function0)var10005;
         var49.endReplaceGroup();
         Modifier var58 = ClickableKt.recovered$1c9660c41fd850dc(
            var95, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var57, 15, null
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var63 = Arrangement.INSTANCE.getSpaceBetween();
         androidx.compose.ui.Alignment.Vertical var67 = Alignment.Companion.getCenterVertically();
         short var71 = 432;
         int var76 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var49, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var79 = RowKt.rowMeasurePolicy(var63, var67, var49, 14 & var71 >> 3 | 112 & var71 >> 3);
         Modifier var82 = var58;
         MeasurePolicy var85 = var79;
         Composer var18 = var49;
         int var19 = 112 & var71 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var82);
         Function0 var24 = ComposeUiNode.Companion.getConstructor();
         Composer var25 = var18;
         int var26 = 6 | 896 & var19 << 6;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var25.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var25.startReusableNode();
         if (var25.getInserting()) {
            var25.createNode(var24);
         } else {
            var25.useNode();
         }

         Composer var28 = Updater.recovered$676aa17b44fd6280(var25);
         int var29 = 0;
         Updater.recovered$89682b199a142880(var28, var85, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var33 = 6 | 112 & var71 >> 6;
         Composer var34 = var31;
         RowScope var35 = RowScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var34, 29308259, "C99@4183L10,100@4236L6,100@4269L43,94@4018L306,103@4361L54,104@4450L54,106@4587L6,102@4333L278:DatePickerQuestion.kt#uw1bh6"
         );
         Intrinsics.checkNotNull(var54);
         String var37 = var54;
         Modifier var96 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var40 = SizeKt.wrapContentSize$default(
            PaddingKt.recovered$eb6a744cd98cdbca(var96, Dp.recovered$95b3d103663bf060($this$dp$ivx)), null, false, 3, null
         );
         TextStyle var89 = IntercomTheme.INSTANCE.getTypography(var34, IntercomTheme.$stable).getType04();
         long var41 = Color.recovered$41aa0ee03c2282fb(
            IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
            contentAlpha(!Intrinsics.areEqual(var54, var52), var34, 0),
            0.0F,
            0.0F,
            0.0F,
            14,
            null
         );
         TextKt.recovered$5b114ab32a4f4190(
            var37, var40, var41, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var89, var34, 48, 0, 131064
         );
         Painter var97 = PainterResources_androidKt.painterResource(drawable.intercom_ic_pick_date, var34, 0);
         String var100 = StringResources_androidKt.stringResource(string.intercom_choose_the_date, var34, 0);
         Modifier var104 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var97,
            var100,
            PaddingKt.recovered$eb6a744cd98cdbca(var104, Dp.recovered$95b3d103663bf060($this$dp$ivxx)),
            IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).recovered$202acdf3dbb5c020(),
            var34,
            384 | Painter.$stable,
            0
         );
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var49);
         if (DatePicker$lambda$4(var50)) {
            Answer var98 = var1;
            var49.startReplaceGroup(995445687);
            ComposerKt.sourceInformation(var49, "CC(remember):DatePickerQuestion.kt#9igjgp");
            Composer var64 = var49;
            boolean var68 = (var5 & 896) == 256;
            int $i$f$cachexx = 0;
            Object it$ivxx = var64.rememberedValue();
            int var80/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var102;
            if (!var68 && it$ivxx != Composer.Companion.getEmpty()) {
               var102 = it$ivxx;
            } else {
               Answer var91 = var1;
               int var83 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1377_0 = var2;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_1377_1 = var50;
               Function1 var101 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1377_0) -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.DatePicker$lambda$10$lambda$9(recoveredExpressionCapture_1377_0, recoveredExpressionCapture_1377_1, (io.intercom.android.sdk.survey.ui.models.Answer) ((io.intercom.android.sdk.survey.ui.models.Answer)recoveredExpressionArgument_1377_0)));
               var98 = var91;
               Object var86 = var101;
               var64.updateRememberedValue(var86);
               var102 = var86;
            }

            Function1 var59 = (Function1)var102;
            var49.endReplaceGroup();
            Function1 var103 = var59;
            var49.startReplaceGroup(995449738);
            ComposerKt.sourceInformation(var49, "CC(remember):DatePickerQuestion.kt#9igjgp");
            Composer var65 = var49;
            boolean var69 = false;
            int $i$f$cachexxx = 0;
            Object it$ivxxx = var65.rememberedValue();
            int var81/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
            Object var105;
            if (it$ivxxx == Composer.Companion.getEmpty()) {
               Function1 var93 = var59;
               Answer var92 = var98;
               int var84 = 0;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_1378_0 = var50;
               Function0 var94 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.DatePicker$lambda$12$lambda$11(recoveredExpressionCapture_1378_0));
               var98 = var92;
               var103 = var93;
               Object var87 = var94;
               var65.updateRememberedValue(var87);
               var105 = var87;
            } else {
               var105 = it$ivxxx;
            }

            Function0 var60 = (Function0)var105;
            var49.endReplaceGroup();
            ComposeDatePickerDialog(var98, var103, var60, var49, 384 | 14 & var5 >> 3);
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var99 = var49.endRestartGroup();
      if (var99 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1375_0 = var0;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1375_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1375_2 = var2;
         final int recoveredExpressionCapture_1375_3 = var4;
         var99.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1375_0, recoveredExpressionArgument_1375_1) -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.DatePicker$lambda$13(recoveredExpressionCapture_1375_0, recoveredExpressionCapture_1375_1, recoveredExpressionCapture_1375_2, recoveredExpressionCapture_1375_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1375_0), ((java.lang.Integer)recoveredExpressionArgument_1375_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ComposeDatePickerDialog(Answer var0, Function1 var1, Function0 var2, Composer var3, int var4) {
      Composer var9 = var3.startRestartGroup(-307045537);
      ComposerKt.sourceInformation(
         var9, "C(ComposeDatePickerDialog)134@5214L66,138@5366L954,160@6346L139,165@6493L122,136@5286L1329:DatePickerQuestion.kt#uw1bh6"
      );
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var9.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var9.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var9.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -307045537, var5, -1, "io.intercom.android.sdk.survey.ui.questiontype.ComposeDatePickerDialog (DatePickerQuestion.kt:127)"
            );
         }

         long var6 = var0 instanceof io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer && ((io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer)var0).getDate() > 0L ? ((io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer)var0).getDate() : System.currentTimeMillis();
         DatePickerState var8 = DatePickerKt.recovered$9a31b396de23dc2d(var6, null, null, 0, null, var9, 0, 30);
         DatePickerDialog_androidKt.recovered$bbbcc46d5888e9c7(
            var2,
            ComposableLambdaKt.rememberComposableLambda(308986865, true, new DatePickerQuestionKt$ComposeDatePickerDialog$1(var8, var0, var1, var2), var9, 54),
            null,
            ComposableLambdaKt.rememberComposableLambda(1712856051, true, new DatePickerQuestionKt$ComposeDatePickerDialog$2(var2), var9, 54),
            null,
            0.0F,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-725444728, true, new DatePickerQuestionKt$ComposeDatePickerDialog$3(var8), var9, 54),
            var9,
            100666416 | 14 & var5 >> 6,
            244
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var9.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1373_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1373_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1373_2 = var2;
         final int recoveredExpressionCapture_1373_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1373_0, recoveredExpressionArgument_1373_1) -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.ComposeDatePickerDialog$lambda$14(recoveredExpressionCapture_1373_0, recoveredExpressionCapture_1373_1, recoveredExpressionCapture_1373_2, recoveredExpressionCapture_1373_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1373_0), ((java.lang.Integer)recoveredExpressionArgument_1373_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TimePicker(Modifier var0, Answer var1, Function1 var2, Composer var3, int var4) {
      Composer var48 = var3.startRestartGroup(1270472949);
      ComposerKt.sourceInformation(
         var48,
         "C(TimePicker)P(1)175@6761L7,176@6795L34,187@7140L6,188@7218L6,190@7278L6,191@7330L6,192@7371L95,183@7018L1179,218@8310L103,222@8439L26,216@8233L243:DatePickerQuestion.kt#uw1bh6"
      );
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var48.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var48.changed(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var48.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var48.getSkipping()) {
         var48.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1270472949, var5, -1, "io.intercom.android.sdk.survey.ui.questiontype.TimePicker (DatePickerQuestion.kt:174)");
         }

         CompositionLocal var7 = CompositionLocalsKt.getLocalFocusManager();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var48, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var48.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var48);
         FocusManager var6 = (FocusManager)var10;
         var48.startReplaceGroup(-1349246543);
         ComposerKt.sourceInformation(var48, "CC(remember):DatePickerQuestion.kt#9igjgp");
         Composer var52 = var48;
         boolean var54 = false;
         int var11 = 0;
         Object var12 = var52.rememberedValue();
         int var13 = 0;
         Object var10000;
         if (var12 == Composer.Companion.getEmpty()) {
            int var14 = 0;
            Object var73 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var52.updateRememberedValue(var73);
            var10000 = var73;
         } else {
            var10000 = var12;
         }

         MutableState var50 = (MutableState)var10000;
         var48.endReplaceGroup();
         MutableState var49 = var50;
         String var51 = "HH:MM";
         String var53 = var1 instanceof io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer && !StringsKt.isBlank(((io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer)var1).getLocalTime())
            ? ((io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer)var1).getLocalTime()
            : var51;
         int var55 = 1;
         int var60 = 0;
         Modifier var94 = BackgroundKt.recovered$42a3174180b3fb7a(
            ClipKt.clip(
               BorderKt.recovered$1227359600bc50fd(
                  var0,
                  Dp.recovered$95b3d103663bf060(var55),
                  Color.recovered$41aa0ee03c2282fb(
                     IntercomTheme.INSTANCE.getColors(var48, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null
                  ),
                  IntercomTheme.INSTANCE.getShapes(var48, IntercomTheme.$stable).getSmall()
               ),
               IntercomTheme.INSTANCE.getShapes(var48, IntercomTheme.$stable).getSmall()
            ),
            IntercomTheme.INSTANCE.getColors(var48, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var48.startReplaceGroup(-1349228050);
         ComposerKt.sourceInformation(var48, "CC(remember):DatePickerQuestion.kt#9igjgp");
         Composer var61 = var48;
         boolean var65 = var48.changedInstance(var6);
         int $i$f$cachex = 0;
         Object it$ivx = var61.rememberedValue();
         int var15/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10005;
         if (!var65 && it$ivx != Composer.Companion.getEmpty()) {
            var10005 = it$ivx;
         } else {
            Object var46 = null;
            Object var45 = null;
            Object var44 = null;
            boolean var43 = false;
            Modifier var42 = var94;
            int var16 = 0;
            final androidx.compose.ui.focus.FocusManager recoveredExpressionCapture_1382_0 = var6;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1382_1 = var49;
            Function0 var47 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.TimePicker$lambda$19$lambda$18(recoveredExpressionCapture_1382_0, recoveredExpressionCapture_1382_1));
            var94 = var42;
            var10001 = var43;
            var10002 = var44;
            var10003 = var45;
            var10004 = var46;
            Object var17 = var47;
            var61.updateRememberedValue(var17);
            var10005 = var17;
         }

         Function0 var56 = (Function0)var10005;
         var48.endReplaceGroup();
         Modifier var57 = ClickableKt.recovered$1c9660c41fd850dc(
            var94, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var56, 15, null
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var62 = Arrangement.INSTANCE.getSpaceBetween();
         androidx.compose.ui.Alignment.Vertical var66 = Alignment.Companion.getCenterVertically();
         short var70 = 432;
         int var75 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var48, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var78 = RowKt.rowMeasurePolicy(var62, var66, var48, 14 & var70 >> 3 | 112 & var70 >> 3);
         Modifier var81 = var57;
         MeasurePolicy var84 = var78;
         Composer var18 = var48;
         int var19 = 112 & var70 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var81);
         Function0 var24 = ComposeUiNode.Companion.getConstructor();
         Composer var25 = var18;
         int var26 = 6 | 896 & var19 << 6;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var25.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var25.startReusableNode();
         if (var25.getInserting()) {
            var25.createNode(var24);
         } else {
            var25.useNode();
         }

         Composer var28 = Updater.recovered$676aa17b44fd6280(var25);
         int var29 = 0;
         Updater.recovered$89682b199a142880(var28, var84, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var33 = 6 | 112 & var70 >> 6;
         Composer var34 = var31;
         RowScope var35 = RowScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var34, 497777700, "C204@7763L10,205@7816L6,205@7849L43,199@7598L306,208@7941L54,209@8030L54,211@8167L6,207@7913L278:DatePickerQuestion.kt#uw1bh6"
         );
         Modifier var95 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var39 = SizeKt.wrapContentSize$default(
            PaddingKt.recovered$eb6a744cd98cdbca(var95, Dp.recovered$95b3d103663bf060($this$dp$ivx)), null, false, 3, null
         );
         TextStyle var87 = IntercomTheme.INSTANCE.getTypography(var34, IntercomTheme.$stable).getType04();
         long var40 = Color.recovered$41aa0ee03c2282fb(
            IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
            contentAlpha(!Intrinsics.areEqual(var53, var51), var34, 0),
            0.0F,
            0.0F,
            0.0F,
            14,
            null
         );
         TextKt.recovered$5b114ab32a4f4190(
            var53, var39, var40, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var87, var34, 48, 0, 131064
         );
         Painter var96 = PainterResources_androidKt.painterResource(drawable.intercom_ic_pick_time, var34, 0);
         String var99 = StringResources_androidKt.stringResource(string.intercom_choose_the_date, var34, 0);
         Modifier var103 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var96,
            var99,
            PaddingKt.recovered$eb6a744cd98cdbca(var103, Dp.recovered$95b3d103663bf060($this$dp$ivxx)),
            IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).recovered$202acdf3dbb5c020(),
            var34,
            384 | Painter.$stable,
            0
         );
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var48);
         if (TimePicker$lambda$16(var49)) {
            Answer var97 = var1;
            var48.startReplaceGroup(-1349197994);
            ComposerKt.sourceInformation(var48, "CC(remember):DatePickerQuestion.kt#9igjgp");
            Composer var63 = var48;
            boolean var67 = (var5 & 896) == 256;
            int $i$f$cachexx = 0;
            Object it$ivxx = var63.rememberedValue();
            int var79/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var101;
            if (!var67 && it$ivxx != Composer.Companion.getEmpty()) {
               var101 = it$ivxx;
            } else {
               Answer var90 = var1;
               int var82 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1383_0 = var2;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_1383_1 = var49;
               Function1 var100 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1383_0) -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.TimePicker$lambda$22$lambda$21(recoveredExpressionCapture_1383_0, recoveredExpressionCapture_1383_1, (io.intercom.android.sdk.survey.ui.models.Answer) ((io.intercom.android.sdk.survey.ui.models.Answer)recoveredExpressionArgument_1383_0)));
               var97 = var90;
               Object var85 = var100;
               var63.updateRememberedValue(var85);
               var101 = var85;
            }

            Function1 var58 = (Function1)var101;
            var48.endReplaceGroup();
            Function1 var102 = var58;
            var48.startReplaceGroup(-1349193943);
            ComposerKt.sourceInformation(var48, "CC(remember):DatePickerQuestion.kt#9igjgp");
            Composer var64 = var48;
            boolean var68 = false;
            int $i$f$cachexxx = 0;
            Object it$ivxxx = var64.rememberedValue();
            int var80/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
            Object var104;
            if (it$ivxxx == Composer.Companion.getEmpty()) {
               Function1 var92 = var58;
               Answer var91 = var97;
               int var83 = 0;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_1384_0 = var49;
               Function0 var93 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.TimePicker$lambda$24$lambda$23(recoveredExpressionCapture_1384_0));
               var97 = var91;
               var102 = var92;
               Object var86 = var93;
               var64.updateRememberedValue(var86);
               var104 = var86;
            } else {
               var104 = it$ivxxx;
            }

            Function0 var59 = (Function0)var104;
            var48.endReplaceGroup();
            ComposeTimePickerDialog(var97, var102, var59, var48, 384 | 14 & var5 >> 3);
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var98 = var48.endRestartGroup();
      if (var98 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1381_0 = var0;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1381_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1381_2 = var2;
         final int recoveredExpressionCapture_1381_3 = var4;
         var98.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1381_0, recoveredExpressionArgument_1381_1) -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.TimePicker$lambda$25(recoveredExpressionCapture_1381_0, recoveredExpressionCapture_1381_1, recoveredExpressionCapture_1381_2, recoveredExpressionCapture_1381_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1381_0), ((java.lang.Integer)recoveredExpressionArgument_1381_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ComposeTimePickerDialog(Answer var0, Function1 var1, Function0 var2, Composer var3, int var4) {
      Composer var22 = var3.startRestartGroup(-1295396418);
      ComposerKt.sourceInformation(var22, "C(ComposeTimePickerDialog)238@8862L129,244@9034L2028,244@8997L2065:DatePickerQuestion.kt#uw1bh6");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var22.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var22.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var22.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var22.getSkipping()) {
         var22.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1295396418, var5, -1, "io.intercom.android.sdk.survey.ui.questiontype.ComposeTimePickerDialog (DatePickerQuestion.kt:233)"
            );
         }

         Iterable var7 = getLocalTime(var0);
         int var8 = 0;
         Iterable var9 = var7;
         ArrayList var10 = new ArrayList();
         int var11 = 0;
         Iterable var12 = var9;
         int var13 = 0;

         for (Object var15 : var12) {
            Object var16 = var15;
            int var17 = 0;
            String var18 = (String)var16;
            int var19 = 0;
            Integer var10000 = StringsKt.toIntOrNull(var18);
            if (var10000 != null) {
               Object var20 = var10000;
               int var21 = 0;
               var10.add(var20);
            }
         }

         List var6 = (List)var10;
         List var24 = var6;
         int var26 = 0;
         Object var32;
         if (var26 < var24.size()) {
            var32 = var24.get(var26);
         } else {
            int var30 = 0;
            var32 = 0;
         }

         int var23 = ((Number)var32).intValue();
         List var27 = var6;
         int itx = 1;
         Object var33;
         if (itx < var27.size()) {
            var33 = var27.get(itx);
         } else {
            int var31 = 0;
            var33 = 0;
         }

         int var25 = ((Number)var33).intValue();
         TimePickerState var28 = TimePickerKt.rememberTimePickerState(var23, var25, true, var22, 384, 0);
         AndroidDialog_androidKt.Dialog(
            var2,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               -1529528875, true, new DatePickerQuestionKt$ComposeTimePickerDialog$1(var28, var2, var0, var1), var22, 54
            ),
            var22,
            384 | 14 & var5 >> 6,
            2
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var34 = var22.endRestartGroup();
      if (var34 != null) {
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1374_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1374_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1374_2 = var2;
         final int recoveredExpressionCapture_1374_3 = var4;
         var34.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1374_0, recoveredExpressionArgument_1374_1) -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.ComposeTimePickerDialog$lambda$29(recoveredExpressionCapture_1374_0, recoveredExpressionCapture_1374_1, recoveredExpressionCapture_1374_2, recoveredExpressionCapture_1374_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1374_0), ((java.lang.Integer)recoveredExpressionArgument_1374_1).intValue())));
      }
   }

   @Composable
   public static final float contentAlpha(boolean var0, @Nullable Composer var1, int var2) {
      var1.startReplaceGroup(2064501839);
      ComposerKt.sourceInformation(var1, "C(contentAlpha):DatePickerQuestion.kt#uw1bh6");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(2064501839, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.contentAlpha (DatePickerQuestion.kt:297)");
      }

      float var3 = var0 ? 1.0F : 0.6F;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   private static final List getUtcTime(int var0, int var1) {
      String var10000 = TimeFormatter.formatToUtcTime(var0, var1);
      Intrinsics.checkNotNullExpressionValue(var10000, "formatToUtcTime(...)");
      CharSequence var3 = var10000;
      String[] var2 = new String[]{":"};
      return StringsKt.split$default(var3, var2, false, 0, 6, null);
   }

   private static final List getLocalTime(Answer var0) {
      List var2;
      if (var0 instanceof io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer) {
         CharSequence var10000 = ((io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer)var0).getLocalTime();
         String[] var1 = new String[]{":"};
         var2 = StringsKt.split$default(var10000, var1, false, 0, 6, null);
      } else {
         var2 = CollectionsKt.emptyList();
      }

      return var2;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void DatePickerQuestionPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1652233850);
      ComposerKt.sourceInformation(var2, "C(DatePickerQuestionPreview)312@11501L496:DatePickerQuestion.kt#uw1bh6");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1652233850, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionPreview (DatePickerQuestion.kt:311)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.ComposableSingletons$DatePickerQuestionKt.INSTANCE.recovered$3faf0e22fa74c6a7(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1380_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1380_0, recoveredExpressionArgument_1380_1) -> io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt.DatePickerQuestionPreview$lambda$30(recoveredExpressionCapture_1380_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1380_0), ((java.lang.Integer)recoveredExpressionArgument_1380_1).intValue())));
      }
   }

   private static final Unit DatePickerQuestion$lambda$2(
      Modifier var0, io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel var1, Answer var2, Function1 var3, Function2 var4, int var5, int var6, Composer var7, int var8
   ) {
      DatePickerQuestion(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final boolean DatePicker$lambda$4(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void DatePicker$lambda$5(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit DatePicker$lambda$7$lambda$6(FocusManager var0, MutableState var1) {
      FocusManager.clearFocus$default(var0, false, 1, null);
      DatePicker$lambda$5(var1, true);
      return Unit.INSTANCE;
   }

   private static final Unit DatePicker$lambda$10$lambda$9(Function1 var0, MutableState var1, Answer var2) {
      Intrinsics.checkNotNullParameter(var2, "newAnswer");
      var0.invoke(var2);
      DatePicker$lambda$5(var1, false);
      return Unit.INSTANCE;
   }

   private static final Unit DatePicker$lambda$12$lambda$11(MutableState var0) {
      DatePicker$lambda$5(var0, false);
      return Unit.INSTANCE;
   }

   private static final Unit DatePicker$lambda$13(Modifier var0, Answer var1, Function1 var2, int var3, Composer var4, int var5) {
      DatePicker(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ComposeDatePickerDialog$lambda$14(Answer var0, Function1 var1, Function0 var2, int var3, Composer var4, int var5) {
      ComposeDatePickerDialog(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   private static final boolean TimePicker$lambda$16(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void TimePicker$lambda$17(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit TimePicker$lambda$19$lambda$18(FocusManager var0, MutableState var1) {
      FocusManager.clearFocus$default(var0, false, 1, null);
      TimePicker$lambda$17(var1, true);
      return Unit.INSTANCE;
   }

   private static final Unit TimePicker$lambda$22$lambda$21(Function1 var0, MutableState var1, Answer var2) {
      Intrinsics.checkNotNullParameter(var2, "newAnswer");
      var0.invoke(var2);
      TimePicker$lambda$17(var1, false);
      return Unit.INSTANCE;
   }

   private static final Unit TimePicker$lambda$24$lambda$23(MutableState var0) {
      TimePicker$lambda$17(var0, false);
      return Unit.INSTANCE;
   }

   private static final Unit TimePicker$lambda$25(Modifier var0, Answer var1, Function1 var2, int var3, Composer var4, int var5) {
      TimePicker(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ComposeTimePickerDialog$lambda$29(Answer var0, Function1 var1, Function0 var2, int var3, Composer var4, int var5) {
      ComposeTimePickerDialog(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit DatePickerQuestionPreview$lambda$30(int var0, Composer var1, int var2) {
      DatePickerQuestionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final List access$getUtcTime(int var0, int var1) {
      return getUtcTime(var0, var1);
   }
}

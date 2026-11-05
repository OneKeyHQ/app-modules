package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.ButtonColors;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.focus.FocusChangedModifierKt;
import androidx.compose.ui.focus.FocusState;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.components.IntercomPrimaryButtonKt;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType;
import io.intercom.android.sdk.survey.ui.components.QuestionComponentKt;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u001ag\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\t2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\tH\u0001¢\u0006\u0002\u0010\r\u001ac\u0010\u000e\u001a\u00020\u00012\b\b\u0002\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\t2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\tH\u0001¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0017\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0018\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015\"\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004¢\u0006\u0002\n\u0000\"\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e",
   d2 = {
         "CreateTicketScreen",
         "",
         "uiState",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState;",
         "onBackClick",
         "Lkotlin/Function0;",
         "onCreateTicket",
         "onCancel",
         "onAnswerUpdated",
         "Lkotlin/Function1;",
         "",
         "onAnswerClick",
         "Lio/intercom/android/sdk/survey/ui/questiontype/AnswerClickData;",
         "(Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "CreateTicketContentScreen",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "state",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState$Content;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState$Content;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "CreateTicketContentScreenPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "CreateTicketLoadingPreview",
         "CreateTicketErrorPreview",
         "CreateTicketContentErrorScreenPreview",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "questions",
         "",
         "Lio/intercom/android/sdk/survey/QuestionState;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nCreateTicketContentScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/CreateTicketContentScreenKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,374:1\n118#2:375\n118#2:408\n118#2:416\n118#2:417\n118#2:419\n118#2:420\n118#2:421\n118#2:422\n118#2:427\n87#3:376\n84#3,9:377\n94#3:426\n81#4,6:386\n88#4,6:401\n96#4:425\n402#5,9:392\n411#5:407\n412#5,2:423\n1869#6:409\n1870#6:418\n1047#7,6:410\n*S KotlinDebug\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/CreateTicketContentScreenKt\n*L\n120#1:375\n123#1:408\n161#1:416\n162#1:417\n172#1:419\n201#1:420\n202#1:421\n216#1:422\n325#1:427\n114#1:376\n114#1:377,9\n114#1:426\n114#1:386,6\n114#1:401,6\n114#1:425\n114#1:392,9\n114#1:407\n114#1:423,2\n124#1:409\n124#1:418\n148#1:410,6\n*E\n"
)
public final class CreateTicketContentScreenKt {
   @NotNull
   private static final SurveyUiColors surveyUiColors = new SurveyUiColors(
      Color.Companion.recovered$235b30c090a399c5(),
      Color.Companion.recovered$95989221dad41c4d(),
      Color.Companion.recovered$ba858dcfc5e04b9c(),
      Color.Companion.recovered$ff90a8751967f2f7(),
      null,
      16,
      null
   );
   @NotNull
   private static final List questions;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CreateTicketScreen(
      @NotNull io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var0,
      @NotNull Function0 var1,
      @NotNull Function0 var2,
      @NotNull Function0 var3,
      @NotNull Function1 var4,
      @NotNull Function1 var5,
      @Nullable Composer var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "uiState");
      Intrinsics.checkNotNullParameter(var1, "onBackClick");
      Intrinsics.checkNotNullParameter(var2, "onCreateTicket");
      Intrinsics.checkNotNullParameter(var3, "onCancel");
      Intrinsics.checkNotNullParameter(var4, "onAnswerUpdated");
      Intrinsics.checkNotNullParameter(var5, "onAnswerClick");
      Composer var9 = var6.startRestartGroup(-2129527205);
      ComposerKt.sourceInformation(
         var9, "C(CreateTicketScreen)P(5,2,4,3,1)69@3484L6,70@3548L13,71@3581L164,76@3753L979,67@3408L1324:CreateTicketContentScreen.kt#xh6pmz"
      );
      int var8 = var7;
      if ((var7 & 6) == 0) {
         var8 |= var9.changed(var0) ? 4 : 2;
      }

      if ((var7 & 48) == 0) {
         var8 |= var9.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 384) == 0) {
         var8 |= var9.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 3072) == 0) {
         var8 |= var9.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 24576) == 0) {
         var8 |= var9.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var7 & 196608) == 0) {
         var8 |= var9.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var8 & 74899) == 74898 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2129527205, var8, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketScreen (CreateTicketContentScreen.kt:66)");
         }

         ScaffoldKt.recovered$857cf99fc8161460(
            WindowInsetsPaddingKt.windowInsetsPadding(
               BackgroundKt.recovered$42a3174180b3fb7a(
                  Modifier.Companion, IntercomTheme.INSTANCE.getColors(var9, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
               ),
               WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var9, 6)
            ),
            ComposableLambdaKt.rememberComposableLambda(-2106967777, true, new CreateTicketContentScreenKt$CreateTicketScreen$1(var0, var1), var9, 54),
            null,
            null,
            null,
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               426563690, true, new CreateTicketContentScreenKt$CreateTicketScreen$2(var0, var2, var3, var4, var5), var9, 54
            ),
            var9,
            805306416,
            508
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var9.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState recoveredExpressionCapture_1598_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1598_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1598_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1598_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1598_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1598_5 = var5;
         final int recoveredExpressionCapture_1598_6 = var7;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1598_0, recoveredExpressionArgument_1598_1) -> io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreenKt.CreateTicketScreen$lambda$0(recoveredExpressionCapture_1598_0, recoveredExpressionCapture_1598_1, recoveredExpressionCapture_1598_2, recoveredExpressionCapture_1598_3, recoveredExpressionCapture_1598_4, recoveredExpressionCapture_1598_5, recoveredExpressionCapture_1598_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1598_0), ((java.lang.Integer)recoveredExpressionArgument_1598_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CreateTicketContentScreen(
      @Nullable Modifier var0,
      @NotNull io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content var1,
      @NotNull Function0 var2,
      @NotNull Function0 var3,
      @NotNull Function1 var4,
      @NotNull Function1 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "state");
      Intrinsics.checkNotNullParameter(var2, "onCreateTicket");
      Intrinsics.checkNotNullParameter(var3, "onCancel");
      Intrinsics.checkNotNullParameter(var4, "onAnswerUpdated");
      Intrinsics.checkNotNullParameter(var5, "onAnswerClick");
      Composer var59 = var6.startRestartGroup(-296750187);
      ComposerKt.sourceInformation(var59, "C(CreateTicketContentScreen)P(!1,5,4,3,2)112@5012L21,118@5212L6,113@5038L4406:CreateTicketContentScreen.kt#xh6pmz");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var59.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var59.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var59.changedInstance(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var59.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var59.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var59.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var59.getSkipping()) {
         var59.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -296750187, var9, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreen (CreateTicketContentScreen.kt:111)"
            );
         }

         ScrollState var10 = ScrollKt.rememberScrollState(0, var59, 0, 1);
         Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
            ScrollKt.verticalScroll$default(
               WindowInsetsPadding_androidKt.imePadding(SizeKt.fillMaxSize$default(var0, 0.0F, 1, null)), var10, true, null, false, 12, null
            ),
            IntercomTheme.INSTANCE.getColors(var59, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         int var11 = 16;
         int var12 = 0;
         Modifier var60 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var11), 0.0F, 2, null);
         byte var14 = 0;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var59, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var61 = Arrangement.INSTANCE.getTop();
         Horizontal var13 = Alignment.Companion.getStart();
         MeasurePolicy var16 = ColumnKt.columnMeasurePolicy(var61, var13, var59, 14 & var14 >> 3 | 112 & var14 >> 3);
         Modifier var17 = var60;
         MeasurePolicy var18 = var16;
         Composer var19 = var59;
         int var20 = 112 & var14 << 3;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var19, 0));
         CompositionLocalMap var23 = var19.getCurrentCompositionLocalMap();
         Modifier var24 = ComposedModifierKt.materializeModifier(var19, var17);
         Function0 var25 = ComposeUiNode.Companion.getConstructor();
         Composer var26 = var19;
         int var27 = 6 | 896 & var20 << 6;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var26.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var26.startReusableNode();
         if (var26.getInserting()) {
            var26.createNode(var25);
         } else {
            var26.useNode();
         }

         Composer var29 = Updater.recovered$676aa17b44fd6280(var26);
         int var30 = 0;
         Updater.recovered$89682b199a142880(var29, var18, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var29, var23, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var29, var22, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var29, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var29, var24, ComposeUiNode.Companion.getSetModifier());
         int var31 = 14 & var27 >> 6;
         Composer var32 = var26;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var34 = 6 | 112 & var14 >> 6;
         Composer var35 = var32;
         ColumnScope var36 = ColumnScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var35,
            2069308012,
            "C122@5344L41,167@7433L27,174@7725L983,168@7469L1239,203@8944L6,205@9057L6,204@8994L97,197@8717L612,215@9397L41:CreateTicketContentScreen.kt#xh6pmz"
         );
         Modifier var88 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var88, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var35, 6);
         var35.startReplaceGroup(-210342761);
         ComposerKt.sourceInformation(var35, "*147@6567L373,159@7137L6,145@6476L938");
         Iterable var62 = var1.getQuestions();
         int var66 = 0;

         for (Object var41 : var62) {
            QuestionState var42 = (QuestionState)var41;
            int var43 = 0;
            SurveyUiColors var89;
            if (var42.getQuestionModel() instanceof io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel) {
               var35.startReplaceGroup(-267642190);
               ComposerKt.sourceInformation(var35, "128@5672L6,129@5747L6,130@5819L6,131@5887L6");
               SurveyUiColors var44 = new SurveyUiColors(
                  IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
                  IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
                  IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).recovered$202acdf3dbb5c020(),
                  IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).recovered$8a5d9ad2993bccce(),
                  null,
                  16,
                  null
               );
               var35.endReplaceGroup();
               var89 = var44;
            } else {
               var35.startReplaceGroup(-267236710);
               ComposerKt.sourceInformation(var35, "137@6078L6,138@6153L6,139@6225L6,140@6296L6,141@6383L6");
               SurveyUiColors var79 = new SurveyUiColors(
                  IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
                  IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
                  IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
                  IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
                  Color.recovered$f46ef195ad4a2363(IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).recovered$202acdf3dbb5c020()),
                  null
               );
               var35.endReplaceGroup();
               var89 = var79;
            }

            SurveyUiColors var45 = var89;
            Modifier var90 = Modifier.Companion;
            var35.startReplaceGroup(-978433203);
            ComposerKt.sourceInformation(var35, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
            Composer var46 = var35;
            boolean var47 = var35.changedInstance(var42);
            int var48 = 0;
            Object var49 = var46.rememberedValue();
            int var50 = 0;
            Object var97;
            if (!var47 && var49 != Composer.Companion.getEmpty()) {
               var97 = var49;
            } else {
               Modifier var51 = var90;
               int var52 = 0;
               final io.intercom.android.sdk.survey.QuestionState recoveredExpressionCapture_1594_0 = var42;
               Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1594_0) -> io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreenKt.CreateTicketContentScreen$lambda$4$lambda$3$lambda$2$lambda$1(recoveredExpressionCapture_1594_0, (androidx.compose.ui.focus.FocusState) ((androidx.compose.ui.focus.FocusState)recoveredExpressionArgument_1594_0)));
               var90 = var51;
               Object var53 = var10001;
               var46.updateRememberedValue(var53);
               var97 = var53;
            }

            Function1 var80 = (Function1)var97;
            var35.endReplaceGroup();
            Modifier var54 = FocusChangedModifierKt.onFocusChanged(var90, var80);
            long var55 = IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            int $this$dp$ivxx = 0;
            int $i$f$getDpxx = 0;
            float var81 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
            Modifier var91 = Modifier.Companion;
            int $this$dp$ivxxx = 24;
            int $i$f$getDpxxx = 0;
            Modifier var83 = PaddingKt.recovered$03b481d31d62885a(var91, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxx), 0.0F, 0.0F, 13, null);
            long var57 = TextUnitKt.getSp(16);
            FontWeight var87 = FontWeight.Companion.getSemiBold();
            QuestionComponentKt.recovered$3f1b8998319da4be(
               var54, var83, var42, var45, var4, var55, var81, var87, var57, var5, var35, 114819120 | 57344 & var9 | 1879048192 & var9 << 12, 0
            );
         }

         var35.endReplaceGroup();
         SpacerKt.Spacer(ColumnScope.weight$default(var36, var0, 1.0F, false, 2, null), var35, 0);
         Modifier var92 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int $this$dp$ivxx = 24;
         int $i$f$getDpxx = 0;
         Modifier var63 = PaddingKt.recovered$03b481d31d62885a(var92, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 0.0F, 13, null);
         boolean var68 = var1.getEnableCta() && !var1.getShowCreatingTicketProgress();
         IntercomPrimaryButtonKt.LegacyIntercomPrimaryButton(
            var2,
            var63,
            var68,
            ComposableLambdaKt.rememberComposableLambda(-964987781, true, new CreateTicketContentScreenKt$CreateTicketContentScreen$1$2(var1), var35, 54),
            var35,
            3120 | 14 & var9 >> 6,
            0
         );
         Modifier var93 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int $this$dp$ivxxx = 8;
         int $i$f$getDpxxx = 0;
         float var10002 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
         int $this$dp$ivxxxx = 16;
         int $i$f$getDpxxxx = 0;
         Modifier var94 = PaddingKt.recovered$03b481d31d62885a(var93, 0.0F, var10002, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 5, null);
         int $this$dp$ivxxxxx = 48;
         int $i$f$getDpxxxxx = 0;
         Modifier var64 = SizeKt.recovered$3f6c6a76aa292717(var94, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx));
         CornerBasedShape var72 = IntercomTheme.INSTANCE.getShapes(var35, IntercomTheme.$stable).getSmall();
         ButtonColors var78 = ButtonDefaults.INSTANCE
            .recovered$a89e246567752be2(
               0L,
               IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
               0L,
               0L,
               var35,
               ButtonDefaults.$stable << 12,
               13
            );
         ButtonKt.TextButton(
            var3,
            var64,
            false,
            var72,
            var78,
            null,
            null,
            null,
            null,
            io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.INSTANCE.recovered$b26556cd5c386b55(),
            var35,
            805306416 | 14 & var9 >> 9,
            484
         );
         Modifier var95 = Modifier.Companion;
         int $this$dp$ivxxxxxx = 16;
         int $i$f$getDpxxxxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var95, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxx)), var35, 6);
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var59);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var96 = var59.endRestartGroup();
      if (var96 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1593_0 = var0;
         final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content recoveredExpressionCapture_1593_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1593_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1593_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1593_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1593_5 = var5;
         final int recoveredExpressionCapture_1593_6 = var7;
         final int recoveredExpressionCapture_1593_7 = var8;
         var96.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1593_0, recoveredExpressionArgument_1593_1) -> io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreenKt.CreateTicketContentScreen$lambda$5(recoveredExpressionCapture_1593_0, recoveredExpressionCapture_1593_1, recoveredExpressionCapture_1593_2, recoveredExpressionCapture_1593_3, recoveredExpressionCapture_1593_4, recoveredExpressionCapture_1593_5, recoveredExpressionCapture_1593_6, recoveredExpressionCapture_1593_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1593_0), ((java.lang.Integer)recoveredExpressionArgument_1593_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void CreateTicketContentScreenPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1070922859);
      ComposerKt.sourceInformation(var2, "C(CreateTicketContentScreenPreview)222@9531L413:CreateTicketContentScreen.kt#xh6pmz");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1070922859, var1, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreenPreview (CreateTicketContentScreen.kt:221)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.INSTANCE.recovered$1400af5e34386102(),
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
         final int recoveredExpressionCapture_1595_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1595_0, recoveredExpressionArgument_1595_1) -> io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreenKt.CreateTicketContentScreenPreview$lambda$6(recoveredExpressionCapture_1595_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1595_0), ((java.lang.Integer)recoveredExpressionArgument_1595_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void CreateTicketLoadingPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1078617214);
      ComposerKt.sourceInformation(var2, "C(CreateTicketLoadingPreview)241@10025L292:CreateTicketContentScreen.kt#xh6pmz");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1078617214, var1, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketLoadingPreview (CreateTicketContentScreen.kt:240)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.INSTANCE.recovered$fd838d44303763da(),
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
         final int recoveredExpressionCapture_1597_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1597_0, recoveredExpressionArgument_1597_1) -> io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreenKt.CreateTicketLoadingPreview$lambda$7(recoveredExpressionCapture_1597_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1597_0), ((java.lang.Integer)recoveredExpressionArgument_1597_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void CreateTicketErrorPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-627794766);
      ComposerKt.sourceInformation(var2, "C(CreateTicketErrorPreview)256@10396L467:CreateTicketContentScreen.kt#xh6pmz");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -627794766, var1, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketErrorPreview (CreateTicketContentScreen.kt:255)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
         final int recoveredExpressionCapture_1596_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1596_0, recoveredExpressionArgument_1596_1) -> io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreenKt.CreateTicketErrorPreview$lambda$8(recoveredExpressionCapture_1596_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1596_0), ((java.lang.Integer)recoveredExpressionArgument_1596_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void CreateTicketContentErrorScreenPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1908579859);
      ComposerKt.sourceInformation(var2, "C(CreateTicketContentErrorScreenPreview)276@10955L595:CreateTicketContentScreen.kt#xh6pmz");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1908579859, var1, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketContentErrorScreenPreview (CreateTicketContentScreen.kt:275)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.INSTANCE.recovered$265b6f46b79d310a(),
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
         final int recoveredExpressionCapture_1592_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1592_0, recoveredExpressionArgument_1592_1) -> io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreenKt.CreateTicketContentErrorScreenPreview$lambda$9(recoveredExpressionCapture_1592_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1592_0), ((java.lang.Integer)recoveredExpressionArgument_1592_1).intValue())));
      }
   }

   private static final Unit CreateTicketScreen$lambda$0(
      io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var0, Function0 var1, Function0 var2, Function0 var3, Function1 var4, Function1 var5, int var6, Composer var7, int var8
   ) {
      CreateTicketScreen(var0, var1, var2, var3, var4, var5, var7, RecomposeScopeImplKt.updateChangedFlags(var6 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit CreateTicketContentScreen$lambda$4$lambda$3$lambda$2$lambda$1(QuestionState var0, FocusState var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      if (var0.getQuestionModel() instanceof io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel && !(var0.getAnswer() instanceof io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer) && !var1.getHasFocus()) {
         var0.validate();
      }

      return Unit.INSTANCE;
   }

   private static final Unit CreateTicketContentScreen$lambda$5(
      Modifier var0, io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content var1, Function0 var2, Function0 var3, Function1 var4, Function1 var5, int var6, int var7, Composer var8, int var9
   ) {
      CreateTicketContentScreen(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit CreateTicketContentScreenPreview$lambda$6(int var0, Composer var1, int var2) {
      CreateTicketContentScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit CreateTicketLoadingPreview$lambda$7(int var0, Composer var1, int var2) {
      CreateTicketLoadingPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit CreateTicketErrorPreview$lambda$8(int var0, Composer var1, int var2) {
      CreateTicketErrorPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit CreateTicketContentErrorScreenPreview$lambda$9(int var0, Composer var1, int var2) {
      CreateTicketContentErrorScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final List access$getQuestions$p() {
      return questions;
   }

   static {
      QuestionState[] var0 = new QuestionState[6];
      var0[0] = new QuestionState(
         new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel(
            "1",
            CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Email").withType("paragraph")),
            false,
            "abc@example.com",
            SurveyData$Step$Question$QuestionValidation$ValidationType.EMAIL,
            null,
            false,
            null,
            null,
            448,
            null
         ),
         surveyUiColors
      );
      List var10007 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Multiline text").withType("paragraph"));
      int var1 = 120;
      int var2 = 0;
      var0[1] = new QuestionState(
         new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel(
            "2", var10007, true, "Enter text here...", SurveyData$Step$Question$QuestionValidation$ValidationType.EMAIL, null, Dp.recovered$95b3d103663bf060(var1), 0, null, 384, null
         ),
         surveyUiColors
      );
      List var4 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("List attribute").withType("paragraph"));
      String[] var3 = new String[]{"Option A", "Option B", "Option C"};
      List var6 = CollectionsKt.listOf(var3);
      var0[2] = new QuestionState(new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel("3", var4, true, var6, "Please select...", null, 32, null), surveyUiColors);
      List var7 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Boolean").withType("paragraph"));
      String[] var5 = new String[]{"True", "False"};
      var0[3] = new QuestionState(new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel("4", var7, false, CollectionsKt.listOf(var5), false), surveyUiColors);
      var0[4] = new QuestionState(
         new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel("5", CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Date and Time").withType("paragraph")), true), surveyUiColors
      );
      var0[5] = new QuestionState(
         new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel("5", CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Date and Time").withType("paragraph")), true), surveyUiColors
      );
      questions = CollectionsKt.listOf(var0);
   }
}

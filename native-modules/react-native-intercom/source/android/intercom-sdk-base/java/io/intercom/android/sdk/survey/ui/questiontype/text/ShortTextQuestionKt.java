package io.intercom.android.sdk.survey.ui.questiontype.text;

import android.content.Context;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.relocation.BringIntoViewRequester;
import androidx.compose.foundation.relocation.BringIntoViewRequesterKt;
import androidx.compose.foundation.text.KeyboardActionScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.focus.FocusEventModifierKt;
import androidx.compose.ui.focus.FocusState;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.models.CountryAreaCode;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.ValidationError;
import io.intercom.android.sdk.survey.ValidationError$NoValidationError;
import io.intercom.android.sdk.survey.ValidationError$ValidationStringError;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$ResetNoAnswer;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.PhoneNumberValidator;
import io.intercom.android.sdk.utilities.UtilsKt;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a}\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0019\b\u0002\u0010\u000e\u001a\u0013\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\t¢\u0006\u0002\b\u00102\u0013\b\u0002\u0010\u0011\u001a\r\u0012\u0004\u0012\u00020\u00010\u0012¢\u0006\u0002\b\u0013H\u0001¢\u0006\u0002\u0010\u0014\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0016\u001a\r\u0010\u0017\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0016\u001a\r\u0010\u0018\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0016\u001a\r\u0010\u0019\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0016¨\u0006\u001a",
   d2 = {
         "ShortTextQuestion",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "textQuestionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$ShortTextQuestionModel;",
         "answer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "onAnswer",
         "Lkotlin/Function1;",
         "colors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "validationError",
         "Lio/intercom/android/sdk/survey/ValidationError;",
         "onImeActionNext",
         "Landroidx/compose/foundation/text/KeyboardActionScope;",
         "Lkotlin/ExtensionFunctionType;",
         "questionHeader",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$ShortTextQuestionModel;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/survey/SurveyUiColors;Lio/intercom/android/sdk/survey/ValidationError;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "ShortTextPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ShortTextAnsweredPreview",
         "ShortTextPhoneNumberPreview",
         "ShortTextDisabledPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nShortTextQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShortTextQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/ShortTextQuestionKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 4 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n+ 5 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 9 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 10 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,263:1\n1047#2,6:264\n1047#2,6:270\n1047#2,3:283\n1050#2,3:287\n1047#2,6:290\n1047#2,6:363\n615#3:276\n612#3,6:277\n613#4:286\n70#5:296\n67#5,9:297\n77#5:378\n81#6,6:306\n88#6,6:321\n81#6,6:339\n88#6,6:354\n96#6:373\n96#6:377\n402#7,9:312\n411#7:327\n402#7,9:345\n411#7:360\n412#7,2:371\n412#7,2:375\n87#8:328\n83#8,10:329\n94#8:374\n118#9:361\n118#9:369\n118#9:370\n75#10:362\n*S KotlinDebug\n*F\n+ 1 ShortTextQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/ShortTextQuestionKt\n*L\n59#1:264,6\n62#1:270,6\n63#1:283,3\n63#1:287,3\n67#1:290,6\n120#1:363,6\n63#1:276\n63#1:277,6\n63#1:286\n64#1:296\n64#1:297,9\n64#1:378\n64#1:306,6\n64#1:321,6\n75#1:339,6\n75#1:354,6\n75#1:373\n64#1:377\n64#1:312,9\n64#1:327\n75#1:345,9\n75#1:360\n75#1:371,2\n64#1:375,2\n75#1:328\n75#1:329,10\n75#1:374\n77#1:361\n152#1:369\n162#1:370\n95#1:362\n*E\n"
)
public final class ShortTextQuestionKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void ShortTextQuestion(
      @Nullable Modifier var0,
      @NotNull io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel var1,
      @Nullable Answer var2,
      @NotNull Function1 var3,
      @NotNull SurveyUiColors var4,
      @NotNull ValidationError var5,
      @Nullable Function1 var6,
      @Nullable Function2 var7,
      @Nullable Composer var8,
      int var9,
      int var10
   ) {
      Intrinsics.checkNotNullParameter(var1, "textQuestionModel");
      Intrinsics.checkNotNullParameter(var3, "onAnswer");
      Intrinsics.checkNotNullParameter(var4, "colors");
      Intrinsics.checkNotNullParameter(var5, "validationError");
      Composer var102 = var8.startRestartGroup(13974558);
      ComposerKt.sourceInformation(
         var102, "C(ShortTextQuestion)P(2,6!1,3!1,7)58@3122L2,61@3212L37,62@3275L24,66@3448L198,63@3304L4390:ShortTextQuestion.kt#z2rnlt"
      );
      int var11 = var9;
      if ((var10 & 1) != 0) {
         var11 |= 6;
      } else if ((var9 & 6) == 0) {
         var11 |= var102.changed(var0) ? 4 : 2;
      }

      if ((var10 & 2) != 0) {
         var11 |= 48;
      } else if ((var9 & 48) == 0) {
         var11 |= var102.changed(var1) ? 32 : 16;
      }

      if ((var10 & 4) != 0) {
         var11 |= 384;
      } else if ((var9 & 384) == 0) {
         var11 |= var102.changed(var2) ? 256 : 128;
      }

      if ((var10 & 8) != 0) {
         var11 |= 3072;
      } else if ((var9 & 3072) == 0) {
         var11 |= var102.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var10 & 16) != 0) {
         var11 |= 24576;
      } else if ((var9 & 24576) == 0) {
         var11 |= var102.changed(var4) ? 16384 : 8192;
      }

      if ((var10 & 32) != 0) {
         var11 |= 196608;
      } else if ((var9 & 196608) == 0) {
         var11 |= var102.changed(var5) ? 131072 : 65536;
      }

      if ((var10 & 64) != 0) {
         var11 |= 1572864;
      } else if ((var9 & 1572864) == 0) {
         var11 |= var102.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var10 & 128) != 0) {
         var11 |= 12582912;
      } else if ((var9 & 12582912) == 0) {
         var11 |= var102.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 4793491) == 4793490 && var102.getSkipping()) {
         var102.skipToGroupEnd();
      } else {
         if ((var10 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var10 & 4) != 0) {
            var2 = io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer.INSTANCE;
         }

         if ((var10 & 64) != 0) {
            var102.startReplaceGroup(-1483115004);
            ComposerKt.sourceInformation(var102, "CC(remember):ShortTextQuestion.kt#9igjgp");
            Composer var13 = var102;
            boolean var14 = false;
            int var15 = 0;
            Object var16 = var13.rememberedValue();
            int var17 = 0;
            Object var10000;
            if (var16 == Composer.Companion.getEmpty()) {
               int var18 = 0;
               Object var121 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1507_0) -> io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt.ShortTextQuestion$lambda$1$lambda$0((androidx.compose.foundation.text.KeyboardActionScope) ((androidx.compose.foundation.text.KeyboardActionScope)recoveredExpressionArgument_1507_0)));
               var13.updateRememberedValue(var121);
               var10000 = var121;
            } else {
               var10000 = var16;
            }

            Function1 var12 = (Function1)var10000;
            var102.endReplaceGroup();
            var6 = var12;
         }

         if ((var10 & 128) != 0) {
            var7 = io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$ShortTextQuestionKt.INSTANCE.recovered$eac478aa65045e5d();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(13974558, var11, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestion (ShortTextQuestion.kt:60)");
         }

         var102.startReplaceGroup(-1483112089);
         ComposerKt.sourceInformation(var102, "CC(remember):ShortTextQuestion.kt#9igjgp");
         Composer var106 = var102;
         boolean var109 = false;
         int var113 = 0;
         Object var117 = var106.rememberedValue();
         int var122 = 0;
         Object var161;
         if (var117 == Composer.Companion.getEmpty()) {
            int var19 = 0;
            Object var126 = BringIntoViewRequesterKt.BringIntoViewRequester();
            var106.updateRememberedValue(var126);
            var161 = var126;
         } else {
            var161 = var117;
         }

         BringIntoViewRequester var104 = (BringIntoViewRequester)var161;
         var102.endReplaceGroup();
         BringIntoViewRequester var103 = var104;
         boolean var110 = false;
         int var114 = 0;
         ComposerKt.sourceInformationMarkerStart(var102, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var118 = var102;
         ComposerKt.sourceInformationMarkerStart(var102, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var123 = var102;
         boolean var127 = false;
         int $i$f$cachex = 0;
         Object var21 = var123.rememberedValue();
         int var22 = 0;
         Object var162;
         if (var21 == Composer.Companion.getEmpty()) {
            int var23 = 0;
            int var24 = 0;
            Object var136 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var118);
            var123.updateRememberedValue(var136);
            var162 = var136;
         } else {
            var162 = var21;
         }

         CoroutineScope var25 = (CoroutineScope)var162;
         ComposerKt.sourceInformationMarkerEnd(var102);
         CoroutineScope var26 = var25;
         ComposerKt.sourceInformationMarkerEnd(var102);
         CoroutineScope var105 = var26;
         Modifier var163 = BringIntoViewRequesterKt.bringIntoViewRequester(var0, var103);
         var102.startReplaceGroup(-1483104376);
         ComposerKt.sourceInformation(var102, "CC(remember):ShortTextQuestion.kt#9igjgp");
         Composer var111 = var102;
         boolean var115 = var102.changedInstance(var105) | var102.changedInstance(var103);
         int $i$f$cachexx = 0;
         Object it$ivx = var111.rememberedValue();
         int var128/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var173;
         if (!var115 && it$ivx != Composer.Companion.getEmpty()) {
            var173 = it$ivx;
         } else {
            Modifier var101 = var163;
            int var130 = 0;
            final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_1508_0 = var105;
            final androidx.compose.foundation.relocation.BringIntoViewRequester recoveredExpressionCapture_1508_1 = var103;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1508_0) -> io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt.ShortTextQuestion$lambda$4$lambda$3(recoveredExpressionCapture_1508_0, recoveredExpressionCapture_1508_1, (androidx.compose.ui.focus.FocusState) ((androidx.compose.ui.focus.FocusState)recoveredExpressionArgument_1508_0)));
            var163 = var101;
            Object var132 = var10001;
            var111.updateRememberedValue(var132);
            var173 = var132;
         }

         Function1 var107 = (Function1)var173;
         var102.endReplaceGroup();
         Modifier var108 = FocusEventModifierKt.onFocusEvent(var163, var107);
         byte var120 = 0;
         int var125 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var102, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var112 = Alignment.Companion.getTopStart();
         boolean var116 = false;
         MeasurePolicy var129 = BoxKt.maybeCachedBoxMeasurePolicy(var112, var116);
         Modifier var131 = var108;
         MeasurePolicy var133 = var129;
         Composer var134 = var102;
         int var135 = 112 & var120 << 3;
         int var137 = 0;
         ComposerKt.sourceInformationMarkerStart(var134, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var138 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var134, 0));
         CompositionLocalMap var139 = var134.getCurrentCompositionLocalMap();
         Modifier var27 = ComposedModifierKt.materializeModifier(var134, var131);
         Function0 var28 = ComposeUiNode.Companion.getConstructor();
         Composer var29 = var134;
         int var30 = 6 | 896 & var135 << 6;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var29.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var29.startReusableNode();
         if (var29.getInserting()) {
            var29.createNode(var28);
         } else {
            var29.useNode();
         }

         Composer var32 = Updater.recovered$676aa17b44fd6280(var29);
         int var33 = 0;
         Updater.recovered$89682b199a142880(var32, var133, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var32, var139, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var32, var138, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var32, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var32, var27, ComposeUiNode.Companion.getSetModifier());
         int var34 = 14 & var30 >> 6;
         Composer var35 = var29;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var37 = 6 | 112 & var120 >> 6;
         Composer var38 = var35;
         BoxScope var39 = BoxScopeInstance.INSTANCE;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 915379882, "C74@3664L4024:ShortTextQuestion.kt#z2rnlt");
         int var41 = 0;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var38, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var43 = Modifier.Companion;
         Vertical var44 = Arrangement.INSTANCE.getTop();
         Horizontal var45 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var44, var45, var38, 14 & var41 >> 3 | 112 & var41 >> 3);
         Modifier var47 = var43;
         MeasurePolicy var48 = measurePolicy$ivx;
         Composer var49 = var38;
         int var50 = 112 & var41 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var49, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var49, 0));
         CompositionLocalMap localMap$iv$ivx = var49.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var49, var47);
         Function0 var55 = ComposeUiNode.Companion.getConstructor();
         Composer var56 = var49;
         int var57 = 6 | 896 & var50 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var56, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var56.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var56.startReusableNode();
         if (var56.getInserting()) {
            var56.createNode(var55);
         } else {
            var56.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var56);
         int var60/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var48, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var61 = 14 & var57 >> 6;
         Composer $composer$ivx = var56;
         int var63 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var64 = 6 | 112 & var41 >> 6;
         Composer var65 = $composer$ivx;
         ColumnScope var66 = ColumnScopeInstance.INSTANCE;
         int var67 = 0;
         ComposerKt.sourceInformationMarkerStart(var65, 1809894314, "C75@3700L8,76@3721L40,119@5562L221,115@5392L1295:ShortTextQuestion.kt#z2rnlt");
         var7.invoke(var65, 14 & var11 >> 21);
         Modifier var164 = Modifier.Companion;
         int var68 = 8;
         int var69 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var164, Dp.recovered$95b3d103663bf060(var68)), var65, 6);
         int var165;
         switch (var1.getValidationType()) {
            case NUMBER:
               var165 = KeyboardType.Companion.recovered$b13b0fa1ee2a59aa();
               break;
            case EMAIL:
               var165 = KeyboardType.Companion.recovered$690918277adbb689();
               break;
            case PHONE:
               var165 = KeyboardType.Companion.recovered$3f6427f56eed8f7b();
               break;
            case FLOAT:
               var165 = KeyboardType.Companion.recovered$b13b0fa1ee2a59aa();
               break;
            default:
               var165 = KeyboardType.Companion.recovered$29477a01a04c8fe0();
         }

         int var140 = var165;
         String var141 = var2 instanceof io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer ? ((io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer)var2).getAnswer() : "";
         Function2 var70 = null;
         var65.startReplaceGroup(1720965522);
         ComposerKt.sourceInformation(var65, "88@4306L59");
         String var71 = var1.getPlaceHolderStringRes() != null
            ? StringResources_androidKt.stringResource(var1.getPlaceHolderStringRes(), var65, 0)
            : var1.getPlaceholder();
         var65.endReplaceGroup();
         String var72 = var71;
         String var142 = var72;
         var65.startReplaceGroup(1720974793);
         ComposerKt.sourceInformation(var65, "94@4597L7,109@5238L74");
         if (var1.getValidationType() == io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.PHONE) {
            CompositionLocal var73 = AndroidCompositionLocals_androidKt.getLocalContext();
            int $changed$ivx = 0;
            int var75 = 0;
            ComposerKt.sourceInformationMarkerStart(var65, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var76 = var65.consume(var73);
            ComposerKt.sourceInformationMarkerEnd(var65);
            Context var77 = (Context)var76;
            PhoneNumberValidator.loadCountryAreaCodes(var77);
            Locale var143 = UtilsKt.getLocaleCompat(var77);
            CountryAreaCode var166;
            if (!(var2 instanceof io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer)) {
               String var147 = PhoneNumberValidator.stripPrefix(var141);
               var166 = PhoneNumberValidator.getCountryAreaCodeFromNumber(var147);
            } else {
               var166 = PhoneNumberValidator.getCountryAreaCodeFromLocale(var143.getCountry());
            }

            CountryAreaCode var145 = var166;
            if (var2 instanceof io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer) {
               var141 = '+' + var145.getDialCode();
            }

            var70 = ComposableLambdaKt.rememberComposableLambda(854302399, true, new ShortTextQuestionKt$ShortTextQuestion$3$1$1(var145), var65, 54);
            var142 = "+1 123 456 7890";
         }

         var65.endReplaceGroup();
         String var151 = var141;
         String var144 = var142;
         Function2 var146 = var70;
         Color var148 = var5 instanceof io.intercom.android.sdk.survey.ValidationError$ValidationStringError
               && !(var2 instanceof io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer)
               && ((io.intercom.android.sdk.survey.ValidationError$ValidationStringError)var5).getStringRes() != string.intercom_surveys_required_response
            ? Color.recovered$f46ef195ad4a2363(Color.Companion.recovered$176187de5f63aca7())
            : null;
         long var78 = ColorExtensionsKt.recovered$93702c38c1546c4a(var4.recovered$923dd289d74a1df8());
         boolean var80 = var1.getEnabled() && var5 instanceof io.intercom.android.sdk.survey.ValidationError$NoValidationError && !(var2 instanceof io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer);
         int var81 = ImeAction.Companion.recovered$c2fd69510cff9e19();
         boolean var82 = var1.getEnabled();
         String var167 = var151;
         String var174 = var144;
         var65.startReplaceGroup(1721007965);
         ComposerKt.sourceInformation(var65, "CC(remember):ShortTextQuestion.kt#9igjgp");
         Composer var83 = var65;
         boolean var84 = (var11 & 7168) == 2048;
         int $i$f$cachexxx = 0;
         Object it$ivxx = var83.rememberedValue();
         int var87/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10002;
         if (!var84 && it$ivxx != Composer.Companion.getEmpty()) {
            var10002 = it$ivxx;
         } else {
            String var88 = var144;
            String var89 = var151;
            int var90 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1505_0 = var3;
            Function1 var91 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1505_0) -> io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt.ShortTextQuestion$lambda$9$lambda$8$lambda$6$lambda$5(recoveredExpressionCapture_1505_0, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_1505_0)));
            var167 = var89;
            var174 = var88;
            Object var92 = var91;
            var83.updateRememberedValue(var92);
            var10002 = var92;
         }

         Function1 var93 = (Function1)var10002;
         var65.endReplaceGroup();
         TextInputPillKt.recovered$1e363f8e6e037ae9(
            var167, var174, var93, var148, var78, 0, true, var6, var140, var81, var80, var146, var82, 0.0F, var65, 806879232 | 29360128 & var11 << 3, 0, 8224
         );
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType var152 = var1.getValidationType();
         var65.startReplaceGroup(1721044754);
         ComposerKt.sourceInformation(var65, "*148@7031L6,145@6865L366");
         if (var152 == io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.NO_VALIDATION || var152 == io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.TEXT) {
            Integer var168 = var1.getCharacterLimit();
            if (var168 == null) {
               Object var169 = null;
            } else {
               int var94 = var168.intValue();
               int var154 = 0;
               String var156 = "" + var2.getLength() + '/' + var94;
               long var95 = TextUnitKt.getSp(12);
               long var97 = IntercomTheme.INSTANCE.getColors(var65, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
               Modifier var170 = var66.align(Modifier.Companion, Alignment.Companion.getEnd());
               int var157 = 4;
               int var158 = 0;
               Modifier var159 = PaddingKt.recovered$03b481d31d62885a(var170, 0.0F, Dp.recovered$95b3d103663bf060(var157), 0.0F, 0.0F, 13, null);
               TextKt.recovered$5b114ab32a4f4190(
                  var156, var159, var97, null, var95, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var65, 24576, 0, 262120
               );
            }
         }

         var65.endReplaceGroup();
         var65.startReplaceGroup(1721063955);
         ComposerKt.sourceInformation(var65, "158@7405L57,159@7506L6,160@7567L10,157@7372L292");
         if (var1.getValidationType() == io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.EMAIL) {
            String var153 = StringResources_androidKt.stringResource(string.intercom_tickets_email_copy, var65, 0);
            long var99 = IntercomTheme.INSTANCE.getColors(var65, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
            TextStyle var149 = IntercomTheme.INSTANCE.getTypography(var65, IntercomTheme.$stable).getType04();
            Modifier var171 = Modifier.Companion;
            int var160 = 4;
            int var155 = 0;
            Modifier var150 = PaddingKt.recovered$03b481d31d62885a(var171, 0.0F, Dp.recovered$95b3d103663bf060(var160), 0.0F, 0.0F, 13, null);
            TextKt.recovered$5b114ab32a4f4190(
               var153, var150, var99, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var149, var65, 48, 0, 131064
            );
         }

         var65.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var65);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var56.endNode();
         ComposerKt.sourceInformationMarkerEnd(var56);
         ComposerKt.sourceInformationMarkerEnd(var49);
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var134);
         ComposerKt.sourceInformationMarkerEnd(var102);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var172 = var102.endRestartGroup();
      if (var172 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1506_0 = var0;
         final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel recoveredExpressionCapture_1506_1 = var1;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1506_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1506_3 = var3;
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredExpressionCapture_1506_4 = var4;
         final io.intercom.android.sdk.survey.ValidationError recoveredExpressionCapture_1506_5 = var5;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1506_6 = var6;
         final kotlin.jvm.functions.Function2 recoveredExpressionCapture_1506_7 = var7;
         final int recoveredExpressionCapture_1506_8 = var9;
         final int recoveredExpressionCapture_1506_9 = var10;
         var172.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1506_0, recoveredExpressionArgument_1506_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt.ShortTextQuestion$lambda$10(recoveredExpressionCapture_1506_0, recoveredExpressionCapture_1506_1, recoveredExpressionCapture_1506_2, recoveredExpressionCapture_1506_3, recoveredExpressionCapture_1506_4, recoveredExpressionCapture_1506_5, recoveredExpressionCapture_1506_6, recoveredExpressionCapture_1506_7, recoveredExpressionCapture_1506_8, recoveredExpressionCapture_1506_9, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1506_0), ((java.lang.Integer)recoveredExpressionArgument_1506_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ShortTextPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(2147193389);
      ComposerKt.sourceInformation(var2, "C(ShortTextPreview)171@7778L720:ShortTextQuestion.kt#z2rnlt");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2147193389, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextPreview (ShortTextQuestion.kt:170)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$ShortTextQuestionKt.INSTANCE.recovered$1400af5e34386102(),
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
         final int recoveredExpressionCapture_1504_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1504_0, recoveredExpressionArgument_1504_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt.ShortTextPreview$lambda$11(recoveredExpressionCapture_1504_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1504_0), ((java.lang.Integer)recoveredExpressionArgument_1504_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ShortTextAnsweredPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1590545552);
      ComposerKt.sourceInformation(var2, "C(ShortTextAnsweredPreview)194@8590L765:ShortTextQuestion.kt#z2rnlt");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1590545552, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextAnsweredPreview (ShortTextQuestion.kt:193)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$ShortTextQuestionKt.INSTANCE.recovered$fd838d44303763da(),
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
         final int recoveredExpressionCapture_1501_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1501_0, recoveredExpressionArgument_1501_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt.ShortTextAnsweredPreview$lambda$12(recoveredExpressionCapture_1501_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1501_0), ((java.lang.Integer)recoveredExpressionArgument_1501_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ShortTextPhoneNumberPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-38271892);
      ComposerKt.sourceInformation(var2, "C(ShortTextPhoneNumberPreview)218@9450L717:ShortTextQuestion.kt#z2rnlt");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -38271892, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextPhoneNumberPreview (ShortTextQuestion.kt:217)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$ShortTextQuestionKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
         final int recoveredExpressionCapture_1503_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1503_0, recoveredExpressionArgument_1503_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt.ShortTextPhoneNumberPreview$lambda$13(recoveredExpressionCapture_1503_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1503_0), ((java.lang.Integer)recoveredExpressionArgument_1503_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ShortTextDisabledPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1539795729);
      ComposerKt.sourceInformation(var2, "C(ShortTextDisabledPreview)241@10259L798:ShortTextQuestion.kt#z2rnlt");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1539795729, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextDisabledPreview (ShortTextQuestion.kt:240)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$ShortTextQuestionKt.INSTANCE.recovered$265b6f46b79d310a(),
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
         final int recoveredExpressionCapture_1502_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1502_0, recoveredExpressionArgument_1502_1) -> io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt.ShortTextDisabledPreview$lambda$14(recoveredExpressionCapture_1502_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1502_0), ((java.lang.Integer)recoveredExpressionArgument_1502_1).intValue())));
      }
   }

   private static final Unit ShortTextQuestion$lambda$1$lambda$0(KeyboardActionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return Unit.INSTANCE;
   }

   private static final Unit ShortTextQuestion$lambda$4$lambda$3(CoroutineScope var0, BringIntoViewRequester var1, FocusState var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      if (var2.isFocused()) {
         BuildersKt.launch$default(var0, null, null, new ShortTextQuestionKt$ShortTextQuestion$2$1$1(var1, null), 3, null);
      }

      return Unit.INSTANCE;
   }

   private static final Unit ShortTextQuestion$lambda$9$lambda$8$lambda$6$lambda$5(Function1 var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      if (var1.length() > 0) {
         var0.invoke(new io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer(var1));
      } else {
         var0.invoke(io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$ResetNoAnswer.INSTANCE);
      }

      return Unit.INSTANCE;
   }

   private static final Unit ShortTextQuestion$lambda$10(
      Modifier var0,
      io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel var1,
      Answer var2,
      Function1 var3,
      SurveyUiColors var4,
      ValidationError var5,
      Function1 var6,
      Function2 var7,
      int var8,
      int var9,
      Composer var10,
      int var11
   ) {
      ShortTextQuestion(var0, var1, var2, var3, var4, var5, var6, var7, var10, RecomposeScopeImplKt.updateChangedFlags(var8 | 1), var9);
      return Unit.INSTANCE;
   }

   private static final Unit ShortTextPreview$lambda$11(int var0, Composer var1, int var2) {
      ShortTextPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ShortTextAnsweredPreview$lambda$12(int var0, Composer var1, int var2) {
      ShortTextAnsweredPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ShortTextPhoneNumberPreview$lambda$13(int var0, Composer var1, int var2) {
      ShortTextPhoneNumberPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ShortTextDisabledPreview$lambda$14(int var0, Composer var1, int var2) {
      ShortTextDisabledPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

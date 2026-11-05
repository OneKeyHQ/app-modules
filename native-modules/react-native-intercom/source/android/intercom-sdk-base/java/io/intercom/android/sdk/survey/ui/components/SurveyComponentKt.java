package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.BoxWithConstraintsKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Brush.Companion;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.tooling.preview.Preview;
import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.AttachmentSettings;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.ConversationStateSyncSettings;
import io.intercom.android.sdk.survey.ProgressBarState;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.SurveyState;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.TopBarState;
import io.intercom.android.sdk.survey.SurveyState$Content;
import io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta;
import io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta$Fallback;
import io.intercom.android.sdk.survey.SurveyState$Error$WithCTA;
import io.intercom.android.sdk.survey.TopBarState$SenderTopBarState;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.survey.model.SurveyData$Step;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.SetsKt;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u001ac\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\u00052\u0014\b\u0002\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u0005H\u0001¢\u0006\u0002\u0010\r\u001a[\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000f2\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0001¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0014\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0014\"\u0014\u0010\u0016\u001a\u00020\u0017X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019¨\u0006\u001a",
   d2 = {
         "SurveyComponent",
         "",
         "state",
         "Lio/intercom/android/sdk/survey/SurveyState;",
         "onContinue",
         "Lkotlin/Function1;",
         "Lkotlinx/coroutines/CoroutineScope;",
         "onClose",
         "Lkotlin/Function0;",
         "onAnswerUpdated",
         "",
         "onSecondaryCtaClicked",
         "Lio/intercom/android/sdk/survey/SurveyState$Content$SecondaryCta;",
         "(Lio/intercom/android/sdk/survey/SurveyState;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "SurveyContent",
         "Lio/intercom/android/sdk/survey/SurveyState$Content;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/survey/SurveyState$Content;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "SimpleSurvey",
         "(Landroidx/compose/runtime/Composer;I)V",
         "SurveyErrorState",
         "emptyAppConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "getEmptyAppConfig",
         "()Lio/intercom/android/sdk/identity/AppConfig;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nSurveyComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SurveyComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyComponentKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 7 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,367:1\n1047#2,6:368\n1047#2,6:374\n1047#2,3:423\n1050#2,3:427\n1047#2,6:434\n1047#2,6:440\n1047#2,6:446\n1047#2,6:452\n1047#2,6:458\n1047#2,6:464\n1047#2,6:470\n70#3:380\n67#3,9:381\n77#3:415\n81#4,6:390\n88#4,6:405\n96#4:414\n402#5,9:396\n411#5,3:411\n615#6:416\n612#6,6:417\n613#7:426\n1563#8:430\n1634#8,3:431\n*S KotlinDebug\n*F\n+ 1 SurveyComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyComponentKt\n*L\n70#1:368,6\n71#1:374,6\n136#1:423,3\n136#1:427,3\n303#1:434,6\n304#1:440,6\n305#1:446,6\n322#1:452,6\n324#1:458,6\n325#1:464,6\n326#1:470,6\n90#1:380\n90#1:381,9\n90#1:415\n90#1:390,6\n90#1:405,6\n90#1:414\n90#1:396,9\n90#1:411,3\n136#1:416\n136#1:417,6\n136#1:426\n294#1:430\n294#1:431,3\n*E\n"
)
public final class SurveyComponentKt {
   @NotNull
   private static final AppConfig emptyAppConfig = new AppConfig(
      "",
      0,
      0,
      0,
      false,
      false,
      false,
      100,
      1000L,
      1000L,
      1000L,
      1000L,
      true,
      true,
      "",
      "",
      false,
      true,
      "",
      SetsKt.emptySet(),
      SetsKt.emptySet(),
      "",
      "",
      false,
      true,
      false,
      false,
      false,
      null,
      new NexusConfig(),
      AttachmentSettings.Companion.getDEFAULT(),
      true,
      ConversationStateSyncSettings.Companion.getDEFAULT(),
      true
   );

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SurveyComponent(
      @NotNull SurveyState var0,
      @NotNull Function1 var1,
      @NotNull Function0 var2,
      @Nullable Function1 var3,
      @Nullable Function1 var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "state");
      Intrinsics.checkNotNullParameter(var1, "onContinue");
      Intrinsics.checkNotNullParameter(var2, "onClose");
      Composer var41 = var5.startRestartGroup(290100480);
      ComposerKt.sourceInformation(var41, "C(SurveyComponent)P(4,2,1)69@3539L2,70@3615L2,89@4266L1338:SurveyComponent.kt#bmpoeo");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var41.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var41.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var41.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var41.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var41.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var41.getSkipping()) {
         var41.skipToGroupEnd();
      } else {
         if ((var7 & 8) != 0) {
            var41.startReplaceGroup(-1619621977);
            ComposerKt.sourceInformation(var41, "CC(remember):SurveyComponent.kt#9igjgp");
            Composer var10 = var41;
            boolean var11 = false;
            int var12 = 0;
            Object var13 = var10.rememberedValue();
            int var14 = 0;
            Object var10000;
            if (var13 == Composer.Companion.getEmpty()) {
               int var15 = 0;
               Object var56 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1343_0) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SurveyComponent$lambda$1$lambda$0((java.lang.String) ((java.lang.String)recoveredExpressionArgument_1343_0)));
               var10.updateRememberedValue(var56);
               var10000 = var56;
            } else {
               var10000 = var13;
            }

            Function1 var9 = (Function1)var10000;
            var41.endReplaceGroup();
            var3 = var9;
         }

         if ((var7 & 16) != 0) {
            var41.startReplaceGroup(-1619619545);
            ComposerKt.sourceInformation(var41, "CC(remember):SurveyComponent.kt#9igjgp");
            Composer var44 = var41;
            boolean var48 = false;
            int var50 = 0;
            Object var52 = var44.rememberedValue();
            int var54 = 0;
            Object var60;
            if (var52 == Composer.Companion.getEmpty()) {
               int var57 = 0;
               Object var58 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1344_0) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SurveyComponent$lambda$3$lambda$2((io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta) ((io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta)recoveredExpressionArgument_1344_0)));
               var44.updateRememberedValue(var58);
               var60 = var58;
            } else {
               var60 = var52;
            }

            Function1 var42 = (Function1)var60;
            var41.endReplaceGroup();
            var4 = var42;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(290100480, var8, -1, "io.intercom.android.sdk.survey.ui.components.SurveyComponent (SurveyComponent.kt:71)");
         }

         List var61;
         if (ColorExtensionsKt.recovered$41e561cae61044c5(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d())) {
            Color[] var45 = new Color[]{
               Color.recovered$f46ef195ad4a2363(ColorExtensionsKt.recovered$da172df9fccf1b21(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d())),
               Color.recovered$f46ef195ad4a2363(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d()),
               Color.recovered$f46ef195ad4a2363(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d()),
               Color.recovered$f46ef195ad4a2363(ColorExtensionsKt.recovered$da172df9fccf1b21(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d()))
            };
            var61 = CollectionsKt.listOf(var45);
         } else {
            Color[] var46 = new Color[]{
               Color.recovered$f46ef195ad4a2363(ColorExtensionsKt.recovered$de7fe4417d7923f0(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d())),
               Color.recovered$f46ef195ad4a2363(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d()),
               Color.recovered$f46ef195ad4a2363(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d()),
               Color.recovered$f46ef195ad4a2363(ColorExtensionsKt.recovered$de7fe4417d7923f0(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d()))
            };
            var61 = CollectionsKt.listOf(var46);
         }

         List var43 = var61;
         Brush var47 = Companion.recovered$ea3ca130c5d0d624(Brush.Companion, var43, 0.0F, 0.0F, 0, 14, null);
         Modifier var49 = BackgroundKt.background$default(Modifier.Companion, var47, null, 0.0F, 6, null);
         int var55 = 0;
         int var59 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var41, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var51 = Alignment.Companion.getTopStart();
         boolean var53 = false;
         MeasurePolicy var16 = BoxKt.maybeCachedBoxMeasurePolicy(var51, var53);
         Modifier var17 = var49;
         MeasurePolicy var18 = var16;
         Composer var19 = var41;
         int var20 = 112 & var55 << 3;
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
         ComposerKt.sourceInformationMarkerStart(var32, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var34 = 6 | 112 & var55 >> 6;
         Composer var35 = var32;
         BoxScope var36 = BoxScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, -272062591, "C91@4395L13,93@4480L216,100@4720L867,90@4320L1278:SurveyComponent.kt#bmpoeo");
         Modifier var38 = WindowInsetsPaddingKt.windowInsetsPadding(
            Modifier.Companion, WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var35, 6)
         );
         long var39 = Color.Companion.recovered$562f21ab17d9b4c7();
         ScaffoldKt.recovered$857cf99fc8161460(
            var38,
            ComposableLambdaKt.rememberComposableLambda(-365860662, true, new SurveyComponentKt$SurveyComponent$3$1(var0, var2), var35, 54),
            null,
            null,
            null,
            0,
            var39,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1342576427, true, new SurveyComponentKt$SurveyComponent$3$2(var0, var1, var3, var4), var35, 54),
            var35,
            806879280,
            444
         );
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var41);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var62 = var41.endRestartGroup();
      if (var62 != null) {
         final io.intercom.android.sdk.survey.SurveyState recoveredExpressionCapture_1345_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1345_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1345_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1345_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1345_4 = var4;
         final int recoveredExpressionCapture_1345_5 = var6;
         final int recoveredExpressionCapture_1345_6 = var7;
         var62.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1345_0, recoveredExpressionArgument_1345_1) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SurveyComponent$lambda$5(recoveredExpressionCapture_1345_0, recoveredExpressionCapture_1345_1, recoveredExpressionCapture_1345_2, recoveredExpressionCapture_1345_3, recoveredExpressionCapture_1345_4, recoveredExpressionCapture_1345_5, recoveredExpressionCapture_1345_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1345_0), ((java.lang.Integer)recoveredExpressionArgument_1345_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SurveyContent(
      @NotNull io.intercom.android.sdk.survey.SurveyState$Content var0,
      @NotNull Function1 var1,
      @NotNull Function1 var2,
      @NotNull Function1 var3,
      @Nullable Modifier var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "state");
      Intrinsics.checkNotNullParameter(var1, "onContinue");
      Intrinsics.checkNotNullParameter(var2, "onAnswerUpdated");
      Intrinsics.checkNotNullParameter(var3, "onSecondaryCtaClicked");
      Composer var22 = var5.startRestartGroup(433920899);
      ComposerKt.sourceInformation(var22, "C(SurveyContent)P(4,2,1,3)135@5896L24,137@5980L3299,137@5926L3353:SurveyComponent.kt#bmpoeo");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var22.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var22.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var22.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var22.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var22.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var22.getSkipping()) {
         var22.skipToGroupEnd();
      } else {
         if ((var7 & 16) != 0) {
            var4 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(433920899, var8, -1, "io.intercom.android.sdk.survey.ui.components.SurveyContent (SurveyComponent.kt:134)");
         }

         boolean var10 = false;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var12 = var22;
         ComposerKt.sourceInformationMarkerStart(var22, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var13 = var22;
         boolean var14 = false;
         int var15 = 0;
         Object var16 = var13.rememberedValue();
         int var17 = 0;
         Object var10000;
         if (var16 == Composer.Companion.getEmpty()) {
            int var18 = 0;
            int var19 = 0;
            Object var23 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var12);
            var13.updateRememberedValue(var23);
            var10000 = var23;
         } else {
            var10000 = var16;
         }

         CoroutineScope var20 = (CoroutineScope)var10000;
         ComposerKt.sourceInformationMarkerEnd(var22);
         CoroutineScope var21 = var20;
         ComposerKt.sourceInformationMarkerEnd(var22);
         CoroutineScope var9 = var21;
         BoxWithConstraintsKt.BoxWithConstraints(
            SizeKt.fillMaxSize$default(var4, 0.0F, 1, null),
            null,
            false,
            ComposableLambdaKt.rememberComposableLambda(-1844267539, true, new SurveyComponentKt$SurveyContent$1(var0, var1, var9, var3, var2), var22, 54),
            var22,
            3072,
            6
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var24 = var22.endRestartGroup();
      if (var24 != null) {
         final io.intercom.android.sdk.survey.SurveyState$Content recoveredExpressionCapture_1346_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1346_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1346_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1346_3 = var3;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1346_4 = var4;
         final int recoveredExpressionCapture_1346_5 = var6;
         final int recoveredExpressionCapture_1346_6 = var7;
         var24.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1346_0, recoveredExpressionArgument_1346_1) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SurveyContent$lambda$6(recoveredExpressionCapture_1346_0, recoveredExpressionCapture_1346_1, recoveredExpressionCapture_1346_2, recoveredExpressionCapture_1346_3, recoveredExpressionCapture_1346_4, recoveredExpressionCapture_1346_5, recoveredExpressionCapture_1346_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1346_0), ((java.lang.Integer)recoveredExpressionArgument_1346_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SimpleSurvey(@Nullable Composer var0, int var1) {
      Composer var28 = var0.startRestartGroup(126014647);
      ComposerKt.sourceInformation(var28, "C(SimpleSurvey)302@13071L2,303@13093L2,304@13123L2,223@9406L3726:SurveyComponent.kt#bmpoeo");
      if (var1 == 0 && var28.getSkipping()) {
         var28.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(126014647, var1, -1, "io.intercom.android.sdk.survey.ui.components.SimpleSurvey (SurveyComponent.kt:221)");
         }

         SurveyUiColors var2 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
         io.intercom.android.sdk.blocks.lib.models.Block$Builder[] var4 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder[]{
            new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("<b>Step 1</b>").withType(BlockType.HEADING.getSerializedName()),
            new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Get tailored discounts to your inbox").withType(BlockType.PARAGRAPH.getSerializedName())
         };
         io.intercom.android.sdk.survey.model.SurveyData$Step var3 = new io.intercom.android.sdk.survey.model.SurveyData$Step(CollectionsKt.listOf(var4), "preview-step", CollectionsKt.emptyList(), 1, null, null, 48, null);
         Avatar var10000 = Avatar.create("", "AD");
         Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
         Avatar var5 = var10000;
         AppConfig var6 = emptyAppConfig;
         ProgressBarState var7 = new ProgressBarState(true, 0.5F);
         io.intercom.android.sdk.survey.TopBarState$SenderTopBarState var32 = new io.intercom.android.sdk.survey.TopBarState$SenderTopBarState(var5, "Andy", var6, true, var2, var7);
         io.intercom.android.sdk.blocks.lib.models.Block$Builder[] var40 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder[]{
            new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("<b>Step 1</b>").withType(BlockType.HEADING.getSerializedName()),
            new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Get tailored discounts to your inbox").withType(BlockType.PARAGRAPH.getSerializedName())
         };
         List var36 = CollectionsKt.listOf(var40);
         QuestionState[] var45 = new QuestionState[3];
         String var10004 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var10004, "toString(...)");
         String var8 = var10004;
         List var9 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Is this a preview?"));
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType var10 = io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.NO_VALIDATION;
         var45[0] = new QuestionState(new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel(var8, var9, true, "Let us know", var10, 250, false, null, null, 448, null), var2);
         String var81 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var81, "toString(...)");
         String var50 = var81;
         List var56 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Question Title"));
         String[] var11 = new String[]{"Option A", "Option B", "Option C", "Option D"};
         List var64 = CollectionsKt.listOf(var11);
         var45[1] = new QuestionState(
            new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel(var50, var56, true, var64, false), SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null))
         );
         String var10002 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var10002, "toString(...)");
         String var51 = var10002;
         List var57 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("How would your rate your experience?"));
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType var65 = io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType.STARS;
         Iterable var12 = new IntRange(1, 5);
         byte var22 = 2;
         QuestionState[] var21 = var45;
         int var13 = 0;
         Iterable var14 = var12;
         ArrayList var15 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var12, 10));
         int var16 = 0;
         Iterator var17 = var14.iterator();

         while (var17.hasNext()) {
            int var18 = ((IntIterator)var17).nextInt();
            int var19 = var18;
            Collection var25 = var15;
            int var20 = 0;
            var25.add(new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption(var19));
         }

         List var73 = (List)var15;
         List var66 = var73;
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel var78 = new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel(var51, var57, true, var66, "Poor", "Excellent", 1, 5, var65);
         SurveyUiColors var26 = var2;
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel var27 = var78;
         var21[var22] = new QuestionState(var27, var26);
         List var41 = CollectionsKt.listOf(var45);
         io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta$Fallback var46 = new io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta$Fallback(string.intercom_surveys_next_button);
         List var52 = CollectionsKt.emptyList();
         SurveyState var74 = new io.intercom.android.sdk.survey.SurveyState$Content(var3, var36, var41, var52, var46, var2, var32);
         var28.startReplaceGroup(-688243060);
         ComposerKt.sourceInformation(var28, "CC(remember):SurveyComponent.kt#9igjgp");
         Composer var33 = var28;
         boolean var37 = false;
         int var42 = 0;
         Object var47 = var33.rememberedValue();
         int var53 = 0;
         Object var76;
         if (var47 == Composer.Companion.getEmpty()) {
            SurveyState var67 = var74;
            int var58 = 0;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1339_0) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SimpleSurvey$lambda$9$lambda$8((kotlinx.coroutines.CoroutineScope) ((kotlinx.coroutines.CoroutineScope)recoveredExpressionArgument_1339_0)));
            var74 = var67;
            Object var59 = var10001;
            var33.updateRememberedValue(var59);
            var76 = var59;
         } else {
            var76 = var47;
         }

         Function1 var29 = (Function1)var76;
         var28.endReplaceGroup();
         Function1 var77 = var29;
         var28.startReplaceGroup(-688242356);
         ComposerKt.sourceInformation(var28, "CC(remember):SurveyComponent.kt#9igjgp");
         Composer var34 = var28;
         boolean var38 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var34.rememberedValue();
         int var54/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var79;
         if (it$ivx == Composer.Companion.getEmpty()) {
            Function1 var70 = var29;
            SurveyState var68 = var74;
            int var60 = 0;
            Function0 var23 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SimpleSurvey$lambda$11$lambda$10());
            var74 = var68;
            var77 = var70;
            Object var61 = var23;
            var34.updateRememberedValue(var61);
            var79 = var61;
         } else {
            var79 = it$ivx;
         }

         Function0 var30 = (Function0)var79;
         var28.endReplaceGroup();
         Function0 var80 = var30;
         var28.startReplaceGroup(-688241396);
         ComposerKt.sourceInformation(var28, "CC(remember):SurveyComponent.kt#9igjgp");
         Composer var35 = var28;
         boolean var39 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var35.rememberedValue();
         int var55/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10003;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            Function0 var72 = var30;
            Function1 var71 = var77;
            SurveyState var69 = var74;
            int var62 = 0;
            Function1 var24 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1341_0) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SimpleSurvey$lambda$13$lambda$12((java.lang.String) ((java.lang.String)recoveredExpressionArgument_1341_0)));
            var74 = var69;
            var77 = var71;
            var80 = var72;
            Object var63 = var24;
            var35.updateRememberedValue(var63);
            var10003 = var63;
         } else {
            var10003 = it$ivxx;
         }

         Function1 var31 = (Function1)var10003;
         var28.endReplaceGroup();
         SurveyComponent(var74, var77, var80, var31, null, var28, 3504, 16);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var75 = var28.endRestartGroup();
      if (var75 != null) {
         final int recoveredExpressionCapture_1342_0 = var1;
         var75.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1342_0, recoveredExpressionArgument_1342_1) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SimpleSurvey$lambda$14(recoveredExpressionCapture_1342_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1342_0), ((java.lang.Integer)recoveredExpressionArgument_1342_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SurveyErrorState(@Nullable Composer var0, int var1) {
      Composer var23 = var0.startRestartGroup(-1165269984);
      ComposerKt.sourceInformation(var23, "C(SurveyErrorState)321@13656L2,323@13692L3,324@13715L3,325@13746L2,311@13195L560:SurveyComponent.kt#bmpoeo");
      if (var1 == 0 && var23.getSkipping()) {
         var23.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1165269984, var1, -1, "io.intercom.android.sdk.survey.ui.components.SurveyErrorState (SurveyComponent.kt:310)");
         }

         Avatar var10000 = Avatar.create("", "AD");
         Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
         Avatar var3 = var10000;
         AppConfig var4 = emptyAppConfig;
         SurveyUiColors var5 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
         io.intercom.android.sdk.survey.TopBarState$SenderTopBarState var2 = new io.intercom.android.sdk.survey.TopBarState$SenderTopBarState(var3, "Andy", var4, true, var5, null, 32, null);
         SurveyUiColors var27 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
         byte var58 = 0;
         SurveyUiColors var10001 = var27;
         TopBarState var10002 = var2;
         var23.startReplaceGroup(178647459);
         ComposerKt.sourceInformation(var23, "CC(remember):SurveyComponent.kt#9igjgp");
         Composer var35 = var23;
         boolean var6 = false;
         int var7 = 0;
         Object var8 = var35.rememberedValue();
         int var9 = 0;
         Object var10003;
         if (var8 == Composer.Companion.getEmpty()) {
            TopBarState var15 = var10002;
            SurveyUiColors var14 = var27;
            byte var13 = 0;
            int var10 = 0;
            Function0 var16 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SurveyErrorState$lambda$16$lambda$15());
            var58 = var13;
            var10001 = var14;
            var10002 = var15;
            Object var51 = var16;
            var35.updateRememberedValue(var51);
            var10003 = var51;
         } else {
            var10003 = var8;
         }

         Function0 var31 = (Function0)var10003;
         var23.endReplaceGroup();
         Object var17 = null;
         byte var18 = 1;
         Function0 var19 = var31;
         TopBarState var20 = var10002;
         SurveyUiColors var21 = var10001;
         byte var22 = var58;
         SurveyState var59 = new io.intercom.android.sdk.survey.SurveyState$Error$WithCTA(var22, var21, var20, var19, var18, (DefaultConstructorMarker)var17);
         var23.startReplaceGroup(178648612);
         ComposerKt.sourceInformation(var23, "CC(remember):SurveyComponent.kt#9igjgp");
         Composer var28 = var23;
         boolean var32 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var28.rememberedValue();
         int var42/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var62;
         if (it$ivx == Composer.Companion.getEmpty()) {
            SurveyState var11 = var59;
            int var45 = 0;
            Function1 var61 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1348_0) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SurveyErrorState$lambda$18$lambda$17((kotlinx.coroutines.CoroutineScope) ((kotlinx.coroutines.CoroutineScope)recoveredExpressionArgument_1348_0)));
            var59 = var11;
            Object var46 = var61;
            var28.updateRememberedValue(var46);
            var62 = var46;
         } else {
            var62 = it$ivx;
         }

         Function1 var24 = (Function1)var62;
         var23.endReplaceGroup();
         Function1 var63 = var24;
         var23.startReplaceGroup(178649348);
         ComposerKt.sourceInformation(var23, "CC(remember):SurveyComponent.kt#9igjgp");
         Composer var29 = var23;
         boolean var33 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var29.rememberedValue();
         int var43/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var64;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            Function1 var12 = var24;
            SurveyState var52 = var59;
            int var47 = 0;
            Function0 var55 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SurveyErrorState$lambda$20$lambda$19());
            var59 = var52;
            var63 = var12;
            Object var48 = var55;
            var29.updateRememberedValue(var48);
            var64 = var48;
         } else {
            var64 = it$ivxx;
         }

         Function0 var25 = (Function0)var64;
         var23.endReplaceGroup();
         Function0 var65 = var25;
         var23.startReplaceGroup(178650339);
         ComposerKt.sourceInformation(var23, "CC(remember):SurveyComponent.kt#9igjgp");
         Composer var30 = var23;
         boolean var34 = false;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var30.rememberedValue();
         int var44/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var66;
         if (it$ivxxx == Composer.Companion.getEmpty()) {
            Function0 var56 = var25;
            Function1 var54 = var63;
            SurveyState var53 = var59;
            int var49 = 0;
            Function1 var57 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1350_0) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SurveyErrorState$lambda$22$lambda$21((java.lang.String) ((java.lang.String)recoveredExpressionArgument_1350_0)));
            var59 = var53;
            var63 = var54;
            var65 = var56;
            Object var50 = var57;
            var30.updateRememberedValue(var50);
            var66 = var50;
         } else {
            var66 = it$ivxxx;
         }

         Function1 var26 = (Function1)var66;
         var23.endReplaceGroup();
         SurveyComponent(var59, var63, var65, var26, null, var23, 3504, 16);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var60 = var23.endRestartGroup();
      if (var60 != null) {
         final int recoveredExpressionCapture_1351_0 = var1;
         var60.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1351_0, recoveredExpressionArgument_1351_1) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt.SurveyErrorState$lambda$23(recoveredExpressionCapture_1351_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1351_0), ((java.lang.Integer)recoveredExpressionArgument_1351_1).intValue())));
      }
   }

   @NotNull
   public static final AppConfig getEmptyAppConfig() {
      return emptyAppConfig;
   }

   private static final Unit SurveyComponent$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit SurveyComponent$lambda$3$lambda$2(io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit SurveyComponent$lambda$5(
      SurveyState var0, Function1 var1, Function0 var2, Function1 var3, Function1 var4, int var5, int var6, Composer var7, int var8
   ) {
      SurveyComponent(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit SurveyContent$lambda$6(
      io.intercom.android.sdk.survey.SurveyState$Content var0, Function1 var1, Function1 var2, Function1 var3, Modifier var4, int var5, int var6, Composer var7, int var8
   ) {
      SurveyContent(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit SimpleSurvey$lambda$9$lambda$8(CoroutineScope var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit SimpleSurvey$lambda$11$lambda$10() {
      return Unit.INSTANCE;
   }

   private static final Unit SimpleSurvey$lambda$13$lambda$12(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit SimpleSurvey$lambda$14(int var0, Composer var1, int var2) {
      SimpleSurvey(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SurveyErrorState$lambda$16$lambda$15() {
      return Unit.INSTANCE;
   }

   private static final Unit SurveyErrorState$lambda$18$lambda$17(CoroutineScope var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit SurveyErrorState$lambda$20$lambda$19() {
      return Unit.INSTANCE;
   }

   private static final Unit SurveyErrorState$lambda$22$lambda$21(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit SurveyErrorState$lambda$23(int var0, Composer var1, int var2) {
      SurveyErrorState(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}

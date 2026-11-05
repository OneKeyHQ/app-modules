package io.intercom.android.sdk.survey.ui.questiontype.numericscale;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Absolute;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
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
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
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
   d1 = "\u0000 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a7\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\bH\u0001¢\u0006\u0002\u0010\t¨\u0006\n",
   d2 = {
         "EmojiQuestion",
         "",
         "options",
         "",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption;",
         "answer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "onAnswer",
         "Lkotlin/Function1;",
         "(Ljava/util/List;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nEmojiQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EmojiQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/EmojiQuestionKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,43:1\n118#2:44\n118#2:74\n99#3,6:45\n106#3:85\n81#4,6:51\n88#4,6:66\n96#4:84\n402#5,9:57\n411#5:72\n412#5,2:82\n1869#6:73\n1870#6:81\n1047#7,6:75\n*S KotlinDebug\n*F\n+ 1 EmojiQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/EmojiQuestionKt\n*L\n23#1:44\n35#1:74\n21#1:45,6\n21#1:85\n21#1:51,6\n21#1:66,6\n21#1:84\n21#1:57,9\n21#1:72\n21#1:82,2\n26#1:73\n26#1:81\n36#1:75,6\n*E\n"
)
public final class EmojiQuestionKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EmojiQuestion(@NotNull List var0, @NotNull Answer var1, @NotNull Function1 var2, @Nullable Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "options");
      Intrinsics.checkNotNullParameter(var1, "answer");
      Intrinsics.checkNotNullParameter(var2, "onAnswer");
      var3 = var3.startRestartGroup(1738433356);
      ComposerKt.sourceInformation(var3, "C(EmojiQuestion)P(2)20@788L839:EmojiQuestion.kt#hdztyp");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changedInstance(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1738433356, var5, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.EmojiQuestion (EmojiQuestion.kt:19)");
         }

         Modifier var6 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var7 = 12;
         int var8 = 0;
         Horizontal var59 = Absolute.INSTANCE.recovered$49b0bf381d0b9a78(Dp.recovered$95b3d103663bf060(var7), Alignment.Companion.getCenterHorizontally());
         Vertical var60 = Alignment.Companion.getCenterVertically();
         short var9 = 438;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var11 = RowKt.rowMeasurePolicy(var59, var60, var3, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var6;
         MeasurePolicy var13 = var11;
         Composer var14 = var3;
         int var15 = 112 & var9 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var12);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var14;
         int var22 = 6 | 896 & var15 << 6;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var21.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var21.startReusableNode();
         if (var21.getInserting()) {
            var21.createNode(var20);
         } else {
            var21.useNode();
         }

         Composer var24 = Updater.recovered$676aa17b44fd6280(var21);
         int var25 = 0;
         Updater.recovered$89682b199a142880(var24, var13, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         RowScope var31 = RowScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1133962129, "C:EmojiQuestion.kt#hdztyp");
         var30.startReplaceGroup(-1487440982);
         ComposerKt.sourceInformation(var30, "*35@1494L102,29@1186L425");
         Iterable var33 = var0;
         int var34 = 0;

         for (Object var36 : var33) {
            SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption var37 = (SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption)var36;
            int var38 = 0;
            SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption var39 = var37;
            boolean var40 = var1 instanceof Answer$SingleAnswer
               && Intrinsics.areEqual(((Answer$SingleAnswer)var1).getAnswer(), String.valueOf(var39.getValue()));
            String var10000 = var39.getEmojiUrl();
            String var10001 = var39.getUnicode();
            boolean var10002 = var1 instanceof Answer$NoAnswer || var40;
            Modifier var10003 = Modifier.Companion;
            float var10004;
            if (var40) {
               int $this$dp$ivx = 34;
               int $i$f$getDpx = 0;
               var10004 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            } else {
               int $this$dp$ivx = 32;
               int $i$f$getDpx = 0;
               var10004 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            }

            var10003 = SizeKt.recovered$111ef94c93e78c04(var10003, var10004);
            boolean var67 = false;
            Object var10005 = null;
            Object var10006 = null;
            Object var10007 = null;
            var30.startReplaceGroup(1945180755);
            ComposerKt.sourceInformation(var30, "CC(remember):EmojiQuestion.kt#9igjgp");
            Composer var64 = var30;
            boolean var43 = (var5 & 896) == 256 | var3.changed(var39);
            int var44 = 0;
            Object var45 = var64.rememberedValue();
            int var46 = 0;
            Object var10008;
            if (!var43 && var45 != Composer.Companion.getEmpty()) {
               var10008 = var45;
            } else {
               Object var47 = null;
               Object var48 = null;
               Object var49 = null;
               boolean var50 = false;
               Modifier var51 = var10003;
               boolean var52 = var10002;
               String var53 = var10001;
               String var54 = var10000;
               int var55 = 0;
               final kotlin.jvm.functions.Function1 recoveredCapture_1466_0 = var2;
               final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption recoveredCapture_1466_1 = var39;
               Function0 var56 = ((kotlin.jvm.functions.Function0) () -> EmojiQuestionKt.EmojiQuestion$lambda$3$lambda$2$lambda$1$lambda$0(recoveredCapture_1466_0, recoveredCapture_1466_1));
               var10000 = var54;
               var10001 = var53;
               var10002 = var52;
               var10003 = var51;
               var67 = var50;
               var10005 = var49;
               var10006 = var48;
               var10007 = var47;
               Object var57 = var56;
               var64.updateRememberedValue(var57);
               var10008 = var57;
            }

            Function0 var62 = (Function0)var10008;
            var30.endReplaceGroup();
            EmojiRatingKt.EmojiRating(
               var10000,
               var10001,
               var10002,
               ClickableKt.recovered$1c9660c41fd850dc(var10003, var67, (String)var10005, (Role)var10006, (MutableInteractionSource)var10007, var62, 15, null),
               var30,
               0,
               0
            );
         }

         var30.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var65 = var3.endRestartGroup();
      if (var65 != null) {
         final java.util.List recoveredCapture_1467_0 = var0;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1467_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredCapture_1467_2 = var2;
         final int recoveredCapture_1467_3 = var4;
         var65.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1467_0, recoveredArgument_1467_1) -> EmojiQuestionKt.EmojiQuestion$lambda$4(recoveredCapture_1467_0, recoveredCapture_1467_1, recoveredCapture_1467_2, recoveredCapture_1467_3, (androidx.compose.runtime.Composer)recoveredArgument_1467_0, ((java.lang.Integer)recoveredArgument_1467_1).intValue())));
      }
   }

   private static final Unit EmojiQuestion$lambda$3$lambda$2$lambda$1$lambda$0(
      Function1 var0, SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption var1
   ) {
      var0.invoke(new Answer$SingleAnswer(String.valueOf(var1.getValue())));
      return Unit.INSTANCE;
   }

   private static final Unit EmojiQuestion$lambda$4(List var0, Answer var1, Function1 var2, int var3, Composer var4, int var5) {
      EmojiQuestion(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }
}

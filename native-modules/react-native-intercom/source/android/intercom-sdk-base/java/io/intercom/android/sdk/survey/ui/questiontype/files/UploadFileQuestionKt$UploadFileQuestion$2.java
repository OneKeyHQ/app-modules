package io.intercom.android.sdk.survey.ui.questiontype.files;

import android.content.Context;
import android.net.Uri;
import androidx.compose.foundation.Indication;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.utils.URIExtensionsKt;
import io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt;
import io.intercom.android.sdk.m5.preview.ui.MediaType;
import io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle$TextButton;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nUploadFileQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UploadFileQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/UploadFileQuestionKt$UploadFileQuestion$2\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 7 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 8 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,115:1\n87#2:116\n84#2,9:117\n94#2:165\n81#3,6:126\n88#3,6:141\n96#3:164\n402#4,9:132\n411#4:147\n412#4,2:162\n118#5:148\n118#5:155\n1047#6,6:149\n1047#6,6:156\n1617#7,9:166\n1869#7:175\n1870#7:177\n1626#7:178\n1563#7:179\n1634#7,3:180\n1#8:176\n*S KotlinDebug\n*F\n+ 1 UploadFileQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/UploadFileQuestionKt$UploadFileQuestion$2\n*L\n40#1:116\n40#1:117,9\n40#1:165\n40#1:126,6\n40#1:141,6\n40#1:164\n40#1:132,9\n40#1:147\n40#1:162,2\n42#1:148\n58#1:155\n47#1:149,6\n67#1:156,6\n68#1:166,9\n68#1:175\n68#1:177\n68#1:178\n69#1:179\n69#1:180,3\n68#1:176\n*E\n"
)
final class UploadFileQuestionKt$UploadFileQuestion$2 implements Function2 {
   // $VF: synthetic field
   final Modifier $modifier;
   // $VF: synthetic field
   final Function2 $questionHeader;
   // $VF: synthetic field
   final Answer $answer;
   // $VF: synthetic field
   final Function1 $onAnswerClick;
   // $VF: synthetic field
   final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel $questionModel;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final Function1 $onAnswer;

   UploadFileQuestionKt$UploadFileQuestion$2(
      Modifier var1, Function2 var2, Answer var3, Function1 var4, io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel var5, Context var6, Function1 var7
   ) {
      this.$modifier = var1;
      this.$questionHeader = var2;
      this.$answer = var3;
      this.$onAnswerClick = var4;
      this.$questionModel = var5;
      this.$context = var6;
      this.$onAnswer = var7;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C39@1803L2348:UploadFileQuestion.kt#lphomb");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1739158412, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestion.<anonymous> (UploadFileQuestion.kt:39)"
            );
         }

         Modifier var3 = this.$modifier;
         Function2 var6 = this.$questionHeader;
         Answer var7 = this.$answer;
         Function1 var8 = this.$onAnswerClick;
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel var9 = this.$questionModel;
         Context var10 = this.$context;
         Function1 var11 = this.$onAnswer;
         int var12 = 0;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var4 = Arrangement.INSTANCE.getTop();
         Horizontal var5 = Alignment.Companion.getStart();
         MeasurePolicy var14 = ColumnKt.columnMeasurePolicy(var4, var5, var1, 14 & var12 >> 3 | 112 & var12 >> 3);
         Modifier var15 = var3;
         MeasurePolicy var16 = var14;
         Composer var17 = var1;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         ColumnScope var34 = ColumnScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -2053978433, "C40@1845L16,41@1874L40:UploadFileQuestion.kt#lphomb");
         var6.invoke(var33, 0);
         Modifier var10000 = Modifier.Companion;
         int var36 = 8;
         int var37 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var36)), var33, 6);
         var33.startReplaceGroup(903575015);
         ComposerKt.sourceInformation(var33, "46@2145L287,44@2044L407,57@2586L40");
         if (var7 instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer) {
            List var63 = ((io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer)var7).getMediaItems();
            var33.startReplaceGroup(903581547);
            ComposerKt.sourceInformation(var33, "CC(remember):UploadFileQuestion.kt#9igjgp");
            Composer var53 = var33;
            boolean var38 = var33.changed(var8) | var33.changedInstance(var9);
            int var39 = 0;
            Object var40 = var53.rememberedValue();
            int var41 = 0;
            Object var66;
            if (!var38 && var40 != Composer.Companion.getEmpty()) {
               var66 = var40;
            } else {
               List var42 = var63;
               int var43 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1464_0 = var8;
               final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel recoveredExpressionCapture_1464_1 = var9;
               Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1464_0) -> io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestionKt$UploadFileQuestion$2.invoke$lambda$7$lambda$1$lambda$0(recoveredExpressionCapture_1464_0, recoveredExpressionCapture_1464_1, (io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem) ((io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem)recoveredExpressionArgument_1464_0)));
               var63 = var42;
               Object var44 = var10001;
               var53.updateRememberedValue(var44);
               var66 = var44;
            }

            Function1 var50 = (Function1)var66;
            var33.endReplaceGroup();
            FileAttachmentListKt.FileAttachmentList(var63, var50, var33, 0);
            if (!((io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer)var7).getMediaItems().isEmpty()) {
               Modifier var64 = Modifier.Companion;
               int $this$dp$ivx = 8;
               int $i$f$getDpx = 0;
               SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var64, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var33, 6);
            }
         }

         var33.endReplaceGroup();
         int var52 = var7 instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer ? ((io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer)var7).getMediaItems().size() : 0;
         var33.startReplaceGroup(903602228);
         ComposerKt.sourceInformation(var33, "66@3070L631,80@3813L97,84@3953L174,62@2820L1307");
         if (var52 < var9.getMaxSelection()) {
            int var65 = var9.getMaxSelection() - var52;
            Object var67 = null;
            MediaType var10002 = MediaType.All;
            Set var10003 = var9.getSupportedFileType();
            var33.startReplaceGroup(903611491);
            ComposerKt.sourceInformation(var33, "CC(remember):UploadFileQuestion.kt#9igjgp");
            Composer var56 = var33;
            boolean var57 = var33.changedInstance(var10) | var33.changed(var7) | var33.changed(var11);
            int var58 = 0;
            Object var59 = var56.rememberedValue();
            int var61 = 0;
            Object var10004;
            if (!var57 && var59 != Composer.Companion.getEmpty()) {
               var10004 = var59;
            } else {
               Set var45 = var10003;
               MediaType var46 = var10002;
               Object var47 = null;
               int var60 = var65;
               int var62 = 0;
               final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1465_0 = var7;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1465_1 = var11;
               final android.content.Context recoveredExpressionCapture_1465_2 = var10;
               Function1 var48 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1465_0) -> io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestionKt$UploadFileQuestion$2.invoke$lambda$7$lambda$6$lambda$5(recoveredExpressionCapture_1465_0, recoveredExpressionCapture_1465_1, recoveredExpressionCapture_1465_2, (java.util.List) ((java.util.List)recoveredExpressionArgument_1465_0)));
               var65 = var60;
               var67 = var47;
               var10002 = var46;
               var10003 = var45;
               Object var49 = var48;
               var56.updateRememberedValue(var49);
               var10004 = var49;
            }

            Function1 var55 = (Function1)var10004;
            var33.endReplaceGroup();
            MediaPickerButtonKt.MediaPickerButton(
               var65,
               (Indication)var67,
               var10002,
               var10003,
               var55,
               new io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle$TextButton(StringResources_androidKt.stringResource(string.intercom_add, var33, 0)),
               false,
               null,
               ComposableLambdaKt.rememberComposableLambda(2026251827, true, new UploadFileQuestionKt$UploadFileQuestion$2$1$3(var9), var33, 54),
               var33,
               100663680,
               194
            );
         }

         var33.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$7$lambda$1$lambda$0(Function1 var0, io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel var1, io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem var2) {
      Intrinsics.checkNotNullParameter(var2, "item");
      var0.invoke(new AnswerClickData(var2, var1.getId()));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6$lambda$5(Answer var0, Function1 var1, Context var2, List var3) {
      Intrinsics.checkNotNullParameter(var3, "uris");
      Iterable var5 = var3;
      int var6 = 0;
      Iterable var7 = var5;
      ArrayList var8 = new ArrayList();
      int var9 = 0;
      Iterable var10 = var7;
      int var11 = 0;

      for (Object var13 : var10) {
         Object var14 = var13;
         int var15 = 0;
         Uri var16 = (Uri)var14;
         int var17 = 0;
         io.intercom.android.sdk.m5.upload.data.MediaData$Media var10000 = URIExtensionsKt.getMediaData$default(var16, var2, false, 2, null);
         if (var10000 != null) {
            Object var18 = var10000;
            int var19 = 0;
            var8.add(var18);
         }
      }

      var var21 = (Iterable & List)var8;
      int var23 = 0;
      Iterable var25 = var21;
      var destination$iv$ivx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var21, 10));
      int var29 = 0;

      for (Object var31 : var25) {
         io.intercom.android.sdk.m5.upload.data.MediaData$Media var32 = (io.intercom.android.sdk.m5.upload.data.MediaData$Media)var31;
         Collection var20 = destination$iv$ivx;
         int var33 = 0;
         var20.add(new io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem(var32));
      }

      List var4 = (List)destination$iv$ivx;
      if (var0 instanceof io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer) {
         var1.invoke(new io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer(var4));
      } else {
         List var24 = new ArrayList();
         List var26 = var24;
         int var28 = 0;
         Intrinsics.checkNotNull(var0, "null cannot be cast to non-null type io.intercom.android.sdk.survey.ui.models.Answer.MediaAnswer");
         var26.addAll(((io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer)var0).getMediaItems());
         var26.addAll(var4);
         List var22 = var24;
         var1.invoke(new io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer(var22));
      }

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

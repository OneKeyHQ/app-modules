package io.intercom.android.sdk.survey.ui.questiontype.dropdown;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
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
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.AndroidMenu_androidKt;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\u001aZ\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0013\b\u0002\u0010\f\u001a\r\u0012\u0004\u0012\u00020\u00010\r¢\u0006\u0002\b\u000eH\u0001¢\u0006\u0002\u0010\u000f\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0011\"\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014²\u0006\n\u0010\u0015\u001a\u00020\u0016X\u008a\u008e\u0002",
   d2 = {
         "DropDownQuestion",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "dropDownQuestionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$DropDownQuestionModel;",
         "answer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "onAnswer",
         "Lkotlin/Function1;",
         "colors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "questionHeader",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$DropDownQuestionModel;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/survey/SurveyUiColors;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "DropDownQuestionPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "DropDownSelectedQuestionPreview",
         "ColoredDropDownSelectedQuestionPreview",
         "intercom-sdk-base_release",
         "expanded",
         ""
   }
)
@SourceDebugExtension(
   "SMAP\nDropDownQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DropDownQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/dropdown/DropDownQuestionKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 5 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 9 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 10 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,187:1\n1047#2,6:188\n1047#2,6:294\n1047#2,6:335\n118#3:194\n118#3:261\n118#3:328\n118#3:329\n118#3:334\n75#4:195\n70#5:196\n67#5,9:197\n77#5:352\n81#6,6:206\n88#6,6:221\n81#6,6:239\n88#6,6:254\n81#6,6:272\n88#6,6:287\n81#6,6:306\n88#6,6:321\n96#6:332\n96#6:343\n96#6:347\n96#6:351\n402#7,9:212\n411#7:227\n402#7,9:245\n411#7:260\n402#7,9:278\n411#7:293\n402#7,9:312\n411#7:327\n412#7,2:330\n412#7,2:341\n412#7,2:345\n412#7,2:349\n87#8:228\n83#8,10:229\n87#8:262\n84#8,9:263\n94#8:344\n94#8:348\n99#9,6:300\n106#9:333\n85#10:353\n117#10,2:354\n*S KotlinDebug\n*F\n+ 1 DropDownQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/dropdown/DropDownQuestionKt\n*L\n58#1:188,6\n85#1:294,6\n110#1:335,6\n63#1:194\n70#1:261\n97#1:328\n104#1:329\n114#1:334\n65#1:195\n67#1:196\n67#1:197,9\n67#1:352\n67#1:206,6\n67#1:221,6\n68#1:239,6\n68#1:254,6\n71#1:272,6\n71#1:287,6\n81#1:306,6\n81#1:321,6\n81#1:332\n71#1:343\n68#1:347\n67#1:351\n67#1:212,9\n67#1:227\n68#1:245,9\n68#1:260\n71#1:278,9\n71#1:293\n81#1:312,9\n81#1:327\n81#1:330,2\n71#1:341,2\n68#1:345,2\n67#1:349,2\n68#1:228\n68#1:229,10\n71#1:262\n71#1:263,9\n71#1:344\n68#1:348\n81#1:300,6\n81#1:333\n58#1:353\n58#1:354,2\n*E\n"
)
public final class DropDownQuestionKt {
   @NotNull
   private static final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel dropDownQuestionModel;

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void DropDownQuestion(
      @Nullable Modifier var0,
      @NotNull io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel var1,
      @Nullable Answer var2,
      @NotNull Function1 var3,
      @NotNull SurveyUiColors var4,
      @Nullable Function2 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "dropDownQuestionModel");
      Intrinsics.checkNotNullParameter(var3, "onAnswer");
      Intrinsics.checkNotNullParameter(var4, "colors");
      Composer var140 = var6.startRestartGroup(1475245134);
      ComposerKt.sourceInformation(var140, "C(DropDownQuestion)P(3,2!1,4)57@2552L34,61@2870L6,64@3039L7,66@3052L2953:DropDownQuestion.kt#ppg8nv");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var140.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var140.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var140.changed(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var140.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var140.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var140.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var140.getSkipping()) {
         var140.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var8 & 4) != 0) {
            var2 = io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer.INSTANCE;
         }

         if ((var8 & 32) != 0) {
            var5 = io.intercom.android.sdk.survey.ui.questiontype.dropdown.ComposableSingletons$DropDownQuestionKt.INSTANCE.recovered$eac478aa65045e5d();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1475245134, var9, -1, "io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestion (DropDownQuestion.kt:56)"
            );
         }

         var140.startReplaceGroup(1842846242);
         ComposerKt.sourceInformation(var140, "CC(remember):DropDownQuestion.kt#9igjgp");
         Composer var12 = var140;
         boolean var13 = false;
         int var14 = 0;
         Object var15 = var12.rememberedValue();
         int var16 = 0;
         Object var10000;
         if (var15 == Composer.Companion.getEmpty()) {
            int var17 = 0;
            Object var146 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var12.updateRememberedValue(var146);
            var10000 = var146;
         } else {
            var10000 = var15;
         }

         MutableState var11 = (MutableState)var10000;
         var140.endReplaceGroup();
         MutableState var10 = var11;
         boolean var141 = DropDownQuestion$lambda$1(var10) || !(var2 instanceof io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer);
         var140.startReplaceGroup(1842850020);
         ComposerKt.sourceInformation(var140, "59@2721L6");
         long var143 = var141
            ? var4.recovered$923dd289d74a1df8()
            : IntercomTheme.INSTANCE.getColors(var140, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
         var140.endReplaceGroup();
         long var142 = var143;
         long var144 = var141 ? ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var4.recovered$923dd289d74a1df8()) : ColorKt.Color(4285756278L);
         long var145 = Color.recovered$41aa0ee03c2282fb(
            IntercomTheme.INSTANCE.getColors(var140, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null
         );
         int var19 = 1;
         int var20 = 0;
         float var18 = Dp.recovered$95b3d103663bf060(var19);
         Color var181 = var4.recovered$ec244e2186b74a5a();
         long var147 = var181 != null ? var181.recovered$45f9de7b85e0e0b5() : var144;
         CompositionLocal var22 = CompositionLocalsKt.getLocalFocusManager();
         int var23 = 0;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var140, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var25 = var140.consume(var22);
         ComposerKt.sourceInformationMarkerEnd(var140);
         FocusManager var21 = (FocusManager)var25;
         int var150 = 14 & var9;
         int var151 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var140, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var148 = Alignment.Companion.getTopStart();
         boolean var149 = false;
         MeasurePolicy var26 = BoxKt.maybeCachedBoxMeasurePolicy(var148, var149);
         Modifier var27 = var0;
         MeasurePolicy var28 = var26;
         Composer var29 = var140;
         int var30 = 112 & var150 << 3;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var32 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var29, 0));
         CompositionLocalMap var33 = var29.getCurrentCompositionLocalMap();
         Modifier var34 = ComposedModifierKt.materializeModifier(var29, var27);
         Function0 var35 = ComposeUiNode.Companion.getConstructor();
         Composer var36 = var29;
         int var37 = 6 | 896 & var30 << 6;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var36.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var36.startReusableNode();
         if (var36.getInserting()) {
            var36.createNode(var35);
         } else {
            var36.useNode();
         }

         Composer var39 = Updater.recovered$676aa17b44fd6280(var36);
         int var40 = 0;
         Updater.recovered$89682b199a142880(var39, var28, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var39, var33, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var39, var32, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var39, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var39, var34, ComposeUiNode.Companion.getSetModifier());
         int var41 = 14 & var37 >> 6;
         Composer var42 = var36;
         int var43 = 0;
         ComposerKt.sourceInformationMarkerStart(var42, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var44 = 6 | 112 & var150 >> 6;
         Composer var45 = var42;
         BoxScope var46 = BoxScopeInstance.INSTANCE;
         int var47 = 0;
         ComposerKt.sourceInformationMarkerStart(var45, 1812241828, "C67@3087L2912:DropDownQuestion.kt#ppg8nv");
         int $changed$ivx = 0;
         int var49 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var45, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var50 = Modifier.Companion;
         Vertical var51 = Arrangement.INSTANCE.getTop();
         Horizontal var52 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var51, var52, var45, 14 & $changed$ivx >> 3 | 112 & $changed$ivx >> 3);
         Modifier var54 = var50;
         MeasurePolicy var55 = measurePolicy$ivx;
         Composer var56 = var45;
         int var57 = 112 & $changed$ivx << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var56, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var56, 0));
         CompositionLocalMap localMap$iv$ivx = var56.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var56, var54);
         Function0 var62 = ComposeUiNode.Companion.getConstructor();
         Composer var63 = var56;
         int var64 = 6 | 896 & var57 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var63, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var63.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var63.startReusableNode();
         if (var63.getInserting()) {
            var63.createNode(var62);
         } else {
            var63.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var63);
         int var67/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var55, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var68 = 14 & var64 >> 6;
         Composer $composer$ivx = var63;
         int var70 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var71 = 6 | 112 & $changed$ivx >> 6;
         Composer var72 = $composer$ivx;
         ColumnScope var73 = ColumnScopeInstance.INSTANCE;
         int var74 = 0;
         ComposerKt.sourceInformationMarkerStart(var72, -809918556, "C68@3123L8,69@3144L40,76@3442L6,78@3518L6,70@3197L2792:DropDownQuestion.kt#ppg8nv");
         var5.invoke(var72, 14 & var9 >> 15);
         Modifier var182 = Modifier.Companion;
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var182, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var72, 6);
         Modifier var152 = ClipKt.clip(
            BorderKt.recovered$1227359600bc50fd(
               SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
               var18,
               var145,
               IntercomTheme.INSTANCE.getShapes(var72, IntercomTheme.$stable).getSmall()
            ),
            IntercomTheme.INSTANCE.getShapes(var72, IntercomTheme.$stable).getSmall()
         );
         byte var77 = 0;
         int $i$f$Columnx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var72, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
         Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(
            verticalArrangement$ivx, horizontalAlignment$ivx, var72, 14 & var77 >> 3 | 112 & var77 >> 3
         );
         Modifier var81 = var152;
         MeasurePolicy var82 = measurePolicy$ivxx;
         Composer var83 = var72;
         int var84 = 112 & var77 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var83, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var83, 0));
         CompositionLocalMap localMap$iv$ivxx = var83.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var83, var81);
         Function0 var89 = ComposeUiNode.Companion.getConstructor();
         Composer var90 = var83;
         int var91 = 6 | 896 & var84 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var90, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var90.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var90.startReusableNode();
         if (var90.getInserting()) {
            var90.createNode(var89);
         } else {
            var90.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var90);
         int var94/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var82, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var95 = 14 & var91 >> 6;
         Composer $composer$ivxx = var90;
         int var97/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var98 = 6 | 112 & var77 >> 6;
         Composer var99 = $composer$ivxx;
         ColumnScope var100 = ColumnScopeInstance.INSTANCE;
         int var101 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var99, -978551716, "C84@3738L19,80@3565L1365,111@5174L6,112@5234L6,109@5041L20,114@5310L665,107@4947L1028:DropDownQuestion.kt#ppg8nv"
         );
         Modifier var183 = BackgroundKt.recovered$42a3174180b3fb7a(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), var142, null, 2, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var99.startReplaceGroup(-585752368);
         ComposerKt.sourceInformation(var99, "CC(remember):DropDownQuestion.kt#9igjgp");
         Composer var102 = var99;
         boolean var103 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var102.rememberedValue();
         int var106/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10005;
         if (it$ivx == Composer.Companion.getEmpty()) {
            Object var107 = null;
            Object var108 = null;
            Object var109 = null;
            boolean var110 = false;
            Modifier var111 = var183;
            int var112 = 0;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1420_0 = var10;
            Function0 var113 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestionKt.DropDownQuestion$lambda$10$lambda$9$lambda$8$lambda$4$lambda$3(recoveredExpressionCapture_1420_0));
            var183 = var111;
            var10001 = var110;
            var10002 = var109;
            var10003 = var108;
            var10004 = var107;
            Object var114 = var113;
            var102.updateRememberedValue(var114);
            var10005 = var114;
         } else {
            var10005 = it$ivx;
         }

         Function0 var115 = (Function0)var10005;
         var99.endReplaceGroup();
         Modifier var170 = ClickableKt.recovered$1c9660c41fd850dc(
            var183, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var115, 15, null
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var154 = Arrangement.INSTANCE.getSpaceBetween();
         androidx.compose.ui.Alignment.Vertical var156 = Alignment.Companion.getCenterVertically();
         short var157 = 432;
         int var158 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var99, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy measurePolicy$ivxxx = RowKt.rowMeasurePolicy(var154, var156, var99, 14 & var157 >> 3 | 112 & var157 >> 3);
         Modifier var164 = var170;
         MeasurePolicy var167 = measurePolicy$ivxxx;
         Composer var116 = var99;
         int var117 = 112 & var157 << 3;
         int $i$f$Layoutxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var116, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var116, 0));
         CompositionLocalMap localMap$iv$ivxxx = var116.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxxx = ComposedModifierKt.materializeModifier(var116, var164);
         Function0 var122 = ComposeUiNode.Companion.getConstructor();
         Composer var123 = var116;
         int var124 = 6 | 896 & var117 << 6;
         int $i$f$ReusableComposeNodexxx = 0;
         ComposerKt.sourceInformationMarkerStart(var123, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var123.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var123.startReusableNode();
         if (var123.getInserting()) {
            var123.createNode(var122);
         } else {
            var123.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxxx = Updater.recovered$676aa17b44fd6280(var123);
         int var127/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, var167, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, localMap$iv$ivxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, compositeKeyHash$iv$ivxxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, materialized$iv$ivxxx, ComposeUiNode.Companion.getSetModifier());
         int var128 = 14 & var124 >> 6;
         Composer $composer$ivxxx = var123;
         int var130 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var131 = 6 | 112 & var157 >> 6;
         Composer var132 = $composer$ivxxx;
         RowScope var133 = RowScopeInstance.INSTANCE;
         int var134 = 0;
         ComposerKt.sourceInformationMarkerStart(var132, 1507283320, "C98@4509L10,93@4230L343,101@4634L57,102@4738L49,100@4594L318:DropDownQuestion.kt#ppg8nv");
         var132.startReplaceGroup(602811226);
         ComposerKt.sourceInformation(var132, "89@4037L63");
         String var135 = var1.getPlaceHolderStringRes() != null
            ? StringResources_androidKt.stringResource(var1.getPlaceHolderStringRes(), var132, 0)
            : var1.getPlaceholder();
         var132.endReplaceGroup();
         String var136 = var135;
         String var184 = var2 instanceof io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer ? ((io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer)var2).getAnswer() : var136;
         Modifier var188 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         TextKt.recovered$5b114ab32a4f4190(
            var184,
            SizeKt.wrapContentSize$default(PaddingKt.recovered$eb6a744cd98cdbca(var188, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), null, false, 3, null),
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
            TextStyle.recovered$cf01d8b71690b5c5(
               IntercomTheme.INSTANCE.getTypography(var132, IntercomTheme.$stable).getType04(),
               var144,
               0L,
               null,
               null,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0,
               0,
               0L,
               null,
               null,
               null,
               0,
               0,
               null,
               16777214,
               null
            ),
            var132,
            48,
            0,
            131068
         );
         Painter var185 = PainterResources_androidKt.painterResource(drawable.intercom_ic_down_arrow, var132, 0);
         String var189 = StringResources_androidKt.stringResource(string.intercom_choose_one, var132, 0);
         Modifier var191 = Modifier.Companion;
         int $this$dp$ivxxx = 16;
         int $i$f$getDpxxx = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var185,
            var189,
            PaddingKt.recovered$eb6a744cd98cdbca(var191, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)),
            var147,
            var132,
            384 | Painter.$stable,
            0
         );
         ComposerKt.sourceInformationMarkerEnd(var132);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxxx);
         var123.endNode();
         ComposerKt.sourceInformationMarkerEnd(var123);
         ComposerKt.sourceInformationMarkerEnd(var116);
         ComposerKt.sourceInformationMarkerEnd(var99);
         boolean var171 = DropDownQuestion$lambda$1(var10);
         Modifier var155 = SizeKt.fillMaxWidth(Modifier.Companion, 0.8F);
         long var138 = IntercomTheme.INSTANCE.getColors(var99, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
         CornerBasedShape var159 = IntercomTheme.INSTANCE.getShapes(var99, IntercomTheme.$stable).getSmall();
         int $this$dp$ivxxxx = 8;
         int $i$f$getDpxxxx = 0;
         float var161 = Dp.recovered$95b3d103663bf060($this$dp$ivxxxx);
         boolean var186 = var171;
         var99.startReplaceGroup(-585710671);
         ComposerKt.sourceInformation(var99, "CC(remember):DropDownQuestion.kt#9igjgp");
         Composer var169 = var99;
         boolean var172 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var169.rememberedValue();
         int var175/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var190;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            boolean var163 = var171;
            int var176 = 0;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1421_0 = var10;
            Function0 var162 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestionKt.DropDownQuestion$lambda$10$lambda$9$lambda$8$lambda$7$lambda$6(recoveredExpressionCapture_1421_0));
            var186 = var163;
            Object var177 = var162;
            var169.updateRememberedValue(var177);
            var190 = var177;
         } else {
            var190 = it$ivxx;
         }

         Function0 var166 = (Function0)var190;
         var99.endReplaceGroup();
         AndroidMenu_androidKt.recovered$7e5e85d5a2943f5c(
            var186,
            var166,
            var155,
            0L,
            null,
            null,
            var159,
            var138,
            0.0F,
            var161,
            null,
            ComposableLambdaKt.rememberComposableLambda(17506981, true, new DropDownQuestionKt$DropDownQuestion$1$1$1$4(var21, var1, var3, var10), var99, 54),
            var99,
            805306800,
            48,
            1336
         );
         ComposerKt.sourceInformationMarkerEnd(var99);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var90.endNode();
         ComposerKt.sourceInformationMarkerEnd(var90);
         ComposerKt.sourceInformationMarkerEnd(var83);
         ComposerKt.sourceInformationMarkerEnd(var72);
         ComposerKt.sourceInformationMarkerEnd(var72);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var63.endNode();
         ComposerKt.sourceInformationMarkerEnd(var63);
         ComposerKt.sourceInformationMarkerEnd(var56);
         ComposerKt.sourceInformationMarkerEnd(var45);
         ComposerKt.sourceInformationMarkerEnd(var45);
         ComposerKt.sourceInformationMarkerEnd(var42);
         var36.endNode();
         ComposerKt.sourceInformationMarkerEnd(var36);
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var140);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var187 = var140.endRestartGroup();
      if (var187 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1422_0 = var0;
         final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel recoveredExpressionCapture_1422_1 = var1;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1422_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1422_3 = var3;
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredExpressionCapture_1422_4 = var4;
         final kotlin.jvm.functions.Function2 recoveredExpressionCapture_1422_5 = var5;
         final int recoveredExpressionCapture_1422_6 = var7;
         final int recoveredExpressionCapture_1422_7 = var8;
         var187.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1422_0, recoveredExpressionArgument_1422_1) -> io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestionKt.DropDownQuestion$lambda$11(recoveredExpressionCapture_1422_0, recoveredExpressionCapture_1422_1, recoveredExpressionCapture_1422_2, recoveredExpressionCapture_1422_3, recoveredExpressionCapture_1422_4, recoveredExpressionCapture_1422_5, recoveredExpressionCapture_1422_6, recoveredExpressionCapture_1422_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1422_0), ((java.lang.Integer)recoveredExpressionArgument_1422_1).intValue())));
      }
   }

   @Preview(name = "Default Mode", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DropDownQuestionPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(281876673);
      ComposerKt.sourceInformation(var2, "C(DropDownQuestionPreview)152@6495L206:DropDownQuestion.kt#ppg8nv");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               281876673, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestionPreview (DropDownQuestion.kt:151)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.dropdown.ComposableSingletons$DropDownQuestionKt.INSTANCE.recovered$1400af5e34386102(),
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
         final int recoveredExpressionCapture_1423_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1423_0, recoveredExpressionArgument_1423_1) -> io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestionKt.DropDownQuestionPreview$lambda$12(recoveredExpressionCapture_1423_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1423_0), ((java.lang.Integer)recoveredExpressionArgument_1423_1).intValue())));
      }
   }

   @Preview(name = "Selected Mode", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DropDownSelectedQuestionPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-891294020);
      ComposerKt.sourceInformation(var2, "C(DropDownSelectedQuestionPreview)164@6824L253:DropDownQuestion.kt#ppg8nv");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -891294020, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownSelectedQuestionPreview (DropDownQuestion.kt:163)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.dropdown.ComposableSingletons$DropDownQuestionKt.INSTANCE.recovered$fd838d44303763da(),
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
         final int recoveredExpressionCapture_1424_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1424_0, recoveredExpressionArgument_1424_1) -> io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestionKt.DropDownSelectedQuestionPreview$lambda$13(recoveredExpressionCapture_1424_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1424_0), ((java.lang.Integer)recoveredExpressionArgument_1424_1).intValue())));
      }
   }

   @Preview(name = "Colored Selected Mode", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ColoredDropDownSelectedQuestionPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-2103500414);
      ComposerKt.sourceInformation(var2, "C(ColoredDropDownSelectedQuestionPreview)177@7215L279:DropDownQuestion.kt#ppg8nv");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2103500414,
               var1,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.dropdown.ColoredDropDownSelectedQuestionPreview (DropDownQuestion.kt:176)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.dropdown.ComposableSingletons$DropDownQuestionKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
         final int recoveredExpressionCapture_1419_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1419_0, recoveredExpressionArgument_1419_1) -> io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestionKt.ColoredDropDownSelectedQuestionPreview$lambda$14(recoveredExpressionCapture_1419_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1419_0), ((java.lang.Integer)recoveredExpressionArgument_1419_1).intValue())));
      }
   }

   private static final boolean DropDownQuestion$lambda$1(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void DropDownQuestion$lambda$2(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit DropDownQuestion$lambda$10$lambda$9$lambda$8$lambda$4$lambda$3(MutableState var0) {
      DropDownQuestion$lambda$2(var0, true);
      return Unit.INSTANCE;
   }

   private static final Unit DropDownQuestion$lambda$10$lambda$9$lambda$8$lambda$7$lambda$6(MutableState var0) {
      DropDownQuestion$lambda$2(var0, false);
      return Unit.INSTANCE;
   }

   private static final Unit DropDownQuestion$lambda$11(
      Modifier var0, io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel var1, Answer var2, Function1 var3, SurveyUiColors var4, Function2 var5, int var6, int var7, Composer var8, int var9
   ) {
      DropDownQuestion(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit DropDownQuestionPreview$lambda$12(int var0, Composer var1, int var2) {
      DropDownQuestionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit DropDownSelectedQuestionPreview$lambda$13(int var0, Composer var1, int var2) {
      DropDownSelectedQuestionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ColoredDropDownSelectedQuestionPreview$lambda$14(int var0, Composer var1, int var2) {
      ColoredDropDownSelectedQuestionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel access$getDropDownQuestionModel$p() {
      return dropDownQuestionModel;
   }

   // $VF: synthetic method
   public static final void access$DropDownQuestion$lambda$2(MutableState var0, boolean var1) {
      DropDownQuestion$lambda$2(var0, var1);
   }

   static {
      String var10002 = UUID.randomUUID().toString();
      Intrinsics.checkNotNullExpressionValue(var10002, "toString(...)");
      List var10003 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText("Is this a preview?"));
      String[] var0 = new String[]{"Option A", "Option B", "Option C"};
      dropDownQuestionModel = new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel(var10002, var10003, true, CollectionsKt.listOf(var0), "Please Select", null, 32, null);
   }
}

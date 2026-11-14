package io.intercom.android.sdk.survey.ui.questiontype.choice;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.TextKt;
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
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPillKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nOtherOption.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OtherOption.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/OtherOptionKt$OtherOption$1$2\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,86:1\n87#2:87\n83#2,10:88\n94#2:124\n81#3,6:98\n88#3,6:113\n96#3:123\n402#4,9:104\n411#4:119\n412#4,2:121\n118#5:120\n*S KotlinDebug\n*F\n+ 1 OtherOption.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/OtherOptionKt$OtherOption$1$2\n*L\n54#1:87\n54#1:88,10\n54#1:124\n54#1:98,6\n54#1:113,6\n54#1:123\n54#1:104,9\n54#1:119\n54#1:121,2\n59#1:120\n*E\n"
)
final class OtherOptionKt$OtherOption$1$2 implements Function3 {
   // $VF: synthetic field
   final SurveyUiColors $surveyUiColors;
   // $VF: synthetic field
   final String $text;
   // $VF: synthetic field
   final Function1 $onTextChanged;

   OtherOptionKt$OtherOption$1$2(SurveyUiColors var1, String var2, Function1 var3) {
      this.$surveyUiColors = var1;
      this.$text = var2;
      this.$onTextChanged = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C53@2168L743:OtherOption.kt#w8l19x");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -252181085, var3, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.OtherOption.<anonymous>.<anonymous> (OtherOption.kt:53)"
         );
      }

      SurveyUiColors var7 = this.$surveyUiColors;
      String var8 = this.$text;
      Function1 var9 = this.$onTextChanged;
      int var10 = 0;
      int var11 = 0;
      ComposerKt.sourceInformationMarkerStart(
         var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
      );
      Modifier var4 = Modifier.Companion;
      Vertical var5 = Arrangement.INSTANCE.getTop();
      Horizontal var6 = Alignment.Companion.getStart();
      MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var10 >> 3 | 112 & var10 >> 3);
      Modifier var13 = var4;
      MeasurePolicy var14 = var12;
      Composer var15 = var2;
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
      ComposerKt.sourceInformationMarkerStart(var31, -1833568774, "C55@2226L83,56@2353L10,54@2193L196,58@2406L40,64@2743L89,59@2463L434:OtherOption.kt#w8l19x");
      TextKt.recovered$5b114ab32a4f4190(
         StringResources_androidKt.stringResource(string.intercom_surveys_multiselect_other_option_input_label, var31, 0),
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
         IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04(),
         var31,
         0,
         0,
         131070
      );
      Modifier var10000 = Modifier.Companion;
      int var34 = 4;
      int var35 = 0;
      SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var34)), var31, 6);
      long var36 = ColorExtensionsKt.recovered$93702c38c1546c4a(var7.recovered$923dd289d74a1df8());
      int var38 = ImeAction.Companion.recovered$c2fd69510cff9e19();
      String var39 = StringResources_androidKt.stringResource(string.intercom_surveys_multiselect_other_option_input_placeholder, var31, 0);
      TextInputPillKt.recovered$1e363f8e6e037ae9(var8, var39, var9, null, var36, 0, false, null, 0, var38, false, null, false, 0.0F, var31, 805306368, 6, 14824);
      ComposerKt.sourceInformationMarkerEnd(var31);
      ComposerKt.sourceInformationMarkerEnd(var28);
      var22.endNode();
      ComposerKt.sourceInformationMarkerEnd(var22);
      ComposerKt.sourceInformationMarkerEnd(var15);
      ComposerKt.sourceInformationMarkerEnd(var2);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((AnimatedVisibilityScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

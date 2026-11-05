package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta;
import io.intercom.android.sdk.survey.SurveyUiColors;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSurveyCtaButtonComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SurveyCtaButtonComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyCtaButtonComponentKt$SurveyCtaButtonComponent$3$1$2\n+ 2 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,152:1\n99#2:153\n95#2,10:154\n106#2:191\n81#3,6:164\n88#3,6:179\n96#3:190\n402#4,9:170\n411#4:185\n412#4,2:188\n118#5:186\n118#5:187\n*S KotlinDebug\n*F\n+ 1 SurveyCtaButtonComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyCtaButtonComponentKt$SurveyCtaButtonComponent$3$1$2\n*L\n56#1:153\n56#1:154,10\n56#1:191\n56#1:164,6\n56#1:179,6\n56#1:190\n56#1:170,9\n56#1:185\n56#1:188,2\n63#1:186\n64#1:187\n*E\n"
)
final class SurveyCtaButtonComponentKt$SurveyCtaButtonComponent$3$1$2 implements Function3 {
   // $VF: synthetic field
   final SurveyState$Content$SecondaryCta $it;
   // $VF: synthetic field
   final SurveyUiColors $surveyUiColors;

   SurveyCtaButtonComponentKt$SurveyCtaButtonComponent$3$1$2(SurveyState$Content$SecondaryCta var1, SurveyUiColors var2) {
      this.$it = var1;
      this.$surveyUiColors = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$Button");
      ComposerKt.sourceInformation(var2, "C55@2251L701:SurveyCtaButtonComponent.kt#bmpoeo");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               468620518,
               var3,
               -1,
               "io.intercom.android.sdk.survey.ui.components.SurveyCtaButtonComponent.<anonymous>.<anonymous>.<anonymous> (SurveyCtaButtonComponent.kt:55)"
            );
         }

         Vertical var6 = Alignment.Companion.getCenterVertically();
         SurveyState$Content$SecondaryCta var7 = this.$it;
         SurveyUiColors var8 = this.$surveyUiColors;
         int var9 = 384;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Modifier var4 = Modifier.Companion;
         Horizontal var5 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var11 = RowKt.rowMeasurePolicy(var5, var6, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var4;
         MeasurePolicy var13 = var11;
         Composer var14 = var2;
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
         ComposerKt.sourceInformationMarkerStart(var30, -168896503, "C66@2804L130:SurveyCtaButtonComponent.kt#bmpoeo");
         var30.startReplaceGroup(410193571);
         ComposerKt.sourceInformation(var30, "58@2407L58,57@2363L398");
         if (var7.isExternalUrl()) {
            Painter var33 = PainterResources_androidKt.painterResource(drawable.intercom_open_help_center, var30, 0);
            long var34 = var8.recovered$9bdc86596139ab10();
            Modifier var10000 = Modifier.Companion;
            int var36 = 4;
            int var37 = 0;
            var10000 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var36), 0.0F, 2, null);
            var36 = 24;
            var37 = 0;
            Modifier var38 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var36));
            IconKt.recovered$e00adffb7fe45b02(var33, null, var38, var34, var30, 432 | Painter.$stable, 0);
         }

         var30.endReplaceGroup();
         TextKt.recovered$5b114ab32a4f4190(
            var7.getButtonText(),
            null,
            var8.recovered$9bdc86596139ab10(),
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
            null,
            var30,
            0,
            0,
            262138
         );
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}

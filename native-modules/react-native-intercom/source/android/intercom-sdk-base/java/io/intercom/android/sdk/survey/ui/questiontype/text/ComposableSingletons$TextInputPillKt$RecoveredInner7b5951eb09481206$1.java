package io.intercom.android.sdk.survey.ui.questiontype.text;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTextInputPill.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextInputPill.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/ComposableSingletons$TextInputPillKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,191:1\n118#2:192\n87#3:193\n84#3,9:194\n94#3:234\n81#4,6:203\n88#4,6:218\n96#4:233\n402#5,9:209\n411#5:224\n412#5,2:231\n1047#6,6:225\n*S KotlinDebug\n*F\n+ 1 TextInputPill.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/ComposableSingletons$TextInputPillKt$lambda-1$1\n*L\n119#1:192\n119#1:193\n119#1:194,9\n119#1:234\n119#1:203,6\n119#1:218,6\n119#1:233\n119#1:209,9\n119#1:224\n119#1:231,2\n122#1:225,6\n*E\n"
)
final class ComposableSingletons$TextInputPillKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TextInputPillKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TextInputPillKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C118@4913L261:TextInputPill.kt#z2rnlt");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1404279250,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$TextInputPillKt.lambda-1.<anonymous> (TextInputPill.kt:118)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 16;
         int var4 = 0;
         Modifier var40 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var3));
         int var6 = 6;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var41 = Arrangement.INSTANCE.getTop();
         Horizontal var5 = Alignment.Companion.getStart();
         MeasurePolicy var8 = ColumnKt.columnMeasurePolicy(var41, var5, var1, 14 & var6 >> 3 | 112 & var6 >> 3);
         Modifier var9 = var40;
         MeasurePolicy var10 = var8;
         Composer var11 = var1;
         int var12 = 112 & var6 << 3;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(var11, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var14 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var11, 0));
         CompositionLocalMap var15 = var11.getCurrentCompositionLocalMap();
         Modifier var16 = ComposedModifierKt.materializeModifier(var11, var9);
         Function0 var17 = ComposeUiNode.Companion.getConstructor();
         Composer var18 = var11;
         int var19 = 6 | 896 & var12 << 6;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var18.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var18.startReusableNode();
         if (var18.getInserting()) {
            var18.createNode(var17);
         } else {
            var18.useNode();
         }

         Composer var21 = Updater.recovered$676aa17b44fd6280(var18);
         int var22 = 0;
         Updater.recovered$89682b199a142880(var21, var10, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var21, var15, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var21, var14, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var21, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var21, var16, ComposeUiNode.Companion.getSetModifier());
         int var23 = 14 & var19 >> 6;
         Composer var24 = var18;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var26 = 6 | 112 & var6 >> 6;
         Composer var27 = var24;
         ColumnScope var28 = ColumnScopeInstance.INSTANCE;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 1923157415, "C121@5068L2,119@4970L194:TextInputPill.kt#z2rnlt");
         Object var43 = null;
         String var10001 = "Enter text...";
         var27.startReplaceGroup(200587612);
         ComposerKt.sourceInformation(var27, "CC(remember):TextInputPill.kt#9igjgp");
         Composer var30 = var27;
         boolean var31 = false;
         int var32 = 0;
         Object var33 = var30.rememberedValue();
         int var34 = 0;
         Object var10002;
         if (var33 == Composer.Companion.getEmpty()) {
            String var35 = "Enter text...";
            Object var36 = null;
            int var37 = 0;
            Function1 var38 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1494_0) -> ComposableSingletons$TextInputPillKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$2$lambda$1$lambda$0((java.lang.String)recoveredArgument_1494_0));
            var43 = var36;
            var10001 = var35;
            Object var42 = var38;
            var30.updateRememberedValue(var42);
            var10002 = var42;
         } else {
            var10002 = var33;
         }

         Function1 var39 = (Function1)var10002;
         var27.endReplaceGroup();
         TextInputPillKt.recovered$1e363f8e6e037ae9(
            (String)var43,
            var10001,
            var39,
            null,
            SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)).recovered$923dd289d74a1df8(),
            0,
            false,
            null,
            0,
            0,
            false,
            null,
            false,
            0.0F,
            var27,
            432,
            0,
            16361
         );
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var24);
         var18.endNode();
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var11);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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

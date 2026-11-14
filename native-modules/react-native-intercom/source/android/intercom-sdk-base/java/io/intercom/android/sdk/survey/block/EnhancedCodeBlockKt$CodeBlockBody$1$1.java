package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nEnhancedCodeBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EnhancedCodeBlock.kt\nio/intercom/android/sdk/survey/block/EnhancedCodeBlockKt$CodeBlockBody$1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,532:1\n118#2:533\n118#2:566\n118#2:567\n99#3:534\n96#3,9:535\n106#3:571\n81#4,6:544\n88#4,6:559\n96#4:570\n402#5,9:550\n411#5:565\n412#5,2:568\n*S KotlinDebug\n*F\n+ 1 EnhancedCodeBlock.kt\nio/intercom/android/sdk/survey/block/EnhancedCodeBlockKt$CodeBlockBody$1$1\n*L\n198#1:533\n203#1:566\n213#1:567\n198#1:534\n198#1:535,9\n198#1:571\n198#1:544,6\n198#1:559,6\n198#1:570\n198#1:550,9\n198#1:565\n198#1:568,2\n*E\n"
)
final class EnhancedCodeBlockKt$CodeBlockBody$1$1 implements Function2 {
   // $VF: synthetic field
   final int $lineCount;
   // $VF: synthetic field
   final TextStyle $textStyle;
   // $VF: synthetic field
   final long $gutterTextColor;
   // $VF: synthetic field
   final AnnotatedString $blockContent;
   // $VF: synthetic field
   final long $textColor;

   EnhancedCodeBlockKt$CodeBlockBody$1$1(int var1, TextStyle var2, long var3, AnnotatedString var5, long var6) {
      this.$lineCount = var1;
      this.$textStyle = var2;
      this.$gutterTextColor = var3;
      this.$blockContent = var5;
      this.$textColor = var6;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C197@8279L764:EnhancedCodeBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -111185777, var2, -1, "io.intercom.android.sdk.survey.block.CodeBlockBody.<anonymous>.<anonymous> (EnhancedCodeBlock.kt:197)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var3 = 12;
         int var4 = 0;
         Modifier var41 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var3), 1, null);
         int var6 = this.$lineCount;
         TextStyle var7 = this.$textStyle;
         long var8 = this.$gutterTextColor;
         AnnotatedString var10 = this.$blockContent;
         long var11 = this.$textColor;
         int var13 = 6;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var42 = Arrangement.INSTANCE.getStart();
         Vertical var5 = Alignment.Companion.getTop();
         MeasurePolicy var15 = RowKt.rowMeasurePolicy(var42, var5, var1, 14 & var13 >> 3 | 112 & var13 >> 3);
         Modifier var16 = var41;
         MeasurePolicy var17 = var15;
         Composer var18 = var1;
         int var19 = 112 & var13 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var16);
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
         Updater.recovered$89682b199a142880(var28, var17, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var33 = 6 | 112 & var13 >> 6;
         Composer var34 = var31;
         RowScope var35 = RowScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, -412109429, "C198@8363L264,211@8942L21,204@8644L385:EnhancedCodeBlock.kt#jxhu3x");
         Modifier var10003 = Modifier.Companion;
         int $this$dp$ivx = 12;
         int $i$f$getDpx = 0;
         EnhancedCodeBlockKt.recovered$46f86118b70b7ca8(
            var6,
            var7,
            var8,
            PaddingKt.recovered$03b481d31d62885a(
               var10003, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, EnhancedCodeBlockKt.access$getGUTTER_TO_CODE_SPACING$p(), 0.0F, 10, null
            ),
            var34,
            3072,
            0
         );
         $this$dp$ivx = TextOverflow.Companion.recovered$04743ed5fce70242();
         var10000 = ScrollKt.horizontalScroll$default(Modifier.Companion, ScrollKt.rememberScrollState(0, var34, 0, 1), false, null, false, 14, null);
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         Modifier var44 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 11, null);
         TextKt.recovered$489343e57da77a35(
            var10, var44, var11, null, 0L, null, null, null, 0L, null, null, 0L, $this$dp$ivx, false, 0, 0, null, null, var7, var34, 0, 3456, 249848
         );
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}

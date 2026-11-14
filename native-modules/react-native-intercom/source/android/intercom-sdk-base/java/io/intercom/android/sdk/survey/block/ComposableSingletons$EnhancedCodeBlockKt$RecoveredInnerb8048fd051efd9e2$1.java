package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nEnhancedCodeBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EnhancedCodeBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$EnhancedCodeBlockKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,532:1\n1047#2,6:533\n*S KotlinDebug\n*F\n+ 1 EnhancedCodeBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$EnhancedCodeBlockKt$lambda-3$1\n*L\n400#1:533,6\n*E\n"
)
final class ComposableSingletons$EnhancedCodeBlockKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$EnhancedCodeBlockKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$EnhancedCodeBlockKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C399@15129L2,389@14526L621:EnhancedCodeBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2044108165,
               var2,
               -1,
               "io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.lambda-3.<anonymous> (EnhancedCodeBlock.kt:389)"
            );
         }

         Block var10000 = new Block$Builder()
            .withText("const greeting = \"Hello, World!\";")
            .withHighlightedHtml(
               "<span class=\"kd\">const</span> <span class=\"nx\">greeting</span> <span class=\"o\">=</span> <span class=\"s2\">&quot;Hello, World!&quot;</span><span class=\"p\">;</span>"
            )
            .withType(BlockType.CODE.getSerializedName())
            .build();
         Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
         Object var10001 = null;
         var1.startReplaceGroup(132677271);
         ComposerKt.sourceInformation(var1, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var11 = null;
            Block var10 = var10000;
            int var9 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$EnhancedCodeBlockKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            Object var14 = var12;
            var4.updateRememberedValue(var14);
            var10002 = var14;
         } else {
            var10002 = var7;
         }

         Function0 var3 = (Function0)var10002;
         var1.endReplaceGroup();
         Function0 var13 = var3;
         EnhancedCodeBlockKt.EnhancedCodeBlock(var10000, (Modifier)var10001, new CodeBlockMode$Inline(var13), null, var1, 0, 10);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
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

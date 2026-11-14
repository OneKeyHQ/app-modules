package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTableBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$TableBlockKt$lambda-9$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,747:1\n1047#2,6:748\n*S KotlinDebug\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$TableBlockKt$lambda-9$1\n*L\n647#1:748,6\n*E\n"
)
final class ComposableSingletons$TableBlockKt$RecoveredInnerd0e289bb4054aaa2$1 implements Function2 {
   public static final ComposableSingletons$TableBlockKt$RecoveredInnerd0e289bb4054aaa2$1 INSTANCE = new ComposableSingletons$TableBlockKt$RecoveredInnerd0e289bb4054aaa2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C646@27924L2,644@27801L141:TableBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1631930747, var2, -1, "io.intercom.android.sdk.survey.block.ComposableSingletons$TableBlockKt.lambda-9.<anonymous> (TableBlock.kt:644)"
            );
         }

         Block var10000 = TableBlockKt.access$captionlessSampleTableBlock();
         Object var10001 = null;
         var1.startReplaceGroup(-2057089748);
         ComposerKt.sourceInformation(var1, "CC(remember):TableBlock.kt#9igjgp");
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
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$TableBlockKt$RecoveredInnerd0e289bb4054aaa2$1.invoke$lambda$1$lambda$0());
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
         TableBlockKt.TableBlock(var10000, (Modifier)var10001, new TableBlockMode$Inline(var13), null, false, var1, 0, 26);
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

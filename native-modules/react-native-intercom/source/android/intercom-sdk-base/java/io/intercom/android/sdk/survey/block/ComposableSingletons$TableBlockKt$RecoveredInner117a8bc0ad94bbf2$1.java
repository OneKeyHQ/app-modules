package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.layout.SizeKt;
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
   "SMAP\nTableBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$TableBlockKt$lambda-5$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,747:1\n1047#2,6:748\n*S KotlinDebug\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$TableBlockKt$lambda-5$1\n*L\n618#1:748,6\n*E\n"
)
final class ComposableSingletons$TableBlockKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$TableBlockKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$TableBlockKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C617@27168L2,615@27055L182:TableBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               456555515, var2, -1, "io.intercom.android.sdk.survey.block.ComposableSingletons$TableBlockKt.lambda-5.<anonymous> (TableBlock.kt:615)"
            );
         }

         Block var3 = TableBlockKt.access$sampleTableBlock();
         var1.startReplaceGroup(502613416);
         ComposerKt.sourceInformation(var1, "CC(remember):TableBlock.kt#9igjgp");
         Composer var6 = var1;
         boolean var7 = false;
         int var8 = 0;
         Object var9 = var6.rememberedValue();
         int var10 = 0;
         Object var10000;
         if (var9 == Composer.Companion.getEmpty()) {
            int var11 = 0;
            Object var14 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$TableBlockKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$1$lambda$0());
            var6.updateRememberedValue(var14);
            var10000 = var14;
         } else {
            var10000 = var9;
         }

         Function0 var5 = (Function0)var10000;
         var1.endReplaceGroup();
         Function0 var12 = var5;
         TableBlockMode$Expanded var4 = new TableBlockMode$Expanded(var12);
         Modifier var13 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         TableBlockKt.TableBlock(var3, var13, var4, null, false, var1, 48, 24);
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

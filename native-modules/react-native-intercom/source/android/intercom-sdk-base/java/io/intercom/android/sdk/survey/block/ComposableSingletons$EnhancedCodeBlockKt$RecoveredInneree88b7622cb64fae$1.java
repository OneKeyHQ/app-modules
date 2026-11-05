package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.block.CodeBlockMode$Expanded;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nEnhancedCodeBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EnhancedCodeBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$EnhancedCodeBlockKt$lambda-14$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,532:1\n1047#2,6:533\n*S KotlinDebug\n*F\n+ 1 EnhancedCodeBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$EnhancedCodeBlockKt$lambda-14$1\n*L\n526#1:533,6\n*E\n"
)
final class ComposableSingletons$EnhancedCodeBlockKt$RecoveredInneree88b7622cb64fae$1 implements Function2 {
   public static final ComposableSingletons$EnhancedCodeBlockKt$RecoveredInneree88b7622cb64fae$1 INSTANCE = new ComposableSingletons$EnhancedCodeBlockKt$RecoveredInneree88b7622cb64fae$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C525@19983L2,502@18746L1306:EnhancedCodeBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1175618227,
               var2,
               -1,
               "io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.lambda-14.<anonymous> (EnhancedCodeBlock.kt:502)"
            );
         }

         Block var10000 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
            .withText(
               "fun main() {<br>    val items = listOf(1, 2, 3, 4, 5)<br>    for (item in items) {<br>        println(item)<br>    }<br>    val map = mapOf(<br>        \"key1\" to \"value1\",<br>        \"key2\" to \"value2\",<br>        \"key3\" to \"value3\",<br>        \"key4\" to \"value4\",<br>        \"key5\" to \"value5\",<br>    )<br>    map.forEach { (k, v) -><br>        println(\"key=$k value=$v\")<br>    }<br>    repeat(20) { i -><br>        println(\"Line $i\")<br>    }<br>}"
            )
            .withType(BlockType.CODE.getSerializedName())
            .build();
         Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
         Block var3 = var10000;
         var1.startReplaceGroup(-2140917087);
         ComposerKt.sourceInformation(var1, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
         Composer var6 = var1;
         boolean var7 = false;
         int var8 = 0;
         Object var9 = var6.rememberedValue();
         int var10 = 0;
         Object var15;
         if (var9 == Composer.Companion.getEmpty()) {
            int var11 = 0;
            Object var14 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt$RecoveredInneree88b7622cb64fae$1.invoke$lambda$1$lambda$0());
            var6.updateRememberedValue(var14);
            var15 = var14;
         } else {
            var15 = var9;
         }

         Function0 var5 = (Function0)var15;
         var1.endReplaceGroup();
         Function0 var12 = var5;
         io.intercom.android.sdk.survey.block.CodeBlockMode$Expanded var4 = new io.intercom.android.sdk.survey.block.CodeBlockMode$Expanded(var12);
         Modifier var13 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         EnhancedCodeBlockKt.EnhancedCodeBlock(var3, var13, var4, null, var1, 48, 8);
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
